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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.vcell.util.gui.EditorScrollTable;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.gui.AbstractWorkflowObjectsTableModel;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataObject;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Repository;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;
import org.vcell.workflow.Workflow;
import org.vcell.workflow.WorkflowDataSource;
import org.vcell.workflow.WorkflowObject;
import org.vcell.workflow.WorkflowParameter;

import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class WorkflowObjectsTableModel extends AbstractWorkflowObjectsTableModel<WorkflowObject> implements java.beans.PropertyChangeListener {
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_TYPE = 1;
	public static final int COLUMN_VALUE = 2;
	public static final int COLUMN_STATUS = 3;
	private static String LABELS[] = {"name", "type", "value", "Status"};
	private boolean bTasks = true;
	private boolean bTaskInputs = true;
	private boolean bTaskOutputs = true;
	private boolean bParameters = true;
	private TaskContext taskContext = null;
	
/**
 * ReactionSpecsTableModel constructor comment.
 */
public WorkflowObjectsTableModel(EditorScrollTable table) {
	super(table);
	setColumns(LABELS);
}

/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class<?> getColumnClass(int col) {
	switch (col){
		case COLUMN_VALUE:
			return String.class;
		case COLUMN_TYPE:
			return String.class;
		case COLUMN_NAME:
			return String.class;
		case COLUMN_STATUS:
			return String.class;
	}
	return Object.class;
}

/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
protected List<WorkflowObject> computeData() {
	ArrayList<WorkflowObject> allWorkflowObjectList = new ArrayList<WorkflowObject>();
	if (taskContext == null){
		return null;
	}
	Workflow workflow = taskContext.getWorkflow();
	if (bTasks) {
		allWorkflowObjectList.addAll(workflow.getTasks());
	}
	if (bParameters) {
		allWorkflowObjectList.addAll(workflow.getParameters());
	}
	if (bTaskInputs) {
		for (Task task : workflow.getTasks()){
			allWorkflowObjectList.addAll(task.getInputs());
		}
	}
	if (bTaskOutputs) {
		for (Task task : workflow.getTasks()){
			allWorkflowObjectList.addAll(task.getOutputs());
		}
	}
	boolean bSearchInactive = searchText == null || searchText.length() == 0;
	String lowerCaseSearchText = bSearchInactive ? null : searchText.toLowerCase();
	ArrayList<WorkflowObject> workflowObjectList = new ArrayList<WorkflowObject>();
	for (WorkflowObject workflowObject : allWorkflowObjectList) {
		if (bSearchInactive
			|| getValue(workflowObject).toLowerCase().contains(lowerCaseSearchText)
			|| getName(workflowObject).toLowerCase().contains(lowerCaseSearchText)
			|| getType(workflowObject).toLowerCase().contains(lowerCaseSearchText)
			|| getStatus(taskContext,workflowObject).toLowerCase().contains(lowerCaseSearchText)) {
			workflowObjectList.add(workflowObject);
		}
	}
	return workflowObjectList;
}

private String getType(WorkflowObject workflowObject){
	if (workflowObject instanceof Task){
		return "Task "+workflowObject.getClass().getSimpleName();
	}else if (workflowObject instanceof DataObject){
		DataObject dataHolder = (DataObject)workflowObject;
		return dataHolder.getType().getSimpleName();
	}
	return workflowObject.getClass().toString();
}

String getName(WorkflowObject workflowObject){
	return workflowObject.getPath();
}

public static <T> String getStatus(TaskContext taskContext, WorkflowObject workflowObject){
	if (workflowObject instanceof WorkflowDataSource){
		WorkflowDataSource dataHolder = (WorkflowDataSource)workflowObject;
		if (taskContext.getRepository().isDirty(taskContext.getWorkflow(), dataHolder)){
			return "needs run";
		}else{
			return "done";
		}
	}else if (workflowObject instanceof DataInput){
		DataInput dataInput = (DataInput)workflowObject;
		if (taskContext.getWorkflow().getConnectorSource(dataInput) == null){
			if (dataInput.isOptional()){
				return "disconnected - optional";
			}else{
				return "disconnected";
			}
		}else if (taskContext.getData(dataInput) == null){
			return "needs data";
		}else{
			return "ready";
		}
	}else if (workflowObject instanceof Task){
		Task task = (Task)workflowObject;
		if (task.isRunning()){
			return "running";
		}else if (taskContext.areOutputsDirty(task)){
			return "outputs dirty";
		}else{
			return "done";
		}
	}else{
		return "unexpected workflowObject, "+workflowObject;
	}
}

private String getValue(WorkflowObject workflowObject){
	if (workflowObject instanceof Task){
		return "";
	}else{
		Object data = null;
		Class dataType = null;
		if (workflowObject instanceof WorkflowDataSource){
			WorkflowDataSource dataHolder = (WorkflowDataSource)workflowObject;
			WorkflowDataSource dataSource = taskContext.getWorkflow().getDataSource((DataObject)dataHolder);
			data = taskContext.getRepository().getData(dataSource);
			dataType = dataHolder.getType();
		}else if (workflowObject instanceof DataInput){
			DataInput dataInput = (DataInput)workflowObject;
			data = taskContext.getData(dataInput);
			dataType = dataInput.getType();
		}
		if (data instanceof RowColumnResultSet){
			RowColumnResultSet rc = (RowColumnResultSet)data;
			int N = rc.getColumnDescriptionsCount();
			StringBuffer buffer = new StringBuffer(rc.getRowCount()+" rows of "+N+" {");
			int MAX = 3;
			for (int i=0;i<N;i++){
				buffer.append("\""+rc.getColumnDescriptions(i).getDisplayName()+"\"");
				if (i>=MAX-1){
					buffer.append(", ...");
					break;
				}
				if (i<N-1){
					buffer.append(", ");
				}
			}
			buffer.append("}");
			return buffer.toString();
		}else if (data instanceof String){
			return "\""+(String)data+"\"";
		}else if (data instanceof ROI){
			return "ROI \""+((ROI)data).getROIName()+"\"";
		}else if (data instanceof ROI[]){
			ROI[] rois = (ROI[])data;
			int N = rois.length;
			int MAX = 3;
			StringBuffer buffer = new StringBuffer("ROI["+N+"] { ");
			for (int i=0;i<N;i++){
				buffer.append("\""+rois[i].getROIName()+"\"");
				if (i>=MAX-1){
					buffer.append(", ...");
					break;
				}
				if (i<N-1){
					buffer.append(", ");
				}
			}
			buffer.append("}");
			return buffer.toString();
		}else if (data instanceof Image){
			Image image = (Image)data;
			return image.getClass().getSimpleName()+" "+image.getISize().toString();
		}else if (data instanceof ImageTimeSeries){
			ImageTimeSeries ts = (ImageTimeSeries)data;
			int N = ts.getSizeT();
			double[] times = ts.getImageTimeStamps();
			return ts.getType().getSimpleName()+"["+N+"] "+ts.getISize()+" times=["+times[0]+","+times[N-1]+"]";
		}else if (data != null){
			return data.toString();
		}else{
			return "null "+dataType.getSimpleName();
		}
	}
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		WorkflowObject workflowObject = getValueAt(row);
		switch (col){
			case COLUMN_VALUE:
				return getValue(workflowObject);
			case COLUMN_TYPE:
				return getType(workflowObject);
			case COLUMN_NAME:
				return getName(workflowObject);
			case COLUMN_STATUS:{					
				return getStatus(taskContext,workflowObject);				
			}
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
	return null;
}

public boolean isCellEditable(int row, int col) {
	if (col == COLUMN_VALUE){
		WorkflowObject workflowObject = getValueAt(row);
		if (workflowObject instanceof WorkflowParameter){
			WorkflowParameter dataHolder = (WorkflowParameter)workflowObject;
			if (dataHolder.getType().equals(Double.class)
				|| dataHolder.getType().equals(Float.class)
				|| dataHolder.getType().equals(Integer.class)
				|| Boolean.class.isAssignableFrom(dataHolder.getType())
				|| String.class.isAssignableFrom(dataHolder.getType())){
				return true;
			}
		}
	}
	return false;
}

@Override
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	super.propertyChange(evt);	
	if (evt.getSource() instanceof WorkflowObject) {
		int changeRow = getRowIndex((WorkflowObject) evt.getSource());
		if (changeRow >= 0) {
			fireTableRowsUpdated(changeRow, changeRow);
		}
	} else {
		String propertyName = evt.getPropertyName();
		if (evt.getSource() == taskContext.getWorkflow()) {
			if (propertyName.equals(Workflow.PROPERTY_NAME_PARAMETERS)) {
				WorkflowParameter[] oldValue = (WorkflowParameter[])evt.getOldValue();
				if (oldValue!=null){
					for (WorkflowParameter parameter : oldValue) {
						parameter.removePropertyChangeListener(this);
					}
				}
				WorkflowParameter[] newValue = (WorkflowParameter[])evt.getNewValue();
				if (newValue!=null){
					for (WorkflowParameter parameter : newValue) {
						parameter.addPropertyChangeListener(this);
					}
				}
				refreshData();
			} else if (propertyName.equals(Workflow.PROPERTY_NAME_TASKS)) {
				Task[] oldValue = (Task[])evt.getOldValue();
				if (oldValue!=null){
					for (Task task : oldValue) {
						task.removePropertyChangeListener(this);
						for (DataInput dataInput : task.getInputs()){
							dataInput.removePropertyChangeListener(this);
						}
						for (DataOutput dataOutput : task.getOutputs()){
							dataOutput.removePropertyChangeListener(this);
						}
					}
				}
				Task[] newValue = (Task[])evt.getNewValue();
				if (newValue!=null){
					for (Task task : newValue) {
						task.addPropertyChangeListener(this);
						for (DataInput dataInput : task.getInputs()){
							dataInput.addPropertyChangeListener(this);
						}
						for (DataOutput dataOutput : task.getOutputs()){
							dataOutput.addPropertyChangeListener(this);
						}
					}
				}
				refreshData();
			}
		}
	}
}

public void setValueAt(Object value, int row, int col) {
	if (value == null) {
		return;
	}
	try {
		String inputValue = (String)value;
		inputValue = inputValue.trim();
		WorkflowObject workflowObject = getValueAt(row);
		switch (col){
			case COLUMN_VALUE:{
				if (inputValue.length() == 0) {
					return;
				}
				if (workflowObject instanceof WorkflowParameter){
					WorkflowParameter dataHolder = (WorkflowParameter)workflowObject;
					Class dataClass = dataHolder.getType();
					Repository repository = taskContext.getRepository();
					Workflow workflow = taskContext.getWorkflow();
					if (dataClass.equals(Double.class)){
						repository.setData(dataHolder, Double.valueOf(inputValue));
						workflow.refreshStatus();
					}else if (dataClass.equals(Float.class)){
						repository.setData(dataHolder, Float.valueOf(inputValue));
						workflow.refreshStatus();
					}else if (dataClass.equals(Integer.class)){
						repository.setData(dataHolder,Integer.valueOf(inputValue));
						workflow.refreshStatus();
					}else if (dataClass.equals(Boolean.class)){
						repository.setData(dataHolder,Boolean.valueOf(inputValue));
						workflow.refreshStatus();
					}else if (dataClass.equals(String.class)){
						repository.setData(dataHolder,inputValue);
						workflow.refreshStatus();
					}else{
						System.out.println("type "+dataClass.getSimpleName()+" not supported");
					}
				}
				break;
			}
		}
	} catch (Exception e){
		e.printStackTrace(System.out);
	}
}


  public Comparator<WorkflowObject> getComparator(final int col, final boolean ascending) {
	  return new Comparator<WorkflowObject>() {

		public int compare(WorkflowObject parm1, WorkflowObject parm2) {
			int scale = ascending ? 1 : -1;
			switch (col){
				case COLUMN_VALUE:
					return scale * getValue(parm1).compareToIgnoreCase(getValue(parm2));
				case COLUMN_NAME:
					return scale * getName(parm1).compareToIgnoreCase(getName(parm2));
				case COLUMN_TYPE:
					return scale * getType(parm1).compareToIgnoreCase(getType(parm2));
				case COLUMN_STATUS:
					return scale * getStatus(taskContext,parm1).compareToIgnoreCase(getStatus(taskContext,parm2));
			}
			return 0;
		}
	  };
  }


public String checkInputValue(String inputValue, int row, int column) {
	return null;
}

public SymbolTable getSymbolTable(int row, int column) {
	return null;
}

public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column) {
	return null;
}

