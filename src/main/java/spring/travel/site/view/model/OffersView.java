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

import spring.travel.site.model.Offer;

import java.util.List;

public class OffersView {

    private List<Offer> offers;

    private OffersView() {
    }

    public static OffersView from(List<Offer> offers, int count) {
        if (offers.size() == 0) {
            return null;
        }
        OffersView view = new OffersView();
        if (offers.size() >= count) {
            view.offers = offers.subList(0, count);
        } else {
            view.offers = offers;
        }
        return view;
    }

    public List<Offer> getOffers() {
        return offers;
    }
}
