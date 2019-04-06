package serie06.es2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

class S6Es1Timer {
	private long startTime = -1;
	private long stopTime = -1;

	final public void start() {
		startTime = System.currentTimeMillis();
	}

	final public void stop() {
		stopTime = System.currentTimeMillis();
	}

	final public long getElapsedTime() {
		if (startTime < 0 || stopTime < 0)
			return 0;
		return stopTime - startTime;
	}
}

class Bagno {
	private boolean occupato = false;

	// Ritorna false se gia occupato
	public synchronized boolean provaOccupare() {
		if (occupato)
			return false;
		this.occupato = true;
		return true;
	}

	public void libera() {
		this.occupato = false;
	}
}

class S6ServiziPubblici {
	private final Bagno bagniUomini[];
	private final Bagno bagniDonne[];

	public S6ServiziPubblici(final int nrBagniUomini, final int nrBagniDonne) {
		this.bagniUomini = new Bagno[nrBagniUomini];
		for (int i = 0; i < nrBagniUomini; i++)
			this.bagniUomini[i] = new Bagno();

		this.bagniDonne = new Bagno[nrBagniDonne];
		for (int i = 0; i < nrBagniDonne; i++)
			this.bagniDonne[i] = new Bagno();
	}

	public boolean occupaBagno(final boolean uomo) {
		Bagno bagnoOccupato = null;

		if (uomo) {
			// Cerca primo bagno libero per uomini
			for (int i = 0; i < bagniUomini.length; i++) {
				final Bagno bagno = bagniUomini[i];
				if (bagno.provaOccupare()) {
					bagnoOccupato = bagno;
					break;
				}
			}
		} else {
			// Cerca primo bagno libero per donne
			for (int i = 0; i < bagniDonne.length; i++) {
				final Bagno bagno = bagniDonne[i];
				if (bagno.provaOccupare()) {
					bagnoOccupato = bagno;
					break;
				}
			}
		}

		// tutti i bagni sono occupati!
		if (bagnoOccupato == null)
			return false;

		try {
			Thread.sleep(45);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		// Libera bagno
		bagnoOccupato.libera();

		return true;
	}
}

class Utente implements Runnable {
	final boolean uomo;
	final int id;
	final S6ServiziPubblici bagno;

	public Utente(final S6ServiziPubblici bagno, final int id, final boolean isMale) {
		this.id = id;
		this.uomo = isMale;
		this.bagno = bagno;
	}

	@Override
	public void run() {
		long tempoTotale = 0;
		int bibiteBevute = 0;
		int count = 0;
		while (count < 20) {
			try {
				Thread.sleep(5);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

			if (ThreadLocalRandom.current().nextBoolean())
				bibiteBevute++;

			if (bibiteBevute > 10) {
				final S6Es1Timer timer = new S6Es1Timer();
				timer.start();
				while (true) {
					final boolean fatto = bagno.occupaBagno(uomo);
					if (fatto)
						break;
				}

				timer.stop();
				tempoTotale += timer.getElapsedTime();
				log("ho impiegato " + timer.getElapsedTime() + " ms per andare al bagno!");
				bibiteBevute = 0;
				count++;
			}
		}

		final double media = tempoTotale / count;
		log("tempo totale trascorso in bagno: " + tempoTotale + " (media " + media + ")");
	}

	private final void log(final String msg) {
		if (uomo)
			System.out.println("Mr" + id + ": " + msg);
		else
			System.out.println("Ms" + id + ": " + msg);
	}
}

public class S6Esercizio2 {
	public static void main(final String[] args) {
		long startTime = System.nanoTime();
		final S6Es1Timer mainTimer = new S6Es1Timer();
		final Collection<Thread> threads = new ArrayList<Thread>();
		final S6ServiziPubblici bagno = new S6ServiziPubblici(3, 3);

		for (int i = 0; i < 10; i++) {
			threads.add(new Thread(new Utente(bagno, i, true)));
			threads.add(new Thread(new Utente(bagno, i, false)));
		}

		System.out.println("Simulation started");
		System.out.println("------------------------------------");

		mainTimer.start();
		// Fa partire tutti i threads
		for (final Thread t : threads)
			t.start();

		// Attende che tutti i threads terminano
		for (final Thread t : threads)
			try {
				t.join();
			} catch (final InterruptedException e) {
				// Do nothing
			}
		mainTimer.stop();

		System.out.println("------------------------------------");

		System.out.println("Simulation took : " + mainTimer.getElapsedTime() + " ms");
		System.out.println("Simulation finished");
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);
	}
}