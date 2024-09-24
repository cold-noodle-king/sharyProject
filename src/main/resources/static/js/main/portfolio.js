$(document).ready(function() {
    // 포트폴리오 링크 클릭 이벤트 핸들러
    $('.portfolio-link').on('click', function(e) {
        e.preventDefault(); // 기본 클릭 동작 방지

        var noteNum = $(this).data('note-num'); // 클릭된 노트의 번호 가져오기

        // Ajax를 통해 서버에서 노트 데이터를 가져오는 요청
        $.ajax({
            url: '/personal/viewNote/' + noteNum, // 해당 노트 데이터를 요청할 URL
            type: 'GET',
            success: function(response) {
                console.log('템플릿 이미지:', response.noteTemplate ? response.noteTemplate.noteImage : '없음'); // 템플릿 이미지 확인 로그

                // 노트 제목을 모달에 삽입
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

                // 프로필 이미지가 있을 경우 파일명만 사용하여 경로 설정
                if (response.profilePicture) {
                    var profilePicture = response.profilePicture.replace('/uploads/profile/', ''); // 중복된 경로 제거
                    $('#customModalProfilePicture').attr('src', '/uploads/profile/' + profilePicture).show();
                } else {
                    $('#customModalProfilePicture').hide();
                }

                // 노트 이미지 삽입
                if (response.fileName && response.fileName.trim() !== "") {
                    $('#customModalNoteImage').attr('src', '/uploads/' + encodeURIComponent(response.fileName)).show();
                } else {
                    $('#customModalNoteImage').hide();
                }

                // 백엔드에서 noteImage가 파일명으로만 전달되었을 때 경로 설정
                if (response.noteTemplate && response.noteTemplate.noteImage) {
                    // 경로가 로컬 경로일 경우 마지막 슬래시 이후의 파일명만 추출
                    var noteImagePath = response.noteTemplate.noteImage;
                    noteImagePath = noteImagePath.substring(noteImagePath.lastIndexOf("/") + 1);

                    $('#customNoteContentModal').css('background-image', 'url(/images/' + noteImagePath + ')');
                } else {
                    console.error('배경 이미지 파일 이름이 없습니다.');
                }

                // 노트 내용 삽입
                $('#customModalContents').text(response.contents);

                // 해시태그 추가
                $('#customModalHashtags').empty();
                response.hashtags.forEach(function(tag) {
                    $('#customModalHashtags').append('<span class="badge bg-secondary me-1">' + tag + '</span>');
                });

                // 모달 열기
                $('#customPortfolioModal').modal('show');
            },
            // 요청 중 에러 발생 시 처리
            error: function(jqXHR, textStatus, errorThrown) {
                console.error('노트 정보를 가져오는 중 오류:', textStatus, errorThrown);
                alert('노트 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
    });
});