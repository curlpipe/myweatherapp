package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WeatherController {

    @Autowired WeatherService weatherService;

    @GetMapping("/forecast/{city}")
    public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) {

        CityInfo ci = weatherService.forecastByCity(city);

        return ResponseEntity.ok(ci);
    }

    /**
     * This method serves as the endpoint that, between two cities,
     * finds the one that has the longest daylight hours
     *
     * Example query: GET /longest_daylight?city1=London&city2=Lagos
     * Note that arguments come from HTTP arguments in the URL
     *
     * @param city1 This is the first city to compare, you can provide a name, zip code or coordinates
     * @param city2 This is the second city to compare, you can provide a name, zip code or coordinates
     * @return The response to show in the browser, in the format of a single string with the city name
     */
    @GetMapping("/longest_daylight")
    public ResponseEntity<String> longestDaylight(
            @RequestParam String city1, @RequestParam String city2) {
        // Retrieve the data for the two cities
        CityInfo city1Info = weatherService.forecastByCity(city1);
        CityInfo city2Info = weatherService.forecastByCity(city2);

        // Parse sunrise/sunset time data
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime city1Sunrise = LocalTime.parse(city1Info.currentConditions.sunrise, timeFormat);
        LocalTime city1Sunset = LocalTime.parse(city1Info.currentConditions.sunset, timeFormat);
        LocalTime city2Sunrise = LocalTime.parse(city2Info.currentConditions.sunrise, timeFormat);
        LocalTime city2Sunset = LocalTime.parse(city2Info.currentConditions.sunset, timeFormat);

        // Work out the duration between sunrise and sunset for both cities
        long city1Daylight = Duration.between(city1Sunrise, city1Sunset).toSeconds();
        long city2Daylight = Duration.between(city2Sunrise, city2Sunset).toSeconds();

        // Return the city with the longest period between sunrise and sunset
        if (city1Daylight > city2Daylight) {
            // City 1 has more daylight
            return ResponseEntity.ok(city1);
        } else {
            // City 2 has more daylight
            return ResponseEntity.ok(city2);
        }
    }

    /**
     * This method serves as the endpoint that, between two cities,
     * will find out which ones are raining.
     *
     * Example query: GET /raining_in?city1=Madrid&city2=Ankara
     * Note that arguments come from HTTP arguments in the URL
     *
     * @param city1 This is the first city to compare, you can provide a name, zip code or coordinates
     * @param city2 This is the second city to compare, you can provide a name, zip code or coordinates
     * @return A string describing whether neither city has rain, only one city has rain or both have rain
     */
    @GetMapping("/raining_in")
    public ResponseEntity<String> rainingIn(
            @RequestParam String city1, @RequestParam String city2) {
        // Retrieve the data for the two cities
        CityInfo city1Info = weatherService.forecastByCity(city1);
        CityInfo city2Info = weatherService.forecastByCity(city2);

        // Based on the Visual Crossing Weather API, create a list of all condition codes where rain
        // occurs
        // Here, I assume that ice, dust, snow and hail are not rain
        Set<String> rainCodes = new HashSet<>();
        rainCodes.add("Heavy Freezing Drizzle/Freezing Rain");
        rainCodes.add("Light Freezing Drizzle/Freezing Rain");
        rainCodes.add("Heavy Freezing Rain");
        rainCodes.add("Light Freezing Rain");
        rainCodes.add("Drizzle");
        rainCodes.add("Precipitation In Vicinity");
        rainCodes.add("Rain");
        rainCodes.add("Heavy Rain And Snow");
        rainCodes.add("Light Rain And Snow");
        rainCodes.add("Rain Showers");
        rainCodes.add("Heavy Rain");
        rainCodes.add("Light Rain");
        rainCodes.add("Heavy Drizzle");
        rainCodes.add("Snow And Rain Showers");
        rainCodes.add("Squalls");
        rainCodes.add("Thunderstorm");
        rainCodes.add("Light Drizzle");
        rainCodes.add("Heavy Drizzle/Rain");
        rainCodes.add("Light Drizzle/Rain");
        rainCodes.add("Freezing Drizzle/Freezing Rain");

        // Extract the conditions for each city and determine if it is raining there
        String[] city1Conditions = city1Info.currentConditions.conditions.split(", ");
        String[] city2Conditions = city2Info.currentConditions.conditions.split(", ");
        boolean city1Raining =
                Arrays.stream(city1Conditions).map(String::trim).anyMatch(rainCodes::contains);
        boolean city2Raining =
                Arrays.stream(city2Conditions).map(String::trim).anyMatch(rainCodes::contains);

        // Return a some text describing the difference in rain between the two cities
        if (city1Raining && city2Raining) {
            // Raining in both cities
            return ResponseEntity.ok(
                    String.format("It is raining in both %s and %s", city1, city2));
        } else if (city1Raining) {
            // Only raining in city 1
            return ResponseEntity.ok(String.format("It is only raining in %s", city1));
        } else if (city2Raining) {
            // Only raining in city 2
            return ResponseEntity.ok(String.format("It is only raining in %s", city2));
        } else {
            // No rain in either city
            return ResponseEntity.ok(
                    String.format("It is raining in neither %s nor %s", city1, city2));
        }
    }
}
