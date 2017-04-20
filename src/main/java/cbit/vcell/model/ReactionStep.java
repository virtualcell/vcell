/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vcell.util.BeanUtils;
import org.vcell.util.Cacheable;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.VCAssert;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.Issue.Severity;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.Identifiable;
import org.vcell.util.document.KeyValue;

import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Model.ElectricalTopology;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;
/**
 * This class is the superclass of all classes representing 
 * a step within a <code>Reaction</code>. This encapsulates capability for
 * each <code>Expression</code> within a subclass to be bound to the <code>Reaction</code>.
 * <p>
 *
 * @see     cbit.vcell.model.FluxReaction
 * @see     cbit.vcell.model.SimpleReaction
 * @since   VCELL1.0
 */
@SuppressWarnings("serial")
public abstract class ReactionStep implements ModelProcess, Model.ElectricalTopologyListener,
		Cacheable, Serializable, ScopedSymbolTable, Matchable, VetoableChangeListener, PropertyChangeListener, Identifiable, 
		IssueSource, Displayable
{

	public static final String PROPERTY_NAME_REACTION_PARTICIPANTS = "reactionParticipants";

	public static final String PROPERTY_NAME_KINETICS = "kinetics";
	
	/**
	 * not suitable for second order membrane discussion
	 */
	public static final String MASS_ACTION_ONLINE_DISCUSSION = "https://groups.google.com/forum/#!topic/vcell-discuss/bFgz-pItqMY";
			
	private String annotation = null;
	
	
	public final static int PHYSICS_MOLECULAR_ONLY = 0;
	public final static int PHYSICS_MOLECULAR_AND_ELECTRICAL = 1;
	public final static int PHYSICS_ELECTRICAL_ONLY = 2;

	private int fieldPhysicsOptions = PHYSICS_MOLECULAR_ONLY;
	
	private boolean bReversible = true;

	private KeyValue key = null;
	
	/**
	 * The Structure object that this ReactionStep belongs to.
	 */
   private Structure structure = null;
   
	private String fieldName = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private Kinetics fieldKinetics = null;
	private transient Model model = null;
	private ReactionParticipant[] fieldReactionParticipants = new ReactionParticipant[0];
	private ReactionNameScope nameScope = null; // see constructor

	public class ReactionNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public ReactionNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(ReactionStep.this.getName());
		}
		public NameScope getParent() {
			if (ReactionStep.this.model != null){
				return ReactionStep.this.model.getNameScope();
			}else{
				return null;
			}
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return ReactionStep.this;
		}
		
		@Override
		public String getPathDescription() {
			return "Model / Reaction("+ReactionStep.this.getName()+")";
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.reactionStepType;
		}
	}
	
/**
 * ReactionStep constructor comment.
 */
protected ReactionStep(Model model, Structure structure, KeyValue key, String name, boolean bReversible, String annotation) throws java.beans.PropertyVetoException {
	super();
	
	if (model != null) {
		this.model = model;
	} else {
		throw new RuntimeException("Model cannot be null");
	}
	this.bReversible = bReversible;
	nameScope = new ReactionStep.ReactionNameScope();
	setStructure(structure);
	this.key = key;
	removePropertyChangeListener(this);
	addPropertyChangeListener(this);
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
	setName(name);
	this.annotation = annotation;
}

protected ReactionStep(Model model, Structure structure, KeyValue key, String name, boolean bReversible) throws java.beans.PropertyVetoException {
	this(model, structure,key,name,bReversible,null);
}
/**
 * ReactionStep constructor comment.
 */
protected ReactionStep(Model model, Structure structure, String name, boolean bReversible) throws PropertyVetoException {
	this(model, structure,null,name,bReversible);
}

public void addCatalyst(SpeciesContext speciesContext) throws ModelException, PropertyVetoException {

	// NOTE : Currently, we are not allowing the case where a reactionParticipant is a reactant and/or product AND a catalyst
	// Hence, if the rps array is not null, throw an exception, since the speciesContext is already a reactionParticipant.
	
	if (countNumReactionParticipants(speciesContext) == 0){
		addReactionParticipant(new Catalyst(null,this, speciesContext));
	}else{
		throw new ModelException("reactionParticipant already defined as Reactant and/or Product in the reaction.");
	}		
}  

