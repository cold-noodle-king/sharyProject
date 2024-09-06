document.addEventListener('DOMContentLoaded', function () {
    const emotionImages = document.querySelectorAll('.emotion-image');

    emotionImages.forEach(image => {
        image.addEventListener('click', function () {
            const emotion = this.alt; // 이미지의 alt 속성을 통해 감정 이름을 가져옴

            // 클릭한 이미지에 애니메이션 클래스를 추가
            this.classList.add('clicked');

            // 0.5초 후에 애니메이션 클래스 제거 (애니메이션이 반복되지 않도록)
            setTimeout(() => {
                this.classList.remove('clicked');
            }, 500);

            alert(`선택한 감정: ${emotion}`); // 감정을 알림으로 표시
            // 추가 동작을 여기에 작성할 수 있습니다.
        });
    });
});
