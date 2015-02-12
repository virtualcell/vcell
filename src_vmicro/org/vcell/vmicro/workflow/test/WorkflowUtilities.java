package org.vcell.vmicro.workflow.test;

import javax.swing.JFrame;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.vmicro.workflow.graph.WorkflowModelPanel;
import org.vcell.vmicro.workflow.graph.WorkflowObjectsPanel;
import org.vcell.vmicro.workflow.jgraphx.WorkflowEditorPanel;
import org.vcell.vmicro.workflow.jgraphx.WorkflowJGraphProxy;
import org.vcell.workflow.TaskContext;
import org.vcell.workflow.Workflow;

public class WorkflowUtilities {
	
	public static class Progress implements ClientTaskStatusSupport {
		
		@Override
		public void setProgress(int progress) {
			System.out.println("status: progress : "+progress);
		}
		
		@Override
		public void setMessage(String message) {
			System.out.println("status: message : "+message);
		}
		
		@Override
		public boolean isInterrupted() {
			return false;
		}
		
		@Override
		public int getProgress() {
			return 0;
		}
		
		@Override
		public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
		}
	};
	
	public static void displayWorkflowGraph(Workflow workflow){
		JFrame frame = new javax.swing.JFrame();
		WorkflowModelPanel aWorkflowModelPanel;
		aWorkflowModelPanel = new WorkflowModelPanel();
		frame.setContentPane(aWorkflowModelPanel);
		frame.setSize(aWorkflowModelPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		
		aWorkflowModelPanel.setWorkflow(workflow);
		frame.setTitle("workflow graph - old");
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	}
	
	
	public static void displayWorkflowTable(TaskContext taskContext){
		JFrame frame = new javax.swing.JFrame();
		WorkflowObjectsPanel aWorkflowObjectsPanel;
		aWorkflowObjectsPanel = new WorkflowObjectsPanel();
		frame.setContentPane(aWorkflowObjectsPanel);
		frame.setSize(aWorkflowObjectsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		
		frame.setLocation(0,1000);
		frame.setSize(1000,600);
		frame.setVisible(true);
		frame.setTitle("workflow table");

		aWorkflowObjectsPanel.setTaskContext(taskContext);
	}
	
	
	public static void displayWorkflowGraphJGraphX(WorkflowJGraphProxy workflowJGraphProxy){
		WorkflowEditorPanel aWorkflowEditorPanel;
		aWorkflowEditorPanel = new WorkflowEditorPanel();
		
		aWorkflowEditorPanel.setWorkflowGraphProxy(workflowJGraphProxy);
		JFrame frame = aWorkflowEditorPanel.createFrame(null);
		frame.setLocation(0, 700);
		frame.setSize(2300, 800);
		frame.setVisible(true);
	}

}
