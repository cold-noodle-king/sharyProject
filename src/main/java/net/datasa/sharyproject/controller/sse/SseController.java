package net.datasa.sharyproject.controller.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.sse.SseMessageService;
import net.datasa.sharyproject.service.sse.SseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
/*
    *//**
     * 메인화면으로 이동
     *//*
    @GetMapping({"", "/"})
    public String home() {
        return "home";
    }

    *//**
     * 로그인 페이지로 이동
     *//*
    @GetMapping("loginForm")
    public String loginForm() {
        return "member/loginForm";
    }*/


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
     * 메시지 보내기 페이지
     * 메시지 내용을 전달받아 수신인에게 보낸다.
     * 메시지를 DB에 저장하고 수신인에게 전송 (기존)
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

    // 클라이언트가 다시 접속할 때 로그인한 사용자의 메시지를 가져오는 API
    @ResponseBody
    @GetMapping("messages")
    public List<String> getMessages(@AuthenticationPrincipal AuthenticatedUser user) {
        log.debug("메시지 조회 - 수신자: {}", user.getUsername());
        // 로그인한 사용자에게 보낸 메시지만 조회
        return sseMessageService.getMessages(user.getUsername());
    }




}
