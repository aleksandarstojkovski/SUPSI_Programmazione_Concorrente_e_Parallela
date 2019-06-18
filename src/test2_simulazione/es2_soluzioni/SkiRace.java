package test2_simulazione.es2_soluzioni;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

class Skier implements Runnable {
    final private int id;
    public Skier(final int id) {
        this.id = id;
    }
    @Override
    public void run() {
        long raceTime = 0;
        System.out.println("Skier " + id + ": ready to start! ");
        // Attendi partenza della gara
        SkiRace.start.countDown();
        try {
            // Attendi partenza della gara
            SkiRace.start.await();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Skier " + id + ": started! ");
        for (int lap = 0; lap < SkiRace.NUM_STAGES; lap++) {
            final long lapTime = ThreadLocalRandom.current().nextLong(4, 9);
            try {
                raceTime += lapTime;
                Thread.sleep(lapTime);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            // registra tempo della tappa
            SkiRace.stageTimes[lap].set(id, lapTime);
            // decrementa conteggio di sciatori passati alla tappa corrente
            SkiRace.stageCompleted[lap].countDown();
        }
        System.out.println("Skier " + id + ": finished in " + raceTime + " ms");
    }
}
class SkierTime implements Comparable<SkierTime> {
    final private int skier;
    final private long time;
    public SkierTime(final int skier, final long time) {
        this.skier = skier;
        this.time = time;
    }
    @Override
    public int compareTo(final SkierTime o) {
        return (int) (this.time - o.time);
    }
    @Override
    public String toString() {
        return "Skier" + skier + " (" + time + " ms)";
    }
}
class SkiMarshal implements Runnable {
    final private int stage;
    public SkiMarshal(final int stage) {
        this.stage = stage;
    }
    @Override
    public void run() {
        // Resta in attesa che tutte le macchine abbiano terminato il giro 'lap'
        try {
            SkiRace.stageCompleted[stage].await();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        // Crea oggetti di supporto SkierTime per ogni tappa
        final List<SkierTime> timings = new ArrayList<SkierTime>();
        for (int i = 0; i < SkiRace.NUM_SKIERS; i++)
            timings.add(new SkierTime(i, SkiRace.stageTimes[stage].get(i)));
        // Ordina la lista di oggetti timing
        Collections.sort(timings);
        // Crea stringa classifica del giro
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < timings.size(); i++) {
            sb.append("\n");
            sb.append(1 + i);
            sb.append(". ");
            sb.append(timings.get(i));
        }
        // Stampa a schermo la classifica del giro
        System.out.println("SkiMarshal: ranking for stage " + stage + ": " + sb.toString());
    }
}
public class SkiRace {
    public static final int NUM_SKIERS = 5;
    public static final int NUM_STAGES = 6;
    static final CountDownLatch stageCompleted[] = new CountDownLatch[NUM_STAGES];
    static final AtomicLongArray stageTimes[] = new AtomicLongArray[NUM_STAGES];
    static final CountDownLatch start = new CountDownLatch(NUM_SKIERS);
    public static void main(final String[] args) {
        for (int i = 0; i < NUM_STAGES; i++) {
            stageCompleted[i] = new CountDownLatch(NUM_SKIERS);
            stageTimes[i] = new AtomicLongArray(NUM_SKIERS);
        }
        final ExecutorService executor = Executors.newFixedThreadPool(NUM_SKIERS + NUM_STAGES);
        IntStream.range(0, NUM_STAGES).forEach(i -> executor.execute(new SkiMarshal(i)));
        IntStream.range(0, NUM_SKIERS).forEach(i -> executor.execute(new Skier(i)));
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Race overview:");
        for (int skier = 0; skier < NUM_SKIERS; skier++) {
            System.out.print("Skier" + skier + ":");
            int total = 0;
            for (int stage = 0; stage < NUM_STAGES; stage++) {
                total += stageTimes[stage].get(skier);
                System.out.print("\t" + stageTimes[stage].get(skier));
            }
            System.out.println("\ttotal: " + total);
        }
    }
}