package com.example.template.config;

import com.example.template.model.*;
import com.example.template.model.enums.AppRole;
import com.example.template.model.enums.LoanStatus;
import com.example.template.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Data Seeder for development environment - 2026 Updated Version.
 * Generates initial data with BCrypt encoded passwords and Enum-based roles.
 */
@Slf4j
@Configuration
@Profile("dev")
public class LibrarySimulationConfig {

    @Bean
    CommandLineRunner setupDatabase(
            MemberRepository memberRepository,
            BookRepository bookRepository,
            LoanRepository loanRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            log.info("🚀 Starting library data seeding (2026 Standards)...");

            // --- 1. SEED BOOKS ---
            Book b1 = Book.builder()
                    .title("The Lord of the Rings")
                    .author("J.R.R. Tolkien")
                    .isbn("978-2266154116")
                    .price(new BigDecimal("29.90"))
                    .publishedDate(LocalDate.of(1954, 7, 29))
                    .stock(5)
                    .build();

            Book b2 = Book.builder()
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("978-0132350884")
                    .price(new BigDecimal("42.00"))
                    .publishedDate(LocalDate.of(2008, 8, 1))
                    .stock(2)
                    .build();

            Book b3 = Book.builder()
                    .title("Spring Boot in Action")
                    .author("Craig Walls")
                    .isbn("978-1617292545")
                    .price(new BigDecimal("35.50"))
                    .publishedDate(LocalDate.of(2016, 1, 3))
                    .stock(1)
                    .build();

            bookRepository.saveAll(List.of(b1, b2, b3));

            // --- 1. SEED BOOKS (50 books for Pagination testing) ---
            log.info("📚 Seeding 50 books for pagination testing...");
            for (int i = 1; i <= 50; i++) {
                bookRepository.save(Book.builder()
                        .title("Pro Java Book Vol " + i)
                        .author(i % 2 == 0 ? "Joshua Bloch" : "Anghel Leonard") // Alternate authors
                        .isbn("978-013235" + String.format("%04d", i))
                        .price(new BigDecimal(10 + (Math.random() * 90)).setScale(2, BigDecimal.ROUND_HALF_UP))
                        .publishedDate(LocalDate.now().minusMonths(i))
                        .stock(i % 5 + 1)
                        .build());
            }

            // --- 2. SEED MEMBERS WITH SPECIFIC ROLES ---
            // Common password "password123" for all test accounts

            // Standard User: ROLE_USER only
            Member m1 = Member.builder()
                    .name("Standard User")
                    .email("user@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .roles(Set.of(AppRole.ROLE_USER))
                    .build();

            // Administrator: ROLE_ADMIN and ROLE_USER
            Member m2 = Member.builder()
                    .name("System Admin")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .roles(Set.of(AppRole.ROLE_USER, AppRole.ROLE_ADMIN))
                    .build();

            // Librarian: ROLE_LIBRARIAN and ROLE_USER
            Member m3 = Member.builder()
                    .name("Staff Librarian")
                    .email("staff@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .roles(Set.of(AppRole.ROLE_USER, AppRole.ROLE_LIBRARIAN))
                    .build();

            memberRepository.saveAll(List.of(m1, m2, m3));

            // --- 3. SEED LOANS ---
            Loan loan1 = Loan.builder()
                    .borrowDate(LocalDate.now().minusDays(5))
                    .status(LoanStatus.BORROWED)
                    .member(m1)
                    .book(b3)
                    .build();

            Loan loan2 = Loan.builder()
                    .borrowDate(LocalDate.now().minusDays(2))
                    .status(LoanStatus.BORROWED)
                    .member(m2)
                    .book(b2)
                    .build();

            loanRepository.saveAll(List.of(loan1, loan2));

            log.info("✅ Library simulation data seeded successfully!");
            log.info("👉 Test Credentials (all use 'password123'):");
            log.info("   - user@test.com    -> Role: USER");
            log.info("   - admin@test.com   -> Role: ADMIN");
            log.info("   - staff@test.com   -> Role: LIBRARIAN");
        };
    }
}

