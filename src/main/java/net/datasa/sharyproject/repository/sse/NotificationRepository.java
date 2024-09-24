package net.datasa.sharyproject.repository.sse;

import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.sse.NotificationEntity;
import net.datasa.sharyproject.domain.entity.sse.SseMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByReceiver_MemberIdOrderByCreatedAtDesc(String receiverId);

    int countByReceiverAndIsReadFalse(MemberEntity receiver);
}



