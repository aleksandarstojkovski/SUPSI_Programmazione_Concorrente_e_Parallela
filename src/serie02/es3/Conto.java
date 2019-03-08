package serie02.es3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Conto {

    private double saldo;
    Lock lock = new ReentrantLock();

    Conto(double saldo){
        this.saldo=saldo;
    }

    double preleva(double quantita){
        if (quantita<=saldo){
            saldo=saldo-quantita;
        } else {
            quantita=saldo;
            saldo=0;
        }
        return  quantita;
    }

    double getSaldo(){
        return saldo;
    }

    public Lock getLock() {
        return lock;
    }
}
