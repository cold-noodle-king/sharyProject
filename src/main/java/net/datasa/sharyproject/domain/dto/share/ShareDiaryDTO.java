package net.datasa.sharyproject.domain.dto.share;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareDiaryDTO {

    private Integer shareDiaryNum;
    private String shareDiaryName;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // 카테고리 관련 정보 (CategoryDTO를 이용하여 설정 가능)
    private Integer categoryNum; // CategoryEntity의 참조 ID
    private String categoryName; // 추가적으로 카테고리 이름 포함 가능 (선택 사항)

    // 커버 템플릿 관련 정보 (CoverTemplateDTO를 이용하여 설정 가능)
    private Integer coverTemplateNum; // CoverTemplateEntity의 참조 ID
    private String coverTemplateName; // 커버 템플릿 이름 (선택 사항)

    // 다이어리 주인 정보 (MemberDTO를 이용하여 설정 가능)
    private String memberId; // MemberEntity의 참조 ID
    private String nickname; // 추가적으로 회원 이름 포함 가능 (선택 사항)

    // 다이어리 소개글
    private String diaryBio;

    // 공유다이어리 멤버 수
    private int memberCount;

    // 공유다이어리 멤버
    private List<ShareMemberDTO> shareMemberList;
}
