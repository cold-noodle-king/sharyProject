package net.datasa.sharyproject.controller.mypage;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.service.mypage.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mypage/api")
@RequiredArgsConstructor
public class StatsRestController {

    private final StatsService statsService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 해시태그 통계 데이터 추가
        Map<String, Map<String, Long>> hashtagStats = statsService.getHashtagUsageStats();
        stats.put("해시태그", hashtagStats);

        // 다이어리 통계 데이터 추가
        Map<String, Long> diaryStats = statsService.getDiaryStats();
        stats.put("다이어리", diaryStats);

        // 좋아요 랭킹 데이터 추가 (키 이름 수정)
        List<Map<String, Object>> topLikedNotes = statsService.getTopLikedNotes();
        stats.put("좋아요랭킹", topLikedNotes);

        return ResponseEntity.ok(stats);  // JSON 응답
    }
}
