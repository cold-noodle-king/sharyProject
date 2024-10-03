function getSelectedDiaryNum() {
    const selectedDiary = document.getElementById('diarySelect').value;
    return selectedDiary ? selectedDiary : null;
}

function handleEditDiary() {
    const diaryNum = getSelectedDiaryNum();
    if (diaryNum) {
        // 선택된 다이어리 번호를 사용하여 수정 페이지로 이동
        window.location.href = `/personal/editOrDeleteDiary/${diaryNum}`;
    } else {
        alert("수정할 다이어리를 선택해주세요.");
    }
}

function handleDeleteDiary() {
    const diaryNum = getSelectedDiaryNum();
    if (diaryNum) {
        if (confirm("정말로 이 다이어리를 삭제하시겠습니까?")) {
            // Ajax를 사용하여 다이어리 삭제 요청을 보냄
            $.ajax({
                url: `/personal/deleteDiary`,
                type: 'POST',
                data: { diaryNum: diaryNum },
                success: function(response) {
                    alert("다이어리가 삭제되었습니다.");
                    location.reload(); // 페이지 새로고침
                },
                error: function() {
                    alert("다이어리 삭제 중 오류가 발생했습니다.");
                }
            });
        }
    } else {
        alert("삭제할 다이어리를 선택해주세요.");
    }
}