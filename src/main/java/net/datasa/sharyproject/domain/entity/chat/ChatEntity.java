package net.datasa.sharyproject.domain.entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ChatEntity 클래스
 * 이 클래스는 데이터베이스의 'chat' 테이블과 매핑되는 엔티티(Entity)입니다.
 * 채팅방에 대한 정보를 데이터베이스에 저장하고 불러오기 위한 객체입니다.
 */
@Data  // Lombok 어노테이션으로 getter, setter, toString, equals, hashCode 메서드를 자동으로 생성
@NoArgsConstructor  // 기본 생성자를 자동으로 생성하는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 자동으로 생성하는 Lombok 어노테이션
@Builder  // 빌더 패턴을 사용하여 객체를 유연하게 생성할 수 있도록 하는 Lombok 어노테이션
@Entity  // JPA에서 이 클래스가 데이터베이스의 테이블과 매핑된다는 것을 나타내는 어노테이션
@Table(name = "chat")  // 'chat'이라는 이름의 테이블에 이 엔티티가 매핑됨을 나타냄
public class ChatEntity {

    @Id  // 이 필드가 테이블의 기본 키(primary key)임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 값을 자동으로 생성, 여기서는 자동 증가(AUTO_INCREMENT)를 사용
    @Column(name = "chat_id", nullable = false)  // 'chat_id'라는 이름으로 데이터베이스의 컬럼과 매핑, null을 허용하지 않음
    private int chatId;  // 채팅방의 고유 ID (기본 키)

    @Column(name = "participant1_id", nullable = false)  // 'participant1_id' 컬럼과 매핑, null을 허용하지 않음
    private String participant1Id;  // 첫 번째 참여자의 ID

    @Column(name = "participant2_id", nullable = false)  // 'participant2_id' 컬럼과 매핑, null을 허용하지 않음
    private String participant2Id;  // 두 번째 참여자의 ID

    @Column(name = "created_date", nullable = false)  // 'created_date' 컬럼과 매핑, null을 허용하지 않음
    private LocalDateTime createdDate;  // 채팅방이 생성된 날짜와 시간
}
