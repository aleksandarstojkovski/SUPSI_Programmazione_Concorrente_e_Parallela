package serie01.es2;
import java.util.concurrent.TimeUnit;


public class MyThread implements Runnable {

    private long startTime;
    private long endTime;
    private long timeElapsed;
    private int timeToSleep;

    MyThread(int timeToSleep){
        this.timeToSleep=timeToSleep;
    }

    @Override
    public void run() {
        try {
            startTime = System.nanoTime();
            Thread.sleep(this.timeToSleep);
            endTime = System.nanoTime();
            timeElapsed = endTime - startTime;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public long getTimeElapsed() {
        return timeElapsed/1000000;
    }

    int getTimeToSleep() {
        return timeToSleep;
    }
}