public Set<String> getAutoCompletionWords(int row, int column) {
	return null;
}

public final void setIncludeParameters(boolean newValue) {
	if (newValue == bParameters) {
		return;
	}	
	this.bParameters = newValue;
	refreshData();
}

public final void setIncludeTasks(boolean newValue) {
	if (newValue == bTasks) {
		return;
	}	
	this.bTasks = newValue;
	refreshData();
}

public final void setIncludeTaskInputs(boolean newValue) {
	if (newValue == bTaskInputs) {
		return;
	}	
	this.bTaskInputs = newValue;
	refreshData();
}

public final void setIncludeTaskOutputs(boolean newValue) {
	if (newValue == bTaskOutputs) {
		return;
	}	
	this.bTaskOutputs = newValue;
	refreshData();
}

public void setTaskContext(TaskContext taskContext) {
	if (taskContext!=null){
		Workflow workflow = taskContext.getWorkflow();
		workflow.removePropertyChangeListener(this);
		for (WorkflowParameter<? extends Object> dataHolder : workflow.getParameters()){
			dataHolder.removePropertyChangeListener(this);
		}
		for (Task task : workflow.getTasks()){
			task.removePropertyChangeListener(this);
		}
	}
	this.taskContext = taskContext;
	if (this.taskContext!=null){
		Workflow workflow = this.taskContext.getWorkflow();
		workflow.addPropertyChangeListener(this);
		for (WorkflowParameter<? extends Object> dataHolder : workflow.getParameters()){
			dataHolder.addPropertyChangeListener(this);
		}
		for (Task task : workflow.getTasks()){
			task.addPropertyChangeListener(this);
		}
	}
	refreshData();
}

}
