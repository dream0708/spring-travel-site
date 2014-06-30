package spring.travel.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.Callback;
import spring.travel.api.compose.SuccessHandler;
import spring.travel.api.model.Profile;

import java.util.Optional;

public class ProfileService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    public void profile(Optional<String> id, SuccessHandler<Optional<Profile>> successHandler) {
        if (id.isPresent()) {
            asyncRestTemplate.getForEntity("http://localhost:9091/profile/" + id.get(), Profile.class).
                    addCallback(new Callback<>(successHandler));
        } else {
            successHandler.handle(Optional.<Profile>empty());
        }
    }
}
