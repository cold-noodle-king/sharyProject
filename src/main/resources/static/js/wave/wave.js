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

    // 감정별 색상 팔레트 정의
    const colorPalettes = [
        {   // 행복 (Happiness) - 색상 팔레트
            name: '행복',
            colors: ['#df9b70', '#f69d41', '#fad366', '#FFD1A9', '#FFB6C1', '#FFEBCD'],
            firstWaveColor: '#eaa087',
            backgroundColor: '#f2d7d2'  // 행복의 배경색
        },
        {   // 우울 (Sadness) - 색상 팔레트
            name: '우울',
            colors: ['#70a1df', '#419df6', '#66d3fa', '#a9d1ff', '#b6c1ff', '#ebcdff'],
            firstWaveColor: '#70a1df',
            backgroundColor: '#d2e9f2'  // 우울의 배경색
        },
        {   // 화남 (Anger) - 연한 붉은 색상 조합
            name: '화남',
            colors: ['#ffcccc', '#ffb3a0', '#ff9980', '#ff8066', '#ff6666', '#ff9966'],
            firstWaveColor: '#ffcccc',
            backgroundColor: '#f2d2d2'  // 화남의 배경색
        },
        {   // 놀람 (Surprise) - 여러가지 색상 계열
            name: '놀람',
            colors: ['#ec1b2c', '#ffffff', '#d1a6e8', '#ffef93', '#FFB6C1', '#FFEBCD'],
            firstWaveColor: '#0d1a5e',
            backgroundColor: '#f2f2d2'  // 놀람의 배경색
        },
        {   // 두려움 (Fear) - 연한 보라색 계열
            name: '두려움',
            colors: ['#e6ccff', '#d1b3ff', '#bc99ff', '#a680ff', '#9966ff', '#8c4dff'],
            firstWaveColor: '#e6ccff',
            backgroundColor: '#e9d2f2'  // 두려움의 배경색
        },
        {   // 사랑 (Love) - 연한 핑크색 계열
            name: '사랑',
            colors: ['#ffe6f2', '#ffccdd', '#ffb3c9', '#ff99b5', '#ff80a1', '#ff668c'],
            firstWaveColor: '#ffe6f2',
            backgroundColor: '#f2d2e3'  // 사랑의 배경색
        }
    ];

    let currentPaletteIndex = 0;  // 현재 색상 팔레트의 인덱스
    let colors = colorPalettes[currentPaletteIndex].colors;  // 초기 색상 팔레트 설정

    // HEX 색상을 RGB 객체로 변환하는 함수
    function hexToRgb(hex) {
        hex = hex.replace('#', '');
        if (hex.length === 3) {
            hex = hex.split('').map(char => char + char).join('');
        }
        const bigint = parseInt(hex, 16);
        const r = (bigint >> 16) & 255;
        const g = (bigint >> 8) & 255;
        const b = bigint & 255;
        return { r, g, b };
    }

    // 두 색상 사이를 보간하는 함수
    function interpolateColor(color1, color2, factor) {
        const result = {};
        result.r = Math.round(color1.r + (color2.r - color1.r) * factor);
        result.g = Math.round(color1.g + (color2.g - color1.g) * factor);
        result.b = Math.round(color1.b + (color2.b - color1.b) * factor);
        return result;
    }

    // RGB 객체를 HEX 문자열로 변환하는 함수
    function rgbToHex(rgb) {
        const r = rgb.r.toString(16).padStart(2, '0');
        const g = rgb.g.toString(16).padStart(2, '0');
        const b = rgb.b.toString(16).padStart(2, '0');
        return `#${r}${g}${b}`;
    }

    // 파도 클래스를 정의하여 각 파도에 대한 설정과 동작을 관리
    class Wave {
        constructor(index) {
            this.index = index;  // 파도의 인덱스 값
            this.amplitude = 50 + (Math.random() * 50);  // 파도의 진폭을 랜덤으로 설정
            this.baseAmplitude = this.amplitude;  // 진폭의 기본값 저장
            this.frequency = 0.004 + (Math.random() * 0.002);  // 파도의 주기
            this.phase = Math.random() * Math.PI * 2;  // 파도의 위상
            this.speed = 0.01 + Math.random() * 0.02;  // 파도의 이동 속도

            // 수직 움직임을 위한 변수들 추가
            this.verticalPhase = Math.random() * Math.PI * 2;  // 수직 움직임의 위상
            this.verticalSpeed = 0.005 + Math.random() * 0.01;  // 수직 움직임 속도
            this.verticalAmplitude = 10 + Math.random() * 10;  // 수직 움직임 진폭

            // 색상 전환을 위한 속성 추가
            this.color = null;           // 현재 색상 (HEX 문자열)
            this.currentColor = null;    // 현재 색상 (RGB 객체)
            this.targetColor = null;     // 목표 색상 (RGB 객체)
            this.transitionProgress = 1; // 색상 전환 진행도 (0 ~ 1)

            // 초기 색상 설정
            this.setColor();
        }

        // 초기 색상 설정 메서드
        setColor() {
            if (this.index === 0) {
                this.colorHex = colorPalettes[currentPaletteIndex].firstWaveColor;
            } else {
                this.colorHex = colors[this.index % colors.length];
            }
            this.currentColor = hexToRgb(this.colorHex);
            this.targetColor = this.currentColor;
            this.color = this.colorHex;
            this.transitionProgress = 1;
        }

        // 색상 전환 시작 메서드
        updateColor() {
            // 현재 색상을 시작 색상으로 설정
            this.currentColor = hexToRgb(this.color);
            // 목표 색상을 새로운 팔레트의 색상으로 설정
            if (this.index === 0) {
                this.colorHex = colorPalettes[currentPaletteIndex].firstWaveColor;
            } else {
                this.colorHex = colors[this.index % colors.length];
            }
            this.targetColor = hexToRgb(this.colorHex);
            // 전환 진행도 초기화
            this.transitionProgress = 0;
        }

        // 파도의 움직임과 색상을 업데이트하는 메서드
        update() {
            this.phase += this.speed;  // 파도의 위상 이동으로 수평 움직임 표현
            this.verticalPhase += this.verticalSpeed;  // 수직 움직임의 위상 업데이트

            // 진폭을 시간에 따라 변하게 하여 랜덤한 흔들림 효과 추가
            this.amplitude = this.baseAmplitude + Math.sin(this.phase * 2) * (this.baseAmplitude * 0.2);

            // 색상 전환 진행
            if (this.transitionProgress < 1) {
                this.transitionProgress += 0.02;  // 전환 속도 조절
                if (this.transitionProgress > 1) {
                    this.transitionProgress = 1;
                }
                const interpolatedColor = interpolateColor(this.currentColor, this.targetColor, this.transitionProgress);
                this.color = rgbToHex(interpolatedColor);
            } else {
                this.color = this.colorHex;
            }
        }

        // 파도를 그리는 메서드
        draw() {
            ctx.beginPath();  // 새로운 경로 시작

            // 수직 중심선을 계산 (위아래로 움직임)
            let centerY = canvas.height / 2 + Math.sin(this.verticalPhase) * this.verticalAmplitude;

            ctx.moveTo(0, centerY);  // 파도의 시작 위치를 수직 중심선으로 이동

            // 파도의 형태를 그리기 위한 반복문
            for (let x = 0; x <= canvas.width; x++) {
                // y 좌표 계산 (진폭을 시간에 따라 변경)
                let y = centerY + Math.sin(x * this.frequency + this.phase) * this.amplitude;
                ctx.lineTo(x, y);  // 각 x 값에 대한 y 좌표로 라인을 그림
            }

            // 파도의 아래쪽을 캔버스 끝까지 채우기 위한 경로 닫기
            ctx.lineTo(canvas.width, canvas.height);
            ctx.lineTo(0, canvas.height);
            ctx.closePath();
            ctx.fillStyle = this.color;  // 파도의 색상을 설정
            ctx.fill();  // 파도 채우기
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
            wave.update();  // 파도 위치 및 색상 업데이트
            wave.draw();    // 파도 그리기
        });

        requestAnimationFrame(animate);  // 애니메이션을 지속적으로 실행
    }

    initWaves();  // 파도 초기화
    animate();    // 애니메이션 시작

    // "Shary" 요소 가져오기
    const sharyText = document.getElementById('sharyText');

    // ".wave-section" 요소 가져오기
    const waveSection = document.querySelector('.wave-section');

    // 초기 배경색 설정
    waveSection.style.backgroundColor = colorPalettes[currentPaletteIndex].backgroundColor;

    // "Shary" 클릭 이벤트 리스너 추가
    sharyText.addEventListener('click', function() {
        // 다음 색상 팔레트로 이동
        currentPaletteIndex = (currentPaletteIndex + 1) % colorPalettes.length;
        colors = colorPalettes[currentPaletteIndex].colors;

        // 각 파도의 색상 전환 시작
        waves.forEach(wave => {
            wave.updateColor();
        });

        // 배경색 업데이트
        waveSection.style.backgroundColor = colorPalettes[currentPaletteIndex].backgroundColor;
    });
};
