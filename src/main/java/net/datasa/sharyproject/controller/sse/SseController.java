package net.datasa.sharyproject.controller.sse;

// SseController: SSE 기능을 제공하는 컨트롤러
// 주요 기능: SSE 구독, 메시지 전송, 알림 관리
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.sse.MessageDTO;
import net.datasa.sharyproject.domain.dto.sse.NotificationDTO;
import net.datasa.sharyproject.domain.dto.sse.UnifiedNotificationDTO;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.sse.SseMessageService;
import net.datasa.sharyproject.service.sse.SseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;


/**
 * SseController는 서버에서 클라이언트로 실시간 데이터를 전송하기 위한 Server-Sent Events(SSE)를 처리하는 컨트롤러입니다.
 * 이 컨트롤러는 주로 구독, 메시지 전송, 알림 관련 기능을 담당합니다.
 */
@Slf4j  // 로깅을 위한 Lombok 어노테이션. 로그 메시지를 출력할 수 있습니다.
@RequiredArgsConstructor  // final 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션
@Controller  // Spring MVC 컨트롤러임을 나타내는 어노테이션
public class SseController {

    // 서비스 의존성 주입
    private final SseService sseService;    // SSE 구독 및 메시지 전송을 담당하는 서비스
    private final SseMessageService sseMessageService; // 메시지 처리 관련 서비스

    /**
     * SSE 홈 페이지로 이동하는 메서드
     * @return SSE 홈 화면 템플릿을 반환
     */
    @GetMapping("/SseHome")
    public String SseHome() {
        // sse/SseHome.html 파일로 이동
        return "sse/SseHome"; // SSE 홈 템플릿 리턴
    }

    /**
     * 메시지 받기 테스트 페이지로 이동하는 메서드
     * @return 메시지 테스트 페이지 템플릿을 반환
     */
    @GetMapping("msgTest")
    public String msgTest() {
        // sse/msg.html 파일로 이동
        return "sse/msg";
    }

    /**
     * 클라이언트가 SSE를 구독하는 메서드
     * @param user 현재 로그인한 사용자
     * @return SseEmitter 객체를 반환하여 클라이언트가 실시간 데이터를 받을 수 있도록 함
     */
    @ResponseBody  // 이 메서드는 HTML 뷰가 아닌 데이터를 직접 반환함 (JSON 형식 등)
    @GetMapping("subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal AuthenticatedUser user) {

        log.debug("구독할 로그인 아이디 : {}", user.getUsername());  // 구독을 시작한 사용자 정보를 로그에 남김
        return sseService.subscribe(user.getUsername());   // 사용자 ID로 SSE 구독 시작, 서버가 클라이언트에게 실시간 데이터를 보냄
    }

    /**
     * 클라이언트가 메시지를 전송하는 메서드
     * SSE를 통해 실시간으로 메시지를 전송하고, 데이터베이스에도 저장함
     * @param sender 현재 로그인한 사용자 (메시지 발신자)
     * @param toId 수신자의 ID
     * @param message 전송할 메시지 내용
     */
    @ResponseBody
    @PostMapping("send")
    public void send(
            @AuthenticationPrincipal AuthenticatedUser sender,  // 현재 로그인한 사용자 정보를 가져옴
            @RequestParam("memberId") String toId,  // 수신자의 사용자 ID
            @RequestParam("message") String message) {  // 전송할 메시지 내용

        String fromId = sender.getUsername();  // 발신자의 사용자 ID
        log.debug("발신자: {}, 수신자: {}, 메시지: {}", fromId, toId, message);  // 발신자, 수신자, 메시지 내용을 로그에 기록

        // 수신자에게 SSE를 통해 실시간 메시지를 전송
        sseService.sendMessage(toId, fromId, message);
        // 발신자와 수신자 간의 메시지를 데이터베이스에 저장
        sseMessageService.saveMessage(fromId, toId, message);
    }


