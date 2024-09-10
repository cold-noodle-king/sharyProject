package net.datasa.sharyproject.controller.spotify;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {

    private static final String SPOTIFY_API_URL = "https://api.spotify.com/v1/me/player/play";

    @PostMapping("/play")
    public ResponseEntity<String> playMusic(@RequestBody Map<String, Object> requestData) {
        // 요청 데이터에서 액세스 토큰, 장치 ID, 재생할 트랙 URI 추출
        String accessToken = (String) requestData.get("accessToken");
        String deviceId = (String) requestData.get("deviceId");
        List<String> uris = (List<String>) requestData.get("uris");

        // Spotify API에 보낼 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");

        // API 요청을 보낼 본문 설정
        String body = "{\"uris\": [\"" + String.join("\",\"", uris) + "\"]}";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            // RestTemplate을 사용하여 API 요청을 전송
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    SPOTIFY_API_URL + "?device_id=" + deviceId,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            return response; // Spotify API의 응답을 그대로 반환
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            return ResponseEntity.status(500).body("음악 재생 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
