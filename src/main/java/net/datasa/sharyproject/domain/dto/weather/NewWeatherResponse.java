package net.datasa.sharyproject.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NewWeatherResponse {
    @JsonProperty("response")
    private Response response;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        @JsonProperty("header")
        private Header header;

        @JsonProperty("body")
        private Body body;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        @JsonProperty("resultCode")
        private String resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonProperty("dataType")
        private String dataType;

        @JsonProperty("items")
        private Items items;

        @JsonProperty("pageNo")
        private int pageNo;

        @JsonProperty("numOfRows")
        private int numOfRows;

        @JsonProperty("totalCount")
        private int totalCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        @JsonProperty("item")
        private List<Item> item;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JsonProperty("stnId")
        private String stnId; // 지점 번호

        @JsonProperty("tm")
        private String tm; // 날짜 "yyyy-MM-dd"

        @JsonProperty("avgTa")
        private String avgTa; // 평균 기온

        @JsonProperty("minTa")
        private String minTa; // 최저 기온

        @JsonProperty("maxTa")
        private String maxTa; // 최고 기온

        @JsonProperty("avgRhm")
        private String avgRhm; // 평균 상대습도

        @JsonProperty("avgWs")
        private String avgWs; // 평균 풍속

        private double sumRn; // 일강수량 필드 추가
        private double avgTca; // 평균 전운량 필드 추가

    }
}
