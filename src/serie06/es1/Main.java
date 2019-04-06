package serie06.es1;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class TassiDiCambio {
    private final double[][] tassi = new double[5][5];
    // CHF, EUR, USD, GBP e JPY
    public static final int CHF = 0;
    public static final int EUR = 1;
    public static final int USD = 2;
    public static final int GBP = 3;
    public static final int JPY = 4;
    public TassiDiCambio() {
        final Random random = new Random();
        final double[] tmp = new double[5];
        tmp[CHF] = random.nextDouble() * .5 + 1.;
        tmp[EUR] = random.nextDouble() * .5 + 1.;
        tmp[USD] = random.nextDouble() * .5 + 1.;
        tmp[GBP] = random.nextDouble() * .5 + 1.;
        tmp[JPY] = random.nextDouble() * .5 + 1.;
        for (int from = 0; from < 5; from++)
            for (int to = 0; to < 5; to++)
                tassi[from][to] = tmp[from] / tmp[to];
    }
    final public double getExchangeRate(final int from, final int to) {
        // REMARK: immutable object -> cannot return reference!
        try {
            return tassi[from][to];
        } catch (IndexOutOfBoundsException e) {
            return Double.NaN;
        }
    }

    final static String getCurrencyLabel(final int code) {
        switch (code) {
            case CHF:
                return "chf";
            case EUR:
                return "eur";
            case USD:
                return "usd";
            case GBP:
                return "gbp";
            case JPY:
                return "jpy";
        }
        return "";
    }
}
class Sportello implements Runnable {
    private final int id;
    public Sportello(final int id) {
        this.id = id;
    }
    @Override
    public void run() {
        final Random random = new Random();
        // Usato per formattare l'output della valuta e tasso di cambio
        final DecimalFormat format_money = new DecimalFormat("000.00");
        final DecimalFormat format_tasso = new DecimalFormat("0.00");
        while (Main.isRunning) {
            final int from = random.nextInt(5);
            int to;
            do {
                to = random.nextInt(5);
            } while (to == from);
            final TassiDiCambio nuoviTassi;
            nuoviTassi = Main.tassiAttuali.get();


            final double amount = random.nextInt(451) + 50;
            final double tasso = nuoviTassi.getExchangeRate(from,to);
            final double changed = amount * tasso;
            System.out.println("Sportello" + id + ": ho cambiato " + format_money.format(amount) + " "
                    + TassiDiCambio.getCurrencyLabel(from) + " in " + format_money.format(changed) + " "
                    + TassiDiCambio.getCurrencyLabel(to) + " tasso " + format_tasso.format(tasso));
            try {
                Thread.sleep(random.nextInt(4) + 1);
            } catch (final InterruptedException e) {
                // Ignored
            }
        }
    }
}
public class Main {
    static volatile boolean isRunning = true;
    static AtomicReference<TassiDiCambio> tassiAttuali = new AtomicReference<>(new TassiDiCambio());
    public static void main(final String[] args) {
        final List<Thread> allThread = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allThread.add(new Thread(new Sportello(i)));
        }
        for (final Thread t : allThread)
            t.start();
        for (int i = 0; i < 100; i++) {
            tassiAttuali.set(new TassiDiCambio());
            System.out.println("Nuovi tassi di cambio disponibili");
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Termina simulazione
        isRunning = false;
        for (final Thread t : allThread)
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
    }
}