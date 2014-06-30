package spring.travel.api.compose;

@FunctionalInterface
public interface SuccessHandler<T> {

    void handle(T t);
}
