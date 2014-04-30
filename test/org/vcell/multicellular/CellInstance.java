package org.vcell.multicellular;

import java.util.ArrayList;
import java.util.Random;

import org.sbml.libsbml.Domain;

public class CellInstance {
	
	public enum CellInstanceEventType {
		DIVIDE,
		MOVE
	}
	
	public static class CellInstanceEvent {
		public final CellInstance cellInstance;
		public final CellInstanceEventType eventType;
		public CellInstanceEvent(CellInstance cellInstance, CellInstanceEventType eventType){
			this.cellInstance = cellInstance;
			this.eventType = eventType;
		}
	}
	
	public interface CellInstanceEventListener {
		void event(CellInstanceEvent cellInstanceEvent);
	}
	
	private final Random random = new Random();
	public final Domain domain;
	public final CellModel cellModel;
	public final World world;
	private ArrayList<CellInstanceEventListener> listeners = new ArrayList<CellInstanceEventListener>();
	
	public CellInstance(World world, CellModel cellModel, Domain domain){
		this.cellModel = cellModel;
		this.domain = domain;
		this.world = world;
	}
	
	public void addListener(CellInstanceEventListener listener){
		if (!listeners.contains(listener)){
			listeners.add(listener);
		}
	}
	
	public void removeListener(CellInstanceEventListener listener){
		if (listeners.contains(listener)){
			listeners.remove(listener);
		}
	}
	
	private void fireEvent(CellInstanceEvent event){
		for (CellInstanceEventListener listener : listeners){
			listener.event(event);
		}
	}
	
	public void step(){
		if (random.nextDouble() < 0.1){
			fireEvent(new CellInstanceEvent(this,CellInstanceEventType.DIVIDE));
		}
	}
}