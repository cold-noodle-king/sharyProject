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

/**
 * SseMessageService 클래스
 * 이 클래스는 SSE(서버-보낸 이벤트) 메시지와 알림을 처리합니다.
 * 메시지 저장, 읽기, 일반 알림 처리 등의 작업을 담당합니다.
 */
@RequiredArgsConstructor  // 모든 의존성을 생성자 주입 방식으로 자동 생성
@Service  // 이 클래스가 서비스 계층임을 명시
public class SseMessageService {

    // 의존성 주입 (회원, 메시지, 알림 관련 레포지토리)
    private final MemberRepository memberRepository;
    private final SseMessageRepository sseMessageRepository;
    private final NotificationRepository notificationRepository;


    /**
     * 메시지를 데이터베이스에 저장하는 메서드
     * @param fromId 메시지를 보낸 사용자의 ID
     * @param toId 메시지를 받을 사용자의 ID
     * @param content 메시지 내용
     */
    @Transactional
    public void saveMessage(String fromId, String toId, String content) {
        // 발신자와 수신자의 정보를 바탕으로 메시지 엔티티 생성
        SseMessageEntity messageEntity = SseMessageEntity.builder()
                .fromMember(memberRepository.findById(fromId).orElseThrow())
                .toMember(memberRepository.findById(toId).orElseThrow())
                .content(content)
                .createDate(LocalDateTime.now())
                .build();

        // 메시지 엔티티를 데이터베이스에 저장
        sseMessageRepository.save(messageEntity);
    }

    /**
     * 로그인한 사용자가 받은 메시지를 데이터베이스에서 조회하는 메서드
     * @param memberId 로그인한 사용자의 ID
     * @return 메시지 목록 (MessageDTO 리스트)
     */
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

    /**
     * 사용자가 받은 모든 메시지를 조회하는 메서드
     * @param toId 수신자의 ID
     * @return 메시지 목록 (MessageDTO 리스트)
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getReceivedMessages(String toId) {
        // 수신자가 toId인 메시지를 조회
        List<SseMessageEntity> messages = sseMessageRepository.findByToMember_MemberId(toId);

        // 메시지 엔티티를 MessageDTO로 변환하여 반환
        return messages.stream()
                .map(message -> new MessageDTO(message.getFromMember().getMemberId(), message.getContent(), message.getCreateDate()))
                .collect(Collectors.toList());
    }


    /**
     * 로그인한 사용자의 일반 알림을 조회하는 메서드
     * @param memberId 로그인한 사용자의 ID
     * @return 알림 목록 (NotificationDTO 리스트)
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotifications(String memberId) {
        // 수신자가 로그인한 사용자이고, 생성된 순서대로 정렬된 알림 목록을 조회
        List<NotificationEntity> notifications = notificationRepository.findByReceiver_MemberIdOrderByCreatedAtDesc(memberId);

        // 조회된 알림을 NotificationDTO로 변환하여 반환
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

    /**
     * 사용자가 받은 모든 메시지를 조회하는 메서드
     * @param toId 수신자의 ID
     * @return 메시지 목록 (MessageDTO 리스트)
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getAllMessages(String toId) {
        // 수신자가 toId인 메시지 목록을 조회
        List<SseMessageEntity> messages = sseMessageRepository.findByToMember_MemberId(toId);

        // 메시지 엔티티를 MessageDTO로 변환하여 반환
        return messages.stream()
                .map(message -> new MessageDTO(
                        message.getFromMember().getMemberId(),
                        message.getContent(),
                        message.getCreateDate()))
                .collect(Collectors.toList());
    }
}
