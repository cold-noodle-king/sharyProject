package net.datasa.sharyproject.controller.spotify;

import net.datasa.sharyproject.service.spotify.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {

    @Autowired
    private SpotifyService spotifyService;

    /**
     * 사용자의 감정에 따라 스포티파이에서 플레이리스트를 재생하는 엔드포인트
     * @param accessToken 스포티파이 인증 토큰
     * @param playlistUri 재생할 플레이리스트 URI
     * @return 재생 결과 메시지
     */
    @PostMapping("/play")
    public String play(@RequestHeader("Authorization") String accessToken, @RequestBody String playlistUri) {
        // 스포티파이 API를 사용하여 플레이리스트 재생
        return spotifyService.playPlaylist(accessToken, playlistUri);
    }
}
