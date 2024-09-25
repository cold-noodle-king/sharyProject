window.onload = function() {
    // 캔버스와 컨텍스트 정의
    const canvas = document.getElementById('waveCanvas');
    const ctx = canvas.getContext('2d');

    // 창 크기에 맞게 캔버스 크기 조정
    function resizeCanvas() {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    }
    resizeCanvas();  // 초기 캔버스 크기 조정
    window.addEventListener('resize', resizeCanvas);  // 창 크기 변경 시 캔버스 크기 다시 조정

    let waves = [];  // 파도 객체를 저장할 배열
    let waveCount = 6;  // 파도의 레이어(층) 수 설정
    // 밝은 계열의 따뜻한 색상 팔레트
    let colors = ['#df9b70', '#f4901b', '#FFE7A8', '#FFD1A9', '#FFB6C1', '#FFEBCD'];

    // Easing 함수 (사용하지 않으므로 제거 가능)
    // function easeInOutSine(t) {
    //     return -(Math.cos(Math.PI * t) - 1) / 2;
    // }

    // 파도 클래스를 정의하여 각 파도에 대한 설정과 동작을 관리
    class Wave {
        constructor(index) {
            this.index = index;  // 파도의 인덱스 값
            this.amplitude = 50 + (Math.random() * 50);  // 파도의 진폭을 랜덤으로 설정 (출렁임의 높이)
            this.frequency = 0.004 + (Math.random() * 0.002);  // 파도의 주기 (파도의 너비 조정)
            this.phase = Math.random() * Math.PI * 2;  // 파도의 위상 (파도 시작점의 위치)
            this.speed = 0.01 + Math.random() * 0.02;  // 파도의 이동 속도

            // 첫 번째 파도의 색상을 #f89671로 고정
            if (this.index === 0) {
                this.color = '#f89671';
            } else {
                this.color = colors[index % colors.length];  // 다른 파도는 색상 팔레트에서 순서대로 가져옴
            }
        }

        // 파도를 그리는 메서드
        draw() {
            ctx.beginPath();  // 새로운 경로 시작
            ctx.moveTo(0, canvas.height / 2);  // 파도의 시작 위치를 화면 중앙으로 이동

            // 파도의 형태를 그리기 위한 반복문
            for (let x = 0; x < canvas.width; x++) {
                // 진폭을 일정하게 유지하기 위해 easing 함수 제거
                // let progress = x / canvas.width;
                // let easing = easeInOutSine(progress);

                // y 좌표 계산 (진폭을 일정하게 유지)
                let y = canvas.height / 2 + Math.sin(x * this.frequency + this.phase) * this.amplitude;
                ctx.lineTo(x, y);  // 각 x 값에 대한 y 좌표로 라인을 그림
            }

            // 파도의 아래쪽을 캔버스 끝까지 채우기 위한 경로 닫기
            ctx.lineTo(canvas.width, canvas.height);
            ctx.lineTo(0, canvas.height);
            ctx.closePath();
            ctx.fillStyle = this.color;  // 파도의 색상을 설정
            ctx.fill();  // 파도 채우기
        }

        // 파도의 움직임을 업데이트하는 메서드
        update() {
            this.phase += this.speed;  // 파도의 위상(시작점)을 이동시켜 움직임을 표현
        }
    }

    // 파도 배열 초기화
    function initWaves() {
        waves = [];  // 배열 초기화
        for (let i = 0; i < waveCount; i++) {
            waves.push(new Wave(i));  // waveCount에 따라 파도를 생성하여 배열에 추가
        }
    }

    // 애니메이션을 실행하는 함수
    function animate() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);  // 매 프레임마다 캔버스를 지움

        // 색상 혼합 모드를 설정하여 겹치는 부분에서 색상이 섞이도록 함
        ctx.globalCompositeOperation = 'overlay';  // 파도가 겹칠 때 색상이 섞이도록 설정

        // 각 파도 업데이트 및 그리기
        waves.forEach(wave => {
            wave.update();  // 파도 위치 업데이트
            wave.draw();  // 파도 그리기
        });

        requestAnimationFrame(animate);  // 애니메이션을 지속적으로 실행
    }

    initWaves();  // 파도 초기화
    animate();  // 애니메이션 시작
}
