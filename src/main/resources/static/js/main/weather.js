$(document).ready(function() {
    // 페이지 로드 시 현재 날씨 데이터 가져오기
    $.ajax({
        url: '/currentWeatherData',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            var currentWeather = response.currentWeather;
            var temperature = currentWeather.temperature;
            var weatherIcon = currentWeather.icon;

            $('#headerWeatherIcon').attr('src', '/images/weather/' + weatherIcon);
            $('#headerTemperature').text(temperature + '℃');
        },
        error: function(xhr, status, error) {
            console.error('현재 날씨 정보를 가져오는 데 실패했습니다:', error);
        }
    });

    // 이벤트 리스너가 중복으로 등록되지 않도록 .off() 사용
    $('.weather').off('click').on('click', function(event) {
        event.preventDefault(); // 기본 링크 동작 방지

        // 모달 요소가 존재하는지 확인
        var weatherModalElement = document.getElementById('weatherModal');
        if (!weatherModalElement) {
            console.error('모달 요소가 존재하지 않습니다. 페이지에 모달 HTML이 포함되어 있는지 확인하세요.');
            return;
        }


        $.ajax({
            url: '/weatherData',
            method: 'GET',
            dataType: 'json',
            success: function(response) {
                var todayWeather = response.todayWeather;
                var lastWeekWeather = response.lastWeekWeather;

                var baseDate = todayWeather.baseDate;
                if (baseDate.length === 8) {
                    var year = baseDate.substring(0, 4);
                    var month = baseDate.substring(4, 6);
                    var day = baseDate.substring(6, 8);
                    baseDate = year + '년 ' + month + '월 ' + day + '일';
                }

                $('#baseDate').text(baseDate);
                $('#todayWeatherIcon').attr('src', '/images/weather/' + todayWeather.icon);
                $('#todayWeatherDesc').text(todayWeather.weather);
                $('#todayTemperature').text(todayWeather.temperature);
                $('#todayHumidity').text(todayWeather.humidity);

                var $tbody = $('#lastWeekWeatherTable tbody');
                $tbody.empty();

                lastWeekWeather.forEach(function(weather) {
                    var originalDate = weather.date;
                    var dateParts = originalDate.split('-');
                    var formattedDate = dateParts[1] + '/' + dateParts[2];

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

                // 모달을 열기 전에 Bootstrap 모달 인스턴스 확인
                var weatherModalElement = document.getElementById('weatherModal');
                var weatherModal = bootstrap.Modal.getInstance(weatherModalElement) || new bootstrap.Modal(weatherModalElement);
                weatherModal.show();
            },
            error: function(xhr, status, error) {
                console.error('날씨 정보를 가져오는 데 실패했습니다:', error);
            }
        });
    });
    $('.btn-close, .modal-backdrop').on('click', function() {
        var weatherModalElement = document.getElementById('weatherModal');
        var weatherModal = bootstrap.Modal.getInstance(weatherModalElement);
        if (weatherModal) {
            weatherModal.hide();
        }
    });

});
