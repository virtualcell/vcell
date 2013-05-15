package cbit.vcell.modeldb;

import java.math.BigDecimal;
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
	
	
}
