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

import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataObject;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.Workflow;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.SimpleContainerShape;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 9:11:57 AM)
 * @author: Jim Schaff
 */
public class WorkflowGraphModel extends GraphModel {
	private Workflow fieldWorkflow = new Workflow("unnamedWorkflow");

	/**
	 * SimpleGraphModel constructor comment.
	 */
	public WorkflowGraphModel() {
		super();
	}
	/**
	 * Gets the graph property (cbit.vcell.mapping.potential.Graph) value.
	 * @return The graph property value.
	 * @see #setGraph
	 */
	public Workflow getWorkflow() {
		return fieldWorkflow;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (7/8/2003 9:11:57 AM)
	 */
	@Override
	public void refreshAll() {
		clearAllShapes();
		if (getWorkflow() == null){
			fireGraphChanged(new GraphEvent(this));
			return;
		}
		ContainerShape containerShape = new SimpleContainerShape(new Object(),this,"workflow-container");
		addShape(containerShape);
		//
		// add workflow shape and workflow parameters
		//
//		WorkflowShape workflowShape = new WorkflowShape(getWorkflow(),this);
//		containerShape.addChildShape(workflowShape);
//		addShape(workflowShape);
		
//		for (DataHolder<? extends Object> parameter : getWorkflow().getParameters()){
//			DataHolderShape dataHolderShape = new DataHolderShape(parameter,this);
//			containerShape.addChildShape(dataHolderShape);
//			addShape(dataHolderShape);
//			
//			WorkflowEdgeShape workflowEdgeShape = new WorkflowEdgeShape("parameter.getName(), workflowShape, dataHolderShape, this, true, false);
//			containerShape.addChildShape(workflowEdgeShape);
//			addShape(workflowEdgeShape);
//		}
		//
		// add a TaskShape for each Task
		//
		for (Task task : getWorkflow().getTasks()){
			TaskShape taskShape = new TaskShape(task,this);
			containerShape.addChildShape(taskShape);
			addShape(taskShape);
			//
			// add a DataHolderShape for each output and connect to TaskShape with an edge
			//
			for (DataOutput<? extends Object> output : task.getOutputs()){
				if (!output.getName().equals("displayed")){
					DataHolderShape dataHolderShape = new DataHolderShape(output,this);
					containerShape.addChildShape(dataHolderShape);
					addShape(dataHolderShape);
					
					WorkflowEdgeShape workflowEdgeShape = new WorkflowEdgeShape(task.getName()+":"+output.getName(),taskShape, dataHolderShape, this, true, false);
					containerShape.addChildShape(workflowEdgeShape);
					addShape(workflowEdgeShape);
				}
			}
//			//
//			// add a DataInputShape for each input and connect to TaskShape with an edge
//			//
//			for (DataInput<? extends Object> input : task.getInputs()){
//				DataInputShape dataInputShape = new DataInputShape(input,this);
//				containerShape.addChildShape(dataInputShape);
//				addShape(dataInputShape);
//				
//				WorkflowEdgeShape workflowEdgeShape = new WorkflowEdgeShape("input:"+task.getName()+":"+input.name,dataInputShape, taskShape, this, true, false);
//				containerShape.addChildShape(workflowEdgeShape);
//				addShape(workflowEdgeShape);
//			}
		}
		//
		// for each TaskShape input, connect the appropriate data output
		//
		for (Task task : getWorkflow().getTasks()){
			for (DataInput<? extends Object> input : task.getInputs()){
				DataObject<? extends Object> source = getWorkflow().getConnectorSource(input);
				if (source instanceof DataOutput){
					DataHolderShape dataHolderShape = (DataHolderShape) getShapeFromModelObject(source);
					TaskShape taskShape = (TaskShape) getShapeFromModelObject(task);
					WorkflowEdgeShape workflowEdgeShape = new WorkflowEdgeShape("connection:"+input.getName(),dataHolderShape, taskShape, this, true, false);
					containerShape.addChildShape(workflowEdgeShape);
					addShape(workflowEdgeShape);
				}
			}
		}
		fireGraphChanged(new GraphEvent(this));
	}

	/**
	 * Sets the graph property (cbit.vcell.mapping.potential.Graph) value.
	 * @param graph The new value for the property.
	 * @see #getGraph
	 */
	public void setWorkflow(Workflow workflow) {
		Workflow oldValue = fieldWorkflow;
		fieldWorkflow = workflow;
		firePropertyChange("workflow", oldValue, workflow);
		refreshAll();
	}

}
