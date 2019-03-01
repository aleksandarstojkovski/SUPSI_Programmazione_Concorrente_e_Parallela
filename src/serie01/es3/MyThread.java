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
        for (int i = firstElementOfIntervall; i < lastElementOfIntervall; i++) {
            sum += arrayOfIntegers[i];
        }
        System.out.println("Somma degli elementi nell'intervallo [" + firstElementOfIntervall + ";" + lastElementOfIntervall + "]" + " = " + sum);
    }
}
