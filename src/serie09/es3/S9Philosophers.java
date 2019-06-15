package serie09.es3;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Fork {
	public static final char FORK = '|';
	public static final char NO_FORK = ' ';

	private int id;
	private Lock lock = new ReentrantLock();

	public Fork(final int id) {
		this.id = id;
	}

	public boolean tryLock()  {
		return lock.tryLock();
	}

	public void lock() {
		lock.lock();
	}

	public void unlock() {
		lock.unlock();
	}
}

class Philosopher extends Thread {
	public static final char PHIL_THINKING = '-';
	public static final char PHIL_LEFT_FORK = '=';
	public static final char PHIL_EATING = 'o';

	private final int id;
	int nr_eat = 0;

	public Philosopher(final int id) {
		this.id = id;
	}

	@Override
	public void run() {
		final Random random = new Random();

		final Fork leftForkLock = S9Philosophers.forks[id];
		final Fork rightForkLock = S9Philosophers.forks[(id + 1) % S9Philosophers.NUM_PHILOSOPHERS];

		final int tableOffset = 4 * id;
		// 0, 4, 8, 12, 16
		final int table__farL = tableOffset + 0;
		// 1, 5, 9, 13, 17
		final int table__left = tableOffset + 1;
		// 2, 6, 10, 14, 18
		final int table_philo = tableOffset + 2;
		// 3, 7, 11, 15, 19
		final int table_right = tableOffset + 3;
		// 4, 8, 12, 16, 0
		final int table__farR = (tableOffset + 4) % (4 * S9Philosophers.NUM_PHILOSOPHERS);

		while (!isInterrupted()) {
			try {
				think(random.nextInt(6));

				// Try to get the fork on the left
				if(leftForkLock.tryLock()) {
					if(rightForkLock.tryLock()) {

						synchronized (S9Philosophers.class) {
							S9Philosophers.dinerTable[table__farL] = Fork.NO_FORK;
							S9Philosophers.dinerTable[table__left] = Fork.FORK;
							S9Philosophers.dinerTable[table_philo] = PHIL_LEFT_FORK;
						}

						sleep(S9Philosophers.UNIT_OF_TIME * 1);


						synchronized (S9Philosophers.class) {
							nr_eat++;
							if(nr_eat % 10 == 0)
								System.out.println("*** PHILOSOFER " + id + " has eaten " + nr_eat + " times");

							S9Philosophers.dinerTable[table_philo] = PHIL_EATING;
							S9Philosophers.dinerTable[table_right] = Fork.FORK;
							S9Philosophers.dinerTable[table__farR] = Fork.NO_FORK;
						}

						eat();

						synchronized (S9Philosophers.class) {
							S9Philosophers.dinerTable[table__farL] = Fork.FORK;
							S9Philosophers.dinerTable[table__left] = Fork.NO_FORK;
							S9Philosophers.dinerTable[table_philo] = PHIL_THINKING;
							S9Philosophers.dinerTable[table_right] = Fork.NO_FORK;
							S9Philosophers.dinerTable[table__farR] = Fork.FORK;
						}

						// Release right fork
						rightForkLock.unlock();
					}

					// Release left fork
					leftForkLock.unlock();
				}
			} catch (final InterruptedException e) {
				break;
			}
		}
	}


	private void eat() throws InterruptedException {
		sleep(S9Philosophers.UNIT_OF_TIME * 1);
	}

	private void think(int i) throws InterruptedException {
		Thread.sleep(S9Philosophers.UNIT_OF_TIME * (i));
	}
}

public class S9Philosophers {
	public static final int NUM_PHILOSOPHERS = 5;
	public static final int UNIT_OF_TIME = 50;

	// 5 objects Fork to manage lock
	public static final Fork[] forks = new Fork[NUM_PHILOSOPHERS];

	public static char[] dinerTable = null;

	static {
		for (int i = 0; i < NUM_PHILOSOPHERS; i++)
			forks[i] = new Fork(i);
	}

	public static void main(final String[] a) {


		final char[] lockedDiner = new char[4 * NUM_PHILOSOPHERS];
		for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
			lockedDiner[4 * i + 0] = Fork.NO_FORK;
			lockedDiner[4 * i + 1] = Fork.FORK;
			lockedDiner[4 * i + 2] = Philosopher.PHIL_LEFT_FORK;
			lockedDiner[4 * i + 3] = Fork.NO_FORK;
		}


		final String lockedString = new String(lockedDiner);

		// safe publication of the initial representation
		synchronized (S9Philosophers.class) {
			dinerTable = new char[4 * NUM_PHILOSOPHERS];
			for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
				dinerTable[4 * i + 0] = Fork.FORK;
				dinerTable[4 * i + 1] = Fork.NO_FORK;
				dinerTable[4 * i + 2] = Philosopher.PHIL_THINKING;
				dinerTable[4 * i + 3] = Fork.NO_FORK;
			}
		}

		for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
			final Thread t = new Philosopher(i);
			// uses this solution to allow terminating the application even if
			// there is a deadlock
			t.setDaemon(true);
			t.start();
		}

		System.out.println("The diner table:");
		long step = 0;
		while (true) {
			step++;

			String curTableString = null;
			synchronized (S9Philosophers.class) {
				curTableString = new String(dinerTable);
			}
			System.out.println(curTableString + "   " + step);

			if (lockedString.equals(curTableString))
				break;
			try {
				Thread.sleep(UNIT_OF_TIME * 10);
			} catch (final InterruptedException e) {
				System.out.println("Interrupted.");
			}
		}
		System.out.println("The diner is locked.");

		System.out.println("*******************************");

	}
}