package cbit.vcell.geometry.surface;

public interface VolumeSamples {

	int getNumXYZ();

	void add(int volumeIndex, long exteriorMask, float f);

}
