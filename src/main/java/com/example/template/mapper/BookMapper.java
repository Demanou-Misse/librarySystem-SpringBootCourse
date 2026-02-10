package com.example.template.mapper;

import com.example.template.dto.book.BookRequestDTO;
import com.example.template.dto.book.BookRequestUpdateDTO;
import com.example.template.dto.book.BookResponseDTO;
import com.example.template.model.Book;
import org.mapstruct.*;

/**
 * Mapper interface for Book entity and its DTOs.
 * Handles the conversion between persistent models and immutable Records.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    /**
     * Maps a Book creation Record to a persistent Entity.
     */
    Book toEntity(BookRequestDTO request);

    /**
     * Maps a Book entity to an immutable response Record.
     */
    BookResponseDTO toResponse(Book book);

    /**
     * Partial update of a Book entity.
     * Null fields in the DTO will not overwrite existing data in the entity.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(BookRequestUpdateDTO dto, @MappingTarget Book entity);
}

