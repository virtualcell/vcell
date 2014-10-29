package cbit.vcell.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.mapping.ParameterContext;
import cbit.vcell.mapping.ParameterContext.GlobalParameterContext;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.ParameterPolicy;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.units.UnitSystemProvider;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;

public class RbmKineticLaw implements Serializable, ModelProcessDynamics, Matchable, PropertyChangeListener, IssueSource {
	
	public static enum ParameterType {
		MassActionForwardRate(0,"kf","forward rate"),
		MassActionReverseRate(1,"kr","reverse rate"),
		MichaelisMentenKcat(2,"kcat","enzymatic rate??"),
		MichaelisMentenKm(3,"Km","saturating concentration"),
		SaturableVmax(4,"Vmax","max rate"),
		SaturableKs(5,"Ks","saturating concentration"),
		UserDefined(6,null,"user defined");
		
		private final int role;
		private final String defaultName;
		private final String description;
		
		private ParameterType(int role,String defaultName,String description){
			this.role = role;
			this.defaultName = defaultName;
			this.description = description;
		}
		
		public int getRole(){
			return role;
		}
	
		public String getDefaultName() {
			return defaultName;
		}
	
		public String getDescription() {
			return description;
		}
		
		public static ParameterType fromRole(int role){
			for (ParameterType type : values()){
				if (type.getRole()==role){
					return type;
				}
			}
			return null;
		}
	}

	public static enum RateLawType {
		MassAction,
		MichaelisMenten,
		Saturable
	}
	
	private class ParameterContextSettings implements Serializable, ParameterPolicy, UnitSystemProvider, GlobalParameterContext {

		public boolean isUserDefined(LocalParameter localParameter) {
			return (localParameter.getRole() == RbmKineticLaw.ParameterType.UserDefined.getRole());
		}

		public boolean isExpressionEditable(LocalParameter localParameter) {
			return (localParameter.getExpression()!=null);
		}

		public boolean isNameEditable(LocalParameter localParameter) {
			return true;
		}

		public boolean isUnitEditable(LocalParameter localParameter) {
			return isUserDefined(localParameter);
		}	
		
		public VCUnitSystem getUnitSystem() {
			return reactionRule.getModel().getUnitSystem();
		}
		
		@Override
		public ScopedSymbolTable getSymbolTable() {
			return reactionRule.getModel();
		}
		
		@Override
		public Parameter getParameter(String name) {
			return reactionRule.getModel().getModelParameter(name);
		}
		
		@Override
		public Parameter addParameter(String name, Expression exp, VCUnitDefinition unit) throws PropertyVetoException {
			Model model = reactionRule.getModel();
			return model.addModelParameter(model.new ModelParameter(name, exp, Model.ROLE_UserDefined, unit));
		}

	};
	private ParameterContextSettings parameterContextSettings = new ParameterContextSettings();

	private ReactionRule reactionRule;
	private final RbmKineticLaw.RateLawType rateLawType;
	private transient PropertyChangeSupport propertyChangeSupport;
	
	protected ParameterContext parameterContext = null;

	
	public RbmKineticLaw(final ReactionRule reactionRule, RbmKineticLaw.RateLawType rateLawType) {
		this.reactionRule = reactionRule;
		this.rateLawType = rateLawType;
		this.parameterContext = new ParameterContext(reactionRule.getNameScope(),parameterContextSettings, parameterContextSettings);
		// propagate property change events from parameter context to Kinetic Law listeners.
		parameterContext.addPropertyChangeListener(this);
		ModelUnitSystem modelUnitSystem = reactionRule.getModel().getUnitSystem();
		VCUnitDefinition unit_TBD = modelUnitSystem.getInstance_TBD();

		switch (rateLawType){
		case MassAction: {
			try {
				parameterContext.setLocalParameters(new LocalParameter[] {
					parameterContext.new LocalParameter(RbmKineticLaw.ParameterType.MassActionForwardRate.getDefaultName(), new Expression(0.0), RbmKineticLaw.ParameterType.MassActionForwardRate.getRole(), unit_TBD, RbmKineticLaw.ParameterType.MassActionForwardRate.getDescription()),
					parameterContext.new LocalParameter(RbmKineticLaw.ParameterType.MassActionReverseRate.getDefaultName(), new Expression(0.0), RbmKineticLaw.ParameterType.MassActionReverseRate.getRole(), unit_TBD, RbmKineticLaw.ParameterType.MassActionReverseRate.getDescription()),
				});
			} catch (PropertyVetoException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(),e);
			}
			break;
		}
		case MichaelisMenten: {
			try {
				parameterContext.setLocalParameters(new LocalParameter[] {
						parameterContext.new LocalParameter(RbmKineticLaw.ParameterType.MichaelisMentenKcat.getDefaultName(), new Expression(0.0), RbmKineticLaw.ParameterType.MichaelisMentenKcat.getRole(), unit_TBD, RbmKineticLaw.ParameterType.MichaelisMentenKcat.getDescription()), 
						parameterContext.new LocalParameter(RbmKineticLaw.ParameterType.MichaelisMentenKm.getDefaultName(),   new Expression(0.0), RbmKineticLaw.ParameterType.MichaelisMentenKm.getRole(),   unit_TBD, RbmKineticLaw.ParameterType.MichaelisMentenKm.getDescription()),
				});
			} catch (PropertyVetoException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(),e);
			}
			break;
		}
		case Saturable: {
			try {
				parameterContext.setLocalParameters(new LocalParameter[] {
						parameterContext.new LocalParameter(RbmKineticLaw.ParameterType.SaturableKs.getDefaultName(),   new Expression(0.0), RbmKineticLaw.ParameterType.SaturableKs.getRole(),   unit_TBD, RbmKineticLaw.ParameterType.SaturableKs.getDescription()),
						parameterContext.new LocalParameter(RbmKineticLaw.ParameterType.SaturableVmax.getDefaultName(), new Expression(0.0), RbmKineticLaw.ParameterType.SaturableVmax.getRole(), unit_TBD, RbmKineticLaw.ParameterType.SaturableVmax.getDescription()), 								
				});
			} catch (PropertyVetoException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(),e);
			}
			break;
		}
		default:{
			throw new RuntimeException("unsupported rule-based kinetic law "+rateLawType);
		}
		}
	}
	
	public RbmKineticLaw.RateLawType getRateLawType() {
		return rateLawType;
	}

	public Expression getParameterValue(RbmKineticLaw.ParameterType parameterType) {
		if(parameterContext.getLocalParameterFromRole(parameterType.getRole()) == null) {
			return null;
		}
		return parameterContext.getLocalParameterFromRole(parameterType.getRole()).getExpression();
	}

	public LocalParameter getParameter(RbmKineticLaw.ParameterType parameterType) {
		return parameterContext.getLocalParameterFromRole(parameterType.getRole());
	}

	public void setParameterValue(RbmKineticLaw.ParameterType parameterType, Expression expression) throws ExpressionBindingException, PropertyVetoException {
		parameterContext.getLocalParameterFromRole(parameterType.getRole()).setExpression(expression);
	}
	
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList){
		if (rateLawType==RbmKineticLaw.RateLawType.MassAction && parameterContext.getLocalParameterFromRole(RbmKineticLaw.ParameterType.MassActionForwardRate.getRole()).getExpression() == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.KineticsExpressionError, "Forward Rate is null", Issue.SEVERITY_ERROR));
		}
