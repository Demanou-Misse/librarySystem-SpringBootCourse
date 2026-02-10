package com.example.template.model;

import com.example.template.model.enums.AppRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Member Entity - Enterprise Standard 2026.
 * Integrated with UserDetails and Typed Enums for RBAC.
 */
@Entity
@Table(name = "members")
@Getter @Setter
@ToString(callSuper = true) // Ensures fields from the parent class (AuditableEntity) like createdAt are included in logs.
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Restricts direct instantiation (e.g., new Member()) to favor the Builder Pattern, while remaining JPA-compliant.
@AllArgsConstructor
@SuperBuilder
public class Member extends AuditableEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private String password;

    // FIX: Using Set<AppRole> instead of List<String> for better type safety and no duplicates
    @ElementCollection(fetch = FetchType.EAGER) // Forces the immediate loading of roles when a member is fetched, which is critical for security checks.
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id")) // Maps a simple list of Enums (Roles) into a dedicated database table without needing a full Entity class for roles.
    @Enumerated(EnumType.STRING) // Saves Enums as readable text (e.g., "ROLE_ADMIN") in the DB instead of confusing numbers (0, 1).
    @Builder.Default // Guarantees that default values (like an empty HashSet) are initialized even when using the Builder pattern.
    private Set<AppRole> roles = new HashSet<>(Set.of(AppRole.ROLE_USER));

    @JsonIgnore // Prevents "Circular Reference" errors. It stops the API from trying to display Loans inside a Member, who is inside a Loan, etc.
    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true) // 1. Life-cycle synchronization. If a Member is deleted, their associated Loans are automatically cleaned up. 2. If one of loans is deleted, the database is automatically updated.
    @ToString.Exclude // Prevents the toString() method from loading heavy collections (like loans), which avoids performance issues and LazyInitializationException.
    @Setter(AccessLevel.NONE)
    private final Set<Loan> loans = new HashSet<>();

    // --- Helper Methods ---
    // A defensive coding practice. It provides "read-only" access to a collection, forcing the use of specific helper methods (addLoan) for modifications.
    public Set<Loan> getLoans() {
        return Collections.unmodifiableSet(loans);
    }

    public void addLoan(Loan loan) {
        if (loan != null && !this.loans.contains(loan)) {
            this.loans.add(loan);
            loan.setMember(this);
        }
    }

    public void removeLoan(Loan loan) {
        if (loan != null) {
            this.loans.remove(loan);
            loan.setMember(null);
        }
    }

    // --- Update Password ---
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // --- UserDetails Implementation ---

    /**
     * Converts AppRole enums into Spring Security GrantedAuthority.
     * Uses role.name() to get the String value (e.g., "ROLE_USER").
     */

    // The bridge to Spring Security. It maps your custom Enums to Spring's internal security system to handle permissions.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return email;
    }

    // --- Identity Methods ---

    /**
     * Checks if the account has expired.
     * Returns true because expiration logic is not yet implemented.
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Checks if the user is banned or locked out.
     * Returns true so users are never locked by default.
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Checks if the password needs to be changed.
     * Returns true to allow current password to remain valid.
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Checks if the account is active.
     * Returns true so all created members can log in immediately.
     */
    @Override
    public boolean isEnabled() { return true; }
}
