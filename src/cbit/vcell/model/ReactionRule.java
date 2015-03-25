package cbit.vcell.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypeMapping;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.common.RbmEventHandler;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.Pair;
import org.vcell.util.TokenMangler;
import org.vcell.util.Displayable;
import org.vcell.util.document.PropertyConstants;

import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

public class ReactionRule implements Serializable, Matchable, ModelProcess, PropertyChangeListener,
	IssueSource, Displayable
	{
	public static int reactionRuleLabelIndex;
	public static ArrayList<String> reactionRuleNames = new ArrayList<String>();

	private String name;
	private boolean bReversible;
	private String comments;
	private List<ReactantPattern> reactantPatterns = new ArrayList<ReactantPattern>();
	private List<ProductPattern> productPatterns = new ArrayList<ProductPattern>();
	private RbmKineticLaw kineticLaw = null;
	private List<MolecularTypeMapping> molecularTypeMappings = new ArrayList<MolecularTypeMapping>();
//	private String reactantWarning = null;
//	private String productWarning = null;
	private Structure structure = null;
	private Model model = null;
	
	private final ReactionRuleNameScope nameScope = new ReactionRuleNameScope();
	public class ReactionRuleNameScope extends BioNameScope {
		private NameScope[] children = new NameScope[0];
		public ReactionRuleNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			return children;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(ReactionRule.this.getName());
		}
		public NameScope getParent() {
			return ReactionRule.this.model.getNameScope();
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return kineticLaw.getScopedSymbolTable();
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.reactionRuleType;
		}
		@Override
		public String getPathDescription(){
			return "Model / ReactionRule("+getName()+")";
		}
	}

	private RbmEventHandler eventHandler = new RbmEventHandler(this);
	public static final String PROPERTY_NAME_NAME = "name";
	public static final String PROPERTY_NAME_REACTANT_PATTERNS = "reactantPatterns";
	public static final String PROPERTY_NAME_PRODUCT_PATTERNS = "productPatterns";
	public static final String PROPERTY_NAME_FORWARD_RATE = "forwardRate";
	public static final String PROPERTY_NAME_REVERSE_RATE = "reverseRate";
	public static final String PROPERTY_NAME_REACTANT_WARNING = "reactantWarning";
	public static final String PROPERTY_NAME_PRODUCT_WARNING = "productWarning";
	public static final Object PROPERTY_NAME_KINETICLAW = "kineticLaw";
	
	public static enum ReactionRuleParticipantType {
		Reactant,
		Product,
	}
	/**
	 * allowable changes from reactant patterns to product patterns:
	 * 
	 * change in a component state
	 * removal of an internal bond within an existing species pattern
	 * adding an internal bond within an existing or new species pattern
	 * removal of a molecule of a molecular type
	 * adding a fully defined molecule
	 * @param label 
	 */
	
	public ReactionRule(Model model, String name, boolean reversible) {
		super();
		this.model = model;
		if((name == null) || name.isEmpty()) {
			this.name = model.getReactionName();
		} else if ((model.getReactionStep(name) == null) && (model.getRbmModelContainer().getReactionRule(name) == null)){
			this.name = name;
		} else {
			this.name = model.getReactionName();
		}
		this.bReversible = reversible;
		this.kineticLaw = new RbmKineticLaw(this,RbmKineticLaw.RateLawType.MassAction);
	}
	
	public void addReactant(ReactantPattern reactant) {
		List<ReactantPattern> newValue = new ArrayList<ReactantPattern>(reactantPatterns);
		newValue.add(reactant);
		setReactantPatterns(newValue);		
	}
	public void removeReactant(ReactantPattern reactant) {
		if (reactantPatterns.contains(reactant)) {
			List<ReactantPattern> newValue = new ArrayList<ReactantPattern>(reactantPatterns);
			newValue.remove(reactant);
			setReactantPatterns(newValue);	
		}
	}
	
	public void setReactantPatterns(List<ReactantPattern> newValue) {
		List<ReactantPattern> oldValue = reactantPatterns;
		if (oldValue != null) {
			for (ReactantPattern rp : oldValue) {
				rp.getSpeciesPattern().removePropertyChangeListener(this);
			}
		}
		reactantPatterns = newValue;
		if (newValue != null) {
			for (ReactantPattern rp : newValue) {
				rp.getSpeciesPattern().addPropertyChangeListener(this);				
			}
			resolveBonds(ReactionRuleParticipantType.Reactant);
		}
		firePropertyChange(ReactionRule.PROPERTY_NAME_REACTANT_PATTERNS, oldValue, newValue);
//		checkReactantPatterns(null);
//		checkProductPatterns(null);
	}
	
	public void checkReactantPatterns(IssueContext issueContext, List<Issue> issueList) {
		issueContext = issueContext.newChildContext(ContextType.ReactionRule, this);
		int cnt = 0;
//		StringBuilder warning = new StringBuilder();
		for (ReactantPattern rp : reactantPatterns) {
			++ cnt;
			if (rp.getSpeciesPattern().getMolecularTypePatterns().size() == 0) {
				String msg = "Reactant " + cnt + " does not have any molecules.\n";
//				warning.append(msg);
				if(issueList != null) {
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
				}
			} else {
				rp.getSpeciesPattern().checkSpeciesPattern(issueContext, issueList);
				for(MolecularTypePattern mtpThis : rp.getSpeciesPattern().getMolecularTypePatterns()) {
					checkComponentStateConsistency(issueContext, issueList, mtpThis);
				}
			}
		}
//		setReactantWarning(warning.length() == 0 ? null : warning.toString());
	}
	public void checkProductPatterns(IssueContext issueContext, List<Issue> issueList) {
		issueContext = issueContext.newChildContext(ContextType.ReactionRule, this);
		int reactantCnt = 0;
		for (ReactantPattern rp : reactantPatterns) {
			reactantCnt += rp.getSpeciesPattern().getMolecularTypePatterns().size();
		}
		int productCnt = 0;
		int cnt = 0;
//		StringBuilder warning = new StringBuilder();
		for (ProductPattern pp : productPatterns) {
			productCnt += pp.getSpeciesPattern().getMolecularTypePatterns().size();
			++ cnt;
			if (pp.getSpeciesPattern().getMolecularTypePatterns().size() == 0) {
				String msg = "Product " + cnt + " does not have any molecules.\n";
//				warning.append(msg);
				if(issueList != null) {
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
				}
			} else {
				pp.getSpeciesPattern().checkSpeciesPattern(issueContext, issueList);
				for(MolecularTypePattern mtpThis : pp.getSpeciesPattern().getMolecularTypePatterns()) {
					checkComponentStateConsistency(issueContext, issueList, mtpThis);
				}
			}
		}
		if(productCnt != reactantCnt) {
			String msg = "The number of molecules in products does not match the number of molecules in reactants.";
//			warning.append(msg);
			if(issueList != null) {
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_INFO));
			}
		}
