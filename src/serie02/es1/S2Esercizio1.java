package serie02.es1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

class Autostrada {
	public int entrate = 0;
	public int uscite = 0;
	public int pedaggi = 0;
}

class Automobilista implements Runnable {
	private final int id;
	private final Autostrada autostrada;
	private final int delay;
	private int pedaggiPagati = 0;

	public Automobilista(int id, Autostrada state, int delay) {
		this.autostrada = state;
		this.delay = delay;
		this.id = id;
		this.pedaggiPagati = 0;
		System.out.println("Automobilista " + id + ": creato con " + delay
				+ " ms di percorrenza");
	}

	public int getPedaggiPagati() {
		return pedaggiPagati;
	}

	public int getID() {
		return id;
	}

	@Override
	public void run() {
		System.out.println("Automobilista " + id + ": partito");

		for (int i = 0; i < 500; i++) {
			vaiVersoAutostrada();

			autostrada.entrate++;

			int pedaggioTratta = percorriAutostrada();

			autostrada.uscite++;
			autostrada.pedaggi += pedaggioTratta;
			pedaggiPagati += pedaggioTratta;
		}
		System.out.println("Automobilista " + id + ": terminato");
	}

	private void vaiVersoAutostrada() {
		try {
			Thread.sleep(ThreadLocalRandom.current().nextLong(1, 5));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private int percorriAutostrada() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ThreadLocalRandom.current().nextInt(10, 20);
	}
}

public class S2Esercizio1 {
	public static void main(final String[] args) {
		final Collection<Automobilista> workers = new ArrayList<Automobilista>();
		final Collection<Thread> threads = new ArrayList<Thread>();
		final Random rand = new Random();

		// Crea l'oggetto condiviso
		final Autostrada autostrada = new Autostrada();

		for (int i = 0; i < 10; i++) {
			// Genera numero casuale tra 1 e 5 ms
			final int delay = 1 + rand.nextInt(5);
			// Crea nuovo automobilista con oggetto condiviso e delay
			final Automobilista a = new Automobilista(i, autostrada, delay);
			workers.add(a);
			// Aggiungi alla lista di threads un nuovo thread con il nuovo
			// worker
			threads.add(new Thread(a));
		}

		System.out.println("Simulation started");
		System.out.println("------------------------------------");

		// fa partire tutti i threads
		threads.forEach(Thread::start);
		
		try {
			// Resta in attesa che tutti i threads abbiamo terminato
			for (final Thread t : threads)
				t.join();
		} catch (final InterruptedException e) {
			return;
		}

		// Stampa i risultati
		System.out.println("------------------------------------");
		System.out.println("Simulation finished");

		int totalePedaggiUtenti = 0;
		for (final Automobilista a : workers) {
			int pedaggiPagati = a.getPedaggiPagati();
			totalePedaggiUtenti += pedaggiPagati;
			System.out.println("Automobilista " + a.getID() + " ha pagato " + pedaggiPagati);
		}
		
		System.out.println("Automobilisti totale pedaggi: " + totalePedaggiUtenti);
		System.out.println("Autostrada totale pedaggi   : " + autostrada.pedaggi);
		System.out.println("Autostrada totale entrate :" + autostrada.entrate);
		System.out.println("Autostrada totale uscite  :" + autostrada.uscite);
	}
}
