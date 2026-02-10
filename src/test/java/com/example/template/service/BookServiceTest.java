package com.example.template.service;

import com.example.template.dto.book.BookRequestDTO;
import com.example.template.dto.book.BookResponseDTO;
import com.example.template.exception.AlreadyExistsException;
import com.example.template.mapper.BookMapper;
import com.example.template.model.Book;
import com.example.template.repository.BookRepository;
import com.example.template.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock private BookRepository bookRepository;
    @Mock private BookMapper bookMapper;
    @InjectMocks private BookServiceImpl bookService;

    @Test
    @DisplayName("Should successfully create a book and return the correct DTO")
    void shouldCreateBookSuccessfully() {
        // Arrange
        LocalDate publishedDate = LocalDate.of(1925, 4, 10);
        BigDecimal price = new BigDecimal("29.99");

        BookRequestDTO request = new BookRequestDTO(
                "The Great Gatsby", "F. Scott Fitzgerald", publishedDate,
                "978-3-16-148410-0", price, 5
        );

        Book bookEntity = Book.builder()
                .id(1L)
                .title("The Great Gatsby")
                .price(price)
                .build();

        // Correcting based on your DTO structure (no isbn here)
        BookResponseDTO expectedResponse = new BookResponseDTO(
                1L, "The Great Gatsby", "F. Scott Fitzgerald",
                price, 5, publishedDate
        );

        when(bookRepository.existsByIsbn("978-3-16-148410-0")).thenReturn(false);
        when(bookMapper.toEntity(request)).thenReturn(bookEntity);
        when(bookRepository.save(any(Book.class))).thenReturn(bookEntity);
        when(bookMapper.toResponse(bookEntity)).thenReturn(expectedResponse);

        // Act
        BookResponseDTO result = bookService.create(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("The Great Gatsby");
        assertThat(result.price()).isEqualByComparingTo(price);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when ISBN already exists")
    void shouldThrowExceptionIfIsbnExists() {
        // Arrange
        BookRequestDTO request = new BookRequestDTO(
                "Title", "Author", LocalDate.now(), "1234567890", BigDecimal.TEN, 1
        );
        when(bookRepository.existsByIsbn("1234567890")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> bookService.create(request))
                .isInstanceOf(AlreadyExistsException.class);

        verify(bookRepository, never()).save(any());
    }
}
