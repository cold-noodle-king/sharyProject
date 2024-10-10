package net.datasa.sharyproject.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ChatDTO 클래스
 * 채팅방에 대한 정보를 담고 있는 데이터 전송 객체(DTO)입니다.
 * 이 객체는 채팅방의 ID, 참여자 정보, 생성일 등을 담고 있습니다.
 */
@Data  // Getter, Setter, toString, equals, hashCode 등의 메서드를 자동으로 생성해주는 Lombok 어노테이션
@NoArgsConstructor  // 기본 생성자를 자동으로 생성해주는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함하는 생성자를 자동으로 생성해주는 Lombok 어노테이션
public class ChatDTO {
    // 채팅방의 ID (고유 식별자)
    private int chatId;
    // 첫 번째 참여자의 ID (사용자 ID)
    private String participant1Id;
    // 두 번째 참여자의 ID (사용자 ID)
    private String participant2Id;
    // 채팅방이 생성된 날짜 및 시간
    private LocalDateTime createdDate;
}