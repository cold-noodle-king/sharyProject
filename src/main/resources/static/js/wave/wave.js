window.onload = function() {
    const canvas = document.getElementById('waveCanvas');
    const ctx = canvas.getContext('2d');

    function resizeCanvas() {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    }
    resizeCanvas();
    window.addEventListener('resize', resizeCanvas);

    let waves = [];
    let waveCount = 6;  // 여러 파도층
    let colors = ['#ff6f61', '#9052f8', '#7ec4c5', '#f8e1d4', '#ffdf64', '#00b3b3'];

    // Easing 함수 적용
    function easeInOutSine(t) {
        return -(Math.cos(Math.PI * t) - 1) / 2;
    }

    // 파도 클래스
    class Wave {
        constructor(index) {
            this.index = index;
            this.amplitude = 50 + (Math.random() * 100);  // 진폭을 더 극적으로 조정
            this.frequency = 0.002 + (Math.random() * 0.005);  // 주기 더 다양하게
            this.phase = Math.random() * Math.PI * 2;
            this.speed = 0.01 + Math.random() * 0.02;  // 속도 다양화

            // 첫 번째 파도의 색상을 #242741로 고정
            if (this.index === 0) {
                this.color = '#242741';
            } else {
                this.color = colors[index % colors.length];
            }
        }

        draw() {
            ctx.beginPath();
            ctx.moveTo(0, canvas.height / 2);

            for (let x = 0; x < canvas.width; x++) {
                let progress = x / canvas.width;
                let easing = easeInOutSine(progress);  // easing 함수 적용
                let y = canvas.height / 2 + Math.sin(x * this.frequency + this.phase) * this.amplitude * easing;
                ctx.lineTo(x, y);
            }

            ctx.lineTo(canvas.width, canvas.height);
            ctx.lineTo(0, canvas.height);
            ctx.closePath();
            ctx.fillStyle = this.color;
            ctx.fill();
        }

        update() {
            this.phase += this.speed;
        }
    }

    function initWaves() {
        waves = [];
        for (let i = 0; i < waveCount; i++) {
            waves.push(new Wave(i));
        }
    }

    function animate() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // 색상 혼합 모드를 적용하여 겹치는 부분에서 색상이 섞이도록 설정
        ctx.globalCompositeOperation = 'overlay';  // 색상 혼합 모드

        waves.forEach(wave => {
            wave.update();
            wave.draw();
        });

        requestAnimationFrame(animate);
    }

    initWaves();
    animate();
}
