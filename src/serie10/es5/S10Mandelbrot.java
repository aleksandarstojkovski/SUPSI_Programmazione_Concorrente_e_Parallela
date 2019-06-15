package serie10.es5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This JPanel holds the image
 */
class ImagePanel extends JPanel {
	private static final long serialVersionUID = -765326845521113343L;

	// contains the image that is computed by this program
	final BufferedImage image;
	private final JPanel imagePanel;

	public ImagePanel(final int w, final int h) {
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		paintGray();

		// imagePanel is a JPanel that draws the image data
		imagePanel = new JPanel() {
			private static final long serialVersionUID = 4002004872041961024L;

			@Override
			protected void paintComponent(final Graphics g) {
				if (image == null)
					// fill with background color, gray
					super.paintComponent(g);
				else {
					// Multiple access to update image!
					synchronized (image) {
						g.drawImage(image, 0, 0, null);
					}
				}
			}
		};
		imagePanel.setPreferredSize(new Dimension(w, h));
		setLayout(new BorderLayout());
		add(imagePanel, BorderLayout.CENTER);
	}

	/**
	 * Adds the give rowData to the image and updates the image
	 *
	 * @param rowData
	 * @param row
	 */
	public void setRowAndUpdate(final int[] rowData, final int row) {
		final int width = getWidth();

		// Image is a shared resource!
		synchronized (image) {
			image.setRGB(0, row, width, 1, rowData, 0, width);
		}
		// Repaint just the newly computed row.
		imagePanel.repaint(0, row, width, 1);
	}

	private void paintGray() {
		final Graphics g = image.getGraphics();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.dispose();
	}

	public void resetImage() {
		paintGray();
		imagePanel.repaint();
	}

	@Override
	public int getWidth() {
		return image.getWidth();
	}

	@Override
	public int getHeight() {
		return image.getHeight();
	}
}

/**
 * Mandelbrot generator class
 */
class Mandelbrot {
	private final int[] palette = new int[256];
	private final double xmin = -1.6744096740931858;
	private final double xmax = -1.6744096740934730;
	private final double ymin = 4.716540768697223E-5;
	private final double ymax = 4.716540790246652E-5;
	private final int maxIterations = 10000;

	private final int width;
	private final int height;
	private final double dx;
	private final double dy;

	public Mandelbrot(final int width, final int heigth) {
		this.width = width;
		this.height = heigth;
		dx = (xmax - xmin) / (width - 1);
		dy = (ymax - ymin) / (height - 1);

		for (int i = 0; i < 256; i++)
			palette[i] = Color.getHSBColor(i / 255F, 1, 1).getRGB();
	}

	/**
	 * Returns the imageData for given row
	 *
	 * @return
	 */
	public int[] computeRow(final int row) {
		final int[] rgbRow = new int[width];
		final double y = ymax - dy * row;
		for (int col = 0; col < width; col++) {
			final double x = xmin + dx * col;
			final int count = computePoint(x, y);
			if (count == maxIterations)
				rgbRow[col] = 0;
			else
				rgbRow[col] = palette[count % palette.length];
		}
		return rgbRow;
	}

	private int computePoint(final double x, final double y) {
		int count = 0;
		double xx = x;
		double yy = y;
		while (count < maxIterations && (xx * xx + yy * yy) < 4) {
			count++;
			final double newxx = xx * xx - yy * yy + x;
			yy = 2 * xx * yy + y;
			xx = newxx;
		}
		return count;
	}
}

public class S10Mandelbrot extends JPanel {
	private static final long serialVersionUID = -765326845524613343L;

	// the threads that compute the image
	private Thread[] workers;

	// used to signal the thread to abort
	volatile boolean running;

	// how many threads have finished running?
	private volatile int threadsCompleted;

	// button the user can click to start or abort the thread
	private final JButton startButton;

	// for specifying the number of threads to be used
	private final JComboBox<Integer> threadCountSelect;

	final ImagePanel imagePanel;

	final Mandelbrot fractal;

