package spring.travel.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.async.DeferredResult;
import spring.travel.api.auth.AuthException;
import spring.travel.api.auth.PlaySessionCookieBaker;
import spring.travel.api.auth.Session;
import spring.travel.api.services.UserService;

import java.util.Map;
import java.util.Optional;

public class OptionalUserController {

    private Logger logger = LoggerFactory.getLogger(OptionalUserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PlaySessionCookieBaker cookieBaker;

    <T> DeferredResult<T> withOptionalUser(Optional<Session> session, OptionalUserAction<T> action) {
        DeferredResult<T> result = new DeferredResult<>();

        if (session.isPresent()) {
            withSession(session.get(), result, action);
        } else {
            withoutSession(result, action);
        }

        return result;
    }

    private <T> void withSession(Session session, DeferredResult<T> result, OptionalUserAction<T> action) {
        try {
            Map<String,String> sessionVariables = cookieBaker.decode(session.getValue());
            Optional<String> userId = Optional.ofNullable(sessionVariables.get("id"));
            userService.user(userId).onCompletion(
                (user) -> action.execute(result, user)
            ).execute();
        } catch (AuthException ae) {
            logger.warn("Error trying to decode session cookie [%s]", session, ae);
            action.execute(result, Optional.empty());
        }
    }

    private <T> void withoutSession(DeferredResult<T> result, OptionalUserAction<T> action) {
        action.execute(result, Optional.empty());
    }
}
