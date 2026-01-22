package org.vcell.sedml.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton.ToggleButtonModel;

import org.jlibsedml.SedMLDataContainer;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.model.Model;
import org.jlibsedml.components.task.AbstractTask;
import org.jlibsedml.components.model.Change;
import org.jlibsedml.components.task.RepeatedTask;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.task.SubTask;
import org.jlibsedml.components.task.Task;
import org.vcell.sedml.SedMLUtil;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;


// we build the list of tasks (applications) we can import in vCell and
// ask the user to choose one task only (we only support importing of one task)
public class SEDMLChooserPanel extends JPanel {
	
	private SedMLDataContainer sedml;
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
	
	public SEDMLChooserPanel(SedMLDataContainer sedml) {
		super();
		this.sedml = sedml;
		initialize();
	}
	
	private void initialize() {
		
		Set<String> issues = new HashSet<> ();

		setLayout(new GridBagLayout());
		int gridy = 0;
		
		// build and present to the user all the tasks that we can import; we skip those that we can't import for some reason 
		// (incompatibility with vCell for example) and for them we create a list of problems which we show to the user 
		for(AbstractTask at : this.sedml.getSedML().getTasks()) {
			
			String text = "";
			String tooltip = "";
			boolean issueFound = false;
			
			if(at instanceof Task t) {
                SedBase maybeModel = this.sedml.getSedML().searchInModelsFor(t.getModelReference());
                SedBase maybeSim = this.sedml.getSedML().searchInSimulationsFor(t.getSimulationReference());
                String modelSimpleName = maybeModel == null ? "<unknown>" : maybeModel.getClass().getSimpleName();
                String simSimpleName = maybeSim == null ? "<unknown>" : maybeSim.getClass().getSimpleName();
                text = " Simple task '" + t.getId() + "' - " +
                        modelSimpleName + " '" +		// model class
						SedMLUtil.getName(maybeModel) + "' : " +
                        simSimpleName + " '" +	// simulation class
						SedMLUtil.getName(maybeSim) + "' ";
                if (maybeModel instanceof Model model){
                    tooltip = "The model has " + model.getListOfChanges().size() + " changes.";
                } else {
                    tooltip = "";
                }

			} else if(at instanceof RepeatedTask rt) {

                // TODO: we issue warning that importing repeated task is not implemented yet
				// but we have still can import it as simple task, so we don't set issueFound to true
				issues.add("Importing a RepeatedTask is not implemented yet, '" + SedMLUtil.getName(rt) + "' may be imported as SimpleTask.");
				
				// Verify that all the changes are supported (setValue only, for now) and if anything else is found
				// add an error message to the list of errors and skip the task
				for(Change c : rt.getChanges()) {
					if(!c.getChangeKind().equals(SedMLTags.SET_VALUE_KIND)) {
						issues.add("The '" + c.getChangeKind() + "' change kind is not supported.");
						issueFound = true;
					}
				}
				
				switch(rt.getSubTasks().size()) {
				case 0:
					issues.add("At least one subtask is required within a repeated task: " + rt.getId());
					issueFound = true;
				case 1:
					SubTask st = rt.getSubTasks().iterator().next();		// first (and only) element
					SId taskId = st.getTask();
                    SedBase maybeAbstractTask = this.sedml.getSedML().searchInModelsFor(taskId);
                    Task rtBaseTask;
					if (maybeAbstractTask instanceof AbstractTask abstractTask && null != (rtBaseTask = this.sedml.getBaseTask(abstractTask.getId()))){
                        SedBase maybeRTModel = this.sedml.getSedML().searchInModelsFor(rtBaseTask.getModelReference());
                        SedBase maybeRTSim =  this.sedml.getSedML().searchInSimulationsFor(rtBaseTask.getSimulationReference());
                        String rtModelSimpleName = maybeRTModel == null ? "<unknown>" : maybeRTModel.getClass().getSimpleName();
                        String rtSimSimpleName = maybeRTSim == null ? "<unknown>" : maybeRTSim.getClass().getSimpleName();
                        text = " Repeated task '" + rt.getId() + "' - " +
                                rtModelSimpleName + " '" +		// model class
                                SedMLUtil.getName(maybeRTModel) + "' : " +
                                rtSimSimpleName + " '" +	// simulation class
                                SedMLUtil.getName(maybeRTSim) + "' ";
                        tooltip = "The repeated task has " + rt.getChanges().size() + " changes and " + rt.getRanges().size() + " ranges.";
                    } else {
                        text = "Unknown task";
                        tooltip = "Unable to determine task type or details";
                    }


					break;
				default:
					issues.add("Multiple subtasks within a repeated task '" + rt.getId() + "' are not supported.");
					issueFound = true;
				}
			} else {
				issues.add("The task class '" + SedMLUtil.getName(at) + "' is not supported.");
				issueFound = true;
			}
			
			if(issueFound) {
				continue;		// we skip the tasks we don't know how to import in vCell
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
		
		// we display the issues (but no more than a certain number)
		final int MAX_ISSUES = 10;
		int issueIndex = 0;
		for(String issue : issues) {
			if(issueIndex >= MAX_ISSUES) {
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = gridy;
				gbc.anchor = GridBagConstraints.WEST;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.insets = new Insets(2, 4, 2, 4);
				add(new JLabel("<html><font color = \"#8B0000\">" + "...More" + "</font></html>"), gbc);
				gridy++;
				break;
			}
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(2, 4, 2, 4);
			add(new JLabel("<html><font color = \"#8B0000\">" + issue + "</font></html>"), gbc);
			gridy++;
			issueIndex++;
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
	
	public static AbstractTask chooseTask(SedMLDataContainer sedml, Component requester, String name) {
		
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
