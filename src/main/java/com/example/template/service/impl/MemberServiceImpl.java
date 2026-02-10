package com.example.template.service.impl;

import com.example.template.dto.member.*;
import com.example.template.exception.AlreadyExistsException;
import com.example.template.exception.AuthException;
import com.example.template.exception.ResourceNotFoundException;
import com.example.template.mapper.MemberMapper;
import com.example.template.model.Member;
import com.example.template.model.enums.AppRole;
import com.example.template.repository.MemberRepository;
import com.example.template.service.interfaces.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder; // Spring injects your BCrypt bean here

    @Override
    @Transactional
    public MemberResponseDTO create(MemberRequestDTO request) {
        if (memberRepository.existsByEmail(request.email())) {
            log.warn("Account creation failed: Email {} already exists", request.email());
            throw new AlreadyExistsException("A member with this email already exists");
        }
        Member member = memberMapper.toEntity(request);

        // Security Note: Don't forget to encode password here!
        member.updatePassword(passwordEncoder.encode(request.password()));

        Member savedMember = memberRepository.save(member);
        log.info("New member successfully created with email: {}", savedMember.getEmail());
        return memberMapper.toResponse(savedMember);
    }

    @Override
    public MemberResponseDTO findById(Long id) {
        return memberRepository.findById(id)
                .map(memberMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
    }

    @Override
    public MemberResponseDTO getByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(memberMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with email: " + email));
    }


    @Override
    public List<MemberResponseDTO> getAll() {
        return memberRepository.findAll().stream().map(memberMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public MemberResponseDTO update(Long id, MemberRequestUpdateDTO request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed: Member with ID {} not found", id);
                    return new ResourceNotFoundException("Member not found");
                });
        memberMapper.updateEntityFromDto(request, member);
        log.info("Profile updated for member ID: {}", id);
        return memberMapper.toResponse(memberRepository.save(member));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!memberRepository.existsById(id)) {
            log.warn("Delete aborted: Member ID {} does not exist", id);
            throw new ResourceNotFoundException("Member not found");
        }
        memberRepository.deleteById(id);
        log.info("Member ID {} has been permanently deleted", id);
    }

    @Transactional
    @Override
    public void updateMemberRoles(Long memberId, Set<AppRole> newRoles) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        member.setRoles(newRoles);

        memberRepository.save(member);
        log.info("Roles updated for member {}: {}", member.getEmail(), newRoles);
    }

    @Transactional
    @Override
    public void changePassword(Long memberId, PasswordUpdateDTO dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!passwordEncoder.matches(dto.oldPassword(), member.getPassword())) {
            log.warn("Security Alert: Unauthorized password change attempt for member {}", member.getEmail());
            throw new AuthException("Incorrect old password");
        }

        // Use encode() to hash the new password
        member.updatePassword(passwordEncoder.encode(dto.newPassword()));
        memberRepository.save(member);
        log.info("Password successfully changed for member: {}", member.getEmail());
    }


}
