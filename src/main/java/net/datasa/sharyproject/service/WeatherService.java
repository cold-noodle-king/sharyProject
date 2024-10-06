package net.datasa.sharyproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.weather.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 기상청 날씨 API를 이용하여 날씨 정보를 가져오는 메서드
 */
@Slf4j
@Service
public class WeatherService {

    // URL 디코딩된 서비스 키 사용
    private final String SERVICE_KEY = "Al7SHCwILG%2BRLK3XAOjOTNgbXPq7ohv0V0ZXjB5F9KVxB%2FqAJ8YwyvcWkpbL6Z%2BtDa%2FKSDB5jyeA8G8zH8tl%2Bg%3D%3D";
    private final String BASE_URL_ASOS = "http://apis.data.go.kr/1360000/AsosDalyInfoService/";
    private final String BASE_URL_ULTRA = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String stnIds = "108"; // 서울 지점 번호
    private final String nx = "61"; // 강남구 삼성1동 X 좌표
    private final String ny = "125"; // 강남구 삼성1동 Y 좌표

    /**
     * 지난 일주일의 날씨 정보를 가져오는 메서드
     * @return
     * @throws Exception
     */
    public List<NewWeatherData> getLastWeekWeather() throws Exception {
        // 오늘 날짜와 일주일 전 날짜 계산
        LocalDate today = LocalDate.now().minusDays(1); // 전일(D-1)까지 제공하므로 오늘 날짜에서 1일을 빼줍니다.
        LocalDate startDate = today.minusDays(6); // 총 7일치 데이터

        String startDt = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDt = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // API URL 구성
        String url = BASE_URL_ASOS + "getWthrDataList"; // 엔드포인트

        String apiUrl = url + "?" + "serviceKey=" + SERVICE_KEY + "&" + UriComponentsBuilder.newInstance()
                .queryParam("numOfRows", "10")
                .queryParam("pageNo", "1")
                .queryParam("dataType", "JSON")
                .queryParam("dataCd", "ASOS")
                .queryParam("dateCd", "DAY")
                .queryParam("startDt", startDt)
                .queryParam("endDt", endDt)
                .queryParam("stnIds", stnIds)
                .build().encode().toUriString();

        System.out.println("ASOS API URL: " + apiUrl);

        // API 호출 및 응답 수신
        String responseString = getApiResponse(apiUrl);
        System.out.println("ASOS API 응답: " + responseString); // 응답 출력 (디버깅용)

        // 응답이 JSON인지 확인
        if (responseString.startsWith("{")) {
            // JSON 파싱
            NewWeatherResponse response = objectMapper.readValue(responseString, NewWeatherResponse.class);

            // 데이터 추출
            List<NewWeatherData> weatherDataList = new ArrayList<>();

            if (response.getResponse().getBody().getItems().getItem() != null) {
                for (NewWeatherResponse.Item item : response.getResponse().getBody().getItems().getItem()) {
                    NewWeatherData weatherData = new NewWeatherData();
                    weatherData.setDate(item.getTm()); // 날짜
                    weatherData.setAvgTemperature(item.getAvgTa()); // 평균 기온
                    weatherData.setMinTemperature(item.getMinTa()); // 최저 기온
                    weatherData.setMaxTemperature(item.getMaxTa()); // 최고 기온
                    weatherData.setHumidity(item.getAvgRhm()); // 평균 상대습도
                    weatherData.setWindSpeed(item.getAvgWs()); // 평균 풍속

                    // **추가된 부분: 날씨 상태 추정**
                    String weatherCode = getWeatherCodeFromAsosData(item);
                    WeatherDescription weatherDescription = getWeatherDescriptionFromCode(weatherCode);
                    weatherData.setWeatherDescription(weatherDescription.getDescription());
                    weatherData.setIcon(weatherDescription.getIcon());

                    // 필요한 필드 추가
                    weatherDataList.add(weatherData);
                }
            }
            log.debug("일주일 날씨 데이터: {}", weatherDataList);

            return weatherDataList;
        } else {
            // JSON 형식이 아님
            System.err.println("ASOS 응답이 JSON 형식이 아닙니다. 응답 내용: " + responseString);
            throw new Exception("ASOS API로부터 JSON 형식이 아닌 응답을 받았습니다.");
        }
    }

