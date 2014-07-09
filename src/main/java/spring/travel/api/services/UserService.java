package spring.travel.api.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.AsyncTask;
import spring.travel.api.compose.ImmediatelyNoneAsyncTaskAdapter;
import spring.travel.api.compose.ListenableFutureAsyncTaskAdapter;
import spring.travel.api.model.User;

import java.util.Optional;

public class UserService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private String url;

    public UserService(String url) {
        this.url = url;
    }

    public AsyncTask<User> user(Optional<String> id) {
        if (id.isPresent()) {
            return new ListenableFutureAsyncTaskAdapter<>(
                () -> asyncRestTemplate.getForEntity(url + "?id=" + id.get(), User.class)
            );
        } else {
            return new ImmediatelyNoneAsyncTaskAdapter();
        }
    }
}
