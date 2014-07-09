package spring.travel.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.async.DeferredResult;
import spring.travel.api.auth.AuthException;
import spring.travel.api.auth.PlaySessionCookieBaker;
import spring.travel.api.auth.Session;
import spring.travel.api.services.UserService;

import java.util.Map;
import java.util.Optional;

public class OptionalUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlaySessionCookieBaker cookieBaker;

    <T> DeferredResult<T> withOptionalUser(Optional<Session> session, OptionalUserAction<T> action) {

        DeferredResult<T> result = new DeferredResult<>();

        if (session.isPresent()) {
            Session s = session.get();
            try {
                Map<String,String> sessionVariables = cookieBaker.decode(s.getValue());
                Optional<String> userId = Optional.ofNullable(sessionVariables.get("id"));
                userService.user(userId).onCompletion(
                    (user) -> action.execute(result, user)
                ).execute();
            } catch (AuthException ae) {
                ae.printStackTrace();
                action.execute(result, Optional.empty());
            }
        } else {
            action.execute(result, Optional.empty());
        }

        return result;
    }
}
