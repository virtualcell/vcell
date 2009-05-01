package cbit.vcell.model;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.*;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.parser.*;
import cbit.vcell.parser.Expression;

import java.io.*;
import java.util.*;

import org.vcell.util.BeanUtils;
import org.vcell.util.Cacheable;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.vcell.xml.MIRIAMAnnotatable;
import cbit.vcell.xml.MIRIAMAnnotation;
import cbit.util.*;
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
public abstract class ReactionStep
	implements
		Cacheable, Serializable, ScopedSymbolTable, Matchable, VetoableChangeListener, PropertyChangeListener,
		MIRIAMAnnotatable
{

	private String annotation = null;
	
	
	public final static int PHYSICS_MOLECULAR_ONLY = 0;
	public final static int PHYSICS_MOLECULAR_AND_ELECTRICAL = 1;
	public final static int PHYSICS_ELECTRICAL_ONLY = 2;

	private int fieldPhysicsOptions = PHYSICS_MOLECULAR_ONLY;

	private KeyValue key = null;
	
	/**
	 * The Structure object that this ReactionStep belongs to.
	 */
   private Structure structure = null;
   
	private String fieldName = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private Kinetics fieldKinetics = null;
	private transient cbit.vcell.model.Model model = null;
	private ChargeCarrierValence fieldChargeCarrierValence = null;  // see constructor
	private cbit.vcell.model.ReactionParticipant[] fieldReactionParticipants = new ReactionParticipant[0];
	private ReactionNameScope nameScope = null; // see constructor
	private MIRIAMAnnotation miriamAnnotation;

	public class ReactionNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public ReactionNameScope(){
			super();
		}
		public cbit.vcell.parser.NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(ReactionStep.this.getName());
		}
		public cbit.vcell.parser.NameScope getParent() {
			if (ReactionStep.this.model != null){
				return ReactionStep.this.model.getNameScope();
			}else{
				return null;
			}
		}
		public cbit.vcell.parser.ScopedSymbolTable getScopedSymbolTable() {
			return ReactionStep.this;
		}
	}
	
/**
 * ReactionStep constructor comment.
 */
protected ReactionStep(Structure structure, KeyValue key, String name,String annotation) throws java.beans.PropertyVetoException {
	super();
	nameScope = new ReactionStep.ReactionNameScope();
	fieldChargeCarrierValence = new ChargeCarrierValence("charge",getNameScope());
	setStructure(structure);
	this.key = key;
	removePropertyChangeListener(this);
	addPropertyChangeListener(this);
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
	setName(name);
	this.annotation = annotation;
	try {
		setKinetics(new GeneralKinetics(this));
	}catch (Exception e){
		e.printStackTrace(System.out);
	}
}

protected ReactionStep(Structure structure, KeyValue key, String name) throws java.beans.PropertyVetoException {
	this(structure,key,name,null);
//	super();
//	nameScope = new ReactionStep.ReactionNameScope();
//	fieldChargeCarrierValence = new ChargeCarrierValence("charge",getNameScope());
//	setStructure(structure);
//	this.key = key;
//	removePropertyChangeListener(this);
//	addPropertyChangeListener(this);
//	removeVetoableChangeListener(this);
//	addVetoableChangeListener(this);
//	setName(name);
//	try {
//		setKinetics(new GeneralKinetics(this));
//	}catch (Exception e){
//		e.printStackTrace(System.out);
//	}
}
/**
 * ReactionStep constructor comment.
 */
protected ReactionStep(Structure structure, String name) throws PropertyVetoException {
	this(structure,null,name);
}


public MIRIAMAnnotation getMIRIAMAnnotation() {
	return miriamAnnotation;
}
public void setMIRIAMAnnotation(MIRIAMAnnotation miriamAnnotation) {
	this.miriamAnnotation = miriamAnnotation;
	
}


