package com.example.template.dto.loan;

import com.example.template.model.enums.LoanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LoanResponseDTO(
        @Schema(description = "Unique identifier of the loan", example = "101")
        Long id,

        @Schema(description = "Date when the book was borrowed", example = "2026-01-21")
        LocalDate borrowDate,

        @Schema(description = "Date when the book was returned (null if active)", example = "2026-02-04")
        LocalDate returnDate,

        @Schema(description = "Current status of the loan", example = "BORROWED")
        LoanStatus status,

        @Schema(description = "Name of the borrowing member", example = "Standard User")
        String memberName,

        @Schema(description = "Title of the borrowed book", example = "Clean Code")
        String bookTitle,

        @Schema(description = "Identifier of member", example = "104")
        Long memberId,

        @Schema(description = "Identifier of book", example = "200")
        Long bookId
) {}

