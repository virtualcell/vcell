package cbit.vcell.mapping;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.mapping.SimulationContext.Kind;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.VCellSbmlName;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTableEntry;

public class RateRule implements Matchable, Serializable, IssueSource, SimulationContextEntity,
			Displayable, VetoableChangeListener, PropertyChangeListener, VCellSbmlName {
	
	private String fieldName = null;
	private String sbmlId = null;
	private String sbmlName = null;
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
		RateRule rule = (RateRule)obj;
		
		if (!Compare.isEqual(rateRuleVar.getName(), rule.rateRuleVar.getName())) {
			return false;
		}
		if (!Compare.isEqualOrNull(getSbmlName(), rule.getSbmlName())) {
			return false;
		}
		if (!Compare.isEqualOrNull(getSbmlId(), rule.getSbmlId())) {
			return false;
		}
		if (!Compare.isEqual(rateRuleExpression, rule.rateRuleExpression)) {
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
		// if we find an issue with the current rule we post it and stop looking for more
		if(fieldName == null || fieldName.isEmpty()) {
			String msg = typeName + " Name is missing";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			return;
		}
		if(rateRuleVar == null || rateRuleVar.getName() == null || rateRuleVar.getName().isEmpty()) {
			String msg = typeName + " Variable is missing";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			return;
		}
		if(rateRuleVar instanceof Structure.StructureSize) {
			String msg = Structure.StructureSize.typeName + " Variable is not supported at this time for RateRules";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			return;
		}
		if(!(rateRuleVar instanceof SymbolTableEntry)) {
			String msg = typeName + " Variable is not a SymbolTableEntry";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			return;
		}
		if(rateRuleExpression == null) {
			String msg = typeName + " Expression is missing";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			return;
		}
		if(rateRuleExpression != null) {
			String[] symbols = rateRuleExpression.getSymbols();
			if(symbols != null && symbols.length > 0) {
				for(String symbol : symbols) {
					SymbolTableEntry ste = simulationContext.getEntry(symbol);
					if(ste == null) {
						String msg = "Missing Symbol '" + symbol + "' in Expression for " + typeName + " '" + fieldName + "'.";
						issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
						return;
					}
				}
			}
		}
		// now we look into errors caused by interaction with other rules
		// TODO: should also look into interaction with events!
		if(simulationContext.getRateRules() != null) {
			for(RateRule rr : simulationContext.getRateRules()) {
				if(rr == this) {
					continue;
				}
				if(rr.getRateRuleVar() == null) {
					continue;		// another rate rule variable may be still not initialized
				}
				String ruleVariableName = rateRuleVar.getName();
				if(!alreadyIssue.contains(ruleVariableName) && ruleVariableName.equals(rr.getRateRuleVar().getName())) {
					String msg = typeName + " Variable '" + rateRuleVar.getName() + " is duplicated in " + rr.getDisplayName();
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
					alreadyIssue.add(ruleVariableName);
					return;
				}
			}
		}
		// we do the following check for the assignemnt rule too, so there's no point in complaining here too about the same problem
		// TODO: uncomment to raise an issue here as well
//		if(simulationContext.getAssignmentRules() != null) {
//			for(AssignmentRule ar : simulationContext.getAssignmentRules()) {
//				if(ar.getAssignmentRuleVar() == null) {
//					continue;		// we may be in the middle of creating the assignment rule and the variable is still missing
//				}
//				if(rateRuleVar.getName().equals(ar.getAssignmentRuleVar().getName())) {
//					String msg = typeName + " Variable '" + rateRuleVar.getName() + "' is duplicated as an " + AssignmentRule.typeName + " Variable";
//					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
//					return;
//				}
//			}
//		}
		// TODO: the following code belongs to SpeciesContextSpec rather than here (so that the Issue navigates to the Specifications / Species panel
		// rate rule variable must not be also a reactant or a product in a reaction
		ReactionContext rc = getSimulationContext().getReactionContext();
		ReactionSpec[] rsArray = rc.getReactionSpecs();
		if(rateRuleVar instanceof SpeciesContext) {
			for(ReactionSpec rs : rsArray) {
				if(rs.isExcluded()) {
					continue;		// we don't care about reactions which are excluded
				}
				ReactionStep step = rs.getReactionStep();
				for(ReactionParticipant p : step.getReactionParticipants()) {
					if(p instanceof Product || p instanceof Reactant) {
						SpeciesContext sc = p.getSpeciesContext();
						SpeciesContextSpec scs = rc.getSpeciesContextSpec(sc);
						if(!scs.isConstant() && sc.getName().equals(rateRuleVar.getName())) {
							String msg = "'" + rateRuleVar.getName() + "' must be clamped to be both a rate rule variable and a participant in reaction '" + step.getDisplayName() + "'.";
							issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, msg, Issue.Severity.ERROR));
							return;
						}
					}
				}
			}
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

	@Override
	public Kind getSimulationContextKind() {
		return SimulationContext.Kind.PROTOCOLS_KIND;
	}

}
