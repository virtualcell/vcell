package org.vcell.workflow;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.Issue.Severity;
import org.vcell.util.IssueContext;
import org.vcell.util.TokenMangler;
import org.vcell.vmicro.workflow.data.LocalWorkspace;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;

public class Workflow implements Serializable, IssueSource {
	
	public static final String PROPERTY_NAME_PARAMETERS		= "parameter";
	public static final String PROPERTY_NAME_TASKS			= "tasks";

	private ArrayList<Task> tasks = new ArrayList<Task>();
	private ArrayList<Connection<? extends Object>> connections = new ArrayList<Connection<? extends Object>>();
	private ArrayList<WorkflowParameter<? extends Object>> parameters = new ArrayList<WorkflowParameter<? extends Object>>();
	private final String name;
	
	private transient ArrayList<WorkflowChangeListener> listeners;
	private transient PropertyChangeSupport propertyChangeSupport;
	
	private class Connection<T> {
		private final DataObject<T> source;
		private final DataObject<T> target;
		
		private Connection(DataObject<T> source, DataObject<T> target){
			if (source==null || target==null){
				throw new IllegalArgumentException("both input and output must be non-null");
			}
			this.source = source;
			this.target = target;
		}
		
		public String toString(){
			return "connection(from="+source.getPath()+", to="+target.getPath();
		}
	}
		
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
	
