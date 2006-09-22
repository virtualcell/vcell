package org.vcell.ncbc.physics;

import org.jdom.Element;
import org.vcell.modelapp.analysis.IAnalysisTask;
import org.vcell.modelapp.analysis.IAnalysisTaskFactory;
import org.vcell.modelapp.analysis.IAnalysisTaskView;
import org.vcell.ncbc.physics.component.gui.PhysicalModelGraphPanel;

import cbit.vcell.modelapp.SimulationContext;


public class MultiphysicsAnalysisTaskFactory implements IAnalysisTaskFactory {

	private static MultiphysicsAnalysisTaskView multiphysicsAnalysisTaskView = null;
	
	public IAnalysisTask create(Element rootElement, SimulationContext simContext) {
		// TODO Auto-generated method stub
		return null;
	}

	public IAnalysisTask createNew(SimulationContext simContext) throws Exception {
		return new MultiphysicsAnalysisTask(simContext,"unnamed");
	}

	public String getDisplayName() {
		return "Physics Layer Analysis";
	}

	public String getShortDescription() {
		return "Physics layer analysis";
	}
	
	public IAnalysisTaskView getView() {
		if (MultiphysicsAnalysisTaskFactory.multiphysicsAnalysisTaskView == null){
			MultiphysicsAnalysisTaskFactory.multiphysicsAnalysisTaskView = new MultiphysicsAnalysisTaskView();
		}
		return MultiphysicsAnalysisTaskFactory.multiphysicsAnalysisTaskView;
	}

}
