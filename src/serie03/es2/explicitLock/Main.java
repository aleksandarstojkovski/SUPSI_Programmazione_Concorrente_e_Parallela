package serie03.es2.explicitLock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        List<Sensore> sensori = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        Random random = new Random();
        Counter counter = new Counter();
        int totaleTuttiContatori=0;
        int totaleTuttiIncrementiMain=0;

        for (int i=1; i <= 10; i++){
            Sensore sensore = new Sensore(i,i*10,counter);
            sensori.add(sensore);
            Thread thread = new Thread(sensore);
            threads.add(thread);
            System.out.println("Starto " + sensore);
            thread.start();
        }

        while(counter.getValue()<120){
            int increment = random.nextInt(9)+1;
            int timeToWait = random.nextInt(11)+5;

            counter.addValue(increment);
            totaleTuttiIncrementiMain+=increment;

            try {
                Thread.sleep(timeToWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(Sensore sensore : sensori)
            totaleTuttiContatori+=sensore.getMaxContatore();

        System.out.println("\nTotale contatori di tutti i sensori: " + totaleTuttiContatori);
        System.out.println("Totale incrementi effetuati nel main: " + totaleTuttiIncrementiMain);
        System.out.println("Valore finale contatore: " + counter.getValue());

        if (totaleTuttiContatori+ counter.getValue() == totaleTuttiIncrementiMain){
            System.out.println("\nRISULTATO CORRETTO: " + totaleTuttiContatori + " + " + counter.getValue() + " = " + (totaleTuttiContatori+ counter.getValue()) );
        } else {
            System.out.println("\nRISULTATO NON CORRETTO: " + totaleTuttiContatori + " + " + counter.getValue() + " != " + (totaleTuttiContatori+ counter.getValue()) );
        }

    }
}
