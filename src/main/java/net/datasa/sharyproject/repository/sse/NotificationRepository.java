package net.datasa.sharyproject.repository.sse;


import jakarta.transaction.Transactional;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.sse.NotificationEntity;
import net.datasa.sharyproject.security.AuthenticatedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * NotificationRepository 인터페이스
 * 이 인터페이스는 데이터베이스에서 알림(NotificationEntity) 데이터를 관리하기 위해 사용됩니다.
 * JpaRepository를 상속받아 기본적인 CRUD(Create, Read, Update, Delete) 기능을 자동으로 제공받습니다.
 * 알림의 읽기 상태를 업데이트하거나, 특정 사용자의 알림을 조회하는 커스텀 메서드가 정의되어 있습니다.
 */
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    /**
     * 주어진 사용자가 받은 알림을 최신 순서대로 조회하는 메서드
     * @param receiverId 알림을 받은 사용자의 ID
     * @return 해당 사용자가 받은 알림 목록 (최신 순으로 정렬)
     */
    List<NotificationEntity> findByReceiver_MemberIdOrderByCreatedAtDesc(String receiverId);

    /**
     * 주어진 사용자가 읽지 않은 알림의 개수를 반환하는 메서드
     * @param memberId 사용자의 ID
     * @return 읽지 않은 알림의 개수
     */
    int countByReceiver_MemberIdAndIsReadFalse(String memberId);

    /**
     * 특정 사용자의 모든 알림을 읽음 처리하는 메서드
     * @param memberId 알림을 읽음 처리할 사용자의 ID
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.receiver.memberId = :memberId")
    void markAllAsReadByReceiverId(@Param("memberId") String memberId);

    /**
     * 주어진 사용자가 읽은 알림의 개수를 반환하는 메서드
     * @param memberId 사용자의 ID
     * @return 읽은 알림의 개수
     */
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.receiver.memberId = :memberId AND n.isRead = true")
    int countByReceiver_MemberIdAndIsReadTrue(@Param("memberId") String memberId); // 읽은 알림 수 쿼리 추가
}


