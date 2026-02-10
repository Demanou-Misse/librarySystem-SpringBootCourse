package com.example.template.repository;

import com.example.template.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository for Book inventory management.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    /**
     * Used during book creation to prevent duplicate ISBN entries.
     */
    boolean existsByIsbn(String isbn);
}
