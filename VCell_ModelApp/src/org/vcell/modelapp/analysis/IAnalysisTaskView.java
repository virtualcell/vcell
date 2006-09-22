package org.vcell.modelapp.analysis;

import javax.swing.JPanel;

public interface IAnalysisTaskView {
	public void setAnalysisTask(IAnalysisTask newAnalysisTask);
	public IAnalysisTask getAnalysisTask();
	public JPanel getPanel();
}
