package org.vcell.modelapp.parest;

import javax.swing.JPanel;

import org.vcell.modelapp.analysis.IAnalysisTask;
import org.vcell.modelapp.analysis.IAnalysisTaskView;

import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.gui.OptTestPanel;

public class ParameterEstimationTaskView implements IAnalysisTaskView {

	private OptTestPanel optTestPanel = null;
	
	public JPanel getPanel() {
		if (optTestPanel==null){
			optTestPanel = new OptTestPanel();
		}
		return optTestPanel;
	}

	public void setAnalysisTask(IAnalysisTask newAnalysisTask) {
		getPanel();
		optTestPanel.setParameterEstimationTask((ParameterEstimationTask)newAnalysisTask);
	}
	
	public IAnalysisTask getAnalysisTask(){
		if (optTestPanel!=null){
			return optTestPanel.getParameterEstimationTask();
		}else{
			return null;
		}
	}

}
