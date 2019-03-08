package serie02.es3;

import java.util.concurrent.ThreadLocalRandom;

public class Utente implements Runnable {

    private int tempoDiAttesa;
    private final Conto conto;
    private int id;
    private double totalePrelievi;

    Utente(int tempoDiAttesa, Conto conto, int id){
        this.tempoDiAttesa=tempoDiAttesa;
        this.conto=conto;
        this.id=id;
    }

    public double getTotalePrelievi() {
        return totalePrelievi;
    }

    @Override
    public void run() {
        while (true){

            double quantitaDaPrelevare = (double)ThreadLocalRandom.current().nextLong(5, 50);
            double quantitaPrelevata;
            double saldoPrimaDelPrelievo;
            double saldoDopoPrelievo;


            try {
                Thread.sleep(tempoDiAttesa);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            conto.getLock().lock();
                saldoPrimaDelPrelievo = conto.getSaldo();
                quantitaPrelevata = conto.preleva(quantitaDaPrelevare);
                saldoDopoPrelievo = conto.getSaldo();
                totalePrelievi += quantitaPrelevata;
            conto.getLock().unlock();


            if (quantitaDaPrelevare == quantitaPrelevata){
                System.out.println("Utente " + id + ": prelevo " + quantitaDaPrelevare + " dal conto contenente " + saldoPrimaDelPrelievo + ". Nuovo saldo: " + saldoDopoPrelievo);
            } else {
                System.out.println("Utente " + id + ": sono riuscito a prelevare " + quantitaPrelevata + " invece di " + quantitaDaPrelevare);
                break;
            }

        }

    }

}
