package com.example.template.mapper;

import com.example.template.dto.member.MemberRequestDTO;
import com.example.template.dto.member.MemberRequestUpdateDTO;
import com.example.template.dto.member.MemberResponseDTO;
import com.example.template.model.Member;
import org.mapstruct.*;

/**
 * Mapper interface for Member entity and its DTOs.
 * Uses Spring component model for dependency injection.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MemberMapper {

    /**
     * Maps a creation request DTO to a Member entity.
     * Password should be encoded in the service layer before persistence.
     */
    Member toEntity(MemberRequestDTO request);

    /**
     * Maps a Member entity to a response Record.
     */
    MemberResponseDTO toResponse(Member member);

    /**
     * Updates an existing Member entity with non-null values from the update DTO.
     * Essential for PATCH-style partial updates.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(MemberRequestUpdateDTO dto, @MappingTarget Member member);
}
