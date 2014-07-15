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
package spring.travel.site.view.model;

import spring.travel.site.model.weather.DailyForecast;
import spring.travel.site.model.weather.Forecast;

import java.util.ArrayList;
import java.util.List;

public class DailyForecastView {

    private String city;

    private List<ForecastView> forecasts = new ArrayList<>();

    private DailyForecastView() {
    }

    public static DailyForecastView fromDailyForecast(DailyForecast dailyForecast) {
        DailyForecastView view = new DailyForecastView();
        view.city = dailyForecast.getCity().getName();
        for (Forecast forecast : dailyForecast.getForecasts()) {
            view.forecasts.add(ForecastView.fromForecast(forecast));
        }
        return view;
    }

    public String getCity() {
        return city;
    }

    public List<ForecastView> getForecasts() {
        return forecasts;
    }
}
