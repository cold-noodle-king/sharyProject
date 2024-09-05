package net.datasa.sharyproject.domain.entity.share;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Slf4j
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

    @Column(name = "note_num", nullable = false)
    private Integer noteNum;

    @Column(name = "emotion_num", nullable = false)
    private Integer emotionNum;

    @Column(name = "profile_num", nullable = false)
    private Integer profileNum;

    @Column(name = "member_id", nullable = false, length = 50)
    private String memberId;

    @Column(name = "contents", columnDefinition = "TEXT")
    private String contents;

    @Column(name = "diary_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime diaryDate;

    @CreatedDate
    @Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedDate;

    @Column(name = "hashtag_num", nullable = false)
    private Integer hashtagNum;

    @Column(name = "like_count", columnDefinition = "INT DEFAULT 0")
    private Integer likeCount;

    @Column(name = "share_diary_num", nullable = false)
    private Integer shareDiaryNum;
}
