package net.datasa.sharyproject.domain.entity.sse;

import jakarta.persistence.*;
import lombok.*;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;

import java.time.LocalDateTime;

/**
 * SseMessageEntity 클래스
 * 이 클래스는 데이터베이스의 'sse_message' 테이블과 매핑되는 엔티티로,
 * SSE(Server-Sent Events) 메시지를 관리하는 데 사용됩니다.
 */
@Entity  // JPA에서 이 클래스가 데이터베이스 테이블과 매핑된다는 것을 나타냄
@Table(name = "sse_message")  // 이 클래스는 'sse_message' 테이블과 매핑됨을 나타냄
@Data  // Lombok 어노테이션으로 getter, setter, toString, equals, hashCode 메서드를 자동으로 생성
@NoArgsConstructor  // 기본 생성자를 자동으로 생성하는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 자동으로 생성하는 Lombok 어노테이션
@Builder  // 빌더 패턴을 사용하여 객체를 유연하게 생성할 수 있도록 지원하는 Lombok 어노테이션
public class SseMessageEntity {

    @Id  // 이 필드가 테이블의 기본 키(primary key)임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 값이 자동으로 생성되도록 설정, 자동 증가(AUTO_INCREMENT) 방식 사용
    @Column(name = "message_id")  // 'message_id' 컬럼과 매핑
    private Integer messageId;  // 메시지의 고유 ID

    // 메시지 발신자
    @ManyToOne(fetch = FetchType.LAZY)  // MemberEntity와의 다대일 관계, 지연 로딩 설정
    @JoinColumn(name = "from_id", referencedColumnName = "member_id", nullable = false)  // 'from_id' 컬럼과 매핑, 외래 키(foreign key) 설정
    private MemberEntity fromMember;  // 메시지를 보낸 사용자 정보

    // 메시지 수신자
    @ManyToOne(fetch = FetchType.LAZY)  // MemberEntity와의 다대일 관계, 지연 로딩 설정
    @JoinColumn(name = "to_id", referencedColumnName = "member_id", nullable = false)  // 'to_id' 컬럼과 매핑, 외래 키(foreign key) 설정
    private MemberEntity toMember;  // 메시지를 받은 사용자 정보

    @Column(name = "content", nullable = false, length = 1000)  // 'content' 컬럼과 매핑, 최대 1000자까지 저장 가능, null 값을 허용하지 않음
    private String content;  // 메시지 내용

    @Column(name = "create_date", nullable = false)  // 'create_date' 컬럼과 매핑, null 값을 허용하지 않음
    private LocalDateTime createDate;  // 메시지가 생성된 시간

    // 메시지가 읽혔는지 여부
    @Column(name = "is_read", nullable = false)  // 'is_read' 컬럼과 매핑, null 값을 허용하지 않음
    private boolean isRead = false;  // 메시지가 읽혔는지 여부 (기본값: false)
}
