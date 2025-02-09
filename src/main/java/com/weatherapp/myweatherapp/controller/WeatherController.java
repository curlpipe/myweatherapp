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

/**
 * The weather controller for working with weather-related data
 *
 * @author Luke (curlpipe)
 */
@Controller
public class WeatherController {

    @Autowired WeatherService weatherService;

    /**
     * This method provides weather forecast data in JSON format
     *
     * Example query: GET /forecast/London
     *
     * @param city This is the city to get the forecast
     * @return The weather forecast for the provided city in JSON format
     */
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
            @RequestParam(required = false) String city1,
            @RequestParam(required = false) String city2) {
        // Handle exceptions where user hasn't provided correct arguments
        if (city1 == null || city2 == null) {
            return ResponseEntity.badRequest()
                    .body("Ensure you have both `city1` and `city2` parameters");
        }

        // Retrieve the data for the two cities
        CityInfo city1Info;
        CityInfo city2Info;
        try {
            city1Info = weatherService.forecastByCity(city1);
            city2Info = weatherService.forecastByCity(city2);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Ensure that you have valid names for `city1` and `city2` parameters");
        }

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
            @RequestParam(required = false) String city1,
            @RequestParam(required = false) String city2) {
        // Handle exceptions where user hasn't provided correct arguments
        if (city1 == null || city2 == null) {
            return ResponseEntity.badRequest()
                    .body("Ensure you have both `city1` and `city2` parameters");
        }

        // Retrieve the data for the two cities
        CityInfo city1Info;
        CityInfo city2Info;
        try {
            city1Info = weatherService.forecastByCity(city1);
            city2Info = weatherService.forecastByCity(city2);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Ensure that you have valid names for `city1` and `city2` parameters");
        }

        // Find out if any of the conditions for each city involves rain
        boolean city1Raining = hasRain(city1Info.currentConditions.conditions);
        boolean city2Raining = hasRain(city2Info.currentConditions.conditions);

        // Return a some text describing the difference in rain between the two cities
        if (city1Raining && city2Raining) {
            // Raining in both cities
            return ResponseEntity.ok(String.format("Rain in %s and %s", city1, city2));
        } else if (city1Raining) {
            // Only raining in city 1
            return ResponseEntity.ok(String.format("Rain in %s only", city1));
        } else if (city2Raining) {
            // Only raining in city 2
            return ResponseEntity.ok(String.format("Rain in %s only", city2));
        } else {
            // No rain in either city
            return ResponseEntity.ok(String.format("No rain in %s nor %s", city1, city2));
        }
    }

    /**
     * This is a helper method for working out if a weather
     * condition from the Visual Crossing API involves rain
     *
     * @param condition The condition given by the Visual Crossing API
     * @return A boolean that is true if there is rain, otherwise false
     */
    public boolean hasRain(String condition) {
        // Based on the Visual Crossing API, create a set of raining conditions
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

        // Break up list of conditions and check if any involve rain
        String[] conditions = condition.split(", ");
        return Arrays.stream(conditions).map(String::trim).anyMatch(rainCodes::contains);
    }
}
