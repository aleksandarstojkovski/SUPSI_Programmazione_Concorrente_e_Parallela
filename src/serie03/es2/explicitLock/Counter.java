package serie03.es2.explicitLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {

    Lock lock = new ReentrantLock();
    int value;

    Counter(){
        value=0;
    }

    public int getValue() {
        lock.lock();
        try{
            return value;
        }finally {
            lock.unlock();
        }
    }

    public void setValue(int value) {
        lock.lock();
        this.value = value;
        lock.unlock();
    }

    public void addValue(int value) {
        lock.lock();
        this.value+=value;
        lock.unlock();
    }
}
