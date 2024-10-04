package net.datasa.sharyproject.controller;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.weather.NewWeatherData;
import net.datasa.sharyproject.domain.dto.weather.TodayWeatherData;
import net.datasa.sharyproject.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public String getWeather(Model model) throws Exception {
        // 오늘의 날씨 정보
        TodayWeatherData todayWeather = weatherService.getTodayWeather();

        // 지난 일주일의 날씨 정보
        List<NewWeatherData> lastWeekWeather = weatherService.getLastWeekWeather();

        model.addAttribute("todayWeather", todayWeather);
        model.addAttribute("lastWeekWeather", lastWeekWeather);

        return "weather";
    }
}
