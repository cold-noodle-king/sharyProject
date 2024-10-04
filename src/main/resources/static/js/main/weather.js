$(document).ready(function () {
    $("#getWeatherButton").click(function () {
        getWeather();
    });
});

function getWeather() {
    $.ajax({
        url: "/weather/come",
        type: "get",
        timeout: 30000,
        contentType: "application/json",
        dataType: "json",
        success: function (data, status, xhr) {
            let dataHeader = data.result.response.header.resultsCode;

            if (dataHeader === "00") {
                console.log("success == >");
                console.log(data);

                // 데이터를 화면에 표시
                displayWeatherData(data.result.response.body.items.item);
            } else {
                console.log("fail == >");
                console.log(data);
            }
        },
        error: function (e, status, xhr, data) {
            console.log("error == >");
            console.log(e);
        }
    });
}

function displayWeatherData(items) {
    // 결과를 표시할 위치
    let resultContainer = $("#weatherResult");

    // 이전에 표시된 내용 제거
    resultContainer.empty();

    // 각 항목을 결과 컨테이너에 추가
    items.forEach(function (item) {
        let info = item.category;
        let dataValue = item.fcstValue;

        switch (info) {
            case "TMP":
                info = "기온";
                dataValue = dataValue + " ℃";
                break;
            case "UUU":
                info = "동서 성분 풍속";
                dataValue = dataValue + " m/s";
                break;
            case "VVV":
                info = "남북 성분 풍속";
                dataValue = dataValue + " m/s";
                break;
            case "VEC":
                info = "풍향";
                dataValue = dataValue + " ℃";
                break;
            case "WSD":
                info = "풍속";
                dataValue = dataValue + " m/s";
                break;
            case "SKY":
                info = "하늘 상태";
                // 조건에 따라 SKY 값 변경
                switch (dataValue) {
                    case "1":
                        dataValue = "맑음";
                        break;
                    case "2":
                        dataValue = "비";
                        break;
                    case "3":
                        dataValue = "구름 많음";
                        break;
                    // 다른 조건들 추가
                    default:
                        break;
                }
                break;
            case "PTY":
                info = "강수형태";
                // 조건에 따라 PTY 값 변경
                switch (dataValue) {
                    case "0":
                        dataValue = "없음";
                        break;
                    case "1":
                        dataValue = "비";
                        break;
                    case "2":
                        dataValue = "눈/비";
                        break;
                    case "3":
                        dataValue = "눈";
                        break;
                    // 다른 조건들 추가
                    default:
                        break;
                }
                break;
            case "POP":
                info = "강수확률";
                dataValue = dataValue + " %";
                break;
            case "WAV":
                info = "파고";
                dataValue = dataValue + " m";
                break;
            case "PCP":
                info = "강수량";
                // 조건에 따라 PCP 값 변경
                switch (dataValue) {
                    case "0":
                        dataValue = "강수 없음";
                        break;
                    // 다른 조건들 추가
                    default:
                        break;
                }
                break;
            // 다른 항목들도 추가
            default:
                break;
        }
        resultContainer.append("<div class='weatherItem'>" + info + " : "
            + dataValue + "</div>");
    });
}