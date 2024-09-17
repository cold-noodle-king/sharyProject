package net.datasa.sharyproject.domain.dto.personal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalNoteDTO {
    private int personalNoteNum; // 노트 번호

    private String noteTitle; // 노트 제목

    private String weather; // 날씨

    private String contents; // 내용

    private Timestamp diaryDate; // 작성 날짜

    private int likeCount; // 추천 수

    private int viewCount; // 조회 수

    // 외래키로 연결된 엔티티 정보를 DTO로 참조할 수 있음
    private String emotionName; // 감정 이름
    private String hashtagName; // 해시태그 이름
    private String grantedName; // 권한 이름
}

