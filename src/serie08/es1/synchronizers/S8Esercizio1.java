package serie08.es1.synchronizers;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Calculator implements Runnable{

	private int id;

	Calculator(int id){
		this.id=id;
		// per forza nel custruttore !!!!!!!
		S8Esercizio1.phaser.register();
	}

	@Override
	public void run() {

		S8Esercizio1.rowSum[id]=S8Esercizio1.sumRow(id);

		S8Esercizio1.phaser.arriveAndAwaitAdvance();

		S8Esercizio1.colSum[id]=S8Esercizio1.sumColumn(id);

		S8Esercizio1.phaser.arriveAndDeregister();
		// anche phaser.arrive() avrebbe funzionato, ma dato che non
		// usiamo piu il phaser ci deregistriamo

	}

}

public class S8Esercizio1 {

	final static int[][] matrix = new int[10][10];
	final static int[] rowSum = new int[matrix.length];
	final static int[] colSum = new int[matrix[0].length];
	// Phaser(1) è come se facessimo Phaser(0) e poi phaser.register().
	// in pratica registriamo il main
	final static Phaser phaser = new Phaser(1);

	public static void main(String[] args) {
		// Inizializza matrice con valori random
		initMatrix();

		// Stampa matrice
		System.out.println("Matrice:");
		printMatrix();

		for (int i=0;i<matrix.length;i++){
			Calculator c = new Calculator(i);
			Thread t = new Thread(c);
			t.start();
		}

		phaser.arriveAndAwaitAdvance();

		// Stampa somma delle righe
		System.out.println("Somme delle righe:");
		printArray(rowSum);

		phaser.arriveAndAwaitAdvance();

		// Stampa somma delle colonne
		System.out.println("Somme delle colonne:");
		printArray(colSum);
	}

	public static int sumRow(final int row) {
		int result = 0;
		for (int col = 0; col < matrix[row].length; col++)
			result += matrix[row][col];
		return result;
	}

	public static int sumColumn(final int row) {
		int temp = 0;
		for (int col = 0; col < matrix.length; col++)
			temp += matrix[col][row];
		return temp;
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
