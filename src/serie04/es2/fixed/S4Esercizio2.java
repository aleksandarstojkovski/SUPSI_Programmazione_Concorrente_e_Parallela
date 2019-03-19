package serie04.es2.fixed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

class Sensore implements Runnable {
	private final int soglia;

	public Sensore(final int soglia) {
		this.soglia = soglia;
	}

	@Override
	public void run() {
		System.out.println("Sensore[" + soglia + "]: inizio ad osservare!");

		while (!resetIfAbove()) {
			/* Busy wait */ 
		}

		System.out.println("Sensore[" + soglia + "]: soglia superata!");
	}
	
	private boolean resetIfAbove() {
		int currentAmount = S4Esercizio2.counter.get();
		if (currentAmount < soglia)
			return false;
		S4Esercizio2.counter.set(0);
		return true;
	}
}

public class S4Esercizio2 {
	// shared counter
	static final AtomicInteger counter = new AtomicInteger();

	public static void main(final String[] args) {
		final List<Thread> threads = new ArrayList<>();

		for (int i = 1; i <= 10; i++) {
			final int soglia_sensore = (i * 10);
			threads.add(new Thread(new Sensore(soglia_sensore)));
		}

		// start all threads
		threads.forEach(Thread::start);

		while (true) {
			final int increment = ThreadLocalRandom.current().nextInt(1, 9);
			final int newAmount = counter.addAndGet(increment);
			System.out.println("Attuatore: ho incrementato stato a " + newAmount);
			if (newAmount > 120)
				break;
			try {
				Thread.sleep(ThreadLocalRandom.current().nextLong(5, 11));
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		// wait all threads to terminate
		for (final Thread t : threads) {
			try {
				t.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}