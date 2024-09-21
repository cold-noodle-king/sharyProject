package net.datasa.sharyproject.service.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.repository.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE 테스트 서비스 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SseService {
    /**
     * SSE 연결 관리 맵 (thread-safe Map 사용)
     * 사용자 아이디를 Key로 지정
     */
    private final ConcurrentHashMap<String , SseEmitter> emitterMap = new ConcurrentHashMap<>();

    /**
     * SSE 연결. 클라이언트가 SSE 연결 요청을 보낼 때 이루어진다.
     * @param memberId 사용자 아이디
     * @return 생성된 Emitter
     */
    public SseEmitter subscribe(String memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitterMap.put(memberId, emitter);
        log.debug("구독 성공 - 로그인 아이디 : {}", memberId);

        emitter.onCompletion(() -> emitterMap.remove(memberId));
        emitter.onTimeout(() -> emitterMap.remove(memberId));
        emitter.onError(e -> emitterMap.remove(memberId));

        return emitter;
    }


    /**
     * 메시지 보내기
     * @param memberId 수신인 아이디
     * @param content 메시지 내용
     */
    public void sendMessage(String memberId, String fromId, String content) {
        SseEmitter emitter = emitterMap.get(memberId);

        if (emitter != null) {
            try {
                // JSON 형식의 메시지 전송
                String jsonMessage = String.format("{\"sender\":\"%s\", \"message\":\"%s\"}", fromId, content);
                emitter.send(SseEmitter.event().name("message").data(jsonMessage));
            } catch (IOException e) {
                emitterMap.remove(memberId);
                log.error("메시지 전송 실패: {}", e.getMessage());
            }
        }
    }


}
