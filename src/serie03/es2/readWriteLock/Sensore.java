package serie03.es2.readWriteLock;

public class Sensore implements Runnable{

    private final int id;
    private Counter counter;
    private final int soglia;
    private int maxContatore;

    Sensore(int id, int soglia, Counter counter){
        this.id=id;
        this.soglia=soglia;
        this.counter=counter;
        maxContatore=0;
    }

    @Override
    public void run() {

        while (counter.getValue()<soglia){
            //wait
        }

        // notifica soglia superata
        System.out.println("Sensore " + id + " - contatore ha superato la soglia di " + soglia );

        // prima di resettare il contatore, mi salvo il massimo raggiunto
        maxContatore=counter.getValue();

        // azzero contatore
        counter.setValue(0);

    }

    @Override
    public String toString() {
        return "Sensore " + id + "(soglia" + soglia + ")";
    }

    public int getMaxContatore() {
        return maxContatore;
    }
}
