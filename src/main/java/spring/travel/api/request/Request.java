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
package spring.travel.api.request;

import spring.travel.api.model.User;

import java.util.Optional;

public class Request {

    Optional<User> user;

    Optional<String> cookieValue;

    String remoteAddress;

    public Request(Optional<User> user, Optional<String> cookieValue, String remoteAddress) {
        this.user = user;
        this.cookieValue = cookieValue;
        this.remoteAddress = remoteAddress;
    }

    public Optional<User> getUser() {
        return user;
    }

    public Optional<String> getCookieValue() {
        return cookieValue;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }
}
