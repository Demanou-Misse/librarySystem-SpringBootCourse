package com.example.template.mapper;

import com.example.template.dto.loan.LoanResponseDTO;
import com.example.template.model.*;
import com.example.template.model.enums.LoanStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for LoanMapper - Testing Data Transformation.
 */
class LoanMapperTest {

    private final LoanMapper loanMapper = Mappers.getMapper(LoanMapper.class);

    @Test
    void shouldMapLoanToLoanResponseDTO() {
        // Arrange
        Member member = Member.builder().id(1L).name("Alice").build();
        Book book = Book.builder().id(5L).title("Clean Architecture").build();

        Loan loan = Loan.builder()
                .id(100L)
                .borrowDate(LocalDate.now())
                .status(LoanStatus.BORROWED)
                .member(member)
                .book(book)
                .build();

        // Act
        LoanResponseDTO response = loanMapper.toResponse(loan);

        // Assert
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.memberName()).isEqualTo("Alice"); // Verifies flattening
        assertThat(response.bookTitle()).isEqualTo("Clean Architecture");
        assertThat(response.status()).isEqualTo(LoanStatus.BORROWED);
    }
}

