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

import spring.travel.site.model.Advert;
import spring.travel.site.model.Offer;
import spring.travel.site.model.user.User;
import spring.travel.site.model.weather.DailyForecast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HomePage {

    public static Map<String, ?> from(Optional<User> user, List<Offer> offers,
                                      Optional<DailyForecast> forecast, List<Advert> adverts) {
        Map<String, Object> map = new HashMap<>();
        user.ifPresent(u -> map.put("user", u));
        map.put("offers", OffersView.from(offers, 4));
        forecast.ifPresent(f -> map.put("weather", DailyForecastView.fromDailyForecast(f)));
        map.put("adverts", AdvertsView.from(adverts, 4));
        if (adverts.size() > 4) {
            map.put("mainAdvert", adverts.get(4));
        }
        return map;
    }
}
