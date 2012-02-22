package cbit.vcell.geometry.surface;

import cbit.vcell.geometry.SubVolume;

public class SubvolumeSignedDistanceMap {
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

}
