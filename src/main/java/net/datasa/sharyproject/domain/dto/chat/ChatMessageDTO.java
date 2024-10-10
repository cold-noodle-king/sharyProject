package net.datasa.sharyproject.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ChatMessageDTO 클래스
 * 채팅 메시지에 대한 정보를 담고 있는 데이터 전송 객체(DTO)입니다.
 * 이 객체는 메시지 ID, 채팅방 ID, 메시지 내용, 전송 시간, 발신자 ID 등을 담고 있습니다.
 */
@Data  // Getter, Setter, toString, equals, hashCode 등의 메서드를 자동으로 생성해주는 Lombok 어노테이션
@NoArgsConstructor  // 기본 생성자를 자동으로 생성해주는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함하는 생성자를 자동으로 생성해주는 Lombok 어노테이션
public class ChatMessageDTO {
    // 메시지의 고유 ID (메시지를 식별하기 위한 값)
    private int messageId;

    // 메시지가 속한 채팅방의 ID (어느 채팅방에서 보내졌는지 나타냄)
    private int chatId;

    // 메시지 내용 (실제 사용자가 보낸 텍스트)
    private String messageContent;

    // 메시지가 전송된 날짜 및 시간
    private LocalDateTime createdDate;

    // 메시지를 보낸 사용자의 ID
    private String senderId;
}