package com.khangdjnh.edu_app.service.search;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterEntity {
    private Object value;
    private String operator;
    private String type;
    private String sortOrder;

    public <T> FilterEntity(Object value, String operator) {
        this.value = value;
        this.operator = operator;
    }

    public <T> FilterEntity(Object value, String operator, String type) {
        this.value = value;
        this.operator = operator;
        this.type = type;
    }
}
