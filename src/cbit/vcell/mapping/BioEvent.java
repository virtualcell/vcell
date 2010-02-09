package cbit.vcell.mapping;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

public class BioEvent implements Matchable, Serializable, VetoableChangeListener, PropertyChangeListener {
	public static class EventAssignment implements Matchable, Serializable {
		private SymbolTableEntry target = null;
		private Expression assignmentExpression = null;
		public EventAssignment(SymbolTableEntry argTarget, Expression assignment) {
			super();
			this.target = argTarget;
			this.assignmentExpression = assignment;
		}
		
		public boolean compareEqual(Matchable obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof EventAssignment)) {
				return false;
			}
			EventAssignment eventAssignment = (EventAssignment)obj;
			
			if (!Compare.isEqual(target.getName(), eventAssignment.target.getName())) {
				return false;
			}
			if (!Compare.isEqual(assignmentExpression, eventAssignment.assignmentExpression)) {
				return false;
			}

			return true;
		}
		public final SymbolTableEntry getTarget() {
			return target;
		}
		public final Expression getAssignmentExpression() {
			return assignmentExpression;
		}
		public void setAssignmentExpression(Expression expr) {
			this.assignmentExpression = expr; 
		}
		public void bind(SimulationContext simContext) throws ExpressionBindingException {
			// target = mathDescription.getVariable(target.getName());
			assignmentExpression.bindExpression(simContext);
		}	
	}
	public static class Delay implements Matchable, Serializable {
		private boolean bUseValuesFromTriggerTime;
		private Expression durationExpression = null;
		
		public Delay(boolean bUseValuesFromTriggerTime, Expression durationExpression) {
			this.bUseValuesFromTriggerTime = bUseValuesFromTriggerTime;
			this.durationExpression = durationExpression;
		}
		public boolean compareEqual(Matchable obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Delay)) {
				return false;
			}
			Delay delay = (Delay)obj;
			
			if (bUseValuesFromTriggerTime != delay.bUseValuesFromTriggerTime) {
				return false;
			}
			if (!Compare.isEqual(durationExpression, delay.durationExpression)) {
				return false;
			}

			return true;
		}
		public final boolean useValuesFromTriggerTime() {
			return bUseValuesFromTriggerTime;
		}
		public final Expression getDurationExpression() {
			return durationExpression;
		}
		public void bind(SimulationContext simContext) throws ExpressionBindingException {
			durationExpression.bindExpression(simContext);
		}		
	}
	private String name;
	private Expression triggerExpression = null;
	private Delay delay = null;
	private ArrayList<EventAssignment> eventAssignmentList = new ArrayList<EventAssignment>();

	protected transient SimulationContext simulationContext = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	
	public BioEvent(String name, Expression triggerExpression, Delay delay, ArrayList<EventAssignment> eventAssignmentList) {
		super();
		this.name = name; 
		this.triggerExpression = triggerExpression;
		this.delay = delay;
		this.eventAssignmentList = eventAssignmentList;
	}


	public final Expression getTriggerExpression() {
		return triggerExpression;
	}
	
	public final Delay getDelay() {
		return delay;
	}

	public SimulationContext getSimulationContext() {
		return simulationContext;
	}


	public void setSimulationContext(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
	}

	public boolean compareEqual(Matchable obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BioEvent)) {
			return false;
		}
		
		BioEvent event = (BioEvent) obj;		
		if (!Compare.isEqual(getName(),event.getName())){
			return false;
		}
		if (!Compare.isEqual(triggerExpression, event.triggerExpression)) {
			return false;
		}
		if (!Compare.isEqualOrNull(delay, event.delay)) {
			return false;
		}
		if (!Compare.isEqual(eventAssignmentList, event.eventAssignmentList)) {
			return false;
		}
		return true;
	}

	public ArrayList<EventAssignment> getEventAssignments() {
		return eventAssignmentList;
	}

	public int getNumEventAssignments() {
		return eventAssignmentList.size();
	}
	
	public void addEventAssignment(EventAssignment eventAssign) throws PropertyVetoException {
		if (eventAssign == null){
			return;
		}	
		try {
			eventAssign.getAssignmentExpression().bindExpression(this.simulationContext);
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}

		ArrayList<EventAssignment> newEventAssignmentList = new ArrayList<EventAssignment>(eventAssignmentList);
		newEventAssignmentList.add(eventAssign);
		setEventAssignmentsList(newEventAssignmentList);
	}   

	public void removeEventAssignment(EventAssignment eventAssign) throws PropertyVetoException {
		if (eventAssign == null){
			return;
		}	
		if (eventAssignmentList.contains(eventAssign)){
			ArrayList<EventAssignment> newEventAssignmentList = new ArrayList<EventAssignment>(eventAssignmentList);
			newEventAssignmentList.remove(eventAssign);
			setEventAssignmentsList(newEventAssignmentList);
		}
	}         

	public void setEventAssignmentsList(ArrayList<EventAssignment> eventsAssigns) throws PropertyVetoException {
		ArrayList<EventAssignment> oldValue = eventAssignmentList;
		fireVetoableChange("eventAssignments", oldValue, eventsAssigns);
		eventAssignmentList = eventsAssigns;
		firePropertyChange("eventAssignments", oldValue, eventsAssigns);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public void fireVetoableChange(String propertyName, Object oldValue, Object newValue)
			throws PropertyVetoException {
				getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
			}

	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	protected VetoableChangeSupport getVetoPropertyChange() {
		if (vetoPropertyChange == null) {
			vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
		};
		return vetoPropertyChange;
	}

	public void bind() throws ExpressionBindingException {
		triggerExpression.bindExpression(getSimulationContext());
		if (delay != null) {
			delay.bind(getSimulationContext());
		}
		for (EventAssignment eventAssignment : eventAssignmentList) {
			eventAssignment.bind(getSimulationContext());
		}
	}
	
	public String getName() {
		return name;
	}

	public void vetoableChange(PropertyChangeEvent evt)	throws PropertyVetoException {
	}

	public void propertyChange(PropertyChangeEvent evt) {
	}

	public void refreshDependencies() {
		try {
			bind();
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
}
