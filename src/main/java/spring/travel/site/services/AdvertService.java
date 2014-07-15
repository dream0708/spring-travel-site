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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.site.compose.AsyncTask;
import spring.travel.site.compose.ListenableFutureAsyncTaskAdapter;
import spring.travel.site.model.Advert;
import spring.travel.site.model.user.Profile;

import java.util.List;
import java.util.Optional;

public class AdvertService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private String url;

    public AdvertService(String url) {
        this.url = url;
    }

    public AsyncTask<List<Advert>> adverts(int count, Optional<Profile> profile) {
        return new ListenableFutureAsyncTaskAdapter<>(
            () -> {
                String targetQueryString = profile.map(p -> "&target=" + target(p)).orElse("");
                ParameterizedTypeReference<List<Advert>> typeRef = new ParameterizedTypeReference<List<Advert>>() {};
                return asyncRestTemplate.exchange(url + "?count=" + count + targetQueryString,
                    HttpMethod.GET, null, typeRef);
            }
        );
    }

    private String target(Profile profile) {
        switch (profile.getSpending()) {
            case Economy: return "low";
            case Standard: return "middle";
            case Luxury: return "high";
        }
        throw new IllegalStateException("Unexpected Spending value '" + profile.getSpending() + "'");
    }
}
