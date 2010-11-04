package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.Hashtable;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.modelopt.AnalysisTask;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.gui.AnalysisTaskComboBoxModel;
import cbit.vcell.modelopt.gui.OptTestPanel;
import cbit.vcell.opt.solvers.OptimizationService;

@SuppressWarnings("serial")
public class AnalysisPanel extends JPanel {

	private static final String PROPERTY_NAME_SIMULATION_CONTEXT = "simulationContext";
	private JPanel taskTablePanel;
	private OptTestPanel ivjoptTestPanel;
	private SimulationContext simulationContext;
	private JButton ivjDeleteAnalysisTaskButton = null;
	private JButton ivjNewAnalysisTaskButton = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton ivjCopyButton;
	private JComboBox ivjAnalysisTaskComboBox = null;	
	private AnalysisTaskComboBoxModel ivjAnalysisTaskComboBoxModel = null;
	
	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getNewAnalysisTaskButton()) 
				newParameterEstimationTaskButton_ActionPerformed();
			else if (e.getSource() == getDeleteAnalysisTaskButton()) 
				deleteAnalysisTaskButton_ActionPerformed();
			else if (e.getSource() == getCopyButton()) 
				copyAnalysisTaskButton_ActionPerformed();
			else if (e.getSource() == getAnalysisTaskComboBox()) 
				analysisTaskComboBox_ActionPerformed();
			
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == AnalysisPanel.this && (evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_CONTEXT))) {
				ivjAnalysisTaskComboBoxModel.setSimulationContext(simulationContext);
				refreshAnalysisTaskEnables();
			}
		}
	};
	
	public AnalysisPanel() {
		super();
		initialize();
	}

	private void initialize() {
		setName("AnalysisPanel");
		setLayout(new java.awt.BorderLayout());
		add(getOptTestPanel(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.NORTH);		
		
		addPropertyChangeListener(ivjEventHandler);
		getNewAnalysisTaskButton().addActionListener(ivjEventHandler);
		getDeleteAnalysisTaskButton().addActionListener(ivjEventHandler);
		getCopyButton().addActionListener(ivjEventHandler);
		getAnalysisTaskComboBox().addActionListener(ivjEventHandler);
	}
	
	private OptTestPanel getOptTestPanel() {
		if (ivjoptTestPanel == null) {
			ivjoptTestPanel = new OptTestPanel();
			ivjoptTestPanel.setName("optTestPanel");
		}
		return ivjoptTestPanel;
	}
	
	private javax.swing.JPanel getButtonPanel() {
		if (taskTablePanel == null) {
			taskTablePanel = new javax.swing.JPanel();
			taskTablePanel.setName("ButtonPanel");
						
			taskTablePanel.setLayout(new FlowLayout());
			getAnalysisTaskComboBox().setPreferredSize(new Dimension(300, (int)getAnalysisTaskComboBox().getPreferredSize().getHeight()));
			taskTablePanel.add(getAnalysisTaskComboBox());			
			taskTablePanel.add(getNewAnalysisTaskButton());			
			taskTablePanel.add(getCopyButton());			
			taskTablePanel.add(getDeleteAnalysisTaskButton());
		}
		return taskTablePanel;
	}
	
	public void setSimulationContext(SimulationContext newValue) {
		SimulationContext oldValue = simulationContext;
		simulationContext = newValue;
		firePropertyChange(PROPERTY_NAME_SIMULATION_CONTEXT, oldValue, newValue);
	}
	
	private AnalysisTask getSelectedAnalysisTask() {
		return (AnalysisTask)getAnalysisTaskComboBox().getSelectedItem();
	}
	private void copyAnalysisTaskButton_ActionPerformed() {
		try {
			AnalysisTask taskToCopy = getSelectedAnalysisTask();
			if (simulationContext != null && taskToCopy != null){
				AnalysisTask newAnalysisTask = simulationContext.copyAnalysisTask(taskToCopy);
				getAnalysisTaskComboBox().setSelectedItem(newAnalysisTask);
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this,e.getMessage(), e);
		}
	}
	
	public void setOptimizationService(OptimizationService arg1) {
		getOptTestPanel().setOptimizationService(arg1);
	}
	
	private javax.swing.JComboBox getAnalysisTaskComboBox() {
		if (ivjAnalysisTaskComboBox == null) {
			ivjAnalysisTaskComboBox = new javax.swing.JComboBox();
			ivjAnalysisTaskComboBox.setName("AnalysisTaskComboBox");
			ivjAnalysisTaskComboBox.setRenderer(new DefaultListCellRenderer() {
				public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					if (value instanceof AnalysisTask){
						AnalysisTask analysisTask = (AnalysisTask)value;
						setText(analysisTask.getAnalysisTypeDisplayName()+" \""+analysisTask.getName()+"\"");
					} else{
						setText((value == null) ? "" : value.toString());
					}
					return this;
				}
			});
			ivjAnalysisTaskComboBox.setEnabled(false);
			ivjAnalysisTaskComboBoxModel = new AnalysisTaskComboBoxModel();
			ivjAnalysisTaskComboBox.setModel(ivjAnalysisTaskComboBoxModel);
		}
		return ivjAnalysisTaskComboBox;
	}
	
	private void deleteAnalysisTaskButton_ActionPerformed() {
		AnalysisTask taskToDelete = getSelectedAnalysisTask();
		if (taskToDelete != null && simulationContext != null) {
			try {
				simulationContext.removeAnalysisTask(taskToDelete);
			} catch (PropertyVetoException e) {
				e.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this, e.getMessage());
			}
		}
		return;
	}
	
	private javax.swing.JButton getDeleteAnalysisTaskButton() {
		if (ivjDeleteAnalysisTaskButton == null) {
			ivjDeleteAnalysisTaskButton = new javax.swing.JButton("Delete");
			ivjDeleteAnalysisTaskButton.setName("DeleteAnalysisTaskButton");
			ivjDeleteAnalysisTaskButton.setEnabled(false);
			ivjDeleteAnalysisTaskButton.setActionCommand("DeleteModelOptSpec");
		}
		return ivjDeleteAnalysisTaskButton;
	}
	
	private javax.swing.JButton getCopyButton() {
		if (ivjCopyButton == null) {
			ivjCopyButton = new javax.swing.JButton();
			ivjCopyButton.setName("CopyButton");
			ivjCopyButton.setText("Copy...");
		}
		return ivjCopyButton;
	}

	private void newParameterEstimationTaskButton_ActionPerformed() {
		try {
			String parameterEstimationName = "task0";
			if (simulationContext==null){
				return;
			}

			AnalysisTask analysisTasks[] = simulationContext.getAnalysisTasks();
			boolean found = true;
			while (found) {
				found = false;
				parameterEstimationName = TokenMangler.getNextEnumeratedToken(parameterEstimationName);
				for (int i = 0;analysisTasks!=null && i < analysisTasks.length; i++){
					if (analysisTasks[i].getName().equals(parameterEstimationName)){
						found = true;
						continue;
					}
				}
			}

			String newParameterEstimationName = null;
			try {
				newParameterEstimationName = DialogUtils.showInputDialog0(this,"name for new parameter estimation set",parameterEstimationName);
			} catch (UtilCancelException ex) {
				// user canceled; it's ok
			}

			if (newParameterEstimationName != null) {
				if (newParameterEstimationName.length() == 0) {
					PopupGenerator.showErrorDialog(this,"Error:\n name for new parameter estimation can't be empty" );
				} else {
					final String finalname = newParameterEstimationName;
					AsynchClientTask task1 = new AsynchClientTask("init task", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
						
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							ParameterEstimationTask newParameterEstimationTask = new ParameterEstimationTask(simulationContext);
							newParameterEstimationTask.setName(finalname);
							hashTable.put("newParameterEstimationTask", newParameterEstimationTask);
						}
					};
					AsynchClientTask task2 = new AsynchClientTask("add task", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							ParameterEstimationTask newParameterEstimationTask = (ParameterEstimationTask)hashTable.get("newParameterEstimationTask");
							simulationContext.addAnalysisTask(newParameterEstimationTask);
							getAnalysisTaskComboBox().setSelectedItem(newParameterEstimationTask);
							refreshAnalysisTaskEnables();
						}
					};
					
					ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
				}
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this,e.getMessage(), e);
		}
	}
	
	private void analysisTaskComboBox_ActionPerformed() {
		getOptTestPanel().setParameterEstimationTask((ParameterEstimationTask)getAnalysisTaskComboBox().getSelectedItem());
		return;
	}
	
	private void refreshAnalysisTaskEnables() {
		boolean bHasTasks = getAnalysisTaskComboBox().getItemCount() > 0;
		getAnalysisTaskComboBox().setEnabled(bHasTasks);
		getDeleteAnalysisTaskButton().setEnabled(bHasTasks);
		getCopyButton().setEnabled(bHasTasks);
	}
	
	private javax.swing.JButton getNewAnalysisTaskButton() {
		if (ivjNewAnalysisTaskButton == null) {
			ivjNewAnalysisTaskButton = new javax.swing.JButton();
			ivjNewAnalysisTaskButton.setName("NewAnalysisTaskButton");
			ivjNewAnalysisTaskButton.setText("New...");
			ivjNewAnalysisTaskButton.setActionCommand("NewModelOptSpec");
		}
		return ivjNewAnalysisTaskButton;
	}

	public void setUserPreferences(UserPreferences arg1) {
		getOptTestPanel().setUserPreferences(arg1);
	}
}
