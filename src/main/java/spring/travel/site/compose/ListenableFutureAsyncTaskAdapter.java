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

public class ListenableFutureAsyncTaskAdapter<A> implements AsyncTask<A>, Executable {

    private final ServiceTask<A> serviceTask;

    private volatile CompletionHandler<Optional<A>> completionHandler;

    private volatile CompletionHandler<Optional<A>> beforeCompletionHandler;

    public ListenableFutureAsyncTaskAdapter(ServiceTask<A> serviceTask) {
        this(serviceTask, (t) -> { ; }); // do nothing!
    }

    public ListenableFutureAsyncTaskAdapter(ServiceTask<A> serviceTask, CompletionHandler<Optional<A>> beforeCompletionHandler) {
        this.serviceTask = serviceTask;
        this.beforeCompletionHandler = beforeCompletionHandler;
    }

    @Override
    public void execute() {
        ListenableFuture<ResponseEntity<A>> future = serviceTask.execute();
        future.addCallback(new ListenableFutureCallback<ResponseEntity<A>>() {
            @Override
            public void onSuccess(ResponseEntity<A> responseEntity) {
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
    public Executable onCompletion(CompletionHandler<Optional<A>> completionHandler) {
        this.completionHandler = completionHandler;
        return this;
    }
}
