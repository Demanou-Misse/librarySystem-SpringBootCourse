package com.example.template.repository;

import com.example.template.config.AuditConfig;
import com.example.template.model.*;
import com.example.template.model.enums.AppRole;
import com.example.template.model.enums.LoanStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Enterprise Integration Test for LoanRepository - 2026 Standards.
 * Uses @Import(AuditConfig.class) to satisfy NOT NULL constraints on audit fields.
 */
@DataJpaTest
@Import(AuditConfig.class) // Charges your real AuditConfig (OffsetDateTime.now())
class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository loanRepository;

    @Test

    @DisplayName("Should detect an overdue loan when duration exceeds 14 days")
    void shouldReturnTrueForOverdueLoan() {
        // Arrange: Creating data. Audit fields are handled by AuditConfig + @Import
        Member member = Member.builder()
                .name("Professional User")
                .email("pro.user@example.com")
                .password("encoded_secret")
                .roles(Set.of(AppRole.ROLE_USER))
                .build();
        member = entityManager.persistAndFlush(member);

        Book book = Book.builder()
                .title("Clean Architecture 2026")
                .author("Robert C. Martin")
                .publishedDate(LocalDate.of(2020, 1, 1))
                .isbn("978-0134494166")
                .price(new BigDecimal("45.5000"))
                .stock(10)
                .build();
        book = entityManager.persistAndFlush(book);

        // Create a loan dated 20 days ago (strictly before the 14-day limit)
        Loan overdueLoan = Loan.builder()
                .borrowDate(LocalDate.now().minusDays(20))
                .status(LoanStatus.BORROWED)
                .member(member)
                .book(book)
                .build();

        entityManager.persistAndFlush(overdueLoan);

        // Act: Define the 14-day business rule limit
        LocalDate limitDate = LocalDate.now().minusDays(14);
        boolean isOverdue = loanRepository.existsByMemberAndStatusAndBorrowDateBefore(
                member, LoanStatus.BORROWED, limitDate);

        // Assert: Verify business logic
        assertThat(isOverdue).isTrue();
    }
}


