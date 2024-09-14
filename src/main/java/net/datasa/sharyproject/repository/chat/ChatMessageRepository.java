package net.datasa.sharyproject.repository.chat;

import net.datasa.sharyproject.domain.entity.chat.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Integer> {
    List<ChatMessageEntity> findByChatIdOrderByCreatedDateAsc(int chatId);
}