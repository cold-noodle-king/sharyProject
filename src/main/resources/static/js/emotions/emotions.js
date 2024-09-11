document.addEventListener('DOMContentLoaded', function () {
    console.log("emotions.js 파일이 로드되었습니다.");

    const emotionImages = document.querySelectorAll('.emotion-image');
    let accessToken;
    let deviceId;
    let player; // Spotify Player 객체
    let currentTrackDuration = 0; // 현재 트랙 길이
    let currentTrackPosition = 0; // 현재 트랙 위치

    const clientId = '0370bb5560a145aaa0899ee8e8bac122'; // 여기에 내 실제 클라이언트 ID를 입력
    const redirectUri = 'http://localhost:8888'; // 여기에 내 리다이렉트 URL을 입력
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

        // 로그인 성공 시 버튼 숨기기
        hideLoginButtons();

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
            });

            player.addListener('not_ready', ({ device_id }) => {
                console.log('Device ID has gone offline', device_id);
            });

            player.addListener('player_state_changed', (state) => {
                console.log('Player state changed:', state);
                if (state) {
                    currentTrackDuration = state.duration;
                    currentTrackPosition = state.position;
                    updateSeekBar();
                }
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

            // 시크 바 변경 시
            document.getElementById('seek-bar').addEventListener('input', function (event) {
                const newPosition = (event.target.value / 100) * currentTrackDuration;
                player.seek(newPosition).then(() => {
                    console.log('재생 위치가 조정되었습니다.');
                });
            });
        };
    }

    // 감정별 여러 트랙 URI 맵핑
    const emotionTracks = {
        "행복": [
            "spotify:track:60nZcImufyMA1MKQY3dcCH?si=85311777eb444bf2", // "Happy" - Pharrell Williams
            "spotify:track:4ppKRxity3jJd2hGoVyD4u?si=1d117fb66d0c46f6", // "No make up" - 자이언티
            "spotify:track:1pz24zu5H9A0S1a2NKT4F0?si=27f27692be264a27"  // "SoulMate" - ZICO
        ],
        "슬픔": [
            "spotify:track:2njgIBj0nJ1UUFYNuW06et?si=5337a89a01644761", // "비가 오는 날엔" - 비스트 (B2ST)
            "spotify:track:1VnjByC7TUx5A73A4qtgoo?si=7e19abf3e28b4010", // "우산" - 에픽하이
            "spotify:track:4RqL3r72UOdolRaOwykb32?si=1e96cbbb917441aa", // "서쪽하늘" - 울랄라세션
            "spotify:track:5UEnHoDYpsA3cEH4Rz4oXT", // "All I Want" - Kodaline
            "spotify:track:6NpP18oTkDAFhzT7s3bJGW", // "Tears in Heaven" - Eric Clapton
            "spotify:track:0Ryd8975WihbObpp5cPW1t"  // "Goodbye My Lover" - James Blunt
        ],
        "화남": [
            "spotify:track:5ghIJDpPoe3CfHMGu71E6T", // "Smells Like Teen Spirit" - Nirvana
            "spotify:track:2G7V7zsVDxg1yRsu7Ew9RJ", // "Lose Yourself" - Eminem
            "spotify:track:60a0Rd6pjrkxjPbaKzXjfq"  // "In the End" - Linkin Park
        ],
        "놀람": [
            "spotify:track:3gkvM6A51Uydh0bNE3u8GC", // "Thunder" - Imagine Dragons
            "spotify:track:2cGxRwrMyEAp8dEbuZaVv6", // "Thriller" - Michael Jackson
            "spotify:track:2nLtzopw4rPReszdYBJU6h", // "Don't Stop Believin'" - Journey
            "spotify:track:4uLU6hMCjMI75M1A2tKUQC"  // "Bohemian Rhapsody" - Queen
        ],
        "두려움": [
            "spotify:track:6nDKrPlXdpomGBgAlO7UdP", // "Haunted" - Taylor Swift
            "spotify:track:2jLLAJ2wY0JHQOUfYn7LhA", // "Disturbia" - Rihanna
            "spotify:track:1DFD5Fotzgn6yYXkYsKiGs"  // "Psycho" - Post Malone
        ],
        "사랑": [
            "spotify:track:6cxbaStUOFS9Ssz3bHVVDJ?si=5a294cebf2594738", // "사랑은 은하수 다방에서" - 10CM
            "spotify:track:6gBFPUFcJLzWGx4lenP6h2", // "Thinking Out Loud" - Ed Sheeran
            "spotify:track:3U4isOIWM3VvDubwSI3y7a"  // "All of Me" - John Legend
        ]
    };

    // 감정 이미지 클릭 시 이벤트
    emotionImages.forEach(image => {
        image.parentElement.addEventListener('click', function (event) {
            event.preventDefault();

            // 애니메이션 효과 추가
            image.classList.add('animated'); // 클릭한 이미지에 애니메이션 클래스 추가

            // 일정 시간 후 애니메이션 클래스 제거 (1초 후)
            setTimeout(() => {
                image.classList.remove('animated');
            }, 1000);

            const emotion = image.alt.trim(); // 이미지의 alt 속성을 통해 감정 이름 가져오기
            const uris = emotionTracks[emotion]; // 해당 감정에 매핑된 여러 트랙 URI 가져오기

            if (!uris) {
                console.error("해당 감정에 대한 트랙을 찾을 수 없습니다.");
                return;
            }

            fetch('/spotify/play', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    accessToken: accessToken,
                    deviceId: deviceId, // 서버에 전달할 장치 ID
                    uris: uris // 선택된 감정에 대한 여러 트랙 URI 전송
                })
            }).then(response => {
                if (response.ok) {
                    console.log(`${emotion} 감정에 대한 음악이 재생되었습니다.`);
                } else {
                    console.error("음악 재생 오류:", response);
                }
            });
        });
    });

    // 로그인 성공 시 버튼 숨기기
    function hideLoginButtons() {
        document.getElementById('loginButton').style.display = 'none'; // 첫 번째 버튼 숨기기
        document.getElementById('spotifySiteLogin').style.display = 'none'; // 두 번째 링크 숨기기
    }

    // 시크 바 업데이트
    function updateSeekBar() {
        const seekBar = document.getElementById('seek-bar');
        if (seekBar) {
            seekBar.value = (currentTrackPosition / currentTrackDuration) * 100;
        }
    }
});
