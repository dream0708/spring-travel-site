package spring.travel.api.compose;

import java.util.Optional;

public class ParallelCollector<A, B> {

    private Optional<A> a;

    private Optional<B> b;

    private ParallelCompletionHandler<A, B> completionHandler;

    public ParallelCollector(ParallelCompletionHandler<A, B> completionHandler) {
        this.completionHandler = completionHandler;
    }

    public synchronized void updateA(Optional<A> a) {
        this.a = a;
        if (b != null) {
           completionHandler.handle(a, b);
        }
    }

    public synchronized void updateB(Optional<B> b) {
        this.b = b;
        if (a != null) {
            completionHandler.handle(a, b);
        }
    }
}
