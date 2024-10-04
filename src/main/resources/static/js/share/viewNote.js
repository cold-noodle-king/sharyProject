$(document).ready(function() {
    // "노트 보기" 버튼 클릭 시 이벤트 처리
    $(document).on('click', '.btn-view-note', function(e) {
        e.preventDefault();

        let noteNum = $(this).data('note-num'); // 클릭된 노트의 번호 가져오기
        $('#noteNum').val(noteNum); // 클릭된 노트의 번호 가져오기

        let likeCnt = $(this).data('like-cnt'); // 좋아요 수 가져오기
        $('#cnt').val(likeCnt);
        $('#likeCnt').html(likeCnt);

        var modal = $('#noteModal'); // 노트 모달 객체

        // Ajax로 서버에서 해당 노트 정보를 가져오는 요청
        $.ajax({
            url: '/share/viewNote/' + noteNum, // 노트 정보 요청 URL
            type: 'GET',
            success: function(response) {
                console.log(response);

                // `shareNote`와 `likeResponse`로 데이터 분리
                var note = response.shareNote;
                var likeResponse = response.likeResponse;

                // 노트 제목 설정
                $('#noteModalLabel').text(note.shareNoteTitle);

                $('#nickname').text(note.nickname);

                let createdDate = new Date(note.createdDate);
                let formedCreatedDate = (createdDate.getFullYear()) + '.' + ('0' + (createdDate.getMonth() + 1)).slice(-2) + '.' + ('0' + createdDate.getDate()).slice(-2);
                $('#createdDate').text(formedCreatedDate);

                // 위치 정보에서 괄호 제거 후 표시
                var location = note.location.replace(/\(.*?\)/g, '');
                $('#noteLocation').text(location);

                // 작성일 설정
                var diaryDate = new Date(note.diaryDate);
                var formattedDate = diaryDate.getFullYear() + '년 ' + (diaryDate.getMonth() + 1) + '월 ' + diaryDate.getDate() + '일';
                $('#noteDate').text(formattedDate);

                // 감정 정보 설정
                var emotionIcon;
                switch(note.emotionName) {
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

                // 좋아요 카운트 설정 (likeResponse에서 가져옴)
                $('#cnt').val(likeResponse.cnt); // 숨겨진 필드에 좋아요 수 설정
                $('#likeCnt').text(likeResponse.cnt); // 모달에 좋아요 수 표시

                // 감정별 좋아요 수 설정
                $('#joyCnt').html(likeResponse.joyCnt);
                $('#loveCnt').html(likeResponse.loveCnt);
                $('#sadCnt').html(likeResponse.sadCnt);
                $('#angryCnt').html(likeResponse.angryCnt);
                $('#wowCnt').html(likeResponse.wowCnt);

                // 노트 내용 설정
                $('#noteContents').text(note.contents);

                // 해시태그 설정
                if (note.hashtags && note.hashtags.length > 0) {
                    var hashtagsText = note.hashtags.join(', ');
                    $('#noteHashtags').text(hashtagsText);
                } else {
                    $('#noteHashtags').text('해시태그 없음');
                }

                // 이미지가 있을 경우 이미지 표시, 없으면 숨김
                if (note.fileName && note.fileName.trim() !== "") {
                    $('#personalNoteImage').attr('src', '/uploads/' + encodeURIComponent(note.fileName)).show();
                } else {
                    $('#personalNoteImage').hide();
                }

                // 배경 이미지 설정
                if (note.noteTemplate && note.noteTemplate.noteName) {
                    var backgroundImageUrl = '/images/' + encodeURIComponent(note.noteTemplate.noteName) + '.png';
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
                if (note.profilePicture && note.profilePicture.trim() !== "") {
                    $('.profile-image img').attr('src', note.profilePicture);
                } else {
                    $('.profile-image img').attr('src', '/images/default_profile.png');
                }

                // 모달 열기
                modal.modal('show');

            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log('Error Status: ' + textStatus); // 오류 상태
                console.log('Error Thrown: ' + errorThrown); // 오류 메시지
                console.log('Response Text: ' + jqXHR.responseText); // 서버 응답 내용
                alert('노트 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
        // 댓글 리스트 출력
        commentList(noteNum);
    });

    // 댓글 작성 처리 로직
    $(document).on('click', '.replyBtn', function() {
        let noteNum = $('#noteNum').val(); // 숨겨진 입력 필드에서 noteNum 가져오기
        let inputData = {
            shareNoteNum: noteNum,
            contents: $('#contents').val()
        };
        console.log(noteNum);
        if ($('#contents').val() == '') {
            alert('내용을 입력해 주세요');
            $('#contents').focus();
            return false;
        }
        $.ajax({
            url: '/reply/write',
            type: 'post',
            data: inputData,
            success: function() {
                $('#contents').val(''); // 입력창 초기화
                commentList(inputData.shareNoteNum); // 댓글 목록 갱신
            },
            error: function() {
                alert('댓글을 저장하는 중 오류가 발생했습니다.');
            }
        });
    });

    // 좋아요 버튼 처리 로직
    $(document).on('click', '.likeBtn', function () {
        let noteNum = $('#noteNum').val(); // 숨겨진 입력 필드에서 noteNum 가져오기
        let likeCnt = $('#cnt').val();

        // 클릭한 이모지 가져오기 (선택 사항)
        const emoji = $(this).data('emoji');
        console.log(emoji);
        // console.log(`Clicked emoji: ${emoji}`);  // 클릭된 이모지 콘솔 출력

        $.ajax({
            url: 'like',
            type: 'post',
            data: { noteNum: noteNum, emotionNum : emoji }, // noteNum 서버로 전송
            dataType: 'json',
            success: function (res) {
                console.log(res.isLiked);
                console.log(res.joyCnt);
                console.log(res.loveCnt);
                console.log(res.sadCnt);
                console.log(res.angryCnt);
                console.log(res.wowCnt);
                // 좋아요 상태에 따라 처리
                if (res.isLiked === true) {
                    // 좋아요 추가됨
                    $('#cnt').val(res.cnt); // 좋아요 수 갱신
                    $('#likeCnt').html(res.cnt);
                    $('#joyCnt').html(res.joyCnt);
                    $('#loveCnt').html(res.loveCnt);
                    $('#sadCnt').html(res.sadCnt);
                    $('#angryCnt').html(res.angryCnt);
                    $('#wowCnt').html(res.wowCnt);

                } else {
                    // 좋아요 취소됨
                    $('#cnt').val(res.cnt); // 좋아요 수 갱신
                    $('#likeCnt').html(res.cnt);
                    $('#joyCnt').html(res.joyCnt);
                    $('#loveCnt').html(res.loveCnt);
                    $('#sadCnt').html(res.sadCnt);
                    $('#angryCnt').html(res.angryCnt);
                    $('#wowCnt').html(res.wowCnt);
                }
            },
            error: function (err) {
                console.error('좋아요 처리 중 오류:', err);
            }
        });
    });


});

function commentList(noteNum) {
    $.ajax({
        url: '/reply/commentList',
        type: 'post',
        data: { noteNum: noteNum },
        dataType: 'json',
        success: function(list) {
            console.log(list);
            $('.commentTbody').empty();
            $(list).each(function(i, com) {
                // 댓글 작성자가 현재 로그인한 사용자와 같은지 확인
                let isCurrentUser = (authenticatedUserId === com.memberId);
                let deleteIconHtml = '';
                if (isCurrentUser) {
                    // 로그인한 사용자일 경우 삭제 아이콘 표시
                    deleteIconHtml = `<img src="/images/xicon.png" style="width: 15px; height: 15px;" alt="삭제">`;
                }

                // 댓글 HTML 구성
                let html = `
                    <tr>
                        <td style="width: 80px"><strong>${com.nickname}</strong></td>
                        <td style="width: 210px">${com.contents}</td>
                        <td>${moment(com.createdDate).format('MM.DD.')}</td>
                        <td><a href="#" class="delReply" data-note-num="${com.shareNoteNum}" data-reply-num="${com.replyNum}">${deleteIconHtml}</a></td>
                    </tr>
                `;
                $('.commentTbody').append(html);
            });

            // 삭제 아이콘 클릭 이벤트 리스너 추가
            $('.delReply').off('click').on('click', function(e) {
                e.preventDefault(); // 기본 링크 동작 방지
                let noteNum = $(this).data('note-num'); // 노트 번호 가져오기
                let replyNum = $(this).data('reply-num'); // 댓글 번호 가져오기
                let trElement = $(this).closest('tr'); // 해당 tr 요소 가져오기

                // 삭제 확인 알림
                if (!confirm('댓글을 삭제하시겠습니까?')) {
                    return;
                } else {
                    $.ajax({
                        url: '/reply/delete',
                        type: 'post',
                        data: { noteNum: noteNum, replyNum: replyNum },
                        success: function() {
                            trElement.css('display', 'none'); // 해당 tr을 숨김
                        },
                        error: function() {
                            alert('댓글 삭제 중 오류가 발생했습니다.');
                        }
                    });
                }
            });
        }
    });
}

$('#noteModal').on('hidden.bs.modal', function () {
    // 모달 초기화 코드
    $('#noteModalLabel').text('');
    $('#noteLocation').text('');
    $('#noteDate').text('');
    $('#noteEmotion').text('');
    $('#noteContents').text('');
    $('#noteHashtags').text('');
    $('#personalNoteImage').hide();
    $('#likeCnt').text('');
});
