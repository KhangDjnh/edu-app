package com.khangdjnh.edu_app.service.search;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFilter {
    private Integer pageNumber = 0;
    private Integer pageSize = 20;
    private Map<String, FilterEntity> filterMap;
    private String sortProperty;
    private String sortOrder; // "ASC" or "DESC"
}
