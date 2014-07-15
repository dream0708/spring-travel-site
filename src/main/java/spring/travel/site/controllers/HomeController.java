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
package spring.travel.site.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import spring.travel.site.compose.AsyncTask;
import spring.travel.site.compose.ParallelCollector;
import spring.travel.site.compose.Tuple2;
import spring.travel.site.model.Advert;
import spring.travel.site.model.Offer;
import spring.travel.site.model.user.Loyalty;
import spring.travel.site.model.user.Profile;
import spring.travel.site.model.weather.DailyForecast;
import spring.travel.site.model.weather.Location;
import spring.travel.site.request.Request;
import spring.travel.site.request.RequestInfo;
import spring.travel.site.services.AdvertService;
import spring.travel.site.services.LoyaltyService;
import spring.travel.site.services.OffersService;
import spring.travel.site.services.ProfileService;
import spring.travel.site.services.WeatherService;
import spring.travel.site.view.model.HomePage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static spring.travel.site.compose.Tasks.parallel;

@RestController
@RequestMapping("/")
public class HomeController extends OptionalUserController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private LoyaltyService loyaltyService;

    @Autowired
    private OffersService offersService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private GeoLocator geoLocator;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ModelAndView> home(@RequestInfo Request requestInfo) {
        return withOptionalUser(requestInfo,
            (request, response) -> {

                ParallelCollector<DailyForecast, Tuple2<Optional<List<Advert>>, Optional<List<Offer>>>> collector =
                    new ParallelCollector<>(
                        (result) -> {
                            Optional<DailyForecast> forecast = result.a();

                            Optional<Tuple2<Optional<List<Advert>>, Optional<List<Offer>>>> adsOffers = result.b();
                            List<Advert> adverts = adsOffers.flatMap(ao -> ao.a()).orElse(Collections.emptyList());
                            List<Offer> offers = adsOffers.flatMap(ao -> ao.b()).orElse(Collections.emptyList());

                            Map<String, ?> map = HomePage.from(request.getUser(), offers, forecast, adverts);
                            response.setResult(new ModelAndView("home", map));
                        }
                    );

                Location location = geoLocator.locate(request);
                AsyncTask<DailyForecast> forecast = weatherService.forecast(location, 5);
                forecast.onCompletion(
                    df -> collector.updateA(df)
                ).execute();

                parallel(
                    profileService.profile(request.getUser()),
                    loyaltyService.loyalty(request.getUser())
                ).onCompletion(
                    profileLoyalty -> {
                        Optional<Profile> profile = profileLoyalty.flatMap(t -> t.a());
                        Optional<Loyalty> loyalty = profileLoyalty.flatMap(t -> t.b());
                        parallel(
                            advertService.advert(5, profile),
                            offersService.offers(profile, loyalty)
                        ).onCompletion(
                            adsOffers -> collector.updateB(adsOffers)
                        ).execute();
                    }
                ).execute();
            }
        );
    }
}
