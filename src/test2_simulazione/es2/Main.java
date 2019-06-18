package test2_simulazione.es2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Commissario implements Runnable{

    private Long[][] matrix = new Long[Main.N_SCIATORI][Main.N_TAPPPE];
    private AtomicInteger tempiComunicati = new AtomicInteger();
    private Lock lock = new ReentrantLock();
    private Condition tuttiHannoComunicatoLaNota = lock.newCondition();

    @Override
    public void run() {
        try {
            Main.sciatoriPronti.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Tutti pronti! Si Parte!");
        System.out.println("Pronti... Partenza... Via!");
        for (int i=0;i<Main.N_TAPPPE;i++){
            lock.lock();
            try{
                try {
                    tuttiHannoComunicatoLaNota.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                lock.unlock();
            }
            tempiComunicati.set(0);
            System.out.println("Fase" + i + " finita!");
            Main.lock.lock();
            try{
                Main.commissarioHaPresoNota.signalAll();
            } finally {
                Main.lock.unlock();
            }
        }

    }

    void comunicaTempo(int id, long tempo, int fase){
        matrix[id][fase] = tempo;
        tempiComunicati.incrementAndGet();
        if (tempiComunicati.get()>=Main.N_SCIATORI){
            lock.lock();
            try{
                tuttiHannoComunicatoLaNota.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public void risultati(){
        System.out.println("Di seguito i risultati:");
        System.out.println(Arrays.deepToString(matrix));
    }
}

class Sciatore implements Runnable{

    private final int id;
    private Commissario commissario;

    Sciatore(int id, Commissario commissario){
        this.id=id;
        this.commissario=commissario;
    }

    @Override
    public void run() {
        print("aspetto la partenza");
        Main.sciatoriPronti.countDown();
        try {
            Main.sciatoriPronti.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i=0;i<Main.N_TAPPPE;i++) {
            long startTime = System.currentTimeMillis();
            print("partito per la fase" + i);
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(4, 8 + 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long estimatedTime = System.currentTimeMillis() - startTime;
            commissario.comunicaTempo(id, estimatedTime,i);
            Main.lock.lock();
            try {
                try {
                    Main.commissarioHaPresoNota.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                Main.lock.unlock();
            }
        }
        Main.sciatoriCheHannoFinito.countDown();
    }

    private void print(String s){
        System.out.println("Sciatore"+id+": " + s);
    }
}

public class Main {
    static final int N_SCIATORI=5;
    static final int N_TAPPPE=6;
    static CountDownLatch sciatoriPronti = new CountDownLatch(N_SCIATORI);
    static CountDownLatch sciatoriCheHannoFinito = new CountDownLatch(N_SCIATORI);
    static Lock lock = new ReentrantLock();
    static Condition commissarioHaPresoNota = lock.newCondition();


    public static void main(String[] args) {

        List<Runnable> runnables = new ArrayList<>();
        Commissario commissario = new Commissario();
        runnables.add(commissario);

        for (int i=0;i<N_SCIATORI;i++){
            Sciatore sciatore = new Sciatore(i, commissario);
            runnables.add(sciatore);
        }

        runnables.forEach(e -> new Thread(e).start());

        try {
            sciatoriCheHannoFinito.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("--------- TUTTI HANNO FINITO -----------");
        commissario.risultati();

    }

}