//		if((reverseRate == null) && (bReversible == true)) {
//			issueList.add(new Issue(this, IssueCategory.KineticsExpressionMissing, "Reverse Rate is null", Issue.SEVERITY_WARNING));
//		}

	}
	
//	public String toString(){
//		return "bad stuff";
//	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof RbmKineticLaw){
			RbmKineticLaw other = (RbmKineticLaw)obj;
			if (getRateLawType() != other.getRateLawType()){
				return false;
			}
			if (!Compare.isEqual(parameterContext,  other.parameterContext)){
				return false;
			}
			return true;
		}
		return false;
	}

	public Parameter[] getKineticsParameters() {
		return parameterContext.getLocalParameters();
	}

	public Parameter[] getProxyParameters() {
		return parameterContext.getProxyParameters();
	}

	public Parameter[] getUnresolvedParameters() {
		return parameterContext.getUnresolvedParameters();
	}

	public void renameParameter(String name, String newName) throws ExpressionException, PropertyVetoException {
		parameterContext.renameLocalParameter(name, newName);
	}

	public void convertParameterType(Parameter param, boolean bConvertToGlobal) throws PropertyVetoException, ExpressionBindingException {
		final Model model = reactionRule.getModel();
		if ((param instanceof LocalParameter) && ((LocalParameter)param).getRole() != RbmKineticLaw.ParameterType.UserDefined.getRole()) {
			throw new RuntimeException("Cannot convert pre-defined local parameter : \'" + param.getName() + "\' to global parameter.");
		}

		parameterContext.convertParameterType(param, bConvertToGlobal, parameterContextSettings);
	}

	public Parameter getKineticsParameter(String name) {
		return parameterContext.getLocalParameterFromName(name);
	}

	public void setParameterValue(LocalParameter parm, Expression exp, boolean autocreateLocalParameter) throws PropertyVetoException, ExpressionException {
		parameterContext.setParameterValue(parm, exp, autocreateLocalParameter);
	}

	public void resolveUndefinedUnits() {
		parameterContext.resolveUndefinedUnits();
	}

	public ScopedSymbolTable getScopedSymbolTable() {
		return parameterContext;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		getPropertyChange().firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}
	
	private PropertyChangeSupport getPropertyChange() {
		if (propertyChangeSupport == null) {
			propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChangeSupport;
	}

	public void bind(ReactionRule reactionRule) throws ExpressionBindingException {
		for(LocalParameter p : parameterContext.getLocalParameters()) {
			p.getExpression().bindExpression(reactionRule.getModel().getRbmModelContainer().getSymbolTable());
		}
		
	}

	public void refreshDependencies() {
		removePropertyChangeListener(this);
//		removeVetoableChangeListener(this);
		addPropertyChangeListener(this);
//		addVetoableChangeListener(this);
		
		reactionRule.removePropertyChangeListener(this);
		reactionRule.addPropertyChangeListener(this);
		
//		for(ReactantPattern p : reactionRule.getReactantPatterns()) {
//			p.removePropertyChangeListener(this);
//			p.addPropertyChangeListener(this);
//		}
//		for(ProductPattern p : reactionRule.getProductPatterns()) {
//			p.removePropertyChangeListener(this);
//			p.addPropertyChangeListener(this);
//		}
	}


}
