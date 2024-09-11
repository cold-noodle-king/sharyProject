package net.datasa.sharyproject.domain.dto.personal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * PersonalDiary 엔티티를 위한 DTO(Data Transfer Object) 클래스
 * 클라이언트와 서버 간의 데이터 전송을 위한 클래스입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDiaryDTO {

    private Integer personalDiaryNum; // 다이어리 고유 번호
    private String diaryName; // 다이어리 이름
    private Timestamp createdDate; // 생성 날짜
    private Timestamp updatedDate; // 수정 날짜
    private Integer categoryNum; // 카테고리 번호 (외래 키)
    private Integer coverNum; // 커버 번호 (외래 키)
    private String memberId; // 회원 ID (외래 키)
}
