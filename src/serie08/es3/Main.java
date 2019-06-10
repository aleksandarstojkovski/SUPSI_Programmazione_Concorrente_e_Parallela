package serie08.es3;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Testimone{}

class Corridore implements Runnable{

    private int id;
    private Squadra squadra;
    private Testimone testimone;

    Corridore(int id, Squadra squadra, Testimone testimone){
        this.id=id;
        this.squadra=squadra;
        this.testimone=testimone;
    }

    @Override
    public void run() {
        if (id == 0){
            print("in attesa del segnale di partenza");
            Main.corridoriPronti.countDown();
            try {
                Main.corridoriPronti.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            corri();
        } else {
            print("in attesa del testimone");
            Main.corridoriPronti.countDown();
            while (testimone == null){
                squadra.getLock().lock();
                try {
                    squadra.getAttendiTurno().await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    squadra.getLock().unlock();
                }
            }
            print("testimone ricevuto!");
            corri();
        }
    }

    private void print(String string){
        System.out.println("Corridore"+this.id+"_Squadra"+squadra.getId()+ ": " +string);
    }

    private void corri(){
        int randomNumber = ThreadLocalRandom.current().nextInt(100, 150);
        print("corro per "+randomNumber+"ms");
        try {
            Thread.sleep(randomNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (id<Main.N_GIOCATORI-1) {
            print("passo testimone a " + "Corridore" + (id + 1) + "_Squadra" + squadra.getId());
            squadra.passaTestimone(id + 1, testimone);
            testimone = null;
            squadra.getLock().lock();
            try {
                squadra.getAttendiTurno().signalAll();
            } finally {
                squadra.getLock().unlock();
            }
        } else {
            Main.ultimoGruppo.incrementAndGet();
            synchronized (Main.ultimoGruppo){
                if (Main.ultimoGruppo.get()==1){
                    print("VINTO!!!!!!!");
                } else {
                    print("...perso...");
                }
            }
        }
    }

    public void setTestimone(Testimone testimone){
        this.testimone=testimone;
    }

}

class Squadra {
    private int id;
    private ArrayList<Corridore> corridori = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    private Condition attendiTurno = lock.newCondition();

    Squadra(int id){
        this.id=id;
    }

    void addCorridore(Corridore c){
        corridori.add(c);
    }

    public int getId() {
        return id;
    }

    public ArrayList<Corridore> getCorridori() {
        return corridori;
    }

    public Condition getAttendiTurno() {
        return attendiTurno;
    }

    public Lock getLock() {
        return lock;
    }

    void passaTestimone(int id, Testimone testimone){
        corridori.get(id).setTestimone(testimone);
    }
}

public class Main {

    public static int N_SQUADRE=4;
    public static int N_GIOCATORI=10;
    public static CountDownLatch corridoriPronti = new CountDownLatch(N_SQUADRE*N_GIOCATORI);
    public static final AtomicInteger ultimoGruppo= new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {

        Squadra squadra;

        for (int i=0;i<N_SQUADRE;i++){
            squadra = new Squadra(i);
            for (int j=0;j<N_GIOCATORI;j++){
                Corridore corridore;
                if (j==0)
                    corridore = new Corridore(j, squadra, new Testimone());
                else
                    corridore = new Corridore(j, squadra, null);
                squadra.addCorridore(corridore);
                Thread t = new Thread(corridore);
                t.start();
            }
        }

        corridoriPronti.await();
        System.out.println("Pronti...Partenza...Via");

    }

}
