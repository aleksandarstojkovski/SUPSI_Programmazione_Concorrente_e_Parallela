package serie11.es2;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Coordinate {
	private final double lat;
	private final double lon;

	public Coordinate(final double lat, final double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	/**
	 * Returns the distance (expressed in km) between two coordinates
	 *
	 * @param from
	 * @return Returns the distance expressed in km
	 */
	public double distance(final Coordinate from) {
		final double earthRadius = 6371.000; // km
		final double dLat = Math.toRadians(from.lat - this.lat);
		final double dLng = Math.toRadians(from.lon - this.lon);
		final double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(from.lat))
				* Math.cos(Math.toRadians(this.lat)) * Math.sin(dLng / 2.0) * Math.sin(dLng / 2.0);
		final double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return (earthRadius * c);
	}

	@Override
	public String toString() {
		return String.format("[%.5f, %.5f]", lat, lon);
	}
}

class Earthquake {
	private final static String CSV_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

	private final Date time;
	private final Coordinate position;
	private final double depth;
	private final double magnitude;
	private final String place;

	public Earthquake(final Date time, final Coordinate pos, final double depth, final double mag, final String place) {
		this.time = time;
		this.position = pos;
		this.depth = depth;
		this.magnitude = mag;
		this.place = place;
	}

	public Coordinate getPosition() {
		return position;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public double getDepth() {
		return depth;
	}

	public static Earthquake parse(final String csvLine) {
		final String[] splits = csvLine.split(CSV_REGEX);
		if (splits.length != 15) {
			System.out.println("Failed to parse: " + csvLine);
			return null;
		}

		final Date time;

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			time = sdf.parse(splits[0]);
		} catch (final ParseException e) {
			return null;
		}

		final double lat = tryParseDouble(splits[1]);
		final double lon = tryParseDouble(splits[2]);
		final double depth = tryParseDouble(splits[3]);
		final double mag = tryParseDouble(splits[4]);
		final String place = splits[13];

		return new Earthquake(time, new Coordinate(lat, lon), depth, mag, place);
	}

	private static Double tryParseDouble(final String str) {
		try {
			return Double.parseDouble(str);
		} catch (final NumberFormatException e) {
			return new Double(0);
		}
	}

	@Override
	public String toString() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(time) + " mag: " + magnitude
				+ " depth: " + depth + "km @ " + position + " " + place;
	}
}

public class S11Esercizio1 {

	static void findNearest(Coordinate supsi, Stream<Earthquake> quakeStream) {
		final long computeTime = System.currentTimeMillis();
		System.out.println("Searching for nearest earthquake ...");

		Earthquake curNearestQuake = quakeStream
				.min(Comparator.comparingDouble(q -> q.getPosition().distance(supsi))).get();

		double curNearestDistance = curNearestQuake.getPosition().distance(supsi);
		//Nearest  : Optional[28.11.15 21:29 mag: 3.0 depth: 5.0km @ [45.79000, 9.79000] "2km N of Albino, Italy"] distance: 72.34566016141176

		final long endTime = System.currentTimeMillis();
		System.out.println("Nearest  : " + curNearestQuake + " distance: " + curNearestDistance + " (computation time=" + (endTime - computeTime) + " ms)");
	}

	static void findFarest(Coordinate supsi, Stream<Earthquake> quakeStream) {
		final long computeTime = System.currentTimeMillis();
		System.out.println("Searching for farest earthquake ...");

		Earthquake curNearestQuake = quakeStream
				.max(Comparator.comparingDouble(q -> q.getPosition().distance(supsi))).get();

		double curNearestDistance = curNearestQuake.getPosition().distance(supsi);
		final long endTime = System.currentTimeMillis();
		System.out.println("Farest  : " + curNearestQuake + " distance: " + curNearestDistance + " (computation time=" + (endTime - computeTime) + " ms)");
	}

	static void findStrongest(Stream<Earthquake> quakeStream) {
		final long computeTime = System.currentTimeMillis();
		System.out.println("Searching for strongest earthquake ...");

		Earthquake result = quakeStream
				.max(Comparator.comparingDouble(Earthquake::getMagnitude)).get();

		final long endTime = System.currentTimeMillis();
		System.out.println("Strongest  : " + result + " (computation time=" + (endTime - computeTime) + " ms)");
	}

	static void findTopTenFromSupsi(Coordinate supsi, Stream<Earthquake> quakeStream) {
		final long computeTime = System.currentTimeMillis();
		System.out.println("Searching top ten...");

		List<Earthquake> topTen = quakeStream
				.filter(q -> q.getPosition().distance(supsi) > 2000)
				.filter(q -> q.getMagnitude() >= 4 && q.getMagnitude() <=6)
				.sorted(Comparator.comparingDouble(q -> q.getPosition().distance(supsi)))
				.limit(10)
				.collect(Collectors.toList());

		final long endTime = System.currentTimeMillis();
		for (int i = 0; i < topTen.size(); i++) {
			System.out.println(i + ") " + topTen.get(i) + " distance: " + topTen.get(i).getPosition().distance(supsi));
		}

		System.out.println(" (computation time=" + (endTime - computeTime) + " ms)");
	}

	static void findLatitude46(Stream<Earthquake> quakeStream) {
		final long computeTime = System.currentTimeMillis();
		System.out.println("Searching for latitude 6 ...");

		long result = quakeStream
				.filter(q -> q.getPosition().getLat() < 47.0 && q.getPosition().getLat() >= 46.0)
				.count();

		final long endTime = System.currentTimeMillis();
		System.out.println("Number of earthquake  with latitude 46: " + result + " (computation time=" + (endTime - computeTime) + " ms)");
	}

