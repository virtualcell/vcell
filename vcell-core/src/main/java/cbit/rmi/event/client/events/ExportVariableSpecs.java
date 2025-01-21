package cbit.rmi.event.client.events;

public class ExportVariableSpecs {
	public final String[] variableNames;
	public final int modeID;
	
	public ExportVariableSpecs(String[] variableNames, int modeID) {
		super();
		this.variableNames = variableNames;
		this.modeID = modeID;
	}
}