public int countNumReactionParticipants(SpeciesContext speciesContext) {
	int count = 0;
	for (ReactionParticipant rp : fieldReactionParticipants) {
		if (rp.getSpeciesContext() == speciesContext) {
			count ++;
		}
	}
	return count;
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
public void addReactionParticipant(ReactionParticipant reactionParticipant) throws PropertyVetoException {
	if (!contains(reactionParticipant)){
		try{
			ReactionParticipant newReactionParticipants[] = (ReactionParticipant[])BeanUtils.addElement(fieldReactionParticipants, reactionParticipant);
			setReactionParticipants(newReactionParticipants);
		}catch (Exception e){
			e.printStackTrace(System.out);
			System.out.println("exception: error adding reactionParticipant to reactionStep ..."+e.getMessage());
		}
	}	
}            
/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
protected boolean compareEqual0(ReactionStep rs) {
	if (rs == null) {
		return false;
	}
	if (!getName().equals(rs.getName())){
		return false;
	}
	
	if (!getStructure().compareEqual(rs.getStructure())){
		return false;
	}
	
	if (!getKinetics().compareEqual(rs.getKinetics())) {
		return false;
	}

	if (fieldPhysicsOptions != rs.fieldPhysicsOptions){
		return false;
	}
	
	if (bReversible != rs.bReversible){
		return false;
	}
	
	if (!Compare.isEqual(fieldReactionParticipants, rs.fieldReactionParticipants)) {
		return false;
	}
	if(!Compare.isEqualOrNull(getAnnotation(), rs.getAnnotation())){
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (6/5/2002 3:18:46 PM)
 * @return boolean
 * @param reactionparticipant cbit.vcell.model.ReactionParticipant
 */
public boolean contains(ReactionParticipant reactionparticipant) {
	for (int i=0;i<fieldReactionParticipants.length;i++){
		if (fieldReactionParticipants[i].equals(reactionparticipant)){
			return true;
		}
	}
	return false;
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws java.beans.PropertyVetoException {
    getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 10:26:42 PM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
	issueContext = issueContext.newChildContext(ContextType.ReactionStep,this);
	if (fieldKinetics!=null){
		fieldKinetics.gatherIssues(issueContext,issueList);
	}
	if(fieldName != null && fieldName.startsWith("_reverse_")) {
		String msg = "The prefix '_reverse_' is a BioNetGen reserved keyword. Please rename the " + getDisplayType() + ".";
		issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
	}

	if (structure instanceof Membrane){
		if(fieldKinetics instanceof MassActionKinetics) {
			if((getNumReactants() > 1) || (getNumProducts() > 1)) {
				if (isForMembraneMembraneMassAction()) {
					Issue issue = new Issue(this, issueContext, IssueCategory.KineticsExpressionError, 
							"A mass action rate law is not physically correct for bimolecular reactions in membranes (see more).",
							Severity.WARNING);
					issue.setHyperlink(MASS_ACTION_ONLINE_DISCUSSION);
					issueList.add(issue);
				}
			}
		}
		if(getPhysicsOptions() == PHYSICS_ELECTRICAL_ONLY || getPhysicsOptions() == PHYSICS_MOLECULAR_AND_ELECTRICAL){
			Feature negFeature = getModel().getElectricalTopology().getNegativeFeature((Membrane)structure);
			Feature posFeature = getModel().getElectricalTopology().getPositiveFeature((Membrane)structure);
			if(negFeature == null || posFeature == null){
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers,
					"Reaction '"+getName()+"' electrical structure '"+structure.getName()+"' negative and positive compartment must be set",
					Issue.Severity.ERROR));
			}else{
				for(ReactionParticipant rxParticipant:getReactionParticipants()){
					if(rxParticipant.getStructure() != negFeature && rxParticipant.getStructure() != posFeature && rxParticipant.getStructure() != structure){
						String msg = "Check STRUCTURE '"+structure.getName()+"' has pos/neg feature settings appropriate for reaction '"+getName()+"' participants";
						issueList.add(new Issue(structure, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));					
					}
				}
			}
		}
	}
	
	if(fieldKinetics instanceof MassActionKinetics) {
		if(getNumReactants() == 0) {
			String msg = "With Mass Action kinetics this reaction will be interpreted as a degradation of the product.";
			String tool = "Use General Kinetics if you want the product to be generated. As is now this reaction is interpreted as a degradation.";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tool, Issue.Severity.WARNING));
		}
	}
}

