package serie08.es1.wait_notify;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class Calculator implements Runnable{

	int id;

	Calculator(int id){
		this.id=id;
	}

	@Override
	public void run() {
		int sum=0;
		for (int i=0; i<S8Esercizio1.matrix[id].length;i++){
			sum += S8Esercizio1.matrix[id][i];
		}

		synchronized (S8Esercizio1.rowSum){
			S8Esercizio1.rowSum.add(sum);
		}

		S8Esercizio1.finishedThreads.incrementAndGet();

		synchronized (S8Esercizio1.matrix) {
			S8Esercizio1.matrix.notifyAll();
		}

		try {
			synchronized (S8Esercizio1.matrix) {
				while (S8Esercizio1.finishedThreads.get()<10) {
					S8Esercizio1.matrix.wait();
				}
			}
		} catch (InterruptedException e) {}

		sum=0;
		for (int i=0; i<S8Esercizio1.matrix.length;i++){
			sum += S8Esercizio1.matrix[i][id];
		}
		synchronized (S8Esercizio1.colSum){
			S8Esercizio1.colSum.add(sum);
		}

		S8Esercizio1.finishedThreads.incrementAndGet();

		synchronized (S8Esercizio1.matrix) {
			S8Esercizio1.matrix.notifyAll();
		}

	}

}
public class S8Esercizio1 {

	volatile static int[][] matrix = new int[10][10];
	final static ArrayList<Integer> rowSum = new ArrayList<>();
	final static ArrayList<Integer> colSum = new ArrayList<>();
	static AtomicInteger finishedThreads = new AtomicInteger(0);

	public static void main(String[] args) {
		System.out.println(matrix.length);
		List<Thread> threads = new ArrayList<>();
		// Inizializza matrice con valori random
		initMatrix();

		// Stampa matrice
		System.out.println("Matrice:");
		printMatrix();

		// Calcola somma delle righe
		for (int row = 0; row < matrix.length; row++){
			Calculator c = new Calculator(row);
			Thread t = new Thread(c);
			threads.add(t);
			t.start();
		}

		synchronized (matrix) {
			while (finishedThreads.get()<10) {
				try {
					matrix.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// Stampa somma delle righe
		System.out.println("Somme delle righe:");
		synchronized (rowSum) {
			for (Integer i : rowSum) {
				System.out.print(i + " ");
			}
		}

		synchronized (matrix) {
			while (finishedThreads.get() < 10*2) {
				try {
					matrix.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// Stampa somma delle colonne
		System.out.println("\nSomme delle colonne:");
		synchronized (colSum) {
			for (Integer i : colSum) {
				System.out.print(i + " ");
			}
		}

	}

	private static void initMatrix() {
		Random r = new Random();
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				matrix[row][col] = 1 + r.nextInt(100);
			}
		}
	}

	private static void printMatrix() {
		for (int i = 0; i < matrix.length; i++)
			printArray(matrix[i]);
	}

	private static void printArray(final int[] array) {
		for (int i = 0; i < array.length; i++)
			System.out.print(array[i] + "\t");
		System.out.println();
	}
}
