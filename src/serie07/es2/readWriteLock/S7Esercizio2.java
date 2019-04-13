package serie07.es2.readWriteLock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class S7Es2Timer {
	private long startTime = -1;
	private long endTime = -1;

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public void stop() {
		endTime = System.currentTimeMillis();
	}

	public long getElapsedTime() {
		if (startTime < 0 || endTime < 0)
			return 0;
		return endTime - startTime;
	}
}

class ReadWorker implements Runnable {
	final int id;
	private int changesRecognized = 0;
	private int totCompares = 0;

	public ReadWorker(final int id) {
		this.id = id;
	}

	@Override
	public void run() {
		String localReferencePhrase = null;
		int compareCounter = 0;
		while (S7Esercizio2.isRunning) {
			compareCounter++;
			final StringBuffer sb = new StringBuffer();

			// Build phrase string from shares words
			S7Esercizio2.readLock.lock();
			try {
				final Iterator<String> iterator = S7Esercizio2.sharedPhrase.iterator();
				while (iterator.hasNext()) {
					sb.append(iterator.next());
					sb.append(" ");
				}
			}finally {
				S7Esercizio2.readLock.unlock();
			}

			// Compare strings: if shared pharse has changed update local
			// reference phrase
			final String readString = sb.toString();
			if (!readString.equals(localReferencePhrase)) {
				localReferencePhrase = readString;

				System.out.println(this + " updating local string to " + localReferencePhrase);
				// Update counters
				changesRecognized++;
				totCompares += compareCounter;
				compareCounter = 0;
			}
		}
	}

	public int getChangesRecognized() {
		return changesRecognized;
	}

	public int getTotCompares() {
		return totCompares;
	}

	@Override
	public String toString() {
		final String simpleName = getClass().getSimpleName();
		if (id < 10)
			return simpleName + "0" + id;
		return simpleName + id;
	}
}

public class S7Esercizio2 {
	private final static ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final static Lock writeLock = rwLock.writeLock();
	final static Lock readLock = rwLock.readLock();
	final static String[] nouns = { "cat", "dog", "pig", "horse", "bird", "lion" };
	final static Random random = new Random();

	private static String getWord() {
		return nouns[random.nextInt(nouns.length)];
	}

	// Used to know when all adders have finished
	static volatile boolean isRunning = true;

	static List<String> sharedPhrase;

	public static void main(final String[] args) {
		final S7Es2Timer timer = new S7Es2Timer();

		// Initialize phrase with words
		final List<String> list = new ArrayList<>();
		for (int i = 0; i < 100; i++)
			list.add(getWord());

		// Share list
		S7Esercizio2.sharedPhrase = list;

		// Create WordAdder and Reader threads
		final List<ReadWorker> allWorkers = new ArrayList<>();
		final List<Thread> allThreads = new ArrayList<>();
		for (int i = 0; i < 15; i++) {
			final ReadWorker worker = new ReadWorker(i);
			allWorkers.add(worker);
			allThreads.add(new Thread(worker));
		}

		System.out.println("Simulation started");
		System.out.println("--------------------------------------------");
		timer.start();
		for (final Thread t : allThreads)
			t.start();

		for (int i = 0; i < 10; i++) {
			writeLock.lock();
			try {
				S7Esercizio2.sharedPhrase.add(getWord());
			} finally {
				writeLock.unlock();
			}
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		isRunning = false;

		for (final Thread t : allThreads) {
			try {
				t.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		timer.stop();
		System.out.println("--------------------------------------------");
		System.out.println("Simulation finished");

		for (final ReadWorker worker : allWorkers) {
			final int changesRecognized = worker.getChangesRecognized();
			final int totCompares = worker.getTotCompares();
			final float comparesPerChange = (float) totCompares / (float) changesRecognized;
			System.out.println(worker + " recognizedChanges:\t" + changesRecognized + "\ttotCompares:\t" + totCompares
					+ "\tcompares. Avg compares per change\t" + comparesPerChange);
		}
		System.out.println("Simulation took:\t" + timer.getElapsedTime() + "\tms");
	}
}
