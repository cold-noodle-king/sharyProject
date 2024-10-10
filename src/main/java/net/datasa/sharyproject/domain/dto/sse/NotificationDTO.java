package net.datasa.sharyproject.domain.dto.sse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * NotificationDTO 클래스
 * 이 클래스는 알림(Notification)에 대한 정보를 담고 있는 데이터 전송 객체(DTO)입니다.
 * 특정 사용자에게 전송된 알림의 정보를 저장합니다.
 */
@Data  // Lombok 어노테이션으로 getter, setter, toString, equals, hashCode 메서드를 자동으로 생성
@NoArgsConstructor  // 기본 생성자를 자동으로 생성해주는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 자동으로 생성해주는 Lombok 어노테이션
@Builder  // 빌더 패턴을 사용하여 객체 생성 시 유연하게 필드를 설정할 수 있도록 해주는 Lombok 어노테이션
public class NotificationDTO {
    // 알림의 고유 ID
    private Long id;

    // 알림을 받는 사용자의 ID
    private String receiverId;

    // 알림 내용
    private String content;

    // 알림이 생성된 날짜와 시간
    private LocalDateTime createdAt;

    // 알림을 읽었는지 여부 (true면 읽음, false면 읽지 않음)
    private boolean isRead;

    // 알림의 종류 (예: "follow", "message" 등 알림의 유형을 나타냄)
    private String notificationType;
}
