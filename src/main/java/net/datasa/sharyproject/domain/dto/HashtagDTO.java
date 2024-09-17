package net.datasa.sharyproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashtagDTO {
    private int hashtagNum;    // 해시태그 번호
    private String hashtagName;  // 해시태그 이름
    private int categoryNum;    // 카테고리 번호 (외래키)
}
