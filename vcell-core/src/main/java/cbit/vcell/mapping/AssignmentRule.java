package cbit.vcell.mapping;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;

import cbit.vcell.mapping.SimulationContext.Kind;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.VCellSbmlName;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTableEntry;

public class AssignmentRule implements Matchable, Serializable, IssueSource, SimulationContextEntity,
		Displayable, VetoableChangeListener, PropertyChangeListener, VCellSbmlName {
	
	private String fieldName = null;
	private String sbmlId = null;
	private String sbmlName = null;
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
	public String getSbmlId() {
		return sbmlId;
	}
	public String getSbmlName() {
		return sbmlName;
	}
	public void setName(String name) {
		this.fieldName = name;
	}
	public void setSbmlName(String newString) throws PropertyVetoException {
		String oldValue = this.sbmlName;
		String newValue = SpeciesContext.fixSbmlName(newString);
		
		fireVetoableChange("sbmlName", oldValue, newValue);
		this.sbmlName = newValue;
		firePropertyChange("sbmlName", oldValue, newValue);
	}
	public void setSbmlId(String newString) throws PropertyVetoException {	// setable only through SBML import
		this.sbmlId = newString;
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
		if (!Compare.isEqualOrNull(getSbmlName(), rule.getSbmlName())) {
			return false;
		}
		if (!Compare.isEqualOrNull(getSbmlId(), rule.getSbmlId())) {
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
		} else if(evt.getSource() == this && evt.getPropertyName().equals("sbmlId")) {
			;		// not editable, we use what we get from the importer
		} else if(evt.getSource() == this && evt.getPropertyName().equals("sbmlName")) {
			String newName = (String)evt.getNewValue();
			if(newName == null) {
				return;
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
	}

	public void refreshDependencies() {
		try {
			bind();
		} catch (ExpressionBindingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void gatherIssues(IssueContext issueContext, List<Issue> issueList, Set<String> alreadyIssue) {
//		issueContext = issueContext.newChildContext(ContextType.SpeciesContext, this);
		// if we find an issue with the current rule we post it and stop looking for more
		if(fieldName == null || fieldName.isEmpty()) {
			String msg = typeName + " Name is missing";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			return;
		}
		if(assignmentRuleVar == null || assignmentRuleVar.getName() == null || assignmentRuleVar.getName().isEmpty()) {
			String msg = typeName + " Variable is missing";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			return;
		}
		if(assignmentRuleVar instanceof Structure.StructureSize) {
			String msg = Structure.StructureSize.typeName + " Variable is not supported at this time for AssignmentRules";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			return;
		}
		if(assignmentRuleExpression == null) {
			String msg = typeName + " Expression is missing";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			return;
		}
		if(assignmentRuleExpression != null) {
			String[] symbols = assignmentRuleExpression.getSymbols();
			if(symbols != null && symbols.length > 0) {
				for(String symbol : symbols) {
					SymbolTableEntry ste = simulationContext.getEntry(symbol);
					if(ste == null) {
						String msg = "Missing Symbol '" + symbol + "' in Expression for " + typeName + " '" + fieldName + "'.";
						issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
						return;
					}
					if(symbol.contentEquals(assignmentRuleVar.getName())) {
						String msg = "An " + typeName + " Variable cannot be part of its Expression.";
						issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
						return;
					}
				}
			}
		}
		// now we look into errors caused by interaction with other rules
		// TODO: should also look into interaction with events!
		if(simulationContext.getAssignmentRules() != null) {
			for(AssignmentRule ar : simulationContext.getAssignmentRules()) {
				if(ar == this) {
					continue;
				}
				if(ar.getAssignmentRuleVar() == null) {
					continue;
				}
				String ruleVariableName = assignmentRuleVar.getName();
				if(!alreadyIssue.contains(ruleVariableName) && ruleVariableName.equals(ar.getAssignmentRuleVar().getName())) {
					String msg = typeName + " Variable '" + assignmentRuleVar.getName() + " is duplicated in " + ar.getDisplayName();
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
					alreadyIssue.add(ruleVariableName);
					return;
				}
			}
		}
		if(simulationContext.getRateRules() != null) {
		// the code below is commented out for rate rules, so that we don't have 2 mirrored issues about the same duplicated variable
			for(RateRule rr : simulationContext.getRateRules()) {
				if(rr.getRateRuleVar() == null) {
					continue;		// we may be in the middle of creating the assignment rule and the variable is still missing
				}
				if(assignmentRuleVar.getName().equals(rr.getRateRuleVar().getName())) {
					String msg = typeName + " Variable '" + assignmentRuleVar.getName() + "' is duplicated as a " + RateRule.typeName + " Variable";
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
					return;
				}
			}
		}
		if(!(assignmentRuleVar instanceof SymbolTableEntry)) {
			String msg = typeName + " Variable is not a SymbolTableEntry";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			return;
		}
		if(sbmlName != null && sbmlName.isEmpty()) {
			String message = "SbmlName cannot be an empty string.";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
		}
		if(sbmlId != null && sbmlId.isEmpty()) {
			String message = "SbmlId cannot be an empty string.";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
		}
	}

	public static final String typeName = "AssignmentRule";
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
	
	@Override
	public Kind getSimulationContextKind() {
		return SimulationContext.Kind.PROTOCOLS_KIND;
	}

}
