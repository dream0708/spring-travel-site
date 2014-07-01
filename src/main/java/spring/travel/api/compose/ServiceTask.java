package spring.travel.api.compose;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

@FunctionalInterface
public interface ServiceTask<R> {

    ListenableFuture<ResponseEntity<R>> execute();
}
