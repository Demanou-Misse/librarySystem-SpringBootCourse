package com.example.template.service;

import com.example.template.model.*;
import com.example.template.repository.*;
import com.example.template.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private BookRepository bookRepository;
    @Mock private LoanRepository loanRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks private LoanServiceImpl loanService;

    @BeforeEach
    void setUpSecurity() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@test.com");
    }

    @Test
    void shouldRejectLoanIfBookOutOfStock() {
        // Arrange
        Member member = Member.builder().email("user@test.com").build();
        Book book = Book.builder().id(1L).stock(0).build(); // Out of stock

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act & Assert
        assertThatThrownBy(() -> loanService.borrowBook(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("out of stock");
    }

    @Test
    void shouldRejectLoanIfMemberHasOverdueBooks() {
        // Arrange
        Member member = Member.builder().email("user@test.com").build();
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        // Mocking an overdue book found
        when(loanRepository.existsByMemberAndStatusAndBorrowDateBefore(any(), any(), any()))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> loanService.borrowBook(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("overdue books");
    }
}

