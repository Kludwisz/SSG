package ssg;

public class Main {
	private static final long seedsTotal = (1L<<48);
	private static final long taskSize = 10_000_000_000L;
	private static final int tasksTotal = (int)(seedsTotal / taskSize);
	
	public static void main(String [] args) throws InterruptedException {
		int threadCount = 16;
		int startTask = 0;
		int endTask = tasksTotal-1;
		String outputFilename = null;
		
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-out")) {
				outputFilename = args[i+1];
			}
			if (args[i].equals("-threads")) {
				threadCount = Integer.parseInt(args[i+1]);
			}
			if (args[i].equals("-start")) {
				startTask = Integer.parseInt(args[i+1]);
			}
			if (args[i].equals("-end")) {
				endTask = Integer.parseInt(args[i+1]);
			}
		}
		
		//if (outputFilename == null) {
		//	System.out.println("ERROR: unspecified output file path, specify with -out \"(path_to_file)\"");
		//	return;
		//}
		//long tStart = System.nanoTime();
		
		final long batchSize = taskSize * threadCount;
		final long batches = (endTask - startTask + 1) / threadCount;
		
		long startOffset = startTask * taskSize;
		FinderThread[] threads = new FinderThread[threadCount];
		
		long tStart = System.nanoTime();
		
		for (int b=1; b<=batches; b++) {
			System.out.print("Batch " + b + " / " + batches + " => ");
			
			long start = startOffset;
			long end = start + taskSize;
			
			for (int i=0; i<threadCount; i++) {
				threads[i] = new FinderThread(start, end, outputFilename);
				//System.out.println("thread " + i + " " + start + " - " + end);
				threads[i].start();
				start += taskSize;
				end += taskSize;
			}
			
			for (int i=0; i<threadCount; i++) {
				threads[i].join();
				threads[i] = null;
			}
			
			System.out.println(" done!");
			startOffset = start;
		}
		
		long tEnd = System.nanoTime();
		double timeSec = (double)(tEnd - tStart) / 1_000_000_000.0D;
		double timeMin = timeSec / 60.0D;
		double timeHours = timeMin / 60.0D;
		
		double fullHours = Math.floor(timeHours);
		double fullMinutes = Math.floor(timeMin) - 60.0D * fullHours;
		double fullSeconds = Math.floor(timeSec) - 3600.0D * fullHours - 60.0D * fullMinutes;
		
		System.out.println("Finished. Total runtime: ");
		System.out.println(fullHours + " hours");
		System.out.println(fullMinutes + " minutes");
		System.out.println(fullSeconds + " seconds");
		
		//System.out.println((tEnd - tStart)/1000000 + " ms");
	}
}
