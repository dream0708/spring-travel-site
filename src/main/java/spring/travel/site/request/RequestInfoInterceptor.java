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
package spring.travel.site.request;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
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
        request.setAttribute(attributeName, new Request(Optional.empty(), getSessionCookie(request), request.getRemoteAddr()));
        return true;
    }

    private Optional<String> getSessionCookie(HttpServletRequest request) {
        Enumeration<String> cookies = request.getHeaders("Cookie");
        if (cookies != null) {
            while (cookies.hasMoreElements()) {
                String cookie = cookies.nextElement();
                if (cookie.startsWith(cookieName) && cookie.indexOf('=') > 0) {
                    return getCookieValue(cookie);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<String> getCookieValue(String cookie) {
        String value = cookie.substring(cookie.indexOf('=') + 1);
        if (value.startsWith("\"")) {
            value = value.substring(1);
        }
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        return Optional.of(value);
    }
}
