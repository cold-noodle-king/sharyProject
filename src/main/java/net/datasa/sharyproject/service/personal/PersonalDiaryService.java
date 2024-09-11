package net.datasa.sharyproject.service.personal;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.personal.PersonalDiaryDTO;
import net.datasa.sharyproject.domain.entity.personal.CoverTemplateEntity;
import net.datasa.sharyproject.domain.entity.personal.PersonalDiaryEntity;
import net.datasa.sharyproject.repository.personal.CoverTemplateRepository;
import net.datasa.sharyproject.repository.personal.PersonalDiaryRepository;
import net.datasa.sharyproject.repository.member.MemberRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalDiaryService {

    private final PersonalDiaryRepository personalDiaryRepository; // 다이어리 저장소
    private final CoverTemplateRepository coverTemplateRepository; // 커버 템플릿 저장소
    private final MemberRepository memberRepository; // 회원 저장소

    /**
     * 다이어리 제목과 커버를 저장하는 메서드
     * @param diaryDTO 다이어리 정보가 포함된 DTO
     */
    public void saveDiary(PersonalDiaryDTO diaryDTO) {
        // 로그인된 사용자의 ID를 가져옴 (Spring Security 사용)
        String currentMemberId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 커버 템플릿 조회
        CoverTemplateEntity coverTemplate = coverTemplateRepository.findById(diaryDTO.getCoverNum())
                .orElseThrow(() -> new RuntimeException("커버 템플릿을 찾을 수 없습니다."));

        // 현재 로그인된 사용자 조회
        var member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 다이어리 엔티티 생성
        PersonalDiaryEntity personalDiary = PersonalDiaryEntity.builder()
                .diaryName(diaryDTO.getDiaryName())
                .coverTemplate(coverTemplate)
                .member(member)
                .build();

        // 다이어리 저장
        personalDiaryRepository.save(personalDiary);
    }
}
