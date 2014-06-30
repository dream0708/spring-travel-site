package spring.travel.api.compose;

@FunctionalInterface
public interface FailureHandler {

    void handle(Throwable t);
}
