package net.datasa.sharyproject.service.chat;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.chat.ChatDTO;
import net.datasa.sharyproject.domain.dto.chat.ChatMessageDTO;
import net.datasa.sharyproject.domain.entity.chat.ChatEntity;
import net.datasa.sharyproject.domain.entity.chat.ChatMessageEntity;
import net.datasa.sharyproject.repository.chat.ChatMessageRepository;
import net.datasa.sharyproject.repository.chat.ChatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 두 사용자가 포함된 채팅을 모두 가져오는 메서드
    public List<ChatDTO> getAllChatsForUser(String memberId) {
        List<ChatEntity> chats = chatRepository.findByParticipant1IdOrParticipant2Id(memberId, memberId);
        return chats.stream()
                .map(chat -> new ChatDTO(chat.getChatId(), chat.getParticipant1Id(), chat.getParticipant2Id(), chat.getCreatedDate()))
                .collect(Collectors.toList());
    }

    // 두 사용자가 포함된 특정 채팅을 찾거나 새로 만드는 메서드
    public ChatDTO findOrCreateChat(String member1Id, String member2Id) {
        // 두 사용자 ID를 사전적으로 정렬하여 순서를 고정
        String participant1Id = member1Id.compareTo(member2Id) < 0 ? member1Id : member2Id;
        String participant2Id = member1Id.compareTo(member2Id) < 0 ? member2Id : member1Id;

        Optional<ChatEntity> chatOpt = chatRepository.findFirstByParticipant1IdAndParticipant2IdOrParticipant2IdAndParticipant1Id(
                participant1Id, participant2Id, participant1Id, participant2Id);

        if (chatOpt.isPresent()) {
            return new ChatDTO(chatOpt.get().getChatId(), chatOpt.get().getParticipant1Id(), chatOpt.get().getParticipant2Id(), chatOpt.get().getCreatedDate());
        } else {
            ChatEntity chat = ChatEntity.builder()
                    .participant1Id(participant1Id)
                    .participant2Id(participant2Id)
                    .createdDate(LocalDateTime.now())
                    .build();
            chat = chatRepository.save(chat);
            return new ChatDTO(chat.getChatId(), chat.getParticipant1Id(), chat.getParticipant2Id(), chat.getCreatedDate());
        }
    }

    // 사용자의 모든 메시지 가져오기
    public List<ChatMessageDTO> getMessages(int chatId) {
        List<ChatMessageEntity> messages = chatMessageRepository.findByChatIdOrderByCreatedDateAsc(chatId);
        return messages.stream()
                .map(m -> new ChatMessageDTO(m.getMessageId(), m.getChatId(), m.getMessageContent(), m.getCreatedDate(), m.getSenderId()))
                .collect(Collectors.toList());
    }

    // 메시지 전송 메서드
    public void sendMessage(int chatId, String senderId, String content) {
        ChatMessageEntity message = ChatMessageEntity.builder()
                .chatId(chatId)
                .senderId(senderId)
                .messageContent(content)
                .createdDate(LocalDateTime.now())
                .build();
        chatMessageRepository.save(message);
    }

    // 특정 채팅 ID로 채팅을 가져오는 메서드
    public ChatDTO getChatById(int chatId) {
        Optional<ChatEntity> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isPresent()) {
            ChatEntity chat = chatOpt.get();
            return new ChatDTO(chat.getChatId(), chat.getParticipant1Id(), chat.getParticipant2Id(), chat.getCreatedDate());
        } else {
            log.error("Chat not found for ID: {}", chatId);
            throw new IllegalArgumentException("Chat not found for ID: " + chatId);
        }
    }
}
