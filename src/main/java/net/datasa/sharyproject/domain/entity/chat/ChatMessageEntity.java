package net.datasa.sharyproject.domain.entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


/**
 * ChatMessageEntity 클래스
 * 이 클래스는 데이터베이스의 'chat_message' 테이블과 매핑되는 엔티티(Entity)입니다.
 * 이 엔티티는 채팅 메시지에 대한 정보를 저장하고 불러오기 위한 객체입니다.
 */
@Data  // Lombok 어노테이션으로 getter, setter, toString, equals, hashCode 메서드를 자동으로 생성
@NoArgsConstructor  // 기본 생성자를 자동으로 생성하는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 자동으로 생성하는 Lombok 어노테이션
@SuperBuilder  // 부모 클래스의 필드까지 빌더 패턴으로 생성 가능하도록 지원하는 Lombok 어노테이션
@Entity  // JPA에서 이 클래스가 데이터베이스 테이블과 매핑된다는 것을 나타냄
@Table(name = "chat_message")  // 이 클래스는 'chat_message' 테이블과 매핑됨을 나타냄
public class ChatMessageEntity {

    @Id  // 이 필드가 테이블의 기본 키(primary key)임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 값이 자동으로 생성되도록 설정, IDENTITY 전략을 사용하여 자동 증가(AUTO_INCREMENT)
    @Column(name = "message_id", nullable = false)  // 'message_id'라는 이름의 컬럼과 매핑되며, null 값을 허용하지 않음
    private int messageId;  // 메시지의 고유 ID

    @Column(name = "chat_id", nullable = false)  // 'chat_id' 컬럼과 매핑, null 값을 허용하지 않음
    private int chatId;  // 해당 메시지가 속한 채팅방의 ID

    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")  // 'message_content' 컬럼과 매핑, null 값을 허용하지 않음, 긴 텍스트 형식으로 저장
    private String messageContent;  // 메시지 내용

    @Column(name = "created_date", nullable = false)  // 'created_date' 컬럼과 매핑, null 값을 허용하지 않음
    private LocalDateTime createdDate;  // 메시지가 전송된 날짜와 시간

    @Column(name = "sender_id", nullable = false)  // 'sender_id' 컬럼과 매핑, null 값을 허용하지 않음
    private String senderId;  // 메시지를 보낸 사용자의 ID
}