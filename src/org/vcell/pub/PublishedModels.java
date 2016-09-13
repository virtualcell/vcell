package org.vcell.pub;

import java.io.Serializable;

import org.vcell.util.document.KeyValue;

public class PublishedModels implements Serializable {
	final Publication vcellPub;
	final KeyValue[] bioModelKeys;
	final KeyValue[] mathModelKeys;
	
	public PublishedModels(Publication vcellPub, KeyValue[] bioModelKeys, KeyValue[] mathModelKeys) {
		super();
		this.vcellPub = vcellPub;
		this.bioModelKeys = bioModelKeys;
		this.mathModelKeys = mathModelKeys;
	}
}