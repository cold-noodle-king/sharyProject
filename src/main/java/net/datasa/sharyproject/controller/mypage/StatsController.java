package net.datasa.sharyproject.controller.mypage;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.service.mypage.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller  // @RestController 대신 @Controller 사용
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    // HTML 템플릿을 반환하는 메서드
    @GetMapping("/stats")
    public String statsPage(Model model) {
        // 카테고리별 해시태그 통계 데이터를 모델에 추가
        Map<String, Map<String, Long>> hashtagStats = statsService.getHashtagUsageStats();
        model.addAttribute("hashtagStats", hashtagStats);

        // stats.html 템플릿을 렌더링
        return "mypage/stats";
    }
}