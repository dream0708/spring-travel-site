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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestInfoInterceptorTest {

    private String cookieName = "TEST_COOKIE";

    private String attributeName = "TEST_REQUEST";

    private String ipAddress = "192.168.3.54";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    RequestInfoInterceptor interceptor;

    @Before
    public void before() {
        interceptor = new RequestInfoInterceptor(cookieName, attributeName);
        when(request.getRemoteAddr()).thenReturn(ipAddress);
    }

    @Test
    public void shouldSetRequestInfoAttributeWithIpAddressIfNoCookiePresent() throws Exception {
        Cookie[] cookies = new Cookie[0];
        when(request.getCookies()).thenReturn(cookies);

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, times(1)).setAttribute(eq(attributeName), requestCaptor.capture());
        Request requestInfo = requestCaptor.getValue();

        assertEquals(Optional.empty(), requestInfo.getCookieValue());
        assertEquals(ipAddress, requestInfo.getRemoteAddress());
        assertEquals(Optional.empty(), requestInfo.getUser());
    }

    @Test
    public void shouldSetRequestInfoAttributeWithIpAddressIfNoSessionCookiePresent() throws Exception {
        Cookie cookie1 = mock(Cookie.class);
        Cookie cookie2 = mock(Cookie.class);
        Cookie[] cookies = new Cookie[] {
            cookie1, cookie2
        };

        when(request.getCookies()).thenReturn(cookies);
        when(cookie1.getName()).thenReturn("SOME_COOKIE");
        when(cookie2.getName()).thenReturn("ANOTHER_COOKIE");

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, times(1)).setAttribute(eq(attributeName), requestCaptor.capture());
        Request requestInfo = requestCaptor.getValue();

        assertEquals(Optional.empty(), requestInfo.getCookieValue());
        assertEquals(ipAddress, requestInfo.getRemoteAddress());
        assertEquals(Optional.empty(), requestInfo.getUser());
    }

    @Test
    public void shouldSetRequestInfoAttributeIfSessionCookieIsPresent() throws Exception {
        Cookie cookie = mock(Cookie.class);
        Cookie[] cookies = new Cookie[] {
            cookie
        };

        String cookieValue = "90348039864-id=111";

        when(request.getCookies()).thenReturn(cookies);
        when(cookie.getName()).thenReturn(cookieName);
        when(cookie.getValue()).thenReturn(cookieValue);

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, times(1)).setAttribute(eq(attributeName), requestCaptor.capture());
        Request requestInfo = requestCaptor.getValue();

        assertEquals(Optional.of(cookieValue), requestInfo.getCookieValue());
        assertEquals(ipAddress, requestInfo.getRemoteAddress());
        assertEquals(Optional.empty(), requestInfo.getUser());
    }
}
