package net.datasa.sharyproject.service.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ConcurrentHashMap<String , SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // SSE 구독
    public SseEmitter subscribe(String memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitterMap.put(memberId, emitter);
        log.debug("구독 성공 - 로그인 아이디 : {}", memberId);

        emitter.onCompletion(() -> emitterMap.remove(memberId));
        emitter.onTimeout(() -> emitterMap.remove(memberId));
        emitter.onError(e -> emitterMap.remove(memberId));

        return emitter;
    }

    // 메시지 전송
    public void sendMessage(String memberId, String fromId, String content) {
        SseEmitter emitter = emitterMap.get(memberId);

        if (emitter != null) {
            try {
                String jsonMessage = String.format("{\"type\":\"message\", \"sender\":\"%s\", \"content\":\"%s\", \"createdAt\":\"%s\"}",
                        fromId, content, LocalDateTime.now().toString());
                emitter.send(SseEmitter.event().name("message").data(jsonMessage));
            } catch (IOException e) {
                emitterMap.remove(memberId);
                log.error("메시지 전송 실패: {}", e.getMessage());
            }
        }
    }

    // 알림 전송
    public void sendNotification(String memberId, String content) {
        SseEmitter emitter = emitterMap.get(memberId);

        if (emitter != null) {
            try {
                String jsonNotification = String.format("{\"type\":\"notification\", \"content\":\"%s\", \"createdAt\":\"%s\"}",
                        content, LocalDateTime.now().toString());
                emitter.send(SseEmitter.event().name("notification").data(jsonNotification));
            } catch (IOException e) {
                emitterMap.remove(memberId);
                log.error("알림 전송 실패: {}", e.getMessage());
            }
        }
    }
}