//		setProductWarning(warning.length() == 0 ? null : warning.toString());
	}
	public void checkComponentStateConsistency(IssueContext issueContext, List<Issue> issueList, MolecularTypePattern mtpThis) {
		if(issueList == null) {
			return;		// this may be called during parsing before the model is consistent 
		}
		issueContext = issueContext.newChildContext(ContextType.ReactionRule, this);
		MolecularType mtThat = mtpThis.getMolecularType();
		for(MolecularComponentPattern mcpThis : mtpThis.getComponentPatternList()) {
			if(mcpThis.isImplied()) {
				continue;
			}
			ComponentStatePattern cspThis = mcpThis.getComponentStatePattern();
			String mcNameThis = mcpThis.getMolecularComponent().getName();
			MolecularComponent[] mcThatList = mtThat.getMolecularComponents(mcNameThis);
			if(mcThatList.length == 0) {
				System.out.println("we already fired an issue about component missing");
				continue;	// nothing to do here, we already fired an issue about component missing 
			} else if(mcThatList.length > 1) {
				String msg = "Multiple " + MolecularComponent.typeName + "s with the same name are not yet supported.";
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_ERROR));
			} else {	// found exactly 1 component
				MolecularComponent mcThat = mcThatList[0];
				List<ComponentStateDefinition> csdListThat = mcThat.getComponentStateDefinitions();
				if(csdListThat.size() == 0) {	// component has no states, we check if mcpThis has any states... it shouldn't
					if(cspThis == null) {
						continue;				// all is well
					}
					if(!cspThis.isAny() || (cspThis.getComponentStateDefinition() != null)) {
						String msg = "Component pattern " + mcNameThis + " is in an invalid State.";
						issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
					}
				} else {						// we check if mcpThis has any of these states... it should!
					if((cspThis == null) || cspThis.isAny() || (cspThis.getComponentStateDefinition() == null)) {
//						String msg = "Component pattern " + mcNameThis + " must be in an explicit State.";
//						issueList.add(new Issue(this, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
					} else {
						String csdNameThis = cspThis.getComponentStateDefinition().getName();
						if(csdNameThis.isEmpty() || (mcThat.getComponentStateDefinition(csdNameThis) == null) ) {
							String msg = "Invalid State " + csdNameThis + " for component pattern " + mcNameThis;
							issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
						}
					}
				}
			}
		}
	}
	
	public void addProduct(ProductPattern product) {
		List<ProductPattern> newValue = new ArrayList<ProductPattern>(productPatterns);
		newValue.add(product);
		setProductPatterns(newValue);		
	}
	
	public void removeProduct(ProductPattern product) {
		if (productPatterns.contains(product)) {
			List<ProductPattern> newValue = new ArrayList<ProductPattern>(productPatterns);
			newValue.remove(product);
			setProductPatterns(newValue);
		}
	}	
	public void setProductPatterns(List<ProductPattern> newValue) {
		List<ProductPattern> oldValue = productPatterns;
		if (oldValue != null) {
			for (ProductPattern pp : oldValue) {
				pp.getSpeciesPattern().removePropertyChangeListener(this);
			}
		}
		productPatterns = newValue;
		if (newValue != null) {
			for (ProductPattern pp : newValue) {
				pp.getSpeciesPattern().addPropertyChangeListener(this);
			}
			resolveBonds(ReactionRuleParticipantType.Product);
		}
		firePropertyChange(ReactionRule.PROPERTY_NAME_PRODUCT_PATTERNS, oldValue, newValue);
//		checkProductPatterns(null);
	}
	public final List<ReactantPattern> getReactantPatterns() {
		return reactantPatterns;
	}
	public final ReactantPattern getReactantPattern(int i) {
		return reactantPatterns.get(i);
	}
	public final List<ProductPattern> getProductPatterns() {
		return productPatterns;
	}
	public final ProductPattern getProductPattern(int i) {
		return productPatterns.get(i);
	}
	
	public final boolean isReversible() {
		return bReversible;
	}
	public final void setReversible(boolean bReversible) {
		this.bReversible = bReversible;
	}
	
	public void resolveBonds(){
		resolveBonds(ReactionRuleParticipantType.Reactant);
		resolveBonds(ReactionRuleParticipantType.Product);
	}
		
	private void resolveBonds(ReactionRuleParticipantType type) {
		HashMap<MolecularType, Integer> moleculeIndexMap = new HashMap<MolecularType, Integer>();
		List<? extends ReactionRuleParticipant> patterns = (type == ReactionRuleParticipantType.Reactant) ? reactantPatterns : productPatterns;
		for (ReactionRuleParticipant participant : patterns) {
			List<MolecularTypePattern> molecularTypePatterns = participant.getSpeciesPattern().getMolecularTypePatterns();
			for (MolecularTypePattern mtp : molecularTypePatterns) {
				Integer index = moleculeIndexMap.get(mtp.getMolecularType());
				if (index == null) {
					index = 1;					
				} else {
					++ index;
				}
				mtp.setIndex(index);
				moleculeIndexMap.put(mtp.getMolecularType(), index);
			}
			participant.getSpeciesPattern().resolveBonds();
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof SpeciesPattern && evt.getPropertyName().equals(SpeciesPattern.PROPERTY_NAME_MOLECULAR_TYPE_PATTERNS)) {
			for (ReactantPattern rp : reactantPatterns) {
				if (rp == evt.getSource()) {
					resolveBonds(ReactionRuleParticipantType.Reactant);
//					checkReactantPatterns(null);
//					checkProductPatterns(null);
					return;
				}
			}
			for (ProductPattern pp : productPatterns) {
				if (pp == evt.getSource()) {
					resolveBonds(ReactionRuleParticipantType.Product);
//					checkProductPatterns(null);
					return;
				}
			}
		}		
	}
	
