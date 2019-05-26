package serie08.es2.linkedBlockingQueue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

class Amico implements Runnable{

    private int N_LETTERE=150;
    private LinkedBlockingQueue<String> inbox;
    private Amico other;
    private String name;

    Amico(LinkedBlockingQueue inbox, String name){
        this.inbox=inbox;
        this.name = name;
    }

    public void setAmico(Amico amico){
        this.other=amico;
    }

    @Override
    public void run() {

        int lettereDaInviare = ThreadLocalRandom.current().nextInt(2, 5 + 1);
        for (int i=0;i<lettereDaInviare;i++){
            String msgToSend="msg"+i;
            other.inbox.add(msgToSend);
            System.out.println("[" + name + "] sent " + msgToSend);
        }


        while (N_LETTERE > 0) {

            String receivedMsg = null;
            try {
                receivedMsg = inbox.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[" + name + "] received " + receivedMsg);

            int timeToWait = ThreadLocalRandom.current().nextInt(5, 50 + 1);
            try {
                Thread.sleep(timeToWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("[" + name + "] reply " + N_LETTERE);
            other.inbox.add("reply");

            N_LETTERE--;

        }

        System.out.println("[" + name + "] finished");

    }

}

public class Main {

    // LinkedBlockingQueue è una cosa Thread-Safe, il metodo take() è blocking, ciò vuol dire che
    // blocca il thread finche non riceve qualcosa e quindi non devo più looppare, per vedere che quello
    // che ho estratto sia diverso da null
    private static LinkedBlockingQueue<String> inbox1 = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<String> inbox2 = new LinkedBlockingQueue<>();

    public static void main(String[] args) {

        Amico amico1 = new Amico(inbox1, "Amico1");
        Amico amico2 = new Amico(inbox2, "Amico2");
        amico1.setAmico(amico2);
        amico2.setAmico(amico1);
        Thread t1 = new Thread(amico1);
        Thread t2 = new Thread(amico2);
        t1.start();
        t2.start();

    }
}
