package spring.travel.api.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OptionalSessionInterceptorTest {

    private String cookieName = "TEST_COOKIE";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Captor
    private ArgumentCaptor<Session> sessionCaptor;

    OptionalSessionInterceptor interceptor = new OptionalSessionInterceptor(cookieName);

    @Test
    public void shouldNotSetSessionAttributeIfNoCookiesArePresent() throws Exception {
        Cookie[] cookies = new Cookie[0];
        when(request.getCookies()).thenReturn(cookies);

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, never()).setAttribute(anyString(), anyObject());
    }

    @Test
    public void shouldNotSetSessionAttributeIfNoSessionCookieIsPresent() throws Exception {
        Cookie cookie = mock(Cookie.class);
        Cookie[] cookies = new Cookie[] {
            cookie
        };

        when(request.getCookies()).thenReturn(cookies);
        when(cookie.getName()).thenReturn("SOME_COOKIE");

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, never()).setAttribute(anyString(), anyObject());
    }

    @Test
    public void shouldSetSessionAttributeIfSessionCookieIsPresent() throws Exception {
        Cookie cookie = mock(Cookie.class);
        Cookie[] cookies = new Cookie[] {
            cookie
        };

        String cookieValue = "90348039864-id=111";

        when(request.getCookies()).thenReturn(cookies);
        when(cookie.getName()).thenReturn(cookieName);
        when(cookie.getValue()).thenReturn(cookieValue);

        assertTrue(interceptor.preHandle(request, response, new Object()));

        verify(request, times(1)).setAttribute(eq("session"), sessionCaptor.capture());
        Session session = sessionCaptor.getValue();
        assertEquals(cookieValue, session.getValue());
    }
}
