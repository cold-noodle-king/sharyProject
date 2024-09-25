package net.datasa.sharyproject.controller.sse;

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
 * 메인
 */
@Slf4j
@RequiredArgsConstructor
@Controller
public class SseController {

    private final SseService sseService;
    private final SseMessageService sseMessageService; // MessageService 주입

    /**
     * SSE 홈 페이지로 이동
     */
    @GetMapping("/SseHome")
    public String SseHome() {
        return "sse/SseHome"; // 반환하는 문자열은 템플릿의 경로입니다.
    }

    /**
     * 메시지 받기 테스트 페이지로 이동
     */
    @GetMapping("msgTest")
    public String msgTest() {
        return "sse/msg";
    }

    /**
     * 클라이언트가 SSE를 구독(기존)
     * 로그인한 사용자의 아이디를 이용하여 SseEmitter 생성
     */
    @ResponseBody
    @GetMapping("subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal AuthenticatedUser user) {

        log.debug("구독할 로그인 아이디 : {}", user.getUsername());
        return sseService.subscribe(user.getUsername());
    }

    /**
     * 메시지 보내기
     */
    @ResponseBody
    @PostMapping("send")
    public void send(
            @AuthenticationPrincipal AuthenticatedUser sender,
            @RequestParam("memberId") String toId,
            @RequestParam("message") String message) {

        String fromId = sender.getUsername();
        log.debug("발신자: {}, 수신자: {}, 메시지: {}", fromId, toId, message);

        sseService.sendMessage(toId, fromId, message);  // 메시지 전송
        sseMessageService.saveMessage(fromId, toId, message);  // 메시지 저장
    }


    /**
     * 모든 알림 불러오기
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

        for (MessageDTO message : messages) {
            unifiedNotifications.add(UnifiedNotificationDTO.builder()
                    .type("message")
                    .sender(message.getSender())
                    .content(message.getContent())
                    .createdAt(message.getCreatedAt())
                    .build());
        }

        for (NotificationDTO notification : notifications) {
            unifiedNotifications.add(UnifiedNotificationDTO.builder()
                    .type("notification")
                    .sender(null)
                    .content(notification.getContent())
                    .createdAt(notification.getCreatedAt())
                    .build());
        }

        // createdAt 기준으로 정렬 (최신 순)
        unifiedNotifications.sort((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()));

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

    @GetMapping("/notifications")
    public String notificationsPage() {
        return "sse/msg";  // 알림함 페이지 템플릿 파일 경로
    }


    @ResponseBody
    @GetMapping("/receivedMessages")
    public List<MessageDTO> getReceivedMessages(@AuthenticationPrincipal AuthenticatedUser user) {
        return sseMessageService.getReceivedMessages(user.getUsername());
    }

    @GetMapping("/message")
    public String messagePage() {
        return "sse/SseMessage";  // message.html로 이동
    }

    // 모든 쪽지 조회 API 추가
    @ResponseBody
    @GetMapping("/allMessages")
    public List<MessageDTO> getAllMessages(@AuthenticationPrincipal AuthenticatedUser user) {
        return sseMessageService.getAllMessages(user.getUsername());
    }

}


