package testing;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class RemoveWorker implements Runnable {
    private final int id;
    RemoveWorker(int i) {
        id = i;
    }

    @Override
    public void run() {
        while(!allEmpty()) {
            Random random = new Random();
            DayOfWeek dow = DayOfWeek.of(1 + random.nextInt(7));

            boolean isEmpty = false;
            int tries=1;
            do {
                String old = Main.map.get(dow);
                if(old.isEmpty()) {
                    break;
                }

                if(Main.map.replace(dow, old, old.substring(1)))
                    break;
                else
                    tries++;
            } while(true);

            if(tries > 1)
                System.out.printf("RemoveWorker%d: Updated %s after %d tries\n", id, dow, tries);
        }
    }

    private boolean allEmpty() {
        for (DayOfWeek d : DayOfWeek.values()) {
            if(!Main.map.get(d).isEmpty())
                return false;
        }

        return true;
    }
}

public class Main {
    final static ConcurrentMap<DayOfWeek, String> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        for(DayOfWeek dow : DayOfWeek.values()) {
            String str = getRandomString();
            map.put(dow, str);
        }

        printMap();

        List<Thread> threads = new ArrayList<>();
        for (int i=0; i<30; i++)
            threads.add(new Thread(new RemoveWorker(i)));

        for (final Thread t : threads)
            t.start();

        for (final Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        printMap();

        System.out.println("Simulazione finita: " + new Date().toString());
    }

    private static void printMap() {
        for(DayOfWeek dow : DayOfWeek.values()) {
            System.out.println(dow + ": " + map.get(dow));
        }
    }

    private static String getRandomString() {
        Random random = new Random();
        final StringBuilder sb = new StringBuilder();
        for(int i=0; i<10000; i++) {
            char c = (char) ('A' + random.nextInt(26));
            sb.append(c);
        }
        return sb.toString();
    }
}