/**
 * @param rp not null
 * @return true if rp is a {@link Reactant} on a {@link Membrane}
 */
private boolean isMembraneReactant(ReactionParticipant rp) {
	if (rp instanceof Reactant) {
		SpeciesContext sc = rp.getSpeciesContext();
		Structure struct = sc.getStructure();
		return struct instanceof Membrane;
	}
	return false;
	
}
/**
 * @return true if another participant is also a membrane
 */
private boolean isForMembraneMembraneMassAction( ) {
	VCAssert.precondition(structure instanceof Membrane);
	VCAssert.precondition(fieldKinetics instanceof MassActionKinetics);
	int membraneReactants = 0;
	for ( ReactionParticipant rp : fieldReactionParticipants) {
		if (isMembraneReactant(rp)) {
			if (++membraneReactants == 2) {
				return true;
			}
		}
	}
	return false;
}

public SymbolTableEntry getEntry(String identifier) {
	SymbolTableEntry localSTE = getLocalEntry(identifier);
	if (localSTE != null && !(localSTE instanceof Kinetics.UnresolvedParameter)){
		return localSTE;
	}
	Kinetics.UnresolvedParameter unresolvedParameter = (Kinetics.UnresolvedParameter)localSTE;
			
	SymbolTableEntry externalSTE = getNameScope().getExternalEntry(identifier,this);

	if (externalSTE instanceof SymbolTableFunctionEntry){
		return externalSTE;
	}
	//
	// external ste is null and found unresolved parameter, then return unresolved parameter.  
	// (external entry overrides unresolved parameter).
	//
	if (externalSTE!=null){
		if (unresolvedParameter!=null){
			getKinetics().removeUnresolvedParameter(unresolvedParameter);
		}
		return getKinetics().addProxyParameter(externalSTE);
	}else if (unresolvedParameter != null){
		return unresolvedParameter;
	}
	//
	// if all else fails, try reserved symbols
	//
	if (getModel() != null) {
		SymbolTableEntry reservedSTE = getModel().getReservedSymbolByName(identifier);
		if (reservedSTE != null){
			if (reservedSTE.equals(getModel().getX()) || reservedSTE.equals(getModel().getY()) || reservedSTE.equals(getModel().getZ())){
				throw new RuntimeException("x, y or z can not be used in the Reaction Editor. " 
						+ "They are reserved as spatial variables and Physiological Models must be spatially independent.");
			}
			return getKinetics().addProxyParameter(reservedSTE);
		}
	}

	return null;
}   

public Model getModel() {
	return model;
}

/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public KeyValue getKey() {
	return key;
}
/**
 * Gets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @return The kinetics property value.
 * @see #setKinetics
 */
public Kinetics getKinetics() {
	return fieldKinetics;
}
public SymbolTableEntry getLocalEntry(String identifier) {
	//
	// if resolved parameter exists, then return it
	//
	SymbolTableEntry ste = getKinetics().getKineticsParameter(identifier);
	if (ste != null){
		return ste;
	}

	//
	// if unnresolved parameter exists, then return it
	//
	ste = getKinetics().getUnresolvedParameter(identifier);
	if (ste != null){
		return ste;
	}

	//
	// if proxy parameter exists, then return it
	//
	ste = getKinetics().getProxyParameter(identifier);
	if (ste != null){
		return ste;
	}

	return null;
}   
/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public String getName() {
	return fieldName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 10:52:43 PM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return nameScope;
}
/**
 * Gets the physicsOptions property (int) value.
 * @return The physicsOptions property value.
 * @see #setPhysicsOptions
 */
public int getPhysicsOptions() {
	return fieldPhysicsOptions;
}

public boolean isReversible() {
	return bReversible;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}

/**
 * Gets the reactionParticipants property (cbit.vcell.model.ReactionParticipant[]) value.
 * @return The reactionParticipants property value.
 * @see #setReactionParticipants
 */
