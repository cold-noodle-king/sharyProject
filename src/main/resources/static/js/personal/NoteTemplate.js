$(document).ready(function() {
    let selectedNoteId = null; // 선택된 노트 ID 저장

    // 페이지 로드 시 노트 템플릿을 서버에서 가져옴
    $.get(`/personal/getNoteTemplates`, function(notes) {
        notes.forEach(function(note) {
            // 노트 템플릿을 동적으로 HTML에 추가
            const noteHtml = `
                <div class="col-md-4 mb-4">
                    <div class="cover" data-note-id="${note.noteNum}">
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
            selectedNoteId = $(this).data('note-id'); // 선택된 노트 ID 저장
        });
    });

    // 저장 버튼 클릭 시 선택된 노트 알림
    $('#saveBtn').on('click', function() {
        if (!selectedNoteId) {
            alert('노트를 선택해주세요!');
        } else {
            alert('선택한 노트 ID: ' + selectedNoteId + '\n저장 기능은 나중에 구현 예정입니다.');
        }
    });

    // 취소 버튼 클릭 시 이전 페이지로 돌아감
    $('#cancelBtn').on('click', function() {
        window.location.href = '/note/NoteList'; // 이전 페이지로 이동
    });
});