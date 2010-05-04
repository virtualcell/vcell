package cbit.vcell.client.data;

import cbit.vcell.document.SimulationOwner;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;

/**
 * Insert the type's description here.
 * Creation date: (9/19/2005 1:30:44 PM)
 * @author: Frank Morgan
 */
public class SimulationWorkspaceModelInfo implements SimulationModelInfo {

	private SimulationOwner simulationOwner = null;
	private String simulationName = null;

/**
 * SimulationWorkspaceModelInfo constructor comment.
 */
public SimulationWorkspaceModelInfo(SimulationOwner simOwner,String argSimulationName) {
	super();
	simulationOwner = simOwner;
	simulationName = argSimulationName;
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2005 11:04:29 AM)
 * @return java.lang.String
 */
public java.lang.String getContextName() {
	String results = null;
	if(simulationOwner instanceof MathModel){
		MathModel mathModel = (MathModel)simulationOwner;
		results = mathModel.getName();
	}else if(simulationOwner instanceof SimulationContext){
		SimulationContext simContext = (SimulationContext)simulationOwner;
		results = simContext.getBioModel().getName()+"::"+simContext.getName();
	}
	
	return results;
}

/**
 * Insert the method's description here.
 * Creation date: (9/19/2005 1:30:44 PM)
 * @return java.lang.String
 * @param subVolumeIdIn int
 * @param subVolumeIdOut int
 */
public String getMembraneName(int subVolumeIdIn, int subVolumeIdOut, boolean bFromGeometry) {
	String results = null;
	if(simulationOwner instanceof MathModel){
		MathModel mathModel = (MathModel)simulationOwner;
		final GeometrySpec geometrySpec = mathModel.getMathDescription().getGeometry().getGeometrySpec();
		if(geometrySpec.getSubVolume(subVolumeIdIn) != null &&
			geometrySpec.getSubVolume(subVolumeIdOut) != null){
			SubVolume svIn = geometrySpec.getSubVolume(subVolumeIdIn);
			SubVolume svOut = geometrySpec.getSubVolume(subVolumeIdOut);
			SurfaceClass membrane = mathModel.getMathDescription().getGeometry().getGeometrySurfaceDescription().getSurfaceClass(svIn, svOut);
			results = membrane.getName();
		}
	}else if(simulationOwner instanceof SimulationContext){
		SimulationContext simContext = (SimulationContext)simulationOwner;
		SubVolume svIn = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeIdIn);
		SubVolume svOut = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeIdOut);
		if (bFromGeometry) {
			SurfaceClass membrane = simContext.getMathDescription().getGeometry().getGeometrySurfaceDescription().getSurfaceClass(svIn, svOut);
			results = membrane.getName();
		} else {
			if(svIn != null && svOut != null){
				Feature featureIn = simContext.getGeometryContext().getResolvedFeature(svIn);
				Feature featureOut = simContext.getGeometryContext().getResolvedFeature(svOut);
				if(featureIn != null && featureOut != null){
					Structure[] structArr = simContext.getModel().getStructures();
					for(int i=0;i<structArr.length;i+= 1){
						if(structArr[i] instanceof Membrane){
							Membrane mem = (Membrane)structArr[i];
							if((mem.getOutsideFeature() == featureOut && mem.getInsideFeature() == featureIn)||
								(mem.getOutsideFeature() == featureIn && mem.getInsideFeature() == featureOut)){
									results = mem.getName();
									break;
								}
						}
					}
				}
			}
		}
	}
	
	return results;
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2005 11:18:44 AM)
 * @return java.lang.String
 */
public java.lang.String getSimulationName() {
	return simulationName;
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2005 1:30:44 PM)
 * @return java.lang.String
 * @param subVolumeID int
 */
public String getVolumeNamePhysiology(int subVolumeID) {
	String results = null;
	if(simulationOwner instanceof MathModel){
		MathModel mathModel = (MathModel)simulationOwner;
		if(mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeID) != null){
			results = mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeID).getName();
		}
	}else if(simulationOwner instanceof SimulationContext){
		SimulationContext simContext = (SimulationContext)simulationOwner;
		SubVolume sv = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeID);
		if(sv != null){
			Feature volFeature = simContext.getGeometryContext().getResolvedFeature(sv);
			if(volFeature != null){
				results = volFeature.getName();
			}
		}
	}
	
	return results;
}
public String getVolumeNameGeometry(int subVolumeID) {
	String results = null;
	if(simulationOwner instanceof MathModel){
		MathModel mathModel = (MathModel)simulationOwner;
		if(mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeID) != null){
			results = mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeID).getName();
		}
	}else if(simulationOwner instanceof SimulationContext){
		SimulationContext simContext = (SimulationContext)simulationOwner;
		if(simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeID) != null){
			results = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeID).getName();
		}
	}
	
	return results;
}

}