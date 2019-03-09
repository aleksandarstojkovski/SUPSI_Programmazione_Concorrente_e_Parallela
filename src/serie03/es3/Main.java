package serie03.es3;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private final static int NUMBER_OF_THREADS=10;

    public static void main(String[] args) {

        int array[] = new int[] {0,0,0,0,0};

        List<Worker> workers = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for(int i=0; i<NUMBER_OF_THREADS;i++){
            Worker worker = new Worker(i,array);
            workers.add(worker);
            Thread thread = new Thread(worker);
            threads.add(thread);
            thread.start();
        }

        System.out.println("Attendo che threads terminino");

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Tutti i thread hanno terminato");


    }
}
