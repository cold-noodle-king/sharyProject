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

/*public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByReceiver_MemberIdOrderByCreatedAtDesc(String receiverId);

    int countByReceiver_MemberIdAndIsReadFalse(String memberId);


    //@Transactional
    *//*@Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.receiver.memberId = :memberId")
    void markAllAsReadByReceiverId(@Param("memberId") String memberId);
*//*
    @Modifying(clearAutomatically = true)
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.receiver.memberId = :memberId")
    void markAllAsReadByReceiverId(@Param("memberId") String memberId);


    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.receiver.memberId = :memberId AND n.isRead = true")
    int countByReceiver_MemberIdAndIsReadTrue(@Param("memberId") String memberId); // 읽은 알림 수 쿼리 추가
}*/

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByReceiver_MemberIdOrderByCreatedAtDesc(String receiverId);

    int countByReceiver_MemberIdAndIsReadFalse(String memberId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.receiver.memberId = :memberId")
    void markAllAsReadByReceiverId(@Param("memberId") String memberId);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.receiver.memberId = :memberId AND n.isRead = true")
    int countByReceiver_MemberIdAndIsReadTrue(@Param("memberId") String memberId); // 읽은 알림 수 쿼리 추가
}


