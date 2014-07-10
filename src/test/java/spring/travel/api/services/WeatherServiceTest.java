/**
 * Copyright 2014 Andy Godwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spring.travel.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.HandOff;
import spring.travel.api.model.weather.DailyForecast;
import spring.travel.api.model.weather.Forecast;
import spring.travel.api.model.weather.Location;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static spring.travel.api.controllers.WireMockSupport.stubGet;

public class WeatherServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9101);

    private WeatherService weatherService;

    @Before
    public void before() {
        weatherService = new WeatherService("http://localhost:9101/weather");
        ReflectionTestUtils.setField(weatherService, "asyncRestTemplate", new AsyncRestTemplate());
    }

    @Test
    public void shouldGetDailyForecast() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/weather-lhr-3days.json");
        ObjectMapper mapper = new ObjectMapper();
        DailyForecast stubData = mapper.readValue(inputStream, DailyForecast.class);
        inputStream.close();
        stubGet("/weather?id=2652546&cnt=3&mode=json", stubData);

        HandOff<Optional<DailyForecast>> handOff = new HandOff<>();

        Location location = new Location(2652546, -2.43, 34.66);
        weatherService.forecast(location, 3).onCompletion(
            df -> handOff.put(df)
        ).execute();

        Optional<DailyForecast> dailyForecastOptional = handOff.get(1);

        assertNotEquals(Optional.empty(), dailyForecastOptional);

        DailyForecast dailyForecast = dailyForecastOptional.get();
        assertEquals("Colnbrook", dailyForecast.getCity().getName());
        assertEquals("GB", dailyForecast.getCity().getCountry());

        assertEquals(3, dailyForecast.getForecasts().size());
        List<Forecast> forecasts = dailyForecast.getForecasts();
        Forecast forecast = forecasts.get(0);

        assertEquals(new BigDecimal("285.14"), forecast.getTemperatures().getMin());
        assertEquals(new BigDecimal("295.07"), forecast.getTemperatures().getMax());
    }
}
