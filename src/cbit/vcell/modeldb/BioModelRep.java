package cbit.vcell.modeldb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public class BioModelRep {
	private final KeyValue bmKey;
	private final String name;
	private final int privacy;
	private final User[] groupUsers;
	private final Date date;
	private final String annot;
	private final BigDecimal branchID;
	private final KeyValue modelRef;
	private final User owner;
	private final KeyValue[] simKeyList;
	private final KeyValue[] simContextKeyList;
	private ArrayList<SimContextRep> simContextRepList = new ArrayList<SimContextRep>();
	private ArrayList<SimulationRep> simulationRepList = new ArrayList<SimulationRep>();
	
	public BioModelRep(KeyValue bmKey, String name, int privacy, User[] groupUsers, Date date, String annot, BigDecimal branchID, KeyValue modelRef,
			User owner, KeyValue[] simKeyList, KeyValue[] simContextKeyList) {
		super();
		this.bmKey = bmKey;
		this.name = name;
		this.privacy = privacy;
		this.groupUsers = groupUsers;
		this.date = date;
		this.annot = annot;
		this.branchID = branchID;
		this.modelRef = modelRef;
		this.owner = owner;
		this.simKeyList = simKeyList;
		this.simContextKeyList = simContextKeyList;
	}

	public KeyValue getBmKey() {
		return bmKey;
	}

	public String getName() {
		return name;
	}

	public int getPrivacy() {
		return privacy;
	}

	public User[] getGroupUsers() {
		return groupUsers;
	}

	public Date getDate() {
		return date;
	}

	public String getAnnot() {
		return annot;
	}

	public BigDecimal getBranchID() {
		return branchID;
	}

	public KeyValue getModelRef() {
		return modelRef;
	}

	public User getOwner() {
		return owner;
	}

	public KeyValue[] getSimKeyList() {
		return simKeyList;
	}

	public KeyValue[] getSimContextKeyList() {
		return simContextKeyList;
	}

	public void addSimContextRep(SimContextRep scRep) {
		if (!simContextRepList.contains(scRep)){
			simContextRepList.add(scRep);
		}
	}
	
	public SimContextRep[] getSimContextRepList() {
		return simContextRepList.toArray(new SimContextRep[simContextRepList.size()]);
	}
	
	public void addSimulationRep(SimulationRep simRep) {
		if (!simulationRepList.contains(simRep)){
			simulationRepList.add(simRep);
		}
	}
	
	public SimulationRep[] getSimulationRepList() {
		return simulationRepList.toArray(new SimulationRep[simulationRepList.size()]);
	}

	public SimContextRep getSimContextRepFromMathKey(KeyValue mathKey) {
		for (SimContextRep simContextRep : simContextRepList) {
			if (simContextRep.getMathKey().compareEqual(mathKey)){
				return simContextRep;
			}
		}
		return null;
	}
	
	
}
