package net.datasa.sharyproject.domain.dto.share;

// 윤조가 새로 생성
// 이유: 가입 요청 현황 페이지에서 여러 명의 가입 요청이 있을 때 각각의 요청에 대해 '수락' 버튼을 클릭하면 첫 번째 요청은 정상적으로 리스트에서 사라지는데 두번째 요청부터는 리스트에 잔존함 이를 해결하기 위해

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptRequestDTO {
    private Integer diaryNum;
    private String memberId;
}
