package net.datasa.sharyproject.domain.entity.sse;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 수신자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "member_id", nullable = false)
    private MemberEntity receiver;

    // 알림 내용
    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    // 알림 생성 시간
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 알림이 읽혔는지 여부
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    //알림 타입
    @Column(name = "notification_type", nullable = false)
    private String notificationType; // 예: 'chat', 'follow', 'system' 등
}
