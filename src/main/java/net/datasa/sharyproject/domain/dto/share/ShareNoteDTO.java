package net.datasa.sharyproject.domain.dto.share;

import java.time.LocalDateTime;

public class ShareNoteDTO {

    private Integer shareNoteNum;    // 공유 노트 번호
    private String shareNoteTitle;   // 공유 노트 제목
    private String weather;          // 날씨 정보
    private String originalName;     // 원본 파일 이름
    private String fileName;         // 파일 이름
    private String location;         // 위치 정보
    private Integer noteNum;         // 노트 번호
    private Integer emotionNum;      // 감정 번호
    private Integer profileNum;      // 프로필 번호
    private String memberId;         // 작성자 ID
    private String contents;         // 노트 내용
    private LocalDateTime diaryDate; // 다이어리 작성 날짜
    private LocalDateTime createdDate; // 생성 날짜
    private LocalDateTime updatedDate; // 수정 날짜
    private Integer hashtagNum;      // 해시태그 번호
    private Integer likeCount;       // 좋아요 수
    private Integer shareDiaryNum;   // 공유 다이어리 번호

}
