package org.vcell.physics;

import org.jdom.Element;
import org.vcell.modelapp.analysis.IAnalysisTask;
import org.vcell.modelapp.analysis.IAnalysisTaskFactory;
import org.vcell.modelapp.analysis.IAnalysisTaskView;
import org.vcell.physics.component.gui.OOModelGraphPanel;

import cbit.vcell.mapping.SimulationContext;

public class OOModelingTaskFactory implements IAnalysisTaskFactory {

	private static OOModelingTaskView oOModelingTaskView = null;
	
	public IAnalysisTask create(Element rootElement, SimulationContext simContext) {
		// TODO Auto-generated method stub
		return null;
	}

	public IAnalysisTask createNew(SimulationContext simContext) throws Exception {
		return new OOModelingTask(simContext,"unnamed");
	}

	public String getDisplayName() {
		return "OOModeling Analysis";
	}

	public String getShortDescription() {
		return "Object Oriented Modeling analysis";
	}
	
	public IAnalysisTaskView getView() {
		if (OOModelingTaskFactory.oOModelingTaskView == null){
			OOModelingTaskFactory.oOModelingTaskView = new OOModelingTaskView();
		}
		return OOModelingTaskFactory.oOModelingTaskView;
	}

}
