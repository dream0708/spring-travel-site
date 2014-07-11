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
package spring.travel.api.controllers;

import spring.travel.api.model.Advert;
import spring.travel.api.model.Offer;
import spring.travel.api.model.User;
import spring.travel.api.model.weather.DailyForecast;

import java.util.List;

public class HomePage {

    private User user;

    private List<Offer> offers;

    private DailyForecast dailyForecast;

    private List<Advert> adverts;

    public HomePage() {
    }

    public HomePage(User user, List<Offer> offers, DailyForecast dailyForecast, List<Advert> adverts) {
        this.user = user;
        this.offers = offers;
        this.dailyForecast = dailyForecast;
        this.adverts = adverts;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public DailyForecast getDailyForecast() {
        return dailyForecast;
    }

    public void setDailyForecast(DailyForecast dailyForecast) {
        this.dailyForecast = dailyForecast;
    }

    public List<Advert> getAdverts() {
        return adverts;
    }

    public void setAdverts(List<Advert> adverts) {
        this.adverts = adverts;
    }
}