	public Workflow(String name) {
		super();
		this.name = name;
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
		if (task instanceof WorkflowTask){
			((WorkflowTask)task).addWorkflowComponents(this);
		}
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
	
	public <T> WorkflowParameter<T> addParameter(Class<T> type, String name, Repository repository, T data) {
		WorkflowParameter<T> parameter = new WorkflowParameter<T>(type,name,this);
		parameters.add(parameter);
		repository.setData(parameter, data);
		return parameter;
	}
	
	public <T> WorkflowParameter<T> addParameter(Class<T> type, String name) {
		WorkflowParameter<T> parameter = new WorkflowParameter<T>(type,name,this);
		parameters.add(parameter);
		return parameter;
	}
		
	public void compute(TaskContext context, ClientTaskStatusSupport progress) throws Exception {
		boolean bComputedSomething = true;
		boolean bSomethingDirty = false;
		int iterationCount = 0;
		int numTasks = getTasks().size();
		while (bComputedSomething && iterationCount < numTasks){
			bComputedSomething = false;
			bSomethingDirty = false;
			for (Task task : tasks){
				boolean bInputDirty = false;
				for (DataInput<? extends Object> dataInput : task.getInputs()){
					if (!dataInput.bOptional && context.getRepository().isDirty(this, dataInput)){
						bInputDirty = true;
						bSomethingDirty = true;
					}
				}
				boolean bOutputsDirty = false;
				for (DataOutput<? extends Object> output : task.getOutputs()){
					if (context.getRepository().isDirty(this, output)){
						bOutputsDirty = true;
						bSomethingDirty = true;
					}
				}
				if (!bInputDirty && bOutputsDirty){
					System.out.println("iteration "+iterationCount+", task "+task.getName()+" run");
					task.compute(context, progress);
					bComputedSomething = true;
				}else if (bInputDirty || bOutputsDirty){
					System.out.println("iteration "+iterationCount+", task "+task.getName()+" not run, inputDirty="+bInputDirty+", outputsDirty="+bOutputsDirty);
				}
			}
			iterationCount++;
			System.out.println("\n\n\n\n\n");
		}
		if (bComputedSomething == false && bSomethingDirty == true) {
			throw new Exception("finished prematurely ... still have dirty tasks");
		}
	}
	
	public void gatherIssues(IssueContext issueContext, ArrayList<Issue> issues) {
		//
		// look for workflowParameters and task inputs that have no connections.
		//
		for (Task task : tasks) {
			for (DataInput input : task.getInputs()){
				if (!input.bOptional && getConnectorSource(input)==null){
					issues.add(new Issue(this, issueContext, IssueCategory.Workflow_missingInput, "input "+(task.getName()+"."+input.getName())+"is not connected and is not optional", Issue.SEVERITY_ERROR));
				}
			}
		}
		for (WorkflowParameter p : parameters) {
			if (getTargets(p).isEmpty()){
				issues.add(new Issue(this, issueContext, IssueCategory.Workflow_missingInput, "workflow parameter "+p.getName()+"is not used", Issue.SEVERITY_WARNING));
			}
		}
	}
	
	public void reportIssues(ArrayList<Issue> issues, Severity minSeverity, boolean bExceptionOnError) {
		boolean bAbort = false;
		StringBuffer errorBuffer = new StringBuffer();
		for (Issue issue : issues){
			if (issue.getSeverity().ordinal() >= minSeverity.ordinal()){
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
			for (DataOutput<? extends Object> output : task.getOutputs()){
				Node outputNode = new Node(output.getName(),output);
				graph.addNode(outputNode);
				nodeMap.put(output,outputNode);
				
				Edge outputEdge = new Edge(taskNode,outputNode);
				graph.addEdge(outputEdge);
			}
		}
		//
		// hook up connections
		//
		for (Connection connection : connections){
			if (connection.source instanceof DataOutput && connection.target instanceof DataInput){
				Node outputNode = nodeMap.get(connection.source);
				Node taskNode = nodeMap.get(((DataInput)connection.target).getParent());
				Edge connectionEdge = new Edge(outputNode,taskNode);
				graph.addEdge(connectionEdge);
			}
		}
		return graph;
	}

	public List<Task> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	public List<WorkflowParameter<? extends Object>> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
	
	public static Workflow parse(Repository repository, LocalWorkspace localWorkspace, String workflowLanguage){
		String currAnonymousParameterName = "anonParam000";
		Workflow workflow = new Workflow("parsedWorkflow");
		
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
				String prefix = "org.vcell.vmicro.workflow.task";
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
					if (token1.contains(".")){
						String[] sourceParts = token1.split("\\.");
						String sourceTaskName = sourceParts[0];
						String sourceOutputName = sourceParts[1];
						Task sourceTask = workflow.getTaskByName(sourceTaskName);
						try {
							Field sourceOutputField = sourceTask.getClass().getField(sourceOutputName);
							DataOutput dataOutput = (DataOutput)sourceOutputField.get(sourceTask);
							if (dataOutput!=null){								// token1 was a reference to a parameter
								workflow.connect2(dataOutput, dataInput);
							}else{
								throw new RuntimeException("failed to resolve task output "+token1+" on line "+lineNumber);
							}
						}catch (Exception e){
							e.printStackTrace();
							throw new RuntimeException("failed to resolve task output "+token1+" on line "+lineNumber);
						}
					}else{
						WorkflowParameter generalParameter = workflow.getParameter(token1);
						if (generalParameter!=null){
							// token1 was a reference to a parameter
							workflow.connectParameter(generalParameter, dataInput);
						}else{
							// token1 may be a literal ... create an anonymous parameter
							if (token1.startsWith("file(\"") && token1.endsWith("\")")){
								//
								// input is a filename (type File)
								//
								// file("filename") --> filename
								//
								currAnonymousParameterName = TokenMangler.getNextEnumeratedToken(currAnonymousParameterName);
								String filename = token1.replace("file(\"", "").replace("\")", "");
								File file = new File(filename);
								WorkflowParameter<File> fileParameter = workflow.addParameter(File.class,currAnonymousParameterName, repository, file);
								if (!dataInput.getType().equals(File.class)){
									throw new RuntimeException("cannot assign file parameter "+fileParameter.getPath()+" to input "+dataInput.getPath()+" of type "+dataInput.getType().getName());
								}
								workflow.connectParameter(fileParameter, (DataInput<File>)dataInput);
							}else if (token1.startsWith("\"") && token1.endsWith("\"")){
								// RHS is a string
								currAnonymousParameterName = TokenMangler.getNextEnumeratedToken(currAnonymousParameterName);
								WorkflowParameter<String> stringParameter = workflow.addParameter(String.class,currAnonymousParameterName, repository, token1.replace("\"", ""));
								if (!dataInput.getType().equals(String.class)){
									throw new RuntimeException("cannot assign string parameter "+stringParameter.getPath()+" to input "+dataInput.getPath()+" of type "+dataInput.getType().getName());
								}
								workflow.connectParameter(stringParameter, (DataInput<String>)dataInput);
							}else{
								try {
									// RHS is an integer
									Integer parsedInt = Integer.parseInt(token1);
									currAnonymousParameterName = TokenMangler.getNextEnumeratedToken(currAnonymousParameterName);
									WorkflowParameter<Integer> intParameter = workflow.addParameter(Integer.class,currAnonymousParameterName, repository, parsedInt);
									workflow.connectParameter(intParameter,(DataInput<Integer>)dataInput);
								}catch (Exception e1){
									try {
										// RHS is a double
										Double parsedDouble = Double.parseDouble(token1);
										currAnonymousParameterName = TokenMangler.getNextEnumeratedToken(currAnonymousParameterName);
										WorkflowParameter<Double> doubleParameter = workflow.addParameter(Double.class,currAnonymousParameterName,repository,parsedDouble);
										workflow.connectParameter(doubleParameter, (DataInput<Double>)dataInput);
									}catch (Exception e2){
										if (token1.toLowerCase().equals("true") || token1.toLowerCase().equals("false")){
											// RHS is an boolean
											try {
												Boolean parsedBoolean = Boolean.parseBoolean(token1);
												currAnonymousParameterName = TokenMangler.getNextEnumeratedToken(currAnonymousParameterName);
												WorkflowParameter<Boolean> booleanParameter = workflow.addParameter(Boolean.class,currAnonymousParameterName, repository, parsedBoolean);
												workflow.connectParameter(booleanParameter,  (DataInput<Boolean>)dataInput);
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
				if (sourceToken.startsWith("file(\"") && sourceToken.endsWith("\")")){
					//
					// input is a filename (type File)
					//
					// file("filename") --> filename
					//
					String filename = sourceToken.replace("file(\"", "").replace("\")", "");
					File file = new File(filename);
					workflow.addParameter(File.class, parameterName, repository, file);
				}else if (sourceToken.startsWith("\"") && sourceToken.endsWith("\"")){
					// input is a string   "string" --> string
					workflow.addParameter(String.class,parameterName, repository, sourceToken.replace("\"", ""));
				}else{
					try {
						Integer parsedInt = Integer.parseInt(sourceToken);
						workflow.addParameter(Integer.class,parameterName, repository, parsedInt);
					}catch (Exception e1){
						try {
							Double parsedDouble = Double.parseDouble(sourceToken);
							workflow.addParameter(Double.class,parameterName,repository,parsedDouble);
						}catch (Exception e2){
							if (sourceToken.toLowerCase().equals("true") || sourceToken.toLowerCase().equals("false")){
								try {
									Boolean parsedBoolean = Boolean.parseBoolean(sourceToken);
									workflow.addParameter(Boolean.class,parameterName, repository, parsedBoolean);
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

	public WorkflowParameter getParameter(String id) {
		for (WorkflowParameter parameter : parameters){
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

	private <T> void connect0(DataObject<T> source, DataObject<T> target) {
		DataObject<T> existingSource = getConnectorSource(target);
		if (existingSource != null){
			throw new RuntimeException("target "+target.getPath()+" already connected to source "+existingSource.getPath());
		}
		Connection<T> connection = new Connection<T>(source,target);
		connections.add(connection);
	}

	public <T> void connectParameter(WorkflowParameter<T> source, DataInput<T> target) {
		connect0(source,target);
	}

	public <T> void connect2(DataOutput<T> source, DataInput<T> target) {
		connect0(source,target);
	}

	public <T> void joinInputs(DataInput<T> source, DataInput<T> target) {
		connect0(source,target);
	}
	
	public <T> void joinOutputs(DataOutput<T> source, DataOutput<T> target) {
		connect0(source,target);
	}

	public <T> WorkflowDataSource<T> getDataSource(DataObject<T> target){
		for (Connection c : connections){
			if (c.target == target){
				if (c.source instanceof WorkflowDataSource){
					DataObject connectorSource = getConnectorSource(c.source);
					if (connectorSource == null){ // this source is authoritative
						return (WorkflowDataSource)c.source;
					}else{ // this source depends on another source (output-output join).
						return getDataSource(connectorSource);
					}
				}else if (c.source instanceof DataInput){ // transitive property (two inputs bound together).
					return getDataSource(c.source);
				}
			}
		}
		if (target instanceof WorkflowDataSource){
			return (WorkflowDataSource)target;
		}
		return null;
	}

	public <T> DataObject<T> getConnectorSource(DataObject<T> target){
		for (Connection c : connections){
			if (c.target == target){
				return c.source;
			}
		}
		return null;
	}

	public <T> List<DataObject<T>> getTargets(DataObject<T> source){
		ArrayList<DataObject<T>> targets = new ArrayList<DataObject<T>>();
		for (Connection c : connections){
			if (c.source == source){
				targets.add((DataObject<T>)c.target);
			}
		}
		return targets;
	}
	
	public void refreshStatus() {
		//
	}

	public String getName(){
		return name;
	}

	public void removeConnector(DataOutput output, DataInput input) {
		Connection connectionToRemove = null;
		for (Connection c : connections){
			if (c.source == output && c.target == input){
				connectionToRemove = c;
			}
		}
		if (connectionToRemove!=null){
			connections.remove(connectionToRemove);
		}
	}

}
