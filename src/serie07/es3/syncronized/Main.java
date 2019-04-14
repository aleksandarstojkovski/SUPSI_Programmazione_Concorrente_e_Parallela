package serie07.es3.syncronized;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Amico implements Runnable{

    static final int MAX_LETTERE = 150;
    private final String name;
    private final Random random = new Random();
    private final List<String> mailbox = new ArrayList<>();
    private Amico friend;

    public Amico(String name){
        this.name=name;
    }

    @Override
    public void run() {
        int randomNumber = ThreadLocalRandom.current().nextInt(2, 5 + 1);
        // invia tra 2 e 5 lettere all'amico
        for (int i=0;i<randomNumber;i++){
            synchronized (friend.mailbox) {
                friend.mailbox.add("Messaggio " + i + " da " + name);
            }
        }

        int lettereInviate = 0;

        while(true){
            String incomingMessage;
            do {
                if (mailbox.isEmpty()) {
                    incomingMessage = null;
                } else {
                    incomingMessage = mailbox.remove(0);
                }
            } while (incomingMessage==null);
            System.out.println(name + " - Ricevuto: " + incomingMessage);

            try {
                Thread.sleep(5 + random.nextInt(46));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (friend.mailbox) {
                friend.mailbox.add("Risposta " + lettereInviate + " da " + name);
            }
            lettereInviate++;

            if (lettereInviate==MAX_LETTERE){
                System.out.println(name + " - Ho finito le lettere");
                break;
            }
        }

    }

    public void setFriend(Amico friend) {
        this.friend = friend;
    }
}


public class Main {
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        Amico amico1 = new Amico("Ale");
        Thread t1 = new Thread(amico1);
        Amico amico2 = new Amico("Milena");
        Thread t2 = new Thread(amico2);

        amico1.setFriend(amico2);
        amico2.setFriend(amico1);

        threads.add(t1);
        threads.add(t2);

        for (Thread t : threads){
            t.start();
        }

        for (Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
