document.addEventListener('DOMContentLoaded', function () {
    console.log("emotions.js 파일이 로드되었습니다.");

    const emotionImages = document.querySelectorAll('.emotion-image');
    let accessToken;

    const clientId = '0370bb5560a145aaa0899ee8e8bac122'; // Spotify 대시보드에서 가져온 클라이언트 ID
    const redirectUri = 'http://localhost:8888'; // Spotify 대시보드에 등록한 Redirect URI
    const scopes = 'streaming user-read-email user-read-private'; // 필요한 스코프 설정

    // URL에서 액세스 토큰 추출
    const hash = window.location.hash.substring(1).split('&').reduce(function (initial, item) {
        if (item) {
            var parts = item.split('=');
            initial[parts[0]] = decodeURIComponent(parts[1]);
        }
        return initial;
    }, {});

    window.location.hash = ''; // URL 해시 부분을 제거

    accessToken = hash.access_token; // 추출한 액세스 토큰을 변수에 저장

    // 로그인 버튼 클릭 시 이벤트 처리
    document.getElementById('loginButton').addEventListener('click', function (event) {
        event.preventDefault(); // 기본 링크 동작 방지
        if (!accessToken) {
            // Spotify 로그인 페이지로 리디렉션
            const authUrl = `https://accounts.spotify.com/authorize?0370bb5560a145aaa0899ee8e8bac122=${encodeURIComponent(clientId)}&http://localhost:8888=${encodeURIComponent(redirectUri)}&scope=${encodeURIComponent(scopes)}&response_type=token&show_dialog=true`;
            window.location.href = authUrl;
        } else {
            console.log('이미 인증되었습니다. Access Token:', accessToken);
        }
    });

    if (accessToken) {
        console.log('Access Token:', accessToken);

        // Spotify Web Playback SDK 초기화
        window.onSpotifyWebPlaybackSDKReady = () => {
            const player = new Spotify.Player({
                name: 'Web Playback SDK Quick Start Player',
                getOAuthToken: cb => { cb(accessToken); },
                volume: 0.5
            });

            player.addListener('ready', ({ device_id }) => {
                console.log('Ready with Device ID', device_id);
            });

            player.addListener('not_ready', ({ device_id }) => {
                console.log('Device ID has gone offline', device_id);
            });

            player.connect();
        };
    }

    // 감정 이미지 클릭 시 이벤트
    emotionImages.forEach(image => {
        image.parentElement.addEventListener('click', function (event) {
            event.preventDefault();

            const playlistUri = image.parentElement.getAttribute('data-playlist');
            fetch('/spotify/play', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ context_uri: playlistUri })
            }).then(response => {
                if (response.ok) {
                    console.log("음악이 재생되었습니다.");
                } else {
                    console.error("음악 재생 오류:", response);
                }
            });
        });
    });
});
