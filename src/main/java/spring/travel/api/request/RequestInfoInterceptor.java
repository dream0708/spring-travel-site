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

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RequestInfoInterceptor extends HandlerInterceptorAdapter {

    private String cookieName;
    private String attributeName;

    public RequestInfoInterceptor(String cookieName, String attributeName) {
        this.cookieName = cookieName;
        this.attributeName = attributeName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        List<Cookie> cookies = Optional.ofNullable(request.getCookies()).map(
            c -> Arrays.asList(c)
        ).orElse(
            Collections.emptyList()
        );
        Optional<String> cookie = cookies.stream().filter(
            c -> c.getName().equals(cookieName) // equalsIgnoreCase ?
        ).findFirst().map(c -> c.getValue());

        request.setAttribute(attributeName, new Request(Optional.empty(), cookie, request.getRemoteAddr()));

        return true;
    }
}
