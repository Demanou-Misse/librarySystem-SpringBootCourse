package com.example.template.service.impl;

import com.example.template.dto.loan.LoanResponseDTO;
import com.example.template.exception.ResourceNotFoundException;
import com.example.template.mapper.LoanMapper;
import com.example.template.model.*;
import com.example.template.model.enums.LoanStatus;
import com.example.template.repository.*;
import com.example.template.service.interfaces.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanServiceImpl implements LoanService {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;

    @Override
    @Transactional
    public LoanResponseDTO borrowBook(Long bookId) {
        // 1. Get current user identity from Security Context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in member not found in database"));

        // We define the deadline: 14 days ago
        LocalDate limitDate = LocalDate.now().minusDays(14);

        // 2. Check if any active loan was started BEFORE the limit date
        boolean hasOverdue = loanRepository.existsByMemberAndStatusAndBorrowDateBefore(
                member, LoanStatus.BORROWED, limitDate);

        if (hasOverdue) {
            log.warn("Loan rejected: User {} has books borrowed more than 14 days ago", email);
            throw new RuntimeException("Cannot borrow: You have overdue books (older than 14 days).");
        }

        // 3. Check book availability
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (book.getStock() <= 0) {
            throw new RuntimeException("Book out of stock");
        }

        // 4. Create and persist loan
        Loan loan = Loan.builder()
                .borrowDate(LocalDate.now())
                .status(LoanStatus.BORROWED)
                .member(member)
                .book(book)
                .build();

        // Update inventory
        book.setStock(book.getStock() - 1);

        log.info("Book '{}' successfully borrowed by '{}'", book.getTitle(), email);
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    @Transactional
    public LoanResponseDTO returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan record not found"));

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        // Return book to stock
        loan.getBook().setStock(loan.getBook().getStock() + 1);

        log.info("Loan ID {} has been returned", loanId);
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public List<LoanResponseDTO> findMyLoanHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        return loanMapper.toResponseList(
                loanRepository.findByMemberOrderByBorrowDateDesc(member)
        );
    }

    @Override
    public List<LoanResponseDTO> findAllActiveLoans() {
        return loanMapper.toResponseList(
                loanRepository.findByStatus(LoanStatus.BORROWED)
        );
    }

    @Override
    public List<LoanResponseDTO> findAllOverdueLoans() {
        // Assuming 14 days loan period standard
        return loanMapper.toResponseList(
                loanRepository.findByStatusAndBorrowDateBefore(LoanStatus.BORROWED, LocalDate.now().minusDays(14))
        );
    }

    @Override
    public List<LoanResponseDTO> findMemberLoanHistory(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        return loanMapper.toResponseList(
                loanRepository.findByMemberOrderByBorrowDateDesc(member)
        );
    }
}
