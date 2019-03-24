package serie04.es3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class Contact implements Runnable {
	private final int id;

	public Contact(int id) {
		this.id = id;
	}

	@Override
	public void run() {
		System.out.println("Contact" + id + ": started");
		
		int version = -1;
		while  (true) {
			// Wait for version updates
			if (version == S4Esercizio3.version)
				continue;
			// Update local version
			version = S4Esercizio3.version;
			// Used to terminate
			if (version == -1)
				break;
			
			// update local numbers
			int home = S4Esercizio3.home;
			int office = S4Esercizio3.office;
			int mobile = S4Esercizio3.mobile;
			int emergency = S4Esercizio3.emergency;
			System.out.println("Contact" + id + ": new Phonenumbers [home=" + home + ", office=" + office + ", mobile=" + mobile + ", emergency=" + emergency + "]");
		}
		System.out.println("Contact" + id + ": terminating");
	}
}

public class S4Esercizio3 {
	
	// Shared phone numbers of business man 
	public volatile static int home;
	public volatile static int office;
	public volatile static int mobile;
	public volatile static int emergency;
	public volatile static int version;
	
	private static int getNewPhoneNumber() {
		// simulate some time comsuming task to obtain a new number from operator 
		try {
			Thread.sleep(ThreadLocalRandom.current().nextInt(100, 250));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// generate new phone number
		return ThreadLocalRandom.current().nextInt(1000000, 10000000);
	}
	
	public static void main(String[] args) {
		// Create initial version of phoneNumbers
		S4Esercizio3.home = getNewPhoneNumber();
		S4Esercizio3.office = getNewPhoneNumber();
		S4Esercizio3.mobile = getNewPhoneNumber();
		S4Esercizio3.emergency = getNewPhoneNumber();
		S4Esercizio3.version = 0;

		// Create contacts, threads and start threads
		List<Thread> allThreads = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			allThreads.add(new Thread(new Contact(i)));
		allThreads.forEach(Thread::start);

		// Start simulation
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(500);
				System.out.println("I moved! Getting new phone numbers");
				S4Esercizio3.home = getNewPhoneNumber();
				S4Esercizio3.office = getNewPhoneNumber();
				S4Esercizio3.mobile = getNewPhoneNumber();
				S4Esercizio3.emergency = getNewPhoneNumber();
				
				System.out.println("new numbers are: new Phonenumbers [home=" + S4Esercizio3.home + ", office=" + S4Esercizio3.office + ", mobile=" +S4Esercizio3.mobile + ", emergency=" + S4Esercizio3.emergency + "]");
				// 
				S4Esercizio3.version++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Used to terminate 
		S4Esercizio3.version = -1;
		for (Thread t : allThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Completed");
	}
}
