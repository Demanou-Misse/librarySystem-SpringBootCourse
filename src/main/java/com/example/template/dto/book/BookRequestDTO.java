package com.example.template.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new Book record.
 * Uses BigDecimal for precise financial calculations.
 */
public record BookRequestDTO(
        @Schema(description = "Title of the book", example = "The Great Gatsby")
        @NotBlank(message = "The Title is required!")
        String title,

        @Schema(description = "Author's full name", example = "F. Scott Fitzgerald")
        @NotBlank(message = "The author's name is required!")
        String author,

        @Schema(description = "The date of Publication", example = "2021-01-21")
        @PastOrPresent(message = "The date of publication cannot be in the future!")
        @NotNull(message = "Published date is required")
        LocalDate publishedDate,

        @Schema(description = "Unique ISBN identifier", example = "9783161484100")
        @NotBlank(message = "The ISBN is required!")
        @Size(min = 10, max = 13, message = "The ISBN must be between 10 and 13 characters long.")
        String isbn,

        @Schema(description = "Price of the book", example = "300")
        @Positive(message = "The price must be positive!")
        @NotNull(message = "Price is required")
        BigDecimal price, // Standard Pro 2026: Always use BigDecimal for money

        @Schema(description = "Number of copies available for loan", example = "5")
        @PositiveOrZero(message = "The stock cannot be negative!")
        int stock
) {}