//	private void setReactantWarning(String newValue) {
//		if (reactantWarning == newValue) {
//			return;
//		}
//		String oldValue = reactantWarning;
//		reactantWarning = newValue;
//		firePropertyChange(ReactionRule.PROPERTY_NAME_REACTANT_WARNING, oldValue, newValue);
//	}
//	
//	private void setProductWarning(String newValue) {
//		if (productWarning == newValue) {
//			return;
//		}
//		String oldValue = productWarning;
//		productWarning = newValue;
//		firePropertyChange(ReactionRule.PROPERTY_NAME_PRODUCT_WARNING, oldValue, newValue);
//	}
	
	public List<MolecularTypePattern> getMissingMoleculesInProducts() {
		List<MolecularTypePattern> patterns = new ArrayList<MolecularTypePattern>();
		for (ReactantPattern rp : reactantPatterns) {
			for (MolecularTypePattern rmtp : rp.getSpeciesPattern().getMolecularTypePatterns()) {
				boolean bFound = false;
				for (ProductPattern pp : productPatterns) {
					for (MolecularTypePattern pmtp : pp.getSpeciesPattern().getMolecularTypePatterns()) {
						if (rmtp.getMolecularType() == pmtp.getMolecularType() && rmtp.getIndex() == pmtp.getIndex()) {
							bFound = true;
							break;
						}
					}
					if (bFound) {
						break;
					}
				}
				if (!bFound) {
					patterns.add(rmtp);
				}
			}
		}
		return patterns;
	}

