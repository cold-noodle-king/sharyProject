$(document).ready(function() {
    // 페이지 로드 시 AJAX 요청으로 해시태그 통계 데이터를 가져옴
    $.ajax({
        url: '/mypage/api/stats',
        method: 'GET',
        success: function(data) {
            console.log('Data received:', data);  // 전체 데이터 로그

            // 기본 탭(일상)에 대한 차트를 렌더링
            renderHashtagChart('hashtagChart1', data['일상']);
            console.log('일상 데이터:', data['일상']);  // 일상 데이터 로그

            // 탭 전환 시 해당 카테고리의 차트를 렌더링
            $('a[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
                const targetTabId = $(e.target).attr('href');  // href 속성값을 가져옴 (#category1 등)
                const chartId = targetTabId.replace('#category', 'hashtagChart');  // category1 -> hashtagChart1
                const category = $(e.target).text().trim();  // 탭의 텍스트 (카테고리 이름)
                console.log('Switched to category:', category);  // 선택된 카테고리 로그

                // 선택된 카테고리의 데이터를 가져옴
                if (data[category]) {
                    console.log(`${category} 데이터:`, data[category]);  // 카테고리 데이터 로그
                    renderHashtagChart(chartId, data[category]);  // 해당 카테고리의 차트 렌더링
                } else {
                    console.log(`${category} 데이터가 없습니다.`);
                }
            });
        },
        error: function(error) {
            console.log('Error fetching data:', error);  // 오류 발생 시 로그 출력
        }
    });

    // 차트를 렌더링하는 함수
    function renderHashtagChart(chartId, hashtagData) {
        const canvas = document.getElementById(chartId);

        // canvas가 존재하는지 먼저 확인
        if (!canvas) {
            console.error(`Canvas with id ${chartId} not found.`);
            return;  // canvas가 존재하지 않으면 차트를 렌더링하지 않음
        }

        const ctx = canvas.getContext('2d');
        const hashtags = Object.keys(hashtagData);  // 해시태그 이름 배열
        const counts = Object.values(hashtagData);  // 해시태그 사용 횟수 배열

        // 차트가 이미 존재하고, destroy 메서드가 존재하는 경우 제거
        if (window[chartId] && typeof window[chartId].destroy === 'function') {
            window[chartId].destroy();  // 기존 차트 객체 삭제
        }

        // 새로운 차트 생성 및 window 객체에 저장
        window[chartId] = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: hashtags,
                datasets: [{
                    label: '해시태그 사용 횟수',
                    data: counts,
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,  // 반응형 차트
                scales: {
                    y: {
                        beginAtZero: true  // Y축 0부터 시작
                    }
                }
            }
        });
    }
});
