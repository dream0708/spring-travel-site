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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.Callback;
import spring.travel.api.compose.SuccessHandler;
import spring.travel.api.model.Profile;

import java.util.Optional;

public class ProfileService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private String url;

    public ProfileService(String url) {
        this.url = url;
    }

    public void profile(Optional<String> id, SuccessHandler<Optional<Profile>> successHandler) {
        if (id.isPresent()) {
            asyncRestTemplate.getForEntity(url + "/" + id.get(), Profile.class).
                    addCallback(new Callback<>(successHandler));
        } else {
            successHandler.handle(Optional.<Profile>empty());
        }
    }
}
