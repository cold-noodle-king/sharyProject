package net.datasa.sharyproject.domain.entity.share;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "share_like")
//BoardEntity와 ReplyEntity의 순환참조 문제로 toString() 호출시 오류일때 해당 필드를 제외
@ToString(exclude = "shareNote")
@EntityListeners(AuditingEntityListener.class)
public class ShareLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_num")
    private Integer likeNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_note_num", referencedColumnName = "share_note_num", nullable = false)
    private ShareNoteEntity shareNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "like_clicked", columnDefinition = "tinyint(1) default 0 check(like_clicked in(1, 0))")
    private Boolean likeClicked;

    @Column(name = "emotion_name")
    private String emotionName;
}
