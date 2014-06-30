package spring.travel.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.Callback;
import spring.travel.api.compose.FailureHandler;
import spring.travel.api.compose.SuccessHandler;
import spring.travel.api.model.Loyalty;
import spring.travel.api.model.Offer;
import spring.travel.api.model.Profile;

import java.util.List;
import java.util.Optional;

public class OffersService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    public void offers(Optional<Profile> profile, Optional<Loyalty> loyalty,
                       SuccessHandler<Optional<List<Offer>>> successHandler, FailureHandler failureHandler) {
        ParameterizedTypeReference<List<Offer>> typeRef = new ParameterizedTypeReference<List<Offer>>() {};
        asyncRestTemplate.exchange("http://localhost:9091/offers/123", HttpMethod.GET, null, typeRef).
                addCallback(new Callback<>(successHandler, failureHandler));
    }
}
