package net.datasa.sharyproject.controller.mypage;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.service.mypage.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController  // API 응답을 위한 @RestController
@RequestMapping("/mypage/api")
@RequiredArgsConstructor
public class StatsRestController {

    private final StatsService statsService;

    // 카테고리별 해시태그 및 다이어리 사용 통계 데이터를 JSON으로 반환하는 API
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 해시태그 통계 데이터 추가
        Map<String, Map<String, Long>> hashtagStats = statsService.getHashtagUsageStats();
        stats.put("해시태그", hashtagStats);

        // 다이어리 통계 데이터 추가
        Map<String, Long> diaryStats = statsService.getDiaryStats();  // 서비스에서 다이어리 통계 데이터 가져옴
        stats.put("다이어리", diaryStats);

        return ResponseEntity.ok(stats);  // JSON 응답
    }
}
