package spring.travel.api.compose;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Optional;

public class Callback<T> implements ListenableFutureCallback<ResponseEntity<T>> {

    private final SuccessHandler<Optional<T>> successHandler;
    private final FailureHandler failureHandler;

    public Callback(SuccessHandler<Optional<T>> successHandler) {
        this.successHandler = successHandler;
        failureHandler = (t) -> successHandler.handle(Optional.ofNullable(null));
    }

    public Callback(SuccessHandler<Optional<T>> successHandler, FailureHandler failureHandler) {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @Override
    public void onSuccess(ResponseEntity<T> responseEntity) {
        successHandler.handle(Optional.ofNullable(responseEntity.getBody()));
    }

    @Override
    public void onFailure(Throwable throwable) {
        failureHandler.handle(throwable);
    }
}
