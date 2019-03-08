package serie02.es2.explicitLock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class BagnoPubblico {

	ReadWriteLock lock = new ReentrantReadWriteLock();
	Lock readLock = lock.readLock();
	Lock writeLock = lock.writeLock();

	private int totUtilizzi;
	private int totOccupati;
	private final int disponibili;
	private int occupati;

	public BagnoPubblico(int numeroBagno) {
		this.disponibili = numeroBagno;
		this.occupati = 0;
		this.totUtilizzi = 0;
		this.totOccupati = 0;
	}

	public boolean occupa() {
		// Verifica disponibilita bagni liberi!
		int tmpOccupati;
		int tmpDisplonbili;
		readLock.lock();

		try {
			tmpOccupati = occupati;
			tmpDisplonbili = disponibili;
		} finally {
			readLock.unlock();
		}

		if (tmpOccupati < tmpDisplonbili) {
			// Bagno libero! Occupa
			writeLock.lock();
			try {
				occupati++;
				totUtilizzi++;
			} finally {
				writeLock.unlock();
			}
		} else {
			// Tutti i bagni sono occupati!
			writeLock.lock();
			try {
				totOccupati++;
			} finally {
				writeLock.unlock();
			}
			return false;
		}

		// Utilizza il bagno
		utilizzaBagno();

		// Libera il bagno
		writeLock.lock();
		try {
			occupati--;
		} finally {
			writeLock.unlock();
		}
		return true;
	}

	private void utilizzaBagno() {
		try {
			Thread.sleep(ThreadLocalRandom.current().nextLong(5, 15));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int getTotaleUtilizzo() {
		return totUtilizzi;
	}

	public int getTotaleOccupato() {
		return totOccupati;
	}
}

class Utente implements Runnable {
	private final int ID;
	private final BagnoPubblico bp;
	private int numUtilizzi;
	private int numOccupato;

	public Utente(BagnoPubblico bagno, int id) {
		this.bp = bagno;
		this.ID = id;
		this.numUtilizzi = 0;
		this.numOccupato = 0;
	}

	@Override
	public void run() {
		System.out.println(this + " inizio");
		for (int i = 0; i < 250; i++) {
			if (bp.occupa())
				numUtilizzi++;
			else
				numOccupato++;

			// Simula il tempo prima di dover tornare al bagno
			try {
				Thread.sleep(ThreadLocalRandom.current().nextLong(1, 5));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(this + " termino");
	}

	public int getNumUtilizzi() {
		return numUtilizzi;
	}

	public int getNumOccupato() {
		return numOccupato;
	}

	@Override
	public String toString() {
		return "Utente" + ID;
	}
}

public class S2Esercizio2 {
	public static void main(final String[] args) {
		BagnoPubblico bp = new BagnoPubblico(2);

		List<Thread> allPersons = new ArrayList<>();
		List<Utente> allUsers = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			final Utente user = new Utente(bp, i);
			allUsers.add(user);
			allPersons.add(new Thread(user));
			System.out.println("Creato utente: " + user);
		}

		allPersons.forEach(Thread::start);

		for (Thread thread : allPersons) {
			try {
				thread.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		int total = 0;
		int totalOccupato = 0;
		for (Utente utente : allUsers) {
			total += utente.getNumUtilizzi();
			totalOccupato += utente.getNumOccupato();
			System.out.println(
					utente + ": utilizzi: " + utente.getNumUtilizzi() + " occupato: " + utente.getNumOccupato());
		}

		System.out.println("Riepilogo utilizzi");
		System.out.println("Totale utenti: " + total);
		System.out.println("Totale Bagno: " + bp.getTotaleUtilizzo());

		System.out.println("Riepilogo occupazione");
		System.out.println("Totale utenti: " + totalOccupato);
		System.out.println("Totale Bagno: " + bp.getTotaleOccupato());
	}
}