public ReactionParticipant[] getReactionParticipants() {
	return fieldReactionParticipants;
}
/**
 * Gets the reactionParticipants index property (cbit.vcell.model.ReactionParticipant) value.
 * @return The reactionParticipants property value.
 * @param index The index value into the property array.
 * @see #setReactionParticipants
 */
public ReactionParticipant getReactionParticipants(int index) {
	return getReactionParticipants()[index];
}

public final int getStoichiometry(SpeciesContext speciesContext) {
	ReactionParticipant[] rps = getReactionParticipants();

	int totalStoich = 0;
	for (ReactionParticipant rp : rps) {
		if (rp.getSpeciesContext() == speciesContext) {
			if (rp instanceof Product){
				totalStoich += rp.getStoichiometry();
			}else if (rp instanceof Reactant){
				totalStoich += (-1)*rp.getStoichiometry();
			}
		}
	}
	return totalStoich;
}
public int getNumReactants() {
	if(!hasReactant()) {
		return 0;
	}
	int count = 0;
	for(int i=0; i<getReactionParticipants().length; i++) {
		ReactionParticipant p = getReactionParticipants(i);
		if(p instanceof Reactant) {
			count++;
		}
	}
	return count;
}
public List<Reactant> getReactants() {
	List<Reactant> rList = new ArrayList<Reactant>();
	for(int i=0; i<getReactionParticipants().length; i++) {
		ReactionParticipant p = getReactionParticipants(i);
		if(p instanceof Reactant) {
			rList.add((Reactant)p);
		}
	}
	return rList;
}

public int getNumProducts() {
	if(!hasProduct()) {
		return 0;
	}
	int count = 0;
	for(int i=0; i<getReactionParticipants().length; i++) {
		ReactionParticipant p = getReactionParticipants(i);
		if(p instanceof Product) {
			count++;
		}
	}
	return count;
}
public List<Product> getProducts() {
	List<Product> pList = new ArrayList<Product>();
	for(int i=0; i<getReactionParticipants().length; i++) {
		ReactionParticipant p = getReactionParticipants(i);
		if(p instanceof Product) {
			pList.add((Product)p);
		}
	}
	return pList;
}

public Reactant getReactant(String name) {
	if(!hasReactant()) {
		return null;
	}
	for(int i=0; i<getReactionParticipants().length; i++) {
		ReactionParticipant p = getReactionParticipants(i);
		if(p instanceof Reactant) {
			if(p.getName().equals(name)) {
				return (Reactant)p;
			}
		}
	}
	return null;
}
public Reactant getReactant(int thatIndex) {
	if(!hasReactant()) {
		return null;	// could throw an IndexOutOfBoundsException too
	}
	int thisIndex = 0;
	for(int i=0; i<getReactionParticipants().length; i++) {
		ReactionParticipant p = getReactionParticipants(i);
		if(p instanceof Reactant) {
			if(thisIndex == thatIndex) {
				return (Reactant)p;
			}
			thisIndex++;
		}
	}
	return null;
}
public Product getProduct(String name) {
	if(!hasProduct()) {
		return null;
	}
	for(int i=0; i<getReactionParticipants().length; i++) {
		ReactionParticipant p = getReactionParticipants(i);
		if(p instanceof Product) {
			if(p.getName().equals(name)) {
				return (Product)p;
			}
		}
	}
	return null;
}
public Product getProduct(int thatIndex) {
	if(!hasProduct()) {
		return null;
	}
	int thisIndex = 0;
	for(int i=0; i<getReactionParticipants().length; i++) {
		ReactionParticipant p = getReactionParticipants(i);
		if(p instanceof Product) {
			if(thisIndex == thatIndex) {
				return (Product)p;
			}
			thisIndex++;
		}
	}
	return null;
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Reaction
 */
public Structure getStructure() {
	return structure;
}

//public Structure getLocation() {
//	return structure;
//}

/**
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}
/**
 * Insert the method's description here.
 * Creation date: (5/24/01 4:47:54 PM)
 * @param evt java.beans.PropertyChangeEvent
 */
public void propertyChange(PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("physicsOptions")){
		Integer newPhysicsOption = (Integer)evt.getNewValue();
		if (newPhysicsOption!=null && newPhysicsOption.intValue()==ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
			//
			// setting physicsOptions to "Molecular&Electrical", must have a non-zero chargeValence
			//
			try {
				KineticsParameter chargeValence = getKinetics().getChargeValenceParameter();
				if (chargeValence.getConstantValue()==0){
					chargeValence.setExpression(new Expression(1.0));
				}
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
			}
		}
	}
}
/**
 * This method was created by a SmartGuide.
 * @param symbolTable cbit.vcell.parser.SymbolTable
 */
