$(document).ready(function() {
    // 노트 보기 버튼 클릭 시 실행되는 이벤트 리스너
    $('.btn-view-note').on('click', function(e) {
        e.preventDefault(); // 기본 클릭 동작 중단

        var noteNum = $(this).data('note-num'); // 클릭된 버튼의 data-note-num에서 노트 번호 가져오기
        var modal = $('#noteModal'); // 모달 요소 선택

        // 서버에서 노트 데이터를 가져오는 Ajax 요청
        $.ajax({
            url: '/personal/viewNote/' + noteNum, // 노트 번호를 포함한 URL 요청
            type: 'GET', // GET 메서드 사용
            success: function(response) {
                // 노트 제목 설정
                $('#noteModalLabel').text(response.noteTitle);

                // 위치 정보 설정
                $('#noteLocation').text(response.location);

                // 작성일을 "YYYY년 MM월 DD일" 형식으로 변환하여 설정
                var diaryDate = new Date(response.diaryDate);
                var formattedDate = diaryDate.getFullYear() + '년 ' + (diaryDate.getMonth() + 1) + '월 ' + diaryDate.getDate() + '일';
                $('#noteDate').text(formattedDate);

                // 노트 내용 설정
                $('#noteContents').text(response.contents);

                // 감정 정보 설정
                $('#noteEmotion').text(response.emotionName);

                // 해시태그가 있을 경우 표시
                if (response.hashtags && response.hashtags.length > 0) {
                    var hashtagsText = response.hashtags.join(', '); // 해시태그를 배열로 연결
                    $('#noteHashtags').text(hashtagsText); // 해시태그 표시
                } else {
                    $('#noteHashtags').text('해시태그 없음'); // 해시태그가 없을 경우 기본값
                }

                // 노트 템플릿 이미지가 있을 경우 배경 이미지로 설정
                if (response.noteTemplate && response.noteTemplate.noteName) {
                    var imageUrl = 'http://localhost:8888/images/' + response.noteTemplate.noteName + '.png';  // 절대 경로 사용
                    console.log("Setting background image:", imageUrl);

                    // 모달 배경 이미지 설정
                    $('#noteContentModal').css({
                        'background-image': 'url(' + imageUrl + ')',
                        'background-size': 'contain',
                        'background-repeat': 'no-repeat',
                        'background-position': 'center'
                    });
                } else {
                    $('#noteContentModal').css('background-image', 'none'); // 이미지가 없을 때
                }

                // 모달 표시
                modal.modal('show');
            },
            error: function() {
                // Ajax 요청이 실패한 경우 경고 표시
                alert('노트 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
    });
});
