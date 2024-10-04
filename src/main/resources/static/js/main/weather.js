$(document).ready(function() {
    // 페이지 로드 시 현재 날씨 데이터 가져오기
    $.ajax({
        url: '/currentWeatherData',  // 서버에서 현재 날씨 데이터를 제공하는 엔드포인트
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            // 응답 데이터 처리
            var currentWeather = response.currentWeather;
            var temperature = currentWeather.temperature;  // 현재 온도
            var weatherIcon = currentWeather.icon;         // 날씨 아이콘 파일명 (예: 'sunny.png')

            // 헤더의 날씨 아이콘 업데이트
            $('#headerWeatherIcon').attr('src', '/images/weather/' + weatherIcon);

            // 헤더의 온도 표시 업데이트
            $('#headerTemperature').text(temperature + '℃');
        },
        error: function(xhr, status, error) {
            console.error('현재 날씨 정보를 가져오는 데 실패했습니다:', error);
        }
    });


    // 날씨 아이콘 클릭 이벤트 핸들러
    $('.weather').on('click', function() {
        event.preventDefault(); // 기본 링크 동작 방지
        // AJAX 요청을 보내 날씨 정보를 가져옴
        $.ajax({
            url: '/weatherData',
            method: 'GET',
            dataType: 'json',
            success: function(response) {
                // 응답 데이터 처리
                var todayWeather = response.todayWeather;
                var lastWeekWeather = response.lastWeekWeather;

                // 날짜 포맷팅 (baseDate)
                var baseDate = todayWeather.baseDate; // 예: "20241004"
                if (baseDate.length === 8) {
                    var year = baseDate.substring(0, 4);
                    var month = baseDate.substring(4, 6);
                    var day = baseDate.substring(6, 8);
                    baseDate = year + '년 ' + month + '월 ' + day + '일';
                }

                // 모달의 내용을 업데이트
                $('#baseDate').text(baseDate);
                $('#todayWeatherIcon').attr('src', '/images/weather/' + todayWeather.icon);
                $('#todayWeatherDesc').text(todayWeather.weather);
                $('#todayTemperature').text(todayWeather.temperature);
                $('#todayHumidity').text(todayWeather.humidity);

                // 지난 일주일의 날씨 정보 업데이트
                var $tbody = $('#lastWeekWeatherTable tbody');
                $tbody.empty(); // 기존 내용 지우기

                lastWeekWeather.forEach(function(weather) {
                    // 기존 날짜 포맷 0000-00-00
                    var originalDate = weather.date;

                    // 날짜를 월/일 형식으로 변환
                    var dateParts = originalDate.split('-'); // ["2024", "09", "27"]
                    var formattedDate = dateParts[1] + '/' + dateParts[2]; // "09/27"

                    var row = '<tr>' +
                        '<td>' + formattedDate + '</td>' +
                        '<td>' +
                        '<img src="/images/weather/' + weather.icon + '" alt="날씨 아이콘" class="weather-icon" style="width: 30px; height: 30px; margin-right: 5px;">' +
                        weather.weatherDescription +
                        '</td>' +
                        '<td>' + weather.avgTemperature + "℃" + '</td>' +
                        '</tr>';
                    $tbody.append(row);
                });

                // 모달 열기
                var weatherModal = new bootstrap.Modal(document.getElementById('weatherModal'));
                weatherModal.show();
            },
            error: function(xhr, status, error) {
                console.error('날씨 정보를 가져오는 데 실패했습니다:', error);
            }
        });
    });
});
