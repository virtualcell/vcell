package org.vcell.vis.mapping;

import java.io.IOException;

public interface VisMeshData {
	public String[] getVarNames();
	public double getTime();
	public double[] getData(String var) throws IOException;
}
