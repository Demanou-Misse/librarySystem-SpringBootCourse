package com.example.template.dto.book;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for Book representation returned to the client.
 */
public record BookResponseDTO(
        Long id,
        String title,
        String author,
        BigDecimal price, // Use BigDecimal for financial data
        int stock,
        LocalDate publishedDate,
        String coverImage,
        String pdfPath
) {}

