package com.example.template.mapper;

import com.example.template.dto.loan.LoanResponseDTO;
import com.example.template.model.Loan;
import org.mapstruct.*;

/**
 * This annotation tells MapStruct to generate an implementation of this interface.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoanMapper {

    /**
     * Maps a Loan entity to an immutable response DTO.
     * Extracts member and book details for a flattened response.
     */
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.name", target = "memberName")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    LoanResponseDTO toResponse(Loan loan);

    /**
     * Helper to map a list of loans.
     */
    java.util.List<LoanResponseDTO> toResponseList(java.util.List<Loan> loans);
}

