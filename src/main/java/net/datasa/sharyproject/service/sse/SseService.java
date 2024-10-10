package net.datasa.sharyproject.service.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.chat.ChatMessageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SseService 클래스
 * 이 클래스는 SSE(서버-보낸 이벤트)를 이용해 클라이언트와 서버 간의 실시간 통신을 처리합니다.
 * 주로 알림, 메시지, 채팅 등을 클라이언트에게 실시간으로 전송하는 역할을 합니다.
 */
@Slf4j  // 로그 출력 기능을 위한 Lombok 어노테이션
@RequiredArgsConstructor  // 의존성 주입을 위한 생성자 자동 생성
@Service  // 이 클래스가 서비스 역할을 함을 명시
public class SseService {

    // SSE 연결 관리 맵
    private final ConcurrentHashMap<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    /**
     * 사용자가 SSE를 구독하는 메서드
     * @param memberId 구독할 사용자의 ID
     * @return SSE 연결 객체(SseEmitter)
     */
    public SseEmitter subscribe(String memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitterMap.put(memberId, emitter);
        log.debug("구독 성공 - 로그인 아이디 : {}", memberId);

        // 연결이 완료되면 발생하는 이벤트 처리
        emitter.onCompletion(() -> emitterMap.remove(memberId));
        emitter.onTimeout(() -> emitterMap.remove(memberId));
        emitter.onError(e -> emitterMap.remove(memberId));

        // SSE Emitter 반환
        return emitter;
    }

    /**
     * 특정 사용자에게 메시지(쪽지)를 전송하는 메서드
     * @param memberId 메시지를 받을 사용자 ID
     * @param fromId 메시지를 보낸 사용자 ID
     * @param content 메시지 내용
     */
    public void sendMessage(String memberId, String fromId, String content) {
        SseEmitter emitter = emitterMap.get(memberId);

        if (emitter != null) {
            try {
                // JSON 형식으로 메시지를 구성
                String jsonMessage = String.format(
                        "{\"type\":\"message\", \"sender\":\"%s\", \"content\":\"%s\", \"createdAt\":\"%s\"}",
                        fromId, content, LocalDateTime.now().toString());

                // 메시지를 SSE 이벤트로 전송
                emitter.send(SseEmitter.event().name("message").data(jsonMessage));
            } catch (IOException e) {
                // 전송 실패 시 Emitter를 맵에서 제거
                emitterMap.remove(memberId);
                log.error("메시지 전송 실패: {}", e.getMessage());
            }
        }
    }

    /**
     * 특정 사용자에게 알림을 전송하는 메서드
     * @param memberId 알림을 받을 사용자 ID
     * @param content 알림 내용
     * @param notificationType 알림 유형 (예: "chat", "follow")
     */
    public void sendNotification(String memberId, String content, String notificationType) {
        SseEmitter emitter = emitterMap.get(memberId);

        if (emitter != null) {
            try {
                // JSON 형식으로 알림을 구성
                String jsonNotification = String.format(
                        "{\"type\":\"notification\", \"content\":\"%s\", \"createdAt\":\"%s\", \"notificationType\":\"%s\"}",
                        content, LocalDateTime.now().toString(), notificationType);
                // 알림을 SSE 이벤트로 전송
                emitter.send(SseEmitter.event().name("notification").data(jsonNotification));
            } catch (IOException e) {
                // 전송 실패 시 Emitter를 맵에서 제거
                emitterMap.remove(memberId); // Emitter 제거
                log.error("알림 전송 실패: {}", e.getMessage());
                // 알림 전송 실패 시 재시도 로직 호출
                retryNotification(memberId, content, notificationType); // 재시도 로직 추가
            }
        } else {
            log.warn("수신자의 SSE Emitter를 찾을 수 없음 - 수신자 ID: {}", memberId);
        }
    }

    /**
     * 알림 전송 실패 시 재시도하는 메서드
     * @param memberId 알림을 받을 사용자 ID
     * @param content 알림 내용
     * @param notificationType 알림 유형 (예: "chat", "follow")
     */
    private void retryNotification(String memberId, String content, String notificationType) {
        // 여기서 실패한 알림을 나중에 다시 시도하거나, DB에 저장하여 관리할 수 있는 로직을 추가할 수 있습니다.
        log.info("재시도 로직: 사용자 {}에게 알림을 다시 전송합니다.", memberId);
    }


    // 새로운 채팅 메시지를 수신자에게 전송하는 메서드 추가
  /*  public void sendChatMessage(String recipientId, String senderId, ChatMessageDTO messageDTO) {
        SseEmitter emitter = emitterMap.get(recipientId);

        if (emitter != null) {
            try {
                String jsonMessage = String.format(
                        "{\"type\":\"chat\", \"sender\":\"%s\", \"content\":\"%s\", \"chatId\":%d, \"createdAt\":\"%s\"}",
                        senderId,
                        messageDTO.getMessageContent(),
                        messageDTO.getChatId(),
                        messageDTO.getCreatedDate().toString()
                );
                emitter.send(SseEmitter.event().name("chat").data(jsonMessage));
                log.debug("채팅 메시지 전송 성공 - 수신자: {}, 메시지: {}", recipientId, jsonMessage);
            } catch (IOException e) {
                emitterMap.remove(recipientId);
                log.error("채팅 메시지 전송 실패: {}", e.getMessage());
            }
        } else {
            log.warn("수신자의 SSE Emitter를 찾을 수 없음 - 수신자 ID: {}", recipientId);
        }
    }
}*/

    /**
     * 새로운 채팅 메시지를 수신자에게 전송하는 메서드
     * @param recipientId 채팅 메시지 수신자 ID
     * @param senderId 채팅 메시지 발신자 ID
     * @param messageDTO 전송할 채팅 메시지의 정보
     */
    public void sendChatMessage(String recipientId, String senderId, ChatMessageDTO messageDTO) {
        SseEmitter emitter = emitterMap.get(recipientId);

        if (emitter != null) {
            try {
                // JSON 형식으로 채팅 메시지를 구성
                String jsonMessage = String.format(
                        "{\"type\":\"chat\", \"sender\":\"%s\", \"content\":\"%s\", \"chatId\":%d, \"createdAt\":\"%s\"}",
                        senderId,
                        messageDTO.getMessageContent(),
                        messageDTO.getChatId(),
                        messageDTO.getCreatedDate().toString()
                );

                // 채팅 메시지를 SSE 이벤트로 전송
                emitter.send(SseEmitter.event().name("chat").data(jsonMessage));
                log.debug("채팅 메시지 전송 성공 - 수신자: {}, 메시지: {}", recipientId, jsonMessage);
            } catch (IOException e) {
                // 전송 실패 시 Emitter를 맵에서 제거
                emitterMap.remove(recipientId);
                log.error("채팅 메시지 전송 실패: {}", e.getMessage());
            }
        } else {
            log.warn("수신자의 SSE Emitter를 찾을 수 없음 - 수신자 ID: {}", recipientId);
        }
    }
}
