package serie01.es3;

public class MyThread implements Runnable {

    private int[] arrayOfIntegers;
    private int firstElementOfIntervall;
    private int lastElementOfIntervall;

    MyThread(int[] arrayOfIntegers, int firstElementOfIntervall, int lastElementOfIntervall){
        this.arrayOfIntegers=arrayOfIntegers;
        this.firstElementOfIntervall=firstElementOfIntervall;
        this.lastElementOfIntervall=lastElementOfIntervall;
    }

    @Override
    public void run() {
        int sum=0;
        // ho reso appositamente il calcolo piu difficile, facendoglielo fare 10000000 di volte
        // in questo modo posso osservare i threads che lavorano
        for (int j=0;j<1000000;j++) {
            sum=0;
            for (int i = firstElementOfIntervall; i < lastElementOfIntervall; i++) {
                sum += arrayOfIntegers[i];
            }
        }
        System.out.println("Somma degli elementi nell'intervallo [" + firstElementOfIntervall + ";" + lastElementOfIntervall + "]" + " = " + sum);
    }
}
