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
                console.log(response); // 서버에서 받아온 데이터를 콘솔에 출력하여 확인합니다.

                // 노트 제목을 모달에 삽입
                $('#modalNoteTitle').text(response.noteTitle);

                // 위치 및 날짜 출력
                $('#modalLocation').text(response.location);
                var date = new Date(response.diaryDate);
                var formattedDate = date.getFullYear() + '년 ' + (date.getMonth() + 1) + '월 ' + date.getDate() + '일';
                $('#modalDiaryDate').text(formattedDate);

                // 감정 데이터 삽입
                $('#modalEmotion').text(response.emotionNum);

                // 이미지 경로 확인
                if (response.fileName && response.fileName.trim() !== "") {
                    console.log("이미지 파일 이름:", response.fileName); // 콘솔에서 파일 이름 확인
                    $('#modalNoteImage').attr('src', '/uploads/' + encodeURIComponent(response.fileName)).show();
                } else {
                    $('#modalNoteImage').hide(); // 이미지가 없으면 숨깁니다.
                }

                // 노트 이미지 삽입
                if (response.fileName && response.fileName.trim() !== "") {
                    $('#modalNoteImage').attr('src', '/uploads/' + encodeURIComponent(response.fileName)).show();
                } else {
                    $('#modalNoteImage').hide();
                }

                // 해시태그 추가
                $('#modalHashtags').empty();
                response.hashtags.forEach(function(tag) {
                    $('#modalHashtags').append('<span class="badge bg-secondary me-1">' + tag + '</span>');
                });

                // 모달 열기
                $('#portfolioModal').modal('show');
            },
            // 요청 중 에러 발생 시 처리
            error: function(jqXHR, textStatus, errorThrown) {
                console.error('노트 정보를 가져오는 중 오류:', textStatus, errorThrown);
                alert('노트 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
    });
});