public void rebindAllToModel(Model model) throws ExpressionException, ModelException, PropertyVetoException {
	this.model = model;
	
	if (getName() == null){
		try {
			if (this instanceof FluxReaction){
				int count=0;
				String reactionStepName = null;
				while (true) {
					reactionStepName = "flux" + count;
					if (model.getReactionStep(reactionStepName) == null){
						break;
					}
				
					count++;
				}
				setName(reactionStepName);
			}else if (this instanceof SimpleReaction){
				int count=0;
				String reactionStepName = null;
				while (true) {
					reactionStepName = "r" + count;
					if (model.getReactionStep(reactionStepName) == null){
						break;
					}
				
					count++;
				}
				setName(reactionStepName);
			}else{
				throw new RuntimeException("ReactionStep of type "+getClass().toString()+" cannot be named by model");
			}
		}catch (java.beans.PropertyVetoException e){
		}
	}
	if (getKinetics()!=null) {
		getKinetics().removeUnresolvedParameters(this);
		//
		// remove any unresolved parameters that correspond to SpeciesContexts by adding them as Catalysts.
		//
		Kinetics.UnresolvedParameter unresolvedParameters[] = getKinetics().getUnresolvedParameters();
		for (int i = 0; i < unresolvedParameters.length; i++){
			SpeciesContext referencedSpeciesContext = model.getSpeciesContext(unresolvedParameters[i].getName());
			if (referencedSpeciesContext!=null){
				addCatalyst(referencedSpeciesContext);
			}
		}
		getKinetics().bind(this);
		getKinetics().resolveUndefinedUnits();
		getKinetics().refreshUnits();
	}
	
}
public void refreshDependencies() {
	fieldKinetics.refreshDependencies();
	for (int i=0;fieldReactionParticipants!=null && i<fieldReactionParticipants.length;i++){
		ReactionParticipant rp = fieldReactionParticipants[i];
		rp.removeVetoableChangeListener(this);
		rp.addVetoableChangeListener(this);
		rp.refreshDependencies();
	}
	if (structure instanceof Membrane){
		((Membrane)structure).getMembraneVoltage().removePropertyChangeListener(this);
		((Membrane)structure).getMembraneVoltage().addPropertyChangeListener(this);
	}
	removePropertyChangeListener(this);
	addPropertyChangeListener(this);
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
public void removeReactionParticipant(ReactionParticipant reactionParticipant) throws PropertyVetoException, ExpressionException, ModelException {
	if (reactionParticipant == null){
		return;
	}	
	if (contains(reactionParticipant)){
		ReactionParticipant newReactionParticipants[] = (ReactionParticipant[])BeanUtils.removeElement(fieldReactionParticipants, reactionParticipant);
		setReactionParticipants(newReactionParticipants);
	}
}            
/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}
/**
 * Sets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @param kinetics The new value for the property.
 * @see #getKinetics
 */
