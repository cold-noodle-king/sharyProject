$(document).ready(function() {
    $('.btn-view-note').on('click', function(e) {
        e.preventDefault(); // 기본 동작 방지

        var noteNum = $(this).data('note-num');
        var modal = $('#noteModal');

        // Ajax로 서버에서 데이터 가져오기
        $.ajax({
            url: '/personal/viewNote/' + noteNum,
            type: 'GET',
            success: function(response) {
                // 데이터 설정
                $('#noteModalLabel').text(response.noteTitle);

                // 위치 정보에서 괄호 및 내용 제거
                var location = response.location.replace(/\(.*?\)/g, '');
                $('#noteLocation').text(location);

                // 작성일을 "YYYY년 MM월 DD일" 형식으로 변환하여 설정
                var diaryDate = new Date(response.diaryDate);
                var formattedDate = diaryDate.getFullYear() + '년 ' + (diaryDate.getMonth() + 1) + '월 ' + diaryDate.getDate() + '일';
                $('#noteDate').text(formattedDate);

                // 감정 정보 설정
                $('#noteEmotion').text(response.emotionName);

                // 노트 내용 설정
                $('#noteContents').text(response.contents);

                // 해시태그가 있을 경우 표시
                if (response.hashtags && response.hashtags.length > 0) {
                    var hashtagsText = response.hashtags.join(', ');
                    $('#noteHashtags').text(hashtagsText);
                } else {
                    $('#noteHashtags').text('해시태그 없음');
                }

                // 이미지가 있는지 확인하고, 없으면 이미지 섹션을 숨김
                if (response.fileName && response.fileName.trim() !== "") {
                    $('#personalNoteImage').attr('src', '/uploads/' + encodeURIComponent(response.fileName)).show();
                } else {
                    $('#personalNoteImage').hide(); // 이미지가 없을 경우 숨기기
                }

                // 배경 이미지 설정 (템플릿 이미지: note_name + '.png')
                if (response.noteTemplate && response.noteTemplate.noteName) {
                    var backgroundImageUrl = '/images/' + encodeURIComponent(response.noteTemplate.noteName) + '.png';
                    console.log("Setting background image:", backgroundImageUrl); // 콘솔에 경로 출력

                    $('#noteContentModal').css({
                        'background-image': 'url(' + backgroundImageUrl + ')',
                        'background-size': 'cover',
                        'background-position': 'center',
                        'background-repeat': 'no-repeat'
                    });
                } else {
                    // 배경 이미지가 없을 경우 기본 배경 색으로 설정
                    $('#noteContentModal').css('background-image', 'none');
                }

                // 모달 열기
                modal.modal('show');
            },
            error: function() {
                // 오류 처리 코드
                alert('노트 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
    });
});