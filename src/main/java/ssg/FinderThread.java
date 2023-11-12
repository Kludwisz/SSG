package ssg;

import java.io.File;
import java.io.FileWriter;

import com.seedfinding.mccore.rand.seed.RegionSeed;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.pos.CPos;

public class FinderThread extends Thread {
	private static final DistanceMetric EUSQ = DistanceMetric.EUCLIDEAN_SQ;
	private static final DistanceMetric CHEB = DistanceMetric.CHEBYSHEV;
	
	private final long bastionSalt = 30084232L;
	private final long portalSalt = 34222645L;
	private final long fortSaltNegX = 30084232L - RegionSeed.A;
	private final long fortSaltNegZ = 30084232L - RegionSeed.B;

	private final FastRand frand = new FastRand();
	private final PortalFilter portalFilter = new PortalFilter();
	
	private final long startseed, endseed;
	private final String outputFileName;

	public FinderThread(long startseed, long endseed, String outputFileName) {
		this.startseed = startseed;
		this.endseed = endseed;
		this.outputFileName = outputFileName;
	}
	
	@Override
	public void run() {
		this.find();
	}
	
	public void find() {
		for (long i=this.startseed; i<this.endseed; i++) {
			this.process(i);
		}
	}
	
	private void process(long structseed) {
		// bastion near 0,0
		frand.setSeed(structseed + bastionSalt);
		int bx = frand.nextInt(23);
		int bz = frand.nextInt(23);
		if (bx > 2 || bz > 2 || frand.nextInt(5) < 2) return;
		
		CPos bast = new CPos(bx, bz);
		CPos fort = null;
				
		// close fort in -1 0 OR 0 -1
		boolean foundCloseFort = false;
		
		frand.setSeed(structseed + fortSaltNegX);
		fort = new CPos(frand.nextInt(23) - 27, frand.nextInt(23));
		
		if (fort.distanceTo(bast, EUSQ) <= 64 && frand.nextInt(5) < 2)
			foundCloseFort = true;
		else {
			frand.setSeed(structseed + fortSaltNegZ);
			fort = new CPos(frand.nextInt(23), frand.nextInt(23) - 27);
			if (fort.distanceTo(bast, EUSQ) <= 64 && frand.nextInt(5) < 2)
				foundCloseFort = true;
		}

		if (!foundCloseFort) return;
		
		// ruined portal close in 0,0 region
		frand.setSeed(structseed + portalSalt);
		CPos rp = new CPos(frand.nextInt(25), frand.nextInt(25));
		if (rp.distanceTo(CPos.ZERO, CHEB) > 12)
			return;
		
		// good potential RP loot
		if (!portalFilter.portalCanBeGood(structseed, rp))
			return;
		
		writeToFile(structseed);
	}
	
	private synchronized void writeToFile(long structseed) {
		try {
			FileWriter fout = new FileWriter(new File(outputFileName), true);
			fout.append(Long.toString(structseed) + '\n');
			fout.close();
		}
		catch(Exception ex) {
			System.out.println("File write failed: " + ex.getMessage());
		}
	}
}
