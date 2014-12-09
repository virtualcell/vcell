package org.vcell.workflow;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.TokenMangler;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.workflow.WorkflowObject.Status;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;

public class Workflow implements Serializable {
	
	public static final String PROPERTY_NAME_PARAMETERS		= "parameter";
	public static final String PROPERTY_NAME_TASKS			= "tasks";
	
	private transient ArrayList<WorkflowChangeListener> listeners;
	private transient PropertyChangeSupport propertyChangeSupport;
	
	public interface WorkflowChangeListener{
		public void workflowChanged(Workflow source);
	}

	private ArrayList<WorkflowChangeListener> getListeners(){
		if (listeners==null){
			listeners = new ArrayList<WorkflowChangeListener>();
		}
		return listeners;
	}
	
	public void addWorkflowChangeListener(WorkflowChangeListener listener){
		if (!getListeners().contains(listener)){
			getListeners().add(listener);
		}
	}
	public void removeWorkflowChangeListener(WorkflowChangeListener listener){
		if (getListeners().contains(listener)){
			getListeners().remove(listener);
		}
	}
	private void fireWorkflowChange(){
		Iterator<WorkflowChangeListener> iter = getListeners().iterator();
		while (iter.hasNext()){
			iter.next().workflowChanged(this);
		}
	}
	
	private PropertyChangeSupport getPropertyChangeSupport(){
		if (propertyChangeSupport==null){
			propertyChangeSupport = new PropertyChangeSupport(this);
		}
		return propertyChangeSupport;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}
	
