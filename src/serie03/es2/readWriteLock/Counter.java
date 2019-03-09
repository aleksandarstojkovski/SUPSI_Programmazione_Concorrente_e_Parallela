package serie03.es2.readWriteLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Counter {

    ReadWriteLock lock = new ReentrantReadWriteLock();
    Lock readLock = lock.readLock();
    Lock writeLock = lock.writeLock();
    int value;

    Counter(){
        value=0;
    }

    public int getValue() {
        readLock.lock();
        try{
            return value;
        }finally {
            readLock.unlock();
        }
    }

    public void setValue(int value) {
        writeLock.lock();
        this.value = value;
        writeLock.unlock();
    }

    public void addValue(int value) {
        writeLock.lock();
        this.value+=value;
        writeLock.unlock();
    }
}
