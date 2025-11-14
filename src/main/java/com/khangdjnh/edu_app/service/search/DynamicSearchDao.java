package com.khangdjnh.edu_app.service.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class DynamicSearchDao {

    private final DataSource dataSource;

    public DynamicSearchDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SearchResult searchWithDynamicFilter(String tableName, DynamicFilter filter) throws SQLException {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("tableName required");
        }

        // Sanitize tableName: allow only letters, digits, underscores, dots (for schema.table)
        if (!tableName.matches("[A-Za-z0-9_.]+")) {
            throw new IllegalArgumentException("Invalid tableName");
        }

        StringBuilder where = new StringBuilder();
        List<Object> params = new ArrayList<>();

        int pageSize = filter.getPageSize() == null ? 20 : filter.getPageSize();
        int pageNumber = filter.getPageNumber() == null ? 0 : filter.getPageNumber();

        try (Connection conn = dataSource.getConnection()) {
            // Lấy danh sách cột thực sự tồn tại
            Set<String> actualColumns = getActualColumns(conn, tableName);

            if (filter.getFilterMap() != null && !filter.getFilterMap().isEmpty()) {
                List<String> clauses = new ArrayList<>();

                for (Map.Entry<String, FilterEntity> e : filter.getFilterMap().entrySet()) {
                    String col = e.getKey();
                    FilterEntity fe = e.getValue();

                    // **Bỏ qua cột không tồn tại**
                    if (!actualColumns.contains(col.toLowerCase())) {
                        continue;
                    }

                    String op = fe.getOperator() == null ? "=" : fe.getOperator().toLowerCase(Locale.ROOT);
                    String type = fe.getType() == null ? "STRING" : fe.getType().toUpperCase(Locale.ROOT);
                    Object val = fe.getValue();

                    switch (op) {
                        case "=":
                        case ">":
                        case "<":
                        case ">=":
                        case "<=":
                            if (isDatePartType(type)) {
                                String sqlCol = buildTimeColumnSQL(col, type);
                                String[] converted = convertDateValue(val, type);
                                clauses.add(sqlCol + " " + op + " ?");
                                params.add(converted[0]);
                            } else {
                                clauses.add(col + " " + op + " ?");
                                params.add(convertForType(val, type));
                            }
                            break;

                        case "like":
                            clauses.add("LOWER(" + col + ") LIKE ?");
                            params.add(val == null ? null : "%" + val.toString().toLowerCase() + "%");
                            break;

                        case "in":
                        case "not in":
                            Collection<?> coll = toCollection(val);
                            if (coll == null || coll.isEmpty()) {
                                if (op.equals("in")) clauses.add("1=0");
                                else clauses.add("1=1");
                            } else {
                                String placeholders = coll.stream().map(x -> "?").collect(Collectors.joining(","));
                                clauses.add(col + " " + op.toUpperCase() + " (" + placeholders + ")");
                                params.addAll(coll.stream().map(v -> convertForType(v, type)).toList());
                            }
                            break;

                        case "between":
                            List<?> betweenValues = toList(val);
                            if (betweenValues == null || betweenValues.size() < 2) continue;

                            if (isDatePartType(type)) {
                                String sqlCol = buildTimeColumnSQL(col, type);

                                String[] v1 = convertDateValue(betweenValues.get(0), type);
                                String[] v2 = convertDateValue(betweenValues.get(1), type);

                                clauses.add(sqlCol + " BETWEEN ? AND ?");
                                params.add(v1[0]);
                                params.add(v2[0]);
                            } else {
                                clauses.add(col + " BETWEEN ? AND ?");
                                params.add(convertForType(betweenValues.get(0), type));
                                params.add(convertForType(betweenValues.get(1), type));
                            }
                            break;

                        case "is null":
                        case "is not null":
                            clauses.add(col + " " + op.toUpperCase());
                            break;

                        default:
                            throw new IllegalArgumentException("Unsupported operator: " + op);
                    }
                }

                if (!clauses.isEmpty()) {
                    where.append(" WHERE ").append(String.join(" AND ", clauses));
                }
            }

            // Build base query
            String baseQuery = "SELECT * FROM " + tableName + where.toString();

            // Count total if pagination enabled
            long total = -1;
            if (pageSize > 0) {
                String countSql = "SELECT COUNT(*) FROM " + tableName + where.toString();
                try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                    setPreparedStatementParams(psCount, params);
                    try (ResultSet rs = psCount.executeQuery()) {
                        if (rs.next()) total = rs.getLong(1);
                    }
                }
            }

            // Add ORDER BY
            String orderPart = getStringOrderPart(filter);

            // Add LIMIT/OFFSET if needed
            String pagingPart = "";
            List<Object> finalParams = new ArrayList<>(params);
            if (pageSize > 0) {
                pagingPart = " LIMIT ? OFFSET ?";
                finalParams.add(pageSize);
                finalParams.add((long) pageNumber * pageSize);
            }

            String finalSql = baseQuery + orderPart + pagingPart;
            log.info("SearchSQL: {}", finalSql);
            log.info("SearchParams: {}", finalParams);
            try (PreparedStatement ps = conn.prepareStatement(finalSql)) {
                setPreparedStatementParams(ps, finalParams);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Map<String, Object>> rows = new ArrayList<>();
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int i = 1; i <= cols; i++) {
                            row.put(md.getColumnLabel(i), rs.getObject(i));
                        }
                        rows.add(row);
                    }
                    if (pageSize > 0) return new SearchResult(total, rows);
                    else return new SearchResult(rows.size(), rows);
                }
            }
        }
    }

    // ---------- Helper methods ----------
    private static void setPreparedStatementParams(PreparedStatement ps, List<Object> params) throws SQLException {
        int idx = 1;
        for (Object p : params) {
            if (p == null) ps.setObject(idx++, null);
            else if (p instanceof Integer) ps.setInt(idx++, (Integer) p);
            else if (p instanceof Long) ps.setLong(idx++, (Long) p);
            else if (p instanceof Double) ps.setDouble(idx++, (Double) p);
            else if (p instanceof Float) ps.setFloat(idx++, (Float) p);
            else if (p instanceof Boolean) ps.setBoolean(idx++, (Boolean) p);
            else if (p instanceof java.sql.Date) ps.setDate(idx++, (java.sql.Date) p);
            else if (p instanceof java.sql.Timestamp) ps.setTimestamp(idx++, (java.sql.Timestamp) p);
            else ps.setObject(idx++, p);
        }
    }

    private static String getStringOrderPart(DynamicFilter filter) {
        String orderPart = "";
        if (filter.getSortProperty() != null && !filter.getSortProperty().trim().isEmpty()) {
            String sortProp = filter.getSortProperty();
            if (!sortProp.matches("[A-Za-z0-9_.]+")) return orderPart;
            String order = "ASC";
            if ("desc".equalsIgnoreCase(filter.getSortOrder())) order = "DESC";
            orderPart = " ORDER BY " + sortProp + " " + order;
        }
        return orderPart;
    }

    private static boolean isDatePartType(String type) {
        return switch (type) {
            case "MONTH", "YEAR", "DAY", "HOUR" -> true;
            default -> false;
        };
    }

    private static Object convertForType(Object v, String type) {
        if (v == null) return null;
        switch (type) {
            case "NUMBER":
                if (v instanceof Number) return v;
                return Long.parseLong(v.toString());
            case "BOOLEAN":
                if (v instanceof Boolean) return v;
                return Boolean.parseBoolean(v.toString());
            case "DATE":
                if (v instanceof java.sql.Date) return v;
                if (v instanceof java.util.Date) return new java.sql.Date(((java.util.Date) v).getTime());
                return java.sql.Date.valueOf(v.toString());
            default:
                return v.toString();
        }
    }

    private static Collection<?> toCollection(Object val) {
        if (val == null) return null;
        if (val instanceof Collection) return (Collection<?>) val;
        if (val.getClass().isArray()) return Arrays.asList((Object[]) val);
        if (val instanceof String s) {
            if (s.isEmpty()) return Collections.emptyList();
            return Arrays.asList(s.split(","));
        }
        return Collections.singletonList(val);
    }

    private static List<?> toList(Object val) {
        if (val == null) return null;
        if (val instanceof List) return (List<?>) val;
        if (val.getClass().isArray()) return Arrays.asList((Object[]) val);
        if (val instanceof Collection) return new ArrayList<>((Collection<?>) val);
        if (val instanceof String s) return Arrays.asList(s.split(","));
        return Collections.singletonList(val);
    }

    private static Set<String> getActualColumns(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getColumns(null, null, tableName, null);
        Set<String> cols = new HashSet<>();
        while (rs.next()) {
            cols.add(rs.getString("COLUMN_NAME").toLowerCase());
        }
        return cols;
    }

    private static final DateTimeFormatter ISO_INPUT = DateTimeFormatter.ISO_DATE_TIME;

    private static LocalDateTime parseDateTime(Object v) {
        if (v instanceof LocalDateTime dt) return dt;
        return LocalDateTime.parse(v.toString(), ISO_INPUT);
    }

    private static String[] convertDateValue(Object v, String type) {
        LocalDateTime dt = parseDateTime(v);

        return switch (type) {
            case "YEAR" -> new String[]{ String.valueOf(dt.getYear()) };
            case "MONTH" -> new String[]{ dt.getYear() + "-" + String.format("%02d", dt.getMonthValue()) };
            case "DAY" -> new String[]{ dt.toLocalDate().toString() };
            case "HOUR" -> new String[]{ dt.toLocalDate().toString() + " " + String.format("%02d", dt.getHour()) };
            case "MINUTE" -> new String[]{ dt.toLocalDate().toString() + " " +
                    String.format("%02d:%02d", dt.getHour(), dt.getMinute()) };
            case "SECOND" -> new String[]{ dt.toLocalDate().toString() + " " +
                    String.format("%02d:%02d:%02d", dt.getHour(), dt.getMinute(), dt.getSecond()) };
            default -> throw new IllegalArgumentException("Unsupported time type: " + type);
        };
    }

    private static String buildTimeColumnSQL(String col, String type) {
        return switch (type) {
            case "YEAR" -> "YEAR(" + col + ")";
            case "MONTH" -> "CONCAT(YEAR(" + col + "), '-', LPAD(MONTH(" + col + "), 2, '0'))";
            case "DAY" -> "DATE(" + col + ")";
            case "HOUR" -> "DATE_FORMAT(" + col + ", '%Y-%m-%d %H')";
            case "MINUTE" -> "DATE_FORMAT(" + col + ", '%Y-%m-%d %H:%i')";
            case "SECOND" -> "DATE_FORMAT(" + col + ", '%Y-%m-%d %H:%i:%s')";
            default -> throw new IllegalArgumentException("Unsupported date type " + type);
        };
    }

}
