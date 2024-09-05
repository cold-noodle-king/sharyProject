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
                    </div>
                </div>`;
            $('.cover-row').append(coverHtml);  // 수정된 부분: .row -> .cover-row
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

    // 저장 버튼 클릭 시 선택된 커버가 있으면 NoteTemplate 페이지로 이동
    $('#saveBtn').on('click', function() {
        if (!selectedCoverId) {
            alert('커버를 선택해주세요!');
        } else {
            // 커버 ID와 함께 노트 템플릿 선택 페이지로 이동 (필요시 쿼리 파라미터 전달 가능)
            window.location.href = '/personal/note?coverId=' + selectedCoverId;  // 커버 ID와 함께 노트 템플릿 페이지로 이동
        }
    });

    // 취소 버튼 클릭 시 이전 페이지로 돌아감
    $('#cancelBtn').on('click', function() {
        window.location.href = '/personal/MyDiary'; // 이전 페이지로 이동
    });
});