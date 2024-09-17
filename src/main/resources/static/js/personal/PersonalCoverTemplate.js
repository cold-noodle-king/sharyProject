$(document).ready(function() {
    let selectedCoverId = null; // 선택된 커버 ID 저장
    const urlParams = new URLSearchParams(window.location.search);
    const categoryNum = urlParams.get('categoryNum'); // URL에서 카테고리 번호 가져오기

    // 콘솔에 categoryNum 출력해서 값 확인
    console.log("Received categoryNum:", categoryNum);

    if (!categoryNum) {
        alert('카테고리 번호를 받아오지 못했습니다.'); // 카테고리 번호가 없는 경우 경고 표시
    }

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
            $('.cover-row').append(coverHtml);  // 커버 리스트 추가
        });

        // 동적으로 생성된 커버 클릭 이벤트 등록
        $('.cover').on('click', function() {
            // 다른 커버 선택 해제
            $('.cover').removeClass('selected');

            // 선택된 커버에 스타일 적용
            $(this).addClass('selected');
            selectedCoverId = $(this).data('cover-id'); // 선택된 커버 ID 저장

            // 선택된 커버 ID를 숨겨진 input에 설정
            $('#selectedCoverId').val(selectedCoverId);
        });
    });

    // 저장 버튼 클릭 시 검증 후 모달 띄움
    $('#saveBtn').on('click', function() {
        const diaryName = $('#diaryTitle').val(); // 다이어리 제목 가져오기

        // 다이어리 제목과 커버가 선택되었는지 확인
        if (!diaryName) {
            alert('다이어리 제목을 입력해주세요!');
        } else if (!selectedCoverId) {
            alert('커버를 선택해주세요!');
        } else if (!categoryNum) {
            alert('카테고리 정보가 없습니다.'); // 이미 받아온 카테고리 정보 확인
        } else {
            // 다이어리 저장 요청
            $.ajax({
                url: '/personal/saveDiary',
                method: 'POST',
                data: {
                    diaryName: diaryName,
                    coverTemplateNum: selectedCoverId,
                    categoryNum: categoryNum // 카테고리 번호 전송
                },
                success: function(response) {
                    // 서버로부터 diaryNum을 받아옴
                    const diaryNum = response.diaryNum;

                    if (!diaryNum) {
                        console.error("diaryNum is undefined");
                        return;
                    }

                    // 모달 띄우기
                    $('#diaryModal').modal('show');

                    // Yes 버튼 클릭 시 다이어리 번호를 포함하여 속지 템플릿 선택 페이지로 이동
                    $('#yesBtn').on('click', function() {
                        window.location.href = '/personal/note?diaryNum=' + diaryNum;
                    });

                    // No 버튼 클릭 시 MyDiary 페이지로 이동
                    $('#noBtn').on('click', function() {
                        window.location.href = '/personal/MyDiary';
                    });
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.error("Diary save failed: ", textStatus, errorThrown);
                    alert('다이어리 저장에 실패했습니다.');
                }
            });
        }
    });

    // 모달에서 No 버튼 클릭 시 MyDiary 페이지로 이동
    $('#noBtn').on('click', function() {
        window.location.href = '/personal/MyDiary';
    });
});