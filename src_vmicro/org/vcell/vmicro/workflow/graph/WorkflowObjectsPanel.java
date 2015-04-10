/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vmicro.workflow.graph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.optimization.ProfileData;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.vmicro.op.display.DisplayImageOp;
import org.vcell.vmicro.op.display.DisplayPlotOp;
import org.vcell.vmicro.op.display.DisplayProfileLikelihoodPlotsOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.task.DisplayTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.MemoryRepository;
import org.vcell.workflow.Repository;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;
import org.vcell.workflow.WorkflowDataSource;
import org.vcell.workflow.WorkflowObject;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;

@SuppressWarnings("serial")
public class WorkflowObjectsPanel extends JPanel {
	private JCheckBox tasksCheckBox = null;
	private JCheckBox parametersCheckBox = null;
	private JCheckBox taskInputsCheckBox = null;
	private JCheckBox taskOutputsCheckBox = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();

	private JButton runButton = null;
	private BioModelWindowManager bioModelWindowManager = null;
	
	private JButton showButton = null;
	private JButton deleteButton = null;
	private EditorScrollTable parametersFunctionsTable;
	private WorkflowObjectsTableModel parametersFunctionsTableModel = null;
	private TaskContext taskContext;
	private JTextField textFieldSearch = null;
	private JPanel parametersFunctionsPanel = null;
		
	private class InternalEventHandler implements ActionListener, ListSelectionListener, DocumentListener {		
				
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == showButton) {
				showButtonPressed();
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			} else if (e.getSource() == runButton) {
				try {
					runButtonPressed();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (e.getSource() == tasksCheckBox) {
				parametersFunctionsTableModel.setIncludeTasks(tasksCheckBox.isSelected());
			} else if (e.getSource() == parametersCheckBox) {
				parametersFunctionsTableModel.setIncludeParameters(parametersCheckBox.isSelected());
			} else if (e.getSource() == taskInputsCheckBox) {
				parametersFunctionsTableModel.setIncludeTaskInputs(taskInputsCheckBox.isSelected());
			} else if (e.getSource() == taskOutputsCheckBox) {
				parametersFunctionsTableModel.setIncludeTaskOutputs(taskOutputsCheckBox.isSelected());
			}
		}
		
		public void valueChanged(ListSelectionEvent e) {
			if (taskContext == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == parametersFunctionsTable.getSelectionModel()) {
				tableSelectionChanged();
			}
		}
		
		public void insertUpdate(DocumentEvent e) {
			searchTable();
		}

		public void removeUpdate(DocumentEvent e) {
			searchTable();
		}

		public void changedUpdate(DocumentEvent e) {
			searchTable();
		}
	}	
	
	public WorkflowObjectsPanel() {
		super();
		initialize();
	}

	private void showButtonPressed() {
		int[] rows = parametersFunctionsTable.getSelectedRows();
		if (rows == null || rows.length==0) {
			return;
		}
		for (int r : rows) {
			if (r < parametersFunctionsTableModel.getRowCount()) {
				WorkflowObject workflowObject = parametersFunctionsTableModel.getValueAt(r);
				displayData(taskContext,workflowObject);
			}
		}
	}

	private void deleteButtonPressed() {
		int[] rows = parametersFunctionsTable.getSelectedRows();
		if (rows == null || rows.length==0) {
			return;
		}
		for (int r : rows) {
			if (r < parametersFunctionsTableModel.getRowCount()) {
				WorkflowObject workflowObject = parametersFunctionsTableModel.getValueAt(r);
				if (workflowObject instanceof Task){
					taskContext.getWorkflow().removeTask((Task)workflowObject);
				}
			}
		}
	}