//	public String getId() {
//		System.err.println("ReactionRule id generated badly ...");
//		return "ReactionRule_"+hashCode();
//	}
	
	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof ReactionRule)) {
			return false;
		}
		ReactionRule that = (ReactionRule)aThat;

		if (!Compare.isEqual(name, that.name)){
			return false;
		}
		if(!(bReversible == that.bReversible)) {
			return false;
		}
		if (!Compare.isEqual(comments, that.comments)){
			return false;
		}
		if (!Compare.isEqual(kineticLaw, that.kineticLaw)){
			return false;
		}
		if (!Compare.isEqual(productPatterns, that.productPatterns)){
			return false;
		}
		if (!Compare.isEqual(reactantPatterns, that.reactantPatterns)){
			return false;
		}
		return true;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		eventHandler.addPropertyChangeListener(listener);
	}

	public void addVetoableChangeListener(VetoableChangeListener listener) {
		eventHandler.addVetoableChangeListener(listener);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		eventHandler.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
		eventHandler.fireVetoableChange(propertyName, oldValue, newValue);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		eventHandler.removePropertyChangeListener(listener);
	}

	public void removeVetoableChangeListener(VetoableChangeListener listener) {
		eventHandler.removeVetoableChangeListener(listener);
	}

	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		issueContext = issueContext.newChildContext(ContextType.ReactionRule, this);
		if(name == null || name.isEmpty()) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Label is missing", Issue.SEVERITY_ERROR));
		}
		if(reactantPatterns == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Reactant Pattern is null", Issue.SEVERITY_ERROR));
		} else {
			checkReactantPatterns(issueContext, issueList);
			for (ReactantPattern rp : reactantPatterns) {
				issueContext = issueContext.newChildContext(ContextType.ReactionRule, this);
				rp.getSpeciesPattern().gatherIssues(issueContext, issueList);				
			}
		}
		if(productPatterns == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Product Pattern is null", Issue.SEVERITY_ERROR));
		} else {
			checkProductPatterns(issueContext, issueList);
			for (ProductPattern pp : productPatterns) {
				issueContext = issueContext.newChildContext(ContextType.ReactionRule, this);
				pp.getSpeciesPattern().gatherIssues(issueContext, issueList);				
			}
		}
		kineticLaw.gatherIssues(issueContext, issueList);
		if(molecularTypeMappings == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.KineticsExpressionMissing, MolecularType.typeName + " Mapping is null", Issue.SEVERITY_WARNING));
		}


	}

	public RbmKineticLaw getKineticLaw() {
		return this.kineticLaw;
	}
	public void setKineticLaw(RbmKineticLaw kineticLaw) {
		this.kineticLaw = kineticLaw;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String newValue)  throws PropertyVetoException {
		String oldValue = name;
		fireVetoableChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
		this.name = newValue;
		firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
		
	}

	public boolean containsSearchText(String lowerCaseSearchText) {
		if (getName().toLowerCase().contains(lowerCaseSearchText)){
			return true;
		}
		if (RbmUtils.toBnglStringLong(this).toLowerCase().contains(lowerCaseSearchText)){
			return true;
		}
		return false;
	}

	public Structure getStructure() {
		return structure;
	}

	public ModelProcessDynamics getDynamics() {
		return kineticLaw;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
	}

	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter() {
		AutoCompleteSymbolFilter stef = new AutoCompleteSymbolFilter() {		
			public boolean accept(SymbolTableEntry ste) {		
				if (ste instanceof StructureSize) {
					if (((StructureSize)ste).getStructure() != structure) {
						return false;
					}
				} else {			
					if (structure instanceof Membrane) {
						Membrane membrane = (Membrane)structure;				
						StructureTopology structTopology = getModel().getStructureTopology();
						if (ste instanceof SpeciesContext) {	
							Structure entryStructure = ((SpeciesContext)ste).getStructure();
							if (entryStructure != membrane && entryStructure != structTopology.getInsideFeature(membrane) && entryStructure != structTopology.getOutsideFeature(membrane)) {
								return false;
							}
						} else if (ste instanceof MembraneVoltage) {
							if (((MembraneVoltage)ste).getMembrane() != membrane) {
								return false;
							}
						}					
					} else {
						if (ste instanceof SpeciesContext) {
							Structure entryStructure = ((SpeciesContext)ste).getStructure();
							if (entryStructure != structure) {
								return false;
							}
						} else if (ste instanceof MembraneVoltage) {
							return false;
						}
					}
				}
				return true;
			}
			public boolean acceptFunction(String funcName) {
				return true;
			}
		};
		return stef;
	}

	public Model getModel() {
		return model;
	}
	
	public BioNameScope getNameScope() {
		return nameScope;
	}

	public void rebindAllToModel(Model model) throws ExpressionBindingException {
		this.model = model;
		getKineticLaw().bind(this);
	}

	public void refreshDependencies() {
		getKineticLaw().refreshDependencies();
//		for(ReactantPattern p : getReactantPatterns()) {
//			p.removeVetoableChangeListener(this);
//			p.addVetoableChangeListener(this);
//			p.refreshDependencies();
//		}
//		for(ProductPattern p : getProductPatterns()) {
//			p.removeVetoableChangeListener(this);
//			p.addVetoableChangeListener(this);
//			p.refreshDependencies();
//		}
		removePropertyChangeListener(this);
		addPropertyChangeListener(this);
//		removeVetoableChangeListener(this);
//		addVetoableChangeListener(this);

	}

	//TODO: almost identical to findStateUsage() below - pay attention to keep both in sync
	public void findComponentUsage(MolecularType mt, MolecularComponent mc, Map<String, Pair<Displayable, SpeciesPattern>> usedHere) {
		for(ProductPattern pp : getProductPatterns()) {
			SpeciesPattern sp = pp.getSpeciesPattern();
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				if(mtp.getMolecularType() == mt) {
					List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
					for (MolecularComponentPattern mcp : componentPatterns) {
						if (mcp.isImplied()) {			// we don't care about these
							continue;
						}
						if(mcp.getMolecularComponent() == mc) {		// found mc in use
							String key = getDisplayType() + getDisplayName() + sp.getDisplayName();
							usedHere.put(key, new Pair<Displayable, SpeciesPattern>(this, sp));
						}
					}
				}
			}
		}
		for(ReactantPattern rp : getReactantPatterns()) {
			SpeciesPattern sp = rp.getSpeciesPattern();
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				if(mtp.getMolecularType() == mt) {
					List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
					for (MolecularComponentPattern mcp : componentPatterns) {
						if (mcp.isImplied()) {
							continue;
						}
						if(mcp.getMolecularComponent() == mc) {
							String key = getDisplayType() + getDisplayName() + sp.getDisplayName();
							usedHere.put(key, new Pair<Displayable, SpeciesPattern>(this, sp));
						}
					}
				}
			}
		}
	}
	public void findStateUsage(MolecularType mt, MolecularComponent mc, ComponentStateDefinition csd,
			Map<String, Pair<Displayable, SpeciesPattern>> usedHere) {
		for(ProductPattern pp : getProductPatterns()) {
			SpeciesPattern sp = pp.getSpeciesPattern();
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				if(mtp.getMolecularType() == mt) {
					List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
					for (MolecularComponentPattern mcp : componentPatterns) {
						if (mcp.isImplied()) {			// we don't care about these
							continue;
						}
						if(mcp.getMolecularComponent() == mc) {		// found mc in use
							// now let's look at component state definition
							ComponentStatePattern csp = mcp.getComponentStatePattern();
							if(csp == null) {
								continue;
							}
							if(csp.getComponentStateDefinition() == csd) {
								String key = getDisplayType() + getDisplayName() + sp.getDisplayName();
								usedHere.put(key, new Pair<Displayable, SpeciesPattern>(this, sp));
							}
						}
					}
				}
			}
		}
		for(ReactantPattern rp : getReactantPatterns()) {
			SpeciesPattern sp = rp.getSpeciesPattern();
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				if(mtp.getMolecularType() == mt) {
					List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
					for (MolecularComponentPattern mcp : componentPatterns) {
						if (mcp.isImplied()) {
							continue;
						}
						if(mcp.getMolecularComponent() == mc) {
							ComponentStatePattern csp = mcp.getComponentStatePattern();
							if(csp == null) {
								continue;
							}
							if(csp.getComponentStateDefinition() == csd) {
								String key = getDisplayType() + getDisplayName() + sp.getDisplayName();
								usedHere.put(key, new Pair<Displayable, SpeciesPattern>(this, sp));
							}
						}
					}
				}
			}
		}
	}
	
	public boolean deleteComponentFromPatterns(MolecularType mt, MolecularComponent mc) {
		for(ProductPattern pp : getProductPatterns()) {
			SpeciesPattern sp = pp.getSpeciesPattern();
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				if(mtp.getMolecularType() == mt) {
					List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
					for (Iterator<MolecularComponentPattern> iterator = componentPatterns.iterator(); iterator.hasNext();) {
						MolecularComponentPattern mcp = iterator.next();
						if (mcp.getMolecularComponent() == mc) {
							iterator.remove();
						}
					}					
				}
			}
			sp.resolveBonds();
		}
		for(ReactantPattern rp : getReactantPatterns()) {
			SpeciesPattern sp = rp.getSpeciesPattern();
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				if(mtp.getMolecularType() == mt) {
					List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
					for (Iterator<MolecularComponentPattern> iterator = componentPatterns.iterator(); iterator.hasNext();) {
						MolecularComponentPattern mcp = iterator.next();
						if (mcp.getMolecularComponent() == mc) {
							iterator.remove();
						}
					}					
				}
			}
			sp.resolveBonds();
		}
		return true;
	}
	public boolean deleteStateFromPatterns(MolecularType mt, MolecularComponent mc, ComponentStateDefinition csd) {
		for(ProductPattern pp : getProductPatterns()) {
			SpeciesPattern sp = pp.getSpeciesPattern();
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				if(mtp.getMolecularType() == mt) {
					List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
					for(MolecularComponentPattern mcp : componentPatterns) {
						if (!(mcp.getMolecularComponent() == mc)) {
							continue;	// not our mc
						}
						ComponentStatePattern csp = mcp.getComponentStatePattern();
						if(csp == null || csp.isAny()) {
							continue;	// no state to delete
						}
						if(csp.getComponentStateDefinition() == csd) {
							if(mc.getComponentStateDefinitions().size() == 1) {
								// we are about to delete the last possible state, so we set the ComponentStatePattern to null
								mcp.setComponentStatePattern(null);
							} else {
								// some other state is still available, we set the ComponentStatePattern to Any and let the user deal with it
								csp = new ComponentStatePattern();
								mcp.setComponentStatePattern(csp);
							}
						}
					}
				}
			}
		}
		for(ReactantPattern rp : getReactantPatterns()) {
			SpeciesPattern sp = rp.getSpeciesPattern();
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				if(mtp.getMolecularType() == mt) {
					List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
					for(MolecularComponentPattern mcp : componentPatterns) {
						if (!(mcp.getMolecularComponent() == mc)) {
							continue;
						}
						ComponentStatePattern csp = mcp.getComponentStatePattern();
						if(csp == null || csp.isAny()) {
							continue;
						}
						if(csp.getComponentStateDefinition() == csd) {
							if(mc.getComponentStateDefinitions().size() == 1) {
								mcp.setComponentStatePattern(null);
							} else {
								csp = new ComponentStatePattern();
								mcp.setComponentStatePattern(csp);
							}
						}
					}
				}
			}
		}
		return true;
	}


	private static final String typeName = "Reaction Rule";
	@Override
	public final String getDisplayName() {
		return getName();
	}
	@Override
	public final String getDisplayType() {
		return typeName;
	}


}

