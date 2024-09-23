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
    let waveCount = 5;  // 물결의 수를 늘려 부드럽게
    let colors = ['#ff6f61', '#9052f8', '#7ec4c5', '#f8e1d4', '#242741'];

    class Wave {
        constructor(index) {
            this.index = index;
            this.amplitude = 20 + (Math.random() * 60); // 물결의 높이를 조절
            this.frequency = 0.001 + (Math.random() * 0.003); // 물결의 빈도를 조정하여 더 부드럽게
            this.phase = Math.random() * Math.PI * 2;
            this.color = colors[index % colors.length];
        }

        draw() {
            ctx.beginPath();
            ctx.moveTo(0, canvas.height / 2);
            for (let x = 0; x < canvas.width; x++) {
                let y = canvas.height / 2 + Math.sin(x * this.frequency + this.phase) * this.amplitude;
                ctx.lineTo(x, y);
            }
            ctx.lineTo(canvas.width, canvas.height);
            ctx.lineTo(0, canvas.height);
            ctx.closePath();
            ctx.fillStyle = this.color;
            ctx.fill();
        }

        update() {
            this.phase += 0.015;  // 애니메이션 속도를 부드럽게
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

        waves.forEach(wave => {
            wave.update();
            wave.draw();
        });

        requestAnimationFrame(animate);
    }

    initWaves();
    animate();
}
