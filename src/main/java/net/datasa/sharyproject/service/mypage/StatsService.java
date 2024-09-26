package net.datasa.sharyproject.service.mypage;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.repository.personal.PersonalNoteHashtagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final PersonalNoteHashtagRepository personalNoteHashtagRepository;

    // 전체 카테고리별 해시태그 통계를 가져오는 서비스 메서드
    public Map<String, Map<String, Long>> getHashtagUsageStats() {
        // 각 카테고리 번호별로 해시태그 사용 통계를 가져옴
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

    // 쿼리 결과를 Map<String, Long> 형식으로 변환
    private Map<String, Long> convertToMap(List<Object[]> results) {
        return results.stream().collect(Collectors.toMap(
                row -> (String) row[0],  // 해시태그 이름
                row -> (Long) row[1]     // 해시태그 사용 횟수
        ));
    }
}
