$(document).ready(function() {
    // 표지 목록을 불러와서 화면에 표시
    $.ajax({
        url: "/personal/covers",
        method: "GET",
        success: function(data) {
            let coverSelection = $("#cover-selection");
            coverSelection.empty();
            data.forEach(function(cover) {
                coverSelection.append(
                    `<div class="card m-2" style="width: 10rem;">
                        <img src="${cover.coverImage}" class="card-img-top" alt="${cover.coverName}">
                        <div class="card-body text-center">
                            <input type="radio" name="cover" value="${cover.coverNum}" id="cover${cover.coverNum}">
                            <label for="cover${cover.coverNum}">${cover.coverName}</label>
                        </div>
                    </div>`
                );
            });
        }
    });

    // 저장 버튼 클릭 시 처리
    $("#saveDiary").click(function() {
        let selectedCover = $("input[name='cover']:checked").val();
        let diaryTitle = $("#diaryTitle").val();

        if (!selectedCover || !diaryTitle) {
            alert("표지와 제목을 모두 선택해주세요.");
            return;
        }

        // 서버로 데이터 전송하는 코드 추가 가능
        console.log("Selected Cover:", selectedCover);
        console.log("Diary Title:", diaryTitle);

        // 저장 후 카테고리 선택 페이지로 이동
        window.location.href = "/personal/categorySelect";
    });

    // 취소 버튼 클릭 시 처리
    $("#cancelDiary").click(function() {
        window.location.href = "/personal/MyDiary";  // 취소 시 리다이렉트할 페이지 경로 설정
    });
});