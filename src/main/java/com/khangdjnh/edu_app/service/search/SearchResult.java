package com.khangdjnh.edu_app.service.search;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private long total;
    private List<Map<String, Object>> data;
}
