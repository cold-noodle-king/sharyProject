package net.datasa.sharyproject.controller.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.sse.SseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 메인
 */
@Slf4j
@RequiredArgsConstructor
@Controller
public class SseController {

    private final SseService sseService;
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
     * 메시지 받기 테스트 페이지로 이동
     */
    @GetMapping("/msgTest")
    public String msgTest() {
        return "sse/msg";
    }

    /**
     * 클라이언트가 SSE를 구독
     * 로그인한 사용자의 아이디를 이용하여 SseEmitter 생성
     */
    @ResponseBody
    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal AuthenticatedUser user) {
        log.debug("구독할 로그인 아이디 : {}", user.getUsername());
        return sseService.subscribe(user.getUsername());
    }

    /**
     * 메시지 보내기 페이지
     * 메시지 내용을 전달받아 수신인에게 보낸다.
     */
    @ResponseBody
    @PostMapping("/send")
    public void send(@RequestParam("memberId") String memberId, @RequestParam("message") String message) {

        log.debug("메시지 보낼 아이디 : {}, 메시지 내용 : {}", memberId, message);
        sseService.sendMessage(memberId, message);
    }



}
