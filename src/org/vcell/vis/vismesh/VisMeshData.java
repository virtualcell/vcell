package org.vcell.vis.vismesh;

import java.io.IOException;

public interface VisMeshData {
	public String[] getVarNames();
	public double getTime();
	public double[] getData(String var) throws IOException;
}
