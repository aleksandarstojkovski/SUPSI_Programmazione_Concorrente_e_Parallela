package testing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Sensor implements Runnable {
    private final int id;
    private final int threshold;
    private final Counter counter;


    Sensor(int id, int threshold, Counter counter) {
        this.id = id;
        this.threshold = threshold;
        this.counter = counter;
    }

    @Override
    public void run() {
        System.out.println("Sensor" + id + " started (threshold " + threshold + ") ");

        while (counter.getValue() < threshold)
            /*do nothing*/;

        System.out.println(String.format("Sensor%d: Above threshold %d", id, threshold));
        counter.reset();
    }
}

interface Counter {
    int getValue();
    void reset();
    void add(int delta);
}

class CounterNoSync implements Counter {
    private int value;

    public int getValue() { return value; }
    public void reset() { value = 0; }
    public void add(int delta) { value += delta; }
}

class CounterVolatile implements Counter {
    private volatile int value;

    public int getValue() { return value; }
    public void reset() { value = 0; }
    public void add(int delta) { value += delta; }
}

class CounterAtomic implements Counter {
    private AtomicInteger value = new AtomicInteger();

    public int getValue() { return value.get(); }
    public void reset() { value.set(0); }
    public void add(int delta) { value.getAndAdd(delta); }
}

class CounterExplicit implements Counter {
    private int value;
    Lock lock = new ReentrantLock();

    public int getValue() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        lock.lock();
        try {
            value = 0;
        } finally {
            lock.unlock();
        }
    }

    public void add(int delta) {
        lock.lock();
        try {
            value += delta;
        } finally {
            lock.unlock();
        }
    }
}


public class Main {
    private final static int THRESHOLD_RANGE = 10;
    private final static int NUM_SENSORS = 10;
    public static final int MAX_COUNTER = 120;

    public static void main(final String[] args) {
        Counter counter = new CounterNoSync();
        //Counter counter = new CounterVolatile();
        //Counter counter = new CounterAtomic();
        //Counter counter = new CounterExplicit();

        final List<Sensor> allSensors = new ArrayList<>();
        final List<Thread> allThread = new ArrayList<>();
        for (int i = 1; i <= NUM_SENSORS; i++) {
            final int threshold = THRESHOLD_RANGE * i;
            final Sensor target = new Sensor(i, threshold, counter);
            allSensors.add(target);
            final Thread e = new Thread(target);
            allThread.add(e);
            e.start();
        }

        do {
            //System.out.println(String.format("Counter %d", Sensor.value));

            int delay = ThreadLocalRandom.current().nextInt(5, 11);
            int randomValue = ThreadLocalRandom.current().nextInt(1, 9);
            counter.add(randomValue);
            //Sensor.value += randomValue;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //} while (Sensor.value < MAX_COUNTER);
        } while (counter.getValue() < MAX_COUNTER);
        System.out.println("Main thread completed");
    }
}
