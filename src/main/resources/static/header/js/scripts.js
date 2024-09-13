/*!
* Start Bootstrap - Agency v7.0.12 (https://startbootstrap.com/theme/agency)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-agency/blob/master/LICENSE)
*/
//
// Scripts
//

window.addEventListener('DOMContentLoaded', event => {

    // Navbar shrink function
    var navbarShrink = function () {
        const navbarCollapsible = document.body.querySelector('#mainNav');
        if (!navbarCollapsible) {
            return;
        }
        if (window.scrollY === 0) {
            navbarCollapsible.classList.remove('navbar-shrink')
        } else {
            navbarCollapsible.classList.add('navbar-shrink')
        }


    };

    // Shrink the navbar
    navbarShrink();

    // Shrink the navbar when page is scrolled
    document.addEventListener('scroll', navbarShrink);

    //  Activate Bootstrap scrollspy on the main nav element
    const mainNav = document.body.querySelector('#mainNav');
    if (mainNav) {
        new bootstrap.ScrollSpy(document.body, {
            target: '#mainNav',
            rootMargin: '0px 0px -40%',
        });
    };

    // Collapse responsive navbar when toggler is visible
    const navbarToggler = document.body.querySelector('.navbar-toggler');
    const responsiveNavItems = [].slice.call(
        document.querySelectorAll('#navbarResponsive .nav-link')
    );
    responsiveNavItems.map(function (responsiveNavItem) {
        responsiveNavItem.addEventListener('click', () => {
            if (window.getComputedStyle(navbarToggler).display !== 'none') {
                navbarToggler.click();
            }
        });
    });


});

document.addEventListener("DOMContentLoaded", function() {
    var navbar = document.querySelector('.navbar'); // 헤더를 선택합니다.

    window.addEventListener('scroll', function() {
        if (window.scrollY > 50) { // 스크롤이 50px 이상일 때
            navbar.style.backgroundColor = '#b7b7d8'; // 연보라색으로 변경
        } else {
            navbar.style.backgroundColor = '#9982a1'; // 기본 진청색으로 복원
        }
    });
});

// 이미지 슬라이더 부분
document.addEventListener("DOMContentLoaded", function() {
    const track = document.querySelector('.image-track');
    let isPaused = false;

    function resetSlider() {
        if (!isPaused) {
            track.appendChild(track.firstElementChild); // 첫 번째 이미지를 마지막에 추가하여 무한 반복 효과
            track.style.transition = 'none'; // 애니메이션 끊김 방지
            track.style.transform = 'translateX(0)'; // 위치 초기화
            requestAnimationFrame(() => {
                track.style.transition = 'transform 10s linear'; // 부드러운 애니메이션 재시작
                track.style.transform = 'translateX(-100%)'; // 트랙을 다시 이동
            });
        }
    }

    track.addEventListener('transitionend', resetSlider); // 애니메이션 끝날 때 호출

    track.style.transform = 'translateX(-100%)'; // 초기 슬라이드 시작
});


// 둘러보기 섹션 슬라이더 기능
document.addEventListener('DOMContentLoaded', function () {
    const slides = document.querySelectorAll('.portfolio-item'); // 모든 슬라이드 아이템 선택
    const prevBtn = document.getElementById('prevBtn'); // 이전 버튼 선택
    const nextBtn = document.getElementById('nextBtn'); // 다음 버튼 선택
    const slidesToShow = 3; // 한 번에 보여줄 슬라이드 수
    let currentIndex = 0; // 현재 슬라이드 인덱스

    function updateSliderPosition() {
        slides.forEach((slide, index) => {
            slide.style.display = 'none'; // 모든 슬라이드 숨기기
        });

        // 현재 인덱스부터 보여줄 슬라이드 수만큼만 보이도록 설정
        for (let i = currentIndex; i < currentIndex + slidesToShow; i++) {
            if (slides[i]) {
                slides[i].style.display = 'block';
            }
        }
    }

    // 이전 버튼 클릭 이벤트
    prevBtn.addEventListener('click', () => {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = slides.length - slidesToShow; // 첫 번째 슬라이드로 돌아가기
        }
        updateSliderPosition();
    });

    // 다음 버튼 클릭 이벤트
    nextBtn.addEventListener('click', () => {
        if (currentIndex < slides.length - slidesToShow) {
            currentIndex++;
        } else {
            currentIndex = 0; // 첫 번째 슬라이드로 돌아가기
        }
        updateSliderPosition();
    });

    updateSliderPosition(); // 초기 슬라이드 위치 설정
});



// 메인페이지 wave 부분
document.addEventListener('DOMContentLoaded', function() {
    const waveContainer = document.getElementById('waveContainer');

    // 파도 색상 배열에서 첫 번째 색상을 보라색이나 진청색으로 변경
    const colors = ['#dd43c0', '#ffffff', '#072153', '#f33f50', '#9982a1']; // 파도 색상 배열
    const durations = [5, 8, 6, 3, 13]; // 각 파도 애니메이션 지속 시간

    // SVG 파도 생성 함수
    function createWave(color, duration, index) {
        const svgNS = "http://www.w3.org/2000/svg";
        const svg = document.createElementNS(svgNS, "svg");
        svg.setAttribute("class", `wave wave${index + 1}`);
        svg.setAttribute("viewBox", "0 0 1440 320");
        svg.setAttribute("xmlns", svgNS);

        const path = document.createElementNS(svgNS, "path");
        path.setAttribute("fill", color);
        path.setAttribute("fill-opacity", 0.5);
        path.setAttribute("d", "M0,224L48,197.3C96,171,192,117,288,128C384,139,480,213,576,229.3C672,245,768,203,864,202.7C960,203,1056,245,1152,245.3C1248,245,1344,203,1392,181.3L1440,160L1440,320L0,320Z");

        const animate = document.createElementNS(svgNS, "animate");
        animate.setAttribute("attributeName", "d");
        animate.setAttribute("dur", `${duration}s`);
        animate.setAttribute("repeatCount", "indefinite");
        animate.setAttribute("values", `
            M0,224L48,197.3C96,171,192,117,288,128C384,139,480,213,576,229.3C672,245,768,203,864,202.7C960,203,1056,245,1152,245.3C1248,245,1344,203,1392,181.3L1440,160L1440,320L0,320Z;
            M0,220L48,218.7C96,213,192,171,288,154.7C384,139,480,149,576,160C672,171,768,245,864,272C960,299,1056,277,1152,245.3C1248,213,1344,171,1392,149.3L1440,128L1440,320L0,320Z;
            M0,224L48,197.3C96,171,192,117,288,128C384,139,480,213,576,229.3C672,245,768,203,864,202.7C960,203,1056,245,1152,245.3C1248,245,1344,203,1392,181.3L1440,160L1440,320L0,320Z;
            M0,224L48,197.3C96,171,192,117,288,128C384,139,480,213,576,229.3C672,245,768,203,864,202.7C960,203,1056,245,1152,245.3C1248,245,1344,203,1392,181.3L1440,160L1440,320L0,320Z

        `);

        path.appendChild(animate);
        svg.appendChild(path);
        return svg;
    }

    // 각 색상과 지속 시간으로 SVG 파도 생성
    colors.forEach((color, index) => {
        const wave = createWave(color, durations[index], index);
        waveContainer.appendChild(wave);
    });

    // 파도를 Shary의 중심에 맞춰 위치시키기 위한 스타일 조정
    const waveElements = document.querySelectorAll('.wave');
    waveElements.forEach(wave => {
        wave.style.transform = 'translateY(-75%)'; // 파도의 수직 위치를 Shary 텍스트 중심에 맞춤
    });
});











