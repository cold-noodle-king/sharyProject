$(document).ready(function () {
    // Thymeleaf에서 전달된 msg 값을 변수에 저장
    let msg = `[[${msg}]]`;

    // msg가 비어있지 않은 경우에만 confirm 창을 띄움
    if (msg) {
        // confirm 창을 띄우고, 사용자가 'Yes'를 클릭한 경우
        if (confirm(msg)) {
            // 다이어리 넘버와 함께 페이지 이동
            window.location = '/share/note?shareDiaryNum=' + [[${diaryNum}]];
        }
    }
});