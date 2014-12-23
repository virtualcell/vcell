package org.vcell.workflow;


public abstract class WorkflowTask extends Task {
	
	public WorkflowTask(String name) {
		super(name);
	}

	public abstract void addWorkflowComponents(Workflow workflow);
}
