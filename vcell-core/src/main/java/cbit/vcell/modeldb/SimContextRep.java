package cbit.vcell.modeldb;

import java.math.BigDecimal;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public class SimContextRep {
	private final KeyValue scKey;
	private final BigDecimal branchID;
	private final String name;
	private final User owner;
	private final KeyValue mathKey;
	
	public SimContextRep(KeyValue scKey, BigDecimal branchID, String name, User owner, KeyValue mathKey) {
		this.scKey = scKey;
		this.name = name;
		this.branchID = branchID;
		this.owner = owner;
		this.mathKey = mathKey;
	}

	public KeyValue getScKey() {
		return scKey;
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

}