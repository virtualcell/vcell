package org.vcell.pathway;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class SequenceInterval extends SequenceLocation {
	private SequenceSite sequenceIntervalBegin;
	private SequenceSite sequenceIntervalEnd;
	
	public SequenceSite getSequenceIntervalBegin() {
		return sequenceIntervalBegin;
	}
	public SequenceSite getSequenceIntervalEnd() {
		return sequenceIntervalEnd;
	}
	public void setSequenceIntervalBegin(SequenceSite sequenceIntervalBegin) {
		this.sequenceIntervalBegin = sequenceIntervalBegin;
	}
	public void setSequenceIntervalEnd(SequenceSite sequenceIntervalEnd) {
		this.sequenceIntervalEnd = sequenceIntervalEnd;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);

		if(sequenceIntervalBegin == objectProxy) {
			sequenceIntervalBegin = (SequenceSite) concreteObject;
		}
		if(sequenceIntervalEnd == objectProxy) {
			sequenceIntervalEnd = (SequenceSite) concreteObject;
		}
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "sequenceIntervalBegin",sequenceIntervalBegin,level);
		printObject(sb, "sequenceIntervalEnd",sequenceIntervalEnd,level);
	}
}