public void setKinetics(Kinetics kinetics) {
	Kinetics oldValue = fieldKinetics;
	//
	// removing twice because Kinetics adds ReactionStep as a listener also
	//
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(this);
		oldValue.removePropertyChangeListener(this);
		removePropertyChangeListener(oldValue);
		removePropertyChangeListener(oldValue);
	}
	fieldKinetics = kinetics;
	if (kinetics != null) {
		kinetics.removePropertyChangeListener(this);
		kinetics.addPropertyChangeListener(this);
		removePropertyChangeListener(kinetics);
		addPropertyChangeListener(kinetics);
	}

	//
	// if new Kinetic type is incompatible with current PhysicsOptions, choose appropriate PhysicsOptions
	// then if "zero" valence is incompatible with new kinetic type and new physicsOptions, then force to 1
	//
	try {
		KineticsParameter chargeValenceParameter = getKinetics().getChargeValenceParameter();
		if (kinetics.getKineticsDescription().isElectrical()){
			if (getPhysicsOptions() == PHYSICS_MOLECULAR_ONLY){
				if (chargeValenceParameter!=null && !chargeValenceParameter.getExpression().isZero()){
					setPhysicsOptions(PHYSICS_MOLECULAR_AND_ELECTRICAL);
				}else{
					setPhysicsOptions(PHYSICS_ELECTRICAL_ONLY);
				}
			}
		}else{
			if (getPhysicsOptions() == PHYSICS_ELECTRICAL_ONLY){
				if (chargeValenceParameter!=null && !chargeValenceParameter.getExpression().isZero()){
					setPhysicsOptions(PHYSICS_MOLECULAR_AND_ELECTRICAL);
				}else{
					setPhysicsOptions(PHYSICS_MOLECULAR_ONLY);
				}
			}
		}

		if (getKinetics()!=null){
			if (chargeValenceParameter!=null){
				if (chargeValenceParameter.getExpression().isZero()){
					chargeValenceParameter.setExpression(new Expression(1.0));
				}
			}
		}
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
	}	
	
	firePropertyChange(PROPERTY_NAME_KINETICS, oldValue, kinetics);
}


@Override
public final void electricalTopologyChanged(ElectricalTopology electricalTopology) {
	getKinetics().electricalTopologyChanged(electricalTopology);
}

/**
 * Insert the method's description here.
 * Creation date: (5/23/00 1:45:57 PM)
 * @param model cbit.vcell.model.Model
 */
public void setModel(Model model) {
	Model oldValue = this.model;
	if (oldValue != null){
		oldValue.removeElectricalTopologyListener(this);
	}
	this.model = model;
	if (this.model != null){
		this.model.addElectricalTopologyListener(this);
	}
	firePropertyChange("model", oldValue, model);
}
/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
public void setName(String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}
/**
 * Sets the physicsOptions property (int) value.
 * @param physicsOptions The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getPhysicsOptions
 */
public void setPhysicsOptions(int physicsOptions) throws java.beans.PropertyVetoException {
	if (fieldPhysicsOptions==physicsOptions){
		return;
	}
	int oldValue = fieldPhysicsOptions;
	fireVetoableChange("physicsOptions", new Integer(oldValue), new Integer(physicsOptions));
	fieldPhysicsOptions = physicsOptions;
	firePropertyChange("physicsOptions", new Integer(oldValue), new Integer(physicsOptions));
}

public void setReversible(boolean argReversible) {
	if (this.bReversible==argReversible){
		return;
	}
	boolean oldValue = this.bReversible;
	this.bReversible = argReversible;
	firePropertyChange("reversible", new Boolean(oldValue), new Boolean(this.bReversible));
}

public abstract void setReactionParticipantsFromDatabase(Model model, ReactionParticipant[] reactionParticipants) throws PropertyVetoException, DataAccessException;
/**
 * Sets the reactionParticipants property (cbit.vcell.model.ReactionParticipant[]) value.
 * @param reactionParticipants The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getReactionParticipants
 */
public final void setReactionParticipants(ReactionParticipant[] reactionParticipants) throws java.beans.PropertyVetoException {
	ReactionParticipant[] oldValue = fieldReactionParticipants;
	fireVetoableChange(PROPERTY_NAME_REACTION_PARTICIPANTS, oldValue, reactionParticipants);
	
	if (oldValue!=null){
		for (int i = 0; i < oldValue.length; i++){
			oldValue[i].removePropertyChangeListener(this);
			oldValue[i].removeVetoableChangeListener(this);
		}

	}
	
	fieldReactionParticipants = reactionParticipants;
	
	if (reactionParticipants!=null){
		for (int i = 0; i < reactionParticipants.length; i++){
			reactionParticipants[i].addPropertyChangeListener(this);
			reactionParticipants[i].addVetoableChangeListener(this);
		}

	}
	
	firePropertyChange(PROPERTY_NAME_REACTION_PARTICIPANTS, oldValue, reactionParticipants);
}
/**
 * This method was created in VisualAge.
 * @param structure cbit.vcell.model.Structure
 */
public void setStructure(Structure structure) {
	this.structure = structure;
	if (structure instanceof Membrane){
		((Membrane)structure).getMembraneVoltage().removePropertyChangeListener(this);
		((Membrane)structure).getMembraneVoltage().addPropertyChangeListener(this);
	}	
}
/**
 * This method was created in VisualAge.
 * @param e java.beans.PropertyChangeEvent
 */
