package serie08.es4.locks;

import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Fantino implements Runnable{

    private final int id;

    Fantino(int id){
        this.id=id;
    }

    @Override
    public void run() {
        int timeToWait=ThreadLocalRandom.current().nextInt(1000,1050);
        try {
            Thread.sleep(timeToWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        print("arrivo alla linea di partenza");
        long start = System.nanoTime();
        Main.atomicInteger.incrementAndGet();
        Main.lock.lock();
        Main.arrived.signalAll();
        try{
            while (Main.atomicInteger.get()<Main.N_FANTINI){
                Main.arrived.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Main.lock.unlock();
        }
        long finish = System.nanoTime();
        print("ha atteso "+ TimeUnit.NANOSECONDS.toMillis(finish-start)+" ms");
    }

    private void print(String s){
        System.out.println("Fantino"+id+": "+s);
    }

}

public class Main {

    public static int N_FANTINI=10;
    public static Lock lock = new ReentrantLock();
    public static Condition arrived = lock.newCondition();
    public static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {

        for (int i=0;i<N_FANTINI;i++){
            Fantino fantino = new Fantino(i);
            Thread thread = new Thread(fantino);
            thread.start();
        }


    }
}
