package com.weatherapp.myweatherapp.controller;

import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WeatherController {

  @Autowired
  WeatherService weatherService;

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
  public ResponseEntity<String> longestDaylight(@RequestParam String city1, @RequestParam String city2) {
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
}
