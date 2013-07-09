package cbit.vcell.modeldb;

import java.math.BigDecimal;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.SolverTaskDescription;

public class SimulationRep {
	private final KeyValue key;
	private final BigDecimal branchID;
	private final String name;
	private final User owner;
	private final KeyValue mathKey;
	private final SolverTaskDescription solverTaskDescription;
	private final MathOverrides.Element[] mathOverrideElements;
	
	public SimulationRep(KeyValue key, BigDecimal branchID, String name, User owner, KeyValue mathKey, SolverTaskDescription solverTaskDescription, MathOverrides.Element[] mathOverrideElements) {
		this.key = key;
		this.name = name;
		this.branchID = branchID;
		this.owner = owner;
		this.mathKey = mathKey;
		this.solverTaskDescription = solverTaskDescription;
		this.mathOverrideElements = mathOverrideElements;
	}

	public KeyValue getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getBranchID() {
		return branchID;
	}

	public User getOwner() {
		return owner;
	}

	public KeyValue getMathKey() {
		return mathKey;
	}
	
	public SolverTaskDescription getSolverTaskDescription(){
		return solverTaskDescription;
	}
	
	public MathOverrides.Element[] getMathOverrideElements(){
		return mathOverrideElements;
	}
	
	public int getScanCount(){
		int numScans = 1;
		for (MathOverrides.Element mathOverrideElement : mathOverrideElements) {
			if (mathOverrideElement.getSpec()!=null){
				numScans *= mathOverrideElement.getSpec().getNumValues();
			}
		}
		return numScans;
	}

}