	private void runButtonPressed() throws Exception {
		if (taskContext!=null){
			LocalWorkspace localWorkspace = new LocalWorkspace(new File("C:\\temp\\"));
			Repository repository = new MemoryRepository();
			taskContext.getWorkflow().compute(taskContext, new ClientTaskStatusSupport(){

				@Override
				public void setMessage(String message) {
					System.out.println("message = "+message);
				}

				@Override
				public void setProgress(int progress) {
					System.out.println("progress = "+progress);
				}

				@Override
				public int getProgress() {
					return 0;
				}

				@Override
				public boolean isInterrupted() {
					return false;
				}

				@Override
				public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
				}
				
			});
		}
	}


	private void initialize(){
		showButton = new JButton("show data");
		showButton.addActionListener(eventHandler);
		deleteButton = new JButton("Delete Task(s)");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(eventHandler);
		runButton = new JButton("Run");
		runButton.addActionListener(eventHandler);

		textFieldSearch = new JTextField(10);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		textFieldSearch.putClientProperty("JTextField.variant", "search");
		parametersFunctionsTable = new EditorScrollTable();
		parametersFunctionsTableModel = new WorkflowObjectsTableModel(parametersFunctionsTable);
		parametersFunctionsTable.setModel(parametersFunctionsTableModel);
			
		tasksCheckBox = new JCheckBox("Tasks");
		tasksCheckBox.setSelected(true);
		tasksCheckBox.addActionListener(eventHandler);
		parametersCheckBox = new JCheckBox("parameters");
		parametersCheckBox.setSelected(true);
		parametersCheckBox.addActionListener(eventHandler);
		taskInputsCheckBox = new JCheckBox("Task inputs");
		taskInputsCheckBox.setSelected(true);
		taskInputsCheckBox.addActionListener(eventHandler);
		taskOutputsCheckBox = new JCheckBox("Task outputs");
		taskOutputsCheckBox.setSelected(true);
		taskOutputsCheckBox.addActionListener(eventHandler);
						
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints  gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(showButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(deleteButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(runButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,50,4,4);
		buttonPanel.add(new JLabel("Search"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		buttonPanel.add(textFieldSearch, gbc);
		
		setLayout(new BorderLayout());
		add(getParametersFunctionsPanel(), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		parametersFunctionsTable.getSelectionModel().addListSelectionListener(eventHandler);
		parametersFunctionsTable.setDefaultRenderer(NameScope.class, new DefaultScrollTableCellRenderer(){

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value instanceof NameScope) {
					NameScope nameScope = (NameScope)value;
					setText(nameScope.getPathDescription());
				}
				return this;
			}
			
		});
	}
	
	private JPanel getParametersFunctionsPanel() {
		if (parametersFunctionsPanel == null) {
			parametersFunctionsPanel = new JPanel();
			parametersFunctionsPanel.setLayout(new GridBagLayout());
			
			int gridy = 0;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			JLabel label = new JLabel("Defined In:");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			parametersFunctionsPanel.add(label, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(tasksCheckBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(parametersCheckBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(taskInputsCheckBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 4;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(taskOutputsCheckBox, gbc);
			
			gridy ++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridwidth = 5;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(parametersFunctionsTable.getEnclosingScrollPane(), gbc);
		}
		return parametersFunctionsPanel;
	}
	
	protected void tableSelectionChanged() {
		int[] rows = parametersFunctionsTable.getSelectedRows();
		if (rows == null || rows.length==0) {
			showButton.setEnabled(false);
			deleteButton.setEnabled(false);
			return;
		}
		boolean bAllTasks = true;
		boolean bAllDisplable = true;
		for (int r : rows) {
			if (r < parametersFunctionsTableModel.getRowCount()) {
				WorkflowObject workflowObject = parametersFunctionsTableModel.getValueAt(r);
				if (!(workflowObject instanceof Task)) {
					bAllTasks = false;
				}
				if (!hasDisplayData(taskContext, workflowObject)){
					bAllDisplable = false;
				}
			}
		}
		deleteButton.setEnabled(bAllTasks);
		showButton.setEnabled(bAllDisplable);
	}
	
	private void displayData(TaskContext taskContext, WorkflowObject workflowObject){
		if (workflowObject instanceof DataInput || workflowObject instanceof DataOutput){
			String title = parametersFunctionsTableModel.getName(workflowObject);
			WindowListener listener = new WindowAdapter(){};
			Object data = null;
			if (workflowObject instanceof WorkflowDataSource){
				WorkflowDataSource dataHolder = (WorkflowDataSource)workflowObject;
				data = taskContext.getRepository().getData(dataHolder);
			}else if (workflowObject instanceof DataInput){
				DataInput dataInput = (DataInput)workflowObject;
				data = taskContext.getData(dataInput);
			}
			if (data instanceof RowColumnResultSet){
				RowColumnResultSet rc = (RowColumnResultSet)data;
				try {
					new DisplayPlotOp().displayPlot(rc, title, listener);
				} catch (ExpressionException e) {
					e.printStackTrace();
				}
			}else if (data instanceof ROI){
				ROI roi = (ROI)data;
				Image image = roi.getRoiImages()[0];
				new DisplayImageOp().displayImage(image, title, listener);
			}else if (data instanceof ProfileData[]){
				ProfileData[] profileData = (ProfileData[])data;
				new DisplayProfileLikelihoodPlotsOp().displayProfileLikelihoodPlots(profileData, title, listener);
			}else if (data instanceof Image){
				Image image = (Image)data;
				new DisplayImageOp().displayImage(image, title, listener);
			}else if (data instanceof ImageTimeSeries){
				ImageTimeSeries imageTimeSeries = (ImageTimeSeries)data;
				try {
					DisplayTimeSeries.displayImageTimeSeries(imageTimeSeries, title, listener);
				} catch (ImageException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	private boolean hasDisplayData(TaskContext taskContext, WorkflowObject workflowObject){
		if (workflowObject instanceof DataInput || workflowObject instanceof DataOutput){
			String title = parametersFunctionsTableModel.getName(workflowObject);
			WindowListener listener = new WindowAdapter(){};
			Object data = null;
			if (workflowObject instanceof WorkflowDataSource){
				WorkflowDataSource dataHolder = (WorkflowDataSource)workflowObject;
				data = taskContext.getRepository().getData(dataHolder);
			}else if (workflowObject instanceof DataInput){
				DataInput dataInput = (DataInput)workflowObject;
				data = taskContext.getData(dataInput);
			}
			if (data instanceof RowColumnResultSet
				|| data instanceof ROI
				|| data instanceof ProfileData[]
				|| data instanceof Image
				|| data instanceof ImageTimeSeries){
				return true;
			}
		}
		return false;
	}

	
	private void searchTable() {		
		String text = textFieldSearch.getText();
		parametersFunctionsTableModel.setSearchText(text);
	}
	
	public void setTaskContext(TaskContext newValue) {
		if (newValue == taskContext) {
			return;
		}
		taskContext = newValue;		
		parametersFunctionsTableModel.setTaskContext(newValue);
	}
	
}
