package com.example.template.service.interfaces;

import com.example.template.dto.book.*;
import com.example.template.model.Member;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface BookService {
    BookResponseDTO create(BookRequestDTO request);
    BookResponseDTO findById(Long id);
    Page<BookResponseDTO> getAll(String title, String author, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    BookResponseDTO update(Long id, BookRequestUpdateDTO request);
    BookResponseDTO uploadCover(Long id, MultipartFile file);
    BookResponseDTO uploadPdf(Long bookId, MultipartFile file);
    Resource getBookPdfResource(Long bookId, Member currentMember);
    void delete(Long id);
}
