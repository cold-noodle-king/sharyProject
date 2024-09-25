package net.datasa.sharyproject.repository.sse;

import net.datasa.sharyproject.domain.entity.sse.SseMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SseMessageRepository extends JpaRepository<SseMessageEntity, Integer> {

    // 수신자가 해당 사용자인 메시지를 찾기 위한 쿼리
    List<SseMessageEntity> findByToMember_MemberId(String memberId);



}

