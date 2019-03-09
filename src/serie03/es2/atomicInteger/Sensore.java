package serie03.es2.atomicInteger;

import java.util.concurrent.atomic.AtomicInteger;

public class Sensore implements Runnable{
    private final int id;
    private static AtomicInteger contatore;
    private final int soglia;
    private int maxContatore;

    Sensore(int id, int soglia){
        this.id = id;
        this.soglia = soglia;
        contatore = new AtomicInteger(0);
        maxContatore=0;
    }


    @Override
    public void run() {

        while (contatore.get()<soglia){
            //wait
        }

        // notifica soglia superata
        System.out.println("Sensore " + id + " - contatore ha superato la soglia di " + soglia );

        // prima di resettare il contatore, mi salvo il massimo raggiunto
        maxContatore=contatore.get();

        // azzero contatore
        contatore.set(0);

    }

    public static void incrementaContatore(int incremento){
        contatore.addAndGet(incremento);
    }

    public static int getContatore() {
        return contatore.get();
    }

    @Override
    public String toString() {
        return "Sensore " + id + "(soglia" + soglia + ")";
    }

    public int getMaxContatore() {
        return maxContatore;
    }
}
