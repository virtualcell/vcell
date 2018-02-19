package org.vcell.api.common.events;

public class ExportTimeSpecs {
	public final int beginTimeIndex;
	public final int endTimeIndex;
	public final double[] allTimes;
	public final int modeID;
	
	public ExportTimeSpecs(int beginTimeIndex, int endTimeIndex, double[] allTimes, int modeID) {
		super();
		this.beginTimeIndex = beginTimeIndex;
		this.endTimeIndex = endTimeIndex;
		this.allTimes = allTimes;
		this.modeID = modeID;
	}
}
