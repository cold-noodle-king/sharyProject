document.addEventListener('DOMContentLoaded', function () {
    console.log("emotions.js 파일이 로드되었습니다.");

    const emotionImages = document.querySelectorAll('.emotion-image');
    const seekBar = document.getElementById('seek-bar'); // 시크바 요소 가져오기
    const currentTimeDisplay = document.getElementById('current-time'); // 현재 재생 시간을 표시할 요소
    const totalTimeDisplay = document.getElementById('total-time'); // 총 재생 시간을 표시할 요소
    // 볼륨 슬라이더 요소 가져오기
    const volumeSlider = document.getElementById('volume-slider');
    let accessToken;
    let deviceId;
    let player; // Spotify Player 객체
    let isSeeking = false; // 사용자가 시크바를 조작 중인지 확인하는 변수

    const clientId = '0370bb5560a145aaa0899ee8e8bac122'; // 여기에 내 실제 클라이언트 ID를 입력
    const redirectUri = 'http://localhost:8888/emotion-popup'; // 여기에 내 리다이렉트 URL을 입력
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
                    updateSeekBar(state);
                    updateTimeDisplays(state);
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

            // 시크바가 실시간으로 움직이도록 업데이트
            setInterval(function () {
                player.getCurrentState().then(state => {
                    if (state) {
                        updateSeekBar(state);
                        updateTimeDisplays(state);
                    }
                });
            }, 1000); // 1초마다 상태를 확인하고 업데이트
        };
    }

    // 감정별 여러 트랙 URI 맵핑
    const emotionTracks = {
        "행복": [
            "spotify:track:4Q8XZAsvHH5wCPisKMisH5?si=19ab3e39ae4b4bfd", // "풍선" - 동방신기
            "spotify:track:3GcTcXCJLVdy7iYhSKPGUp?si=a15978c2908b4aad",  // "비행기" - 거북이
            "spotify:track:6f4CAdAmrOfGH3FOfwHMSV?si=2d5ced3db4404002", // "후라이의 꿈" - 악뮤
            "spotify:track:4dMGKGfaWMZNLQEjkd8lme?si=1528ba9174e8446f",  // "여행" - 볼빨간사춘기
            "spotify:track:15c7KZTrsCUxCQcOdUVELc?si=10985b832de64324",  // "우주를 줄게" - 볼빨간사춘기
            "spotify:track:3BMAWsBp4jc35mCmebC3WT?si=00287a8bef0b4995",  // "Welcome to the show" - 데이식스
            "spotify:track:03qu1u4hDyepQQi2lNxCka?si=19b5f9fb31c54239"  // "신호등" - 이무진
        ],
        "슬픔": [
            "spotify:track:2zlgwqw8BLX2JGB76LIFeF?si=ddce96a8d98a4bff", // "그리워하다" - 비투비
            "spotify:track:1VnjByC7TUx5A73A4qtgoo?si=7e19abf3e28b4010", // "우산" - 에픽하이
            "spotify:track:1OhIn0L6iofnk5bjalWfHq?si=855c1454f5194ac6", // "소나기" - 이클립스
            "spotify:track:3uBAtRvr4roYk5CshWaKLC?si=93b224f2823a4920", // "열애중" - 벤
            "spotify:track:2njgIBj0nJ1UUFYNuW06et?si=5337a89a01644761", // "비가 오는 날엔" - 비스트 (B2ST)
            "spotify:track:4RqL3r72UOdolRaOwykb32?si=1e96cbbb917441aa", // "서쪽하늘" - 울랄라세션
            "spotify:track:1tSE6WK5ZavbqYQoiPoeu0?si=209de55051124fec", // "이별길" - IKON
            "spotify:track:0Ryd8975WihbObpp5cPW1t"  // "Goodbye My Lover" - James Blunt
        ],
        "화남": [
            "spotify:track:1r9xUipOqoNwggBpENDsvJ?si=ed18ce5794564ad0", // "Enemy" - Imagine Dragons
            "spotify:track:4mlQPWeSMkwJQcYTr9razZ?si=e0bb24330cec4cfd", // "보여줄게" - 에일리
            "spotify:track:4EwNWRBWdZ6bgvxRHlZ8OO?si=b19fcb8fb63748e9", // "삐딱하게" - GD
            "spotify:track:4ebNVkfNL5kHuxr3o5K93R?si=b0854a7fd46c40af", // "GO AWAY" - 2NE1
            "spotify:track:454I78HEySdcHE8fcVabTb?si=91e1c166e0fd4c9f", // "암스테르담" - 낫띵 벗 띠브즈
            "spotify:track:60a0Rd6pjrkxjPbaKzXjfq"  // "In the End" - Linkin Park
        ],
        "놀람": [
            "spotify:track:1zB4vmk8tFRmM9UULNzbLB?si=3b6aab2946d24c46", // "Thunder" - Imagine Dragons
            "spotify:track:5mqzhMuUpvnMfwNz6iepmO?si=40961e65dc1f4a20", // "Welcome to the party" - Diplo
            "spotify:track:3hm9Df6fGtYVPicuJulXyN?si=8f52a96de1f84094", // "celebrate'" - 핏불
            "spotify:track:4uLU6hMCjMI75M1A2tKUQC"  // "Bohemian Rhapsody" - Queen
        ],
        "두려움": [
            "spotify:track:3i7sd3kjMGaAPguhyMCfVV?si=5d7ff8a5140c40d7", // "낙화" - 자우림
            "spotify:track:15OqAO7mTGP98dSylxy5J0?si=208f51451a294bd7", // "창귀" - 안예은
            "spotify:track:5o0hXCXwwswm8Ij2w2ZxfK?si=a7658b4ba20c4722", // "도깨비불" - 에스파
            "spotify:track:2Fxmhks0bxGSBdJ92vM42m?si=694ca82bfc074501", // "배드가이" - 빌리 아일리쉬
            "spotify:track:5NSPsOGVNWYOMeeyZPF7HY?si=f15d43b7242e4da3",  // "중독" - 엑소
            "spotify:track:7n0D1iLva5zp4JcnlIMeVa?si=fd076aea395e4012"  // "늑대와 미녀" - 엑소
        ],
        "사랑": [
            "spotify:track:1qosh64U6CR5ki1g1Rf2dZ?si=4fcbf310d0ca45db", // "Love Lee" - 악동뮤지션
            "spotify:track:6cxbaStUOFS9Ssz3bHVVDJ?si=5a294cebf2594738", // "사랑은 은하수 다방에서" - 10CM
            "spotify:track:1rVPj9cryjgB7MdaU6sqN3?si=a81e49e89cc348b4",  // "봄봄봄" - 로이킴
            "spotify:track:1QeLSyqfbuOPZTIVT4QIn5?si=9bb1dbc0e7944fc5",  // "A Book of Love" - 하현상
            "spotify:track:1rVPj9cryjgB7MdaU6sqN3?si=a81e49e89cc348b4"  // "Love Blossom" - K.WILL
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

    // 시크바 업데이트 함수
    function updateSeekBar(state) {
        const currentPosition = state.position; // 현재 재생 위치 (밀리초)
        const duration = state.duration; // 총 재생 길이 (밀리초)
        const progress = (currentPosition / duration) * 100; // 현재 재생 퍼센트 계산

        if (!isSeeking) { // 사용자가 시크바를 조작 중이 아닌 경우에만 시크바 업데이트
            seekBar.value = progress; // 시크바의 값을 업데이트
        }
    }

    // 재생 시간 업데이트 함수
    function updateTimeDisplays(state) {
        const currentPosition = state.position; // 현재 재생 위치 (밀리초)
        const duration = state.duration; // 총 재생 길이 (밀리초)

        currentTimeDisplay.textContent = formatTime(currentPosition); // 현재 시간 표시
        totalTimeDisplay.textContent = formatTime(duration); // 총 재생 시간 표시
    }

    // 시간 형식 변환 함수 (밀리초 -> MM:SS)
    function formatTime(milliseconds) {
        const minutes = Math.floor(milliseconds / 60000);
        const seconds = Math.floor((milliseconds % 60000) / 1000);
        return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    }

    // 시크바 변경 시 플레이어에 반영
    seekBar.addEventListener('input', function () {
        isSeeking = true; // 시크바를 사용자가 조작 중임을 표시
    });

    seekBar.addEventListener('change', function () {
        player.getCurrentState().then(state => {
            if (state) {
                const seekValue = (seekBar.value / 100) * state.duration; // 사용자가 설정한 위치 계산
                player.seek(seekValue).then(() => {
                    console.log(`시크바 위치로 이동: ${seekValue}ms`);
                    isSeeking = false; // 시크바 조작 끝
                });
            }
        });
    });

    // 볼륨 슬라이더 변경 시 이벤트 처리
    volumeSlider.addEventListener('input', function() {
        const volume = volumeSlider.value / 100; // 0 ~ 1 사이의 값으로 변환
        if (player) {
            player.setVolume(volume).then(() => {
                console.log(`볼륨이 ${volume * 100}%로 설정되었습니다.`);
            });
        }
    });

});



