package net.datasa.sharyproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionDTO {
    private int emotionNum;    // 감정 번호
    private String emotionName;  // 감정 이름
    private String memberId;   // 사용자 ID (외래키)
}
