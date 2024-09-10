document.addEventListener('DOMContentLoaded', function () {
    console.log("emotions.js 파일이 로드되었습니다.");

    const emotionImages = document.querySelectorAll('.emotion-image');
    let accessToken;
    let deviceId;
    let player; // Spotify Player 객체

    const clientId = '0370bb5560a145aaa0899ee8e8bac122'; // 여기에 당신의 실제 클라이언트 ID를 입력
    const redirectUri = 'http://localhost:8888'; // 여기에 당신의 리디렉트 URL을 입력
    const scopes = 'streaming user-read-email user-read-private user-modify-playback-state user-read-playback-state'; // 필요한 스코프 추가

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
            const authUrl = `https://accounts.spotify.com/authorize?client_id=${encodeURIComponent(clientId)}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=${encodeURIComponent(scopes)}&response_type=token&show_dialog=true`;
            window.location.href = authUrl;
        } else {
            console.log('이미 인증되었습니다. Access Token:', accessToken);
        }
    });

    if (accessToken) {
        console.log('Access Token:', accessToken);

        // Spotify Web Playback SDK 초기화
        window.onSpotifyWebPlaybackSDKReady = () => {
            player = new Spotify.Player({
                name: 'Web Playback SDK Quick Start Player',
                getOAuthToken: cb => { cb(accessToken); },
                volume: 0.5
            });

            // 플레이어 이벤트 핸들러 설정
            player.addListener('ready', ({ device_id }) => {
                console.log('Ready with Device ID', device_id);
                deviceId = device_id; // 장치 ID를 변수에 저장합니다.
                playMusic(deviceId, accessToken); // 음악 자동 재생 시도
            });

            player.addListener('not_ready', ({ device_id }) => {
                console.log('Device ID has gone offline', device_id);
            });

            player.addListener('player_state_changed', (state) => {
                console.log('Player state changed:', state);
                if (state && !state.paused) {
                    document.getElementById('control-buttons').style.display = 'block'; // 음악 재생 시 컨트롤 표시
                }
            });

            player.connect();

            // 버튼 이벤트 핸들러 설정
            document.getElementById('playPauseButton').addEventListener('click', function () {
                player.togglePlay().then(() => {
                    console.log('재생/일시정지 버튼이 눌렸습니다.');
                });
            });

            document.getElementById('nextButton').addEventListener('click', function () {
                player.nextTrack().then(() => {
                    console.log('다음 곡 버튼이 눌렸습니다.');
                });
            });

            document.getElementById('prevButton').addEventListener('click', function () {
                player.previousTrack().then(() => {
                    console.log('이전 곡 버튼이 눌렸습니다.');
                });
            });
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
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    accessToken: accessToken,
                    deviceId: deviceId, // 서버에 전달할 장치 ID
                    uris: ["spotify:track:3n3Ppam7vgaVa1iaRUc9Lp"] // 재생할 트랙 URI
                })
            }).then(response => {
                if (response.ok) {
                    console.log("음악이 재생되었습니다.");
                } else {
                    console.error("음악 재생 오류:", response);
                }
            });
        });
    });

    // 재생을 시작하는 함수
    function playMusic(device_id, accessToken) {
        fetch('/spotify/play', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                accessToken: accessToken,
                deviceId: device_id,
                uris: ["spotify:track:3n3Ppam7vgaVa1iaRUc9Lp"] // 재생할 트랙 URI
            })
        }).then(response => {
            if (response.ok) {
                console.log("음악이 재생되었습니다.");
            } else {
                console.error("음악 재생 오류:", response);
            }
        });
    }
});
