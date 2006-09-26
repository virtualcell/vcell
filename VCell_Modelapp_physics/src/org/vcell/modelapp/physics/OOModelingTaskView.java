package org.vcell.modelapp.physics;

import javax.swing.JPanel;

import org.vcell.modelapp.analysis.IAnalysisTask;
import org.vcell.modelapp.analysis.IAnalysisTaskView;
import org.vcell.physics.component.gui.OOModelingPanel;
import org.vcell.physics.math.ModelAnalysisResults;

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
				ooModelingPanel.setStronglyConnectedComponents(modelAnalysisResults.sccArray);
				ooModelingPanel.setVarEquationAssignments(modelAnalysisResults.varEqnAssignments);
				ooModelingPanel.setMathDescription(modelAnalysisResults.mathDescription);
			}else{
				ooModelingPanel.setBipartiteMatchings(null);
				ooModelingPanel.setConnectivityGraphPanelGraph(null);
				ooModelingPanel.setPartitionGraphPanelGraph(null);
				ooModelingPanel.setPhysicalModelGraphPanelModel(null);
				ooModelingPanel.setSccGraphModelPanelGraph(null);
				ooModelingPanel.setStronglyConnectedComponents(null);
				ooModelingPanel.setVarEquationAssignments(null);
				ooModelingPanel.setMathDescription(null);
			}
		}else{
			throw new RuntimeException("unsupported analysis task type = "+newAnalysisTask.getClass().getName());
		}

	}

}
