package net.datasa.sharyproject.domain.dto.share;

import lombok.*;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"shareDiary", "manager"})
public class ShareMemberDTO {

    private Integer shareMemberNum;  // 공유 멤버 번호
    private String memberId;         // 회원 ID
    private String nickname;         // 회원의 닉네임
    private Integer shareDiaryNum;   // 공유 다이어리 번호
    private String managerId;        // 공유 다이어리 매니저 ID
    private String managerName;      // 매니저 닉네임
    private String status;           // 요청 상태
    private LocalDateTime joinDate;  // 가입일

}