    /**
     * 현재 로그인한 사용자의 모든 알림을 가져오는 메서드
     * 메시지와 일반 알림을 모두 가져와 통합하여 반환함
     * @param user 현재 로그인한 사용자
     * @return 통합된 알림 목록을 반환
     */
    @ResponseBody
    @GetMapping("/allNotifications")
    public List<UnifiedNotificationDTO> getAllNotifications(@AuthenticationPrincipal AuthenticatedUser user) {
        String username = user.getUsername();
        log.debug("모든 알림 조회 - 사용자: {}", username);

        // 메시지 알림 가져오기
        List<MessageDTO> messages = sseMessageService.getMessages(username);

        // 일반 알림 가져오기
        List<NotificationDTO> notifications = sseMessageService.getNotifications(username);

        // 메시지와 알림을 통합된 DTO로 변환
        List<UnifiedNotificationDTO> unifiedNotifications = new ArrayList<>();

        // 각 메시지를 통합된 알림 DTO로 변환하여 추가
        for (MessageDTO message : messages) {
            unifiedNotifications.add(UnifiedNotificationDTO.builder()
                    .type("message")  // 알림 유형: 메시지
                    .sender(message.getSender())  // 발신자
                    .content(message.getContent())  // 메시지 내용
                    .createdAt(message.getCreatedAt())  // 생성 시각
                    .build());
        }

        // 각 일반 알림을 통합된 알림 DTO로 변환하여 추가
        for (NotificationDTO notification : notifications) {
            unifiedNotifications.add(UnifiedNotificationDTO.builder()
                    .type("notification")  // 알림 유형: 일반 알림
                    .sender(null)  // 일반 알림에는 발신자가 없을 수 있음
                    .content(notification.getContent())  // 알림 내용
                    .createdAt(notification.getCreatedAt())  // 생성 시각
                    .notificationType(notification.getNotificationType())  // 알림 타입 (예: "follow")
                    .build());
        }

        // createdAt 기준으로 정렬 (최신 순)
        unifiedNotifications.sort((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()));

        // 통합된 알림 목록을 반환
        return unifiedNotifications;
    }

/*    @ResponseBody
    @PostMapping("/sendFollowNotification")
    public void sendFollowNotification(
            @AuthenticationPrincipal AuthenticatedUser sender,
            @RequestParam("memberId") String memberId,
            @RequestParam("message") String message) {

        String fromId = sender.getUsername();
        sseService.sendNotification(memberId, message, "follow");  // 새로운 알림 유형 "follow"
    }*/

    /**
     * 알림 페이지로 이동하는 메서드
     * @return 알림함 페이지 템플릿을 반환
     */
    @GetMapping("/notifications")
    public String notificationsPage() {
        return "sse/msg";  // 알림함 페이지 템플릿 파일 경로
    }


    /**
     * 사용자가 받은 모든 메시지를 가져오는 메서드
     * @param user 현재 로그인한 사용자
     * @return 사용자가 받은 메시지 목록을 반환
     */
    @ResponseBody
    @GetMapping("/receivedMessages")
    public List<MessageDTO> getReceivedMessages(@AuthenticationPrincipal AuthenticatedUser user) {
        // 로그인한 사용자가 받은 모든 메시지를 반환
        return sseMessageService.getReceivedMessages(user.getUsername());
    }

    /**
     * 메시지 페이지로 이동하는 메서드
     * @return 메시지 페이지 템플릿을 반환
     */
    @GetMapping("/message")
    public String messagePage() {
        // sse/SseMessage.html 파일로 이동 (메시지 페이지)
        return "sse/SseMessage";  // message.html로 이동
    }

    /**
     * 현재 사용자가 보낸 모든 메시지를 조회하는 API
     * @param user 현재 로그인한 사용자
     * @return 사용자가 보낸 모든 메시지 목록을 반환
     */
    @ResponseBody
    @GetMapping("/allMessages")
    public List<MessageDTO> getAllMessages(@AuthenticationPrincipal AuthenticatedUser user) {
        // 로그인한 사용자가 보낸 모든 메시지를 반환
        return sseMessageService.getAllMessages(user.getUsername());
    }

}


