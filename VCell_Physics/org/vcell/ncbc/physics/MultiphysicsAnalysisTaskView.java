package org.vcell.ncbc.physics;

import javax.swing.JPanel;

import org.vcell.modelapp.analysis.IAnalysisTask;
import org.vcell.modelapp.analysis.IAnalysisTaskView;
import org.vcell.ncbc.physics.component.gui.PhysicalModelGraphPanel;

public class MultiphysicsAnalysisTaskView implements IAnalysisTaskView {

	private PhysicalModelGraphPanel physicalModelGraphPanel = null;
	private MultiphysicsAnalysisTask multiphysicsAnalysisTask = null;
	
	public IAnalysisTask getAnalysisTask() {
		return multiphysicsAnalysisTask;
	}

	public JPanel getPanel() {
		if (physicalModelGraphPanel==null){
			physicalModelGraphPanel = new PhysicalModelGraphPanel();
		}
		return physicalModelGraphPanel;
	}

	public void setAnalysisTask(IAnalysisTask newAnalysisTask) {
		if (newAnalysisTask instanceof MultiphysicsAnalysisTask){
			multiphysicsAnalysisTask = (MultiphysicsAnalysisTask)newAnalysisTask;
			getPanel();
			physicalModelGraphPanel.setPhysicalModel(multiphysicsAnalysisTask.getPhysicalModel());
		}else{
			throw new RuntimeException("unsupported analysis task type = "+newAnalysisTask.getClass().getName());
		}

	}

}
