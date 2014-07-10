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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.async.DeferredResult;
import spring.travel.api.auth.AuthException;
import spring.travel.api.auth.PlaySessionCookieBaker;
import spring.travel.api.request.Request;
import spring.travel.api.services.UserService;

import java.util.Map;
import java.util.Optional;

public class OptionalUserController {

    private Logger logger = LoggerFactory.getLogger(OptionalUserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PlaySessionCookieBaker cookieBaker;

    @Value("${session.cookieName}")
    private String cookieName;

    <T> DeferredResult<T> withOptionalUser(Request request, OptionalUserAction<T> action) {
        DeferredResult<T> response = new DeferredResult<>();

        if (request.getCookieValue().isPresent()) {
            withCookie(request, response, action);
        } else {
            withoutCookie(request, response, action);
        }

        return response;
    }

    private <T> void withCookie(Request request, DeferredResult<T> response, OptionalUserAction<T> action) {
        try {
            Map<String,String> sessionVariables = cookieBaker.decode(request.getCookieValue().get());
            Optional<String> userId = Optional.ofNullable(sessionVariables.get("id"));
            userService.user(userId).onCompletion(
                (user) -> action.execute(new Request(user, request.getCookieValue(), request.getRemoteAddress()), response)
            ).execute();
        } catch (AuthException ae) {
            logger.warn("Error trying to decode session cookie [%s]", request.getCookieValue().get(), ae);
            action.execute(request, response);
        }
    }

    private <T> void withoutCookie(Request request, DeferredResult<T> response, OptionalUserAction<T> action) {
        action.execute(request, response);
    }
}
