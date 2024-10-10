package net.datasa.sharyproject.service.chat;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.chat.ChatDTO;
import net.datasa.sharyproject.domain.dto.chat.ChatMessageDTO;
import net.datasa.sharyproject.domain.entity.chat.ChatEntity;
import net.datasa.sharyproject.domain.entity.chat.ChatMessageEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.sse.NotificationEntity;
import net.datasa.sharyproject.repository.chat.ChatMessageRepository;
import net.datasa.sharyproject.repository.chat.ChatRepository;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.sse.NotificationRepository;
import net.datasa.sharyproject.service.sse.SseService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ChatService 클래스
 * 이 클래스는 채팅 기능을 처리하는 서비스입니다.
 * 채팅방을 만들거나 메시지를 전송하고, 실시간 알림(SSE)을 처리하는 등의 기능을 담당합니다.
 */
@Service  // 이 클래스가 서비스 계층의 역할을 한다고 명시
@RequiredArgsConstructor  // Lombok 어노테이션: 모든 의존성을 생성자 주입 방식으로 자동 생성
@Transactional  // 메서드 내 모든 작업이 성공해야만 데이터베이스에 반영되도록 설정
@Slf4j  // 로그를 기록할 수 있는 Lombok 어노테이션j
public class ChatService {

    // 기존 의존성
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SseService sseService; // SseService 주입

    // 추가 의존성
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;


    /**
     * 두 사용자가 포함된 채팅방을 찾거나, 없으면 새로 생성하는 메서드
     * @param member1Id 첫 번째 사용자의 ID
     * @param member2Id 두 번째 사용자의 ID
     * @return 생성되거나 존재하는 채팅방의 정보 (ChatDTO 객체)
     */
    public ChatDTO findOrCreateChat(String member1Id, String member2Id) {
        // 두 사용자 ID를 사전적으로 정렬하여 순서를 고정
        String participant1Id = member1Id.compareTo(member2Id) < 0 ? member1Id : member2Id;
        String participant2Id = member1Id.compareTo(member2Id) < 0 ? member2Id : member1Id;

        // 두 사용자 사이의 기존 채팅방이 있는지 확인
        Optional<ChatEntity> chatOpt = chatRepository.findFirstByParticipant1IdAndParticipant2IdOrParticipant2IdAndParticipant1Id(
                participant1Id, participant2Id, participant1Id, participant2Id);

        // 채팅방이 존재하면 해당 채팅방 정보를 반환
        if (chatOpt.isPresent()) {
            return new ChatDTO(chatOpt.get().getChatId(), chatOpt.get().getParticipant1Id(), chatOpt.get().getParticipant2Id(), chatOpt.get().getCreatedDate());
        } else {
            // 채팅방이 없을 경우 새로 생성
            ChatEntity chat = ChatEntity.builder()
                    .participant1Id(participant1Id)
                    .participant2Id(participant2Id)
                    .createdDate(LocalDateTime.now())
                    .build();
            chat = chatRepository.save(chat);
            return new ChatDTO(chat.getChatId(), chat.getParticipant1Id(), chat.getParticipant2Id(), chat.getCreatedDate());
        }
    }


    /**
     * 메시지를 전송하는 메서드
     * @param chatId 메시지를 전송할 채팅방 ID
     * @param senderId 메시지를 보낸 사용자의 ID
     * @param content 메시지 내용
     * @return 전송된 메시지 정보 (ChatMessageDTO 객체)
     */
    public ChatMessageDTO sendMessage(int chatId, String senderId, String content) {
        // 메시지 저장
        ChatMessageEntity message = ChatMessageEntity.builder()
                .chatId(chatId)
                .senderId(senderId)
                .messageContent(content)
                .createdDate(LocalDateTime.now())
                .build();
        chatMessageRepository.save(message);

        // 수신자 ID 찾기 (채팅방에서 상대방의 ID를 가져옴)
        ChatDTO chat = getChatById(chatId);
        String recipientId = chat.getParticipant1Id().equals(senderId) ? chat.getParticipant2Id() : chat.getParticipant1Id();

        // 메시지 DTO 생성 (전송한 메시지를 DTO로 변환)
        ChatMessageDTO messageDTO = new ChatMessageDTO(
                message.getMessageId(),
                message.getChatId(),
                message.getMessageContent(),
                message.getCreatedDate(),
                message.getSenderId()
        );


        // 수신자와 발신자 모두에게 SSE 이벤트 전송
        sseService.sendChatMessage(recipientId, senderId, messageDTO);
        sseService.sendChatMessage(senderId, senderId, messageDTO);

        // 수신자에게 채팅 메시지 도착 알림 생성
        createChatNotification(senderId, recipientId, content);

        // 메시지 DTO 반환
        return messageDTO;
    }

    /**
     * 특정 채팅 ID로 채팅방 정보를 가져오는 메서드
     * @param chatId 채팅방 ID
     * @return 채팅방 정보 (ChatDTO 객체)
     */
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

    /**
     * 채팅 알림을 생성하는 메서드
     * 수신자에게 알림을 생성하고, 실시간으로 알림을 전송
     * @param senderId 메시지를 보낸 사용자의 ID
     * @param recipientId 메시지를 받은 사용자의 ID
     * @param content 메시지 내용
     */
    private void createChatNotification(String senderId, String recipientId, String content) {
        // 수신자 MemberEntity 가져오기
        MemberEntity recipient = memberRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("수신자 회원을 찾을 수 없습니다: " + recipientId));

        // 알림 메시지 생성
        String notificationContent = senderId + "님으로부터 새로운 채팅 메시지가 도착했습니다: " + content;

        // NotificationEntity 생성 및 저장
        NotificationEntity notification = NotificationEntity.builder()
                .receiver(recipient)
                .content(notificationContent)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .notificationType("chat") // 알림 타입 설정
                .build();
        notificationRepository.save(notification);

        // SSE를 통해 알림 전송
        sseService.sendNotification(recipientId, notificationContent, "chat");
    }

    /**
     * 특정 채팅방에 속한 모든 메시지를 가져오는 메서드
     * @param chatId 채팅방 ID
     * @return 해당 채팅방의 메시지 목록 (ChatMessageDTO 리스트)
     */
    public List<ChatMessageDTO> getMessages(int chatId) {
        List<ChatMessageEntity> messages = chatMessageRepository.findByChatIdOrderByCreatedDateAsc(chatId);
        return messages.stream()
                .map(m -> new ChatMessageDTO(m.getMessageId(), m.getChatId(), m.getMessageContent(), m.getCreatedDate(), m.getSenderId()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 포함된 모든 채팅방을 가져오는 메서드
     * @param memberId 사용자의 ID
     * @return 사용자가 포함된 모든 채팅방 목록 (ChatDTO 리스트)
     */
    public List<ChatDTO> getAllChatsForUser(String memberId) {
        List<ChatEntity> chats = chatRepository.findByParticipant1IdOrParticipant2Id(memberId, memberId);
        return chats.stream()
                .map(chat -> new ChatDTO(chat.getChatId(), chat.getParticipant1Id(), chat.getParticipant2Id(), chat.getCreatedDate()))
                .collect(Collectors.toList());
    }
}
