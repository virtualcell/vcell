/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.ParameterPolicy;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.UnitSystemProvider;
import cbit.vcell.units.VCUnitSystem;

@SuppressWarnings("serial")
public class BioEvent implements Matchable, Serializable, VetoableChangeListener, PropertyChangeListener {
	
	private static final String PROPERTY_NAME_NAME = "name";

	private final class BioEventParameterPolicy implements ParameterPolicy, Serializable {
		public boolean isUserDefined(LocalParameter localParameter) {
			return (localParameter.getRole() == ROLE_UserDefined);
		}

		public boolean isExpressionEditable(LocalParameter localParameter) {
			return true;
		}

		public boolean isNameEditable(LocalParameter localParameter) {
			return true;
		}

		public boolean isUnitEditable(LocalParameter localParameter) {
			return isUserDefined(localParameter);
		}
	}

	private final class BioEventUnitSystemProvider implements UnitSystemProvider, Serializable {
		public VCUnitSystem getUnitSystem() {
			return getSimulationContext().getModel().getUnitSystem();
		}
	}

	public class BioEventNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public BioEventNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return BioEvent.this.getName();
		}
		public NameScope getParent() {
			if (BioEvent.this.simulationContext != null){
				return BioEvent.this.simulationContext.getNameScope();
			}else{
				return null;
			}
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return BioEvent.this.parameterContext;
		}
		public String getPathDescription() {
			if (simulationContext != null){
				return "/App(\""+simulationContext.getName()+"\")/Event(\""+BioEvent.this.getName()+"\")";
			}
			return null;
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.bioeventType;
		}
	}
	
	public class EventAssignment implements Matchable, Serializable {
		private SymbolTableEntry target = null;
		private Expression assignmentExpression = null;
		public EventAssignment(SymbolTableEntry argTarget, Expression assignment) throws ExpressionBindingException {
			super();
			this.target = argTarget;
			this.assignmentExpression = assignment;
			assignmentExpression.bindExpression(BioEvent.this.parameterContext);
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
		public void setAssignmentExpression(Expression expr) throws ExpressionBindingException {
			Expression exp = new Expression(expr);
			exp.bindExpression(BioEvent.this.parameterContext);
			this.assignmentExpression = exp; 
		}
		public void rebind() throws ExpressionBindingException {
			// target = mathDescription.getVariable(target.getName());
			assignmentExpression.bindExpression(BioEvent.this.parameterContext);
		}	
	}
	
	public class Delay implements Matchable, Serializable {
		private boolean bUseValuesFromTriggerTime;
		private Expression durationExpression = null;
		
		public Delay(boolean bUseValuesFromTriggerTime, Expression durationExpression) throws ExpressionBindingException {
			this.bUseValuesFromTriggerTime = bUseValuesFromTriggerTime;
			this.durationExpression = durationExpression;
			durationExpression.bindExpression(BioEvent.this.parameterContext);
		}
		
		public Delay(Delay argDelay) {
			this.bUseValuesFromTriggerTime = argDelay.bUseValuesFromTriggerTime;
			this.durationExpression = new Expression(argDelay.durationExpression);
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
		public void rebind() throws ExpressionBindingException {
			durationExpression.bindExpression(BioEvent.this.parameterContext);
		}
	}
	ParameterPolicy parameterPolicy = new BioEventParameterPolicy();
	public final static int ROLE_UserDefined = 0;
	
	private BioEventNameScope nameScope = new BioEventNameScope();
	private ParameterContext parameterContext = new ParameterContext(nameScope, parameterPolicy, new BioEventUnitSystemProvider());
	private String name;
	private Expression triggerExpression = null;
	private Delay delay = null;
	private ArrayList<EventAssignment> eventAssignmentList = new ArrayList<EventAssignment>();

	protected SimulationContext simulationContext = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	
	public BioEvent(String name, SimulationContext simContext) {
		this(name, new Expression(0.0), null, new ArrayList<EventAssignment>(), simContext);		
	}
	
	public BioEvent(String name, Expression triggerExpression, Delay delay, ArrayList<EventAssignment> eventAssignmentList, 
			SimulationContext simContext) {
		super();
		this.name = name; 
		this.triggerExpression = triggerExpression;
		this.delay = delay;
		this.eventAssignmentList = eventAssignmentList;
		simulationContext = simContext;
	}

	public BioEvent(BioEvent argBioEvent, SimulationContext argSimContext) {
		this.simulationContext = argSimContext;
		this.name = argBioEvent.getName();
		this.triggerExpression = new Expression(argBioEvent.getTriggerExpression());
		if (argBioEvent.getDelay() != null) {
			this.delay = new Delay(argBioEvent.getDelay());
		}
		for (int i = 0; i < argBioEvent.getNumEventAssignments(); i++) {
			EventAssignment oldEA = argBioEvent.getEventAssignments().get(i);
			EventAssignment newEA;
			try {
				newEA = new EventAssignment(simulationContext.getEntry(oldEA.getTarget().getName()), new Expression(oldEA.getAssignmentExpression()));
			} catch (ExpressionBindingException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("Error copying event assignment'" + oldEA.getTarget().getName() + "' in event '" + name + "' : " + e.getMessage());
			}
			this.eventAssignmentList.add(newEA);
		}
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

	public ScopedSymbolTable getScopedSymbolTable(){
		return parameterContext;
	}

	public void setName(String newValue) throws PropertyVetoException {
		String oldValue = name;
		fireVetoableChange(PROPERTY_NAME_NAME, oldValue, newValue);
		this.name = newValue;
		firePropertyChange(PROPERTY_NAME_NAME, oldValue, newValue);
	}

	public void setTriggerExpression(Expression triggerExpr) {
		Expression oldValue = triggerExpression;
		this.triggerExpression = triggerExpr;
		firePropertyChange("trigger", oldValue, triggerExpr);
	}
	
	public void setDelay(Delay newDelay) {
		Delay oldValue = delay;
		this.delay = newDelay;
		firePropertyChange("delay", oldValue, newDelay);
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
	
	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
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

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
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
		triggerExpression.bindExpression(parameterContext);
		if (delay != null) {
			delay.rebind();
		}
		for (EventAssignment eventAssignment : eventAssignmentList) {
			eventAssignment.rebind();
		}
	}
	
	public String getName() {
		return name;
	}

	public NameScope getNameScope() {
		return nameScope;
	}
	
	public void vetoableChange(PropertyChangeEvent evt)	throws PropertyVetoException {
		if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_NAME)) {
			String newName = (String) evt.getNewValue();
			if (simulationContext.getBioEvent(newName) != null) {
				throw new PropertyVetoException("An event with name '" + newName + "' already exists!",evt);
			}
			if (simulationContext.getEntry(newName)!=null){
				throw new PropertyVetoException("Cannot use existing symbol '" + newName + "' as an event name",evt);
			}
		}
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
