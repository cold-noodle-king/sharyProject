package net.datasa.sharyproject.domain.dto.sse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * UnifiedNotificationDTO 클래스
 * 이 클래스는 메시지와 알림(Notification)을 통합하여 처리하는 데이터 전송 객체(DTO)입니다.
 * 알림과 메시지 모두를 한 객체로 관리하기 위한 용도로 사용됩니다.
 */
@Data  // Lombok 어노테이션으로 getter, setter, toString, equals, hashCode 메서드를 자동으로 생성
@NoArgsConstructor  // 기본 생성자를 자동으로 생성해주는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 자동으로 생성해주는 Lombok 어노테이션
@Builder  // 빌더 패턴을 사용하여 객체 생성 시 유연하게 필드를 설정할 수 있도록 해주는 Lombok 어노테이션
public class UnifiedNotificationDTO {
    // 알림의 종류 ("message" 또는 "notification")
    private String type;

    // 발신자 (메시지의 경우 발신자의 ID)
    private String sender;

    // 알림 또는 메시지의 내용
    private String content;

    // 생성된 날짜와 시간
    private LocalDateTime createdAt;

    // 알림의 타입 (예: "follow", "message" 등, 알림의 종류를 나타냄)
    private String notificationType;
}