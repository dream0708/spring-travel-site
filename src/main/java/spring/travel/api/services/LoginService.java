package spring.travel.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.AsyncTask;
import spring.travel.api.compose.ListenableFutureAsyncTaskAdapter;
import spring.travel.api.model.LoginData;
import spring.travel.api.model.User;

public class LoginService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private String url;

    public LoginService(String url) {
        this.url = url;
    }

    public AsyncTask<User> login(LoginData loginData) {
        HttpEntity<LoginData> entity = new HttpEntity<>(loginData);
        return new ListenableFutureAsyncTaskAdapter<>(
            () -> asyncRestTemplate.postForEntity(url, entity, User.class)
        );
    }
}
