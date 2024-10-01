package net.datasa.sharyproject.service.mypage;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.repository.personal.PersonalLikeRepository;
import net.datasa.sharyproject.repository.personal.PersonalNoteHashtagRepository;
import net.datasa.sharyproject.repository.personal.PersonalNoteRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final PersonalNoteHashtagRepository personalNoteHashtagRepository;
    private final PersonalNoteRepository personalNoteRepository;
    private final PersonalLikeRepository personalLikeRepository;

    // 해시태그 통계를 제공하는 메서드 (기존 코드)
    public Map<String, Map<String, Long>> getHashtagUsageStats() {
        Map<String, Map<String, Long>> stats = Map.of(
                "일상", convertToMap(personalNoteHashtagRepository.findHashtagUsageByCategory(1)),
                "여행", convertToMap(personalNoteHashtagRepository.findHashtagUsageByCategory(2)),
                "육아", convertToMap(personalNoteHashtagRepository.findHashtagUsageByCategory(3)),
                "연애", convertToMap(personalNoteHashtagRepository.findHashtagUsageByCategory(4)),
                "취미", convertToMap(personalNoteHashtagRepository.findHashtagUsageByCategory(5)),
                "운동", convertToMap(personalNoteHashtagRepository.findHashtagUsageByCategory(6))
        );
        return stats;
    }

    // 해시태그 통계 결과를 Map으로 변환하는 메서드 (기존 코드)
    private Map<String, Long> convertToMap(List<Object[]> results) {
        return results.stream().collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
        ));
    }

    // 다이어리 통계 제공 (카테고리별 다이어리 수)
    public Map<String, Long> getDiaryStats() {
        List<Object[]> results = personalNoteRepository.countDiariesByCategory();
        return results.stream().collect(Collectors.toMap(
                row -> (String) row[0],  // 카테고리 이름
                row -> (Long) row[1]     // 다이어리 개수
        ));
    }

    // 좋아요 상위 3개의 노트를 가져오는 메서드 (새로 추가된 부분)
    public List<Map<String, Object>> getTopLikedNotes() {
        List<Object[]> results = personalLikeRepository.findTopLikedNotes();

        return results.stream()
                .map(row -> {
                    String noteTitle = (String) row[0];
                    Long likeCount = (Long) row[1];

                    Map<String, Object> noteInfo = new HashMap<>();
                    noteInfo.put("noteTitle", noteTitle);
                    noteInfo.put("likeCount", likeCount);
                    return noteInfo;
                })
                .collect(Collectors.toList());
    }
}
