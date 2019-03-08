package serie02.es3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {

        double saldoIniziale=100000;
        Conto conto = new Conto(saldoIniziale);
        List<Utente> utenti = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        double totalePrelieviUtenti=0;

        for (int i=0;i<100;i++){
            Utente ut = new Utente((int) ThreadLocalRandom.current().nextLong(5, 20),conto,i);
            utenti.add(ut);
            Thread t = new Thread(ut);
            threads.add(t);
            t.start();;
        }

        for (Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        for(Utente ut : utenti){
            totalePrelieviUtenti+=ut.getTotalePrelievi();
        }

        System.out.println("Saldo inziale conto: " + saldoIniziale);
        System.out.println("Totale prelievi: " + totalePrelieviUtenti);

    }

}
