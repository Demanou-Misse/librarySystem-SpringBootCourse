package com.example.template.service.interfaces;

import com.example.template.dto.member.*;
import com.example.template.model.enums.AppRole;

import java.util.List;
import java.util.Set;

public interface MemberService {
    MemberResponseDTO create(MemberRequestDTO request);
    MemberResponseDTO findById(Long id);
    MemberResponseDTO getByEmail(String email);
    List<MemberResponseDTO> getAll();
    MemberResponseDTO update(Long id, MemberRequestUpdateDTO request);
    void delete(Long id);

    void updateMemberRoles(Long memberId, Set<AppRole> newRoles);
    void changePassword(Long memberId, PasswordUpdateDTO dto);
}

