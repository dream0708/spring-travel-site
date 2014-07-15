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

import spring.travel.site.model.weather.Forecast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ForecastView {

    private String date;

    private String day;

    private String summary;

    private String maximum;

    private String minimum;

    private String icon;

    private ForecastView(String date, String day, String summary, String maximum, String minimum, String icon) {
        this.date = date;
        this.day = day;
        this.summary = summary;
        this.maximum = maximum;
        this.minimum = minimum;
        this.icon = icon;
    }

    public static ForecastView fromForecast(Forecast forecast) {
        Date date = new Date(forecast.getDate() * 1000L);
        return new ForecastView(
            new SimpleDateFormat("yyyy-MM-dd").format(date),
            new SimpleDateFormat("EEEE").format(date),
            forecast.getWeather()[0].getMain(),
            forecast.getTemperatures().getMax().toPlainString(),
            forecast.getTemperatures().getMin().toPlainString(),
            forecast.getWeather()[0].getIcon()
        );
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return date;
    }

    public String getSummary() {
        return summary;
    }

    public String getMaximum() {
        return maximum;
    }

    public String getMinimum() {
        return minimum;
    }

    public String getIcon() {
        return icon;
    }
}
