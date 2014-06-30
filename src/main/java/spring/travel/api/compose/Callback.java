/**
 * Copyright 2014 Andy Godwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spring.travel.api.compose;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Optional;

public class Callback<T> implements ListenableFutureCallback<ResponseEntity<T>> {

    private final SuccessHandler<Optional<T>> successHandler;
    private final FailureHandler failureHandler;

    public Callback(SuccessHandler<Optional<T>> successHandler) {
        this.successHandler = successHandler;
        failureHandler = (t) -> successHandler.handle(Optional.<T>empty());
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
