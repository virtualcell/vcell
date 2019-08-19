package cbit.vcell.mapping;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTableEntry;

public class AssignmentRule implements Matchable, Serializable, VetoableChangeListener, PropertyChangeListener{
	private String fieldName = null;
	private SymbolTableEntry assignmentRuleVar = null;
	private Expression assignmentRuleExpression = null;
	
	protected SimulationContext simulationContext = null;
	protected transient PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	
	private static final String PROPERTY_NAME_NAME = "name";

	public AssignmentRule(String name, SymbolTableEntry varSTE, SimulationContext simContext) {
		this(name, varSTE, new Expression(0.0), simContext);		
	}
	public AssignmentRule(String arName, SymbolTableEntry assignmentRuleVar, Expression assignmentRuleExpression, SimulationContext simContext) {
		super();
		this.fieldName = arName;
		this.assignmentRuleVar = assignmentRuleVar;
		this.assignmentRuleExpression = assignmentRuleExpression;
		this.simulationContext = simContext;
	}

	public AssignmentRule(AssignmentRule argAssignmentRule, SimulationContext argSimContext) {
		this.simulationContext = argSimContext;
		this.fieldName = argAssignmentRule.getName();
		this.assignmentRuleVar = argAssignmentRule.getAssignmentRuleVar();
		this.assignmentRuleExpression = new Expression(argAssignmentRule.getAssignmentRuleExpression());
	}
	
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
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

	public String getName() {
		return fieldName;
	}
	public void setName(String name) {
		this.fieldName = name;
	}
	public SymbolTableEntry getAssignmentRuleVar() {
		return assignmentRuleVar;
	}
	public void setAssignmentRuleVar(SymbolTableEntry assignmentRuleVar) {
		this.assignmentRuleVar = assignmentRuleVar;
	}
	public Expression getAssignmentRuleExpression() {
		return assignmentRuleExpression;
	}
	public void setAssignmentRuleExpression(Expression assignmentRuleExpression) {
		this.assignmentRuleExpression = assignmentRuleExpression;
	}
	public SimulationContext getSimulationContext() {
		return simulationContext;
	}
	
	
	public boolean compareEqual(Matchable obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AssignmentRule)) {
			return false;
		}
		AssignmentRule rule = (AssignmentRule)obj;
		
		if (!Compare.isEqual(assignmentRuleVar.getName(), rule.assignmentRuleVar.getName())) {
			return false;
		}
		if (!Compare.isEqual(assignmentRuleExpression, rule.assignmentRuleExpression)) {
			return false;
		}
		return true;
	}
	
	public void bind() throws ExpressionBindingException {
		assignmentRuleExpression.bindExpression(simulationContext);
	}	

	public void vetoableChange(PropertyChangeEvent evt)	throws PropertyVetoException {
		if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_NAME)) {
			String newName = (String) evt.getNewValue();
			if (simulationContext.getAssignmentRule(newName) != null) {
				throw new PropertyVetoException("An AssignmentRule with name '" + newName + "' already exists!", evt);
			}
			if (simulationContext.getModel().getReservedSymbolByName(newName)!=null){
				throw new PropertyVetoException("Cannot use reserved symbol '" + newName + "' as an AssignmentRule name", evt);
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