    /**
     * 오늘의 날씨 정보를 가져오는 메서드
     * @return
     * @throws Exception
     */
    public TodayWeatherData getTodayWeather() throws Exception {
        String url = BASE_URL_ULTRA + "getUltraSrtNcst"; // API 엔드포인트

        // 기준 시간 설정
        LocalDateTime now = LocalDateTime.now();
        String baseTime = getBaseTimeForNcst(now);
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // URL 빌드
        String apiUrl = url + "?" + "serviceKey=" + SERVICE_KEY + "&" + UriComponentsBuilder.newInstance()
                .queryParam("numOfRows", "100")
                .queryParam("pageNo", "1")
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build().encode().toUriString();

        System.out.println("UltraSrtNcst API URL: " + apiUrl);

        // API 호출 및 응답 수신
        String responseString = getApiResponse(apiUrl);
        System.out.println("UltraSrtNcst API 응답: " + responseString); // 응답 출력 (디버깅용)

        // 응답이 JSON인지 확인
        if (responseString.startsWith("{")) {
            // JSON 파싱
            TodayWeatherResponse response = objectMapper.readValue(responseString, TodayWeatherResponse.class);

            // 데이터 추출
            Map<String, String> dataMap = new HashMap<>();

            if (response.getResponse().getBody().getItems().getItem() != null) {
                response.getResponse().getBody().getItems().getItem().forEach(item -> {
                    dataMap.put(item.getCategory(), item.getObsrValue());
                });
            } else {
                System.out.println("아이템이 없습니다."); // 디버깅용
            }

            // **여기에서 ptyCode를 정의합니다.**
            String ptyCode = dataMap.get("PTY");

            // SKY 값 가져오기
            String skyCode = getSkyCode(baseDate, getBaseTimeForFcst(now));

            // TodayWeatherData 객체 생성
            TodayWeatherData weatherData = new TodayWeatherData();
            weatherData.setTemperature(dataMap.get("T1H"));
            weatherData.setHumidity(dataMap.get("REH"));
            weatherData.setWindSpeed(dataMap.get("WSD"));
            weatherData.setBaseDate(baseDate);
            weatherData.setBaseTime(baseTime);

            // 날씨 설명 및 아이콘 추가
            WeatherDescription weatherDescription = getWeatherDescription(ptyCode, skyCode);
            weatherData.setWeather(weatherDescription.getDescription());
            weatherData.setIcon(weatherDescription.getIcon());

            System.out.println("오늘의 날씨 데이터: " + weatherData); // 디버깅용

            return weatherData;
        } else {
            // JSON 형식이 아님
            System.err.println("UltraSrtNcst 응답이 JSON 형식이 아닙니다. 응답 내용: " + responseString);
            throw new Exception("UltraSrtNcst API로부터 JSON 형식이 아닌 응답을 받았습니다.");
        }
    }

    /**
     * 공통 API 호출 메서드
     * @param apiUrl
     * @return
     * @throws Exception
     */
    private String getApiResponse(String apiUrl) throws Exception {
        URL urlObj = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("GET");

        System.out.println("HTTP 응답 코드: " + conn.getResponseCode());

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        return sb.toString();
    }

    private WeatherDescription getWeatherDescription(String ptyCode, String skyCode) {
        if (ptyCode == null || ptyCode.equals("")) ptyCode = "0";
        if (skyCode == null || skyCode.equals("")) skyCode = "0";

        String description = "알 수 없음";
        String icon = "unknown.png";

        switch (ptyCode) {
            case "0": // 강수 없음
                switch (skyCode) {
                    case "1":
                        description = "맑음";
                        icon = "sunny.png";
                        break;
                    case "3":
                        description = "구름 많음";
                        icon = "cloudy.png";
                        break;
                    case "4":
                        description = "흐림";
                        icon = "overcast.png";
                        break;
                    default:
                        description = "맑음"; // SKY 코드가 없을 때 기본값
                        icon = "sunny.png";
                        break;
                }
                break;
            case "1":
                description = "비";
                icon = "rain.png";
                break;
            case "2":
                description = "비/눈";
                icon = "rain_snow.png";
                break;
            case "3":
                description = "눈";
                icon = "snow.png";
                break;
            case "4":
                description = "소나기";
                icon = "shower.png";
                break;
            default:
                description = "알 수 없음";
                icon = "unknown.png";
                break;
        }

        return new WeatherDescription(description, icon);
    }

