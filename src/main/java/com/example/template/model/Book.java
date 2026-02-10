package com.example.template.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter // Getters are fine
@Setter // Be careful with setters on Entities, prefer behavior methods
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Use protected to force controlled instantiation
@AllArgsConstructor
@SuperBuilder
public class Book extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID is immutable after creation
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String author;

    @NotNull
    @Column(nullable = false)
    private LocalDate publishedDate;

    @NotBlank
    @Column(unique = true, nullable = false, length = 20)
    private String isbn;

    @NotNull
    // Use scale=4 for currencies for future-proofing international formats
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal price;

    @Min(0)
    @Column(nullable = false)
    private int stock;

    @Column(length = 255)
    private String coverImage; // ex: cover-uuid.jpg

    @Column(length = 255)
    private String pdfPath;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true) // Added orphanRemoval=true for data cleanup
    @ToString.Exclude
    @Setter(AccessLevel.NONE) // Protects collection integrity
    private final Set<Loan> loans = new HashSet<>(); // Made final

    /**
     * Helper method to maintain bidirectional synchronization.
     * Returns an unmodifiable set to protect internal collection state.
     */
    public Set<Loan> getLoans() {
        return Collections.unmodifiableSet(loans);
    }

    public void addLoan(Loan loan) {
        if (loan != null && !this.loans.contains(loan)) {
            this.loans.add(loan);
            loan.setBook(this);
        }
    }
}
