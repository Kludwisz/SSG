package ssg;

public class Main {
	private static final long batchSize = 10_000_000_000L;
	
	public static void main(String [] args) throws InterruptedException {
		long start = System.nanoTime();
		FinderThread thread = new FinderThread(0, batchSize);
		thread.start();
		thread.join();
		long end = System.nanoTime();
		
		long timeMS = (end-start) / 1_000_000L;
		long seedsPerMS = batchSize / timeMS;
		long sps = seedsPerMS * 1000;
		
		long sps16 = sps * 16;
		double etaSEC = (double)(1L<<48) / (double)sps16;
		double etaH = etaSEC / 3600;
		
		System.out.println("Took " + timeMS + " ms");
		System.out.println("Speed: " + sps + " seeds / sec");
		System.out.println("Speed (16-threaded): " + sps16 + " seeds / sec");
		System.out.println("ETA (16-threaded, hours): " + etaH);
	}
}
