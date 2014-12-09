package org.vcell.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.vmicro.workflow.data.LocalWorkspace;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public abstract class Task extends WorkflowObject implements IssueSource {
	
	public class DiagramStyle implements Serializable {
		Integer posX;
		Integer posY;
		
		public int getX(int defaultPosX){
			if (posX!=null){
				return posX;
			}else{
				return defaultPosX;
			}
		}
		public int getY(int defaultPosY){
			if (posY!=null){
				return posY;
			}else{
				return defaultPosY;
			}
		}
		
		private DiagramStyle(){
			
		}
		
		private DiagramStyle(String styleText) {
			Gson gson = new Gson();
			try {
				DiagramStyle diagramStyle = gson.fromJson(styleText, DiagramStyle.class);
				this.posX = diagramStyle.posX;
				this.posY = diagramStyle.posY;
			}catch (JsonParseException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("failed to parse DiagramStyle '"+styleText+"', : "+e.getMessage());
			}
		}
	}

	public static final String STYLE_ATTRIBUTE_NAME = "style";
	private DiagramStyle style = new DiagramStyle();
	private boolean bRunning = false;

	protected LocalWorkspace localWorkspace = null;
	private ArrayList<DataInput<? extends Object>> inputs = new ArrayList<DataInput<? extends Object>>();
	private ArrayList<DataHolder<? extends Object>> outputs = new ArrayList<DataHolder<? extends Object>>();
	
	public Task(String name){
		super(name);
	}
	
	@Override
	public String getPath(){
		return "Task( \""+getName()+"\" )";
	}
	
	public void setLocalWorkspace(LocalWorkspace localWorkspace){
		this.localWorkspace = localWorkspace;
	}
	
	protected LocalWorkspace getLocalWorkspace(){
		return localWorkspace;
	}
	
	public final List<DataInput<? extends Object>> getInputs(){
		return Collections.unmodifiableList(inputs);
	}
	
	public final List<DataHolder<? extends Object>> getOutputs(){
		return Collections.unmodifiableList(outputs);
	}
	
	protected final void addInput(DataInput<? extends Object> input) {
		if (!inputs.contains(input)){
			inputs.add(input);
		}
	}

	protected final void addOutput(DataHolder<? extends Object> output) {
		if (!outputs.contains(output)){
			outputs.add(output);
		}
	}

	public final void compute(ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		try {
			bRunning = true;
			updateStatus();
			validateInputs();
			compute0(clientTaskStatusSupport);
		}finally{
			bRunning = false;
			updateStatus();
		}
	}
	
	protected void updateStatus(){
		boolean bOutputsDirty = false;
		for (DataHolder<? extends Object> output : getOutputs()){
			if (output.isDirty()){
				bOutputsDirty = true;
			}
		}
		IssueContext issueContext = new IssueContext();
		ArrayList<Issue> issues = new ArrayList<Issue>();
		gatherIssues(issueContext, issues);
		setStatus(new Status(bOutputsDirty,bRunning,issues.toArray(new Issue[0])));
	}

	protected abstract void compute0(ClientTaskStatusSupport clientTaskStatusSupport) throws Exception;

	public void gatherIssues(IssueContext issueContext, ArrayList<Issue> issues){
		for (DataInput<? extends Object> input : inputs){
			if (input.getSource()==null){
				if (!input.isOptional()){
//					issues.add(new Issue(this, issueContext, IssueCategory.Workflow_missingInput,"task \""+getName()+"\" missing required connection for input \""+input.getName()+"\"", Issue.SEVERITY_ERROR));
				}else{
//					issues.add(new Issue(this, issueContext, IssueCategory.Workflow_missingInput,"task \""+getName()+"\" missing optional connection for input \""+input.getName()+"\"", Issue.SEVERITY_INFO));
				}
			}else{
				if (input.getData()==null){
//					issues.add(new Issue(this, issueContext, IssueCategory.Workflow_missingInput,"task \""+getName()+"\" input \""+input.getName()+"\" is null", Issue.SEVERITY_WARNING));
				}
			}
		}
		for (DataHolder<? extends Object> output : outputs){
			if (output.getData()==null){
//				issues.add(new Issue(this, issueContext, IssueCategory.Workflow_missingInput,"task \""+getName()+"\" output \""+output.getName()+"\" is null", Issue.SEVERITY_WARNING));
			}
		}
	}
	
	public void validateInputs() throws Exception{
		for (DataInput<? extends Object> input : inputs){
			if (input.getSource()==null){
				if (!input.isOptional()){
					throw new Exception("task \""+getName()+"\" missing required connection for input \""+input.getName()+"\"");
				}
			}else{
				if (input.getData()==null){
					Object parent = input.getSource().getParent();
					if (parent instanceof Task){
						Task inputTask = (Task)parent;
						throw new Exception("task \""+getName()+"\" input \""+input.getName()+"\" is null - see task \""+inputTask.getName()+"\" output \""+input.getSource().getName()+"\"");
					}else{
						throw new Exception("task \""+getName()+"\" input \""+input.getName()+"\" is null - see workflow input \""+input.getSource().getName()+"\"");		
					}
				}
			}
		}
	}

	public DiagramStyle getDiagramStyle() {
		return style;
	}

	public void setDiagramStyleText(String styleText) {
		this.style = new DiagramStyle(styleText);
	}

	protected void fireStatusChanged() {
		// TODO Auto-generated method stub
		
	}
}
