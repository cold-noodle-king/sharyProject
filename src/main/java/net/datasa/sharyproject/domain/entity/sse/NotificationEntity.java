package net.datasa.sharyproject.domain.entity.sse;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;

import java.time.LocalDateTime;


/**
 * NotificationEntity 클래스
 * 이 클래스는 데이터베이스의 'notifications' 테이블과 매핑되는 엔티티(Entity)로,
 * 사용자가 받은 알림 정보를 저장하고 관리하는 데 사용됩니다.
 */
@Entity  // JPA에서 이 클래스가 데이터베이스 테이블과 매핑된다는 것을 나타냄
@Table(name = "notifications")  // 이 클래스는 'notifications' 테이블과 매핑됨을 나타냄
@Data  // Lombok 어노테이션으로 getter, setter, toString, equals, hashCode 메서드를 자동으로 생성
@NoArgsConstructor  // 기본 생성자를 자동으로 생성하는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 자동으로 생성하는 Lombok 어노테이션
@Builder  // 빌더 패턴을 사용하여 객체를 유연하게 생성할 수 있도록 지원하는 Lombok 어노테이션
public class NotificationEntity {

    @Id  // 이 필드가 테이블의 기본 키(primary key)임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 값이 자동으로 생성되도록 설정, 자동 증가(AUTO_INCREMENT) 방식 사용
    @Column(name = "id")  // 'id'라는 이름으로 테이블 컬럼과 매핑
    private Long id;  // 알림의 고유 ID

    // 수신자 정보
    @ManyToOne(fetch = FetchType.LAZY)  // MemberEntity와의 다대일 관계, 지연 로딩 설정
    @JoinColumn(name = "receiver_id", referencedColumnName = "member_id", nullable = false)  // 'receiver_id' 컬럼과 매핑, 외래 키(foreign key) 설정
    private MemberEntity receiver;  // 알림을 받은 사용자 정보

    // 알림 내용
    @Column(name = "content", nullable = false, length = 1000)  // 'content' 컬럼과 매핑, 최대 1000자까지 저장 가능, null 값을 허용하지 않음
    private String content;  // 알림의 내용

    // 알림 생성 시간
    @Column(name = "created_at", nullable = false)  // 'created_at' 컬럼과 매핑, null 값을 허용하지 않음
    private LocalDateTime createdAt;  // 알림이 생성된 시간

    // 알림이 읽혔는지 여부
    @Column(name = "is_read", nullable = false)  // 'is_read' 컬럼과 매핑, null 값을 허용하지 않음
    private boolean isRead = false;  // 알림이 읽혔는지 여부 (기본값: false)

    // 알림 타입
    @Column(name = "notification_type", nullable = false)  // 'notification_type' 컬럼과 매핑, null 값을 허용하지 않음
    private String notificationType;  // 알림의 종류 (예: 'chat', 'follow', 'system' 등)
}
