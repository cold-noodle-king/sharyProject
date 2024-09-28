package net.datasa.sharyproject.domain.dto.share;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponseDTO {
    // cnt 랑 추천했는지 여부 (true/false) 를 담을 변수
    private Integer cnt;
    private boolean isLiked;
    private String emotionName;
    private Integer joyCnt;
    private Integer loveCnt;
    private Integer sadCnt;
    private Integer angryCnt;
    private Integer wowCnt;
}
