$(document).ready(function() {
    const maxSelection = 1; // 선택할 수 있는 최대 카테고리 수
    let selectedCategories = new Set(); // 선택된 카테고리를 저장할 Set

    // 카테고리 버튼 클릭 시 카테고리 선택/해제 로직
    $('.cat-btn').on('click', function() {
        let category = $(this).data('cat'); // 버튼의 데이터 속성에서 카테고리 이름을 가져옴

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

        // 선택된 카테고리를 숨겨진 input에 설정
        $('#selectedCategories').val(Array.from(selectedCategories).join(','));
    });

    // 폼 제출 시 숨겨진 input 값을 설정한 후 제출되도록 설정
    $("#categoryForm").on('submit', function(event) {
        // 폼 제출 전에 선택된 카테고리를 확인
        const selected = Array.from(selectedCategories).join(',');
        if (!selected) {
            event.preventDefault(); // 카테고리가 선택되지 않았으면 폼 제출을 막음
            alert("카테고리를 선택해주세요."); // 사용자에게 알림
        }
    });

    // 취소 버튼 클릭 시 공유 다이어리 메인 페이지로 돌아감
    $("#cancel").click(function() {
        window.location.href = "/share/main"; // MyDiary 페이지로 이동
    });
});
