package serie08.es4.syncronizers;

import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

class Fantino implements Runnable{

    private final int id;

    Fantino(int id){
        this.id=id;
        Main.phaser.register();
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
        Main.phaser.arriveAndAwaitAdvance();
        long finish = System.nanoTime();
        print("ha atteso "+ TimeUnit.NANOSECONDS.toMillis(finish-start)+" ms");
    }

    private void print(String s){
        System.out.println("Fantino"+id+": "+s);
    }

}

public class Main {

    public static int N_FANTINI=10;
    public static Phaser phaser = new Phaser(0);

    public static void main(String[] args) {

        for (int i=0;i<N_FANTINI;i++){
            Fantino fantino = new Fantino(i);
            Thread thread = new Thread(fantino);
            thread.start();
        }


    }
}
