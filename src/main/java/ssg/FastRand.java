package ssg;

import com.seedfinding.mcmath.util.Mth;

public class FastRand {
	public FastRand() {}
	private long seed;
	
	public void setSeed(long seed) {
		this.seed = seed ^ 25214903917L;
	}
	
	private void nextSeed() {
		this.seed = (this.seed * 25214903917L + 11L) & (Mth.MASK_48);
	}
	
	private int next(int bits) {
		this.nextSeed();
		return (int)(this.seed >> (48-bits));
	}
	
	public int nextInt(int n) {
		return(this.next(31) % n);
	}
	
	public long nextLong() {
		long l = ((long)this.next(32) << 32) + this.next(32);
		return l;
	}
	
	public void setCarverSeed(long structureSeed, int chunkX, int chunkZ) {
		this.setSeed(structureSeed);
		long a = this.nextLong();
		long b = this.nextLong();
		long seed = (long) chunkX * a ^ (long) chunkZ * b ^ structureSeed;
		this.setSeed(seed);
	}
}
