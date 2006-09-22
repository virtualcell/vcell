package org.vcell.modelapp.analysis;

import org.jdom.Element;

import cbit.vcell.modelapp.SimulationContext;


public interface IAnalysisTaskFactory {
	
	public IAnalysisTask create(Element rootElement, SimulationContext simContext);

	public IAnalysisTask createNew(SimulationContext simContext) throws Exception;

	public IAnalysisTaskView getView();
	
	public String getDisplayName();
	
	public String getShortDescription();
}
