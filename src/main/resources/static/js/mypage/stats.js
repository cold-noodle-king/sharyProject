$(document).ready(function() {
    // 페이지 로드 시 AJAX 요청으로 해시태그, 다이어리, 좋아요 랭킹 통계 데이터를 가져옴
    $.ajax({
        url: '/mypage/api/stats',  // 백엔드에서 통계 데이터를 가져오는 URL
        method: 'GET',
        success: function(data) {
            console.log('Data received:', data);  // 수신된 데이터 로그 출력

            // 1. 해시태그 데이터가 존재하는지 확인하고 초기 워드 클라우드 렌더링
            if (data['해시태그'] && data['해시태그']['일상']) {
                generateWordCloud('wordCloudCanvas1', data['해시태그']['일상']);  // 일상 카테고리 워드 클라우드 렌더링
                console.log('일상 해시태그 데이터:', data['해시태그']['일상']);
            } else {
                console.log('일상 해시태그 데이터가 없습니다.');
            }

            // 2. 다이어리 데이터가 존재하는지 확인하고 초기 차트 렌더링
            if (data['다이어리']) {
                renderDiaryChart(data['다이어리']);  // 다이어리 차트 렌더링
                console.log('다이어리 데이터:', data['다이어리']);
            } else {
                console.log('다이어리 데이터가 없습니다.');
            }

            // 3. 좋아요 랭킹 데이터가 존재하는지 확인하고 초기 렌더링
            if (data['좋아요랭킹'] && data['좋아요랭킹'].length > 0) {
                renderTopLikedNotesChart(data['좋아요랭킹']);  // 좋아요 랭킹 렌더링
                console.log('좋아요 랭킹 데이터:', data['좋아요랭킹']);
            } else {
                console.log('좋아요 랭킹 데이터가 없습니다.');
            }

            // 4. 탭 전환 시 이벤트 처리 (하나의 이벤트 핸들러로 통합)
            $('a[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
                const targetTabId = $(e.target).attr('href');  // 탭의 href 속성 가져오기
                const tabId = targetTabId.substring(1);  // '#' 제거

                if (tabId.startsWith('category')) {
                    const categoryIndex = parseInt(tabId.replace('category', '')) - 1;  // 카테고리 인덱스 계산
                    const categoryNames = ['일상', '여행', '육아', '연애', '취미', '운동'];
                    const categoryName = categoryNames[categoryIndex];  // 카테고리 이름 가져오기
                    const canvasId = 'wordCloudCanvas' + (categoryIndex + 1);  // 해당 워드 클라우드 캔버스 ID

                    console.log('Switched to category:', categoryName);

                    // 선택된 카테고리의 해시태그 데이터가 존재하는지 확인하고, 워드 클라우드 렌더링
                    if (data['해시태그'] && data['해시태그'][categoryName]) {
                        console.log(`${categoryName} 데이터:`, data['해시태그'][categoryName]);
                        generateWordCloud(canvasId, data['해시태그'][categoryName]);  // 워드 클라우드 렌더링
                    } else {
                        console.log(`${categoryName} 해시태그 데이터가 없습니다.`);
                    }
                } else if (tabId === 'diary') {
                    if (data['다이어리']) {
                        renderDiaryChart(data['다이어리']);  // 다이어리 차트 렌더링
                    } else {
                        console.log('다이어리 데이터가 없습니다.');
                    }
                } else if (tabId === 'likes') {
                    if (data['좋아요랭킹'] && data['좋아요랭킹'].length > 0) {
                        renderTopLikedNotesChart(data['좋아요랭킹']);  // 좋아요 랭킹 렌더링
                    } else {
                        console.log('좋아요 랭킹 데이터가 없습니다.');
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
            console.error(`Canvas with id ${canvasId} not found.`);  // 해당 id를 가진 canvas가 없으면 오류 출력
            return;
        }

        const words = Object.entries(hashtagData).map(([word, count]) => [word, count]);  // 해시태그 데이터를 워드 클라우드 형식으로 변환

        WordCloud(canvas, {
            list: words,
            gridSize: 12,
            weightFactor: 20,
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

        const categories = Object.keys(diaryData);  // 다이어리 카테고리 이름 배열
        const counts = Object.values(diaryData);  // 카테고리별 다이어리 수

        if (window.diaryChart && typeof window.diaryChart.destroy === 'function') {
            window.diaryChart.destroy();  // 기존 차트가 있으면 제거
        }

        // 새로운 차트를 생성
        window.diaryChart = new Chart(ctx, {
            type: 'bar',  // 막대 차트 형식
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
                        beginAtZero: true,  // y축이 0부터 시작하도록 설정
                        ticks: {
                            color: '#333',  // y축 텍스트 색상
                            font: {
                                size: 14  // y축 폰트 크기
                            }
                        }
                    },
                    x: {
                        ticks: {
                            color: '#333',  // x축 텍스트 색상
                            font: {
                                size: 14  // x축 폰트 크기
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

    // 좋아요 랭킹을 렌더링하는 함수
    function renderTopLikedNotesChart(likeRankingData) {
        const rankingList = $('#likeRankingList');  // 좋아요 랭킹 리스트를 표시할 DOM 요소
        rankingList.empty();  // 기존 리스트 초기화

        // 상위 3개의 좋아요 랭킹을 리스트에 추가
        likeRankingData.slice(0, 3).forEach((note, index) => {
            rankingList.append(`<li>${index + 1}위: ${note.noteTitle} (좋아요: ${note.likeCount}개)</li>`);
        });
    }
});
