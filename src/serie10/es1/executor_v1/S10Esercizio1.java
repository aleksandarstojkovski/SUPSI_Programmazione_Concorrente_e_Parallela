package serie10.es1.executor_v1;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class S10Esercizio1 {
	public static final int NUM_OPERATIONS = 100_000;
	public static final int MATRIX_SIZE = 64;
	static ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public static void main(final String[] args) {
		final Random rand = new Random();
		System.out.println("Simulazione iniziata");
		for (int operation = 0; operation < NUM_OPERATIONS; operation++) {
			// Crea matrici
			final int[][] m0 = new int[MATRIX_SIZE][MATRIX_SIZE];
			final int[][] m1 = new int[MATRIX_SIZE][MATRIX_SIZE];
			final int[][] m2 = new int[MATRIX_SIZE][MATRIX_SIZE];

			// Inizializza gli array con numeri random
			for (int i = 0; i < MATRIX_SIZE; i++) {
				final int i1 = i;
				ex.submit(() -> {
					for (int j = 0; j < MATRIX_SIZE; j++) {
						final int j1 = j;
						m0[i1][j1] = rand.nextInt(10);
						m1[i1][j1] = rand.nextInt(10);
					}
				});
			}

			// Moltiplica matrici
			for (int i = 0; i < m0[0].length; i++) {
				for (int j = 0; j < m1.length; j++) {
					final int i1 = i;
					final int j1 = j;
					ex.submit(() -> {
						for (int k = 0; k < m0.length; k++) {
							final int k1 = k;
							m2[i1][j1] += m0[i1][k1] * m1[k1][j1];
						}
					});
				}
			}
			System.out.println(operation);
		}
		System.out.println("Simulazione terminata");
	}
}