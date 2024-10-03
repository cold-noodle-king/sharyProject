// 커버 선택 함수는 전역 범위에 정의
function selectCover(imgElement) {
    // 선택된 커버 번호 가져오기
    const coverNum = imgElement.getAttribute('data-cover-num');

    // 모든 커버 이미지에서 선택된 클래스 제거
    document.querySelectorAll('.cover-img').forEach(img => {
        img.classList.remove('selected');
    });

    // 클릭한 이미지에 선택된 클래스 추가
    imgElement.classList.add('selected');

    // 선택된 커버 번호를 숨겨진 인풋에 설정
    document.getElementById('selectedCoverNum').value = coverNum;
}

$(document).ready(function() {
    // 수정 폼 처리
    $("#diaryEditForm").on('submit', function(e) {
        e.preventDefault();  // 기본 동작 방지
        const diaryNum = $("input[name='diaryNum']").val(); // 다이어리 ID 가져오기
        $.ajax({
            type: 'POST',
            url: '/personal/updateDiary',  // 서버에서 수정 경로 확인
            data: $(this).serialize(),  // 폼 데이터를 시리얼라이즈하여 전송
            success: function(response) {
                alert("다이어리가 수정되었습니다.");
                window.location.href = '/personal/MyDiary';  // 수정 후 다이어리 목록으로 이동
            },
            error: function() {
                alert("수정 중 오류가 발생했습니다.");
            }
        });
    });

    // 삭제 폼 처리
    $("#diaryDeleteForm").on('submit', function(e) {
        e.preventDefault();  // 기본 동작 방지
        const diaryNum = $("input[name='diaryNum']").val(); // 다이어리 ID 가져오기
        if (confirm("정말로 이 다이어리를 삭제하시겠습니까?")) {
            $.ajax({
                type: 'POST',
                url: '/personal/deleteDiary',  // 서버에서 삭제 경로 확인
                data: { diaryNum: diaryNum },  // 다이어리 ID 전송
                success: function(response) {
                    alert("다이어리가 삭제되었습니다.");
                    window.location.href = '/personal/MyDiary';  // 삭제 후 다이어리 목록으로 이동
                },
                error: function() {
                    alert("삭제 중 오류가 발생했습니다.");
                }
            });
        }
    });
});
