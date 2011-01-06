package org.vcell.pathway;

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
}
