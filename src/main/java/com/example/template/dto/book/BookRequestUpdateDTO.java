package com.example.template.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for updating a Book's details (partial update allowed).
 */
public record BookRequestUpdateDTO(
        @Schema(description = "Title of the book", example = "The Great Gatsby")
        String title,

        @Schema(description = "Author's full name", example = "F. Scott Fitzgerald")
        String author,

        @Schema(description = "Unique ISBN identifier", example = "9783161484100")
        @Size(min = 10, max = 13, message = "ISBN must be between 10 and 13 characters")
        String isbn,

        @Schema(description = "Price of the book", example = "300")
        BigDecimal price, // Use BigDecimal

        @Schema(description = "Number of copies available for loan", example = "5")
        @PositiveOrZero(message = "Stock cannot be negative")
        Integer stock,
        LocalDate publishedDate
) {}
