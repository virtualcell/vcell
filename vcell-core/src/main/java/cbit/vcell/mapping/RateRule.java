package cbit.vcell.mapping;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTableEntry;

public class RateRule implements Matchable, Serializable, IssueSource, Displayable, VetoableChangeListener, PropertyChangeListener {
	private String fieldName = null;
	private SymbolTableEntry rateRuleVar = null;
	private Expression rateRuleExpression = null;
	
	protected SimulationContext simulationContext = null;
	protected transient PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	
	private static final String PROPERTY_NAME_NAME = "name";

	public RateRule(String name, SymbolTableEntry varSTE, SimulationContext simContext) {
		this(name, varSTE, new Expression(0.0), simContext);		
	}
	
	
	public RateRule(String rrName, SymbolTableEntry rateRuleVar, Expression rateRuleExpression, SimulationContext simContext) {
		super();
		this.fieldName = rrName;
		this.rateRuleVar = rateRuleVar;
		this.rateRuleExpression = rateRuleExpression;
		this.simulationContext = simContext;
	}

	public RateRule(RateRule argRateRule, SimulationContext argSimContext) {
		this.simulationContext = argSimContext;
		this.fieldName = argRateRule.getName();
		this.rateRuleVar = argRateRule.getRateRuleVar();
		this.rateRuleExpression = new Expression(argRateRule.getRateRuleExpression());
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

	public String getName() {
		return fieldName;
	}
	
	public void setName(String name) {
		this.fieldName = name;
	}
	
	public SymbolTableEntry getRateRuleVar() {
		return rateRuleVar;
	}
	public void setRateRuleVar(SymbolTableEntry rateRuleVar) {
		this.rateRuleVar = rateRuleVar;
	}
	
	public Expression getRateRuleExpression() {
		return rateRuleExpression;
	}
	
	public SimulationContext getSimulationContext() {
		return simulationContext;
	}
	
	public void setRateRuleExpression(Expression rateRuleExpression) {
		this.rateRuleExpression = rateRuleExpression;
	}
	
	public boolean compareEqual(Matchable obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RateRule)) {
			return false;
		}
		RateRule rateRule = (RateRule)obj;
		
		if (!Compare.isEqual(rateRuleVar.getName(), rateRule.rateRuleVar.getName())) {
			return false;
		}
		if (!Compare.isEqual(rateRuleExpression, rateRule.rateRuleExpression)) {
			return false;
		}

		return true;
	}
	
	public void bind() throws ExpressionBindingException {
		rateRuleExpression.bindExpression(simulationContext);
	}	

	public void vetoableChange(PropertyChangeEvent evt)	throws PropertyVetoException {
		if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_NAME)) {
			String newName = (String) evt.getNewValue();
			if (simulationContext.getRateRule(newName) != null) {
				throw new PropertyVetoException("A rateRule with name '" + newName + "' already exists!",evt);
			}
			if (simulationContext.getModel().getReservedSymbolByName(newName)!=null){
				throw new PropertyVetoException("Cannot use reserved symbol '" + newName + "' as an rateRule name",evt);
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
	
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		if(fieldName == null || fieldName.isEmpty()) {
			String msg = typeName + " Name is missing";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.WARNING));
		}
		if(rateRuleVar == null || rateRuleVar.getName() == null || rateRuleVar.getName().isEmpty()) {
			String msg = typeName + " Variable is missing";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
		} else if(!(rateRuleVar instanceof SymbolTableEntry)) {
			String msg = typeName + " Variable is not a SymbolTableEntry";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.WARNING));
		} else if(rateRuleVar instanceof Structure.StructureSize) {
			String msg = Structure.StructureSize.typeName + " Variable is not supported at this time";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
		} else {						// we know that the rateRuleVar can't be null
			if(simulationContext.getRateRules() != null) {
				for(RateRule rr : simulationContext.getRateRules()) {
					if(rr == this) {
						continue;
					}
					if(rr.getRateRuleVar() == null) {
						continue;		// another rate rule variable may be still not initialized
					}
					if(rateRuleVar.getName().equals(rr.getRateRuleVar().getName())) {
						String msg = typeName + " Variable '" + rateRuleVar.getName() + " is duplicated";
						issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
						break;
					}
				}
			}
			if(simulationContext.getAssignmentRules() != null) {
				for(AssignmentRule ar : simulationContext.getAssignmentRules()) {
					if(ar.getAssignmentRuleVar() == null) {
						continue;		// we may be in the middle of creating the assignment rule and the variable is still missing
					}
					if(rateRuleVar.getName().equals(ar.getAssignmentRuleVar().getName())) {
						String msg = typeName + " Variable '" + rateRuleVar.getName() + "' is duplicated as an " + AssignmentRule.typeName + " Variable";
						issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
						break;
					}
				}
			}
		}
		if(rateRuleExpression == null) {
			String msg = typeName + " Expression is missing";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.WARNING));
		}
	}

	public static final String typeName = "RateRule";
	@Override
	public String getDisplayName() {
		if(fieldName == null) {
			return "";
		}
		return fieldName;
	}
	@Override
	public String getDisplayType() {
		return typeName;
	}

}
