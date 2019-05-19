package serie08.es1.conditions;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Calculator implements Runnable{

	private int id;

	Calculator(int id){
		this.id=id;
	}

	@Override
	public void run() {
		int sum;
		sum = S8Esercizio1.sumRow(id);
		synchronized (S8Esercizio1.rowSum){
			S8Esercizio1.rowSum[id]=sum;
		}
		S8Esercizio1.finishedThreads.incrementAndGet();

		S8Esercizio1.lock.lock();
		try{
			S8Esercizio1.rowFinished.signalAll();
			while (S8Esercizio1.finishedThreads.get() < S8Esercizio1.matrix.length) {
				S8Esercizio1.rowFinished.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			S8Esercizio1.lock.unlock();
		}

		sum = S8Esercizio1.sumColumn(id);
		synchronized (S8Esercizio1.colSum){
			S8Esercizio1.colSum[id]=sum;
		}
		S8Esercizio1.finishedThreads.incrementAndGet();

		S8Esercizio1.lock.lock();
		try{
			S8Esercizio1.columnFinished.signalAll();
		} finally {
			S8Esercizio1.lock.unlock();
		}

	}

}

public class S8Esercizio1 {

	final static int[][] matrix = new int[10][10];
	final static int[] rowSum = new int[matrix.length];
	final static int[] colSum = new int[matrix[0].length];
	final static Lock lock = new ReentrantLock();
	final static Condition rowFinished = lock.newCondition();
	final static Condition columnFinished = lock.newCondition();
	final static AtomicInteger finishedThreads = new AtomicInteger(0);

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

		lock.lock();
		try{
			while (S8Esercizio1.finishedThreads.get() < S8Esercizio1.matrix.length) {
				rowFinished.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		// Stampa somma delle righe
		System.out.println("Somme delle righe:");
		printArray(rowSum);


		lock.lock();
		try{
			while (S8Esercizio1.finishedThreads.get() < S8Esercizio1.matrix.length*2) {
				S8Esercizio1.columnFinished.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			S8Esercizio1.lock.unlock();
		}

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
