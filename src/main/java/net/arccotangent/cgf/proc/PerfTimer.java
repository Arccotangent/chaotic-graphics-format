package net.arccotangent.cgf.proc;

import net.arccotangent.cgf.log.Logger;

public class PerfTimer extends Thread {

	private boolean stopping;
	private final BitmapGenerator generator;
	private final Logger log;
	private long startTime;
	private long totalIterations;

	PerfTimer(long startTime, BitmapGenerator generator) {
		this.generator = generator;
		this.startTime = startTime;
		this.totalIterations = generator.getTotalIterations();
		this.log = new Logger("PerformanceTimer/" + startTime);
	}

	@Override
	public void run() {
		long prevIterationCount = 0;

		try {
			while (true) {
				Thread.sleep(1000);
				long currentIterationCount = generator.getDoneIterations();

				long iterationsThisPeriod = currentIterationCount - prevIterationCount;
				log.i("Completed " + currentIterationCount + " of " + totalIterations + " in this period. Running iteration rate is " + iterationsThisPeriod + " iterations/sec.");

				prevIterationCount = currentIterationCount;
			}
		} catch (Exception e) {
			if (!stopping) {
				log.w("Error during timer operation! CGF will continue generating your bitmap, but the timer won't work anymore.");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void interrupt() {
		stopping = true;
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		double avgRate = (double) totalIterations / (double) totalTime;
		log.i("CGF has taken " + totalTime + "ms to generate your bitmap. The average iteration rate was " + avgRate + " iterations/sec.");
		super.interrupt();
	}
}
