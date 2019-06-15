package testing;

import java.util.concurrent.*;

class CallableTask implements Callable<String> {

    @Override
    public String call() throws Exception {
        return null;
    }
}

public class Main {
    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(2);
        CallableTask ct = new CallableTask();
        FutureTask<String> caller = new FutureTask<>(ct);
        es.submit(caller);
        try {
            caller.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
