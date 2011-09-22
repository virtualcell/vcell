package cbit.vcell.geometry.surface;

import cbit.vcell.geometry.gui.HitList;

public class RayCastResults {
	private int numX;
	private int numY;
	private int numZ;
	private HitList[] hitListsXY;  // index = i + numX*j
	private HitList[] hitListsXZ;  // index = i + numX*k
	private HitList[] hitListsYZ;  // index = j + numY*k
	
	public RayCastResults(HitList[] aHitListXY, HitList[] aHitListXZ, HitList[] aHitListYZ, int aNumX, int aNumY, int aNumZ){
		this.hitListsXY = aHitListXY;
		this.hitListsXZ = aHitListXZ;
		this.hitListsYZ = aHitListYZ;
		this.numX = aNumX;
		this.numY = aNumY;
		this.numZ = aNumZ;
	}

	public int getNumX() {
		return numX;
	}

	public int getNumY() {
		return numY;
	}

	public int getNumZ() {
		return numZ;
	}

	public HitList[] getHitListsXY() {
		return hitListsXY;
	}

	public HitList[] getHitListsXZ() {
		return hitListsXZ;
	}

	public HitList[] getHitListsYZ() {
		return hitListsYZ;
	}
	
	
}
