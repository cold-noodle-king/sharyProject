package net.datasa.sharyproject.repository.sse;

import net.datasa.sharyproject.domain.entity.sse.SseMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

/**
 * SseMessageRepository 인터페이스
 * 이 인터페이스는 데이터베이스에서 SSE 메시지(SseMessageEntity) 데이터를 관리하기 위해 사용됩니다.
 * JpaRepository를 상속받아 기본적인 CRUD(Create, Read, Update, Delete) 기능을 자동으로 제공받습니다.
 * 특정 사용자에게 수신된 메시지를 조회하거나, 읽음 상태를 업데이트하는 커스텀 메서드가 정의되어 있습니다.
 */
public interface SseMessageRepository extends JpaRepository<SseMessageEntity, Integer> {

    /**
     * 특정 사용자가 수신한 모든 메시지를 조회하는 메서드
     * @param memberId 수신자의 ID
     * @return 해당 사용자가 받은 메시지 목록 (SseMessageEntity 리스트로 반환)
     */
    List<SseMessageEntity> findByToMember_MemberId(String memberId);

    /**
     * 특정 사용자가 읽지 않은 메시지의 개수를 반환하는 메서드
     * @param memberId 수신자의 ID
     * @return 읽지 않은 메시지의 개수
     */
    int countByToMember_MemberIdAndIsReadFalse(String memberId);

    /*@Modifying
    @Query("UPDATE SseMessageEntity m SET m.isRead = true WHERE m.toMember.memberId = :memberId")
    void markAllAsReadByToMemberId(@Param("memberId") String memberId);*/

    /**
     * 특정 사용자가 받은 모든 메시지를 읽음 상태로 업데이트하는 메서드
     * @param memberId 수신자의 ID
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE SseMessageEntity m SET m.isRead = true WHERE m.toMember.memberId = :memberId")
    void markAllAsReadByToMemberId(@Param("memberId") String memberId);

}

