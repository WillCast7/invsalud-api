package com.aurealab.dto;

import com.aurealab.util.PageableBase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PageableResponseDTO implements PageableBase {
    private int pageNumber;
    private int pageSize;
    private long totalElements;

    // Implementar los métodos de la interfaz
    public int getPageNumber() { return pageNumber; }
    public int getPageSize() { return pageSize; }
    public long getTotalElements() { return totalElements; }
}