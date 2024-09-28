package net.datasa.sharyproject.domain.dto.share;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareLikeDTO {

    private Integer likeNum;

    @JsonIgnore
    private Integer shareNoteNum;
    private String memberId;
    private boolean likeClicked;
    private String emotionName;
}
