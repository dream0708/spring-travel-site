package spring.travel.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import spring.travel.api.compose.Callback;
import spring.travel.api.compose.ParallelCollector;
import spring.travel.api.model.Loyalty;
import spring.travel.api.model.Offer;
import spring.travel.api.model.Profile;
import spring.travel.api.services.LoyaltyService;
import spring.travel.api.services.OffersService;
import spring.travel.api.services.ProfileService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private LoyaltyService loyaltyService;

    @Autowired
    private OffersService offersService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<List<Offer>> home(@RequestParam(value = "id", required = false) String id) {
        Optional<String> userId = Optional.ofNullable(id);

        final DeferredResult<List<Offer>> result = new DeferredResult<>();

        ParallelCollector<Profile, Loyalty> parallelCollector = new ParallelCollector<>(
                (profile, loyalty) -> offersService.offers(profile, loyalty,
                        (offers) -> result.setResult(offers.orElse(Collections.emptyList()))
                )
        );

        profileService.profile(userId,
                (profile) -> parallelCollector.updateA(profile)
        );

        loyaltyService.loyalty(userId,
                (loyalty) -> parallelCollector.updateB(loyalty)
        );

        return result;
    }
}
