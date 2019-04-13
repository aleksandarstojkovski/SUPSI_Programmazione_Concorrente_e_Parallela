package serie07.es3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

class Amico implements Runnable{

    private final String[] messgaes = {"Ciao amico", "come stai ?", "chi sei ?", "quanti anni hai?", "18", "bene e tu ?", "bene"};
    private final List<String> inMailbox;
    private final List<String> outMailbox;
    private final int id;
    private int sentEmails=0;
    private int readEmails=0;
    private final Random random = new Random();

    public Amico(int id, List in, List out){
        this.id=id;
        this.inMailbox=in;
        this.outMailbox=out;
    }

    @Override
    public void run() {
        // send email
        for (int i=0;i<random.nextInt(6)+2;i++) {
            String randomMessage = messgaes[random.nextInt(7)];
            synchronized (outMailbox) {
                outMailbox.add(randomMessage);
            }
            sentEmails++;
        }
        // simulate read email
        synchronized (inMailbox) {
            Iterator<String> it = inMailbox.iterator();
            while (it.hasNext()) {
                try {
                    Thread.sleep(random.nextInt(50) + 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                readEmails++;
            }
        }
    }

    public int getSentEmails() {
        return sentEmails;
    }

    public int getReadEmails() {
        return readEmails;
    }

    public int getId() {
        return id;
    }
}


public class Main {

    static volatile List<String> incomingMailbox = new ArrayList<>();
    static volatile List<String> outgoingMailbox = new ArrayList<>();

    public static void main(String[] args) {

        List<Thread> threads = new ArrayList<>();

        Amico amico1 = new Amico(1,incomingMailbox,outgoingMailbox);
        Thread t1 = new Thread(amico1);
        threads.add(t1);

        Amico amico2 = new Amico(2,outgoingMailbox,incomingMailbox);
        Thread t2 = new Thread(amico2);
        threads.add(t2);

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("---------------------------------------");
        System.out.println("Amico-"+amico1.getId()+" sent " +amico1.getSentEmails() + " messages to Amico-" +amico2.getId());
        System.out.println("Amico-"+amico1.getId()+" read " +amico1.getReadEmails() + " messages from Amico-" +amico2.getId());
        System.out.println("Amico-"+amico2.getId()+" sent " +amico2.getSentEmails() + " messages to Amico-" +amico1.getId());
        System.out.println("Amico-"+amico2.getId()+" read " +amico2.getReadEmails() + " messages from Amico-" +amico1.getId());
    }
}
