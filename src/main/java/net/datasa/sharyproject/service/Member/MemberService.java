package net.datasa.sharyproject.service.Member;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.Member.MemberDTO;
import net.datasa.sharyproject.domain.entity.Member.MemberEntity;
import net.datasa.sharyproject.repository.Member.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 회원정보 관련 처리 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void join(MemberDTO memberDTO) {
        MemberEntity memberEntity = MemberEntity.builder()
                .memberId(memberDTO.getMemberId())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .birthdate(memberDTO.getBirthdate())
                .fullName(memberDTO.getFullName())
                .nickname(memberDTO.getNickname())
                .gender(memberDTO.getGender())
                .email(memberDTO.getEmail())
                .phoneNumber(memberDTO.getPhoneNumber())
                .roleName("ROLE_USER")
                .build();

        memberRepository.save(memberEntity);
    }
}
