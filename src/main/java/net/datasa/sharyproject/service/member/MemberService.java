package net.datasa.sharyproject.service.member;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.member.MemberDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
                .memberPw(passwordEncoder.encode(memberDTO.getMemberPw()))
                .birthdate(memberDTO.getBirthdate())
                .fullName(memberDTO.getFullName())
                .nickname(memberDTO.getNickname())
                .gender(memberDTO.getGender())
                .email(memberDTO.getEmail())
                .phoneNumber(memberDTO.getPhoneNumber())
                .enabled(true)
                .roleName("ROLE_USER")
                .build();

        memberRepository.save(memberEntity);
    }

    public boolean findId(String searchId) {
        if(memberRepository.findById(searchId).isPresent()) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean findNick(String searchNick) {
        if(memberRepository.findByNickname(searchNick).isPresent()) {
            return false;
        }
        else {
            return true;
        }
    }

    public Optional<MemberEntity> findById(String memberId) {
        return memberRepository.findById(memberId);
   }
}
