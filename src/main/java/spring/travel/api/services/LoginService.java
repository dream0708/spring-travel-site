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
import org.springframework.http.HttpEntity;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.AsyncTask;
import spring.travel.api.compose.ListenableFutureAsyncTaskAdapter;
import spring.travel.api.model.LoginData;
import spring.travel.api.model.user.User;

public class LoginService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private String url;

    public LoginService(String url) {
        this.url = url;
    }

    public AsyncTask<User> login(LoginData loginData) {
        HttpEntity<LoginData> entity = new HttpEntity<>(loginData);
        return new ListenableFutureAsyncTaskAdapter<>(
            () -> asyncRestTemplate.postForEntity(url, entity, User.class)
        );
    }
}
