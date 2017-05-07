package cbit.vcell.simdata;

import cbit.vcell.math.VariableType;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.solver.SimulationModelInfo;

public class DataInfoProvider{
	private PDEDataContext pdeDataContext;
	private SimulationModelInfo simulationModelInfo;
	public DataInfoProvider(PDEDataContext pdeDataContext, SimulationModelInfo simulationModelInfo){
		this.pdeDataContext = pdeDataContext;
		this.simulationModelInfo = simulationModelInfo;
	}
	public VolumeDataInfo getVolumeDataInfo(int volumeIndex){
		return new VolumeDataInfo(volumeIndex,pdeDataContext.getCartesianMesh(),simulationModelInfo);
	}
	public MembraneDataInfo getMembraneDataInfo(int membraneIndex){
		return new MembraneDataInfo(membraneIndex,pdeDataContext.getCartesianMesh(),simulationModelInfo);
	}
	public boolean isDefined(int dataIndex){
		if (pdeDataContext.getCartesianMesh().isChomboMesh()) { //Chombo Hack
			double sol = pdeDataContext.getDataValues()[dataIndex];
			return sol != SimDataConstants.BASEFAB_REAL_SETVAL && !Double.isNaN(sol);
		}
		return isDefined(pdeDataContext.getDataIdentifier(),dataIndex);
	}
	public boolean isDefined(DataIdentifier dataIdentifier,int dataIndex){
		try {
			Domain varDomain = dataIdentifier.getDomain();
			if (varDomain == null || dataIdentifier.getVariableType().equals(VariableType.POSTPROCESSING)) {
				return true;
			}
			VariableType varType = dataIdentifier.getVariableType();
			if (pdeDataContext.getCartesianMesh().isChomboMesh() && !Double.isNaN(pdeDataContext.getDataValues()[dataIndex]))
			{
				return true;
			}
			if(varType.equals(VariableType.VOLUME) || varType.equals(VariableType.VOLUME_REGION)){
				int subvol = pdeDataContext.getCartesianMesh().getSubVolumeFromVolumeIndex(dataIndex);
				if (simulationModelInfo.getVolumeNameGeometry(subvol) == null || simulationModelInfo.getVolumeNameGeometry(subvol).equals(varDomain.getName())) {
					return true;
				}				
			}else if(varType.equals(VariableType.MEMBRANE) || varType.equals(VariableType.MEMBRANE_REGION)){
				String memSubdomainName = pdeDataContext.getCartesianMesh().getMembraneSubdomainNamefromMemIndex(dataIndex);
				if (varDomain.getName().equals(memSubdomainName)){
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public final SimulationModelInfo getSimulationModelInfo() {
		return simulationModelInfo;
	}
	public PDEDataContext getPDEDataContext(){
		return pdeDataContext;
	}
}