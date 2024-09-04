package net.datasa.sharyproject.controller.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.service.follow.FollowService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메인
 */
@Slf4j
@RequiredArgsConstructor
@Controller
public class FollowController {

    private final FollowService followService;

    /**
     * 메인화면으로 이동
     */


    // FollowController.java
    @GetMapping("/follow")
    public String follow() {
        return "follow/follow";
    }


    /**
     * 저장 테스트
     */
    @GetMapping("insert")
    public String insert() {

        try {
            followService.insert();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }

}

