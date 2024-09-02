package net.datasa.sharyproject.Controller.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("user")
@Controller
public class userController {

    /**
     * 로그인
     * @return 로그인 페이지 html
     */
    @GetMapping("login")
    public String login() {
        return "user/loginForm";
    }

    /**
     * 회원가입
     * @return 회원가입 페이지 html
     */
    @GetMapping("join")
    public String join() {
        return "user/joinForm";
    }
}
