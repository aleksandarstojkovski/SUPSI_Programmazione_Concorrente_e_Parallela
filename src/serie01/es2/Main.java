package serie01.es2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        int timeToSleep;
        int numberOfThreads = 2;

        System.out.println("Creo " + numberOfThreads + " threads e li starto");

        for (int i=0;i<numberOfThreads;i++){
            timeToSleep = (int) (Math.random() * (2000 - 1500)) + 1500;
            MyThread mt = new MyThread(timeToSleep);
            Thread t = new Thread(mt);
            mt.setThreadId(t.getName());
            t.start();
        }

    }

}
