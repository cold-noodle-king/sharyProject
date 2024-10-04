package net.datasa.sharyproject.repository.sse;

import net.datasa.sharyproject.domain.entity.sse.SseMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface SseMessageRepository extends JpaRepository<SseMessageEntity, Integer> {

    // 수신자가 해당 사용자인 메시지를 찾기 위한 쿼리
    List<SseMessageEntity> findByToMember_MemberId(String memberId);

    int countByToMember_MemberIdAndIsReadFalse(String memberId);

    /*@Modifying
    @Query("UPDATE SseMessageEntity m SET m.isRead = true WHERE m.toMember.memberId = :memberId")
    void markAllAsReadByToMemberId(@Param("memberId") String memberId);*/

    @Modifying(clearAutomatically = true)
    @Query("UPDATE SseMessageEntity m SET m.isRead = true WHERE m.toMember.memberId = :memberId")
    void markAllAsReadByToMemberId(@Param("memberId") String memberId);

}

