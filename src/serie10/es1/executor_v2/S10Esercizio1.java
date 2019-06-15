package serie10.es1.executor_v2;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class MatrixMultipier implements Runnable{

	// Crea matrici
	final int[][] m0 = new int[S10Esercizio1.MATRIX_SIZE][S10Esercizio1.MATRIX_SIZE];
	final int[][] m1 = new int[S10Esercizio1.MATRIX_SIZE][S10Esercizio1.MATRIX_SIZE];
	final int[][] m2 = new int[S10Esercizio1.MATRIX_SIZE][S10Esercizio1.MATRIX_SIZE];
	final Random rand = new Random();

	@Override
	public void run() {
		// Inizializza gli array con numeri random
		for (int i = 0; i < S10Esercizio1.MATRIX_SIZE; i++)
			for (int j = 0; j < S10Esercizio1.MATRIX_SIZE; j++) {
				m0[i][j] = rand.nextInt(10);
				m1[i][j] = rand.nextInt(10);
			}

		// Moltiplica matrici
		for (int i = 0; i < m0[0].length; i++)
			for (int j = 0; j < m1.length; j++)
				for (int k = 0; k < m0.length; k++)
					m2[i][j] += m0[i][k] * m1[k][j];

	}
}

public class S10Esercizio1 {
	public static final int NUM_OPERATIONS = 100_000;
	public static final int MATRIX_SIZE = 64;
	static ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public static void main(final String[] args) {
		System.out.println("Simulazione iniziata");
		for (int operation = 0; operation < NUM_OPERATIONS; operation++) {
			ex.submit(new MatrixMultipier());
		}
		ex.shutdown();
		try {
			ex.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Simulazione terminata");
	}
}