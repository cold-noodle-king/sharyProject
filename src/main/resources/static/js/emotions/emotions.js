document.addEventListener('DOMContentLoaded', function () {
    console.log("emotions.js 파일이 로드되었습니다.");

    const emotionImages = document.querySelectorAll('.emotion-image');
    let accessToken;

    // 액세스 토큰이 URL에서 있는지 확인
    const hash = window.location.hash.substring(1).split('&').reduce(function (initial, item) {
        if (item) {
            var parts = item.split('=');
            initial[parts[0]] = decodeURIComponent(parts[1]);
        }
        return initial;
    }, {});

    window.location.hash = '';

    accessToken = hash.access_token;

    if (!accessToken) {
        console.log('Spotify 인증이 필요합니다.');
    } else {
        console.log('Access Token:', accessToken);
    }

    // 감정 이미지 클릭 시 이벤트
    emotionImages.forEach(image => {
        image.parentElement.addEventListener('click', function (event) {
            event.preventDefault();

            const playlistUri = image.getAttribute('data-playlist');
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
