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
package spring.travel.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import spring.travel.api.model.Loyalty;
import spring.travel.api.model.Offer;
import spring.travel.api.model.Profile;
import spring.travel.api.request.Request;
import spring.travel.api.request.RequestInfo;
import spring.travel.api.services.LoyaltyService;
import spring.travel.api.services.OffersService;
import spring.travel.api.services.ProfileService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static spring.travel.api.compose.Tasks.parallel;

@RestController
@RequestMapping("/")
public class HomeController extends OptionalUserController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private LoyaltyService loyaltyService;

    @Autowired
    private OffersService offersService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<List<Offer>> home(@RequestInfo Request requestInfo) {
        return withOptionalUser(requestInfo,
            (request, response) -> parallel(
                profileService.profile(request.getUser()),
                loyaltyService.loyalty(request.getUser())
            ).onCompletion(
                (result) -> {
                    Optional<Profile> profile = result.flatMap(t -> t.a());
                    Optional<Loyalty> loyalty = result.flatMap(t -> t.b());
                    offersService.offers(profile, loyalty).onCompletion(
                        (offers) -> response.setResult(offers.orElse(Collections.emptyList()))
                    ).execute();
                }
            ).execute()
        );
    }
}
