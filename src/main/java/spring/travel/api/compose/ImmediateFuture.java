package spring.travel.api.compose;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ImmediateFuture<T> implements ListenableFuture<ResponseEntity<T>> {
    @Override
    public void addCallback(ListenableFutureCallback<? super ResponseEntity<T>> listenableFutureCallback) {
        listenableFutureCallback.onSuccess(new ResponseEntity<T>((T)null, HttpStatus.NO_CONTENT));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public ResponseEntity<T> get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public ResponseEntity<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
