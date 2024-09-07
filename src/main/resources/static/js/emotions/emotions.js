console.log("emotions.js 파일이 로드되었습니다.");

// 전역 플레이어 변수
var player;

// API 스크립트가 로드되면 호출되는 함수
function onYouTubeIframeAPIReady() {
    console.log("YouTube IFrame Player API가 로드되었습니다."); // API 로드 확인

    try {
        player = new YT.Player('youtubePlayer', {
            events: {
                'onReady': function(event) {
                    console.log("YouTube Player가 준비되었습니다."); // 디버깅 로그 추가
                },
                'onError': function(event) {
                    console.error("YouTube Player 초기화 중 오류 발생:", event.data); // 오류 로그 추가
                }
            }
        });
    } catch (error) {
        console.error("YouTube Player 객체 생성 중 오류 발생:", error); // 객체 생성 오류 처리
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const emotionImages = document.querySelectorAll('.emotion-image');
    const playerContainer = document.getElementById('player-container');
    const controlButtons = document.getElementById('control-buttons');
    const playPauseButton = document.getElementById('playPauseButton');
    const prevButton = document.getElementById('prevButton');
    const nextButton = document.getElementById('nextButton');

    // 감정 이미지 클릭 시 이벤트
    emotionImages.forEach(image => {
        image.parentElement.addEventListener('click', function (event) {
            event.preventDefault();

            // 감정 이름을 가져오기 위한 alt 속성 사용
            const emotion = image.alt;
            console.log(`선택한 감정: ${emotion}`); // 클릭 로그 출력

            // 클릭한 이미지에 애니메이션 클래스를 추가
            image.classList.add('clicked');

            // 0.5초 후에 애니메이션 클래스 제거 (애니메이션이 반복되지 않도록)
            setTimeout(() => {
                image.classList.remove('clicked');
            }, 500);

            // YouTube 플레이리스트 로드
            const playlistURL = this.getAttribute('data-playlist');
            if (playlistURL) {
                const playlistId = new URL(playlistURL).searchParams.get('list');
                if (player && player.loadPlaylist) {
                    player.loadPlaylist({ list: playlistId, index: 0 });
                    playerContainer.style.display = 'block';
                    controlButtons.style.display = 'flex'; // 컨트롤 표시
                } else {
                    console.error("YouTube Player가 초기화되지 않았습니다.");
                }
            }
        });
    });

    // 재생/일시정지 버튼 클릭 시 동작
    playPauseButton.addEventListener('click', function () {
        if (player && player.getPlayerState() === YT.PlayerState.PLAYING) {
            player.pauseVideo();
            this.querySelector('i').classList.replace('fa-pause', 'fa-play');
        } else if (player) {
            player.playVideo();
            this.querySelector('i').classList.replace('fa-play', 'fa-pause');
        }
    });

    // 이전 곡 버튼 클릭 시 동작
    prevButton.addEventListener('click', function () {
        if (player) player.previousVideo();
    });

    // 다음 곡 버튼 클릭 시 동작
    nextButton.addEventListener('click', function () {
        if (player) player.nextVideo();
    });
});