public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
	if (e.getSource() instanceof ReactionParticipant && e.getPropertyName().equals("speciesContext")){
		SpeciesContext newSpeciesContext = (SpeciesContext)e.getNewValue();
		ReactionParticipant rp = (ReactionParticipant)e.getSource();
		for (int i = 0;fieldReactionParticipants!=null && i < fieldReactionParticipants.length; i++){
			if (fieldReactionParticipants[i]!=rp && fieldReactionParticipants[i].getSpeciesContext()==newSpeciesContext){
				throw new PropertyVetoException("reaction participants (reactants/products/catalysts) must have unique species contexts",e);
			}
		}
	}
	if (e.getSource()==this && e.getPropertyName().equals("name")){
		if (e.getNewValue()==null || ((String)(e.getNewValue())).trim().length()==0){
			throw new PropertyVetoException("reactionStep name is not specified (null)",e);
		}
		if (((String)(e.getNewValue())).trim().length()>255){
			throw new PropertyVetoException("reactionStep name for reaction \'" + (String)(e.getNewValue()) + "\' cannot be longer than 255 characters",e);
		}
	}
	if (e.getSource()==this && e.getPropertyName().equals("chargeCarrierValence")){
		if ((e.getNewValue()!=null) && ((Integer)(e.getNewValue())).intValue()!=0){
			if (getStructure()!=null && !(getStructure() instanceof Membrane)){
				throw new PropertyVetoException("only membrane reactions can have a non-zero 'chargeCarrierValence'",e);
			}
		}
	}
	if (e.getSource()==this && e.getPropertyName().equals("physicsOptions")){
		//
		// only membrane reactions can have a physics mapping of ElectricalOnly or MolecularAndElectrical
		//
		if ((e.getNewValue()!=null) && ((Integer)(e.getNewValue())).intValue()!=PHYSICS_MOLECULAR_ONLY){
			if (getStructure()!=null && !(getStructure() instanceof Membrane)){
				throw new PropertyVetoException("only membrane reactions can have a physics mapping other than 'molecular flux only'",e);
			}
		}
	}
	if (e.getSource()==this && e.getPropertyName().equals("structure")){
		//
		// only membrane reactions can have a physics mapping of ElectricalOnly or MolecularAndElectrical
		//
		Structure structure = (Structure)e.getNewValue();
		if ((structure!=null) && !(structure instanceof Membrane)){
			if (getPhysicsOptions()!=PHYSICS_MOLECULAR_ONLY){
				throw new PropertyVetoException("only membrane reactions can have a physics mapping other than 'molecular flux only'",e);
			}
		}
	}
}
/**
 * This method was created by a SmartGuide.
 * @param ps java.io.PrintStream
 * @exception java.lang.Exception The exception description.
 */
@Deprecated
public String getAnnotation() {
	return annotation;
}

public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
	for (SymbolTableEntry ste : getKinetics().getProxyParameters()) {
		entryMap.put(ste.getName(), ste);
	}
	
	for (SymbolTableEntry ste : getKinetics().getUnresolvedParameters()) {
		entryMap.put(ste.getName(), ste);
	}
	
	for (SymbolTableEntry ste : getKinetics().getKineticsParameters()) {
		entryMap.put(ste.getName(), ste);
	}
}


public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	getNameScope().getExternalEntries(entryMap);
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

