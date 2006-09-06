package org.vcell.physics;

import javax.swing.JPanel;

import org.vcell.model.analysis.IAnalysisTask;
import org.vcell.model.analysis.IAnalysisTaskView;
import org.vcell.physics.component.ModelAnalysisResults;
import org.vcell.physics.component.gui.OOModelingPanel;
import org.vcell.physics.component.gui.OOModelGraphPanel;

public class OOModelingTaskView implements IAnalysisTaskView {

	private OOModelingPanel ooModelingPanel = null;
	private OOModelingTask ooModelingTask = null;
	
	public IAnalysisTask getAnalysisTask() {
		return ooModelingTask;
	}

	public JPanel getPanel() {
		if (ooModelingPanel==null){
			ooModelingPanel = new OOModelingPanel();
		}
		return ooModelingPanel;
	}

	public void setAnalysisTask(IAnalysisTask newAnalysisTask) {
		if (newAnalysisTask instanceof OOModelingTask){
			ooModelingTask = (OOModelingTask)newAnalysisTask;
			getPanel();
			ModelAnalysisResults modelAnalysisResults = ooModelingTask.getModelAnalysisResults();
			if (modelAnalysisResults!=null){
				ooModelingPanel.setBipartiteMatchings(modelAnalysisResults.matching);
				ooModelingPanel.setConnectivityGraphPanelGraph(modelAnalysisResults.connectivityGraph);
				ooModelingPanel.setPartitionGraphPanelGraph(modelAnalysisResults.partitionGraph);
				ooModelingPanel.setPhysicalModelGraphPanelModel(modelAnalysisResults.oOModel);
				ooModelingPanel.setSccGraphModelPanelGraph(modelAnalysisResults.sccGraph);
				ooModelingPanel.setVarEquationAssignments(modelAnalysisResults.varEqnAssignments);
			}else{
				ooModelingPanel.setBipartiteMatchings(null);
				ooModelingPanel.setConnectivityGraphPanelGraph(null);
				ooModelingPanel.setPartitionGraphPanelGraph(null);
				ooModelingPanel.setPhysicalModelGraphPanelModel(null);
				ooModelingPanel.setSccGraphModelPanelGraph(null);
				ooModelingPanel.setVarEquationAssignments(null);
			}
		}else{
			throw new RuntimeException("unsupported analysis task type = "+newAnalysisTask.getClass().getName());
		}

	}

}
