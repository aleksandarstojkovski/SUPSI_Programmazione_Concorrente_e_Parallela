package test2_simulazione.es1;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

final class Coordinate {
    private final double lat;
    private final double lon;

    Coordinate(final double lat, final double lon){
        this.lat=lat;
        this.lon=lon;
    }
//    /**
//     * Set latitude value of the Coordinate
//     */
//    public void setLat(final double lat) {
//        this.lat = lat;
//    }
//
//    /**
//     * Set longitude value of the Coordinate
//     */
//    public void setLon(final double lon) {
//        this.lon = lon;
//    }

    /**
     * Returns the distance (expressed in km) between two coordinates
     */
    public double distance(final Coordinate from) {
        final double dLat = Math.toRadians(from.lat - this.lat);
        final double dLng = Math.toRadians(from.lon - this.lon);
        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(from.lat))
                * Math.cos(Math.toRadians(this.lat)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        return (6371.000 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    @Override
    public String toString() {
        return "[" + lat + ", " + lon + "]";
    }
}

class GPS implements Runnable {

    double lat;
    double lon;
    Coordinate tmp;

    @Override
    public void run() {
        // Update curLocation with first coordinate
        lat = ThreadLocalRandom.current().nextDouble(-90.0, +90.0);
        lon = ThreadLocalRandom.current().nextDouble(-180.0, +180.0);
        tmp = new Coordinate(lat,lon);
        Main.curLocation.set(tmp);
//        Main.curLocation = new Coordinate();
//        Main.curLocation.setLat(ThreadLocalRandom.current().nextDouble(-90.0, +90.0));
//        Main.curLocation.setLon(ThreadLocalRandom.current().nextDouble(-180.0, +180.0));

        // Wait before updating position
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(10, 20));
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update curLocation with second coordinate
//        Main.curLocation = new Coordinate();
//        Main.curLocation.setLat(ThreadLocalRandom.current().nextDouble(-90.0, +90.0));
//        Main.curLocation.setLon(ThreadLocalRandom.current().nextDouble(-180.0, +180.0));
        lat = ThreadLocalRandom.current().nextDouble(-90.0, +90.0);
        lon = ThreadLocalRandom.current().nextDouble(-180.0, +180.0);
        tmp = new Coordinate(lat,lon);
        Main.curLocation.set(tmp);
    }
}

public class Main {
    static volatile Coordinate oldCurLocation = null;
    static AtomicReference<Coordinate> curLocation = new AtomicReference<>(oldCurLocation);

    public static void main(final String[] args) {
        // Create and start GPS thread
        final Thread gpsThread = new Thread(new GPS());
        gpsThread.start();

        System.out.println("Simulation started");
        while (curLocation.get() == null) {
            // Wait until location changes
        }
        final Coordinate firstLocation = curLocation.get();

        while (curLocation.get() == firstLocation) {
            // Wait until location changes
        }
        final Coordinate secondLocation = curLocation.get();

        // Write distance between firstLocation and secondLocation position
        System.out.println("Distance from " + firstLocation + " to "
                + secondLocation + " is "
                + firstLocation.distance(secondLocation));

        // Wait until GPS thread finishes
        try {
            gpsThread.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Simulation completed");
    }
}
