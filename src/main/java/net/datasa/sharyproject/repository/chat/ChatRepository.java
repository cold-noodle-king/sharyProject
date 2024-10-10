package net.datasa.sharyproject.repository.chat;

import net.datasa.sharyproject.domain.entity.chat.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * ChatRepository 인터페이스
 * 이 인터페이스는 데이터베이스에서 채팅방(ChatEntity) 데이터를 관리하기 위해 사용됩니다.
 * JpaRepository를 상속받아 기본적인 CRUD(Create, Read, Update, Delete) 기능을 자동으로 제공받습니다.
 * 두 사용자가 참여한 채팅방을 검색하거나, 특정 사용자가 참여한 모든 채팅방을 조회하는 커스텀 메서드가 정의되어 있습니다.
 */
public interface ChatRepository extends JpaRepository<ChatEntity, Integer> {

    /**
     * 두 사용자가 참가한 채팅방을 검색하는 메서드 (양방향 검색)
     * 두 사용자가 채팅방에 참가한 순서와 관계없이 동일한 채팅방을 검색합니다.
     *
     * @param participant1Id 첫 번째 사용자의 ID
     * @param participant2Id 두 번째 사용자의 ID
     * @param participant2IdInverse 두 번째 사용자의 ID (역순 검색용)
     * @param participant1IdInverse 첫 번째 사용자의 ID (역순 검색용)
     * @return 두 사용자가 참가한 채팅방을 Optional로 반환 (존재하지 않을 경우 빈 값)
     */
    Optional<ChatEntity> findFirstByParticipant1IdAndParticipant2IdOrParticipant2IdAndParticipant1Id(
            String participant1Id, String participant2Id,
            String participant2IdInverse, String participant1IdInverse);

    /**
     * 특정 사용자가 참여한 모든 채팅방을 조회하는 메서드
     *
     * @param participant1Id 첫 번째 사용자의 ID
     * @param participant2Id 두 번째 사용자의 ID
     * @return 주어진 사용자 중 하나라도 참여한 모든 채팅방을 리스트로 반환
     */
    List<ChatEntity> findByParticipant1IdOrParticipant2Id(String participant1Id, String participant2Id);
}
