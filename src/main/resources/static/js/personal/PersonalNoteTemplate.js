$(document).ready(function() {
    let selectedNoteNum = null; // 선택된 노트 Num 저장
    const diaryNum = $('#diaryNum').val();
    console.log("DiaryNum: ", diaryNum); // 콘솔에서 diaryNum 값 확인

    // 페이지 로드 시 노트 템플릿을 서버에서 가져옴
    $.get(`/personal/getNoteTemplates`, function(notes) {
        notes.forEach(function(note) {
            // 노트 템플릿을 동적으로 HTML에 추가
            const noteHtml = `
                <div class="col-md-4 mb-4">
                    <div class="cover" data-note-num="${note.noteNum}"> <!-- noteNum으로 수정 -->
                        <img src="${note.noteImage}" alt="노트 이미지" class="img-fluid cover-img">
                    </div>
                </div>`;
            $('.note-row').append(noteHtml);
        });

        // 동적으로 생성된 노트 템플릿 클릭 이벤트 등록
        $('.cover').on('click', function() {
            // 다른 템플릿 선택 해제
            $('.cover').removeClass('selected');

            // 선택된 템플릿에 스타일 적용
            $(this).addClass('selected');
            selectedNoteNum = $(this).data('note-num'); // 선택된 노트 Num 저장

            // 선택된 noteNum 확인
            console.log("Selected noteNum: ", selectedNoteNum);
        });
    });

    // 저장 버튼 클릭 시 선택된 노트 템플릿 번호, 다이어리 번호, 노트 이름을 URL 파라미터로 전달하여 이동
    $('#saveBtn').on('click', function() {
        const noteName = $('#noteName').val(); // 노트 이름 가져오기

        // diaryNum과 noteNum 확인
        console.log("DiaryNum: ", diaryNum);
        console.log("Selected noteNum: ", selectedNoteNum);

        if (!selectedNoteNum) {
            alert('노트 템플릿을 선택해주세요!');
        } else if (!noteName) {
            alert('노트 이름을 입력해주세요!');
        } else if (!diaryNum) {
            alert('다이어리 번호가 없습니다.');
        } else {
            // NoteForm으로 이동하면서 diaryNum, noteNum, noteName 전달
            window.location.href = `/personal/noteForm?noteNum=${selectedNoteNum}&diaryNum=${diaryNum}&noteName=${encodeURIComponent(noteName)}`;
        }
    });

    // 취소 버튼 클릭 시 이전 페이지로 이동
    $('#cancelBtn').on('click', function() {
        window.location.href = '/note/NoteList'; // 이전 페이지로 이동
    });
});