	static void findLongitude8(Stream<Earthquake> quakeStream) {
		final long computeTime = System.currentTimeMillis();
		System.out.println("Searching for longitude 8...");

		long result = quakeStream
				.filter(q -> q.getPosition().getLon() < 9.0 && q.getPosition().getLon() >= 8.0)
				.count();

		final long endTime = System.currentTimeMillis();
		System.out.println("Number of earthquake  with longitude 8: " + result + " (computation time=" + (endTime - computeTime) + " ms)");
	}

	static void groupByDepth(Stream<Earthquake> quakeStream) {
		final long computeTime = System.currentTimeMillis();
		System.out.println("grouping by depth...");

		Map<Integer, List<Earthquake>> result = quakeStream
				.collect(Collectors.groupingBy(q -> (int)(q.getDepth() / 100)));

		final long endTime = System.currentTimeMillis();

		for (Integer range : result.keySet()) {
			System.out.println("[" + (range*100) + ", " +  (range*100+100) + ") -> " + result.get(range).size());
		}

		System.out.println(" (computation time=" + (endTime - computeTime) + " ms)");
	}

	static void groupByMagnitude(Stream<Earthquake> quakeStream) {
		final long computeTime = System.currentTimeMillis();
		System.out.println("grouping by magnitude...");

		Map<Integer, Long> result = quakeStream
				.collect(Collectors.groupingBy(q -> (int)q.getMagnitude(), Collectors.counting()));

		final long endTime = System.currentTimeMillis();

		for (Integer range : result.keySet()) {
			System.out.println("[" + (range) + ", " +  (range+1) + ") -> " + result.get(range));
		}

		System.out.println(" (computation time=" + (endTime - computeTime) + " ms)");
	}

	private static List<Earthquake> loadEarthquakeDB(final String address, final boolean isLocalFile) {
		final List<Earthquake> quakes = new ArrayList<Earthquake>();

		final Reader reader;
		if (isLocalFile) {
			try {
				final File file = new File(address);
				reader = new FileReader(file);
			} catch (final FileNotFoundException e2) {
				System.out.println("Failed to open file: " + address);
				return Collections.emptyList();
			}
		} else {
			final URL url;
			try {
				url = new URL(address);
			} catch (final MalformedURLException e) {
				System.out.println("Failed to create URL for address: " + address);
				return Collections.emptyList();
			}
			final InputStream is;
			try {
				is = url.openStream();
			} catch (final IOException e) {
				System.out.println("Failed to open stream for: " + address);
				return Collections.emptyList();
			}
			reader = new InputStreamReader(is);
		}

		System.out.println("Requesting earthquake data from: " + address + " ...");

		String line;
		try {
			final BufferedReader br = new BufferedReader(reader);
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				final Earthquake quake = Earthquake.parse(line);
				if (quake != null)
					quakes.add(quake);
				else
					System.out.println("Failed to parse: " + line);
			}
			br.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return quakes;
	}

	public static void main(final String[] args) throws ExecutionException, InterruptedException {
		final String URI = Paths.get(".").toAbsolutePath().normalize().toString()+File.separator+"src"+File.separator+"serie11"+File.separator+"es1"+File.separator+"2014-2015.csv";
		final long startTime = System.currentTimeMillis();
		final Coordinate supsi = new Coordinate(46.0234, 8.9172);


		long computeTime = System.currentTimeMillis();

		System.out.println("V1");
		// v1
		{
			CompletableFuture<List<Earthquake>> loadDB = CompletableFuture.supplyAsync(() -> loadEarthquakeDB(URI, true));
			loadDB.thenAccept(quakeList -> findNearest(supsi, quakeList.stream()));
			loadDB.thenAccept(quakeList -> findFarest(supsi, quakeList.stream()));
			loadDB.thenAccept(quakeList -> findLatitude46(quakeList.stream()));
			loadDB.thenAccept(quakeList -> findStrongest(quakeList.stream()));
			loadDB.thenAccept(quakeList -> findTopTenFromSupsi(supsi, quakeList.stream()));
			// senza loadDB.get(), i thenAccept() sopra non vengono scatenati
			loadDB.get();
		}

		computeTime = System.currentTimeMillis();
		System.out.println("V2");
		//v2
		{
			ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			// funzione loadEarthquakeDB torna una List<Earthquake>
			CompletableFuture<List<Earthquake>> loadDB = CompletableFuture.supplyAsync( () -> loadEarthquakeDB(URI,true), ex);
			loadDB
					// thenApply() deve obbligatoriamente tornare qualcosa
					.thenApply(quakeList -> {findNearest(supsi,quakeList.stream());return quakeList;})
					.thenApply(quakeList -> {findFarest(supsi,quakeList.stream());return quakeList;})
					.thenApply(quakeList -> {findLatitude46(quakeList.stream());return quakeList;})
					.thenApply(quakeList -> {findStrongest(quakeList.stream());return quakeList;})
					// thenAccept() non ritorna niente
					.thenAccept(quakeList -> findTopTenFromSupsi(supsi,quakeList.stream()))
					// thenRun() invece non deve avere nessun parametro "()"
					.thenRun(()-> System.out.println("Finish of the chain"));
			ex.shutdown();
		}


	}
}
