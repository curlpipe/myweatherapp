package com.weatherapp.myweatherapp.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CityInfo {

    @JsonProperty("address")
    public String address;

    @JsonProperty("description")
    public String description;

    @JsonProperty("currentConditions")
    public CurrentConditions currentConditions;

    @JsonProperty("days")
    public List<Days> days;

    public static class CurrentConditions {
        @JsonProperty("temp")
        public String currentTemperature;

        @JsonProperty("sunrise")
        public String sunrise;

        @JsonProperty("sunset")
        public String sunset;

        @JsonProperty("feelslike")
        public String feelslike;

        @JsonProperty("humidity")
        public String humidity;

        @JsonProperty("conditions")
        public String conditions;
    }

    public static class Days {

        @JsonProperty("datetime")
        public String date;

        @JsonProperty("temp")
        public String currentTemperature;

        @JsonProperty("tempmax")
        public String maxTemperature;

        @JsonProperty("tempmin")
        public String minTemperature;

        @JsonProperty("conditions")
        public String conditions;

        @JsonProperty("description")
        public String description;
    }
}
