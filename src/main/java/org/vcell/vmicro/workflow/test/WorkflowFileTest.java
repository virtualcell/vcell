package org.vcell.vmicro.workflow.test;

import java.io.File;
import java.util.ArrayList;

import org.vcell.util.BeanUtils;
import org.vcell.util.Issue;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.jgraphx.WorkflowJGraphProxy;
import org.vcell.workflow.MemoryRepository;
import org.vcell.workflow.Repository;
import org.vcell.workflow.TaskContext;
import org.vcell.workflow.Workflow;

public class WorkflowFileTest {
	
	public static void main(String[] args){
		if (args.length!=2){
			System.out.println("expecting 2 arguments");
			System.out.println("usage: java "+Workflow.class.getSimpleName()+" workingdir workflowInputFile");
			System.out.println("workingdir example: "+"D:\\developer\\eclipse\\workspace\\VCell_5.4_vmicro\\datadir");
			System.out.println("workflowInputFile example: "+"D:\\developer\\eclipse\\workspace\\VCell_5.4_vmicro\\workflow1.txt");
			System.exit(1);
		}
		try {
			File workingDirectory = new File(args[0]);
			LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);

			String workflowLanguageText = BeanUtils.readBytesFromFile(new File(args[1]), null);

			Repository repository = new MemoryRepository();
			Workflow workflow = Workflow.parse(repository, localWorkspace, workflowLanguageText);
			TaskContext taskContext = new TaskContext(workflow,repository,localWorkspace);

			
			
			WorkflowUtilities.displayWorkflowGraph(taskContext.getWorkflow());
			
			WorkflowUtilities.displayWorkflowTable(taskContext);
			
			WorkflowUtilities.displayWorkflowGraphJGraphX(new WorkflowJGraphProxy(taskContext));

			ArrayList<Issue> issues = new ArrayList<Issue>();
			taskContext.getWorkflow().reportIssues(issues, Issue.SEVERITY_INFO, true);

			
			//
			// execute the workflow
			//
			taskContext.getWorkflow().compute(taskContext, new WorkflowUtilities.Progress());
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}
