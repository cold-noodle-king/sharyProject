package net.datasa.sharyproject.service.share;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.share.ShareDiaryDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.personal.CategoryEntity;
import net.datasa.sharyproject.domain.entity.personal.CoverTemplateEntity;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.personal.CategoryRepository;
import net.datasa.sharyproject.repository.personal.CoverTemplateRepository;
import net.datasa.sharyproject.repository.share.ShareDiaryRepository;
import net.datasa.sharyproject.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ShareDiaryService {

    private final ShareDiaryRepository shareDiaryRepository;
    private final MemberRepository memberRepository;
    private final CoverTemplateRepository coverTemplateRepository;
    private final CategoryRepository categoryRepository;

    public void saveDiary(ShareDiaryDTO shareDiaryDTO, AuthenticatedUser user){
        MemberEntity memberEntity = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        CoverTemplateEntity coverTemplateEntity = coverTemplateRepository.findById(shareDiaryDTO.getCoverTemplateNum())
                .orElseThrow(() -> new EntityNotFoundException("커버 템플릿을 불러올 수 없습니다."));

        switch (shareDiaryDTO.getCategoryName()){
            case "일상":
                shareDiaryDTO.setCategoryNum(1); break;
            case "여행":
                shareDiaryDTO.setCategoryNum(2); break;
            case "육아":
                shareDiaryDTO.setCategoryNum(3); break;
            case "연애":
                shareDiaryDTO.setCategoryNum(4); break;
            case "취미":
                shareDiaryDTO.setCategoryNum(5); break;
            case "운동":
                shareDiaryDTO.setCategoryNum(6); break;
            default:
                break;
        }
        CategoryEntity categoryEntity = categoryRepository.findById(shareDiaryDTO.getCategoryNum())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 불러올 수 없습니다."));

        ShareDiaryEntity shareDiaryEntity = ShareDiaryEntity.builder()
                .shareDiaryName(shareDiaryDTO.getShareDiaryName())
                .member(memberEntity)
                .category(categoryEntity)
                .coverTemplate(coverTemplateEntity)
                .build();

        shareDiaryRepository.save(shareDiaryEntity);

        log.debug("저장되는 엔티티:{}", shareDiaryEntity);
    }

}
