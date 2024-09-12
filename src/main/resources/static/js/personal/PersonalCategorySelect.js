$(document).ready(function() {
    let selectedCategoryNum = null; // 선택된 카테고리 번호를 저장할 변수

    // 카테고리 버튼 클릭 시 카테고리 선택/해제 로직
    $('.cat-btn').on('click', function() {
        selectedCategoryNum = $(this).data('cat-num'); // 버튼의 데이터 속성에서 카테고리 번호를 가져옴
        $('.cat-btn').removeClass('btn-warning').addClass('btn-secondary'); // 모든 버튼 스타일 초기화
        $(this).removeClass('btn-secondary').addClass('btn-warning'); // 선택된 버튼 스타일 변경
    });

    // 저장 버튼 클릭 시 카테고리가 선택되지 않으면 경고 메시지 표시
    $("#save").click(function() {
        if (!selectedCategoryNum) {
            alert("카테고리를 선택해주세요!");
            return; // 함수 종료
        }

        // 카테고리 번호를 쿼리 파라미터로 추가하여 커버 선택 페이지로 이동
        window.location.href = "/personal/cover?categoryNum=" + selectedCategoryNum;
    });

    // 취소 버튼 클릭 시 MyDiary 페이지로 돌아감
    $("#cancel").click(function() {
        window.location.href = "/personal/MyDiary"; // MyDiary 페이지로 이동
    });
});