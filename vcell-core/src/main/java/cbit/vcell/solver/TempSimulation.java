package cbit.vcell.solver;

import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;

@SuppressWarnings("serial") 
public class TempSimulation extends Simulation {
	final private SimulationVersion tempSimVersion = SimulationVersion.createTempSimulationVersion();
	public TempSimulation(Simulation simulation, boolean bCloneMath) {
		super(simulation, bCloneMath);
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