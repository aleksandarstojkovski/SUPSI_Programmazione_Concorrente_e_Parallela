package serie03.es2.volatileInteger;

public class Sensore implements Runnable{
    private final int id;
    private volatile static int contatore;
    private final int soglia;
    private int maxContatore;

    Sensore(int id, int soglia){
        this.id=id;
        this.soglia=soglia;
        maxContatore=0;
    }


    @Override
    public void run() {

        while (contatore<soglia){
            //wait
        }

        // notifica soglia superata
        System.out.println("Sensore " + id + " - contatore ha superato la soglia di " + soglia );

        // prima di resettare il contatore, mi salvo il massimo raggiunto
        maxContatore=contatore;

        // azzero contatore
        contatore=0;

    }

    public static void incrementaContatore(int incremento){
        contatore+=incremento;
    }

    public static int getContatore() {
        return contatore;
    }

    @Override
    public String toString() {
        return "Sensore " + id + "(soglia" + soglia + ")";
    }

    public int getMaxContatore() {
        return maxContatore;
    }
}
