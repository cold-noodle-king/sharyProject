package net.datasa.sharyproject.domain.dto.sse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MessageDTO 클래스
 * 이 클래스는 메시지에 대한 정보를 담고 있는 데이터 전송 객체(DTO)입니다.
 * 주로 SSE(Server-Sent Events)를 통해 사용자에게 실시간 메시지를 전송할 때 사용됩니다.
 */
@Data  // Lombok이 제공하는 어노테이션으로 getter, setter, toString, equals, hashCode 메서드를 자동으로 생성
@NoArgsConstructor  // 기본 생성자를 자동으로 생성해주는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 자동으로 생성해주는 Lombok 어노테이션
@Builder  // 빌더 패턴을 사용하여 객체 생성 시 유연하게 필드를 설정할 수 있도록 해주는 Lombok 어노테이션
public class MessageDTO {
    // 메시지 발신자의 ID
    private String sender;

    // 메시지 내용
    private String content;

    // 메시지가 전송된 날짜와 시간
    private LocalDateTime createdAt;
}

