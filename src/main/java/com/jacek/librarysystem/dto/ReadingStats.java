package com.jacek.librarysystem.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ReadingStats {
    private BigDecimal avgBooksPerYear;

    private BigDecimal avgBooksPerMonth;

    private BigDecimal avgPagesPerDay;
}
