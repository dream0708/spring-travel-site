package spring.travel.api.auth;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class OptionalSessionInterceptor extends HandlerInterceptorAdapter {

    private String cookieName;

    public OptionalSessionInterceptor(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Arrays.asList(request.getCookies()).stream().filter(
            c -> c.getName().equals(cookieName) // equalsIgnoreCase ?
        ).findFirst().ifPresent(
            c -> request.setAttribute("session", new Session(c.getValue()))
        );

        return true;
    }
}
