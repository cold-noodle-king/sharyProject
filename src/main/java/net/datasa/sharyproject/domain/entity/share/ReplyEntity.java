package net.datasa.sharyproject.domain.entity.share;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
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
@Table(name = "reply")
//BoardEntity와 ReplyEntity의 순환참조 문제로 toString() 호출시 오류일때 해당 필드를 제외
@ToString(exclude = "shareNote")
@EntityListeners(AuditingEntityListener.class)
public class ReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_num")
    private Integer replyNum;

    @Column(name = "contents", nullable = false, length = 2000)
    private String contents;

    @CreatedDate
    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_note_num", referencedColumnName = "share_note_num")
    private ShareNoteEntity shareNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

}
