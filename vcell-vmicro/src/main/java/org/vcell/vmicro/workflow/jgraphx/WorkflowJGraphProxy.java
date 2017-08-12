package org.vcell.vmicro.workflow.jgraphx;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataObject;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;
import org.vcell.workflow.Workflow;
import org.vcell.workflow.Workflow.WorkflowChangeListener;
import org.vcell.workflow.WorkflowObject;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

public class WorkflowJGraphProxy {

		private TaskContext context = null;
		private WorkflowGraph graph = null;
		
		private WorkflowChangeListener workflowChangeListener = new WorkflowChangeListener() {
			
			@Override
			public void workflowChanged(Workflow source) {
				graph.repaint();
			}
		};
		
		private mxIEventListener graphEventListener = new mxIEventListener() {
			
			@Override
			public void invoke(Object sender, mxEventObject event) {
				try {
//					System.out.println();
//					System.out.println("Workflow Event Listener : \n"+
//							"thisModel = "+graph.getModel()+"\n"+
//							"sender = "+sender+"\n"+
//							"event name:"+event.getName()+"\n"+
//							"properties:"+event.getProperties());
//					System.out.println();
					
					if (event.getName().equals(mxEvent.REMOVE_CELLS)){
						removeCells(event);
					}else if (event.getName().equals(mxEvent.ADD_CELLS)){
						addCells(event);
					}else if (event.getName().equals(mxEvent.CELLS_ADDED)){
						addCells(event);
					}else if (event.getName().equals(mxEvent.REPAINT)){
						// do nothing
					}else{
						System.out.println("unhanded event "+event.getName());
					}
						
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		};
		
		public final class WorkflowGraph extends mxGraph implements Serializable {
			private mxGraphComponent graphComponent;
			
			@Override
			public String validateEdge(Object edge, Object source, Object target) {
				String errorMessage = super.validateEdge(edge, source, target);
				if (errorMessage!=null){
					return errorMessage;
				}
				return validateConnection((mxICell)source, (mxICell)target);
			}
			
			public WorkflowObjectCell getCell(WorkflowObject workflowObject){
				for (Object cell : getChildCells(getCurrentRoot())){
					if (cell instanceof WorkflowObjectCell){
						if (((WorkflowObjectCell)cell).workflowObject == workflowObject){
							return (WorkflowObjectCell)cell;
						}
					}
				}
				return null;
			}
			
			public void setGraphComponent(mxGraphComponent graphComponent) {
				this.graphComponent = graphComponent;
			}
			
			/**
			 * only overriding this to remove the graph property on the cell
			 */
			@Override
			public Object[] removeCells(Object[] cells, boolean includeEdges) {
				if (cells!=null){
					for (Object cell : cells){
						if (cell instanceof WorkflowObjectCell){
							((WorkflowObjectCell)cell).setGraph(null);
						}
					}
				}
				return super.removeCells(cells, includeEdges);
			}

			/**
			 * only overriding this to set the graph property on the cell
			 */
			@Override
			public Object[] addCells(Object[] cells, Object parent,	Integer index, Object source, Object target) {
				if (cells!=null){
					for (Object cell : cells){
						if (cell instanceof WorkflowObjectCell){
							((WorkflowObjectCell)cell).setGraph(this);
						}
					}
				}
				return super.addCells(cells, parent, index, source, target);
			}

			public void forceRepaint() {
				if (graphComponent!=null){
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> F O R C E   A    R E P A I N T");
					graphComponent.refresh();repaint(getView().getGraphBounds());
					graphComponent.repaint();
				}
			}
		}

		public static class WorkflowObjectCell extends mxCell implements PropertyChangeListener {
			public final WorkflowObject workflowObject;
			private WorkflowGraph graph;
			
			public WorkflowObjectCell(WorkflowObject workflowObject, String label, mxGeometry geo, String style){
				super(label,geo,style);
				this.workflowObject = workflowObject;
//				this.workflowObject.addPropertyChangeListener(this);
			}
			
			private void setGraph(WorkflowGraph workflowGraph){
				this.graph = workflowGraph;
				for (int i=0; i<getChildCount(); i++){
					if (getChildAt(i) instanceof WorkflowObjectCell){
						((WorkflowObjectCell)getChildAt(i)).setGraph(workflowGraph);
					}
				}
				this.workflowObject.removePropertyChangeListener(this);
				this.workflowObject.addPropertyChangeListener(this);
			}

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (graph!=null){
					if (evt.getPropertyName().equals(WorkflowObject.PROPERTYNAME_NAME)){
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {
								System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> repainting from propertyChangeEvent, cell = "+workflowObject.getClass().getSimpleName()+", thread = "+Thread.currentThread().getName());
								graph.forceRepaint();
							}
						});
					}
				}
			}
			
		}

		public WorkflowJGraphProxy(TaskContext context){
			this.context = context;
			context.getWorkflow().addWorkflowChangeListener(workflowChangeListener);
		}
		
		public WorkflowGraph getGraph(){
			if (graph == null){
				graph = createGraphJGraphX();
			}
			return graph;
		}
		
		private void addCells(mxEventObject event) {
			try {
				// remove an existing connection
				Object[] cells = (Object[])event.getProperty("cells");
				// add tasks first
				for (Object cellObject : cells){
					if (cellObject instanceof WorkflowObjectCell){
						WorkflowObjectCell cell = (WorkflowObjectCell)cellObject;
						if (!cell.isEdge()){
							// don't know how to add an object through an event yet ...
							WorkflowObject addedTask = cell.workflowObject;
							if (cell.workflowObject instanceof Task){
								Task task = (Task)cell.workflowObject;
								task.setName(context.getWorkflow().getNextAvailableTaskName(task.getName()));
								context.getWorkflow().addTask((Task)cell.workflowObject);
								cell.setId(cell.workflowObject.getName());
								cell.setGraph(graph);
							}else{
								// not a task, not an edge, and not already added ... can't handle this now
								System.err.println("can't handle this now ... non-edge cell (id="+cell.getId()+") added to jGraphX but not already added to workflow");
							}
						}
					}
				}
				// add edges next
				for (Object cellObject : cells){
					if (cellObject instanceof mxCell){
						mxCell cell = (mxCell)cellObject;
						if (cell.isEdge()){
							mxICell source = cell.getSource();
							mxICell target = cell.getTarget();
							if ((source instanceof WorkflowObjectCell) && (target instanceof WorkflowObjectCell)){
								String validationError = validateConnection(source, target);
								if (validationError == null){
									WorkflowObject workflowSourceObject = ((WorkflowObjectCell)source).workflowObject;
									WorkflowObject workflowTargetObject = ((WorkflowObjectCell)target).workflowObject;
									if (workflowSourceObject instanceof DataOutput && workflowTargetObject instanceof DataInput){
										DataInput input = (DataInput)workflowTargetObject;
										if (context.getWorkflow().getConnectorSource(input)!=null){
											System.out.println("don't add this connection, a connection already exists");
										}else{
											DataOutput output = (DataOutput)workflowSourceObject;
											context.getWorkflow().connect2(output, input);
											System.out.println("added edge from "+workflowSourceObject.getPath()+" to "+workflowTargetObject.getPath());
										}
									}
								}else{
									System.out.println("didn't connect, "+validationError);
								}
							}
						}
					}
				}
			}finally{
				context.getWorkflow().refreshStatus();
			}
		}
		
		static WorkflowObjectCell createGenericCell(Task task){
			int posX = task.getDiagramStyle().getX(0);
			int posY = task.getDiagramStyle().getY(0);
			mxGeometry geo = new mxGeometry(posX, posY, 200, 120);
			geo.setAlternateBounds(new mxRectangle(posX,posY,200, 120));
			WorkflowObjectCell taskNode = new WorkflowObjectCell(task, task.getName(), geo, "verticalAlign=top");
			taskNode.setId(task.getPath());
			taskNode.setVertex(true);
			taskNode.setConnectable(false);
			
			final int PORT_DIAMETER = 20;
			final int PORT_RADIUS = PORT_DIAMETER / 2;
			int numInputs = task.getInputs().size();
			double inputCount = 2;
			for (DataInput<? extends Object> input : task.getInputs()){
				mxGeometry geoInputPort = new mxGeometry(0, inputCount/(numInputs+2), PORT_DIAMETER, PORT_DIAMETER);
				inputCount++;
				// Because the origin is at upper left corner, need to translate to
				// position the center of port correctly
				geoInputPort.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
				geoInputPort.setRelative(true);

				WorkflowObjectCell inputPort = new WorkflowObjectCell(input, input.getName(), geoInputPort, "shape=ellipse;perimter=ellipsePerimeter");
				inputPort.setId(input.getPath());
				inputPort.setVertex(true);
				taskNode.insert(inputPort);
				inputPort.setParent(taskNode);
			}
			int numOutputs = task.getOutputs().size();
			double outputCount = 2;
			for (DataOutput<? extends Object> output : task.getOutputs()){
				mxGeometry geoOutputPort = new mxGeometry(1, outputCount/(numOutputs+2), PORT_DIAMETER, PORT_DIAMETER);
				outputCount++;
				// Because the origin is at upper left corner, need to translate to
				// position the center of port correctly
				geoOutputPort.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
				geoOutputPort.setRelative(true);

				WorkflowObjectCell outputPort = new WorkflowObjectCell(output, output.getName(), geoOutputPort, "shape=ellipse;perimter=ellipsePerimeter");
				outputPort.setId(output.getPath());
				outputPort.setVertex(true);
				taskNode.insert(outputPort);
				outputPort.setParent(taskNode);
			}
			return taskNode;
		}

		private void removeCells(mxEventObject event) {
			// remove an existing connection
			Object[] cells = (Object[])event.getProperty("cells");
			// remove edges first
			for (Object cellObject : cells){
				if (cellObject instanceof mxCell){
					mxCell cell = (mxCell)cellObject;
					if (cell.isEdge()){
						mxICell source = cell.getSource();
						mxICell target = cell.getTarget();
						if ((source instanceof WorkflowObjectCell) && (source instanceof WorkflowObjectCell)){
							WorkflowObject workflowSourceObject = ((WorkflowObjectCell)source).workflowObject;
							WorkflowObject workflowTargetObject = ((WorkflowObjectCell)target).workflowObject;
							if (workflowSourceObject instanceof DataOutput && workflowTargetObject instanceof DataInput){
								DataOutput output = (DataOutput)workflowSourceObject;
								DataInput input = (DataInput)workflowTargetObject;
								if (context.getWorkflow().getConnectorSource(input) == output){
									context.getWorkflow().removeConnector(output,input);
									System.out.println("removed edge from "+workflowSourceObject.getPath()+" to "+workflowTargetObject.getPath());
								}else{
									System.out.println("can't remove edge, edge from "+output.getPath()+" to "+input.getPath()+" not found in workflow");
								}
							}else{
								System.out.println("can't remove edge, edge from "+source.getId()+" to "+target.getId()+" not found in workflow");
							}
						}
					}
				}
			}
			// remove tasks next
			for (Object cellObject : cells){
				if (cellObject instanceof mxCell){
					mxCell cell = (mxCell)cellObject;
					if (!cell.isEdge() && cell instanceof WorkflowObjectCell){
						WorkflowObject workflowObject = ((WorkflowObjectCell)cell).workflowObject;
						if (workflowObject instanceof Task){
							context.getWorkflow().removeTask((Task)workflowObject);
							System.out.println("removed task "+workflowObject.getPath());
						}else{
							System.out.println("can't remove non-edge cell "+cell.getId()+" not a task in this workflow");
						}
					}
				}
			}
		}
		private String validateConnection(mxICell source, mxICell target) {
			try {
				if (!(source instanceof WorkflowObjectCell) || !(target instanceof WorkflowObjectCell)){
					return "source or target is null";			
				}
				
				WorkflowObject workflowSourceObject = ((WorkflowObjectCell)source).workflowObject;
				WorkflowObject workflowTargetObject = ((WorkflowObjectCell)target).workflowObject;
				if (workflowSourceObject instanceof DataOutput && workflowTargetObject instanceof DataInput){
					DataInput input = (DataInput)workflowTargetObject;
					Type inputClass = input.getType();
					if (context.getWorkflow().getConnectorSource(input)!=null){
						return "don't add this connection, a connection already exists";
					}else{
						DataOutput output = (DataOutput)workflowSourceObject;
						Type outputClass = output.getType();
						if (outputClass.getClass().isAssignableFrom(inputClass.getClass())){
							return null;
						}else{
							return "can't connect "+workflowSourceObject.getPath()+" of type "+outputClass+"\n to "+workflowTargetObject.getPath()+" of type "+inputClass;
						}
					}
				}else{
					return "unknown source or desintation type";
				}
			}catch (Exception e){
				e.printStackTrace();
				return "error validating edge: exception: "+e.getMessage();
			}finally{
				context.getWorkflow().refreshStatus();
			}
		}
		
		public WorkflowGraph createGraphJGraphX(){
			graph = new WorkflowGraph();
			graph.getModel().beginUpdate();
			graph.setCellsEditable(false);
			graph.setCellsCloneable(false);
			graph.setCellsMovable(true);
			graph.setCellsResizable(false);
			graph.setDropEnabled(false);
			Object parent = graph.getDefaultParent();
			try {
				//
				// make subgraph for each task ... alot space from [-1:1] in x and center in y
				//
				int scale = 100;
				int taskPosX = 20;
				int taskPosXDelta = 240;
				int taskPosY = scale/2;
				HashMap<Task,WorkflowObjectCell> taskMap = new HashMap<Task,WorkflowObjectCell>();
				for (Task task : context.getWorkflow().getTasks()){
					WorkflowObjectCell taskNode = createGenericCell(task);
					taskNode.getGeometry().setX(task.getDiagramStyle().getX(taskPosX));
					taskNode.getGeometry().setY(task.getDiagramStyle().getY(taskPosY));
					taskMap.put(task,taskNode);
					graph.addCell(taskNode);
					taskPosX += taskPosXDelta;
				}
				//
				// hook up inputs and outputs
				//
				for (Task task : context.getWorkflow().getTasks()){
					for (DataInput<? extends Object> input : task.getInputs()){
						DataObject<? extends Object> dataSource = context.getWorkflow().getConnectorSource(input);
						if (dataSource!=null){
							WorkflowObjectCell inputTaskNode = taskMap.get(input.getParent());
							if (inputTaskNode!=null){
								WorkflowObjectCell inputNode = null;
								for (int i=0;i<inputTaskNode.getChildCount();i++){
									if (inputTaskNode.getChildAt(i).getValue().equals(input.getName())){
										inputNode = (WorkflowObjectCell)inputTaskNode.getChildAt(i);
									}
								}
								if (dataSource instanceof DataOutput){
									DataOutput dataHolder = (DataOutput)dataSource;
									Object dataHolderParent = dataHolder.getParent();
									if (dataHolderParent instanceof Task){
										WorkflowObjectCell outputTask = taskMap.get((Task)dataHolderParent);
										WorkflowObjectCell holderNode = null;
										for (int i=0;i<outputTask.getChildCount();i++){
											if (outputTask.getChildAt(i).getValue().equals(dataHolder.getName())){
												holderNode = (WorkflowObjectCell)outputTask.getChildAt(i);
											}
										}
										if (holderNode!=null){ // an output of another task
											graph.insertEdge(parent, null, null, holderNode, inputNode);
										}
									}
								}
							}
						}
					}
				}
			}finally{
				graph.getModel().endUpdate();
			}
			graph.addListener(null, graphEventListener);
//			graph.getModel().addListener(mxEvent.CHANGE, getEventListener());
			return graph;
		}

		public void workflowRun() throws Exception {
			ClientTaskStatusSupport progress = new ClientTaskStatusSupport() {
				String message = "";
				int progress = 0;
				@Override
				public void setProgress(int progress) {
					this.progress = progress;
				}
				
				@Override
				public void setMessage(String message) {
					this.message = message;
				}
				
				@Override
				public boolean isInterrupted() {
					return false;
				}
				
				@Override
				public int getProgress() {
					return progress;
				}
				
				@Override
				public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
				}
			};
			context.getWorkflow().compute(context,progress);
		}

	}