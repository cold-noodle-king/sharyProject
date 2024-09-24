package net.datasa.sharyproject.domain.dto.share;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareNoteDTO {
    private Integer shareNoteNum; // 노트 번호
    private String shareNoteTitle; // 노트 제목
    private String weather; // 날씨
    private String contents; // 내용
    private Timestamp diaryDate; // 작성 날짜
    private Integer likeCount; // 추천 수
    private List<Integer> hashtagNums; // 해시태그 번호 리스트
    private String emotionName; // 감정 이름
    private Integer emotionNum; // 감정 번호
    private String location; // 위치 정보
    private String fileName; // 이미지 파일 이름
    private Integer shareDiaryNum; // 다이어리 번호
    private NoteTemplateDTO noteTemplate; // 노트 템플릿 정보 추가 (노트 이미지 및 기타 정보 포함)
    private String memberId; // 회원 ID
    private List<String> hashtags; // 해시태그 이름 리스트 추가
    private ProfileDTO profile; // Profile 정보를 위한 필드 추가
    private String profilePicture; // 프로필 이미지 경로
    private List<ReplyDTO> replyList;

}
