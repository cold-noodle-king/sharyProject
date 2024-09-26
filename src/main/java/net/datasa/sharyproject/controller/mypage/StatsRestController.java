package net.datasa.sharyproject.controller.mypage;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.service.mypage.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController  // API 응답을 위한 @RestController
@RequestMapping("/mypage/api")
@RequiredArgsConstructor
public class StatsRestController {

    private final StatsService statsService;

    // 카테고리별 해시태그 사용 통계 데이터를 JSON으로 반환하는 API
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Map<String, Long>>> getHashtagStats() {
        Map<String, Map<String, Long>> hashtagStats = statsService.getHashtagUsageStats();
        return ResponseEntity.ok(hashtagStats);  // JSON 응답
    }
}
