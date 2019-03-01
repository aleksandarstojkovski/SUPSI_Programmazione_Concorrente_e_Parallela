package serie01.es3;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int[] array = new int[100000];
        int numberOfThreads=10;
        int firstElementOfIntervall=0;
        int secondElementOfIntervall=9999;
        List<Thread> listOfThreads = new ArrayList<>();

        System.out.println("Riempio l'array di " + array.length + " elementi random");
        for(int i=0;i<array.length;i++){
            array[i]=(int) (Math.random() * (100 - 1)) + 1;
        }

        System.out.println("Creo " + numberOfThreads + " threads");

        for (int i=0;i<10;i++){
            MyThread mt = new MyThread(array,firstElementOfIntervall,secondElementOfIntervall);
            Thread t = new Thread(mt);
            listOfThreads.add(t);
            firstElementOfIntervall+=10000;
            secondElementOfIntervall+=10000;
        }

        System.out.println("Starto i " + numberOfThreads + " threads");

        for (Thread t : listOfThreads){
            t.start();
        }

    }

}
