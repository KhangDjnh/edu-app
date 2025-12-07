# --------------------------------------------------------
# Stage 1: Build ứng dụng (Dùng Maven và JDK 21)
# --------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy toàn bộ source code vào
COPY . .

# Build ra file .jar (Bỏ qua test để build nhanh hơn trên Render)
RUN mvn clean package -DskipTests

# --------------------------------------------------------
# Stage 2: Chạy ứng dụng (Dùng JRE 21 cho nhẹ)
# --------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# [QUAN TRỌNG] Cài thêm Font chữ vì bạn dùng Apache POI và iText
# Nếu không có dòng này, chức năng xuất PDF/Excel sẽ bị lỗi trên server Linux
RUN apk add --no-cache fontconfig ttf-dejavu

# Copy file .jar từ bước Build sang bước Run
COPY --from=build /app/target/*.jar app.jar

# [QUAN TRỌNG] Cấu hình RAM cho Render Free (512MB)
# -Xms256m: Ram khởi điểm
# -Xmx380m: Ram tối đa (Chừa lại khoảng 130MB cho OS chạy)
ENTRYPOINT ["java", "-Xms256m", "-Xmx380m", "-jar", "app.jar"]