package cbit.vcell.modeldb;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public class BioModelReferenceRep {
	private final KeyValue bmKey;
	private final String name;
	private final User owner;
	
	public BioModelReferenceRep(KeyValue bmKey, String name, User owner) {
		super();
		this.bmKey = bmKey;
		this.name = name;
		this.owner = owner;
	}

	public KeyValue getBmKey() {
		return bmKey;
	}

	public String getName() {
		return name;
	}

	public User getOwner() {
		return owner;
	}

}
