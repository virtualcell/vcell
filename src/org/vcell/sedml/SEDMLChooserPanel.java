package org.vcell.sedml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton.ToggleButtonModel;

import org.jlibsedml.AbstractTask;
import org.jlibsedml.RepeatedTask;
import org.jlibsedml.SedML;
import org.jlibsedml.SubTask;
import org.jlibsedml.Task;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

public class SEDMLChooserPanel extends JPanel {
	
	private SedML sedml;
	public ButtonGroup group = new ButtonGroup();
	
	public class SEDMLRadioButtonModel extends ToggleButtonModel {
		
		AbstractTask t = null;
		
		public SEDMLRadioButtonModel(AbstractTask t) {
			super();
			this.t = t;
		}
		public AbstractTask getTask() {
			return t;
		}
	}
	
		// List<AbstractTask> taskList
	
	public SEDMLChooserPanel(SedML sedml) {
		super();
		this.sedml = sedml;
		initialize();
	}
	
	private void initialize() {

		setLayout(new GridBagLayout());
		int gridy = 0;
		//structureList.clear();
		for(AbstractTask at : sedml.getTasks()) {
			
			String text = "";
			String tooltip = "";
			
			if(at instanceof Task) {
				Task t = (Task)at;
				text = " Simple task, id " + t.getId() + " - " + 
						sedml.getModelWithId(t.getModelReference()).getClass().getSimpleName() + " '" +		// model class
						sedml.getModelWithId(t.getModelReference()).getName() + "' : " + 
						sedml.getSimulation(t.getSimulationReference()).getClass().getSimpleName() + " '" +	// simulation class
						sedml.getSimulation(t.getSimulationReference()).getName() + "' ";
				tooltip = "The model has " + sedml.getModelWithId(t.getModelReference()).getListOfChanges().size() + " changes.";
			} else if(at instanceof RepeatedTask) {
				RepeatedTask rt = (RepeatedTask)at;
				switch(rt.getSubTasks().size()) {
				case 0:
					throw new RuntimeException("At least one subtask required within a repeated task: " + rt.getId());
				case 1:
					SubTask st = rt.getSubTasks().entrySet().iterator().next().getValue();		// first (and only) element
					String taskId = st.getTaskId();
					AbstractTask t = sedml.getTaskWithId(taskId);
					text = " Repeated task, id " + rt.getId() + " - " + 
							sedml.getModelWithId(t.getModelReference()).getClass().getSimpleName() + " '" +		// model class
							sedml.getModelWithId(t.getModelReference()).getName() + "' : " + 
							sedml.getSimulation(t.getSimulationReference()).getClass().getSimpleName() + " '" +	// simulation class
							sedml.getSimulation(t.getSimulationReference()).getName() + "' ";
					tooltip = "The repeated task has " + rt.getChanges().size() + " changes and " + rt.getRanges().size() + " ranges.";
					break;
				default:
					throw new RuntimeException("Multiple subtasks within a repeated task not supported at this time: " + rt.getId());
				}
				
			}
			
			JRadioButton rb = new JRadioButton(text);
			rb.setToolTipText(tooltip);
			SEDMLRadioButtonModel bm = new SEDMLRadioButtonModel(at);
			rb.setModel(bm);
			
			if(gridy == 0) {
				rb.setSelected(true);
			}
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(2, 4, 2, 4);
	        group.add(rb);
	        add(rb, gbc);
			gridy++;
		}
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		add(new JLabel(""), gbc);
	}
	
	public static AbstractTask chooseTask(SedML sedml, Component requester, String name) {
		
		SEDMLChooserPanel panel = new SEDMLChooserPanel(sedml);
		int oKCancel = DialogUtils.showComponentOKCancelDialog(requester, panel, "Import Sed-ML file: " + name);
		if (oKCancel == JOptionPane.CANCEL_OPTION || oKCancel == JOptionPane.DEFAULT_OPTION) {
			throw new UserCancelException("Canceling Import");
		}
		SEDMLRadioButtonModel bm = (SEDMLRadioButtonModel) panel.group.getSelection();
		AbstractTask tt = bm.getTask();
		return tt;
	}
	
}
