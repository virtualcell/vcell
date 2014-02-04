package org.vcell.vis.mapping;

import org.vcell.vis.chombo.ChomboMeshData;
import org.vcell.vis.vismesh.VisMesh;
import org.vcell.vis.vismesh.VisMeshData;

public class ChomboVisMeshData implements VisMeshData {
	private final ChomboMeshData chomboMeshData;
	private final VisMesh visMesh;
	
	public ChomboVisMeshData(ChomboMeshData chomboMeshData, VisMesh visMesh){
		this.chomboMeshData = chomboMeshData;
		this.visMesh = visMesh;
	}
	
	@Override
	public String[] getVarNames() {
		return chomboMeshData.getAllNames();
	}

	@Override
	public double getTime() {
		return chomboMeshData.getTime();
	}

	@Override
	public double[] getData(String var) {
		if (chomboMeshData.getMesh().getDimension()==2){
			double[] cellData = chomboMeshData.getPolygonData(var, visMesh.getPolygons());
			return cellData;
		}else if (chomboMeshData.getMesh().getDimension()==3){
			double[] cellData = chomboMeshData.getPolyhedaData(var, visMesh.getPolyhedra());
			return cellData;
		}else{
			throw new RuntimeException("expected 2D or 3D mesh");
		}
	}
}
