package com.example.template.repository;

import com.example.template.model.Book;
import com.example.template.model.Loan;
import com.example.template.model.Member;
import com.example.template.model.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for managing book loans and tracking statuses.
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByStatus(LoanStatus status);

    /**
     * Finds overdue or old loans based on a specific limit date.
     */
    List<Loan> findByStatusAndBorrowDateBefore(LoanStatus status, LocalDate limitDate);

    /**
     * Retrieves all loans for a specific member, sorted by most recent first.
     */
    List<Loan> findByMemberOrderByBorrowDateDesc(Member member);

    /**
     * Validation check to see if a member has any overdue loans.
     */
    boolean existsByMemberAndStatusAndBorrowDateBefore(Member member, LoanStatus status, LocalDate date);

    /**
     * Check if the member has an active loan for this specific book
     */
     boolean existsByBookAndMemberAndStatus(Book book, Member currentMember, LoanStatus loanStatus);
}

