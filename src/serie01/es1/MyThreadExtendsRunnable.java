package serie01.es1;

public class MyThreadExtendsRunnable implements Runnable {
    public void run(){
        long fibo1 = 1, fibo2 = 1, fibonacci = 1;
        for (int i = 3; i <= 700; i++) {
            fibonacci = fibo1 + fibo2;
            fibo1 = fibo2;
            fibo2 = fibonacci;
        }
        /* Stampa risultato */
        System.out.println(this + ": " + fibonacci);
    }
}
