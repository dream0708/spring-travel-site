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

import java.util.Optional;

public class ImmediatelySomethingAsyncTaskAdapter<T> implements AsyncTask<T>, Executable  {

    private CompletionHandler<Optional<T>> completionHandler;
    private T result;

    public ImmediatelySomethingAsyncTaskAdapter(T result) {
        this.result = result;
    }

    @Override
    public Executable onCompletion(CompletionHandler<Optional<T>> completionHandler) {
        this.completionHandler = completionHandler;
        return this;
    }

    @Override
    public void execute() {
        completionHandler.handle(Optional.ofNullable(result));
    }
}
