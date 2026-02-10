package com.example.template.service.impl;

import com.example.template.dto.book.*;
import com.example.template.exception.AlreadyExistsException;
import com.example.template.exception.ResourceNotFoundException;
import com.example.template.mapper.BookMapper;
import com.example.template.model.Book;
import com.example.template.model.Member;
import com.example.template.model.enums.AppRole;
import com.example.template.model.enums.LoanStatus;
import com.example.template.repository.BookRepository;
import com.example.template.repository.LoanRepository;
import com.example.template.repository.specification.BookSpecification;
import com.example.template.service.interfaces.BookService;
import com.example.template.service.interfaces.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j // Enables the 'log' object
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final BookMapper bookMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public BookResponseDTO create(BookRequestDTO request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            log.warn("Inventory conflict: Book with ISBN {} already exists", request.isbn());
            throw new AlreadyExistsException("A book with this ISBN already exists");
        }

        Book book = bookMapper.toEntity(request);
        Book savedBook = bookRepository.save(book);

        log.info("Inventory update: New book '{}' added with ID {}", savedBook.getTitle(), savedBook.getId());
        return bookMapper.toResponse(savedBook);
    }

    @Override
    public BookResponseDTO findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toResponse)
                .orElseThrow(() -> {
                    log.error("Search failed: No book found with ID {}", id);
                    return new ResourceNotFoundException("Book not found");
                });
    }

    @Override
    public Page<BookResponseDTO> getAll(String title, String author, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        // Modern Approach: Start with an empty specification (ALL)
        Specification<Book> spec = Specification.allOf(
                BookSpecification.hasTitle(title),
                BookSpecification.hasAuthor(author),
                BookSpecification.priceBetween(minPrice, maxPrice)
        );

        return bookRepository.findAll(spec, pageable)
                .map(bookMapper::toResponse);
    }

    @Override
    @Transactional
    public BookResponseDTO update(Long id, BookRequestUpdateDTO request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update aborted: Book ID {} not found", id);
                    return new ResourceNotFoundException("Book not found");
                });

        bookMapper.updateEntityFromDto(request, book);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookResponseDTO uploadCover(Long id, MultipartFile file) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book ID {} not found", id);
                    return new ResourceNotFoundException("Book not found with id " + id);
                });

        if (book.getCoverImage() != null) {
            fileStorageService.deleteFile(book.getCoverImage(), "covers");
        }

        String fileName = fileStorageService.storeFile(file, "covers");

        book.setCoverImage(fileName);
        book = bookRepository.save(book);

        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional
    public BookResponseDTO uploadPdf(Long bookId, MultipartFile file) {
        // 1. Check if book exists
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // 2. Store file using our professional StorageService
        // This returns the unique generated filename (e.g. "uuid_manual.pdf")
        String fileName = fileStorageService.storeFile(file, "pdfs");

        // 3. Update the entity (This is where pdfPath is filled!)
        book.setPdfPath(fileName);

        // 4. Save and return updated DTO
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    public Resource getBookPdfResource(Long bookId, Member currentMember) {
        // 1. Fetch book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        // 2. Security Check : Check if user is allowed to access
        boolean isAdminOrLibrarian = currentMember.getRoles().stream()
                .anyMatch(r -> r == AppRole.ROLE_ADMIN || r == AppRole.ROLE_LIBRARIAN);

        // Check if the member has an active loan for this specific book
        boolean hasActiveLoan = loanRepository.existsByBookAndMemberAndStatus(book, currentMember, LoanStatus.BORROWED);

        if (!isAdminOrLibrarian && !hasActiveLoan) {
            throw new AccessDeniedException("You must have an active loan to view this PDF.");
        }

        // 3. Audit : Log the access event
        log.info("AUDIT: User {} accessed PDF for book ID: {} at {}",
                currentMember.getEmail(), bookId, LocalDateTime.now());

        return fileStorageService.loadFileAsResource(book.getPdfPath(), "pdfs");
    }


    @Override
    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            log.error("Deletion failed: Book ID {} does not exist", id);
            throw new ResourceNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
        log.info("Inventory update: Book ID {} permanently removed from catalog", id);
    }
}
