$(document).ready(function () {
    // 포트폴리오 링크 클릭 이벤트 핸들러
    $('.portfolio-link').on('click', function (e) {
        e.preventDefault(); // 기본 클릭 동작 방지

        var noteNum = $(this).data('note-num'); // 클릭된 노트의 번호 가져오기
        console.log('노트 번호:', noteNum); // 노트 번호 콘솔 출력 (디버깅용)

        // 노트 데이터를 가져오는 Ajax 요청
        $.ajax({
            url: '/portfolio/viewNote/' + noteNum, // 해당 노트 데이터를 요청할 URL
            type: 'GET',
            success: function (response) {
                // 노트 번호 및 좋아요 수를 hidden input에 저장
                $('#hiddenNoteNum').val(noteNum);
                $('#hiddenLikeCount').val(response.likeCount || 0);

                // 모달에 데이터 삽입
                $('#customModalNoteTitle').text(response.noteTitle);
                console.log('모달에 설정된 노트 번호:', noteNum);

                // 위치 및 날짜 출력
                $('#customModalLocation').text(response.location);
                var date = new Date(response.diaryDate);
                var formattedDate = date.getFullYear() + '년 ' + (date.getMonth() + 1) + '월 ' + date.getDate() + '일';
                $('#customModalDiaryDate').text(formattedDate);

                // 감정 데이터를 이모티콘으로 변환 후 삽입
                var emotionEmojiMap = {
                    1: "😊",  // 기쁨
                    2: "😢",  // 슬픔
                    3: "😡",  // 화남
                    4: "😲",  // 놀람
                    5: "😨",  // 두려움
                    6: "❤️"   // 사랑
                };
                $('#customModalEmotion').text(emotionEmojiMap[response.emotionNum] || "🙂");

                // 프로필 이미지 설정 및 member-id 추가
                if (response.profilePicture) {
                    var profilePicturePath = response.profilePicture.startsWith('/uploads/profile/')
                        ? response.profilePicture
                        : '/uploads/profile/' + response.profilePicture;

                    $('#customModalProfilePicture')
                        .attr('src', profilePicturePath)
                        .data('member-id', response.memberId)
                        .show();
                    console.log('모달에 설정된 사용자 ID:', response.memberId);
                } else {
                    $('#customModalProfilePicture').hide();
                }

                // 노트 이미지 설정
                if (response.fileName && response.fileName.trim() !== "") {
                    $('#customModalNoteImage').attr('src', '/uploads/' + encodeURIComponent(response.fileName)).show();
                } else {
                    $('#customModalNoteImage').hide();
                }

                // 노트 배경 이미지 설정
                if (response.noteTemplate && response.noteTemplate.noteImage) {
                    var noteImagePath = '/images/' + response.noteTemplate.noteImage;
                    $('#customNoteContentModal').css('background-image', 'url(' + noteImagePath + ')');
                } else {
                    console.error('배경 이미지 파일 이름이 없습니다.');
                }

                // 노트 내용 삽입
                $('#customModalContents').html(response.contents);

                // 해시태그 추가
                $('#customModalHashtags').empty();
                response.hashtags.forEach(function (tag) {
                    $('#customModalHashtags').append('<span class="badge bg-secondary me-1">' + tag + '</span>');
                });

                // 좋아요 수 초기화 및 설정
                $('#likeCount').text(response.likeCount || 0);
                $('#likeCountText').text(response.likeCount || 0);

                // 모달 열기
                $('#customPortfolioModal').modal('show');
                $('#modalTab a[href="#noteContent"]').tab('show'); // 노트 탭으로 초기화
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error('노트 정보를 가져오는 중 오류:', textStatus, errorThrown);
                alert('노트 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
    });

    // 프로필 탭 클릭 시 프로필 정보 가져오기
    $('#profile-tab').on('click', function () {
        var memberId = $('#customModalProfilePicture').data('member-id');

        if (!memberId) {
            console.error('memberId가 지정되지 않았습니다.');
            return;
        }

        // 프로필 정보를 가져오는 Ajax 요청
        $.ajax({
            url: '/portfolio/member/profile/' + memberId,
            type: 'GET',
            success: function (profileResponse) {
                console.log('프로필 데이터:', profileResponse);

                // 프로필 정보 탭 업데이트
                var profilePicturePath = profileResponse.profilePicture
                    ? profileResponse.profilePicture.startsWith('/uploads/profile/')
                        ? profileResponse.profilePicture
                        : '/uploads/profile/' + profileResponse.profilePicture
                    : '/images/profile.png';

                $('#profileModalImage').attr('src', profilePicturePath);
                $('#profileModalNickname').text(profileResponse.nickname || '');
                $('#profileModalMent').text(profileResponse.ment || '소개글이 없습니다.');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error('프로필 정보를 가져오는 중 오류:', textStatus, errorThrown);
                alert('프로필 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
    });

    // 프로필 이미지 클릭 시 프로필 탭으로 전환
    $('#customModalProfilePicture').on('click', function () {
        $('#modalTab a[href="#profileContent"]').tab('show');
        $('#profile-tab').trigger('click'); // 프로필 정보를 탭 전환 시 가져오기
    });
    // 좋아요 버튼 처리 로직
    $(document).on('click', '#likeButton', function () {
        var noteNum = $('#hiddenNoteNum').val(); // 노트 번호 가져오기

        // 현재 좋아요 수 가져오기
        var likeCnt = parseInt($('#hiddenLikeCount').val());

        $.ajax({
            url: '/portfolio/like/' + noteNum, // 수정된 URL
            type: 'POST',
            dataType: 'json',
            success: function (res) {
                if (res.likeClicked === false) {
                    // 좋아요 취소된 경우
                    alert('좋아요가 취소되었습니다.');
                } else {
                    // 좋아요 된 경우
                    alert('좋아요를 눌렀습니다.');
                }
                likeCnt = res.likeCount;
                $('#likeCount').html(likeCnt);  // 새로운 좋아요 수 업데이트
                $('#likeCountText').text(likeCnt);  // 좋아요 수 텍스트 업데이트
                $('#hiddenLikeCount').val(likeCnt);  // 숨겨진 필드에 새로운 좋아요 수 설정
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error('좋아요 요청 중 오류 발생:', textStatus, errorThrown);
                alert('좋아요 처리 중 오류가 발생했습니다.');
            }
        });
    });


    // 팔로우 버튼 클릭 시 followAll 페이지로 이동
    $('#followButton').on('click', function (e) {
        e.preventDefault();
        var followingId = $('#customModalProfilePicture').data('member-id');
        if (followingId) {
            window.location.href = '/followAll?search=' + encodeURIComponent(followingId);
        }
    });
});
