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
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

public class SEDMLChooserPanel extends JPanel {
	
	private List<AbstractTask> taskList;
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
	
	public SEDMLChooserPanel(List<AbstractTask> taskList) {
		super();
		this.taskList = taskList;
		initialize();
	}
	
	private void initialize() {

		setLayout(new GridBagLayout());
		int gridy = 0;
		//structureList.clear();
		for(AbstractTask tt : taskList) {
			JRadioButton rb = new JRadioButton(tt.getModelReference() + ": " + tt.getSimulationReference());
			SEDMLRadioButtonModel bm = new SEDMLRadioButtonModel(tt);
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
	
	public static AbstractTask chooseTask(List<AbstractTask> taskList, Component requester, String name) {
		
		SEDMLChooserPanel panel = new SEDMLChooserPanel(taskList);
		int oKCancel = DialogUtils.showComponentOKCancelDialog(requester, panel, "Import Sed-ML file: " + name);
		if (oKCancel == JOptionPane.CANCEL_OPTION || oKCancel == JOptionPane.DEFAULT_OPTION) {
			throw new UserCancelException("Canceling Import");
		}
		SEDMLRadioButtonModel bm = (SEDMLRadioButtonModel) panel.group.getSelection();
		AbstractTask tt = bm.getTask();
		return tt;
	}
	
}
