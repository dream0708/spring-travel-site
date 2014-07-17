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
package spring.travel.site.compose;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Optional;

public class ListenableFutureAsyncTaskAdapter<T> implements AsyncTask<T>, Executable {

    private final ServiceTask<T> serviceTask;

    private volatile CompletionHandler<Optional<T>> completionHandler;

    private volatile CompletionHandler<Optional<T>> beforeCompletionHandler;

    public ListenableFutureAsyncTaskAdapter(ServiceTask<T> serviceTask) {
        this(serviceTask, (t) -> { ; }); // do nothing!
    }

    public ListenableFutureAsyncTaskAdapter(ServiceTask<T> serviceTask, CompletionHandler<Optional<T>> beforeCompletionHandler) {
        this.serviceTask = serviceTask;
        this.beforeCompletionHandler = beforeCompletionHandler;
    }

    @Override
    public void execute() {
        ListenableFuture<ResponseEntity<T>> future = serviceTask.execute();
        future.addCallback(new ListenableFutureCallback<ResponseEntity<T>>() {
            @Override
            public void onSuccess(ResponseEntity<T> responseEntity) {
                beforeCompletionHandler.handle(Optional.ofNullable(responseEntity.getBody()));
                completionHandler.handle(Optional.ofNullable(responseEntity.getBody()));
            }

            @Override
            public void onFailure(Throwable throwable) {
                beforeCompletionHandler.handle(Optional.empty());
                completionHandler.handle(Optional.empty());
            }
        });
    }

    @Override
    public Executable onCompletion(CompletionHandler<Optional<T>> completionHandler) {
        this.completionHandler = completionHandler;
        return this;
    }
}
