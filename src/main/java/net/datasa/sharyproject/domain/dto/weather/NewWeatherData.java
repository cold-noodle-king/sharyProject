package net.datasa.sharyproject.domain.dto.weather;

import lombok.Data;

@Data
public class NewWeatherData {
    private String date; // 날짜
    private String weatherDescription; // 날씨 설명 필드 추가
    private String icon; // 아이콘 파일명 필드 추가
    private String avgTemperature; // 평균 기온
    private String minTemperature; // 최저 기온
    private String maxTemperature; // 최고 기온
    private String humidity; // 평균 상대습도
    private String windSpeed; // 평균 풍속

    // 추가 필드 필요 시 추가
}
