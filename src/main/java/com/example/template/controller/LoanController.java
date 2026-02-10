package com.example.template.controller;

import com.example.template.dto.loan.LoanResponseDTO;
import com.example.template.response.ApiResponse;
import com.example.template.service.interfaces.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Borrowing and returning books")
public class LoanController {

    private final LoanService service;

    @PostMapping("/borrow/{bookId}")
    @Operation(summary = "Borrow a book (Identity taken from JWT)")
    @PreAuthorize("hasRole('USER')") // Any logged-in user can borrow
    public ResponseEntity<ApiResponse<LoanResponseDTO>> borrowBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(ApiResponse.success("Book borrowed", service.borrowBook(bookId)));
    }

    @PostMapping("/return/{loanId}")
    @Operation(summary = "Return a borrowed book")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')") // Usually staff handles returns
    public ResponseEntity<ApiResponse<LoanResponseDTO>> returnBook(@PathVariable Long loanId) {
        return ResponseEntity.ok(ApiResponse.success("Book returned", service.returnBook(loanId)));
    }

    @GetMapping("/active")
    @Operation(summary = "List all currently borrowed books")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanResponseDTO>>> getActiveLoans() {
        return ResponseEntity.ok(ApiResponse.success("Active loans retrieved", service.findAllActiveLoans()));
    }

    @GetMapping("/overdue")
    @Operation(summary = "List all overdue books")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanResponseDTO>>> getOverdueLoans() {
        return ResponseEntity.ok(ApiResponse.success("Overdue loans retrieved", service.findAllOverdueLoans()));
    }

    /**
     * Endpoint for Admin/Librarian to see any member's history.
     */
    @GetMapping("/history/{memberId}")
    @Operation(summary = "Get a specific member's history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanResponseDTO>> getMemberHistory(@PathVariable Long memberId) {
        return ResponseEntity.ok(service.findMemberLoanHistory(memberId));
    }

    /**
     * Endpoint for the logged-in user to see their own history.
     */
    @GetMapping("/my-history")
    @Operation(summary = "Get my history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LoanResponseDTO>> getMyHistory() {
        return ResponseEntity.ok(service.findMyLoanHistory());
    }
}

