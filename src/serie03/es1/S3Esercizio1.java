package serie03.es1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class Worker implements Runnable {
	public volatile static boolean isRunning = false;
	public static AtomicInteger finished;
    //public static int finished;

	private int count = 0;
	private final int id;
	private final Random random;

	public Worker(final int id) {
		this.id = id;
		this.random = new Random();
		finished = new AtomicInteger(0);
		//finished=0;
	}

	@Override
	public void run() {
		System.out.println("Worker" + id + " waiting to start");
		while (!isRunning) {
			// Wait!
		}

		System.out.println("Worker" + id + " started");
		for (int i = 0; i < 10; i++) {
			count += random.nextInt(40) + 10;
			try {
				Thread.sleep(random.nextInt(151) + 100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Worker" + id + " finished");
		finished.incrementAndGet();
        //finished++;
	}

	public void printResult() {
		System.out.println("Worker" + id + " reached " + count);
	}
}

public class S3Esercizio1 {
	public static void main(final String[] args) {

		final List<Worker> allWorkers = new ArrayList<>();
		final List<Thread> allThread = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			final Worker target = new Worker(i);
			allWorkers.add(target);
			final Thread e = new Thread(target);
			allThread.add(e);
			e.start();
		}

		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

		System.out.println("Main thread starting the race!");
		Worker.isRunning = true;

		while (Worker.finished.get() < allWorkers.size()) {
			// Wait
        }

		for (final Worker worker : allWorkers) {
			worker.printResult();
		}

		for (final Thread thread : allThread) {
			try {
				thread.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}