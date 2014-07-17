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
package spring.travel.site.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.cache.CacheBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.site.compose.HandOff;
import spring.travel.site.model.weather.DailyForecast;
import spring.travel.site.model.weather.Forecast;
import spring.travel.site.model.weather.Location;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static spring.travel.site.controllers.WireMockSupport.stubGet;

public class WeatherServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9101);

    private WeatherService weatherService;

    @Before
    public void before() {
        weatherService = new WeatherService("http://localhost:9101/weather");
        ReflectionTestUtils.setField(weatherService, "asyncRestTemplate", new AsyncRestTemplate());
        ReflectionTestUtils.setField(weatherService, "weatherCache", CacheBuilder.newBuilder().build());
    }

    @Test
    public void shouldGetDailyForecast() throws Exception {
        stubWeatherData("/weather?id=2652546&cnt=3&mode=json", "/weather-lhr-3days.json");

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

    @Test
    public void shouldReturnForecastFromTheCacheIfPresent() throws Exception {
        stubWeatherData("/weather?id=2652546&cnt=3&mode=json", "/weather-lhr-3days.json");

        HandOff<Optional<DailyForecast>> handOff = new HandOff<>(3);

        Location location = new Location(2652546, -2.43, 34.66);

        // need to nest these so the first completion handler has a chance to populate the cache
        weatherService.forecast(location, 3).onCompletion(
            df1 -> {
                handOff.put(df1);
                weatherService.forecast(location, 3).onCompletion(
                    df2 -> handOff.put(df2)
                ).execute();
                weatherService.forecast(location, 3).onCompletion(
                    df3 -> handOff.put(df3)
                ).execute();
            }
        ).execute();

        List<Optional<DailyForecast>> forecasts = handOff.getAll(1);

        assertEquals(3, forecasts.size());

        verify(1, getRequestedFor(urlEqualTo("/weather?id=2652546&cnt=3&mode=json")));
    }

    private void stubWeatherData(String url, String filename) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(filename);
        ObjectMapper mapper = new ObjectMapper();
        DailyForecast stubData = mapper.readValue(inputStream, DailyForecast.class);
        inputStream.close();
        stubGet(url, stubData);
    }
}