public void addReactant(SpeciesContext speciesContext,int stoichiometry) throws ModelException, PropertyVetoException {

	int count = countNumReactionParticipants(speciesContext);

	// NOTE : right now, we are not taking into account the possiblity of allowing 
	// a speciesContext to be a Catalyst as well as pdt or reactant.
	if (count == 0) {
		// No matching reactionParticipant was found for the speciesContext, hence add it as a Pdt
		addReactionParticipant(new Reactant(null,this, speciesContext, stoichiometry));
	} else if (count == 1) {
		ReactionParticipant[] rps = getReactionParticipants();
		ReactionParticipant rp0 = null;
		for (ReactionParticipant rp : rps) {
			if (rp.getSpeciesContext() == speciesContext) {
				rp0 = rp;
				break;
			}
		}
		// One matching reactionParticipant was found for the speciesContext, 
		// if rp[0] is a product, add speciesContext as reactant, else throw exception since it is already a reactant (refer NOTE above)
		if (rp0 instanceof Product){
			addReactionParticipant(new Reactant(null,this, speciesContext, stoichiometry));
		} else if (rp0 instanceof Reactant || rp0 instanceof Catalyst) {
			throw new ModelException("reactionParticipant " + speciesContext.getName() + " already defined as a Reactant or a Catalyst of the reaction.");
		}
	} else if (count > 1) {
		// if rps.length is > 1, speciesContext occurs both as reactant and pdt, so throw exception
		throw new ModelException("reactionParticipant " + speciesContext.getName() + " already defined as a Reactant AND Product of the reaction.");
	}
		
}   

public void addProduct(SpeciesContext speciesContext,int stoichiometry) throws ModelException, PropertyVetoException {

	int count = countNumReactionParticipants(speciesContext);

	// NOTE : right now, we are not taking into account the possiblity of allowing 
	// a speciesContext to be a Catalyst as well as pdt or reactant.
	if (count == 0) {
		// No matching reactionParticipant was found for the speciesContext, hence add it as a Pdt
		addReactionParticipant(new Product(null,this, speciesContext, stoichiometry));
	} else if (count == 1) {
		ReactionParticipant[] rps = getReactionParticipants();
		ReactionParticipant rp0 = null;
		for (ReactionParticipant rp : rps) {
			if (rp.getSpeciesContext() == speciesContext) {
				rp0 = rp;
				break;
			}
		}
		// One matching reactionParticipant was found for the speciesContext, 
		// if rp[0] is a product, add speciesContext as reactant, else throw exception since it is already a reactant (refer NOTE above)
		if (rp0 instanceof Reactant){
			addReactionParticipant(new Product(null,this, speciesContext, stoichiometry));
		} else if (rp0 instanceof Product || rp0 instanceof Catalyst) {
			throw new ModelException("reactionParticipant " + speciesContext.getName() + " already defined as a Product or a Catalyst of the reaction.");
		}
	} else if (count > 1) {
		// if rps.length is > 1, speciesContext occurs both as reactant and pdt, so throw exception
		throw new ModelException("reactionParticipant " + speciesContext.getName() + " already defined as a Reactant AND Product of the reaction.");
	}
		
}

public String getTypeLabel() {
	return "Reaction";
}

public boolean hasCatalyst()
{
	for(ReactionParticipant rp : fieldReactionParticipants)
	{
		if(rp instanceof Catalyst)
		{
			return true;
		}
	}
	return false;
}

public boolean hasProduct()
{
	for(ReactionParticipant rp : fieldReactionParticipants)
	{
		if(rp instanceof Product)
		{
			return true;
		}
	}
	return false;
}

public boolean hasReactant()
{
	for(ReactionParticipant rp : fieldReactionParticipants)
	{
		if(rp instanceof Reactant)
		{
			return true;
		}
	}
	return false;
}

public boolean hasElectrical() {
	// declares currents?
	if (getStructure() instanceof Membrane && getPhysicsOptions() == PHYSICS_ELECTRICAL_ONLY || getPhysicsOptions() == PHYSICS_MOLECULAR_AND_ELECTRICAL){
		return true;
	}
	
	// is voltage dependent?
	for (Kinetics.KineticsProxyParameter proxyParameter : getKinetics().getProxyParameters()){
		if (proxyParameter.getTarget() instanceof Membrane.MembraneVoltage){
			return true;
		}
	}
	
	return false;
}
@Override
public boolean containsSearchText(String searchText){
	String lowerCaseSearchText = searchText.toLowerCase();
	if (getName().toLowerCase().contains(lowerCaseSearchText)
//			|| new ReactionEquation(rs, bioModel.getModel()).toString().toLowerCase().contains(lowerCaseSearchText)
			|| getStructure().getName().toLowerCase().contains(lowerCaseSearchText)
			|| getKinetics().getKineticsDescription().getDescription().toLowerCase().contains(lowerCaseSearchText)) {
			return true;
	}
	return false;
}

public ModelProcessDynamics getDynamics(){
	return fieldKinetics;
}
}
