package net.datasa.sharyproject.domain.dto.share;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDTO {

        private Integer replyNum;
        private String contents;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
        private Integer shareNoteNum; // ShareNoteEntity의 ID
        private String memberId; // MemberEntity의 ID
        private String nickname;

}
