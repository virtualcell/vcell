package cbit.vcell.modeldb;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public class MathModelReferenceRep {
	private final KeyValue mmKey;
	private final String name;
	private final User owner;
	
	public MathModelReferenceRep(KeyValue bmKey, String name, User owner) {
		super();
		this.mmKey = bmKey;
		this.name = name;
		this.owner = owner;
	}

	public KeyValue getMmKey() {
		return mmKey;
	}

	public String getName() {
		return name;
	}

	public User getOwner() {
		return owner;
	}

}
