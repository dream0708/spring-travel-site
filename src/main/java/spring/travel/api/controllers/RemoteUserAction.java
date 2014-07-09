package spring.travel.api.controllers;

import org.springframework.web.context.request.async.DeferredResult;
import spring.travel.api.model.RemoteUser;
import spring.travel.api.model.User;

import java.util.Optional;

@FunctionalInterface
public interface RemoteUserAction<T> {

    void execute(DeferredResult<T> result, RemoteUser user);
}