	private void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue){
		getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private ArrayList<DataHolder<? extends Object>> parameters = new ArrayList<DataHolder<? extends Object>>();
	private LocalWorkspace localWorkspace = null;
	
	public Workflow(LocalWorkspace localWorkspace) {
		super();
		this.localWorkspace = localWorkspace;
	}

	public void addTask(Task task) {
		if (getTaskByName(task.getName())!=null){
			throw new RuntimeException("task with name '"+task.getName()+"' is already in workflow");
		}
		if (tasks.contains(task)){
			throw new RuntimeException("task is already in workflow");
		}
		Task[] oldValue = tasks.toArray(new Task[0]);
		tasks.add(task);
		task.setLocalWorkspace(localWorkspace);
		Task[] newValue = tasks.toArray(new Task[0]);
		firePropertyChangeEvent(PROPERTY_NAME_TASKS, oldValue, newValue);
		System.out.println("added task "+task.getPath()+" to workflow "+this.toString());
	}
	
	public void removeTask(Task task) {
		if (tasks.contains(task)){
			Task[] oldValue = tasks.toArray(new Task[0]);
			tasks.remove(task);
			Task[] newValue = tasks.toArray(new Task[0]);
			firePropertyChangeEvent(PROPERTY_NAME_TASKS, oldValue, newValue);
			System.out.println("removed task "+task.getPath()+" from workflow"+this.toString());
		}else{
			System.out.println("couldn't remove task "+task.getPath()+", not found in workflow."+this.toString());
		}
	}
	
	public <T> DataHolder<T> addParameter(Class<T> type, String name, T value) {
		DataHolder<T> dataHolder = new DataHolder<T>(type,name,this);
		dataHolder.setData(value);
		DataHolder<T>[] oldValue = parameters.toArray(new DataHolder[0]);
		parameters.add(dataHolder);
		DataHolder<T>[] newValue = parameters.toArray(new DataHolder[0]);
		firePropertyChangeEvent(PROPERTY_NAME_PARAMETERS, oldValue, newValue);
		return dataHolder;
	}
	
	public void compute(ClientTaskStatusSupport progress) throws Exception {
		boolean bComputedSomething = true;
		while (bComputedSomething){
			boolean bAnythingDirty = false;
			bComputedSomething = false;
			for (Task task : tasks){
				boolean bInputDirty = false;
				for (DataInput<? extends Object> dataInput : task.getInputs()){
					if (dataInput.getSource()!=null && dataInput.getSource().isDirty()){
						bAnythingDirty = true;
						bInputDirty = true;
					}
				}
				boolean bOutputsDirty = false;
				for (DataHolder<? extends Object> output : task.getOutputs()){
					if (output.isDirty()){
						bOutputsDirty = true;
					}
				}
				if (!bInputDirty && bOutputsDirty){
					System.out.println("computing task "+task.getName());
					task.compute(progress);
					bComputedSomething = true;
					refreshStatus();
				}
			}
			if (bComputedSomething == false && bAnythingDirty == true){
				throw new Exception("finished prematurely ... still have dirty tasks");
			}
		}
	}
	
	public void gatherIssues(IssueContext issueContext, ArrayList<Issue> issues) {
		for (Task task : tasks) {
			task.gatherIssues(issueContext, issues);
		}
	}

	public void reportIssues(ArrayList<Issue> issues, int minSeverity, boolean bExceptionOnError) {
		boolean bAbort = false;
		StringBuffer errorBuffer = new StringBuffer();
		for (Issue issue : issues){
			if (issue.getSeverity() >= minSeverity){
				System.out.println("ISSUE: "+issue.toString());
			}
			if (issue.getSeverity() == Issue.SEVERITY_ERROR){
				bAbort = true;
				errorBuffer.append(issue.getCategory()+":"+issue.getMessage()+"  \n");
			}
		}
		if (bAbort && bExceptionOnError){
			throw new RuntimeException(errorBuffer.toString());
		}
	}
	
	public Graph createGraph(){
		Graph graph = new Graph();
		HashMap<Object,Node> nodeMap = new HashMap<Object, Node>();
		//
		// make subgraph for each task
		//
		for (Task task : tasks){
			Node taskNode = new Node("Task:"+task.getName(),task);
			graph.addNode(taskNode);
			nodeMap.put(task,taskNode);
			
//			for (DataInput<? extends Object> input : task.getInputs()){
//				Node inputNode = new Node("Input:"+input.name,input);
//				graph.addNode(inputNode);
//				nodeMap.put(input,inputNode);
//				
//				Edge inputEdge = new Edge(inputNode,taskNode);
//				graph.addEdge(inputEdge);
//			}
			for (DataHolder<? extends Object> output : task.getOutputs()){
				Node outputNode = new Node(output.getName(),output);
				graph.addNode(outputNode);
				nodeMap.put(output,outputNode);
				
				Edge outputEdge = new Edge(taskNode,outputNode);
				graph.addEdge(outputEdge);
			}
		}
		//
		// hook up inputs and outputs
		//
		for (Task task : tasks){
			Node taskNode = nodeMap.get(task);
			for (DataInput<? extends Object> input : task.getInputs()){
				if (input.getSource() instanceof DataHolder){
					//Node inputNode = nodeMap.get(input);
					DataHolder<? extends Object> dataHolder = (DataHolder)input.getSource();
					Node holderNode = nodeMap.get(dataHolder);
					if (holderNode==null){ // an output of another task
						holderNode = new Node("WORKFLOW:input:"+dataHolder.getName(),input.getSource());
					}
					Edge connectionEdge = new Edge(holderNode,taskNode);
					graph.addEdge(connectionEdge);
//				}else{
//					// not hooked up ... floating node
//					Node inputNode = new Node("Disconnected Input:"+input.name,input);
//					graph.addNode(inputNode);
//					nodeMap.put(input,inputNode);
//					
//					Edge inputEdge = new Edge(inputNode,taskNode);
//					graph.addEdge(inputEdge);
				}
			}
		}
		
		return graph;
	}

	/*
	mxCell v1 = (mxCell) graph.insertVertex(parent, null, "Hello", 20,
			20, 100, 100, "");
	v1.setConnectable(false);
	mxGeometry geo = graph.getModel().getGeometry(v1);
	// The size of the rectangle when the minus sign is clicked
	geo.setAlternateBounds(new mxRectangle(20, 20, 100, 50));

	mxGeometry geo1 = new mxGeometry(0, 0.5, PORT_DIAMETER,
			PORT_DIAMETER);
	// Because the origin is at upper left corner, need to translate to
	// position the center of port correctly
	geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
	geo1.setRelative(true);

	mxCell port1 = new mxCell(null, geo1,
			"shape=ellipse;perimter=ellipsePerimeter");
	port1.setVertex(true);

	mxGeometry geo2 = new mxGeometry(1.0, 0.5, PORT_DIAMETER,
			PORT_DIAMETER);
	geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
	geo2.setRelative(true);

	mxCell port2 = new mxCell(null, geo2,
			"shape=ellipse;perimter=ellipsePerimeter");
	port2.setVertex(true);

	graph.addCell(port1, v1);
	graph.addCell(port2, v1);

	Object v2 = graph.insertVertex(parent, null, "World!", 240, 150, 80, 30);
	
	graph.insertEdge(parent, null, "Edge", port2, v2);
*/
	
	
	public List<Task> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	public List<DataHolder<? extends Object>> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
	
	public static Workflow parse(LocalWorkspace localWorkspace, String workflowLanguage){
		String currAnonymousParameterName = "anonParam000";
		Workflow workflow = new Workflow(localWorkspace);
		
		StringTokenizer lineTokens = new StringTokenizer(workflowLanguage, "\n\t", true);
		int lineNumber = 1;
		while (lineTokens.hasMoreTokens()){
			String line = lineTokens.nextToken();
			if (line.equals("\n")){
				lineNumber++;
				continue;
			}
			if (line.equals("\t")){
				continue;
			}
			String[] tokens = line.split("=");
			if (tokens.length!=2){
				throw new RuntimeException("failed to parse line "+lineNumber+", '"+line+"', expecting line identifier1 = (identifier | identifier.identifier | taskClassName())");
			}
			String token0 = tokens[0].trim();
			String token1 = tokens[1].trim();
			if (token1.endsWith("()")){
				//
				// defining a new task
				//
				String className = token1.substring(0, token1.length()-2);
				String prefix = "cbit.vcell.microscopy.workflow";
				Task task = null;
				try {
					Class<? extends Task> taskClass = (Class<? extends Task>)Workflow.class.forName(prefix+"."+className);
					task = taskClass.getConstructor(String.class).newInstance(token0);
					workflow.addTask(task);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("failed to create task "+token0+" on line "+lineNumber+", reason = "+e.getMessage());
				}
			}else if (token0.contains(".")){
				//
				// defining a connection
				//
				String[] targetParts = token0.split("\\.");
				String targetTaskName = targetParts[0];
				String targetInputName = targetParts[1];
				Task targetTask = workflow.getTaskByName(targetTaskName);
				if (targetTask==null){
					throw new RuntimeException("failed to resolve task "+targetTaskName+" on line "+lineNumber);
				}
				if (targetInputName.equals(Task.STYLE_ATTRIBUTE_NAME)){
					// taskName.style = "{posX:100,posY:200}"
					if (token1.startsWith("\"") && token1.endsWith("\"")){
						// RHS is a string
						String styleText = token1.replace("\"", "");
						targetTask.setDiagramStyleText(styleText);
					}else{
						throw new RuntimeException("style attribute RHS expected to be surrounded by double quotes, line="+lineNumber);
					}
				}else{
					// taskName.input = parameter | task.output | literal
					DataInput dataInput = null;
					try {
						Field targetInputField = targetTask.getClass().getField(targetInputName);
						dataInput = (DataInput)targetInputField.get(targetTask);
					}catch (Exception e){
						e.printStackTrace();
						throw new RuntimeException("failed to resolve task input "+token0+" on line "+lineNumber);
					}
					DataHolder dataHolder = null;
					if (token1.contains(".")){
						String[] sourceParts = token1.split("\\.");
						String sourceTaskName = sourceParts[0];
						String sourceOutputName = sourceParts[1];
						Task sourceTask = workflow.getTaskByName(sourceTaskName);
						try {
							Field sourceOutputField = sourceTask.getClass().getField(sourceOutputName);
							dataHolder = (DataHolder)sourceOutputField.get(sourceTask);
							if (dataHolder!=null){								// token1 was a reference to a parameter
								dataInput.setSource(dataHolder);
							}else{
								throw new RuntimeException("failed to resolve task output "+token1+" on line "+lineNumber);
							}
						}catch (Exception e){
							e.printStackTrace();
							throw new RuntimeException("failed to resolve task output "+token1+" on line "+lineNumber);
						}
					}else{
						dataHolder = workflow.getParameter(token1);
						if (dataHolder!=null){
							// token1 was a reference to a parameter
							dataInput.setSource(dataHolder);
						}else{
							// token1 may be a literal ... create an anonymous parameter
							if (token1.startsWith("\"") && token1.endsWith("\"")){
								// RHS is a string
								currAnonymousParameterName = TokenMangler.getNextEnumeratedToken(currAnonymousParameterName);
								dataInput.setSource(workflow.addParameter(String.class,currAnonymousParameterName, token1.replace("\"", "")));
							}else{
								try {
									// RHS is an integer
									Integer parsedInt = Integer.parseInt(token1);
									currAnonymousParameterName = TokenMangler.getNextEnumeratedToken(currAnonymousParameterName);
									dataInput.setSource(workflow.addParameter(Integer.class,currAnonymousParameterName, parsedInt));
								}catch (Exception e1){
									try {
										// RHS is a double
										Double parsedDouble = Double.parseDouble(token1);
										currAnonymousParameterName = TokenMangler.getNextEnumeratedToken(currAnonymousParameterName);
										dataInput.setSource(workflow.addParameter(Double.class,currAnonymousParameterName,parsedDouble));
									}catch (Exception e2){
										if (token1.toLowerCase().equals("true") || token1.toLowerCase().equals("false")){
											// RHS is an boolean
											try {
												Boolean parsedBoolean = Boolean.parseBoolean(token1);
												currAnonymousParameterName = TokenMangler.getNextEnumeratedToken(currAnonymousParameterName);
												dataInput.setSource(workflow.addParameter(Boolean.class,currAnonymousParameterName, parsedBoolean));
											}catch (Exception e3){
												throw new RuntimeException("failed to parse boolean literal "+token1+" on line "+lineNumber);
											}
										}else{
											throw new RuntimeException("unexpected RHS for assignment '"+token1+"' on line "+lineNumber);
										}
									}
								}
								throw new RuntimeException("couldn't resolve RHS "+token1+" in line "+lineNumber+" as a parameter or a literal");
							}
						}
					}
				}
			}else{
				//
				// defining a parameter
				//
				String parameterName = token0;
				String sourceToken = token1;
				if (sourceToken.startsWith("\"") && sourceToken.endsWith("\"")){
					// input is a string
					workflow.addParameter(String.class,parameterName, sourceToken.replace("\"", ""));
				}else{
					try {
						Integer parsedInt = Integer.parseInt(sourceToken);
						workflow.addParameter(Integer.class,parameterName, parsedInt);
					}catch (Exception e1){
						try {
							Double parsedDouble = Double.parseDouble(sourceToken);
							workflow.addParameter(Double.class,parameterName,parsedDouble);
						}catch (Exception e2){
							if (sourceToken.toLowerCase().equals("true") || sourceToken.toLowerCase().equals("false")){
								try {
									Boolean parsedBoolean = Boolean.parseBoolean(sourceToken);
									workflow.addParameter(Boolean.class,parameterName, parsedBoolean);
								}catch (Exception e3){
									throw new RuntimeException("failed to parse RHS of parameter "+parameterName+" on line "+lineNumber);
								}
							}else{
								throw new RuntimeException("unexpected RHS for parameter assignment '"+sourceToken+"' on line "+lineNumber);
							}
						}
					}
				}
			}
		}
		return workflow;
	}

	private DataHolder getParameter(String id) {
		for (DataHolder parameter : parameters){
			if (parameter.getName().equals(id)){
				return parameter;
			}
		}
		return null;
	}

	public Task getTaskByName(String name) {
		for (Task task : tasks){
			if (task.getName().equals(name)){
				return task;
			}
		}
		return null;
	}
	
	public String getNextAvailableTaskName(String prefix){
		while (getTaskByName(prefix)!=null){
			prefix = TokenMangler.getNextEnumeratedToken(prefix);
		}
		return prefix;
	}

	public void refreshStatus() {
		boolean bChanges = true;
		while (bChanges){
			bChanges = false;
			for (Task task : tasks){
				Status oldStatus = task.getStatus();
				task.updateStatus();
				Status newStatus = task.getStatus();
				if (!Compare.isEqualOrNull(oldStatus, newStatus)){
					bChanges = true;
				}
			}
		}
	}
	
}
