package com.weatherapp.myweatherapp.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private WeatherService weatherService;

    @InjectMocks private WeatherController weatherController;

    // Test rain condition checking:
    // This ensures that the controller correctly identifies if
    // there is rain in a given city based on the
    // Visual Crossing API condition field
    @Test
    void rainConditionCheck() {
        assertTrue(weatherController.hasRain("Rain"));
        assertTrue(weatherController.hasRain("Rain, Overcast"));
        assertTrue(weatherController.hasRain("Overcast, Rain"));
        assertTrue(weatherController.hasRain("Light Drizzle/Rain, Drizzle"));
        assertTrue(weatherController.hasRain("Thunderstorm, Partially Cloudy"));
        assertTrue(weatherController.hasRain("Dust storm, Squalls"));
        assertTrue(weatherController.hasRain("Hail, Hail Showers, Light Drizzle"));
        assertTrue(weatherController.hasRain("Fog, Freezing Drizzle/Freezing Rain"));
        assertTrue(weatherController.hasRain("Snow And Rain Showers, Heavy Rain"));
        assertTrue(weatherController.hasRain("Heavy Rain, Light Rain, Squalls, Diamond Dust"));
        assertFalse(weatherController.hasRain("Overcast, Overcast"));
        assertFalse(weatherController.hasRain("Overcast"));
        assertFalse(weatherController.hasRain("Sky Coverage Decreasing, Hail Showers"));
        assertFalse(weatherController.hasRain("Sky Unchanged, Clear"));
        assertFalse(weatherController.hasRain("Clear, Hail"));
        assertFalse(weatherController.hasRain("Diamond Dust"));
        assertFalse(weatherController.hasRain("Thunderstorm Without Precipitation"));
        assertFalse(weatherController.hasRain("Thunderstorm Without Precipitation, Hail"));
        assertFalse(weatherController.hasRain("Ice, Funnel Cloud/Tornado"));
        assertFalse(weatherController.hasRain("Blowing Or Drifting Snow"));
    }

    // Check the /raining_in endpoint to ensure it finds the correct answer
    @Test
    void rainingInCheck() throws Exception {
        // Set up mock data to use in testing
        when(weatherService.forecastByCity("London")).thenReturn(mockCityInfoForLondon());
        when(weatherService.forecastByCity("Paris")).thenReturn(mockCityInfoForParis());
        when(weatherService.forecastByCity("Lagos")).thenReturn(mockCityInfoForLagos());
        when(weatherService.forecastByCity("Auckland")).thenReturn(mockCityInfoForAuckland());

        // Send some GET requests and test the results (based on mock data)
        mockMvc.perform(get("/raining_in").param("city1", "London").param("city2", "Paris"))
                .andExpect(status().isOk())
                .andExpect(content().string("Rain in London only"));
        mockMvc.perform(get("/raining_in").param("city1", "London").param("city2", "Auckland"))
                .andExpect(status().isOk())
                .andExpect(content().string("Rain in London and Auckland"));
        mockMvc.perform(get("/raining_in").param("city1", "Lagos").param("city2", "Paris"))
                .andExpect(status().isOk())
                .andExpect(content().string("No rain in Lagos nor Paris"));
        mockMvc.perform(get("/raining_in").param("city1", "Auckland").param("city2", "Paris"))
                .andExpect(status().isOk())
                .andExpect(content().string("Rain in Auckland only"));
    }

    // Check the /longest_daylight endpoint to ensure it finds the correct answer
    @Test
    void longestDaylightCheck() throws Exception {
        // Set up mock data to use in testing
        when(weatherService.forecastByCity("London")).thenReturn(mockCityInfoForLondon());
        when(weatherService.forecastByCity("Paris")).thenReturn(mockCityInfoForParis());
        when(weatherService.forecastByCity("Lagos")).thenReturn(mockCityInfoForLagos());
        when(weatherService.forecastByCity("Auckland")).thenReturn(mockCityInfoForAuckland());

        // Send some GET requests and test the results (based on mock data)
        mockMvc.perform(get("/longest_daylight").param("city1", "London").param("city2", "Paris"))
                .andExpect(status().isOk())
                .andExpect(content().string("Paris"));
        mockMvc.perform(get("/longest_daylight").param("city1", "Lagos").param("city2", "Auckland"))
                .andExpect(status().isOk())
                .andExpect(content().string("Lagos"));
        mockMvc.perform(get("/longest_daylight").param("city1", "London").param("city2", "Lagos"))
                .andExpect(status().isOk())
                .andExpect(content().string("Lagos"));
        mockMvc.perform(get("/longest_daylight").param("city1", "Auckland").param("city2", "Paris"))
                .andExpect(status().isOk())
                .andExpect(content().string("Paris"));
    }

    // Mock test data for London
    private CityInfo mockCityInfoForLondon() {
        CityInfo cityInfo = new CityInfo();
        cityInfo.currentConditions = new CityInfo.CurrentConditions();
        cityInfo.currentConditions.conditions = "Rain";
        cityInfo.currentConditions.sunrise = "09:23:00";
        cityInfo.currentConditions.sunset = "18:48:00";
        return cityInfo;
    }

    // Mock test data for Paris
    private CityInfo mockCityInfoForParis() {
        CityInfo cityInfo = new CityInfo();
        cityInfo.currentConditions = new CityInfo.CurrentConditions();
        cityInfo.currentConditions.conditions = "Overcast";
        cityInfo.currentConditions.sunrise = "08:33:00";
        cityInfo.currentConditions.sunset = "19:27:00";
        return cityInfo;
    }

    // Mock test data for Lagos
    private CityInfo mockCityInfoForLagos() {
        CityInfo cityInfo = new CityInfo();
        cityInfo.currentConditions = new CityInfo.CurrentConditions();
        cityInfo.currentConditions.conditions = "Hail, Mist";
        cityInfo.currentConditions.sunrise = "06:15:00";
        cityInfo.currentConditions.sunset = "16:41:00";
        return cityInfo;
    }

    // Mock test data for Auckland
    private CityInfo mockCityInfoForAuckland() {
        CityInfo cityInfo = new CityInfo();
        cityInfo.currentConditions = new CityInfo.CurrentConditions();
        cityInfo.currentConditions.conditions = "Snow And Rain Showers";
        cityInfo.currentConditions.sunrise = "10:12:00";
        cityInfo.currentConditions.sunset = "19:24:00";
        return cityInfo;
    }
}
