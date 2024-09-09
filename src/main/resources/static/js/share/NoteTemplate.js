$(document).ready(function() {
    let selectedNoteNum = null; // 선택된 노트 Num 저장

    // 페이지 로드 시 노트 템플릿을 서버에서 가져옴
    $.get(`/share/getNoteTemplates`, function(notes) {
        notes.forEach(function(note) {
            // 노트 템플릿을 동적으로 HTML에 추가
            const noteHtml = `
                <div class="col-md-4 mb-4">
                    <div class="cover" data-note-num="${note.noteNum}">
                        <img src="${note.noteImage}" alt="노트 이미지" class="img-fluid cover-img">
                    </div>
                </div>`;
            $('.note-row').append(noteHtml);
        });

        // 동적으로 생성된 노트 클릭 이벤트 등록
        $('.cover').on('click', function() {
            // 다른 커버 선택 해제
            $('.cover').removeClass('selected');

            // 선택된 커버에 스타일 적용
            $(this).addClass('selected');
            selectedNoteNum = $(this).data('note-num'); // 선택된 노트 Num 저장
        });
    });

    // 저장 버튼 클릭 시 선택된 노트 번호를 URL 파라미터로 전달하여 다이어리 작성 페이지로 이동
    $('#saveBtn').on('click', function() {
        if (!selectedNoteNum) {
            alert('노트를 선택해주세요!');
        } else {
            window.location.href = `/share/noteForm?noteNum=${selectedNoteNum}`;
        }
    });

    // 취소 버튼 클릭 시 이전 페이지로 이동
    $('#cancelBtn').on('click', function() {
        window.location.href = '/note/NoteList'; // 이전 페이지로 이동
    });
});