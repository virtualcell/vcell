package org.vcell.vis.vismesh;

import org.vcell.util.Matchable;
import org.vcell.vis.mapping.chombo.ChomboCellIndices;

public interface VisPolyhedron extends ChomboCellIndices, Matchable {

	int getRegionIndex();

}
