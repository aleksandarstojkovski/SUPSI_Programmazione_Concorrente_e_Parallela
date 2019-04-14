package test1_simulazione.es3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class Magazzino{
    public volatile static boolean aperto=false;
    static Prodotto[] prodotti = new Prodotto[10];
    Random random = new Random();

    Magazzino(){
        for (int i=0;i<prodotti.length;i++){
            prodotti[i]=new Prodotto("Prodotto"+i,random.nextInt(10)+1,random.nextInt(5)+1);
        }
    }

    public static boolean isAperto() {
        return aperto;
    }

    public static Prodotto getProdotto(int index){
        return prodotti[index];
    }

}
class Prodotto{
    private static AtomicInteger prodottiDisponibili = new AtomicInteger();
    String nome;
    int quantita;
    int prezzo;

    Prodotto(String nome, int quantita, int prezzo){
        this.prezzo=prezzo;
        this.quantita=quantita;
        this.nome=nome;
        prodottiDisponibili.incrementAndGet();
    }

    public boolean acquista(){
        synchronized (this){
            if (quantita==0){
                return false;
            }
            quantita--;
            if (quantita==0){
                prodottiDisponibili.decrementAndGet();
            }
            return true;
        }
    }

    public static boolean prodottiDisponibili(){
        return prodottiDisponibili.get()>0;
    }
}
class Cliente implements Runnable{

    int soldi = 20;
    private final int id;
    private final Random random = new Random();
    int tentativi=0;
    private static AtomicInteger clientiAttivi = new AtomicInteger();

    Cliente(int id){
        this.id=id;
    }

    @Override
    public void run() {
        clientiAttivi.incrementAndGet();
        while (!Magazzino.isAperto()){
            //wait
        }
        while(true){
            Prodotto prodotto = Magazzino.getProdotto(random.nextInt(10));
            boolean possoComprarlo= soldi>=prodotto.prezzo;
            if (possoComprarlo && prodotto.acquista()){
                System.out.println(id + " - Acquisto "+prodotto.nome);
                soldi-=prodotto.prezzo;
                tentativi=0;
            } else {
                tentativi++;
            }

            if (soldi==0){
                System.out.println(id + " - ho finito i soldi");
                break;
            }
            if (tentativi>=10){
                System.out.println(id + " - ho finito i tentativi");
                break;
            }
            if (!Magazzino.isAperto()){
                System.out.println(id + " - negozio chiuso");
                break;
            }

            try {
                Thread.sleep(1+random.nextInt(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(id + " - TERMINATO");
    }

    static int getClientiAttivi(){
        return clientiAttivi.get();
    }

}
public class Main {

    static Magazzino negozio = new Magazzino();

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        for (int i=0;i<10;i++){
            Cliente cliente = new Cliente(i);
            Thread t = new Thread(cliente);
            threads.add(t);
        }

        for (Thread t : threads){
            t.start();
        }

        while(Cliente.getClientiAttivi()!=10){
            //wait
        }

        Magazzino.aperto=true;

        for (Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("----------------------------");

        for (Prodotto prodotto : Magazzino.prodotti){
            System.out.println(prodotto.nome + " -> "+ prodotto.quantita);
        }

    }
}
