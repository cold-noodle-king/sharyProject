package net.datasa.sharyproject.service.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * @param loginId 사용자 아이디
     * @return 생성된 Emitter
     */
    public SseEmitter subscribe(String loginId) {
        //emitter 생성해서 맵에 추가
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); //최대한 오래 유지. millisecond 단위
        emitterMap.put(loginId, emitter);

        log.debug(emitterMap.toString());

        //연결이 완료되거나 시간 초과 시 emitter를 제거
        emitter.onCompletion(() -> emitterMap.remove(loginId));
        emitter.onTimeout(() -> emitterMap.remove(loginId));

        return emitter;
    }

    /**
     * 메시지 보내기
     * @param userId 	수신인 아이디
     * @param content 	메시지 내용
     */
    public void sendMessage(String userId, String content) {
        SseEmitter emitter = emitterMap.get(userId);

        log.debug("아이디:{}, 내용:{}, SseEmitter객체:{}", userId, content, emitter);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(content));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
