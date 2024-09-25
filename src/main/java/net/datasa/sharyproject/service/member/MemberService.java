package net.datasa.sharyproject.service.member;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.CategoryDTO;
import net.datasa.sharyproject.domain.dto.member.MemberDTO;
import net.datasa.sharyproject.domain.dto.member.UserCategoryDTO;
import net.datasa.sharyproject.domain.entity.CategoryEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.member.UserCategoryEntity;
import net.datasa.sharyproject.repository.CategoryRepository;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.member.UserCategoryRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 회원정보 관련 처리 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserCategoryRepository userCategoryRepository;


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

    public void selectedCategories(String memberId, List<String> selectedCategoryIds) {
        log.debug("이게 되냐구우우우ㅜ 저장하려는 memberId: {}", memberId);
        // 멤버 엔티티 가져오기
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("멤버를 찾을 수 없습니다: " + memberId));

        // 기존에 저장된 유저 카테고리 삭제
        userCategoryRepository.deleteByMember(memberEntity);

        // 선택된 각 카테고리 저장
        for (String categoryId : selectedCategoryIds) {
            log.debug("로그 다찍어어어어어ㅓ 저장하려는 categoryId: {}", categoryId);
            // 카테고리 엔티티 가져오기
            CategoryEntity categoryEntity = categoryRepository.findById(Integer.parseInt(categoryId))
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다: " + categoryId));

            // 중복된 카테고리 선택 방지 (선택된 카테고리가 이미 존재하는지 확인)
            if (userCategoryRepository.existsByMemberAndCategory(memberEntity, categoryEntity)) {
                log.debug("이미 선택된 카테고리입니다: {}", categoryEntity.getCategoryName());
                continue; // 이미 선택된 경우는 저장하지 않음
            }

            // UserCategoryEntity 객체 생성 및 저장
            UserCategoryEntity userCategoryEntity = UserCategoryEntity.builder()
                    .member(memberEntity)  // setMemberId 대신, 멤버 엔티티 자체를 넣음
                    .category(categoryEntity)  // setUserCategoryNum 대신, 카테고리 엔티티 자체를 넣음
                    .build();

            // 저장
            userCategoryRepository.save(userCategoryEntity);

        }
    }
}
