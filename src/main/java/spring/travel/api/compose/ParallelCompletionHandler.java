package spring.travel.api.compose;

import java.util.Optional;

@FunctionalInterface
public interface ParallelCompletionHandler<A, B> {

    void handle(Optional<A> a, Optional<B> b);
}
