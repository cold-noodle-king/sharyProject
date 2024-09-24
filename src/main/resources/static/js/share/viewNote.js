    let noteNum;
$(document).ready(function() {
    // "노트 보기" 버튼 클릭 시 이벤트 처리
    $('.btn-view-note').on('click', function(e) {
        e.preventDefault();

        let noteNum = $(this).data('note-num'); // 클릭된 노트의 번호 가져오기
        $('#noteNum').val(noteNum); // 숨겨진 입력 필드에 noteNum 설정

        var modal = $('#noteModal'); // 노트 모달 객체

        // Ajax로 서버에서 해당 노트 정보를 가져오는 요청
        $.ajax({
            url: '/share/viewNote/' + noteNum, // 노트 정보 요청 URL
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
                $('#noteEmotion').text(response.emotionName);

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

                // 댓글 리스트 출력
                commentList(noteNum);
            },
            error: function() {
                alert('노트 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
    });

    // 댓글 작성 처리 로직
    $('.replyBtn').on('click', function() {
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
                commentList(inputData.noteNum); // 댓글 목록 갱신
            },
            error: function() {
                alert('댓글을 저장하는 중 오류가 발생했습니다.');
            }
        });
    });


});
    // 댓글 목록 출력 함수
    function commentList(noteNum) {

        $.ajax({
            url: '/reply/commentList',
            type: 'post',
            data: { shareNoteNum: noteNum },
            dataType: 'json',
            success: function(list) {
                $('#commentTbody').empty();
                $(list).each(function(i, com) {
                    let shouldShowEditButton = (authenticatedUser === com.memberId);
                    let shouldShowDeleteButton = (authenticatedUser === com.memberId);
                    let html = `
                        <tr>
                            <td>${com.memberName}(${com.memberId})</td>
                            <td style="width: 400px">${com.contents}</td>
                            <td>${moment(com.createdDate).format('YYYY-MM-DD')}</td>
                            <td style="width: 70px">
                                <span>
                                    <button class="replyUpBtn ${shouldShowEditButton ? '' : 'hide'}" data-con="${com.contents}"
                                            data-bnum="${com.shareNoteNum}" data-rnum="${com.replyNum}">수정</button>
                                </span>
                                <span>
                                    <button class="replyDelBtn ${shouldShowDeleteButton ? '' : 'hide'}"
                                            data-bnum="${com.shareNoteNum}" data-rnum="${com.replyNum}">삭제</button>
                                </span>
                            </td>
                        </tr>
                    `;
                    $('#commentTbody').append(html);
                });
                /* $('.replyUpBtn').on('click', upBtnClicked);
                 $('.replyDelBtn').on('click', delBtnClicked);*/
            }
        });
    }
