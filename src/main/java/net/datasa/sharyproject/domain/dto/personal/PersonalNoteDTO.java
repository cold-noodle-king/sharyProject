package net.datasa.sharyproject.domain.dto.personal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalNoteDTO {
    private Integer personalNoteNum; // 노트 번호
    private String noteTitle; // 노트 제목
    private String weather; // 날씨
    private String contents; // 내용
    private Timestamp diaryDate; // 작성 날짜
    private Integer likeCount; // 추천 수
    private Integer viewCount; // 조회 수
    private List<Integer> hashtagNums; // 해시태그 번호 리스트
    private String emotionName; // 감정 이름
    private String grantedName; // 권한 이름
    private Integer emotionNum; // 감정 번호
    private Integer grantedNum; // 공개 권한 번호
    private String location; // 위치 정보
    private String fileName; // 이미지 파일 이름
    private Integer diaryNum; // 다이어리 번호
    private NoteTemplateDTO noteTemplate; // 노트 템플릿 정보 추가 (노트 이미지 및 기타 정보 포함)
    private String memberId; // 회원 ID
}
