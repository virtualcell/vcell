package org.vcell.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vcell.util.ClientTaskStatusSupport;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public abstract class Task extends WorkflowObject {
	
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

	private ArrayList<DataInput<? extends Object>> inputs = new ArrayList<DataInput<? extends Object>>();
	private ArrayList<DataOutput<? extends Object>> outputs = new ArrayList<DataOutput<? extends Object>>();
	
	public Task(String name){
		super(name);
	}
	
	@Override
	public String getPath(){
		return "Task( \""+getName()+"\" )";
	}
	
	public final List<DataInput<? extends Object>> getInputs(){
		return Collections.unmodifiableList(inputs);
	}
	
	public final List<DataOutput<? extends Object>> getOutputs(){
		return Collections.unmodifiableList(outputs);
	}
	
	protected final void addInput(DataInput<? extends Object> input) {
		if (!inputs.contains(input)){
			inputs.add(input);
		}
	}

	protected final void addOutput(DataOutput<? extends Object> output) {
		if (!outputs.contains(output)){
			outputs.add(output);
		}
	}

	public final void compute(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		try {
			bRunning = true;
			compute0(context, clientTaskStatusSupport);
		}finally{
			bRunning = false;
		}
	}
	
	protected abstract void compute0(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception;

	public DiagramStyle getDiagramStyle() {
		return style;
	}

	public void setDiagramStyleText(String styleText) {
		this.style = new DiagramStyle(styleText);
	}

	public boolean isRunning() {
		return bRunning;
	}

}
