package net.datasa.sharyproject.service.personal;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.personal.PersonalDiaryDTO;
import net.datasa.sharyproject.domain.entity.CategoryEntity;
import net.datasa.sharyproject.domain.entity.personal.CoverTemplateEntity;
import net.datasa.sharyproject.domain.entity.personal.PersonalDiaryEntity;
import net.datasa.sharyproject.repository.CategoryRepository;
import net.datasa.sharyproject.repository.personal.CoverTemplateRepository;
import net.datasa.sharyproject.repository.personal.NoteTemplateRepository;
import net.datasa.sharyproject.repository.personal.PersonalDiaryRepository;
import net.datasa.sharyproject.repository.member.MemberRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalDiaryService {

    private final PersonalDiaryRepository personalDiaryRepository; // 다이어리 저장소
    private final CoverTemplateRepository coverTemplateRepository; // 커버 템플릿 저장소
    private final MemberRepository memberRepository; // 회원 저장소
    private final CategoryRepository categoryRepository; // 카테고리 저장소 추가
    private final NoteTemplateRepository noteTemplateRepository; // 노트 템플릿 저장소 추가

    /**
     * 다이어리 제목, 카테고리, 커버를 저장하는 메서드
     * @param diaryDTO 다이어리 정보가 포함된 DTO
     */
    public void saveDiary(PersonalDiaryDTO diaryDTO) {
        // 로그인된 사용자의 ID를 가져옴 (Spring Security 사용)
        String currentMemberId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 커버 템플릿 조회
        CoverTemplateEntity coverTemplate = coverTemplateRepository.findById(diaryDTO.getCoverNum())
                .orElseThrow(() -> new RuntimeException("커버 템플릿을 찾을 수 없습니다."));

        // 카테고리 조회
        CategoryEntity category = categoryRepository.findById(diaryDTO.getCategoryNum())
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

        // 현재 로그인된 사용자 조회
        var member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 다이어리 엔티티 생성
        PersonalDiaryEntity personalDiary = PersonalDiaryEntity.builder()
                .diaryName(diaryDTO.getDiaryName())   // 다이어리 제목 설정
                .category(category)                   // 카테고리 설정
                .coverTemplate(coverTemplate)         // 커버 템플릿 설정
                .member(member)                       // 회원 정보 설정
                .build();

        // 다이어리 저장
        personalDiaryRepository.save(personalDiary);
    }

    /**
     * 현재 로그인한 사용자의 다이어리 목록을 조회하는 메서드
     * @return 로그인한 사용자의 다이어리 목록
     */
    public List<PersonalDiaryDTO> getDiariesByLoggedInMember() {
        // 로그인된 사용자의 ID를 가져옴
        String currentMemberId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 로그인된 사용자의 다이어리 목록을 조회
        List<PersonalDiaryEntity> diaryEntities = personalDiaryRepository.findByMember_MemberId(currentMemberId);

        // 조회한 엔티티를 DTO로 변환하여 반환
        return diaryEntities.stream()
                .map(this::convertEntityToDTO) // 엔티티를 DTO로 변환하는 메서드 호출
                .collect(Collectors.toList());
    }

    /**
     * PersonalDiaryEntity를 PersonalDiaryDTO로 변환하는 메서드
     * @param diary PersonalDiaryEntity
     * @return PersonalDiaryDTO
     */
    private PersonalDiaryDTO convertEntityToDTO(PersonalDiaryEntity diary) {
        return PersonalDiaryDTO.builder()
                .personalDiaryNum(diary.getPersonalDiaryNum())   // 다이어리 고유 번호
                .diaryName(diary.getDiaryName())                 // 다이어리 제목
                .createdDate(diary.getCreatedDate())             // 다이어리 생성 날짜
                .updatedDate(diary.getUpdatedDate())             // 다이어리 수정 날짜
                .categoryNum(diary.getCategory().getCategoryNum()) // 카테고리 번호
                .categoryName(diary.getCategory().getCategoryName()) // 카테고리 이름
                .coverNum(diary.getCoverTemplate().getCoverNum())  // 커버 번호
                .coverName(diary.getCoverTemplate().getCoverName()) // 커버 이름 (추가)
                .memberId(diary.getMember().getMemberId())        // 회원 ID
                .build();
    }


}