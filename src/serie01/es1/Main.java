package serie01.es1;

import java.util.ArrayList;
import java.util.Collection;

public class Main {

    private static long fibonacci(){
        long fibo1 = 1, fibo2 = 1, fibonacci = 1;
        for (int i = 3; i <= 700; i++) {
            fibonacci = fibo1 + fibo2;
            fibo1 = fibo2;
            fibo2 = fibonacci;
        }
        return fibonacci;
    }

    public static Collection<Thread> createThreadsWithAnonymousInnerClass(){
        Collection<Thread> allThreads = new ArrayList<Thread>();
        for (int i = 1; i <= 5; i++) {
            System.out.println("Main: creo thread " + i);
            Thread t = new Thread() {

                @Override
                public void run() {
                    long fibo1 = 1, fibo2 = 1, fibonacci = 1;
                    for (int i = 3; i <= 700; i++) {
                        fibonacci = fibo1 + fibo2;
                        fibo1 = fibo2;
                        fibo2 = fibonacci;
                    }
                    /* Stampa risultato */
                    System.out.println(this + ": " + fibonacci);
                }
            };
            allThreads.add(t);
        }
        return  allThreads;
    }

    public static Collection<Thread> createThreadsUsingMyThreadExtendsThread(){
        Collection<Thread> allThreads = new ArrayList<Thread>();
        for (int i = 1; i <= 5; i++) {
            System.out.println("Main: creo thread " + i);
            Thread t = new MyThreadExtendsThread();
            allThreads.add(t);
        }
        return  allThreads;
    }

    public static Collection<Thread> createThreadsUsingMyThreadImplementsRunnable(){
        Collection<Thread> allThreads = new ArrayList<Thread>();
        for (int i = 1; i <= 5; i++) {
            System.out.println("Main: creo thread " + i);
            Thread t = new Thread(new MyThreadExtendsRunnable());
            allThreads.add(t);
        }
        return  allThreads;
    }

//    public static void createThreadsUsingLambda(){
//        (thread) -> new Thread(()-> System.out.println(thread + " : " + fibonacci()) );
//    }

    public static void main(String[] args) {

        // Using Thread defined by anonymous inner class
        // Collection<Thread> allThreads = createThreadsWithAnonymousInnerClass();

        // Using Thread that extends Thread
        // Collection<Thread> allThreads = createThreadsUsingMyThreadExtendsThread();

        // Using Thread that extends Runnable
        Collection<Thread> allThreads = createThreadsUsingMyThreadImplementsRunnable();

        /* Avvio dei threads */
        for (Thread t : allThreads){
            System.out.println("Starto il thread " + t );
            t.start();
        }

        /* Attendo terminazione dei threads */
        for (Thread t : allThreads) {
            try {
                System.out.println("Attendo la terminazione di " + t);
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

