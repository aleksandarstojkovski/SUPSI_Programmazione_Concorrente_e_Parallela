package serie03.es2.explicitLock;

public class Sensore implements Runnable{

    private final int id;
    private Counter counter;
    private final int soglia;

    Sensore(int id, int soglia, Counter counter){
        this.id=id;
        this.soglia=soglia;
        this.counter=counter;
    }

    @Override
    public void run() {

        while (counter.getValue()<soglia){
            //wait
        }

        // notifica soglia superata
        System.out.println("Sensore " + id + " - contatore ha superato la soglia di " + soglia );

        // azzero contatore
        counter.setValue(0);

    }

    @Override
    public String toString() {
        return "Sensore " + id + "(soglia" + soglia + ")";
    }

}
