package net.datasa.sharyproject.domain.dto.share;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.share.ShareNoteEntity;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareLikeDTO {

    private Integer likeNum;

    @JsonIgnore
    private ShareNoteEntity shareNote;
    private MemberEntity member;
    private boolean likeClicked;
}
