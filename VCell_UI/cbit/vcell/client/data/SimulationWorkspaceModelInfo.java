package cbit.vcell.client.data;
/**
 * Insert the type's description here.
 * Creation date: (9/19/2005 1:30:44 PM)
 * @author: Frank Morgan
 */
public class SimulationWorkspaceModelInfo implements SimulationModelInfo {

	private cbit.vcell.simulation.SimulationOwner simulationOwner = null;
	private String simulationName = null;

/**
 * SimulationWorkspaceModelInfo constructor comment.
 */
public SimulationWorkspaceModelInfo(cbit.vcell.simulation.SimulationOwner simOwner,String argSimulationName) {
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
	if(simulationOwner instanceof cbit.vcell.mathmodel.MathModel){
		cbit.vcell.mathmodel.MathModel mathModel = (cbit.vcell.mathmodel.MathModel)simulationOwner;
		results = mathModel.getName();
	}else if(simulationOwner instanceof cbit.vcell.mapping.SimulationContext){
		cbit.vcell.mapping.SimulationContext simContext = (cbit.vcell.mapping.SimulationContext)simulationOwner;
		results = simContext.getSimulationContextOwner().getName()+"::"+simContext.getName();
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
public String getMembraneName(int subVolumeIdIn, int subVolumeIdOut) {
	String results = null;
	if(simulationOwner instanceof cbit.vcell.mathmodel.MathModel){
		cbit.vcell.mathmodel.MathModel mathModel = (cbit.vcell.mathmodel.MathModel)simulationOwner;
		if(	mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeIdIn) != null &&
			mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeIdOut) != null){
				String inName = mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeIdIn).getName();
				String outName = mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeIdOut).getName();
				results = inName+"_"+outName+"_mem";
		}
	}else if(simulationOwner instanceof cbit.vcell.mapping.SimulationContext){
		cbit.vcell.mapping.SimulationContext simContext = (cbit.vcell.mapping.SimulationContext)simulationOwner;
		cbit.vcell.geometry.SubVolume svIn = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeIdIn);
		cbit.vcell.geometry.SubVolume svOut = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeIdOut);
		if(svIn != null && svOut != null){
			cbit.vcell.model.Feature featureIn = simContext.getGeometryContext().getResolvedFeature(svIn);
			cbit.vcell.model.Feature featureOut = simContext.getGeometryContext().getResolvedFeature(svOut);
			if(featureIn != null && featureOut != null){
				cbit.vcell.model.Structure[] structArr = simContext.getModel().getStructures();
				for(int i=0;i<structArr.length;i+= 1){
					if(structArr[i] instanceof cbit.vcell.model.Membrane){
						cbit.vcell.model.Membrane mem = (cbit.vcell.model.Membrane)structArr[i];
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
public String getVolumeName(int subVolumeID) {
	String results = null;
	if(simulationOwner instanceof cbit.vcell.mathmodel.MathModel){
		cbit.vcell.mathmodel.MathModel mathModel = (cbit.vcell.mathmodel.MathModel)simulationOwner;
		if(mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeID) != null){
			results = mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeID).getName();
		}
	}else if(simulationOwner instanceof cbit.vcell.mapping.SimulationContext){
		cbit.vcell.mapping.SimulationContext simContext = (cbit.vcell.mapping.SimulationContext)simulationOwner;
		if(simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeID) != null){
			results = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeID).getName();
		}
	}
	
	return results;
}
}