package serie01.es2;

public class MyThread implements Runnable {

    private int timeToSleep;

    MyThread(int timeToSleep){
        this.timeToSleep=timeToSleep;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.timeToSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    int getTimeToSleep() {
        return timeToSleep;
    }
}
