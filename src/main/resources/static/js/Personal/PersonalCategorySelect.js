$(document).ready(function() {
    const maxSelection = 1; // 선택할 수 있는 최대 카테고리 수
    let selectedCategories = new Set(); // 선택된 카테고리를 저장할 Set

    // 카테고리 버튼 클릭 시 카테고리 선택/해제 로직
    $('.cat-btn').on('click', function() {
        const category = $(this).data('cat'); // 버튼의 데이터 속성에서 카테고리 이름을 가져옴

        // 이미 선택된 카테고리가 있으면 해제
        if (selectedCategories.size >= maxSelection) {
            const previouslySelected = Array.from(selectedCategories)[0]; // 이전에 선택된 카테고리를 가져옴
            selectedCategories.delete(previouslySelected); // Set에서 제거
            $('.cat-btn').filter(`[data-cat="${previouslySelected}"]`).removeClass('btn-warning').addClass('btn-secondary'); // 스타일 초기화
        }

        // 새로운 카테고리를 선택하거나 해제
        if (!selectedCategories.has(category)) {
            selectedCategories.add(category); // Set에 새 카테고리를 추가
            $(this).removeClass('btn-secondary').addClass('btn-warning'); // 버튼 스타일 변경
        } else {
            selectedCategories.delete(category); // Set에서 선택된 카테고리 제거
            $(this).removeClass('btn-warning').addClass('btn-secondary'); // 버튼 스타일 원래대로 변경
        }
    });

    // 저장 버튼 클릭 시 선택한 카테고리 정보를 서버로 전송 후 페이지 이동
    $("#save").click(function() {
        // 서버로 선택한 카테고리 정보를 POST 요청으로 보낼 수 있음
        // $.post("/your-save-endpoint", { categories: Array.from(selectedCategories) });

        // 저장 후 커버 선택 페이지로 이동
        window.location.href = "/personal/cover"; // 커버 선택 페이지로 URL 변경
    });

    // 취소 버튼 클릭 시 MyDiary 페이지로 돌아감
    $("#cancel").click(function() {
        window.location.href = "/personal/MyDiary"; // MyDiary 페이지로 이동
    });
});