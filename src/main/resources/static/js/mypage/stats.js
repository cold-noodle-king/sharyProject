$(document).ready(function() {
    // 페이지 로드 시 AJAX 요청으로 해시태그 및 다이어리 통계 데이터를 가져옴
    $.ajax({
        url: '/mypage/api/stats',  // 백엔드에서 데이터를 가져옴
        method: 'GET',
        success: function(data) {
            console.log('Data received:', data);  // 전체 데이터 로그

            // 해시태그 데이터가 존재하는지 확인
            if (data['해시태그'] && data['해시태그']['일상']) {
                generateWordCloud('wordCloudCanvas1', data['해시태그']['일상']);
                console.log('일상 해시태그 데이터:', data['해시태그']['일상']);
            } else {
                console.log('일상 해시태그 데이터가 없습니다.');
            }

            // 다이어리 데이터가 존재하는지 확인
            if (data['다이어리']) {
                renderDiaryChart(data['다이어리']);
                console.log('다이어리 데이터:', data['다이어리']);  // 다이어리 데이터 로그
            } else {
                console.log('다이어리 데이터가 없습니다.');
            }

            // 해시태그 카테고리 탭 전환 시 워드 클라우드를 렌더링
            $('a[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
                const targetTabId = $(e.target).attr('href');  // href 속성값을 가져옴 (#category1 등)
                const chartId = targetTabId.replace('#category', 'wordCloudCanvas');  // category1 -> wordCloudCanvas1
                const category = $(e.target).text().trim();  // 탭의 텍스트 (카테고리 이름)
                console.log('Switched to category:', category);  // 선택된 카테고리 로그

                // 선택된 카테고리의 해시태그 데이터를 가져옴
                if (data['해시태그'] && data['해시태그'][category]) {
                    console.log(`${category} 데이터:`, data['해시태그'][category]);
                    generateWordCloud(chartId, data['해시태그'][category]);  // 해당 카테고리의 워드 클라우드 렌더링
                } else {
                    console.log(`${category} 해시태그 데이터가 없습니다.`);
                }
            });

            // 다이어리 탭 전환 시 다이어리 차트를 렌더링
            $('a[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
                const targetTabId = $(e.target).attr('href');
                if (targetTabId === '#diary') {
                    if (data['다이어리']) {
                        renderDiaryChart(data['다이어리']);
                    } else {
                        console.log('다이어리 데이터가 없습니다.');
                    }
                }
            });
        },
        error: function(error) {
            console.log('Error fetching data:', error);  // 오류 발생 시 로그 출력
        }
    });

    // 워드 클라우드를 생성하는 함수
    function generateWordCloud(canvasId, hashtagData) {
        const canvas = document.getElementById(canvasId);

        if (!canvas) {
            console.error(`Canvas with id ${canvasId} not found.`);
            return;
        }

        const words = Object.entries(hashtagData).map(([word, count]) => [word, count * 10]);

        WordCloud(canvas, {
            list: words,
            gridSize: 12,
            weightFactor: 2,
            fontFamily: 'Arial, sans-serif',
            color: 'random-light',
            backgroundColor: '#fff',
            rotateRatio: 0.5,
            rotationSteps: 2,
        });
    }

    // 다이어리 통계 차트를 생성하는 함수 (Chart.js)
    function renderDiaryChart(diaryData) {
        const ctx = document.getElementById('diaryChart').getContext('2d');

        // 다이어리 카테고리 이름 배열
        const categories = Object.keys(diaryData);
        const counts = Object.values(diaryData);  // 카테고리별 다이어리 수

        // 기존 차트가 있다면 제거
        if (window.diaryChart && typeof window.diaryChart.destroy === 'function') {
            window.diaryChart.destroy();
        }

        // 새로운 차트를 생성하여 window.diaryChart에 저장
        window.diaryChart = new Chart(ctx, {
            type: 'bar',  // 막대 차트
            data: {
                labels: categories,
                datasets: [{
                    label: '카테고리별 다이어리 수',
                    data: counts,
                    backgroundColor: [
                        'rgba(75, 192, 192, 0.5)',
                        'rgba(255, 99, 132, 0.5)',
                        'rgba(255, 205, 86, 0.5)',
                        'rgba(54, 162, 235, 0.5)',
                        'rgba(153, 102, 255, 0.5)',
                        'rgba(201, 203, 207, 0.5)'
                    ],
                    borderColor: [
                        'rgba(75, 192, 192, 1)',
                        'rgba(255, 99, 132, 1)',
                        'rgba(255, 205, 86, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(201, 203, 207, 1)'
                    ],
                    borderWidth: 2  // 테두리 두께 설정
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,  // 차트가 화면 크기에 맞게 조정되도록 설정
                scales: {
                    y: {
                        beginAtZero: true,  // y축 0부터 시작
                        ticks: {
                            color: '#333',  // y축 텍스트 색상
                            font: {
                                size: 14  // 폰트 크기
                            }
                        }
                    },
                    x: {
                        ticks: {
                            color: '#333',  // x축 텍스트 색상
                            font: {
                                size: 14  // 폰트 크기
                            }
                        }
                    }
                },
                plugins: {
                    legend: {
                        labels: {
                            font: {
                                size: 16  // 범례 폰트 크기
                            }
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.7)',
                        titleFont: {
                            size: 14
                        },
                        bodyFont: {
                            size: 12
                        },
                        cornerRadius: 4  // 툴팁 모서리 둥글게 처리
                    }
                }
            }
        });
    }
});
