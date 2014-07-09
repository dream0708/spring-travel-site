package spring.travel.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import spring.travel.api.auth.AuthException;
import spring.travel.api.auth.PlaySessionCookieBaker;
import spring.travel.api.model.LoginData;
import spring.travel.api.model.User;
import spring.travel.api.services.LoginService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

//    @Autowired
//    private PlaySessionCookieBaker cookieBaker;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity<User>> login(@Valid @ModelAttribute LoginData loginData) {
        DeferredResult<ResponseEntity<User>> result = new DeferredResult();
        loginService.login(loginData).onCompletion(
            (user) -> {
                if (user.isPresent()) {
                    try {
                        result.setResult(success(user.get()));
                    } catch (AuthException ae) {
                        result.setErrorResult(new NotFoundException());
                    }
                } else {
                    result.setErrorResult(new NotFoundException());
                }
            }
        ).execute();
        return result;
    }

    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<Void> handleNotFound(NotFoundException nfe) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<User> success(User user) throws AuthException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "GETAWAY_SESSION=" + cookie(user) + "; path=/");
        return new ResponseEntity<>(user, headers, HttpStatus.OK);
    }

    private String cookie(User user) throws AuthException {
        Map<String, String> values = new HashMap<>();
        values.put("id", user.getId());
//        return cookieBaker.encode(values);
        return user.getId();
    }
}
