// 메인페이지 헤더 프로필 부분 js 파일 헤더 html 아작스여기다가
$(document).on('click', '#profileModalTrigger', function () {
    $.ajax({
        url: '/mypage/profile/modal',
        method: 'GET',
        success: function (data) {
            console.log("응답 데이터:", data);  // 응답 데이터를 확인합니다.
            $('#profileImage').attr('src', data.profilePicture);
            $('#profileNickname').text(data.nickname);
            $('#profileMent').text(data.ment);
            $('#profileModal').modal('show');  // 모달 열기
        },
        error: function (error) {
            console.log("프로필 정보를 불러오는데 실패했습니다.", error);
        }
    });
});

$(document).on('click', '#profileModalTrigger', function () {
    console.log("프로필 버튼이 클릭되었습니다.");  // 클릭 이벤트가 트리거되는지 확인
});
