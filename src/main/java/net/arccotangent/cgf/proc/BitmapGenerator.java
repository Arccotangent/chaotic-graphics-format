package net.arccotangent.cgf.proc;

import net.arccotangent.cgf.data.CGFData;
import net.arccotangent.cgf.data.CGFPoint;
import net.arccotangent.cgf.data.CGFProportion;
import net.arccotangent.cgf.log.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BitmapGenerator {

	private final File targetFile;
	private final Logger log;
	private Random random;
	private long doneIterations = 0;
	private long totalIterations = 0;

	public BitmapGenerator(File targetFile) {
		this.targetFile = targetFile;
		this.log = new Logger("BitmapGenerator/" + targetFile.getName());
	}

	private void initializeRNG(long seed) {
		random = new Random();
		random.setSeed(seed);
	}

	public boolean generateBitmap(CGFData rawCgfData) throws Exception {
		if (rawCgfData == null) {
			log.e("Invalid CGF data.");
			return false;
		}

		if (targetFile.exists()) {
			log.e("Target file already exists. Aborting generation.");
			return false;
		}

		totalIterations = rawCgfData.getIterations();

		int x = rawCgfData.getX();
		int y = rawCgfData.getY();

		ArrayList<CGFPoint> points = rawCgfData.getPoints();
		ArrayList<CGFProportion> proportions = rawCgfData.getProportions();

		HashMap<String, Double> proportionMap = new HashMap<>();

		for (CGFProportion proportion : proportions) {
			proportionMap.put(proportion.getTargetPoint(), proportion.getProportion());
		}

		log.i("Generating bitmap.");
		log.i("Total iterations = " + totalIterations);
		log.i("Bitmap size = " + x + "x" + y + " pixels");
		log.i("There are " + points.size() + " points.");

		BitmapPixelMatrix matrix = new BitmapPixelMatrix(x, y);

		int shadedPoint = 0xff000000;

		log.d("Constructed empty pixel matrix. Seeding points.");

		for (CGFPoint point : points) {
			matrix.setPixel(point.getX() - 1, point.getY() - 1, shadedPoint);
		}

		long seed = System.currentTimeMillis();

		initializeRNG(seed);
		log.i("Generation seed = " + seed);

		CGFPoint seedPoint = new CGFPoint("CGFSEEDPOINT", random.nextInt(x), random.nextInt(y));

		log.d("Pixel matrix is ready. Starting timer and generating image.");

		PerfTimer timer = new PerfTimer(seed, this);
		timer.start();
		int pointCount = points.size();

		int cursorX = seedPoint.getX();
		int cursorY = seedPoint.getY();

		for (doneIterations = 0; doneIterations < totalIterations; doneIterations++) {
			int nextPointID = random.nextInt(pointCount);
			CGFPoint nextPoint = points.get(nextPointID);

			String pointName = nextPoint.getName();

			boolean xIncrease = nextPoint.getX() > cursorX;
			boolean yIncrease = nextPoint.getY() > cursorY;

			double proportion = proportionMap.get(pointName);

			int desiredX = calculateBiasedMidpoint(cursorX, nextPoint.getX(), xIncrease, proportion) - 1;
			int desiredY = calculateBiasedMidpoint(cursorY, nextPoint.getY(), yIncrease, proportion) - 1;

			matrix.setPixel(desiredX, desiredY, shadedPoint);
			cursorX = desiredX;
			cursorY = desiredY;
		}

		timer.interrupt();

		log.i("Bitmap generation complete. Writing image.");

		Bitmap bitmap = new Bitmap(targetFile, matrix);
		return bitmap.writeToTargetFile();
	}

	private int calculateBiasedMidpoint(int origPoint, int newPoint, boolean increase, double biasTowardTarget) {
		int distance = Math.abs(origPoint - newPoint);
		double biasedDistance = (double) distance * biasTowardTarget;

		if (increase) {
			return (int) Math.round((double) origPoint + biasedDistance);
		} else {
			return (int) Math.round((double) origPoint - biasedDistance);
		}
	}

	long getDoneIterations() {
		return doneIterations;
	}

	long getTotalIterations() {
		return totalIterations;
	}
}
