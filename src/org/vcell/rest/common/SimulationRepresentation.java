package org.vcell.rest.common;

import org.vcell.util.document.KeyValue;

import cbit.vcell.messaging.db.BioModelLink;
import cbit.vcell.messaging.db.MathModelLink;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.modeldb.SimContextRep;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.parser.ExpressionException;


public class SimulationRepresentation {
	
	private final String key;
	private final String branchId;
	private final String name;
	private final String ownerName;
	private final String ownerKey;
	private final String mathKey;
	private final String solverName;
	private final int scanCount;
	private final MathModelLink mathModelLink;
	private final BioModelLink bioModelLink;
	private final OverrideRepresentation[] overrides;
	

	public SimulationRepresentation(SimulationRep simulationRep, BioModelRep bioModelRep) throws ExpressionException {
		key = simulationRep.getKey().toString();
		branchId = simulationRep.getBranchID().toString();
		name = simulationRep.getName();
		ownerKey = simulationRep.getOwner().getID().toString();
		ownerName = simulationRep.getOwner().getName();
		mathKey = simulationRep.getMathKey().toString();
		solverName = simulationRep.getSolverTaskDescription().getSolverDescription().getDisplayLabel();
		scanCount = simulationRep.getScanCount();
		this.mathModelLink = null;
		SimContextRep simContextRep = bioModelRep.getSimContextRepFromMathKey(new KeyValue(mathKey));
		if (simContextRep==null){
			System.out.println("couldn't find simContext for MathKey = "+mathKey.toString());
		}
		overrides = new OverrideRepresentation[simulationRep.getMathOverrideElements().length];
		for (int i=0;i<simulationRep.getMathOverrideElements().length;i++){
			overrides[i] = new OverrideRepresentation(simulationRep.getMathOverrideElements()[i]);
		}
		this.bioModelLink = new BioModelLink(
				bioModelRep.getBmKey().toString(), 
				bioModelRep.getBranchID().toString(), 
				bioModelRep.getName(),  
				(simContextRep!=null)?(simContextRep.getScKey().toString()):null, 
				(simContextRep!=null)?(simContextRep.getBranchID().toString()):null,
				(simContextRep!=null)?(simContextRep.getName()):null);
	}

	public String getKey() {
		return key;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getOwnerKey() {
		return ownerKey;
	}

	public String getMathKey() {
		return mathKey;
	}
	
	public String getSolverName(){
		return solverName;
	}
	
	public int getScanCount(){
		return scanCount;
	}

	public MathModelLink getMathModelLink(){
		return mathModelLink;
	}

	public BioModelLink getBioModelLink(){
		return bioModelLink;
	}
	
	public OverrideRepresentation[] getOverrides(){
		return overrides;
	}

}
