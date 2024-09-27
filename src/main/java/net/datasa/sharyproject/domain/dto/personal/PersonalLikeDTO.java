package net.datasa.sharyproject.domain.dto.personal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalLikeDTO {
    private Integer personalLikeNum; // 수정된 필드 이름
    private Integer personalNoteNum;
    private String memberId;
    private boolean likeClicked;
    private int likeCount;
}
