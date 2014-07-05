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

public class ParallelCollector<A, B> {

    private Optional<A> a;

    private Optional<B> b;

    private final CompletionHandler<Tuple2<Optional<A>, Optional<B>>> completionHandler;

    public ParallelCollector(CompletionHandler<Tuple2<Optional<A>, Optional<B>>> completionHandler) {
        this.completionHandler = completionHandler;
    }

    public synchronized void updateA(Optional<A> a) {
        this.a = a;
        if (b != null) {
           completionHandler.handle(new Tuple2<>(a, b));
        }
    }

    public synchronized void updateB(Optional<B> b) {
        this.b = b;
        if (a != null) {
            completionHandler.handle(new Tuple2<>(a, b));
        }
    }
}
