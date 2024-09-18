package net.datasa.sharyproject.domain.entity.personal;

import jakarta.persistence.*;
import lombok.*;
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
    private Integer personalNoteNum; // 노트 번호 (Primary Key)

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
    private Integer likeCount; // 추천 수

    @Column(nullable = false)
    private Integer viewCount; // 조회 수

    @Lob
    @Column(name = "contents", columnDefinition = "TEXT")
    private String contents; // 내용

    // NoteTemplateEntity와 다대일 관계
    @ManyToOne
    @JoinColumn(name = "note_num", nullable = false)
    private NoteTemplateEntity noteTemplate;

    // EmotionEntity와 다대일 관계
    @ManyToOne
    @JoinColumn(name = "emotion_num", nullable = false)
    private EmotionEntity emotion;

    // PersonalDiaryEntity와 다대일 관계
    @ManyToOne
    @JoinColumn(name = "personal_diary_num", nullable = false)
    private PersonalDiaryEntity personalDiary;

    // ProfileEntity와 다대일 관계
    @ManyToOne
    @JoinColumn(name = "profile_num", nullable = false)
    private ProfileEntity profile;

    // MemberEntity와 다대일 관계
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    // GrantedEntity와 다대일 관계
    @ManyToOne
    @JoinColumn(name = "granted_num", nullable = false)
    private GrantedEntity granted;

    // HashtagEntity와 다대다 관계
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "personal_note_hashtag",
            joinColumns = @JoinColumn(name = "personal_note_num"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_num")
    )
    private List<HashtagEntity> hashtags;

    // 생성 및 수정 날짜 자동 처리
    @PrePersist
    protected void onCreate() {
        this.createdDate = new Timestamp(System.currentTimeMillis());
        this.updatedDate = this.createdDate;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = new Timestamp(System.currentTimeMillis());
    }
}
