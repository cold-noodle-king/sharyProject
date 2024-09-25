$(document).ready(function () {
    // 포트폴리오 링크 클릭 이벤트 핸들러
    $('.portfolio-link').on('click', function (e) {
        e.preventDefault(); // 기본 클릭 동작 방지

        var noteNum = $(this).data('note-num'); // 클릭된 노트의 번호 가져오기

        // 노트 데이터를 가져오는 Ajax 요청
        $.ajax({
            url: '/portfolio/viewNote/' + noteNum, // 해당 노트 데이터를 요청할 URL
            type: 'GET',
            success: function (response) {
                // 노트 모달에 데이터 삽입
                $('#customModalNoteTitle').text(response.noteTitle);

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
                $('#customModalEmotion').text(emotionEmojiMap[response.emotionNum] || "🙂"); // 기본 감정은 '🙂'

                // 프로필 이미지 설정 및 member-id 추가
                if (response.profilePicture) {
                    var profilePicturePath = response.profilePicture.startsWith('/uploads/profile/')
                        ? response.profilePicture
                        : '/uploads/profile/' + response.profilePicture;

                    $('#customModalProfilePicture')
                        .attr('src', profilePicturePath)
                        .data('member-id', response.memberId) // member-id 추가
                        .show();
                } else {
                    $('#customModalProfilePicture').hide();
                }

                // 노트 이미지 설정 (노트에 이미지가 있을 경우)
                if (response.fileName && response.fileName.trim() !== "") {
                    $('#customModalNoteImage').attr('src', '/uploads/' + encodeURIComponent(response.fileName)).show(); // 노트 이미지 표시
                } else {
                    $('#customModalNoteImage').hide(); // 이미지가 없을 경우 숨김
                }

                // 노트 배경 이미지 설정 (노트 템플릿이 있을 경우)
                if (response.noteTemplate && response.noteTemplate.noteImage) {
                    var noteImagePath = '/images/' + response.noteTemplate.noteImage;
                    console.log('배경 이미지 파일 경로:', noteImagePath);  // 로그로 경로 확인
                    $('#customNoteContentModal').css('background-image', 'url(' + noteImagePath + ')');
                } else {
                    console.error('배경 이미지 파일 이름이 없습니다.');
                }

                // 노트 내용 삽입
                $('#customModalContents').text(response.contents);

                // 해시태그 추가
                $('#customModalHashtags').empty();
                response.hashtags.forEach(function (tag) {
                    $('#customModalHashtags').append('<span class="badge bg-secondary me-1">' + tag + '</span>'); // 해시태그 추가
                });

                // 모달 열기
                $('#customPortfolioModal').modal('show'); // 노트 모달 표시

                // 노트 탭으로 초기화 (탭 있는 경우)
                $('#modalTab a[href="#noteContent"]').tab('show');

                // 프로필 정보 초기화
                $('#profileModalNickname').text('');
                $('#profileModalMent').text('');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error('노트 정보를 가져오는 중 오류:', textStatus, errorThrown); // 오류 발생 시 처리
                alert('노트 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
    });

    // 프로필 탭 클릭 시 프로필 정보 가져오기
    $('#profile-tab').on('click', function () {
        var memberId = $('#customModalProfilePicture').data('member-id'); // 프로필 이미지에 저장된 member-id 가져오기

        // memberId가 없는 경우 처리
        if (!memberId) {
            console.error('memberId가 지정되지 않았습니다.');
            return;
        }

        // 프로필 정보를 가져오는 Ajax 요청
        $.ajax({
            url: '/portfolio/member/profile/' + memberId, // 프로필 정보 요청 URL
            type: 'GET',
            success: function (profileResponse) {
                console.log('프로필 데이터:', profileResponse);

                // 프로필 정보 탭 업데이트
                var profilePicturePath = profileResponse.profilePicture
                    ? profileResponse.profilePicture.startsWith('/uploads/profile/')
                        ? profileResponse.profilePicture
                        : '/uploads/profile/' + profileResponse.profilePicture
                    : '/images/default_profile.png';

                $('#profileModalImage').attr('src', profilePicturePath);
                $('#profileModalNickname').text(profileResponse.nickname || '');
                $('#profileModalMent').text(profileResponse.ment || '');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error('프로필 정보를 가져오는 중 오류:', textStatus, errorThrown);
                alert('프로필 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
    });

    // 프로필 이미지 클릭 시 프로필 탭으로 전환 (기존 기능 유지)
    $('#customModalProfilePicture').on('click', function () {
        $('#profile-tab').trigger('click');
        // 프로필 탭으로 자동 전환
        $('#modalTab a[href="#profileContent"]').tab('show');
    });
});