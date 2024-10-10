package net.datasa.sharyproject.repository.chat;

import net.datasa.sharyproject.domain.entity.chat.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * ChatMessageRepository 인터페이스
 * 이 인터페이스는 데이터베이스에서 채팅 메시지 데이터를 관리하기 위해 사용됩니다.
 * JpaRepository를 상속받아 기본적인 CRUD(Create, Read, Update, Delete) 기능을 자동으로 제공받습니다.
 * 또한, 채팅방 ID를 기준으로 메시지를 시간 순서대로 조회하는 커스텀 메서드가 정의되어 있습니다.
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Integer> {

    /**
     * 주어진 채팅방 ID(chatId)를 기준으로 해당 채팅방의 모든 메시지를 시간 순서대로 정렬하여 반환하는 메서드
     * @param chatId 채팅방의 ID
     * @return 해당 채팅방의 메시지 목록 (시간순 정렬)
     */
    List<ChatMessageEntity> findByChatIdOrderByCreatedDateAsc(int chatId);
}