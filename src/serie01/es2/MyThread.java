package serie01.es2;

public class MyThread implements Runnable {
    @Override
    public void run() {

        int timeToSleep;
        timeToSleep = (int)(Math.random() * 2000) + 1500;

        try {
            Thread.sleep(timeToSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
