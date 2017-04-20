package cbit.vcell.geometry.surface;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import cbit.vcell.geometry.SubVolume;


public class SubvolumeSignedDistanceMap {
	
	private static final double MAX_NUMBER = Double.POSITIVE_INFINITY;

	private SubVolume subVolume;
	private double[] samplesX;
	private double[] samplesY;
	private double[] samplesZ;
	private double[] signedDistances;
	
	public SubvolumeSignedDistanceMap(SubVolume subVolume, double[] samplesX,
			double[] samplesY, double[] samplesZ, double[] signedDistances) {
		super();
		this.subVolume = subVolume;
		this.samplesX = samplesX;
		this.samplesY = samplesY;
		this.samplesZ = samplesZ;
		this.signedDistances = signedDistances;
	}
	
	public SubVolume getSubVolume() {
		return subVolume;
	}
	public double[] getSamplesX() {
		return samplesX;
	}
	public double[] getSamplesY() {
		return samplesY;
	}
	public double[] getSamplesZ() {
		return samplesZ;
	}
	public double[] getSignedDistances() {
		return signedDistances;
	}

	// save some points which we may visualize with VisIt
	//
	public void to3DFile(int count) {
//		try {
//		int xm = samplesX.length;
//		int ym = samplesY.length;
//		BufferedWriter out = new BufferedWriter(new FileWriter("c:\\TEMP\\distance" + count + ".3D"));
//		out.write("x y z value\n");
//		
//		for(int j=0; j<signedDistances.length; j++) {
//			int x = DistanceMapGenerator.getX(j,xm,ym);
//			int y = DistanceMapGenerator.getY(j,xm,ym);
//			int z = DistanceMapGenerator.getZ(j,xm,ym);
//			if(signedDistances[j] < MAX_NUMBER) {
//				if(j%5 == 0) {
//					out.write(x + " " + y + " " + z + " " + (int)(signedDistances[j]*10) + "\n");
//				}
//			}
//		}
//		out.close();
//		} catch (IOException e) {
//		}			
	}
}
