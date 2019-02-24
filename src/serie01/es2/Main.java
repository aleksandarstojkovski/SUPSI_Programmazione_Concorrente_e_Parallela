package serie01.es2;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        int timeToSleep;
        int numberOfThreads = 2;
        Map<Thread,MyThread> mapThreadRunnable = new HashMap<>();

        System.out.println("Creo " + numberOfThreads + " threads");

        for (int i=0;i<numberOfThreads;i++){
            timeToSleep = (int) (Math.random() * (2000 - 1500)) + 1500;
            MyThread mt = new MyThread(timeToSleep);
            Thread t = new Thread(mt);
            mapThreadRunnable.put(t,mt);
        }

        System.out.println("Starto " + numberOfThreads + " threads");

        for (Map.Entry<Thread,MyThread> me : mapThreadRunnable.entrySet()){
            System.out.println("--> starto "+me.getKey());
            me.getKey().start();
        }

        System.out.println("In attesa che i " + numberOfThreads + " abbiano terminato");

        for (Map.Entry<Thread,MyThread> me : mapThreadRunnable.entrySet()){
            try {
                me.getKey().join();
                System.out.println(me.getKey().getName() + " risvegliato dopo " + me.getValue().getTimeToSleep() + "ms");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


}
