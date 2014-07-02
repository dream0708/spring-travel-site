package spring.travel.api.compose;

public class ParallelServiceTask<R1, R2> {

    private ServiceTask<R1> serviceTask1;

    private ServiceTask<R2> serviceTask2;

    private ParallelCollector<R1, R2> parallelCollector;

    public ParallelServiceTask(ServiceTask<R1> serviceTask1, ServiceTask<R2> serviceTask2) {
        this.serviceTask1 = serviceTask1;
        this.serviceTask2 = serviceTask2;
    }

    public void execute(ParallelCompletionHandler<R1, R2> completionHandler) {
        parallelCollector = new ParallelCollector<>(
                (r1, r2) -> completionHandler.handle(r1, r2)
        );
        serviceTask1.execute().addCallback(new Callback<R1>(
                (r1) -> parallelCollector.updateA(r1)
        ));
        serviceTask2.execute().addCallback(new Callback<R2>(
                (r2) -> parallelCollector.updateB(r2)
        ));
    }
}
