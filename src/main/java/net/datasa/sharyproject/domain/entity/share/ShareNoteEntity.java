package net.datasa.sharyproject.domain.entity.share;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.EmotionEntity;
import net.datasa.sharyproject.domain.entity.HashtagEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.domain.entity.personal.NoteTemplateEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "share_note")
@EntityListeners(AuditingEntityListener.class)
public class ShareNoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_note_num")
    private Integer shareNoteNum;

    @Column(name = "share_note_title", nullable = false, length = 100)
    private String shareNoteTitle;

    @Column(name = "weather", nullable = false, length = 100)
    private String weather;

    @Column(name = "original_name", length = 500)
    private String originalName;

    @Column(name = "file_name", length = 500)
    private String fileName;

    @Column(name = "location", length = 500)
    private String location;

    // NoteTemplateEntity와 다대일 관계
    @ManyToOne
    @JoinColumn(name = "note_num", nullable = false)
    private NoteTemplateEntity noteTemplate;

    // EmotionEntity와 다대일 관계
    @ManyToOne
    @JoinColumn(name = "emotion_num", nullable = false)
    private EmotionEntity emotion;

    // ProfileEntity와 다대일 관계
    @ManyToOne
    @JoinColumn(name = "profile_num", nullable = false)
    private ProfileEntity profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "contents", columnDefinition = "TEXT")
    private String contents;

    @Column(name = "diary_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp diaryDate;

    @CreatedDate
    @Column(name = "created_date", columnDefinition = "")
    private LocalDateTime createdDate;

    @Column(name = "like_count", columnDefinition = "INT DEFAULT 0")
    private Integer likeCount = 0;

    // ShareDiaryEntity와 다대일 관계
    @ManyToOne
    @JoinColumn(name = "share_diary_num", nullable = false)
    private ShareDiaryEntity shareDiary;

    // HashtagEntity와 다대다 관계
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "share_note_hashtag",
            joinColumns = @JoinColumn(name = "share_note_num"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_num")
    )
    private List<HashtagEntity> hashtags;

    @OneToMany(mappedBy = "shareNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyEntity> replyList;

    @OneToMany(mappedBy = "shareNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShareLikeEntity> likeList;
}
