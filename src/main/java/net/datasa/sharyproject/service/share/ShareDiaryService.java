package net.datasa.sharyproject.service.share;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.share.ShareDiaryDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.CategoryEntity;
import net.datasa.sharyproject.domain.entity.personal.CoverTemplateEntity;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.CategoryRepository;
import net.datasa.sharyproject.repository.personal.CoverTemplateRepository;
import net.datasa.sharyproject.repository.share.ShareDiaryRepository;
import net.datasa.sharyproject.security.AuthenticatedUser;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ShareDiaryService {

    private final ShareDiaryRepository shareDiaryRepository;
    private final MemberRepository memberRepository;
    private final CoverTemplateRepository coverTemplateRepository;
    private final CategoryRepository categoryRepository;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;

    /**
     * 생성한 다이어리를 저장하는 메서드
     * @param shareDiaryDTO 생성한 다이어리 정보를 담은 DTO
     * @param user 다이어리를 생성한 유저 정보
     * @return
     */
    public ShareDiaryEntity saveDiary(ShareDiaryDTO shareDiaryDTO, AuthenticatedUser user){
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

        return shareDiaryEntity;
    }

    /**
     * 사용자가 생성한 다이어리 리스트를 가져오는 메서드
     * @param memberId 현재 로그인한 사용자의 ID
     * @return
     */
    public List<ShareDiaryDTO> getCreatedList(String memberId){

        List<ShareDiaryEntity> shareDiaryEntities = shareDiaryRepository.findByMember_MemberId(memberId);

        // 조회한 엔티티를 DTO로 변환하여 반환
        return shareDiaryEntities.stream()
                .map(this::convertEntityToDTO) // 엔티티를 DTO로 변환하는 메서드 호출
                .collect(Collectors.toList());
    }

    public ShareDiaryDTO getDiary(Integer diaryNum, String memberId){
        ShareDiaryEntity shareDiaryEntity = shareDiaryRepository.findById(diaryNum)
                .orElseThrow(() -> new EntityNotFoundException("다이어리 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));

        return convertEntityToDTO(shareDiaryEntity);

    }

    public void updateBio(Integer diaryNum, String diaryBio, String memberId){
        ShareDiaryEntity shareDiaryEntity = shareDiaryRepository.findById(diaryNum)
                .orElseThrow(() -> new EntityNotFoundException("다이어리 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));

        shareDiaryEntity.setDiaryBio(diaryBio);

    }

    private ShareDiaryDTO convertEntityToDTO(ShareDiaryEntity diary) {
        return ShareDiaryDTO.builder()
                .shareDiaryNum(diary.getShareDiaryNum())   // 다이어리 고유 번호
                .shareDiaryName(diary.getShareDiaryName())                 // 다이어리 제목
                .createdDate(diary.getCreatedDate())             // 다이어리 생성 날짜
                .updatedDate(diary.getUpdatedDate())             // 다이어리 수정 날짜
                .categoryNum(diary.getCategory().getCategoryNum()) // 카테고리 번호
                .categoryName(diary.getCategory().getCategoryName()) // 카테고리 이름
                .coverTemplateNum(diary.getCoverTemplate().getCoverNum())  // 커버 번호
                .coverTemplateName(diary.getCoverTemplate().getCoverName()) // 커버 이름 (추가)
                .memberId(diary.getMember().getMemberId())        // 회원 ID
                .nickname(diary.getMember().getNickname())
                .diaryBio(diary.getDiaryBio())
                .build();
    }

}
