package spring.travel.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.Callback;
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
                       SuccessHandler<Optional<List<Offer>>> successHandler) {

        ParameterizedTypeReference<List<Offer>> typeRef = new ParameterizedTypeReference<List<Offer>>() {};

        asyncRestTemplate.exchange("http://localhost:9091/offers" + queryString(profile, loyalty),
                HttpMethod.GET, null, typeRef).
                addCallback(new Callback<>(successHandler));
    }

    private String queryString(Optional<Profile> profile, Optional<Loyalty> loyalty) {
        StringBuilder builder = new StringBuilder();
        if (profile.isPresent()) {
            builder.append("?lifecycle=").append(profile.get().getLifecycle().toString().toLowerCase()).
                    append("&spending=").append(profile.get().getSpending().toString().toLowerCase()).
                    append("&gender=").append(profile.get().getGender().toString().toLowerCase());
        }
        if (loyalty.isPresent()) {
            builder.append(profile.isPresent() ? "&" : "?");
            builder.append("group=").append(loyalty.get().getGroup().toString().toLowerCase()).
                    append("&points=").append(loyalty.get().getPoints());
        }
        return builder.toString();
    }
}
