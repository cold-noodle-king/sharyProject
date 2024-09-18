package net.datasa.sharyproject.domain.entity.personal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.EmotionEntity;
import net.datasa.sharyproject.domain.entity.HashtagEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "personal_note")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalNoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int personalNoteNum; // 노트 번호 (Primary Key)

    @Column(nullable = false)
    private String noteTitle; // 노트 제목

    @Column(nullable = false)
    private String weather; // 날씨

    private String originalName; // 원본 파일 이름

    private String fileName; // 저장된 파일 이름

    private String location; // 위치 정보

    @Column(nullable = false)
    private Timestamp diaryDate; // 작성 날짜

    @Column(nullable = false)
    private Timestamp createdDate; // 생성일

    @Column(nullable = false)
    private Timestamp updatedDate; // 수정일

    @Column(nullable = false)
    private int likeCount; // 추천 수

    @Column(nullable = false)
    private int viewCount; // 조회 수

    @Lob
    @Column(name = "contents", columnDefinition = "TEXT")
    private String contents; // 내용

    // 외래키로 연결된 NoteTemplate 테이블 참조
    @ManyToOne
    @JoinColumn(name = "note_num", nullable = false)
    private NoteTemplateEntity noteTemplate;

    // 외래키로 연결된 Emotion 테이블 참조
    @ManyToOne
    @JoinColumn(name = "emotion_num", nullable = false)
    private EmotionEntity emotion;

    // 외래키로 연결된 PersonalDiary 테이블 참조
    @ManyToOne
    @JoinColumn(name = "personal_diary_num", nullable = false)
    private PersonalDiaryEntity personalDiary;

    // 외래키로 연결된 Profile 테이블 참조
    @ManyToOne
    @JoinColumn(name = "profile_num", nullable = false)
    private ProfileEntity profile;

    // 외래키로 연결된 Member 테이블 참조
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    // 변경: 다대다 관계로 해시태그 설정
    @ManyToMany
    @JoinTable(
            name = "personal_note_hashtag", // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "personal_note_num"), // PersonalNoteEntity의 외래키
            inverseJoinColumns = @JoinColumn(name = "hashtag_num") // HashtagEntity의 외래키
    )
    private List<HashtagEntity> hashtags; // 여러 해시태그와 연관
}