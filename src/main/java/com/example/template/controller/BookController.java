package com.example.template.controller;

import com.example.template.dto.book.BookRequestDTO;
import com.example.template.dto.book.BookRequestUpdateDTO;
import com.example.template.dto.book.BookResponseDTO;
import com.example.template.exception.ResourceNotFoundException;
import com.example.template.model.Member;
import com.example.template.response.ApiResponse;
import com.example.template.service.interfaces.BookService;
import com.example.template.service.interfaces.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Book Controller - Phase 4 (Advanced Authorization).
 * Implements Role-Based Access Control (RBAC) for library inventory.
 */
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Books", description = "Library inventory management")
public class BookController {

    private final BookService service;
    private final FileStorageService fileStorageService;

    /**
     * Create a new book.
     * Restricted to ADMIN and LIBRARIAN roles.
     */
    @PostMapping
    @Operation(summary = "Create a new book")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookResponseDTO>> create(@Valid @RequestBody BookRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book created successfully", service.create(request)));
    }

    /**
     * Get all books.
     * Accessible by any authenticated user (USER, LIBRARIAN, ADMIN).
     */
    @GetMapping
    @Operation(summary = "Get all books with dynamic filtering, pagination and sorting")
    public ResponseEntity<ApiResponse<Page<BookResponseDTO>>> getAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable) {

        Page<BookResponseDTO> books = service.getAll(title, author, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Books retrieved successfully", books)
        );
    }


    /**
     * Get book by ID.
     * Accessible by any authenticated user.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    public ResponseEntity<ApiResponse<BookResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Book found", service.findById(id)));
    }

    /**
     * Full update of a book.
     * Restricted to ADMIN and LIBRARIAN roles.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update of a book")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestUpdateDTO request) {
        return ResponseEntity.ok(ApiResponse.success("Book updated successfully", service.update(id, request)));
    }

    /**
     * Upload of a book's cover.
     * Restricted to ADMIN and LIBRARIAN roles.
     */
    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload of a book's cover")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookResponseDTO>> uploadBookCover(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        BookResponseDTO response = service.uploadCover(id, file);
        return ResponseEntity.ok(ApiResponse.success("Cover uploaded successfully", response));
    }

    /**
     * View of a book's cover.
     * Accessible by any authenticated user.
     */
    @GetMapping("/covers/{fileName:.+}")
    @Operation(summary = "View of a book's cover")
    public ResponseEntity<Resource> getCover(
            @PathVariable String fileName,
            HttpServletRequest request) {

        Resource resource = fileStorageService.loadFileAsResource(fileName, "covers");
        String contentType = "application/octet-stream";

        try {
            contentType = request.getServletContext().getMimeType(
                    resource.getFile().getAbsolutePath()
            );
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Upload a PDF for a book.
     * Restricted to ADMIN and LIBRARIAN roles.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping(value = "/{id}/upload-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload book PDF (Admin/Librarian only)")
    public ResponseEntity<ApiResponse<BookResponseDTO>> uploadPdf(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        // The service handles the storage and updating the pdfPath field
        BookResponseDTO updatedBook = service.uploadPdf(id, file);
        return ResponseEntity.ok(ApiResponse.success("PDF successfully linked to book", updatedBook));
    }

    /**
     * Professional PDF Access Endpoint (View & Download)
     * Optimized for Library System 2026.
     */
    @GetMapping("/{id}/pdf")
    @Operation(summary = "Access book PDF (View or Download)")
    public ResponseEntity<Resource> getBookPdf(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean download,
            @AuthenticationPrincipal Member currentMember) {

        // Get resource with security & audit checks
        Resource resource = service.getBookPdfResource(id, currentMember);

        // Determine content disposition
        // "inline" for reading in browser, "attachment" for downloading
        String disposition = download ? "attachment" : "inline";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + resource.getFilename() + "\"")
                // Disable caching for sensitive PDFs to ensure security checks on every access
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .body(resource);
    }

    /**
     * Delete a book.
     * Strictly restricted to ADMIN only (High-level authority).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
    }
}

