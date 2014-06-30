package spring.travel.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.Callback;
import spring.travel.api.compose.FailureHandler;
import spring.travel.api.compose.SuccessHandler;
import spring.travel.api.model.Loyalty;

import java.util.Optional;

public class LoyaltyService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    public ListenableFuture<ResponseEntity<Loyalty>> loyalty(Optional<String> id) {
        return asyncRestTemplate.getForEntity("http://localhost:9091/lyalty/" + id, Loyalty.class);
    }

    public void loyalty(Optional<String> id, SuccessHandler<Optional<Loyalty>> successHandler, FailureHandler failureHandler) {
        if (id.isPresent()) {
            asyncRestTemplate.getForEntity("http://localhost:9091/lyalty/" + id, Loyalty.class).
                    addCallback(new Callback<>(successHandler, failureHandler));
        } else {
            successHandler.handle(Optional.<Loyalty>empty());
        }
    }
}
