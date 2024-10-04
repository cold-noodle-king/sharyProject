package net.datasa.sharyproject.domain.dto.share;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareNoteLikeResponseDTO {

    private ShareNoteDTO shareNote; // 노트 정보
    private LikeResponseDTO likeResponse; // 좋아요 정보
}
