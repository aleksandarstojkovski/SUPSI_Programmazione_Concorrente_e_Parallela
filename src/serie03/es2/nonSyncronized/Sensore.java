package serie03.es2.nonSyncronized;

public class Sensore implements Runnable{
    private final int id;
    public static int contatore;
    private final int soglia;

    Sensore(int id, int soglia){
        this.id=id;
        this.soglia=soglia;
    }

    @Override
    public void run() {
        System.out.println("sono partito" + contatore);
        while (contatore<soglia){
            //wait
        }

        // notifica soglia superata
        System.out.println("Sensore " + id + " - contatore ha superato la soglia di " + soglia );

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
}
