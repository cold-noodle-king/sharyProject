package net.datasa.sharyproject.service.spotify;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyService {

    private final String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1/me/player/play";

    /**
     * 사용자가 선택한 플레이리스트 URI를 재생하는 메서드
     * @param accessToken 스포티파이 인증 토큰
     * @param playlistUri 재생할 플레이리스트 URI
     * @return 재생 결과 메시지
     */
    public String playPlaylist(String accessToken, String playlistUri) {
        RestTemplate restTemplate = new RestTemplate();
        String url = SPOTIFY_API_BASE_URL;

        // API 요청을 위한 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디에 재생할 플레이리스트 URI 설정
        String requestBody = "{ \"context_uri\": \"" + playlistUri + "\" }";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Spotify API에 POST 요청 보내기
        try {
            restTemplate.postForEntity(url, entity, String.class);
            return "음악이 재생되었습니다.";
        } catch (Exception e) {
            return "음악 재생에 실패했습니다: " + e.getMessage();
        }
    }
}
