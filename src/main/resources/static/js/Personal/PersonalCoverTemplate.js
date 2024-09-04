$(document).ready(function() {
    let selectedCoverId = null; // 선택된 커버 ID 저장

    // 페이지 로드 시 커버 템플릿을 서버에서 가져옴
    $.get('/personal/getCoverTemplates', function(covers) {
        covers.forEach(function(cover) {
            // 커버 템플릿을 동적으로 HTML에 추가
            const coverHtml = `
                <div class="col-md-4 mb-4">
                    <div class="cover" data-cover-id="${cover.coverNum}">
                        <img src="${cover.coverImage}" alt="커버 이미지" class="img-fluid cover-img">
                        <div class="cover-title mt-2">${cover.coverName}</div>
                    </div>
                </div>`;
            $('.row').append(coverHtml);
        });

        // 동적으로 생성된 커버 클릭 이벤트 등록
        $('.cover').on('click', function() {
            // 다른 커버 선택 해제
            $('.cover').removeClass('selected');

            // 선택된 커버에 스타일 적용
            $(this).addClass('selected');
            selectedCoverId = $(this).data('cover-id'); // 선택된 커버 ID 저장
        });
    });

    // 저장 버튼 클릭 시 선택된 커버 알림
    $('#saveBtn').on('click', function() {
        if (!selectedCoverId) {
            alert('커버를 선택해주세요!');
        } else {
            alert('선택한 커버 ID: ' + selectedCoverId + '\n저장 기능은 나중에 구현 예정입니다.');
        }
    });

    // 취소 버튼 클릭 시 이전 페이지로 돌아감
    $('#cancelBtn').on('click', function() {
        window.location.href = '/personal/MyDiary'; // 이전 페이지로 이동
    });
});