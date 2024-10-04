package net.datasa.sharyproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.weather.NewWeatherData;
import net.datasa.sharyproject.domain.dto.weather.TodayWeatherData;
import net.datasa.sharyproject.domain.dto.weather.WeatherData;
import net.datasa.sharyproject.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class WeatherController {

    private final WeatherService weatherService;

    /**
     *
     * @param model
     * @return
     * @throws Exception
     */
    @GetMapping("/weather")
    public String getWeather(Model model) throws Exception {
        // 오늘의 날씨 정보
        TodayWeatherData todayWeather = weatherService.getTodayWeather();
        log.debug("오늘날씨:{}", todayWeather);

        // 지난 일주일의 날씨 정보
        List<NewWeatherData> lastWeekWeather = weatherService.getLastWeekWeather();

        model.addAttribute("todayWeather", todayWeather);
        model.addAttribute("lastWeekWeather", lastWeekWeather);


        return "weather";
    }

    // AJAX 요청에 JSON 데이터를 반환하는 메서드 추가
    @GetMapping("/weatherData")
    @ResponseBody
    public Map<String, Object> getWeatherData() throws Exception {
        // 오늘의 날씨 정보
        TodayWeatherData todayWeather = weatherService.getTodayWeather();
        log.debug("오늘날씨:{}", todayWeather);

        // 지난 일주일의 날씨 정보
        List<NewWeatherData> lastWeekWeather = weatherService.getLastWeekWeather();

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("todayWeather", todayWeather);
        response.put("lastWeekWeather", lastWeekWeather);

        return response;
    }

    @ResponseBody
    @GetMapping("/currentWeatherData")
    public Map<String, Object> getCurrentWeatherData() {
        Map<String, Object> data = new HashMap<>();
        try {
            WeatherData currentWeather = weatherService.getCurrentWeather();
            data.put("currentWeather", currentWeather);
        } catch (Exception e) {
            e.printStackTrace();
            data.put("error", "날씨 정보를 가져오는 데 실패했습니다.");
        }
        return data;
    }
}
