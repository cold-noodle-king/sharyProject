package net.datasa.sharyproject.service.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.chat.ChatMessageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseService {

    // SSE 연결 관리 맵
    private final ConcurrentHashMap<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // SSE 구독
    public SseEmitter subscribe(String memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitterMap.put(memberId, emitter);
        log.debug("구독 성공 - 로그인 아이디 : {}", memberId);

        // 연결이 완료되면 발생하는 이벤트 처리
        emitter.onCompletion(() -> emitterMap.remove(memberId));
        emitter.onTimeout(() -> emitterMap.remove(memberId));
        emitter.onError(e -> emitterMap.remove(memberId));

        return emitter;
    }

    // 메시지(쪽지) 전송
    public void sendMessage(String memberId, String fromId, String content) {
        SseEmitter emitter = emitterMap.get(memberId);

        if (emitter != null) {
            try {
                String jsonMessage = String.format(
                        "{\"type\":\"message\", \"sender\":\"%s\", \"content\":\"%s\", \"createdAt\":\"%s\"}",
                        fromId, content, LocalDateTime.now().toString());
                emitter.send(SseEmitter.event().name("message").data(jsonMessage));
            } catch (IOException e) {
                emitterMap.remove(memberId);
                log.error("메시지 전송 실패: {}", e.getMessage());
            }
        }
    }

    //SSE 알림 전송
    public void sendNotification(String memberId, String content, String notificationType) {
        SseEmitter emitter = emitterMap.get(memberId);

        if (emitter != null) {
            try {
                String jsonNotification = String.format(
                        "{\"type\":\"notification\", \"content\":\"%s\", \"createdAt\":\"%s\", \"notificationType\":\"%s\"}",
                        content, LocalDateTime.now().toString(), notificationType);
                emitter.send(SseEmitter.event().name("notification").data(jsonNotification));
            } catch (IOException e) {
                emitterMap.remove(memberId); // Emitter 제거
                log.error("알림 전송 실패: {}", e.getMessage());
                retryNotification(memberId, content, notificationType); // 재시도 로직 추가
            }
        } else {
            log.warn("수신자의 SSE Emitter를 찾을 수 없음 - 수신자 ID: {}", memberId);
        }
    }

    // 알림 전송 재시도 로직
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

    // 새로운 채팅 메시지를 수신자에게 전송하는 메서드 수정
    public void sendChatMessage(String recipientId, String senderId, ChatMessageDTO messageDTO) {
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
}