public void addCatalyst(SpeciesContext speciesContext) throws ModelException, PropertyVetoException {

	ReactionParticipant[] rps = getReactionParticipants(speciesContext);

	// NOTE : Currently, we are not allowing the case where a reactionParticipant is a reactant and/or product AND a catalyst
	// Hence, if the rps array is not null, throw an exception, since the speciesContext is already a reactionParticipant.
	
	if (rps.length == 0){
		addReactionParticipant(new Catalyst(null,this, speciesContext));
	}else{
		throw new ModelException("reactionParticipant already defined as Reactant and/or Product in the reaction.");
	}
		
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
			reactionParticipant.removePropertyChangeListener(this);
			reactionParticipant.addPropertyChangeListener(this);
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

	if (!fieldChargeCarrierValence.compareEqual(rs.fieldChargeCarrierValence)) {
		return false;
	}

	if (fieldPhysicsOptions != rs.fieldPhysicsOptions){
		return false;
	}
	
	if (!org.vcell.util.Compare.isEqual(fieldReactionParticipants, rs.fieldReactionParticipants)) {
		return false;
	}
	if(!Compare.isEqualOrNull(getAnnotation(), rs.getAnnotation())){
		return false;
	}
	if(!Compare.isEqualOrNull(getMIRIAMAnnotation(), rs.getMIRIAMAnnotation())){
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
public void fireVetoableChange(
    String propertyName,
    Object oldValue,
    Object newValue)
    throws java.beans.PropertyVetoException {
    getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public abstract void fromTokens(org.vcell.util.CommentStringTokenizer tokens, Model model) throws Exception;
/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 10:26:42 PM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(Vector issueList) {
	
	if (fieldKinetics!=null){
		fieldKinetics.gatherIssues(issueList);
	}
}
/**
 * Gets the chargeCarrierValence property (int) value.
 * @return The chargeCarrierValence property value.
 * @see #setChargeCarrierValence
 */
public ChargeCarrierValence getChargeCarrierValence() {
	return fieldChargeCarrierValence;
}
public SymbolTableEntry getEntry(String identifier) throws ExpressionBindingException
{
	cbit.vcell.parser.SymbolTableEntry localSTE = getLocalEntry(identifier);
	if (localSTE != null && !(localSTE instanceof Kinetics.UnresolvedParameter)){
		return localSTE;
	}
	Kinetics.UnresolvedParameter unresolvedParameter = (Kinetics.UnresolvedParameter)localSTE;
			
	SymbolTableEntry externalSTE = getNameScope().getExternalEntry(identifier,this);

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
	SymbolTableEntry reservedSTE = ReservedSymbol.fromString(identifier);
	if (reservedSTE != null){
		ReservedSymbol rs = (ReservedSymbol)reservedSTE;
		if (rs.isX() || rs.isY() || rs.isZ()){
			throw new ExpressionBindingException("can't use x, y, or z, Physiological Models must be spatially independent");
		}
		return getKinetics().addProxyParameter(reservedSTE);
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
public SymbolTableEntry getLocalEntry(String identifier) throws ExpressionBindingException
{
	//
	// check symbol against charge valence
	//
	if (getChargeCarrierValence().getName().equals(identifier)){
		return getChargeCarrierValence();
	}

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
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}

public Expression getReactionRateExpression(ReactionParticipant reactionParticipant, SimulationContext simContext) throws Exception {
	if (reactionParticipant instanceof Catalyst){
		throw new Exception("Catalyst "+reactionParticipant+" doesn't have a rate for this reaction");
		//return new Expression(0.0);
	}	
	double stoich = getStoichiometry(reactionParticipant.getSpecies(),reactionParticipant.getStructure());
	if (stoich==0.0){
		return new Expression(0.0);
	}
	if (getKinetics() instanceof DistributedKinetics){
		DistributedKinetics distributedKinetics = (DistributedKinetics)getKinetics();
		if (stoich!=1){
			Expression exp = Expression.mult(new Expression(stoich),new Expression(distributedKinetics.getReactionRateParameter().getName()));
			exp.bindExpression(this);
			return exp;
		}else{
			Expression exp = new Expression(distributedKinetics.getReactionRateParameter().getName());
			exp.bindExpression(this);
			return exp;
		}
	}else if (getKinetics() instanceof LumpedKinetics){
		StructureMapping structureMapping = simContext.getGeometryContext().getStructureMapping(getStructure());
		final StructureMappingParameter sizeParameter = structureMapping.getSizeParameter();
		//
		// need to put this into concentration/time with respect to structure for reaction.
		//
		SymbolTable tempSymbolTable = new SymbolTable(){
			public SymbolTableEntry getEntry(String identifierString) throws ExpressionBindingException {
				SymbolTableEntry ste = ReactionStep.this.getEntry(identifierString);
				if (ste!=null){
					return ste;
				}
				if (identifierString.equals(sizeParameter.getName())){
					return sizeParameter;
				}
				return null;
			}
		};
		
		LumpedKinetics lumpedKinetics = (LumpedKinetics)getKinetics();
		Expression factor = null;
		if (getStructure() instanceof Feature || ((getStructure() instanceof Membrane) && this instanceof FluxReaction)){
			factor = Expression.mult(new Expression(ReservedSymbol.KMOLE.getName()),Expression.invert(new Expression(sizeParameter.getName())));
		}else if (getStructure() instanceof Membrane && this instanceof SimpleReaction){
			factor = Expression.invert(new Expression(sizeParameter.getName()));
		}else{
			throw new RuntimeException("failed to create reaction rate expression for reaction "+getName()+", with kinetic type of "+getKinetics().getClass().getName());
		}
		if (stoich!=1){
			Expression exp = Expression.mult(new Expression(stoich),Expression.mult(new Expression(lumpedKinetics.getLumpedReactionRateParameter().getName()),factor));
			exp.bindExpression(tempSymbolTable);
			return exp;
		}else{
			Expression exp = Expression.mult(new Expression(lumpedKinetics.getLumpedReactionRateParameter().getName()),factor);
			exp.bindExpression(tempSymbolTable);
			return exp;
		}
	}else{
		throw new RuntimeException("unexpected kinetic type "+getKinetics().getClass().getName());
	}
}   

public ReactionParticipant getReactionParticipantFromSymbol(String reactParticipantName) {

	ReactionParticipant rp_Array[] = getReactionParticipants();
	
	for (int i = 0; i < rp_Array.length; i++) {
		if (AbstractNameScope.getStrippedIdentifier(reactParticipantName).equals(rp_Array[i].getSpeciesContext().getName())){
			return rp_Array[i];
		}
	}
	return null;
}   
/**
 * Gets the reactionParticipants property (cbit.vcell.model.ReactionParticipant[]) value.
 * @return The reactionParticipants property value.
 * @see #setReactionParticipants
 */
public cbit.vcell.model.ReactionParticipant[] getReactionParticipants() {
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
public ReactionParticipant[] getReactionParticipants(Species species, Structure structure){
	ReactionParticipant rpArray[] = getReactionParticipants();
	Vector rpVector = new Vector();

	for (int i = 0; i < rpArray.length; i++) {
		if (species.compareEqual(rpArray[i].getSpecies()) &&
			structure.compareEqual(rpArray[i].getStructure())){
			rpVector.addElement(rpArray[i]);
		}
	}
	
	return (ReactionParticipant[])BeanUtils.getArray(rpVector, ReactionParticipant.class);
}         
public ReactionParticipant[] getReactionParticipants(SpeciesContext speciesContext){
	return getReactionParticipants(speciesContext.getSpecies(), speciesContext.getStructure());
}         
/**
 * This method was created in VisualAge.
 * @return double
 * @param speciesContext cbit.vcell.model.SpeciesContext
 */
public abstract int getStoichiometry(Species species, Structure structure);
/**
 * This method was created in VisualAge.
 * @return double
 * @param speciesContext cbit.vcell.model.SpeciesContext
 */
public int getStoichiometry(SpeciesContext speciesContext) {
	return getStoichiometry(speciesContext.getSpecies(),speciesContext.getStructure());
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Reaction
 */
public Structure getStructure() {
	return structure;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/00 10:20:28 PM)
 * @return java.lang.String
 */
public abstract String getTerm();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getVCML() {
	java.io.StringWriter stringWriter = new java.io.StringWriter();
	java.io.PrintWriter pw = new java.io.PrintWriter(stringWriter);
	writeTokens(pw);
	pw.flush();
	pw.close();
	return stringWriter.getBuffer().toString();
}
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
				if (getChargeCarrierValence().getConstantValue()==0){
					getChargeCarrierValence().setExpression(new Expression(1.0));
				}
			}catch (PropertyVetoException e){
				e.printStackTrace(System.out);
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
				setName(model.getFreeFluxReactionName());
			}else if (this instanceof SimpleReaction){
				setName(model.getFreeReactionStepName());
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
				if (referencedSpeciesContext.getStructure()==getStructure()){
					addCatalyst(referencedSpeciesContext);
				}else if (getStructure() instanceof Membrane &&
						(((Membrane)getStructure()).getInsideFeature()==referencedSpeciesContext.getStructure() || 
						 ((Membrane)getStructure()).getOutsideFeature()==referencedSpeciesContext.getStructure())){
					addCatalyst(referencedSpeciesContext);
				}
			}
		}
		getKinetics().bind(this);
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
		if (getChargeCarrierValence() != null){
			getChargeCarrierValence().removePropertyChangeListener(oldValue);
		}
	}
	fieldKinetics = kinetics;
	if (kinetics != null) {
		kinetics.removePropertyChangeListener(this);
		kinetics.addPropertyChangeListener(this);
		removePropertyChangeListener(kinetics);
		addPropertyChangeListener(kinetics);
	}

	//
	// if new Kinetic type is incompatable with current PhysicsOptions, choose appropriate PhysicsOptions
	// then if "zero" valence is incompatable with new kinetic type and new physicsOptions, then force to 1
	//
	try {
		if (kinetics.getKineticsDescription().isElectrical()){
			if (getPhysicsOptions() == PHYSICS_MOLECULAR_ONLY){
				if (!getChargeCarrierValence().getExpression().isZero()){
					setPhysicsOptions(PHYSICS_MOLECULAR_AND_ELECTRICAL);
				}else{
					setPhysicsOptions(PHYSICS_ELECTRICAL_ONLY);
				}
			}
		}else{
			if (getPhysicsOptions() == PHYSICS_ELECTRICAL_ONLY){
				if (!getChargeCarrierValence().getExpression().isZero()){
					setPhysicsOptions(PHYSICS_MOLECULAR_AND_ELECTRICAL);
				}else{
					setPhysicsOptions(PHYSICS_MOLECULAR_ONLY);
				}
			}
		}

		if (kinetics.getKineticsDescription().needsValence() && getChargeCarrierValence().getExpression().isZero()){
			getChargeCarrierValence().setExpression(new Expression(1.0));
		}
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
	}	
	
	firePropertyChange("kinetics", oldValue, kinetics);
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/00 1:45:57 PM)
 * @param model cbit.vcell.model.Model
 */
public void setModel(Model model) {
	this.model = model;
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
/**
 * Sets the reactionParticipants property (cbit.vcell.model.ReactionParticipant[]) value.
 * @param reactionParticipants The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getReactionParticipants
 */
public void setReactionParticipants(cbit.vcell.model.ReactionParticipant[] reactionParticipants) throws java.beans.PropertyVetoException {
	cbit.vcell.model.ReactionParticipant[] oldValue = fieldReactionParticipants;
	fireVetoableChange("reactionParticipants", oldValue, reactionParticipants);
	
	if (oldValue!=null){
		for (int i = 0; i < oldValue.length; i++){
			oldValue[i].removeVetoableChangeListener(this);
		}

	}
	
	fieldReactionParticipants = reactionParticipants;
	
	if (reactionParticipants!=null){
		for (int i = 0; i < reactionParticipants.length; i++){
			reactionParticipants[i].addVetoableChangeListener(this);
		}

	}
	
	firePropertyChange("reactionParticipants", oldValue, reactionParticipants);
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
public abstract void writeTokens(java.io.PrintWriter pw);
public String getAnnotation() {
	return annotation;
}
public void setAnnotation(String annotation) {
	this.annotation = annotation;
}
}