    /**
     * 오늘 날씨의 기준 시간을 구하는 메서드
     * @param dateTime
     * @return
     */
    private String getBaseTimeForNcst(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        int hour = dateTime.getHour();

        if (minute >= 45) {
            minute = 40;
        } else if (minute >= 35) {
            minute = 30;
        } else if (minute >= 25) {
            minute = 20;
        } else if (minute >= 15) {
            minute = 10;
        } else if (minute >= 5) {
            minute = 0;
        } else {
            minute = 50;
            hour = hour - 1;
            if (hour < 0) {
                hour = 23;
            }
        }
        return String.format("%02d%02d", hour, minute);
    }

    public String getSkyCode(String baseDate, String baseTime) throws Exception {
        String url = BASE_URL_ULTRA + "getUltraSrtFcst"; // API 엔드포인트

        // URL 빌드
        String apiUrl = url + "?" + "serviceKey=" + SERVICE_KEY + "&" + UriComponentsBuilder.newInstance()
                .queryParam("numOfRows", "100")
                .queryParam("pageNo", "1")
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build().encode().toUriString();

        System.out.println("UltraSrtFcst API URL: " + apiUrl);

        // API 호출 및 응답 수신
        String responseString = getApiResponse(apiUrl);
        System.out.println("UltraSrtFcst API 응답: " + responseString); // 응답 출력 (디버깅용)

        // 응답이 JSON인지 확인
        if (responseString.startsWith("{")) {
            // JSON 파싱
            TodayWeatherResponse response = objectMapper.readValue(responseString, TodayWeatherResponse.class);

            // 데이터 추출
            if (response.getResponse().getBody().getItems().getItem() != null) {
                for (TodayWeatherResponse.Item item : response.getResponse().getBody().getItems().getItem()) {
                    if (item.getCategory().equals("SKY")) {
                        // 가장 가까운 시간의 SKY 값을 반환
                        return item.getFcstValue();
                    }
                }
            } else {
                System.out.println("아이템이 없습니다."); // 디버깅용
            }
        } else {
            // JSON 형식이 아님
            System.err.println("UltraSrtFcst 응답이 JSON 형식이 아닙니다. 응답 내용: " + responseString);
        }

        return null; // SKY 값을 찾지 못한 경우
    }

    private String getBaseTimeForFcst(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        int hour = dateTime.getHour();

        if (minute < 45) {
            hour = hour - 1;
        }
        if (hour < 0) {
            hour = 23;
        }
        return String.format("%02d30", hour); // 초단기예보의 base_time은 매시각 30분
    }

    private String getWeatherCodeFromAsosData(NewWeatherResponse.Item item) {
        double sumRn = item.getSumRn(); // 일강수량
        double avgTca = item.getAvgTca(); // 평균 전운량

        if (sumRn > 0) {
            return "RAIN"; // 비
        } else {
            if (avgTca <= 2) {
                return "SUNNY"; // 맑음
            } else if (avgTca <= 7) {
                return "CLOUDY"; // 구름 많음
            } else {
                return "OVERCAST"; // 흐림
            }
        }
    }

    private WeatherDescription getWeatherDescriptionFromCode(String code) {
        String description = "알 수 없음";
        String icon = "unknown.png";

        switch (code) {
            case "SUNNY":
                description = "맑음";
                icon = "sunny.png";
                break;
            case "CLOUDY":
                description = "구름 많음";
                icon = "cloudy.png";
                break;
            case "OVERCAST":
                description = "흐림";
                icon = "overcast.png";
                break;
            case "RAIN":
                description = "비";
                icon = "rain.png";
                break;
            // 필요한 경우 추가적인 날씨 상태 코드와 아이콘을 매핑
            default:
                description = "알 수 없음";
                icon = "unknown.png";
                break;
        }

        return new WeatherDescription(description, icon);
    }


    public WeatherData getCurrentWeather() throws Exception {
        // getTodayWeather() 메서드를 호출하여 현재 날씨 데이터를 가져옵니다.
        TodayWeatherData todayWeather = getTodayWeather();

        // TodayWeatherData에서 온도와 아이콘을 추출합니다.
        String temperature = todayWeather.getTemperature();
        String icon = todayWeather.getIcon();

        // WeatherData 객체를 생성하고 데이터 설정
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature(temperature);
        weatherData.setIcon(icon);

        return weatherData;
    }

}
