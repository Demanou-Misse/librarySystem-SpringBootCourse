package com.example.template.service.interfaces;

import com.example.template.dto.loan.LoanResponseDTO;
import java.util.List;

/**
 * Loan Service Interface - Phase 5 Standards.
 * Identity is managed via SecurityContext for borrowing.
 */
public interface LoanService {

    // Identity extracted from JWT, no memberId needed
    LoanResponseDTO borrowBook(Long bookId);

    LoanResponseDTO returnBook(Long loanId);

    List<LoanResponseDTO> findAllActiveLoans();

    List<LoanResponseDTO> findAllOverdueLoans();

    // For Admin/Librarian to check a specific member's history
    List<LoanResponseDTO> findMemberLoanHistory(Long memberId);

    // For the logged-in user to check their own history
    List<LoanResponseDTO> findMyLoanHistory();
}




