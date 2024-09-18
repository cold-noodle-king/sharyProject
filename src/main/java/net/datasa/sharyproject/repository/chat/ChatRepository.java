package net.datasa.sharyproject.repository.chat;

import net.datasa.sharyproject.domain.entity.chat.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<ChatEntity, Integer> {
    // 두 사용자가 참가한 채팅을 양방향으로 검색하도록 추가 (단일 결과만 반환하도록 수정)
    Optional<ChatEntity> findFirstByParticipant1IdAndParticipant2IdOrParticipant2IdAndParticipant1Id(
            String participant1Id, String participant2Id,
            String participant2IdInverse, String participant1IdInverse);

    List<ChatEntity> findByParticipant1IdOrParticipant2Id(String participant1Id, String participant2Id);
}
