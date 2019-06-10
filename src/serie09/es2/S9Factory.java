package serie09.es2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Depot {
	final private int id;
	private final List<String> elements = new ArrayList<>();

	public Depot(final int id) {
		this.id = id;
		for (int i = 0; i < 1000; i++)
			elements.add(new String("Dep#" + id + "_item#" + i));
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public int getStockSize() {
		return elements.size();
	}

	public String getElement() {
		return elements.remove(0);
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Depot" + id;
	}
}

class AssemblingWorker implements Runnable {
	private final int id;

	public AssemblingWorker(final int id) {
		this.id = id;
	}

	@Override
	public void run() {
		final Random random = new Random();
		int failureCounter = 0;
		while (true) {

			// Choose randomly 3 different suppliers
			final List<Depot> depots = new ArrayList<>();
			while (depots.size() != 3) {
				final Depot randomDepot = S9Factory.suppliers[random.nextInt(S9Factory.suppliers.length)];
				if (!depots.contains(randomDepot))
					depots.add(randomDepot);
			}

			final Depot supplier1 = depots.get(0);
			final Depot supplier2 = depots.get(1);
			final Depot supplier3 = depots.get(2);

			log("assembling from : " + supplier1 + ", " + supplier2 + ", " + supplier3);
			synchronized (supplier1) {
				if (supplier1.isEmpty()) {
					log("not all suppliers have stock available!");
					failureCounter++;
				} else {
					synchronized (supplier2) {
						if (supplier2.isEmpty()) {
							log("not all suppliers have stock available!");
							failureCounter++;
						} else {
							synchronized (supplier3) {
								if (supplier3.isEmpty()) {
									log("not all suppliers have stock available!");
									failureCounter++;
								} else {
									final String element1 = supplier1.getElement();
									final String element2 = supplier2.getElement();
									final String element3 = supplier3.getElement();
									log("assembled product from parts:  " + element1 + ", " + element2 + ", "
											+ element3);
								}
							}
						}
					}
				}
			}

			if (failureCounter > 1000) {
				log("Finishing after " + failureCounter + " failures");
				break;
			}
		}
	}

	private final void log(final String msg) {
		System.out.println("AssemblingWorker" + id + ": " + msg);
	}
}

public class S9Factory {
	final static Depot[] suppliers = new Depot[10];

	public static void main(final String[] args) {
		for (int i = 0; i < 10; i++)
			suppliers[i] = new Depot(i);

		final List<Thread> allThreads = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			allThreads.add(new Thread(new AssemblingWorker(i)));
		}

		System.out.println("Simulation started");
		for (final Thread t : allThreads) {
			t.start();
		}

		for (final Thread t : allThreads) {
			try {
				t.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Simulation finished");
	}
}
