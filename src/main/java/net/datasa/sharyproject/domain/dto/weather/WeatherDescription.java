package net.datasa.sharyproject.domain.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherDescription {
    private String description; // 날씨 설명
    private String icon;        // 아이콘 파일명
}
