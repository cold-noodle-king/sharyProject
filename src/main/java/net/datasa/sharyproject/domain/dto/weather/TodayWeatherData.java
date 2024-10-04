package net.datasa.sharyproject.domain.dto.weather;

import lombok.Data;

@Data
public class TodayWeatherData {
    private String temperature;
    private String humidity;
    private String windSpeed;
    private String baseDate;
    private String baseTime;
    private String weather; // 날씨 설명
    private String icon;    // 아이콘 파일명
}