	/**
	 */
	public S10Mandelbrot() {

		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		setLayout(new BorderLayout());

		// Top - Label, ComboBox and Button
		final JPanel topPanel = new JPanel();
		startButton = new JButton("Start");
		topPanel.add(startButton);
		threadCountSelect = new JComboBox<Integer>();
		for (int i = 1; i <= 32; i++)
			threadCountSelect.addItem(new Integer(i));

		topPanel.add(new JLabel("Number of threads to use: "));
		topPanel.add(threadCountSelect);

		topPanel.setBackground(Color.LIGHT_GRAY);
		add(topPanel, BorderLayout.NORTH);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (running)
					stop();
				else
					start();
			}
		});

		// Main - Image Panel
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final int width = screenSize.width - 200;
		final int height = screenSize.height - 200;
		imagePanel = new ImagePanel(width, height);
		add(imagePanel, BorderLayout.CENTER);
		fractal = new Mandelbrot(width, height);
	}

	/**
	 * This method is called when the user clicks the Start button, while no
	 * computation is in progress. It starts as many new threads as the user has
	 * specified, and assigns a different part of the image to each thread. The
	 * threads are run at lower priority than the event-handling thread, in order to
	 * keep the GUI responsive.
	 */
	void start() {
		System.out.println("NON SONO FREEZZATO, STO GENRANDO I TIMINGS");
		System.out.println("\nThreads;Time(ms)");
		for (int abc=1;abc<=32;abc++) {
			// change name while computation is in progress
			startButton.setText("Abort");
			imagePanel.resetImage();

			// will be re-enabled when all threads finish
			threadCountSelect.setEnabled(false);

			final int threadCount = abc;
			ExecutorService es = Executors.newFixedThreadPool(threadCount);
			workers = new Thread[threadCount];

			// How many rows of pixels should each thread compute?
			int rowsPerThread;

			final int height = imagePanel.getHeight();

			rowsPerThread = height / threadCount;

			// Set the signal before starting the threads!
			running = true;
			// Records how many of the threads have terminated.
			threadsCompleted = 0;
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < threadCount; i++) {
				final int startRow; // first row computed by thread number i
				final int endRow; // last row computed by thread number i
				// Create and start a thread to compute the rows of the image from
				// startRow to endRow. Note that we have to make sure that
				// the endRow for the last thread is the bottom row of the image.
				startRow = rowsPerThread * i;
				if (i == threadCount - 1)
					endRow = height - 1;
				else
					endRow = rowsPerThread * (i + 1) - 1;
				final String threadName = "WorkerThread " + (i + 1) + "/" + threadCount;
				es.submit(() -> {
					try {
						// Compute one row of pixels.
						for (int row = startRow; row <= endRow; row++) {
							final int[] rgbRow = fractal.computeRow(row);
							// Check for the signal to abort the computation.
							if (!running)
								return;
							imagePanel.setRowAndUpdate(rgbRow, row);
						}
					} finally {
						// make sure this is called when the thread finishes for
						// any reason.
						threadFinished();
					}
				});
			}
			es.shutdown();
			try {
				es.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println(threadCount + ";" + estimatedTime);
		}
	}

	/**
	 * Called when the user clicks the button while a thread is running. A signal is
	 * sent to the thread to terminate, by setting the value of the signaling
	 * variable, running, to false.
	 */
	void stop() {
		// will be re-enabled when all threads finish
		startButton.setEnabled(false);
		running = false;
	}

	/**
	 * Called by each thread upon completing it's work
	 */
	synchronized void threadFinished() {
		threadsCompleted++;
		if (threadsCompleted == workers.length) {
			// all threads have finished
			startButton.setText("Start");
			startButton.setEnabled(true);
			// Make sure running is false after the thread ends.
			running = false;

			workers = null;
			threadCountSelect.setEnabled(true); // re-enable pop-up menu
			imagePanel.repaint();
		}
	}

	/**
	 * Program starting point
	 */
	public static void main(final String[] args) {
		final JFrame window = new JFrame("Multiprocessing Demo 1");
		final S10Mandelbrot content = new S10Mandelbrot();
		window.setContentPane(content);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setResizable(false);
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation((screenSize.width - window.getWidth()) / 2, (screenSize.height - window.getHeight()) / 2);
		window.setVisible(true);
	}
}
