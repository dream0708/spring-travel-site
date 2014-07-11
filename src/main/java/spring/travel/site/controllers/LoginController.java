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
package spring.travel.site.controllers;

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
import spring.travel.site.auth.AuthException;
import spring.travel.site.auth.UserNotFoundException;
import spring.travel.site.auth.PlaySessionCookieBaker;
import spring.travel.site.model.LoginData;
import spring.travel.site.model.user.User;
import spring.travel.site.services.LoginService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private PlaySessionCookieBaker cookieBaker;

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
                        result.setErrorResult(new UserNotFoundException());
                    }
                } else {
                    result.setErrorResult(new UserNotFoundException());
                }
            }
        ).execute();
        return result;
    }

    @ExceptionHandler
    @SuppressWarnings("unused")
    public ResponseEntity<Void> handleNotFound(UserNotFoundException nfe) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<User> success(User user) throws AuthException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", cookie(user) + "; path=/");
        return new ResponseEntity<>(user, headers, HttpStatus.OK);
    }

    private String cookie(User user) throws AuthException {
        Map<String, String> values = new HashMap<>();
        values.put("id", user.getId());
        return cookieBaker.encode(values);
    }
}
