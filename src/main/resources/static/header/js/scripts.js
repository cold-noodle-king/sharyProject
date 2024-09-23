/*!
* Start Bootstrap - Agency v7.0.12 (https://startbootstrap.com/theme/agency)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-agency/blob/master/LICENSE)
*/
//
// Scripts
//

// DOMContentLoaded 이벤트가 발생하면, 즉 페이지의 DOM 요소가 모두 로드된 후에 실행될 코드를 설정
window.addEventListener('DOMContentLoaded', event => {





    // 네비게이션 바의 크기를 줄이는 함수 정의
    var navbarShrink = function () {
        // 페이지에서 id가 'mainNav'인 네비게이션 요소를 가져옴
        const navbarCollapsible = document.body.querySelector('#mainNav');

        // 만약 'mainNav' 요소가 없으면, 함수를 종료 (보호 코드)
        if (!navbarCollapsible) {
            return;
        }

        // 페이지 스크롤 위치가 맨 위에 있을 경우, 배경색을 연보라색으로 고정
        if (window.scrollY === 0) {
            navbarCollapsible.classList.remove('navbar-shrink');
            navbarCollapsible.style.backgroundColor = '#ce8ece';  // 연보라색 배경색 설정
        } else {
            navbarCollapsible.classList.add('navbar-shrink');
            navbarCollapsible.style.backgroundColor = '#ce8ece';  // 스크롤이 내려가도 연보라색 유지
        }
    };

    // 페이지 로드 후 바로 네비게이션 바 크기 및 배경 색상을 조정
    navbarShrink();

    // 스크롤 이벤트에서 배경색 변경을 제거함으로써 배경색 고정
    document.removeEventListener('scroll', navbarShrink);

    // 부트스트랩의 ScrollSpy 활성화: 사용자가 스크롤할 때 내비게이션 바에서 해당 위치의 항목이 자동으로 활성화됨
    const mainNav = document.body.querySelector('#mainNav');
    if (mainNav) {
        new bootstrap.ScrollSpy(document.body, {
            target: '#mainNav',  // 스크롤 스파이의 타겟이 될 네비게이션 바 요소
            rootMargin: '0px 0px -40%',  // 스크롤이 트리거되는 여백 설정
        });
    }

    // 반응형 네비게이션 바에서 메뉴 항목 클릭 시, 네비게이션 바를 자동으로 닫음
    const navbarToggler = document.body.querySelector('.navbar-toggler');  // 토글 버튼(햄버거 메뉴) 요소 가져오기
    const responsiveNavItems = [].slice.call(
        document.querySelectorAll('#navbarResponsive .nav-link')  // 모든 네비게이션 링크 가져오기
    );

    // 각 네비게이션 링크 클릭 시 실행할 이벤트 설정
    responsiveNavItems.map(function (responsiveNavItem) {
        responsiveNavItem.addEventListener('click', () => {
            // 토글 버튼이 보이는 경우 (반응형 디자인에서만 작동)
            if (window.getComputedStyle(navbarToggler).display !== 'none') {
                navbarToggler.click();  // 클릭하여 네비게이션 바를 닫음
            }
        });
    });

});

// 기본 헤더 색상을 연보라색으로 고정하는 함수 추가
document.addEventListener("DOMContentLoaded", function() {
    var navbar = document.querySelector('.navbar'); // 헤더를 선택합니다.

    // 기본 배경색을 연보라색으로 고정
    navbar.style.backgroundColor = '#ce8ece'; // 연보라색으로 고정
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
    const colors = ['#ce8ece', '#ffffff', '#072153', '#f33f50', '#9982a1']; // 파도 색상 배열
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



