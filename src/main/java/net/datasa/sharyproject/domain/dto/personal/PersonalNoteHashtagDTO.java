package net.datasa.sharyproject.domain.dto.personal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalNoteHashtagDTO {
    private int personalNoteNum; // 노트 번호
    private int hashtagNum;      // 해시태그 번호
}