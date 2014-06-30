package spring.travel.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.Callback;
import spring.travel.api.compose.SuccessHandler;
import spring.travel.api.model.Loyalty;

import java.util.Optional;

public class LoyaltyService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    public void loyalty(Optional<String> id, SuccessHandler<Optional<Loyalty>> successHandler) {
        if (id.isPresent()) {
            asyncRestTemplate.getForEntity("http://localhost:9091/loyalty/" + id.get(), Loyalty.class).
                    addCallback(new Callback<>(successHandler));
        } else {
            successHandler.handle(Optional.<Loyalty>empty());
        }
    }
}
