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

import java.util.List;

public class AdvertsView {

    private List<Advert> adverts;

    private AdvertsView() {
    }

    public static AdvertsView from(List<Advert> adverts, int count) {
        if (adverts.size() == 0) {
            return null;
        }
        AdvertsView view = new AdvertsView();
        if (adverts.size() >= count) {
            view.adverts = adverts.subList(0, count);
        } else {
            view.adverts = adverts;
        }
        return view;
    }

    public List<Advert> getAdverts() {
        return adverts;
    }
}
