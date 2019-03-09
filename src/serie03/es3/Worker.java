package serie03.es3;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Worker implements Runnable {

    int array[];
    private int id;
    private Random random;
    private final static Lock lock = new ReentrantLock();

    Worker(int id, int array[]){
        this.id=id;
        this.array=array;
        random=new Random();
    }

    @Override
    public void run() {
        for (int i=0; i<10000;i++){

            if (i % 1000 == 0 && i !=0){
                System.out.println("Worker " + id + " ha già effetuato " + i + " cicli");
            }

            int randomPosition=random.nextInt(5);
            int randomNumber=random.nextInt(50)+10;
            int randomTime=random.nextInt(5)+2;

            lock.lock();
            try {
                array[randomPosition] = array[randomPosition] + randomNumber;
                if (array[randomPosition] > 500) {
                    array[randomPosition] = 0;
                }
            } finally {
                lock.unlock();
            }

            try {
                Thread.sleep(randomTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
