package com.example.template.controller;

import com.example.template.dto.member.MemberRequestDTO;
import com.example.template.dto.member.MemberRequestUpdateDTO;
import com.example.template.dto.member.MemberResponseDTO;
import com.example.template.dto.member.PasswordUpdateDTO;
import com.example.template.model.enums.AppRole;
import com.example.template.response.ApiResponse;
import com.example.template.service.interfaces.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Member Controller - Phase 4 (Advanced Authorization).
 * Handles member-related operations with Role-Based Access Control (RBAC).
 */
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Member account management")
public class MemberController {

    private final MemberService service;

    /**
     * Public or Admin: Create a new member.
     * Usually handled by AuthController/Register, but kept here for administrative creation.
     */
    @PostMapping
    @Operation(summary = "Register a new member")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> create(@Valid @RequestBody MemberRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED) // HTTP 201 Created explicitly confirms that a new resource (Member) was successfully stored in the database.
                .body(ApiResponse.success("Member created", service.create(request)));
    }

    /**
     * Get the profile of the currently authenticated user.
     * Uses @AuthenticationPrincipal to identify the user from the JWT.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current authenticated member profile")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> getCurrentMember(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", service.getByEmail(email)));
    }

    /**
     * Only Admin can see the full list of all members.
     */
    @GetMapping
    @Operation(summary = "Get all members list (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MemberResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Members list retrieved", service.getAll()));
    }

    /**
     * Get member by ID. Restrict to Admin or the specific User.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Member found", service.findById(id)));
    }

    /**
     * Update of a member profile.
     * Restricted to Admin or the account owner.
     */
    @PutMapping("/{id}")
    @Operation(summary = "update of a member")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequestUpdateDTO request) {
        return ResponseEntity.ok(ApiResponse.success("Member updated", service.update(id, request)));
    }

    /**
     * Delete a member account.
     * Strictly restricted to Admin in professional library systems.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a member account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Member deleted successfully", null));
    }

    /**
     * Update member roles - Admin Only.
     * Standard 2026: Using Set<AppRole> directly for type safety.
     */
    @PatchMapping("/{id}/roles")
    @Operation(summary = "Update member roles")
    @PreAuthorize("hasRole('ADMIN')") // Security constraint: Only admins can change roles
    public ResponseEntity<ApiResponse<String>> updateRoles(
            @PathVariable Long id,
            @RequestBody Set<AppRole> roles) {

        service.updateMemberRoles(id, roles);

        return ResponseEntity.ok(new ApiResponse<>(true, "Roles updated successfully", null));
    }

    /**
     * Update member Password
     */
    @PatchMapping("/{id}/password")
    @Operation(summary = "Update member password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Long id,
            @RequestBody @Valid PasswordUpdateDTO dto) {

        service.changePassword(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Mot de passe modifié avec succès", null));
    }


}
