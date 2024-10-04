package net.datasa.sharyproject.domain.dto.weather;

import lombok.Data;

@Data
public class WeatherData {
    private String temperature; // 온도 (예: "25")
    private String icon;        // 아이콘 파일명 (예: "sunny.png")

    // 생성자, 게터 및 세터
}
