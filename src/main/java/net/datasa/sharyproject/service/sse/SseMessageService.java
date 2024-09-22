package net.datasa.sharyproject.service.sse;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.sse.MessageDTO;
import net.datasa.sharyproject.domain.dto.sse.NotificationDTO;
import net.datasa.sharyproject.domain.entity.sse.NotificationEntity;
import net.datasa.sharyproject.domain.entity.sse.SseMessageEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.sse.NotificationRepository;
import net.datasa.sharyproject.repository.sse.SseMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SseMessageService {

    private final MemberRepository memberRepository;
    private final SseMessageRepository sseMessageRepository;
    private final NotificationRepository notificationRepository;


    // 메시지를 DB에 저장
    @Transactional
    public void saveMessage(String fromId, String toId, String content) {
        SseMessageEntity messageEntity = SseMessageEntity.builder()
                .fromMember(memberRepository.findById(fromId).orElseThrow())
                .toMember(memberRepository.findById(toId).orElseThrow())
                .content(content)
                .createDate(LocalDateTime.now())
                .build();

        sseMessageRepository.save(messageEntity);
    }

    // 로그인한 사용자가 받은 메시지를 DB에서 조회
    @Transactional(readOnly = true)
    public List<MessageDTO> getMessages(String memberId) {
        // 수신자가 로그인한 사용자인 메시지만 조회
        List<SseMessageEntity> messages = sseMessageRepository.findByToMember_MemberId(memberId);

        // 메시지를 DTO 형식으로 반환
        return messages.stream()
                .map(message -> MessageDTO.builder()
                        .sender(message.getFromMember().getMemberId())
                        .content(message.getContent())
                        .createdAt(message.getCreateDate())
                        .build())
                .collect(Collectors.toList());
    }

    // 로그인한 사용자의 일반 알림을 DB에서 조회
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotifications(String memberId) {
        List<NotificationEntity> notifications = notificationRepository.findByReceiver_MemberIdOrderByCreatedAtDesc(memberId);
        return notifications.stream()
                .map(notification -> NotificationDTO.builder()
                        .id(notification.getId())
                        .receiverId(notification.getReceiver().getMemberId())
                        .content(notification.getContent())
                        .createdAt(notification.getCreatedAt())
                        .isRead(notification.isRead())
                        .build())
                .collect(Collectors.toList());
    }
}
