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

import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.site.compose.AsyncTask;
import spring.travel.site.compose.ImmediatelySomethingAsyncTaskAdapter;
import spring.travel.site.compose.ListenableFutureAsyncTaskAdapter;
import spring.travel.site.model.weather.DailyForecast;
import spring.travel.site.model.weather.Location;

public class WeatherService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    @Autowired
    private Cache<String, DailyForecast> weatherCache;

    private String url;

    public WeatherService(String url) {
        this.url = url;
    }

    public AsyncTask<DailyForecast> forecast(Location location, int numberOfDays) {
        String queryString = "?id=" + location.getCityId() + "&cnt=" + numberOfDays + "&mode=json";

        DailyForecast dailyForecast = weatherCache.getIfPresent(queryString);
        if (dailyForecast != null) {
            return new ImmediatelySomethingAsyncTaskAdapter<>(dailyForecast);
        }

        return new ListenableFutureAsyncTaskAdapter<>(
            () -> asyncRestTemplate.getForEntity(url + queryString, DailyForecast.class),
            (optionalDailyForecast) -> optionalDailyForecast.ifPresent(df -> weatherCache.put(queryString, df))
        );
    }
}
