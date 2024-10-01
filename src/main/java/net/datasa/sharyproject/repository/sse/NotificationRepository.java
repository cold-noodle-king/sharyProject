package net.datasa.sharyproject.repository.sse;

import jakarta.transaction.Transactional;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.sse.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByReceiver_MemberIdOrderByCreatedAtDesc(String receiverId);

    int countByReceiver_MemberIdAndIsReadFalse(String memberId);


    //@Transactional
    /*@Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.receiver.memberId = :memberId")
    void markAllAsReadByReceiverId(@Param("memberId") String memberId);
*/
    @Modifying(clearAutomatically = true)
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.receiver.memberId = :memberId")
    void markAllAsReadByReceiverId(@Param("memberId") String memberId);

}




