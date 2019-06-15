package serie10.es1.originale;

import java.util.Random;

public class S10Esercizio1 {
	public static final int NUM_OPERATIONS = 100_000;
	public static final int MATRIX_SIZE = 64;

	public static void main(final String[] args) {
		final Random rand = new Random();
		System.out.println("Simulazione iniziata");
		for (int operation = 0; operation < NUM_OPERATIONS; operation++) {
			// Crea matrici
			final int[][] m0 = new int[MATRIX_SIZE][MATRIX_SIZE];
			final int[][] m1 = new int[MATRIX_SIZE][MATRIX_SIZE];
			final int[][] m2 = new int[MATRIX_SIZE][MATRIX_SIZE];

			// Inizializza gli array con numeri random
			for (int i = 0; i < MATRIX_SIZE; i++)
				for (int j = 0; j < MATRIX_SIZE; j++) {
					m0[i][j] = rand.nextInt(10);
					m1[i][j] = rand.nextInt(10);
				}

			// Moltiplica matrici
			for (int i = 0; i < m0[0].length; i++)
				for (int j = 0; j < m1.length; j++)
					for (int k = 0; k < m0.length; k++)
						m2[i][j] += m0[i][k] * m1[k][j];
		}
		System.out.println("Simulazione terminata");
	}
}