package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainPage<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    
    public int getTotalPages() {
        if (pageSize == 0) return 0;
        return (int) Math.ceil((double) totalElements / pageSize);
    }
} 