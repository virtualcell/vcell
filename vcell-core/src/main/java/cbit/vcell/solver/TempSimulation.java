package cbit.vcell.solver;

import java.util.TreeMap;

import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;

import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial") 
public class TempSimulation extends Simulation {
	//Store association of tempSimID to originalSimID for use in ImageJHelper
	public static final TreeMap<String,String> mapTempSimIDToOriginalSimID = new TreeMap<String,String>();
	public static final TreeMap<String,String> mapTempSimIDToModelAppSim = new TreeMap<String,String>();
	final private SimulationVersion tempSimVersion = SimulationVersion.createTempSimulationVersion();
	public TempSimulation(Simulation simulation, boolean bCloneMath) {
		super(simulation, bCloneMath);
		mapTempSimIDToModelAppSim.put(tempSimVersion.getVersionKey().toString(), createModelAppSimName(simulation));		
		mapTempSimIDToOriginalSimID.put(tempSimVersion.getVersionKey().toString(), (simulation.getVersion()==null || simulation.getVersion().getVersionKey()==null?"":simulation.getVersion().getVersionKey().toString()));
	}

	public static String createModelAppSimName(Simulation simulation) {
		String modelAppSim = simulation.getName();
		if(simulation.getSimulationOwner() instanceof SimulationContext) {//biomodel
			modelAppSim = ((SimulationContext)simulation.getSimulationOwner()).getBioModel().getName()+"_"+simulation.getSimulationOwner().getName()+"_"+modelAppSim;
		}else {//mathmodel
			modelAppSim = simulation.getSimulationOwner().getName()+"_"+modelAppSim;
		}
		return modelAppSim;
	}
	@Override
	public Version getVersion() {
		return tempSimVersion;
	}

	@Override
	public String getSimulationID() {
		return createSimulationID(tempSimVersion.getVersionKey());
	}

	@Override
	public SimulationInfo getSimulationInfo() {
		return new SimulationInfo(null, tempSimVersion, VCellSoftwareVersion.fromSystemProperty());
	}
}