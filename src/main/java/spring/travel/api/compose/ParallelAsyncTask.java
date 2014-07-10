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

import java.util.Optional;

public class ParallelAsyncTask<A, B> implements AsyncTask<Tuple2<Optional<A>, Optional<B>>>, Executable {

    private final AsyncTask<A> asyncA;

    private final AsyncTask<B> asyncB;

    private volatile CompletionHandler<Optional<Tuple2<Optional<A>, Optional<B>>>> completionHandler;

    public ParallelAsyncTask(AsyncTask<A> asyncA, AsyncTask<B> asyncB) {
        this.asyncA = asyncA;
        this.asyncB = asyncB;
    }

    @Override
    public void execute() {
        ParallelCollector<A, B> collector = new ParallelCollector<>(
            (tuple) -> completionHandler.handle(Optional.of(tuple))
        );
        asyncA.onCompletion((a) -> collector.updateA(a)).execute();
        asyncB.onCompletion((b) -> collector.updateB(b)).execute();
    }

    @Override
    public Executable onCompletion(CompletionHandler<Optional<Tuple2<Optional<A>, Optional<B>>>> completionHandler) {
        this.completionHandler = completionHandler;
        return this;
    }
}
