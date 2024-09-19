package net.datasa.sharyproject.service.member;

import jakarta.persistence.EntityNotFoundException;
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


    /**
     * 개인정보 조회
     * @param username 회원아이디
     * @return 개인정보 DTO객체
     */
    public MemberDTO getMember(String username) {
        MemberEntity entity = memberRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException(username + ":"));
        MemberDTO memberDTO = MemberDTO.builder()
                .memberId(entity.getMemberId())
                .memberPw(entity.getMemberPw())
                .fullName(entity.getFullName())
                .nickname(entity.getNickname())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .build();
        return memberDTO;
    }

    public void infoUpdate(MemberDTO memberDTO) {
        //MemberEntity DB와 연동되어 있는 객체, entity에는 테이블 내용이 그대로 들어있음
        MemberEntity memberEntity = memberRepository.findById(memberDTO.getMemberId()) //프라이머리키 기준으로 작업
                .orElseThrow(() -> new EntityNotFoundException(memberDTO + ":"));

        //엔티티 값을 건드리지 않으면 그냥 유지함
        //memberDTO의 비밀번호가 비어있지 않으면 비번도 수정
        if (!memberDTO.getMemberPw().isEmpty()) {
            memberEntity.setMemberPw(passwordEncoder.encode(memberDTO.getMemberPw()));
        }
        //나머지 이름, 이메일, 전화, 주소는 무조건 대입
        //바꿀 부분만 써주면 됨. 바꾸지 않을 값들은 알아서 채워짐
        memberEntity.setFullName(memberDTO.getFullName());
        memberEntity.setEmail(memberDTO.getEmail());
        memberEntity.setPhoneNumber(memberDTO.getPhoneNumber());
        memberEntity.setNickname(memberDTO.getNickname());

    }
}
