package net.datasa.sharyproject.controller.mypage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("mypage")
@Controller
public class StatsController {

    /**
     * stats 통계
     * @return stats html 페이지로 이동
     */
    @GetMapping("stats")
    public String stats() {
        return "mypage/stats";
    }
}
