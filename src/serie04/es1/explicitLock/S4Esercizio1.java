package serie04.es1.explicitLock;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Classe usata per misurare tempo d'esecuzione
class S4Es1Timer {
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

// Classe che si occupa di effettuare scritture ad uno stato condiviso
class S4Es1Updater implements Runnable {
	final private int delay;
	final private int id;

	public S4Es1Updater(final int id, final int delay) {
		this.id = id;
		this.delay = delay;
	}

	@Override
	public void run() {
		log("started");
		while (true) {
			// Incrementa la variabile condivisa
			final long curValue = S4Esercizio1.increment();
			log("value incremented to " + curValue);

			// Termina l'esecuzione se e' stato raggiunto o superato il limite
			if (curValue >= S4Esercizio1.NUM_UPDATES)
				break;

			// Questo ritardo e' volutamente parametrizzato per permettere
			// incrementi concorrenti della variabile condivisa ad intervalli
			// diversi
			try {
				Thread.sleep(delay);
			} catch (final InterruptedException e) {
				/* do nothing */
			}
		}
		log("finished");
	}

	private final void log(final String message) {
		System.out.println(getClass().getSimpleName() + id + " :" + message);
	}
}

// Classe che si occupa di effettuare letture ad uno stato condiviso
class S4Es1Worker implements Runnable {
	final private int id;
	final private S4Es1Timer timer;
	private long localValue = 0;
	private long reads = 0;
	private long changes = 0;

	public S4Es1Worker(final int id) {
		this.id = id;
		this.timer = new S4Es1Timer();
		this.localValue = S4Esercizio1.getValue();
	}

	@Override
	public void run() {
		log("started");
		timer.start();

		while (true) {
			// Legge il valore attuale della variable condivisa
			final long curShared = S4Esercizio1.getValue();

			try {
				// Questo ritardo simula altre operazioni eseguite dal thread
				Thread.sleep(1);
			} catch (final InterruptedException e) {
				// Eccezione volutamente non gestita
			}

			// Conta il numero di letture riuscite
			reads++;
			// Conta il numero di valori nuovi letti
			if (localValue != curShared) {
				localValue = curShared;
				changes++;
			}

			// Termina l'esecuzione se e' stato raggiunto o superato il limite
			if (curShared >= S4Esercizio1.NUM_UPDATES)
				break;
		}

		timer.stop();
		log("finished");
	}

	private final void log(final String message) {
		System.out.println(getClass().getSimpleName() + id + " :" + message);
	}

	public void logResults() {
		// Stampa a schermo le proprie statistiche di esecuzione (tempo, letture
		// eseguite e numero di valori nuovi letti)
		log("time:\t" + timer.getElapsedTime() + "\tms. Reading done:\t"
				+ reads + "\t. Changes recognized:\t" + changes);
	}
}

public class S4Esercizio1 {
	// Valore condiviso incapsulato e protetto dai metodi increment / getValue
	private static long value = 0;

	private static final Lock lock = new ReentrantLock();

	public static long increment() {
	    lock.lock();
	    try {
            value++;
            try {
                // Questo sleep serve a simulare il costo di una scrittura ed e' da
                // considerarsi parte integrante dell'operazione di scrittura!
                Thread.sleep(10);
            } catch (final InterruptedException e) {
            }
            return value;
        } finally {
	        lock.unlock();
        }
	}

	public static long getValue() {
        lock.lock();
        try {
            try {
                // Questo sleep serve a simulare il costo di una lettura ed e' da
                // considerarsi parte integrante dell'operazione di lettura!
                Thread.sleep(1);
            } catch (final InterruptedException e) {
            }
            return value;
        } finally {
            lock.unlock();
        }
	}

	// Limite updates, oltre al quale i thread terminano
	final public static int NUM_UPDATES = 100;

	public static void main(final String[] args) {
		final S4Es1Timer mainTimer = new S4Es1Timer();
		final ArrayList<Thread> threads = new ArrayList<Thread>();
		final ArrayList<S4Es1Worker> workers = new ArrayList<S4Es1Worker>();

		// Crea 3 Updaters che si occupano di incrementare il contatore
		// condiviso secondo le proprie tempistiche
		threads.add(new Thread(new S4Es1Updater(0, 200)));
		threads.add(new Thread(new S4Es1Updater(1, 250)));
		threads.add(new Thread(new S4Es1Updater(2, 300)));

		// Crea 10 Workers
		for (int i = 0; i < 10; i++) {
			final S4Es1Worker worker = new S4Es1Worker(i);
			workers.add(worker);
			threads.add(new Thread(worker));
		}

		System.out.println("Simulation started");
		System.out.println("------------------------------------");

		mainTimer.start();

		// Fa partire tutti i threads
		for (final Thread t : threads)
			t.start();

		try {
			// Attende che tutti i threads terminano
			for (final Thread t : threads)
				t.join();
		} catch (final InterruptedException e) {
			/* do nothing */
		}
		mainTimer.stop();

		// Stampa tempi d'esecuzione
		for (final S4Es1Worker worker : workers)
			worker.logResults();

		System.out.println("Simulation took: " + mainTimer.getElapsedTime()
				+ " ms");
		System.out.println("------------------------------------");
		System.out.println("Simulation finished");
	}
}
