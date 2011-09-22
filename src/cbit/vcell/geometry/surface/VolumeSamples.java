package cbit.vcell.geometry.surface;

import java.util.Arrays;

public class VolumeSamples {
	private long[] incidentSurfaceMask;
	private float[] distanceMapL1;
	
	public VolumeSamples(int size){
		this.incidentSurfaceMask = new long[size];
		this.distanceMapL1 = new float[size];
		Arrays.fill(this.distanceMapL1,Float.MAX_VALUE);
	}

	public long[] getIncidentSurfaceMask() {
		return incidentSurfaceMask;
	}

	public float[] getDistanceMapL1() {
		return distanceMapL1;
	}
	
	public boolean hasZeros(){
		boolean bHasZero = false;
		for (long mask : incidentSurfaceMask){
			if (mask == 0L){
				bHasZero = true;
				break;
			}
		}
		return bHasZero;
	}
	
	public void add(int index, long mask, float distance){
//		System.out.println("index="+index+", mask="+mask+", distance="+distance);
		incidentSurfaceMask[index] = incidentSurfaceMask[index] | mask;
		distanceMapL1[index] = Math.min(distanceMapL1[index],distance);
	}
}
