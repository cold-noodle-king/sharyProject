package net.datasa.sharyproject.domain.dto.personal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantedDTO {
    private Integer grantedNum;    // 권한 번호
    private String grantedName;  // 권한 이름 (예: 읽기, 쓰기 등)
}
