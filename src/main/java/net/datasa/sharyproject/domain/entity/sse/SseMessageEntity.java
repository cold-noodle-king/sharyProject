package net.datasa.sharyproject.domain.entity.sse;

import jakarta.persistence.*;
import lombok.*;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "sse_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SseMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    // 발신자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id", referencedColumnName = "member_id", nullable = false)
    private MemberEntity fromMember;

    // 수신자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id", referencedColumnName = "member_id", nullable = false)
    private MemberEntity toMember;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;
}
