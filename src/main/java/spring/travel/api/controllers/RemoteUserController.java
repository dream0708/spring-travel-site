package spring.travel.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.async.DeferredResult;
import spring.travel.api.auth.AuthException;
import spring.travel.api.auth.PlaySessionCookieBaker;
import spring.travel.api.model.RemoteUser;
import spring.travel.api.services.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class RemoteUserController {

    private Logger logger = LoggerFactory.getLogger(RemoteUserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PlaySessionCookieBaker cookieBaker;

    @Value("${session.cookieName}")
    private String cookieName;

    <T> DeferredResult<T> withRemoteUser(HttpServletRequest request, RemoteUserAction<T> action) {
        DeferredResult<T> result = new DeferredResult<>();

        Optional<Cookie> cookie = Arrays.asList(request.getCookies()).stream().filter(
            c -> c.getName().equals(cookieName) // equalsIgnoreCase ?
        ).findFirst();

        if (cookie.isPresent()) {
            withCookie(cookie.get(), request.getRemoteAddr(), result, action);
        } else {
            withoutCookie(request.getRemoteAddr(), result, action);
        }

        return result;
    }

    private <T> void withCookie(Cookie cookie, String ipAddress, DeferredResult<T> result, RemoteUserAction<T> action) {
        try {
            Map<String,String> sessionVariables = cookieBaker.decode(cookie.getValue());
            Optional<String> userId = Optional.ofNullable(sessionVariables.get("id"));
            userService.user(userId).onCompletion(
                (user) -> action.execute(result, RemoteUser.from(user, ipAddress))
            ).execute();
        } catch (AuthException ae) {
            logger.warn("Error trying to decode session cookie [%s]", cookie, ae);
            action.execute(result, RemoteUser.from(ipAddress));
        }
    }

    private <T> void withoutCookie(String ipAddress, DeferredResult<T> result, RemoteUserAction<T> action) {
        action.execute(result, RemoteUser.from(ipAddress));
    }
}
