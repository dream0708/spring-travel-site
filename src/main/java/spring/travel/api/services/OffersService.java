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
package spring.travel.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.AsyncTask;
import spring.travel.api.compose.ListenableFutureAsyncTaskAdapter;
import spring.travel.api.model.Loyalty;
import spring.travel.api.model.Offer;
import spring.travel.api.model.Profile;

import java.util.List;
import java.util.Optional;

public class OffersService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private String url;

    public OffersService(String url) {
        this.url = url;
    }

    public AsyncTask<List<Offer>> offers(Optional<Profile> profile, Optional<Loyalty> loyalty) {
        return new ListenableFutureAsyncTaskAdapter<>(
            () -> {
                ParameterizedTypeReference<List<Offer>> typeRef = new ParameterizedTypeReference<List<Offer>>() {};
                return asyncRestTemplate.exchange(url + queryString(profile, loyalty), HttpMethod.GET, null, typeRef);
            }
        );
    }

    private String queryString(Optional<Profile> profile, Optional<Loyalty> loyalty) {
        StringBuilder builder = new StringBuilder();
        if (profile.isPresent()) {
            builder.append("?lifecycle=").append(profile.get().getLifecycle().toString().toLowerCase()).
                    append("&spending=").append(profile.get().getSpending().toString().toLowerCase()).
                    append("&gender=").append(profile.get().getGender().toString().toLowerCase());
        }
        if (loyalty.isPresent()) {
            builder.append(profile.isPresent() ? "&" : "?");
            builder.append("group=").append(loyalty.get().getGroup().toString().toLowerCase()).
                    append("&points=").append(loyalty.get().getPoints());
        }
        return builder.toString();
    }
}
