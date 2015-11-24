package org.vcell.vis.mapping.chombo;

import java.util.ArrayList;
import java.util.Arrays;

import org.vcell.vis.chombo.ChomboMeshData;
import org.vcell.vis.mapping.VisMeshData;
import org.vcell.vis.vismesh.VisMesh;

public class ChomboVisMeshData implements VisMeshData {
	private final ChomboMeshData chomboMeshData;
	private final VisMesh visMesh;
	private final boolean bMembrane;
	
	public ChomboVisMeshData(ChomboMeshData chomboMeshData, VisMesh visMesh, boolean bMembrane){
		this.chomboMeshData = chomboMeshData;
		this.visMesh = visMesh;
		this.bMembrane = bMembrane;
	}
	
	@Override
	public String[] getVarNames() {
		ArrayList<String> names = new ArrayList<String>();
		if (bMembrane){
			names.addAll(Arrays.asList(chomboMeshData.getMembraneBuiltinNames()));
			names.addAll(Arrays.asList(chomboMeshData.getMembraneDataNames()));
		}else{
			names.addAll(Arrays.asList(chomboMeshData.getVolumeBuiltinNames()));
			names.addAll(Arrays.asList(chomboMeshData.getVolumeDataNames()));
		}
		return names.toArray(new String[names.size()]);
	}

	@Override
	public double getTime() {
		return chomboMeshData.getTime();
	}

	@Override
	public double[] getData(String var) {
		if (!bMembrane){
			if (chomboMeshData.getMesh().getDimension()==2){
				double[] cellData = chomboMeshData.getVolumeCellData(var, visMesh.getPolygons());
				return cellData;
			}else if (chomboMeshData.getMesh().getDimension()==3){
				double[] cellData = chomboMeshData.getVolumeCellData(var, visMesh.getPolyhedra());
				return cellData;
			}else{
				throw new RuntimeException("expected 2D or 3D mesh");
			}
		}else{
			if (chomboMeshData.getMesh().getDimension()==2){
				double[] cellData = chomboMeshData.getMembraneCellData(var, visMesh.getLines());
				return cellData;
			}else if (chomboMeshData.getMesh().getDimension()==3){
				double[] cellData = chomboMeshData.getMembraneCellData(var, visMesh.getSurfaceTriangles());
				return cellData;
			}else{
				throw new RuntimeException("expected 2D or 3D mesh");
			}
		}
	}
}
