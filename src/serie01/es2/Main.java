package serie01.es2;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int timeToSleep;
        int numberOfThreads = 2;
        List<Thread> listOfThreads = new ArrayList<Thread>();

        System.out.println("Creo " + numberOfThreads + " threads");

        for (int i=0;i<numberOfThreads;i++){

           Thread t = new Thread();
           listOfThreads.add(t);
        }




    }


}
