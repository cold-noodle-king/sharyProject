$(document).ready(function() {
    $('#goBack').click(function() {
        window.location.href = '/share/viewMember?diaryNum=' + [[${diary.shareDiaryNum}]];
    });
    $('.okBtn').on('click', function () {
        let diaryNum = $(this).data('diary-num');
        let member = $(this).data('member');
        console.log(diaryNum, member);

       /* if (!confirm('가입 요청을 수락하시겠습니까?')){
            return false;
        } else {*/
            $.ajax({
                type: 'POST',
                url: '/share/acceptRequest',
                data: JSON.stringify({ diaryNum: diaryNum, member: member }),
                contentType: 'application/json; charset=utf-8',
                success: function (response) {
                    console.log('Success:', response);
                    // 필요한 경우 성공 후 다른 동작 수행
                    alert(response);
                    location.reload();
                },
                error: function (error) {
                    console.error('Error:', error);
                }
            });
        // }
    });

});