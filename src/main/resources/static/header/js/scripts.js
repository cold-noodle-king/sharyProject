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


// 둘러보기 모달 이동 스크립트
document.addEventListener('DOMContentLoaded', function () {
    const track = document.querySelector('.carousel-track');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const itemsToShow = 1; // 한 번에 보이는 아이템 수
    const itemWidth = track.children[0].offsetWidth; // 하나의 아이템의 너비
    let currentIndex = 0; // 현재 슬라이드 인덱스

    // 슬라이더 위치 업데이트
    function updateSliderPosition() {
        const moveDistance = -(itemWidth * currentIndex);
        track.style.transform = `translateX(${moveDistance}px)`;
    }

    // 이전 버튼 클릭 이벤트
    prevBtn.addEventListener('click', () => {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = track.children.length - itemsToShow;
        }
        updateSliderPosition();
    });

    // 다음 버튼 클릭 이벤트
    nextBtn.addEventListener('click', () => {
        if (currentIndex < track.children.length - itemsToShow) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        updateSliderPosition();
    });

    updateSliderPosition(); // 초기 슬라이드 위치 설정
});






