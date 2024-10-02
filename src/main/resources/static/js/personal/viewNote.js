$(document).ready(function() {
    // "노트 보기" 버튼 클릭 시 이벤트 처리
    $('.btn-view-note').on('click', function(e) {
        e.preventDefault(); // 기본 동작 방지

        var noteNum = $(this).data('note-num'); // 클릭된 노트의 번호 가져오기
        var modal = $('#noteModal'); // 노트 모달 객체

        // Ajax로 서버에서 해당 노트 정보를 가져오는 요청
        $.ajax({
            url: '/personal/viewNote/' + noteNum, // 노트 정보 요청 URL
            type: 'GET',
            success: function(response) {
                // 노트 제목 설정
                $('#noteModalLabel').text(response.noteTitle);

                // 위치 정보에서 괄호 제거 후 표시
                var location = response.location.replace(/\(.*?\)/g, '');
                $('#noteLocation').text(location);

                // 작성일 설정
                var diaryDate = new Date(response.diaryDate);
                var formattedDate = diaryDate.getFullYear() + '년 ' + (diaryDate.getMonth() + 1) + '월 ' + diaryDate.getDate() + '일';
                $('#noteDate').text(formattedDate);

                // 감정 정보 설정
                var emotionIcon;
                switch(response.emotionName) {
                    case '기쁨':
                        emotionIcon = '😊'; // 기쁨 아이콘
                        break;
                    case '슬픔':
                        emotionIcon = '😢'; // 슬픔 아이콘
                        break;
                    case '화남':
                        emotionIcon = '😠'; // 화남 아이콘
                        break;
                    case '놀람':
                        emotionIcon = '😮'; // 놀람 아이콘
                        break;
                    case '두려움':
                        emotionIcon = '😱'; // 두려움 아이콘
                        break;
                    case '사랑':
                        emotionIcon = '❤️'; // 사랑 아이콘
                        break;
                    default:
                        emotionIcon = '😐'; // 기본 아이콘
                        break;
                }
                $('#noteEmotion').html(emotionIcon); // 감정 아이콘 삽입

                // 노트 내용 설정
                $('#noteContents').text(response.contents);

                // 해시태그 설정
                if (response.hashtags && response.hashtags.length > 0) {
                    var hashtagsText = response.hashtags.join(', ');
                    $('#noteHashtags').text(hashtagsText);
                } else {
                    $('#noteHashtags').text('해시태그 없음');
                }

                // 이미지가 있을 경우 이미지 표시, 없으면 숨김
                if (response.fileName && response.fileName.trim() !== "") {
                    $('#personalNoteImage').attr('src', '/uploads/' + encodeURIComponent(response.fileName)).show();
                } else {
                    $('#personalNoteImage').hide();
                }

                // 배경 이미지 설정
                if (response.noteTemplate && response.noteTemplate.noteName) {
                    var backgroundImageUrl = '/images/' + encodeURIComponent(response.noteTemplate.noteName) + '.png';
                    $('#noteContentModal').css({
                        'background-image': 'url(' + backgroundImageUrl + ')',
                        'background-size': 'cover',
                        'background-position': 'center',
                        'background-repeat': 'no-repeat'
                    });
                } else {
                    $('#noteContentModal').css('background-image', 'none');
                }

                // 프로필 이미지 설정
                if (response.profilePicture && response.profilePicture.trim() !== "") {
                    $('.profile-image img').attr('src', response.profilePicture);
                } else {
                    $('.profile-image img').attr('src', '/images/default_profile.png');
                }

                // 모달 열기
                modal.modal('show');
            },
            error: function() {
                alert('노트 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
    });
});

function handleEditNote() {
    var noteNum = document.getElementById("noteSelect").value; // 선택된 노트 번호 가져오기
    if (noteNum) {
        window.location.href = '/personal/editOrDeleteNote?noteNum=' + noteNum; // 노트 수정 또는 삭제 페이지로 이동
    } else {
        alert('수정할 노트를 선택하세요.');
    }
}

function handleDeleteNote() {
    const selectedNote = document.getElementById('noteSelect').value;
    if (selectedNote) {
        const confirmed = confirm('정말로 이 노트를 삭제하시겠습니까?');
        if (confirmed) {
            // 여기서 AJAX 요청을 사용하여 서버로 삭제 요청을 보냅니다.
            $.ajax({
                url: `/personal/deleteNote/${selectedNote}`,
                type: 'DELETE',
                success: function(response) {
                    alert('노트가 성공적으로 삭제되었습니다.');
                    // 페이지를 새로 고쳐 노트 목록을 업데이트합니다.
                    location.reload();
                },
                error: function(error) {
                    alert('노트 삭제에 실패했습니다.');
                }
            });
        }
    } else {
        alert('삭제할 노트를 선택하세요.');
    }
}
