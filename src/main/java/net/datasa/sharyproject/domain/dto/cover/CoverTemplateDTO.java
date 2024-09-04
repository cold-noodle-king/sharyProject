package net.datasa.sharyproject.domain.dto.cover;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoverTemplateDTO {
    private Integer coverNum;
    private String coverName;
    private String coverImage;
}
