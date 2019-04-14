package serie07.es4;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class Worker implements Runnable{

    Random random = new Random();
    int tentativi;
    int id;
    Main.Giorno giornoRandom;
    List<Main.Giorno> tuttiIGiorni;

    Worker(int id){
        this.id=id;
        tuttiIGiorni = new ArrayList<>(Arrays.asList(Main.Giorno.values()));
    }

    @Override
    public void run() {
        while (true) {
            tentativi=0;
            int randomNumber = random.nextInt(tuttiIGiorni.size());
            giornoRandom = tuttiIGiorni.get(randomNumber);

            String oldValue;
            String newValue;

            do {
                oldValue = Main.mappa.get(giornoRandom);
                if (oldValue.isEmpty()) {
                    tuttiIGiorni.remove(giornoRandom);
                    if (tuttiIGiorni.isEmpty()){
                        System.out.println("RemoveWorker" +id+": All days are empty!");
                        return;
                    }
                    break;
                }
                newValue = oldValue.substring(1);
                tentativi++;
            } while (!Main.mappa.replace(giornoRandom, oldValue, newValue));
            if (tentativi>1)
                System.out.println("RemoveWorker" +id+": Updated "+giornoRandom+ " after "+tentativi + " tries");
        }
    }

}
public class Main {

    enum Giorno{
        Lunedi,
        Martedi,
        Mercoledi,
        Giovedi,
        Venerdi,
        Sabato,
        Domenica
    }

    static Map<Giorno,String> mappa = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        final String alphabet = "123xyz";
        final Random random = new Random();
        List<Thread> threads = new ArrayList<>();

        for (int i=0; i<Giorno.values().length;i++){
            StringBuilder sb = new StringBuilder();
            for (int j=0;j<10000;j++)
                sb.append(alphabet.charAt(random.nextInt(6)));
            mappa.put(Giorno.values()[i],sb.toString());
        }

        for(int i=0;i<30;i++){
            Thread t = new Thread(new Worker(i));
            threads.add(t);
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
