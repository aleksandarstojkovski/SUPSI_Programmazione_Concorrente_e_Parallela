package serie09.es4;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ClientSupplyer implements Runnable {

    private static int N_CLIENTS=100;
    private SalaAspetto salaAspetto;
    private int clientiCheSeNeSonoAndati=0;

    ClientSupplyer(SalaAspetto salaAspetto){
        this.salaAspetto=salaAspetto;
    }

    @Override
    public void run() {
        Main.lock.lock();
        try{
            while (!Main.open){
                try {
                    Main.shopIsOpen.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            Main.lock.unlock();
        }
        for (int i=0;i<N_CLIENTS;i++){
            try {
                // tempo che simula un nuovo cliente che entra nel negozio
                Thread.sleep(ThreadLocalRandom.current().nextInt(450,700));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (salaAspetto){
                Cliente cliente = new Cliente();
                if (salaAspetto.cePosto()){
                    salaAspetto.inserisciCliente(cliente);
                    synchronized (salaAspetto) {
                        salaAspetto.notifyAll();
                    }
                } else {
                    System.out.println("Cliente"+cliente.getId() + " se ne va perchè non c'è posto!");
                    clientiCheSeNeSonoAndati++;
                }
            }
        }
        Main.open=false;
        Main.lock.lock();
        try{
            Main.shopIsOpen.signalAll();
        }finally {
            Main.lock.unlock();
        }
    }

    public int getClientiCheSeNeSonoAndati() {
        return clientiCheSeNeSonoAndati;
    }

    public static int getnClients() {
        return N_CLIENTS;
    }
}

class SalaAspetto{
    private static int N_POSTI=5;
    private LinkedList<Cliente> salaAspetto = new LinkedList<>();

    public boolean cePosto(){
        if (salaAspetto.size()<N_POSTI)
            return true;
        return false;
    }

    public boolean inserisciCliente(Cliente cliente){
        if (cePosto()) {
            // simula il tempo che impiega il cliente per andare nella sala d'attesa
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(80,160));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            salaAspetto.push(cliente);
            return true;
        }
        return false;
    }

    public Cliente processa(){
        if (! salaAspetto.isEmpty()) {
            return salaAspetto.poll();
        }
        return null;
    }

}
class Cliente{

    private static int NUMBER_OF_CLIENTS=0;
    private int id;

    Cliente(){
        this.id=NUMBER_OF_CLIENTS;
        NUMBER_OF_CLIENTS++;
    }

    public int getId() {
        return id;
    }

}

class Barbiere implements Runnable{

    SalaAspetto salaAspetto;

    Barbiere (SalaAspetto salaAspetto){
        this.salaAspetto=salaAspetto;
    }

    @Override
    public void run() {
        Main.lock.lock();
        try{
            while (!Main.open){
                try {
                    Main.shopIsOpen.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            Main.lock.unlock();
        }
        while (true) {
            //tempo che simula il barbiere che verficia se ci sono persone in sala d'aspetto
            print("controllo sala d'attesa");
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Cliente cliente;
            synchronized (salaAspetto) {
                cliente = salaAspetto.processa();
            }
            if (cliente == null) {
                print("vado a dormire perchè non ci sono clienti");
                dormi();
                if (Main.open)
                    continue;
                else
                    break;
            } else {
                print("taglio i capelli a Cliente" + cliente.getId());
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1010));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void dormi(){
        if (Main.open) {
            synchronized (salaAspetto) {
                try {
                    salaAspetto.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void print(String s){
        System.out.println("Barbiere: "+s);
    }

}

public class Main {

    public static Lock lock = new ReentrantLock();
    public static Condition shopIsOpen = lock.newCondition();
    public static volatile boolean open=false;

    public static void main(String[] args) {
        SalaAspetto salaAspetto = new SalaAspetto();
        Barbiere barbiere = new Barbiere(salaAspetto);
        ClientSupplyer supplyer = new ClientSupplyer(salaAspetto);

        Thread t1 = new Thread(barbiere);
        Thread t2 = new Thread(supplyer);
        t1.start();
        t2.start();

        // simula attesa che lo shop apra
        for (int i=0;i<100000;i++){}

        System.out.println("SHOP APRE TRA 3...2...1...");

        open=true;
        lock.lock();
        try {
            shopIsOpen.signalAll();
        } finally {
            lock.unlock();
        }

        // wait for barber to finish
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("---------- STATISTICHE ----------");
        System.out.println("Clienti totali: " + ClientSupplyer.getnClients());
        System.out.println("Clienti andati via: " + supplyer.getClientiCheSeNeSonoAndati());

    }
}
