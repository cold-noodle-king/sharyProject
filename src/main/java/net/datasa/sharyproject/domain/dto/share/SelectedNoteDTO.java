package net.datasa.sharyproject.domain.dto.share;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectedNoteDTO {

    Integer shareDiaryNum;
    String shareNoteTitle;
    Integer noteNum;
}
