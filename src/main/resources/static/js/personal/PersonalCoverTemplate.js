$(document).ready(function() {
    let selectedCoverNum = null; // 선택된 커버 num 저장
    let selectedCategoryNum = sessionStorage.getItem('selectedCategory'); // 카테고리 num 가져오기

    // 페이지 로드 시 커버 템플릿을 서버에서 가져옴
    $.get('/personal/getCoverTemplates', function(covers) {
        covers.forEach(function(cover) {
            // 커버 템플릿을 동적으로 HTML에 추가
            const coverHtml = `
                <div class="col-md-4 mb-4">
                    <div class="cover" data-cover-num="${cover.coverNum}">
                        <img src="${cover.coverImage}" alt="커버 이미지" class="img-fluid cover-img">
                    </div>
                </div>`;
            $('.cover-row').append(coverHtml);  // .cover-row에 추가
        });

        // 동적으로 생성된 커버 클릭 이벤트 등록
        $('.cover').on('click', function() {
            // 다른 커버 선택 해제
            $('.cover').removeClass('selected');

            // 선택된 커버에 스타일 적용
            $(this).addClass('selected');
            selectedCoverNum = $(this).data('cover-num'); // 선택된 커버 num 저장
        });
    });

    // 저장 버튼 클릭 시
    $('#saveBtn').on('click', function() {
        const diaryName = $('#diaryName').val(); // 다이어리 name 입력값 가져오기

        // 다이어리 제목을 입력하지 않았거나 커버가 선택되지 않은 경우 경고 메시지
        if (!diaryName) {
            alert('다이어리 제목을 입력해주세요!');
        } else if (!selectedCoverNum) {
            alert('커버를 선택해주세요!');
        } else if (!selectedCategoryNum) {
            alert('카테고리가 설정되지 않았습니다. 다시 시도해주세요.');
        } else {
            // 선택된 카테고리, 커버 num, 다이어리 name을 POST 요청으로 서버에 전달
            $.ajax({
                url: '/personal/diary/save',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    diaryName: diaryName,
                    categoryNum: selectedCategoryNum,  // 카테고리 num도 함께 보냄
                    coverNum: selectedCoverNum         // 커버 num
                }),
                success: function(response) {
                    alert('다이어리가 성공적으로 저장되었습니다!');
                    // 성공적으로 저장된 경우 노트 템플릿 선택 페이지로 이동
                    window.location.href = '/personal/note?coverNum=' + selectedCoverNum;
                },
                error: function(error) {
                    alert('다이어리 저장에 실패했습니다. 다시 시도해주세요.');
                }
            });
        }
    });

    // 취소 버튼 클릭 시 이전 페이지로 돌아감
    $('#cancelBtn').on('click', function() {
        window.location.href = '/personal/MyDiary'; // 이전 페이지로 이동
    });
});