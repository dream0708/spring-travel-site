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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
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
    public void shouldSetRequestInfoAttributeWithIpAddressIfRequestCookiesAreNull() throws Exception {
        when(request.getHeaders("Cookie")).thenReturn(null);

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, times(1)).setAttribute(eq(attributeName), requestCaptor.capture());
        Request requestInfo = requestCaptor.getValue();

        assertEquals(Optional.empty(), requestInfo.getCookieValue());
        assertEquals(ipAddress, requestInfo.getRemoteAddress());
        assertEquals(Optional.empty(), requestInfo.getUser());
    }

    @Test
    public void shouldSetRequestInfoAttributeWithIpAddressIfNoCookiePresent() throws Exception {
        Enumeration<String> cookieHeaders = Collections.emptyEnumeration();
        when(request.getHeaders("Cookie")).thenReturn(cookieHeaders);

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, times(1)).setAttribute(eq(attributeName), requestCaptor.capture());
        Request requestInfo = requestCaptor.getValue();

        assertEquals(Optional.empty(), requestInfo.getCookieValue());
        assertEquals(ipAddress, requestInfo.getRemoteAddress());
        assertEquals(Optional.empty(), requestInfo.getUser());
    }

    @Test
    public void shouldSetRequestInfoAttributeWithIpAddressIfNoSessionCookiePresent() throws Exception {
        List<String> cookies = Arrays.asList("SOME_COOKIE=gkdsjlsdijg", "ANOTHER_COOKIE=soihgweitj");
        when(request.getHeaders("Cookie")).thenReturn(Collections.enumeration(cookies));

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, times(1)).setAttribute(eq(attributeName), requestCaptor.capture());
        Request requestInfo = requestCaptor.getValue();

        assertEquals(Optional.empty(), requestInfo.getCookieValue());
        assertEquals(ipAddress, requestInfo.getRemoteAddress());
        assertEquals(Optional.empty(), requestInfo.getUser());
    }

    @Test
    public void shouldSetRequestInfoAttributeIfSessionCookieIsPresent() throws Exception {
        String cookieValue = "90348039864-id=111";
        List<String> cookies = Arrays.asList(cookieName + "=" + cookieValue);
        when(request.getHeaders("Cookie")).thenReturn(Collections.enumeration(cookies));

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, times(1)).setAttribute(eq(attributeName), requestCaptor.capture());
        Request requestInfo = requestCaptor.getValue();

        assertEquals(Optional.of(cookieValue), requestInfo.getCookieValue());
        assertEquals(ipAddress, requestInfo.getRemoteAddress());
        assertEquals(Optional.empty(), requestInfo.getUser());
    }

    @Test
    public void shouldSetRequestInfoAttributeIfSessionCookieIsPresentButEmpty() throws Exception {
        List<String> cookies = Arrays.asList(cookieName + "=");
        when(request.getHeaders("Cookie")).thenReturn(Collections.enumeration(cookies));

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, times(1)).setAttribute(eq(attributeName), requestCaptor.capture());
        Request requestInfo = requestCaptor.getValue();

        assertEquals(Optional.of(""), requestInfo.getCookieValue());
        assertEquals(ipAddress, requestInfo.getRemoteAddress());
        assertEquals(Optional.empty(), requestInfo.getUser());
    }
}
