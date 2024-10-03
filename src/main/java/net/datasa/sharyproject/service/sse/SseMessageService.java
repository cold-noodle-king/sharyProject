package net.datasa.sharyproject.service.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.sse.MessageDTO;
import net.datasa.sharyproject.domain.dto.sse.NotificationDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
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
@Slf4j
public class SseMessageService {

    private final MemberRepository memberRepository;
    private final SseMessageRepository sseMessageRepository;
    private final NotificationRepository notificationRepository;


    // 메시지를 DB에 저장
/*    @Transactional
    public void saveMessage(String fromId, String toId, String content) {
        SseMessageEntity messageEntity = SseMessageEntity.builder()
                .fromMember(memberRepository.findById(fromId).orElseThrow())
                .toMember(memberRepository.findById(toId).orElseThrow())
                .content(content)
                .createDate(LocalDateTime.now())
                .build();

        sseMessageRepository.save(messageEntity);
    }*/
    @Transactional
    public void saveMessage(String fromId, String toId, String content) {
        MemberEntity fromMember = memberRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("발신자 정보를 찾을 수 없습니다."));
        MemberEntity toMember = memberRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("수신자 정보를 찾을 수 없습니다."));

        SseMessageEntity messageEntity = SseMessageEntity.builder()
                .fromMember(fromMember)
                .toMember(toMember)
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

    // SseMessageService.java
    @Transactional(readOnly = true)
    public List<MessageDTO> getReceivedMessages(String toId) {
        List<SseMessageEntity> messages = sseMessageRepository.findByToMember_MemberId(toId);

        return messages.stream()
                .map(message -> new MessageDTO(message.getFromMember().getMemberId(), message.getContent(), message.getCreateDate()))
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
                        .notificationType(notification.getNotificationType()) // 추가
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getAllMessages(String toId) {
        List<SseMessageEntity> messages = sseMessageRepository.findByToMember_MemberId(toId);

        return messages.stream()
                .map(message -> new MessageDTO(
                        message.getFromMember().getMemberId(),
                        message.getContent(),
                        message.getCreateDate()))
                .collect(Collectors.toList());
    }


/*    @Transactional(readOnly = true)
    public int getUnreadNotificationsCount(String memberId) {
        int notificationCount = notificationRepository.countByReceiver_MemberIdAndIsReadFalse(memberId);
        int messageCount = sseMessageRepository.countByToMember_MemberIdAndIsReadFalse(memberId);
        return notificationCount + messageCount;
    }*/

/*
    @Transactional(readOnly = true)
    public int getUnreadNotificationsCount(String memberId) {
        int notificationCount = notificationRepository.countByReceiver_MemberIdAndIsReadFalse(memberId);
        int messageCount = sseMessageRepository.countByToMember_MemberIdAndIsReadFalse(memberId);
        // log.debug("Unread counts for user {}: notifications={}, messages={}", memberId, notificationCount, messageCount);
        return notificationCount + messageCount;
    }
*/



    //알림 읽은 수 API 추가
/*    @Transactional(readOnly = true)
    public int getReadNotificationsCount(String memberId) {
        // 읽은 알림 수 조회 (is_read=true)
        int readNotificationCount = notificationRepository.countByReceiver_MemberIdAndIsReadTrue(memberId);
        return readNotificationCount;
    }*/


    // 읽지 않은 알림 수를 반환하는 메서드
    @Transactional(readOnly = true)
    public int getUnreadNotificationsCount(String memberId) {
        // 알림과 쪽지의 읽지 않은 수를 모두 계산
        int notificationCount = notificationRepository.countByReceiver_MemberIdAndIsReadFalse(memberId);
        int messageCount = sseMessageRepository.countByToMember_MemberIdAndIsReadFalse(memberId);
        return notificationCount + messageCount;
    }


    // 모든 알림을 읽음으로 처리하는 메서드
    @Transactional
    public void markAllAsRead(String memberId) {
        // 모든 알림과 쪽지를 읽음 처리
        notificationRepository.markAllAsReadByReceiverId(memberId);
        sseMessageRepository.markAllAsReadByToMemberId(memberId);
        log.info("알림 읽음 처리 완료: {}", memberId);
    }


}
