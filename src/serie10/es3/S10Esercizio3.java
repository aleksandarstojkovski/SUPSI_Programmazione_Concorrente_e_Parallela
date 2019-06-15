package serie10.es3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

class MatrixMultipier implements Callable<Integer> {

	// Crea matrici
	final int[][] m0 = new int[S10Esercizio3.MATRIX_SIZE][S10Esercizio3.MATRIX_SIZE];
	final int[][] m1 = new int[S10Esercizio3.MATRIX_SIZE][S10Esercizio3.MATRIX_SIZE];
	final int[][] m2 = new int[S10Esercizio3.MATRIX_SIZE][S10Esercizio3.MATRIX_SIZE];
	final Random rand = new Random();
	int sum=0;

	@Override
	public Integer call() {
		// Inizializza gli array con numeri random
		for (int i = 0; i < S10Esercizio3.MATRIX_SIZE; i++)
			for (int j = 0; j < S10Esercizio3.MATRIX_SIZE; j++) {
				m0[i][j] = rand.nextInt(10);
				m1[i][j] = rand.nextInt(10);
			}

		// Moltiplica matrici
		for (int i = 0; i < m0[0].length; i++)
			for (int j = 0; j < m1.length; j++)
				for (int k = 0; k < m0.length; k++) {
					m2[i][j] += m0[i][k] * m1[k][j];
					sum+=m2[i][j];
				}

		return sum;
	}
}

public class S10Esercizio3 {
	public static final int NUM_OPERATIONS = 100_000;
	public static final int MATRIX_SIZE = 64;
	static ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	static CompletionService<Integer> completionService = new ExecutorCompletionService<>(ex);

	public static void main(final String[] args) {
		System.out.println("Simulazione iniziata");
		for (int operation = 0; operation < NUM_OPERATIONS; operation++) {
			completionService.submit(new MatrixMultipier());
		}

		ex.shutdown();

		int max=0;
		for (int i=0;i<NUM_OPERATIONS;i++){
			Future<Integer> future=null;
			try {
				future = completionService.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				if (future.get()>max)
					max=future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		System.out.println("La somma più grande e' " + max);
		System.out.println("Simulazione terminata");
	}
}