package cbit.vcell.model;
import cbit.vcell.parser.ExpressionBindingException;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import java.beans.*;
import cbit.util.*;
import cbit.vcell.model.Feature;
import cbit.vcell.parser.NameScope;

public class Model implements cbit.util.Versionable, Matchable, PropertyChangeListener, VetoableChangeListener, java.io.Serializable, cbit.vcell.parser.ScopedSymbolTable {
	private Version version = null;
	protected transient PropertyChangeSupport propertyChange;
	private java.lang.String fieldName = new String("NoName");
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private java.lang.String fieldDescription = new String();
	private cbit.vcell.model.Structure[] fieldStructures = new Structure[0];
	private cbit.vcell.model.Species[] fieldSpecies = new Species[0];
	private cbit.vcell.model.SpeciesContext[] fieldSpeciesContexts = new SpeciesContext[0];
	private cbit.vcell.model.ReactionStep[] fieldReactionSteps = new ReactionStep[0];
	private cbit.vcell.model.Diagram[] fieldDiagrams = new Diagram[0];
	private ModelNameScope nameScope = new Model.ModelNameScope();
	private Model.ModelParameter[] fieldModelParameters = new Model.ModelParameter[0];


	public class ModelNameScope extends BioNameScope {
		public ModelNameScope(){
			super();
		}
		public cbit.vcell.parser.NameScope[] getChildren() {
			//
			// return list of reactionNameScopes
			//
			cbit.vcell.parser.NameScope nameScopes[] = new cbit.vcell.parser.NameScope[Model.this.fieldReactionSteps.length+Model.this.fieldStructures.length];
			int j=0;
			for (int i = 0; i < Model.this.fieldReactionSteps.length; i++){
				nameScopes[j++] = Model.this.fieldReactionSteps[i].getNameScope();
			}
			for (int i = 0; i < Model.this.fieldStructures.length; i++){
				nameScopes[j++] = Model.this.fieldStructures[i].getNameScope();
			}
			return nameScopes;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(Model.this.getName());
		}
		public cbit.vcell.parser.NameScope getParent() {
			//System.out.println("ModelNameScope.getParent() returning null ... no parent");
			return null;
		}
		public cbit.vcell.parser.ScopedSymbolTable getScopedSymbolTable() {
			return Model.this;
		}
		public boolean isPeer(cbit.vcell.parser.NameScope nameScope){
			return ((nameScope instanceof cbit.vcell.mapping.MathMapping.MathMappingNameScope) && nameScope.isPeer(this));
		}

	}

	public static final int ROLE_UserDefined	= 0;

	public static final int NUM_ROLES		= 1;

	
	public class ModelParameter extends Parameter {
		
		private String fieldParameterName = null;
		private cbit.vcell.parser.Expression fieldParameterExpression = null;
		private int fieldParameterRole = -1;
		private cbit.vcell.units.VCUnitDefinition fieldUnitDefinition = null;
		
		protected ModelParameter(String argName, cbit.vcell.parser.Expression expression, int argRole, cbit.vcell.units.VCUnitDefinition argUnitDefinition) {
			if (argName == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argName.length()<1){
				throw new IllegalArgumentException("parameter name is zero length");
			}
			this.fieldParameterName = argName;
			this.fieldParameterExpression = expression;
			this.fieldUnitDefinition = argUnitDefinition;
			if (argRole >= 0 && argRole < NUM_ROLES){
				this.fieldParameterRole = argRole;
			}else{
				throw new IllegalArgumentException("parameter 'role' = "+argRole+" is out of range");
			}
		}


		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof ModelParameter)){
				return false;
			}
			ModelParameter mp = (ModelParameter)obj;
			if (!super.compareEqual0(mp)){
				return false;
			}
			if (fieldParameterRole != mp.fieldParameterRole){
				return false;
			}
			
			return true;
		}


		public boolean isExpressionEditable(){
			return true;
		}

		public boolean isUnitEditable(){
			return true;
		}

		public boolean isNameEditable(){
			return true;
		}

		public double getConstantValue() throws cbit.vcell.parser.ExpressionException {
			return this.fieldParameterExpression.evaluateConstant();
		}      


		public cbit.vcell.parser.Expression getExpression() {
			return this.fieldParameterExpression;
		}


		public int getIndex() {
			return -1;
		}


		public String getName(){ 
			return this.fieldParameterName; 
		}   


		public cbit.vcell.parser.NameScope getNameScope() {
			return Model.this.nameScope;
		}

		public int getRole() {
			return this.fieldParameterRole;
		}

		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}

		public void setUnitDefinition(cbit.vcell.units.VCUnitDefinition unitDefinition) throws java.beans.PropertyVetoException {
			cbit.vcell.units.VCUnitDefinition oldValue = fieldUnitDefinition;
			super.fireVetoableChange("unitDefinition", oldValue, unitDefinition);
			fieldUnitDefinition = unitDefinition;
			super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
		}
		public void setExpression(cbit.vcell.parser.Expression expression) throws java.beans.PropertyVetoException {
			cbit.vcell.parser.Expression oldValue = fieldParameterExpression;
			super.fireVetoableChange("expression", oldValue, expression);
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}
		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}

	}
	
public Model(Version argVersion) {
	this.version = argVersion;
	if (argVersion != null){
		fieldName = argVersion.getName();
		fieldDescription = argVersion.getAnnot();
	}
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);
}      


public Model(String argName) {
	this.fieldName = argName;
	this.version = null;
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);
}      


/**
 * This method was created by a SmartGuide.
 * @param featureName java.lang.String
 * @param parent cbit.vcell.model.Feature
 */
public void addFeature(String featureName, cbit.vcell.model.Feature parent, String membraneName) throws Exception {
	
	Structure structure = getStructure(featureName);
	
	if (structure!=null) {
		throw new Exception("adding feature '"+featureName+"', structure already exists with that name");
	}

	structure = getStructure(membraneName);

	if (structure!=null){
		throw new Exception("adding membrane '"+membraneName+"', structure already exists with that name");
	}

	//
	// add feature
	//
	Feature newFeature = new Feature(featureName);
	Structure newStructures[] = (Structure[])BeanUtils.addElement(fieldStructures,newFeature);
	
	//
	// add feature to outside (becomes the new "Top" Feature)
	//
	if (parent==null){
		//
		// get current top feature
		//
		Feature currTopFeature = getTopFeature();
		//
		// current top becomes a child, so a membrane is added to the current top.
		//
		if (currTopFeature!=null){
			String newMembraneName = null;
			if(membraneName == null){
				newMembraneName = currTopFeature.getName()+"_Membrane";
			}else{
				newMembraneName = membraneName;
			}
			Membrane membrane = new Membrane(newMembraneName);
			newStructures = (Structure[])BeanUtils.addElement(newStructures,membrane);
			membrane.setInsideFeature(currTopFeature);
			membrane.setOutsideFeature(newFeature);
			currTopFeature.setMembrane(membrane);
		}
	//
	// add feature inside pick'ed feature
	//
	}else{
		//
		// add new feature and associated membrane
		//
		Membrane membrane = new Membrane(membraneName);
		newStructures = (Structure[])BeanUtils.addElement(newStructures,membrane);
		membrane.setInsideFeature(newFeature);
		membrane.setOutsideFeature(parent);
		newFeature.setMembrane(membrane);
	}	
	setStructures(newStructures);
}


public void addModelParameter(Model.ModelParameter modelParameter) throws PropertyVetoException {
	if (!contains(modelParameter)){
		Model.ModelParameter newModelParameters[] = (Model.ModelParameter[])BeanUtils.addElement(fieldModelParameters,modelParameter);
		setModelParameters(newModelParameters);
	}	
}   


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


public void addReactionStep(ReactionStep reactionStep) throws PropertyVetoException {
	if (!contains(reactionStep)) {
		setReactionSteps((ReactionStep[])BeanUtils.addElement(fieldReactionSteps,reactionStep));
	}
}


public void addSpecies(Species species) throws PropertyVetoException {
	if (!contains(species)){
		Species newSpecies[] = (Species[])BeanUtils.addElement(fieldSpecies,species);
		setSpecies(newSpecies);
	}	
}   


/**
 * This method was created by a SmartGuide.
 * @param species cbit.vcell.model.Species
 */
public void addSpeciesContext(Species species, Structure structure) throws Exception {
	if (species != getSpecies(species.getCommonName())){
		throw new Exception("species "+species.getCommonName()+" not found in model");
	}
	SpeciesContext speciesContext = getSpeciesContext(species, structure);
	if (speciesContext != null){
		throw new Exception("speciesContext for "+species.getCommonName()+" within "+structure.getName()+" already defined");
	}
	speciesContext = new SpeciesContext(species,structure);
	speciesContext.setModel(this);
	addSpeciesContext(speciesContext);
}


/**
 * This method was created by a SmartGuide.
 * @param structure cbit.vcell.model.Structure
 */
public void addSpeciesContext(SpeciesContext speciesContext) throws PropertyVetoException {
	
	if (!contains(speciesContext.getSpecies())){
		throw new RuntimeException("species "+speciesContext.getSpecies().getCommonName()+" not found in model");
	}
	//  JMW and JCS added 26 June 2002: need to also check for structures
	if (!contains(speciesContext.getStructure())){
		throw new RuntimeException("structure "+speciesContext.getStructure().getName()+" not found in model");
	}
	if (getSpeciesContext(speciesContext.getSpecies(), speciesContext.getStructure())!=null){
		throw new RuntimeException("speciesContext for "+speciesContext.getSpecies().getCommonName()+" within "+speciesContext.getStructure().getName()+" already defined");
	}
	if (!contains(speciesContext)){
		SpeciesContext[] newArray = (SpeciesContext[])BeanUtils.addElement(fieldSpeciesContexts,speciesContext);
		speciesContext.setModel(this);
		setSpeciesContexts(newArray);
	}
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (4/24/2003 3:32:45 PM)
 */
public void clearVersion() {
	version = null;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {
	Model model = null;
	if (object == null){
		return false;
	}
	if (!(object instanceof Model)){
		return false;
	}else{
		model = (Model)object;
	}
	
	if (!Compare.isEqual(getName(), model.getName())) {
		return false;
	}
	if (!Compare.isEqual(getDescription(), model.getDescription())) {
		return false;
	}

	if (!Compare.isEqual(fieldSpeciesContexts, model.fieldSpeciesContexts)){
		return false;
	}
	if (!Compare.isEqual(fieldSpecies, model.fieldSpecies)){
		return false;
	}
	if (!Compare.isEqual(fieldStructures, model.fieldStructures)){
		return false;
	}
	if (!Compare.isEqual(fieldReactionSteps, model.fieldReactionSteps)){
		return false;
	}
	if (!Compare.isEqual(fieldDiagrams, model.fieldDiagrams)){
		return false;
	}
	
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:37:02 PM)
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public boolean contains(Diagram diagram) {
	for (int i=0;i<fieldDiagrams.length;i++){
		if (fieldDiagrams[i].equals(diagram)){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:37:02 PM)
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public boolean contains(ModelParameter modelParameter) {
	for (int i=0;i<fieldModelParameters.length;i++){
		if (fieldModelParameters[i].equals(modelParameter)){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:37:02 PM)
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public boolean contains(ReactionStep reactionStep) {
	for (int i=0;i<fieldReactionSteps.length;i++){
		if (fieldReactionSteps[i].equals(reactionStep)){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:37:02 PM)
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public boolean contains(Species species) {
	for (int i=0;i<fieldSpecies.length;i++){
		if (fieldSpecies[i].equals(species)){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:37:02 PM)
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public boolean contains(SpeciesContext speciesContext) {
	for (int i=0;i<fieldSpeciesContexts.length;i++){
		if (fieldSpeciesContexts[i].equals(speciesContext)){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:37:02 PM)
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public boolean contains(Structure structure) {
	for (int i=0;i<fieldStructures.length;i++){
		if (fieldStructures[i] == structure){
			return true;
		}
	}
	return false;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(evt);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 10:38:12 PM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(Vector issueList) {
	//
	// check for unknown units (TBD) and unit consistency
	//
	try {
		for (int i=0;i<fieldModelParameters.length;i++){
			if (fieldModelParameters[i].getUnitDefinition()==null){
			}else if (fieldModelParameters[i].getUnitDefinition().compareEqual(cbit.vcell.units.VCUnitDefinition.UNIT_TBD)){
				issueList.add(new Issue(fieldModelParameters[i], "Units","unit is undefined (TBD) for parameter '"+fieldModelParameters[i].getName()+"'",Issue.SEVERITY_WARNING));
			}
		}
		//
		// determine unit consistency for each expression
		//
		for (int i = 0; i < fieldModelParameters.length; i++){
			try {
				cbit.vcell.units.VCUnitDefinition paramUnitDef = fieldModelParameters[i].getUnitDefinition();
				cbit.vcell.units.VCUnitDefinition expUnitDef = cbit.vcell.parser.VCUnitEvaluator.getUnitDefinition(fieldModelParameters[i].getExpression());
				if (paramUnitDef == null){
					issueList.add(new Issue(fieldModelParameters[i], "Units","defined unit is null for parameter '"+fieldModelParameters[i].getName()+"'",Issue.SEVERITY_WARNING));
				}else if (expUnitDef == null){
					issueList.add(new Issue(fieldModelParameters[i], "Units","computed unit is null for parameter '"+fieldModelParameters[i].getName()+"'",Issue.SEVERITY_WARNING));
				}else if (paramUnitDef.isTBD() || (!paramUnitDef.compareEqual(expUnitDef) && !expUnitDef.isTBD())){
					issueList.add(new Issue(fieldModelParameters[i], "Units","unit mismatch for parameter '"+fieldModelParameters[i].getName()+"' computed = ["+expUnitDef.getSymbol()+"]",Issue.SEVERITY_WARNING));
				}
			}catch (cbit.vcell.units.VCUnitException e){
				issueList.add(new Issue(fieldModelParameters[i],"Units","units inconsistent for parameter '"+fieldModelParameters[i].getName()+"': "+e.getMessage(),Issue.SEVERITY_WARNING));
			}catch (cbit.vcell.parser.ExpressionException e){
				issueList.add(new Issue(fieldModelParameters[i],"Units","units inconsistent for parameter '"+fieldModelParameters[i].getName()+"': "+e.getMessage(),Issue.SEVERITY_WARNING));
			}
		}
	}catch (Throwable e){
		issueList.add(new Issue(this,"Units","unexpected exception: "+e.getMessage(),Issue.SEVERITY_INFO));
	}
	
	//
	// get issues from all ReactionSteps
	//
	for (int i = 0; i < fieldReactionSteps.length; i++){
		fieldReactionSteps[i].gatherIssues(issueList);
	}
}


/**
 * This method was created in VisualAge.
 * @return java.util.Enumeration
 * @param structure cbit.vcell.model.Structure
 */
public Structure[] getChildStructures(Structure structure) {

	Vector childList = new Vector();

	for (int i=0;i<fieldStructures.length;i++){
		if (fieldStructures[i].getParentStructure()==structure){
			childList.addElement(fieldStructures[i]);
		}
	}
	Structure structures[] = new Structure[childList.size()];
	childList.copyInto(structures);
	return structures;
}


/**
 * Gets the description property (java.lang.String) value.
 * @return The description property value.
 * @see #setDescription
 */
public java.lang.String getDescription() {
	return fieldDescription;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Diagram
 * @param structure cbit.vcell.model.Structure
 */
public Diagram getDiagram(Structure structure) throws RuntimeException {
	for (int i=0;i<fieldDiagrams.length;i++){
		if (fieldDiagrams[i].getStructure() == structure){
			return fieldDiagrams[i];
		}
	}
	if (getStructure(structure.getName())==null){
		throw new RuntimeException("structure "+structure.getName()+" not present in model");
	}
	return null;
}


/**
 * Gets the diagrams property (cbit.vcell.model.Diagram[]) value.
 * @return The diagrams property value.
 * @see #setDiagrams
 */
public cbit.vcell.model.Diagram[] getDiagrams() {
	return fieldDiagrams;
}


/**
 * Gets the diagrams index property (cbit.vcell.model.Diagram) value.
 * @return The diagrams property value.
 * @param index The index value into the property array.
 * @see #setDiagrams
 */
public Diagram getDiagrams(int index) {
	return getDiagrams()[index];
}


/**
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	
	cbit.vcell.parser.SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
	return getNameScope().getExternalEntry(identifierString);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getFreeFeatureName() {
	String featureName = "Feature";
	int count=0;
	while (getStructure(featureName+count)!=null){
		count++;
	}
	return featureName+count;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getFreeFluxReactionName() {
	String fluxStepName = "flux";
	int count=0;
	while (getReactionStep(fluxStepName+count)!=null){
		count++;
	}
	return fluxStepName+count;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getFreeMembraneName() {
	String membraneName = "Membrane";
	int count=0;
	while (getStructure(membraneName+count)!=null){
		count++;
	}
	return membraneName+count;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getFreeReactionStepName() {
	String reactionStepName = "reaction";
	int count=0;
	while (getReactionStep(reactionStepName+count)!=null){
		count++;
	}
	return reactionStepName+count;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getFreeSpeciesName() {
	String speciesName = "species";
	int count=0;
	while (getSpecies(speciesName+count)!=null){
		count++;
	}
	return speciesName+count;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public KeyValue getKey() {
	return (getVersion()!=null)?(getVersion().getVersionKey()):null;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.SpeciesContext
 * @param species cbit.vcell.model.Species
 */
public Kinetics.KineticsParameter getKineticsParameter(String kineticsParameterName) {
	for (int i=0;i<fieldReactionSteps.length;i++){
		Kinetics.KineticsParameter parm = fieldReactionSteps[i].getKinetics().getKineticsParameter(kineticsParameterName);
		if (parm!=null){
			return parm;
		}
	}
	return null;		
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 10:03:05 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public cbit.vcell.parser.SymbolTableEntry getLocalEntry(java.lang.String identifier) throws ExpressionBindingException {
	
	cbit.vcell.parser.SymbolTableEntry ste = ReservedSymbol.fromString(identifier);
	if (ste != null){
		ReservedSymbol rs = (ReservedSymbol)ste;
		if (rs.isX() || rs.isY() || rs.isZ()){
			throw new ExpressionBindingException("can't use x, y, or z, Physiological Models must be spatially independent");
		}
		return rs;
	}	

	//
	// get Voltages from structures
	//
	for (int i = 0; i < fieldStructures.length; i++){
		if (fieldStructures[i] instanceof Membrane){
			MembraneVoltage membraneVoltage = ((Membrane)fieldStructures[i]).getMembraneVoltage();
			if (membraneVoltage.getName().equals(identifier)){
				return membraneVoltage;
			}
		}
	}
	
	return getSpeciesContext(identifier);
}


/**
 * Gets the modelParameters property (cbit.vcell.model.ModelParameter[]) value.
 * @return The modelParameters property value.
 * @see #setModelParameters
 */
public cbit.vcell.model.Model.ModelParameter[] getModelParameters() {
	return fieldModelParameters;
}


/**
 * Gets the modelParameters index property (cbit.vcell.model.ModelParameter) value.
 * @return The modelParameters property value.
 * @param index The index value into the property array.
 * @see #setModelParameters
 */
public ModelParameter getModelParameters(int index) {
	return getModelParameters()[index];
}


/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public java.lang.String getName() {
	return fieldName;
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 10:03:05 PM)
 * @return cbit.vcell.parser.NameScope
 */
public cbit.vcell.parser.NameScope getNameScope() {
	return nameScope;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getNumSpecies() {
	return fieldSpecies.length;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getNumSpeciesContexts() {
	return fieldSpeciesContexts.length;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getNumStructures() {
	return fieldStructures.length;
}


/**
 * Accessor for the propertyChange field.
 */
protected PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionStep
 * @param reactionStepName java.lang.String
 */
public ReactionStep getReactionStep(String reactionStepName) {
	if (reactionStepName == null){
		return null;
	}	
	for (int i=0;i<fieldReactionSteps.length;i++){
		if (fieldReactionSteps[i].getName().equals(reactionStepName)){
			return fieldReactionSteps[i];
		}
	}
	return null;
}


/**
 * Gets the reactionSteps property (cbit.vcell.model.ReactionStep[]) value.
 * @return The reactionSteps property value.
 * @see #setReactionSteps
 */
public cbit.vcell.model.ReactionStep[] getReactionSteps() {
	return fieldReactionSteps;
}


/**
 * Gets the reactionSteps index property (cbit.vcell.model.ReactionStep) value.
 * @return The reactionSteps property value.
 * @param index The index value into the property array.
 * @see #setReactionSteps
 */
public ReactionStep getReactionSteps(int index) {
	return getReactionSteps()[index];
}


/**
 * Gets the species property (cbit.vcell.model.Species[]) value.
 * @return The species property value.
 * @see #setSpecies
 */
public cbit.vcell.model.Species[] getSpecies() {
	return fieldSpecies;
}


/**
 * Gets the species index property (cbit.vcell.model.Species) value.
 * @return The species property value.
 * @param index The index value into the property array.
 * @see #setSpecies
 */
public Species getSpecies(int index) {
	return getSpecies()[index];
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2003 4:19:29 PM)
 * @return cbit.vcell.model.Species
 * @param speciesReference cbit.vcell.dictionary.SpeciesReference
 */
public Species[] getSpecies(cbit.vcell.dictionary.DBSpecies dbSpecies) {
	if (dbSpecies == null){
		throw new IllegalArgumentException("DBSpecies was null");
	}
	Vector speciesList = new Vector();
	for (int i = 0; i < fieldSpecies.length; i++){
		if (fieldSpecies[i].getDBSpecies()!=null && fieldSpecies[i].getDBSpecies().compareEqual(dbSpecies)){
			speciesList.add(fieldSpecies[i]);
		}
	}
	Species speciesArray[] = (Species[])BeanUtils.getArray(speciesList,Species.class);
	return speciesArray;
}


public Species getSpecies(String speciesName)
{
	if (speciesName == null){
		return null;
	}	
	for (int i=0;i<fieldSpecies.length;i++){
		if (speciesName.equals(fieldSpecies[i].getCommonName())){
			return fieldSpecies[i];
		}
	}
	return null;
}      


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.SpeciesContext
 * @param species cbit.vcell.model.Species
 */
public SpeciesContext getSpeciesContext(Species species, Structure structure) {
	for (int i=0;i<fieldSpeciesContexts.length;i++){
		if (fieldSpeciesContexts[i].getSpecies().compareEqual(species) && 
			fieldSpeciesContexts[i].getStructure().compareEqual(structure)){
			return fieldSpeciesContexts[i];
		}
	}
	return null;		
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.SpeciesContext
 * @param species cbit.vcell.model.Species
 */
public SpeciesContext getSpeciesContext(String speciesContextName) {
	for (int i=0;i<fieldSpeciesContexts.length;i++){
		if (fieldSpeciesContexts[i].getName().equals(speciesContextName)){
			return fieldSpeciesContexts[i];
		}
	}
	return null;		
}


/**
 * Gets the speciesContexts property (cbit.vcell.model.SpeciesContext[]) value.
 * @return The speciesContexts property value.
 * @see #setSpeciesContexts
 */
public cbit.vcell.model.SpeciesContext[] getSpeciesContexts() {
	return fieldSpeciesContexts;
}


/**
 * Gets the speciesContexts index property (cbit.vcell.model.SpeciesContext) value.
 * @return The speciesContexts property value.
 * @param index The index value into the property array.
 * @see #setSpeciesContexts
 */
public SpeciesContext getSpeciesContexts(int index) {
	return getSpeciesContexts()[index];
}


/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 */
public SpeciesContext[] getSpeciesContexts(Structure structure) {
	Vector scList = new Vector();
	
	for (int i=0;i<fieldSpeciesContexts.length;i++){
		if (fieldSpeciesContexts[i].getStructure().equals(structure)){
			scList.addElement(fieldSpeciesContexts[i]);
		}
	}

	SpeciesContext scArray[] = new SpeciesContext[scList.size()];
	scList.copyInto(scArray);
	return scArray;
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2005 4:13:20 PM)
 * @return cbit.vcell.model.Species[]
 * @param movingFeature cbit.vcell.model.Feature
 * @param destinationFeature cbit.vcell.model.Feature
 */
public SpeciesContext[] getSpeciesContextsNeededByMovingMembrane(Membrane movingMembrane) {

	//Find any species that are needed by reactions in the membrane of movingFeature
	Feature outsideFeature = (Feature)movingMembrane.getParentStructure();
	SpeciesContext[] outSC = getSpeciesContexts(outsideFeature);
	Vector neededSC = new Vector();
	for(int i=0;i<fieldReactionSteps.length;i+= 1){
		if(fieldReactionSteps[i].getStructure() == movingMembrane){
			for(int j=0;j<outSC.length;j+= 1){
				if(fieldReactionSteps[i].getReactionParticipant(outSC[j]) != null){
					if(!neededSC.contains(outSC[j])){
						neededSC.add(outSC[j]);
					}
				}
			}
		}
	}

	if(neededSC.size() > 0){
		SpeciesContext[] scArr = new SpeciesContext[neededSC.size()];
		neededSC.copyInto(scArr);
		return scArr;
	}

	return null;
}


public String[] getSpeciesNames(){
	Vector nameList = new Vector();
	for (int i=0;i<fieldSpecies.length;i++){
		nameList.add(fieldSpecies[i].getCommonName());
	}
	String names[] = new String[nameList.size()];
	nameList.copyInto(names);
	return names;
}               


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Feature
 * @param featureName java.lang.String
 */
public Structure getStructure(String structureName) {
	if (structureName==null){
		return null;
	}
	for (int i=0;i<fieldStructures.length;i++){
		if (structureName.equals(fieldStructures[i].getName())){
			return fieldStructures[i];
		}
	}
	return null;
}


/**
 * Gets the structures property (cbit.vcell.model.Structure[]) value.
 * @return The structures property value.
 * @see #setStructures
 */
public cbit.vcell.model.Structure[] getStructures() {
	return fieldStructures;
}


/**
 * Gets the structures index property (cbit.vcell.model.Structure) value.
 * @return The structures property value.
 * @param index The index value into the property array.
 * @see #setStructures
 */
public Structure getStructures(int index) {
	return getStructures()[index];
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Feature
 */
public cbit.vcell.model.Feature getTopFeature() {
	Feature topFeature = null;
	int topCount = 0;
	for (int i=0;i<fieldStructures.length;i++){
		if (fieldStructures[i].getParentStructure()==null){
			topCount++;
			topFeature = (Feature)fieldStructures[i];
		}
	}
	if (topCount>1){
		throw new RuntimeException("Feature.getTopFeature(), there are more than one top level structures");
	}
	return topFeature;
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2005 5:11:59 PM)
 * @return cbit.vcell.model.Feature[]
 * @param movingFeature cbit.vcell.model.Feature
 */
public Feature[] getValidDestinationsForMovingFeature(Feature movingFeature) {
	
	if(movingFeature == null){
		throw new IllegalArgumentException("moving feature cannot be null");
	}
	 if(!contains(movingFeature)){
		 throw new IllegalArgumentException("Model does not contain moving or feature");
	 }
	if(movingFeature.getMembrane() == null){
		return null;
	}

	//Following code adapted from GraphModel.showShapeHierarchyTopDown
	//Destinations can't be child of moving feature
	Vector invalidDestinationFeatures = new Vector();
	invalidDestinationFeatures.add(movingFeature);
	//Let's not put where we already are as a valid destination for moving
	if(movingFeature.getMembrane() != null){
		invalidDestinationFeatures.add((Feature)movingFeature.getMembrane().getParentStructure());
	}
	Vector features = new Vector();
	for(int i=0;i<getStructures().length;i+= 1){
		if(fieldStructures[i] instanceof Feature){
			features.add(fieldStructures[i]);
		}
	}
	Stack stack = new Stack();
	stack.push(movingFeature);
	features.remove(movingFeature);
	while (stack.size()>0){
		boolean bChildFound = false;
		Feature currFeature = (Feature)stack.peek();
		for(int i=0;i<features.size();i+= 1){
			Feature nextFeature = (Feature)features.elementAt(i);
			if(nextFeature.getMembrane() != null && currFeature == nextFeature.getMembrane().getParentStructure()){
				invalidDestinationFeatures.add(nextFeature);
				stack.push(nextFeature);
				features.remove(nextFeature);
				bChildFound = true;
				break;
			}
		}
		if (bChildFound == false){
			stack.pop();
		}
	}

	Vector validDestinationFeaturesV = new Vector();
	for(int i=0;i<fieldStructures.length;i+= 1){
		if(fieldStructures[i] instanceof Feature && !invalidDestinationFeatures.contains(fieldStructures[i])){
			validDestinationFeaturesV.add(fieldStructures[i]);
		}
	}

	if(validDestinationFeaturesV.size() > 0){
		Feature[] validDestinationFeaturesArr = new Feature[validDestinationFeaturesV.size()];
		validDestinationFeaturesV.copyInto(validDestinationFeaturesArr);
		return validDestinationFeaturesArr;
	}
	
	return null;
}


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
 * This method was created in VisualAge.
 * @return Version
 */
public Version getVersion() {
	return version;
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
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param speciesContext cbit.vcell.model.SpeciesContext
 */
public boolean isUsed(SpeciesContext speciesContext) {
	for (int i=0;i<fieldReactionSteps.length;i++){
		if (fieldReactionSteps[i].getReactionParticipant(speciesContext)!=null){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2005 3:00:44 PM)
 * @param movingFeature cbit.vcell.model.Feature
 * @param destination cbit.vcell.model.Feature
 */
public void moveFeature(Feature movingFeature, Feature destinationFeature) throws Exception{

	if(movingFeature == null || destinationFeature == null){
		throw new IllegalArgumentException("moving and destination feature cannot be null");
	}
	 if(!contains(movingFeature) || !contains(destinationFeature)){
		 throw new IllegalArgumentException("Model does not contain moving or destination feature");
	 }
	if(movingFeature.getMembrane() == null){
		throw new IllegalArgumentException("Can't move top feature");
	}
	if(movingFeature.getMembrane().getParentStructure() == destinationFeature){
		return;//Already there
	}

	//Check if destination is valid
	Feature[] validDestinationFeatures = getValidDestinationsForMovingFeature(movingFeature);
	if(!BeanUtils.arrayContains(validDestinationFeatures,destinationFeature)){
		throw new IllegalArgumentException("'"+destinationFeature.getName()+"' Not a valid destination for '"+movingFeature.getName()+"'");
	}

	//Add SpeciesContext that membrane reactions will need in the new location
	SpeciesContext[] neededSC = getSpeciesContextsNeededByMovingMembrane(movingFeature.getMembrane());
	if(neededSC != null){
		for(int i=0;i<neededSC.length;i+= 1){
			if(getSpeciesContext(neededSC[i].getSpecies(),destinationFeature) == null){
				addSpeciesContext(neededSC[i].getSpecies(),destinationFeature);
			}
		}
	}

	//Update ReactionParticipants with their new location and refresh Reactions
	Feature movingFeatureOldParent = (Feature)movingFeature.getMembrane().getParentStructure();
	movingFeature.getMembrane().setParentStructure(destinationFeature);
	Structure[] structureArr = (Structure[])fieldStructures.clone();
	setStructures(structureArr);
	for(int i=0;i<fieldReactionSteps.length;i+= 1){
		if(fieldReactionSteps[i].getStructure() == movingFeature.getMembrane()){
			if(neededSC != null){
				for(int j=0;j<neededSC.length;j+= 1){
					ReactionParticipant rp = fieldReactionSteps[i].getReactionParticipant(neededSC[j]);
					if(rp != null){
						rp.setSpeciesContext(getSpeciesContext(neededSC[j].getSpecies(),destinationFeature));
					}
				}
			}
			fieldReactionSteps[i].rebindAllToModel(this);
			fieldReactionSteps[i].refreshDependencies();
		}
	}
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() instanceof ReactionStep && evt.getPropertyName().equals("kinetics")){
		Kinetics oldKinetics = (Kinetics)evt.getOldValue();
		Kinetics newKinetics = (Kinetics)evt.getNewValue();
		if (oldKinetics!=null){
			oldKinetics.removePropertyChangeListener(this);
			oldKinetics.removeVetoableChangeListener(this);
		}
		if (newKinetics!=null){
			newKinetics.addPropertyChangeListener(this);
			newKinetics.addVetoableChangeListener(this);
		}
	}
	if (evt.getSource() instanceof SpeciesContext && evt.getPropertyName().equals("name")){
		for (int i = 0; i < fieldDiagrams.length; i++){
			fieldDiagrams[i].renameSpeciesNode((String)evt.getOldValue(),(String)evt.getNewValue());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/00 10:50:08 PM)
 */
public void refreshDependencies() {

	removePropertyChangeListener(this);
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
	addPropertyChangeListener(this);
	
	for (int i=0;i<fieldStructures.length;i++){
		fieldStructures[i].removePropertyChangeListener(this);
		fieldStructures[i].removeVetoableChangeListener(this);
		fieldStructures[i].addPropertyChangeListener(this);
		fieldStructures[i].addVetoableChangeListener(this);
		fieldStructures[i].setModel(this);
	}
	
	for (int i=0;i<fieldReactionSteps.length;i++){
		fieldReactionSteps[i].removePropertyChangeListener(this);
		fieldReactionSteps[i].removeVetoableChangeListener(this);
		fieldReactionSteps[i].getKinetics().removePropertyChangeListener(this);
		fieldReactionSteps[i].getKinetics().removeVetoableChangeListener(this);
		fieldReactionSteps[i].getKinetics().addPropertyChangeListener(this);
		fieldReactionSteps[i].getKinetics().addVetoableChangeListener(this);
		fieldReactionSteps[i].addPropertyChangeListener(this);
		fieldReactionSteps[i].addVetoableChangeListener(this);
		fieldReactionSteps[i].setModel(this);
		try {
			fieldReactionSteps[i].rebindAllToModel(this);
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
		fieldReactionSteps[i].refreshDependencies();
	}
	
	for (int i=0;i<fieldSpeciesContexts.length;i++){
		fieldSpeciesContexts[i].removePropertyChangeListener(this);
		fieldSpeciesContexts[i].removeVetoableChangeListener(this);
		fieldSpeciesContexts[i].addPropertyChangeListener(this);
		fieldSpeciesContexts[i].addVetoableChangeListener(this);
		fieldSpeciesContexts[i].setModel(this);

		fieldSpeciesContexts[i].refreshDependencies();
	}
	
	for (int i=0;i<fieldSpecies.length;i++){
		fieldSpecies[i].removeVetoableChangeListener(this);
		fieldSpecies[i].addVetoableChangeListener(this);
		fieldSpecies[i].refreshDependencies();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 4:38:17 PM)
 */
private void refreshDiagrams() {
    //
    // removed diagrams for those structures that were removed
    //
    boolean bChangedDiagrams = false;
    Diagram newDiagrams[] = (Diagram[]) fieldDiagrams.clone();
    for (int i = 0; i < fieldDiagrams.length; i++) {
        if (!contains(fieldDiagrams[i].getStructure())) {
            newDiagrams =
                (Diagram[]) BeanUtils.removeElement(newDiagrams, fieldDiagrams[i]);
            bChangedDiagrams = true;
        }
    }
    //
    // add new diagrams for new structures
    //
    for (int i = 0; i < fieldStructures.length; i++) {
        if (getDiagram(fieldStructures[i]) == null) {
            newDiagrams = (Diagram[]) BeanUtils.addElement(newDiagrams,new Diagram(fieldStructures[i], fieldStructures[i].getName()));
            bChangedDiagrams = true;
        }
    }

    if (bChangedDiagrams) {
        try {
            setDiagrams(newDiagrams);
        } catch (PropertyVetoException e) {
            e.printStackTrace(System.out);
            throw new RuntimeException(e.getMessage());
        }
    }

}


public void removeFeature(Feature removedFeature) throws PropertyVetoException {

	if (removedFeature == null){
		throw new RuntimeException("feature is null");
	}	
	if (!contains(removedFeature)){
		throw new RuntimeException("feature "+removedFeature.getName()+" not found");
	}
	
	//Check that the feature is empty
	Structure checkThisStructure = removedFeature;
	String errorMessage = null;
	Feature topChildFeature = null;
	Membrane topChildMembrane = null;
	while(true){
		for (int i=0;i<fieldReactionSteps.length;i++){
			if (fieldReactionSteps[i].getStructure() == checkThisStructure){
				errorMessage = "cannot contain Reactions";
				break;
			}
		}
		for (int i=0;i<fieldSpeciesContexts.length;i++){
			if (fieldSpeciesContexts[i].getStructure() == checkThisStructure){
				errorMessage = "cannot contain Species";
				break;
			}
		}

		if(errorMessage != null){
			break;
		}
		if(checkThisStructure == removedFeature){
			checkThisStructure = removedFeature.getMembrane();
			if(checkThisStructure == null){//Top Feature
				//Must have a child
				if(fieldStructures.length == 1){
					throw new RuntimeException(
						"Remove model compartment Error\n"+
						"Feature to be removed '"+removedFeature.getName()+"' "+
						" is TopLevel and has no promotable children");
				}
				//Must be only 1 child
				for (int i=0;i<fieldStructures.length;i++){
					if (fieldStructures[i] instanceof Membrane && ((Membrane)fieldStructures[i]).getOutsideFeature() == removedFeature){
						if(topChildFeature != null){
							throw new RuntimeException(
								"Remove model compartment Error\n"+
								"Feature to be removed '"+removedFeature.getName()+"' "+
								" is TopLevel and can have only 1 promotable child");
						}
						topChildMembrane = (Membrane)fieldStructures[i];
						topChildFeature = topChildMembrane.getInsideFeature();
					}
				}
				checkThisStructure = topChildMembrane;
				
			}
		}else{
			break;
		}		
	}
	
	if(errorMessage != null){
		if(checkThisStructure == removedFeature){
			throw new RuntimeException(
				"Remove model compartment Error\nFeature to be removed '"+removedFeature.getName()+"' "+errorMessage+".");
		}else if(checkThisStructure == removedFeature.getMembrane()){
			throw new RuntimeException(
				"Remove model compartment Error\nMembrane '"+removedFeature.getMembrane().getName()+
				"' associated with Feature '"+removedFeature.getName()+"' "+errorMessage+
				" because it will be removed along with compartment '"+removedFeature.getName()+"'");
		}else if(checkThisStructure != null && checkThisStructure == topChildMembrane){
			throw new RuntimeException(
				"Remove model compartment '"+removedFeature.getName()+"' Error\nMembrane '"+topChildMembrane.getName()+
				"' child of TopLevel Feature '"+removedFeature.getName()+"' "+errorMessage+
				" because it will be removed when compartment '"+topChildFeature.getName()+"' is promoted to TopLevel.");
		}else{
			//We should never get here
			throw new RuntimeException(
				"Remove model compartment Error\nFeature to be removed '"+removedFeature.getName()+"' "+errorMessage+".\n"+
				"associated structure = "+checkThisStructure);
		}
	}

	
	////
	//// first, remove all reaction steps contained by this feature and bounding membrane
	////
	//ReactionStep newReactionSteps[] = (ReactionStep[])fieldReactionSteps.clone();
	//for (int i=0;i<newReactionSteps.length;i++){
		//if (newReactionSteps[i].getStructure()==removedFeature){
			//newReactionSteps = (ReactionStep[])BeanUtils.removeElement(newReactionSteps,newReactionSteps[i]);
			//i--;
		//}
	//}
	//if (removedFeature.getMembrane()!=null){
		//for (int i=0;i<newReactionSteps.length;i++){
			//if (newReactionSteps[i].getStructure()==removedFeature.getMembrane()){
				//newReactionSteps = (ReactionStep[])BeanUtils.removeElement(newReactionSteps,newReactionSteps[i]);
				//i--;
			//}
		//}
	//}
	//setReactionSteps(newReactionSteps);

	////
	//// remove all species Contexts for this feature and accompanying membrane
	//// this will fail if there are still ReactionSteps (outside the removed structures) that use them.
	////
	//// this is transactional
	////
	////
	//SpeciesContext allSpeciesContexts[] = (SpeciesContext[])fieldSpeciesContexts.clone();
	//SpeciesContext structureSpeciesContexts[] = getSpeciesContexts(removedFeature);
	//for (int i=0;i<structureSpeciesContexts.length;i++){
		//allSpeciesContexts = (SpeciesContext[])BeanUtils.removeElement(allSpeciesContexts,structureSpeciesContexts[i]);
	//}
	//if (removedFeature.getMembrane()!=null){
		//structureSpeciesContexts = getSpeciesContexts(removedFeature.getMembrane());
		//for (int i=0;i<structureSpeciesContexts.length;i++){
			//allSpeciesContexts = (SpeciesContext[])BeanUtils.removeElement(allSpeciesContexts,structureSpeciesContexts[i]);
		//}
	//}
	//setSpeciesContexts(allSpeciesContexts);

	
	//
	// remove this feature and it's membrane
	//
	Structure newStructures[] = (Structure[])fieldStructures.clone();
	newStructures = (Structure[])BeanUtils.removeElement(newStructures,removedFeature);
	
	Feature parentFeature = null;
	if (removedFeature.getMembrane()!=null){
		// remove the corresponding membrane
		parentFeature = removedFeature.getMembrane().getOutsideFeature();
		newStructures = (Structure[])BeanUtils.removeElement(newStructures,removedFeature.getMembrane());
	}else if(topChildMembrane == null){
		//This should never happen, was checked earlier
		throw new RuntimeException(
			"Remove model compartment Error\nFeature to be removed '"+removedFeature.getName()+
			" has no membrane and no promotable child");
	}else{
		//TopLevel remove immediate child membrane
		newStructures = (Structure[])BeanUtils.removeElement(newStructures,topChildMembrane);
		//Make child feature TopLevel
		topChildFeature.setMembrane(null);
	}
	
	//
	// set children of 'feature' to parent of feature's membrane
	//
	if(parentFeature != null){
		for (int i=0;i<newStructures.length;i++){
			if (newStructures[i] instanceof Membrane){
				Membrane m = (Membrane)newStructures[i];
				Feature outsideFeature = m.getOutsideFeature();
				if (outsideFeature == removedFeature){
					m.setOutsideFeature(parentFeature);
				}
			}	
		}
	}
	////
	//// if there is no parent, then make first child a parent and remove that child's membrane
	////
	//for (int i=0;i<newStructures.length;i++){
		//if (newStructures[i] instanceof Membrane){
			//Membrane m = (Membrane)newStructures[i];
			//Feature outsideFeature = m.getOutsideFeature();
			//if (outsideFeature==null){
				//parentFeature = m.getInsideFeature();
				//parentFeature.setMembrane(null);
				//newStructures = (Structure[])BeanUtils.removeElement(newStructures,m);
				//break;
			//}	
		//}
	//}	
	////
	//// make other orphaned children the children of parentFeature
	////
	//for (int i=0;i<newStructures.length;i++){
		//if (newStructures[i] instanceof Membrane){
			//Membrane m = (Membrane)newStructures[i];
			//Feature outsideFeature = m.getOutsideFeature();
			//if (outsideFeature==null){
				//m.setOutsideFeature(parentFeature);
			//}	
		//}
	//}
	setStructures(newStructures);
}            


public void removeModelParameter(Model.ModelParameter modelParameter) throws PropertyVetoException {

	if (modelParameter == null){
		return;
	}	
	if (contains(modelParameter)){
		Model.ModelParameter newModelParameters[] = (Model.ModelParameter[])BeanUtils.removeElement(fieldModelParameters,modelParameter);
		setModelParameters(newModelParameters);
	}
}         


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * This method was created in VisualAge.
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
public void removeReactionStep(ReactionStep reactionStep) throws PropertyVetoException {
	if (contains(reactionStep)){
		setReactionSteps((ReactionStep[])BeanUtils.removeElement(fieldReactionSteps,reactionStep));
	}
}


public void removeSpecies(Species species) throws PropertyVetoException {

	if (species == null){
		return;
	}	
	if (contains(species)){
		Species newSpeciesArray[] = (Species[])BeanUtils.removeElement(fieldSpecies,species);
		setSpecies(newSpeciesArray);
	}
}         


public void removeSpeciesContext(SpeciesContext speciesContext) throws PropertyVetoException {
	if (contains(speciesContext)){
		SpeciesContext newSpeciesContexts[] = (SpeciesContext[])BeanUtils.removeElement(fieldSpeciesContexts,speciesContext);
		setSpeciesContexts(newSpeciesContexts);
	}
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


/**
 * Sets the description property (java.lang.String) value.
 * @param description The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getDescription
 */
public void setDescription(java.lang.String description) throws java.beans.PropertyVetoException {
	String oldValue = fieldDescription;
	fireVetoableChange("description", oldValue, description);
	fieldDescription = description;
	firePropertyChange("description", oldValue, description);
}


/**
 * Sets the diagrams property (cbit.vcell.model.Diagram[]) value.
 * @param diagrams The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getDiagrams
 */
public void setDiagrams(cbit.vcell.model.Diagram[] diagrams) throws java.beans.PropertyVetoException {
	cbit.vcell.model.Diagram[] oldValue = fieldDiagrams;
	fireVetoableChange("diagrams", oldValue, diagrams);
	fieldDiagrams = diagrams;
	firePropertyChange("diagrams", oldValue, diagrams);
}


/**
 * Sets the diagrams index property (cbit.vcell.model.Diagram[]) value.
 * @param index The index value into the property array.
 * @param diagrams The new value for the property.
 * @see #getDiagrams
 */
public void setDiagrams(int index, Diagram diagrams) {
	Diagram oldValue = fieldDiagrams[index];
	fieldDiagrams[index] = diagrams;
	if (oldValue != null && !oldValue.equals(diagrams)) {
		firePropertyChange("diagrams", null, fieldDiagrams);
	};
}


/**
 * Sets the modelParameters property (cbit.vcell.model.ModelParameter[]) value.
 * @param modelParameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getModelParameters
 */
public void setModelParameters(Model.ModelParameter[] modelParameters) throws java.beans.PropertyVetoException {
	Model.ModelParameter[] oldValue = fieldModelParameters;
	fireVetoableChange("modelParameters", oldValue, modelParameters);
	fieldModelParameters = modelParameters;
	firePropertyChange("modelParameters", oldValue, modelParameters);
	
	//ModelParameter newValue[] = modelParameters;
	//for (int i=0;i<oldValue.length;i++){	
		//oldValue[i].removePropertyChangeListener(this);
		//oldValue[i].removeVetoableChangeListener(this);
	//}
	//for (int i=0;i<newValue.length;i++){	
		//newValue[i].addPropertyChangeListener(this);
		//newValue[i].addVetoableChangeListener(this);
	//}
}


/**
 * Sets the modelParameters index property (cbit.vcell.model.ModelParameter[]) value.
 * @param index The index value into the property array.
 * @param modelParameters The new value for the property.
 * @see #getModelParameters
 */
public void setModelParameters(int index, ModelParameter modelParameters) {
	ModelParameter oldValue = fieldModelParameters[index];
	fieldModelParameters[index] = modelParameters;
	if (oldValue != null && !oldValue.equals(modelParameters)) {
		firePropertyChange("modelParameters", null, fieldModelParameters);
	};
}


/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}


/**
 * Sets the reactionSteps property (cbit.vcell.model.ReactionStep[]) value.
 * @param reactionSteps The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getReactionSteps
 */
public void setReactionSteps(cbit.vcell.model.ReactionStep[] reactionSteps) throws java.beans.PropertyVetoException {
	cbit.vcell.model.ReactionStep[] oldValue = fieldReactionSteps;
	fireVetoableChange("reactionSteps", oldValue, reactionSteps);
	fieldReactionSteps = reactionSteps;
	firePropertyChange("reactionSteps", oldValue, reactionSteps);

	ReactionStep newValue[] = reactionSteps;
	for (int i=0;i<oldValue.length;i++){	
		oldValue[i].removePropertyChangeListener(this);
		oldValue[i].removeVetoableChangeListener(this);
		oldValue[i].getKinetics().removePropertyChangeListener(this);
		oldValue[i].getKinetics().removeVetoableChangeListener(this);
	}
	for (int i=0;i<newValue.length;i++){	
		newValue[i].addPropertyChangeListener(this);
		newValue[i].addVetoableChangeListener(this);
		newValue[i].getKinetics().addPropertyChangeListener(this);
		newValue[i].getKinetics().addVetoableChangeListener(this);
		newValue[i].setModel(this);
		try {
			newValue[i].rebindAllToModel(this);
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
}


/**
 * Sets the reactionSteps index property (cbit.vcell.model.ReactionStep[]) value.
 * @param index The index value into the property array.
 * @param reactionSteps The new value for the property.
 * @see #getReactionSteps
 */
public void setReactionSteps(int index, ReactionStep reactionSteps) {
	ReactionStep oldValue = fieldReactionSteps[index];
	fieldReactionSteps[index] = reactionSteps;
	if (oldValue != null && !oldValue.equals(reactionSteps)) {
		firePropertyChange("reactionSteps", null, fieldReactionSteps);
	};
}


/**
 * Sets the species property (cbit.vcell.model.Species[]) value.
 * @param species The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSpecies
 */
public void setSpecies(cbit.vcell.model.Species[] species) throws java.beans.PropertyVetoException {
	cbit.vcell.model.Species[] oldValue = fieldSpecies;
	fireVetoableChange("species", oldValue, species);
	fieldSpecies = species;
	firePropertyChange("species", oldValue, species);
	
	Species newValue[] = species;
	for (int i=0;i<oldValue.length;i++){	
		//oldValue[i].removePropertyChangeListener(this);
		oldValue[i].removeVetoableChangeListener(this);
	}
	for (int i=0;i<newValue.length;i++){	
		//newValue[i].addPropertyChangeListener(this);
		newValue[i].addVetoableChangeListener(this);
	}
}


/**
 * Sets the species index property (cbit.vcell.model.Species[]) value.
 * @param index The index value into the property array.
 * @param species The new value for the property.
 * @see #getSpecies
 */
public void setSpecies(int index, Species species) {
	Species oldValue = fieldSpecies[index];
	fieldSpecies[index] = species;
	if (oldValue != null && !oldValue.equals(species)) {
		firePropertyChange("species", null, fieldSpecies);
	};
}


/**
 * Sets the speciesContexts property (cbit.vcell.model.SpeciesContext[]) value.
 * @param speciesContexts The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSpeciesContexts
 */
public void setSpeciesContexts(cbit.vcell.model.SpeciesContext[] speciesContexts) throws java.beans.PropertyVetoException {
	cbit.vcell.model.SpeciesContext[] oldValue = fieldSpeciesContexts;
	fireVetoableChange("speciesContexts", oldValue, speciesContexts);
	fieldSpeciesContexts = speciesContexts;
	firePropertyChange("speciesContexts", oldValue, speciesContexts);

	SpeciesContext newValue[] = speciesContexts;
	for (int i=0;i<oldValue.length;i++){	
		oldValue[i].removePropertyChangeListener(this);
		oldValue[i].removeVetoableChangeListener(this);
		oldValue[i].setModel(null);
	}
	for (int i=0;i<newValue.length;i++){	
		newValue[i].addPropertyChangeListener(this);
		newValue[i].addVetoableChangeListener(this);
		newValue[i].setModel(this);
	}
	//
	//Remove orphaned Species but only for SpeciesContext that were in old and not in new
	//The API should be changed so that species cannot be added or retrieved independently of SpeciesContexts
	//
	List oldSpeciesContextsList = Arrays.asList(oldValue);
	List newSpeciesContextsList = Arrays.asList(newValue);
	for(int i = 0;i < oldSpeciesContextsList.size();i+= 1){
		if(!newSpeciesContextsList.contains(oldSpeciesContextsList.get(i))){
			try{
				removeSpecies(((SpeciesContext)oldSpeciesContextsList.get(i)).getSpecies());
			}catch(Throwable e){
				e.printStackTrace(System.out);
				//Do nothing for now since this is only a kludge to cleanup orphan species
				//so "invisible" species don't interfere with gui operations
			}
		}
	}
}


/**
 * Sets the speciesContexts index property (cbit.vcell.model.SpeciesContext[]) value.
 * @param index The index value into the property array.
 * @param speciesContexts The new value for the property.
 * @see #getSpeciesContexts
 */
public void setSpeciesContexts(int index, SpeciesContext speciesContexts) {
	SpeciesContext oldValue = fieldSpeciesContexts[index];
	speciesContexts.setModel(this);
	fieldSpeciesContexts[index] = speciesContexts;
	if (oldValue != null && !oldValue.equals(speciesContexts)) {
		firePropertyChange("speciesContexts", null, fieldSpeciesContexts);
	};
}


/**
 * Sets the structures property (cbit.vcell.model.Structure[]) value.
 * @param structures The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getStructures
 */
public void setStructures(cbit.vcell.model.Structure[] structures) throws java.beans.PropertyVetoException {
	cbit.vcell.model.Structure[] oldValue = fieldStructures;
	fireVetoableChange("structures", oldValue, structures);
	fieldStructures = structures;
	refreshDiagrams();
	firePropertyChange("structures", oldValue, structures);


	Structure newValue[] = structures;
	for (int i=0;i<oldValue.length;i++){	
		oldValue[i].removePropertyChangeListener(this);
		oldValue[i].removeVetoableChangeListener(this);
		oldValue[i].setModel(null);
	}
	for (int i=0;i<newValue.length;i++){	
		newValue[i].addPropertyChangeListener(this);
		newValue[i].addVetoableChangeListener(this);
		newValue[i].setModel(this);
	}
	
//	showStructureHierarchy();
}


/**
 * Insert the method's description here.
 * Creation date: (3/22/01 12:12:10 PM)
 */
public void showStructureHierarchy() {
	Vector structList = new Vector(Arrays.asList(fieldStructures));

	//
	// gather top(s) ... should only have one
	//
	Vector topList = new Vector();
	for (int i=0;i<structList.size();i++){
		if (((Structure)structList.elementAt(i)).getParentStructure() == null){
			topList.add(structList.elementAt(i));
		}
	}
	//
	// for each top, print tree
	//
	Stack stack = new Stack();
	for (int j=0;j<topList.size();j++){
		Structure top = (Structure)topList.elementAt(j);
		System.out.println(top.getName());
		stack.push(top);
		while (true){
			//
			// find first remaining children of current parent and print
			//
			boolean bChildFound = false;
			for (int i=0;i<structList.size() && stack.size()>0;i++){
				Structure structure = (Structure)structList.elementAt(i);
				if (structure.getParentStructure() == stack.peek()){
					char padding[] = new char[4*stack.size()];
					for (int k=0;k<padding.length;k++) padding[k] = ' ';
					String pad = new String(padding);
					System.out.println(pad+structure.getName());
					stack.push(structure);
					structList.remove(structure);
					bChildFound = true;
					break;
				}
			}
			if (stack.size()==0){
				break;
			}
			if (bChildFound == false){
				stack.pop();
			}
		}
	}	
		
			
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	return "Model@"+Integer.toHexString(hashCode())+"("+((version!=null)?version.toString():getName())+")";
}


/**
 * This method was created in VisualAge.
 * @param e java.beans.PropertyChangeEvent
 * @exception java.beans.PropertyVetoException The exception description.
 */
public void vetoableChange(PropertyChangeEvent e) throws java.beans.PropertyVetoException {
	if (e.getSource() instanceof Structure){
		if (e.getPropertyName().equals("name") && !e.getNewValue().equals(e.getOldValue())){
			if (getStructure((String)e.getNewValue())!=null){
				throw new PropertyVetoException("another structure already using name "+e.getNewValue(),e);
			}
		}
	}
	if (e.getSource() instanceof ReactionStep){
		if (e.getPropertyName().equals("name") && !e.getNewValue().equals(e.getOldValue())){
			String newName = (String)e.getNewValue();
			if (getReactionStep(newName)!=null){
				throw new PropertyVetoException("another reaction step is already using name '"+newName+"'",e);
			}
			if (ReservedSymbol.fromString(newName)!=null){
				throw new PropertyVetoException("cannot use a reserved symbol ('x','y','z','t') as a SpeciesContext name",e);
			}
			//
			// make sure not to change name to that of an existing speciesContext ... (this IS necessary with namespaces).
			//
			SpeciesContext sc = getSpeciesContext(newName);
			if (sc != null){
				throw new PropertyVetoException("a "+sc.getTerm()+" defined in '"+sc.getStructure().getName()+"' already uses name '"+e.getNewValue()+"'",e);
			}
			//
			// make sure not to change name to that of an existing kinetic parameter ... (is this still necessary with namespaces ????).
			//
			//for (int j = 0; j < fieldReactionSteps.length; j++){
				//if (fieldReactionSteps[j].getKinetics().getKineticsParameter(newName)!=null){
					//throw new PropertyVetoException(fieldReactionSteps[j].getTerm()+" '"+fieldReactionSteps[j].getName()+"' in '"+fieldReactionSteps[j].getStructure().getName()+"' already has a parameter named '"+newName+"'",e);
				//}
			//}
			//
			// make sure not to change name of a ReactionStep to that of a Membrane Voltage name  ... (is this still necessary with namespaces ????).
			//
			for (int j = 0; j < fieldStructures.length; j++){
				if (fieldStructures[j] instanceof Membrane && ((Membrane)fieldStructures[j]).getMembraneVoltage().getName().equals(newName)){
					throw new PropertyVetoException("name '"+newName+"' already defined as Membrane Voltage in Membrane '"+fieldStructures[j].getName()+"'",e);
				}
			}
		}
	}
	if (e.getSource() instanceof SpeciesContext){
		if (e.getPropertyName().equals("name") && !e.getNewValue().equals(e.getOldValue())){
			String newName = (String)e.getNewValue();
			if (ReservedSymbol.fromString(newName)!=null){
				throw new PropertyVetoException("cannot use a reserved symbol ('x','y','z','t') as a SpeciesContext name",e);
			}
			SpeciesContext sc = getSpeciesContext(newName);
			if (sc != null){
				throw new PropertyVetoException("another "+sc.getTerm()+" defined in '"+sc.getStructure().getName()+"' already uses name '"+e.getNewValue()+"'",e);
			}
			//
			// make sure not to change name to that of an existing kinetic parameter ... (is this still necessary with namespaces ????).
			//
			//for (int j = 0; j < fieldReactionSteps.length; j++){
				//if (fieldReactionSteps[j].getKinetics().getKineticsParameter(newName)!=null){
					//throw new PropertyVetoException(fieldReactionSteps[j].getTerm()+" '"+fieldReactionSteps[j].getName()+"' in '"+fieldReactionSteps[j].getStructure().getName()+"' already has a parameter named '"+newName+"'",e);
				//}
			//}
			//
			// make sure not to change name of a SpeciesContext to that of a Membrane Voltage name ... (is this still necessary with namespaces ????).
			//
			for (int j = 0; j < fieldStructures.length; j++){
				if (fieldStructures[j] instanceof Membrane && ((Membrane)fieldStructures[j]).getMembraneVoltage().getName().equals(newName)){
					throw new PropertyVetoException("name '"+newName+"' already defined as Membrane Voltage in Membrane '"+fieldStructures[j].getName()+"'",e);
				}
			}
			//
			// make sure not to change name of a SpeciesContext to that of a ReactionStep name .... (this IS necessary with namespaces)
			//
			for (int j = 0; j < fieldReactionSteps.length; j++){
				if (fieldReactionSteps[j].getName().equals(newName)){
					throw new PropertyVetoException(fieldReactionSteps[j].getTerm()+" '"+fieldReactionSteps[j].getName()+"' in '"+fieldReactionSteps[j].getStructure().getName()+"' is already named '"+newName+"'",e);
				}
			}
		}
	}
	//if (e.getSource() instanceof Kinetics){
		//Kinetics kinetics = (Kinetics)e.getSource();
		//if (e.getPropertyName().equals("kineticsParameters")){
			//KineticsParameter parms[] = (KineticsParameter[])e.getNewValue();
			//if (parms != null){
				//for (int i=0;i<parms.length;i++){
					//if (ReservedSymbol.fromString(parms[i].getName())!=null){
						//throw new PropertyVetoException("cannot use a reserved symbol ('x','y','z','t') as a Parameter name",e);
					//}
					//KineticsParameter existingParm = getKineticsParameter(parms[i].getName());
					////
					//// make sure new parameter doesn't have the same name as a parameter from another ReactionStep
					////
					//if (existingParm != null && existingParm != parms[i] && 
						//existingParm.getKinetics().getReactionStep() != parms[i].getKinetics().getReactionStep()){
						//throw new PropertyVetoException(existingParm.getKinetics().getReactionStep().getTerm()+" '"+existingParm.getKinetics().getReactionStep().getName()+"' in '"+existingParm.getKinetics().getReactionStep().getStructure().getName()+"' already has a parameter called '"+parms[i].getName()+"'",e);
					//}
					//SpeciesContext sc = getSpeciesContext(parms[i].getName());
					//if (sc != null){
						//throw new PropertyVetoException(sc.getTerm()+" '"+sc.getName()+"' defined in '"+sc.getStructure().getName()+"', cannot create a parameter with same name",e);
					//}
					////
					//// make sure not to create a kinetics parameter with same name as a Membrane Voltage name
					////
					//for (int j = 0; j < fieldStructures.length; j++){
						//if (fieldStructures[j] instanceof Membrane && ((Membrane)fieldStructures[j]).getMembraneVoltage().getName().equals(parms[i].getName())){
							//throw new PropertyVetoException("name '"+parms[i].getName()+"' already defined as Membrane Voltage in Membrane '"+fieldStructures[j].getName()+"', cannot create a parameter with same name",e);
						//}
					//}
				//}
			//}
		//}
	//}
	if (e.getSource() == this && e.getPropertyName().equals("structures")){
		Structure topStructure = null;
		Structure newStructures[] = (Structure[])e.getNewValue();
		if (newStructures==null){
			throw new PropertyVetoException("structures cannot be null",e);
		}
		HashSet nameSet = new HashSet();
		int topCount = 0;
		for (int i=0;i<newStructures.length;i++){
			if (nameSet.contains(newStructures[i].getName())){
				throw new PropertyVetoException("multiple structures with name '"+newStructures[i].getName()+"' defined",e);
			}
			nameSet.add(newStructures[i].getName());
			if (newStructures[i] instanceof Feature){
				if (newStructures[i].getParentStructure()==null){
					topStructure = newStructures[i];
					topCount++;
				}
			}else if (newStructures[i] instanceof Membrane){
				if (((Membrane)newStructures[i]).getInsideFeature()==null ||
					((Membrane)newStructures[i]).getOutsideFeature()==null){
					throw new PropertyVetoException("membrane '"+newStructures[i].getName()+"' should have inside and outside features",e);
				}
			}
		}
		if (topCount==0){
			throw new PropertyVetoException("there are no top-level features",e);
		}else if (topCount>1){
			throw new PropertyVetoException("there is more than one top-level feature",e);
		}
		//
		// make sure all members are children of the root and all children are in the array
		//
		//
		for (int i=0;i<newStructures.length;i++){
			if (newStructures[i] == topStructure) continue;
			Structure parent = newStructures[i];
			int loopCount = 0;
			while (parent.getParentStructure() != null && loopCount<20){
				//
				// check that parent is in list
				//
				boolean bFound = false;
				for (int j=0;j<newStructures.length;j++){
					if (newStructures[j] == parent.getParentStructure()){
						bFound = true;
					}
				}
				if (!bFound){
					throw new PropertyVetoException("Structure "+parent.getName()+"'s parent '"+parent.getParentStructure().getName()+"' is not in array",e);
				}
				parent = parent.getParentStructure();
				loopCount++;
			}
			if (loopCount >= 20){
				throw new PropertyVetoException("Structure "+newStructures[i].getName()+" has a cyclic ancestry",e);
			}
			if (parent != topStructure){
				throw new PropertyVetoException("Structure "+parent.getName()+"'s parent '"+parent.getParentStructure().getName()+"' is not in array",e);
			}
		}
	}
	if (e.getSource() == this && e.getPropertyName().equals("species")){
		Species newSpeciesArray[] = (Species[])e.getNewValue();
		if (newSpeciesArray==null){
			throw new PropertyVetoException("species cannot be null",e);
		}
		//
		// check that names are not duplicated and that no common names are ReservedSymbols
		//
		HashSet commonNameSet = new HashSet();
		for (int i=0;i<newSpeciesArray.length;i++){
			if (commonNameSet.contains(newSpeciesArray[i].getCommonName())){
				throw new PropertyVetoException("multiple species with common name '"+newSpeciesArray[i].getCommonName()+"' defined",e);
			}
			if (ReservedSymbol.fromString(newSpeciesArray[i].getCommonName())!=null){
				throw new PropertyVetoException("cannot use a reserved symbol ('x','y','z','t') as a Species common name",e);
			}
			commonNameSet.add(newSpeciesArray[i].getCommonName());
		}
		//
		// if species deleted, must not have any SpeciesContexts that need it
		//
		for (int j=0;j<fieldSpeciesContexts.length;j++){
			SpeciesContext sc = fieldSpeciesContexts[j];
			boolean bFound = false;
			for (int i=0;i<newSpeciesArray.length;i++){
				if (newSpeciesArray[i] == sc.getSpecies()){
					bFound = true;
				}
			}
			if (!bFound){
				throw new PropertyVetoException("species[] missing '"+sc.getSpecies().getCommonName()+"' referenced in SpeciesContext '"+sc.getName()+"'",e);
			}
		}
	}

	//
	// Check new Species commonName is legal
	//
	if (e.getSource() instanceof Species && e.getPropertyName().equals("commonName")){
		String commonName = (String)e.getNewValue();
		if (commonName==null){
			throw new PropertyVetoException("species name cannot be null",e);
		}
		//
		// check that new name is not duplicated and that new Name isn't ReservedSymbols
		//
		if (getSpecies(commonName) != null){
			throw new PropertyVetoException("Species with common name '"+commonName+"' already defined",e);
		}
		if (ReservedSymbol.fromString(commonName)!=null){
			throw new PropertyVetoException("cannot use a reserved symbol ('x','y','z','t') as a Species common name",e);
		}
	}

	
	if (e.getSource() == this && e.getPropertyName().equals("speciesContexts")){
		SpeciesContext newSpeciesContextArray[] = (SpeciesContext[])e.getNewValue();
		if (newSpeciesContextArray==null){
			throw new PropertyVetoException("speciesContexts cannot be null",e);
		}
		//
		// check that the species and structure for each speciesContext already exist.
		//
		for (int i=0;i<newSpeciesContextArray.length;i++){
			if (!contains(newSpeciesContextArray[i].getSpecies())){
				throw new PropertyVetoException("can't add speciesContext '"+newSpeciesContextArray[i].getName()+"' before species '"+newSpeciesContextArray[i].getSpecies().getCommonName()+"'",e);
			}
			if (!contains(newSpeciesContextArray[i].getStructure())){
				throw new PropertyVetoException("can't add speciesContext '"+newSpeciesContextArray[i].getName()+"' before structure '"+newSpeciesContextArray[i].getStructure().getName()+"'",e);
			}
		}
		//
		// check that names are not duplicated and that no names are ReservedSymbols
		//
		HashSet nameSet = new HashSet();
		for (int i=0;i<newSpeciesContextArray.length;i++){
			if (nameSet.contains(newSpeciesContextArray[i].getName())){
				throw new PropertyVetoException("multiple speciesContexts with name '"+newSpeciesContextArray[i].getName()+"' defined",e);
			}
			if (ReservedSymbol.fromString(newSpeciesContextArray[i].getName())!=null){
				throw new PropertyVetoException("cannot use a reserved symbol ('x','y','z','t') as a SpeciesContext name",e);
			}
			//
			// make sure not to change name to that of an existing kinetic parameter ... (is this still necessary with namespaces ????).
			//
			//for (int j = 0; j < fieldReactionSteps.length; j++){
				//if (fieldReactionSteps[j].getKinetics().getKineticsParameter(newSpeciesContextArray[i].getName())!=null){
					//throw new PropertyVetoException(fieldReactionSteps[j].getTerm()+" '"+fieldReactionSteps[j].getName()+"' in '"+fieldReactionSteps[j].getStructure().getName()+"' already has a parameter named '"+newSpeciesContextArray[i].getName()+"'",e);
				//}
			//}
			//
			// make sure not to change name of a SpeciesContext to that of a Membrane Voltage name ... (is this still necessary with namespaces ????).
			//
			for (int j = 0; j < fieldStructures.length; j++){
				if (fieldStructures[j] instanceof Membrane && ((Membrane)fieldStructures[j]).getMembraneVoltage().getName().equals(newSpeciesContextArray[i].getName())){
					throw new PropertyVetoException("name '"+newSpeciesContextArray[i].getName()+"' already defined as Membrane Voltage in Membrane '"+fieldStructures[j].getName()+"'",e);
				}
			}
			//
			// make sure not to change name of a SpeciesContext to that of a ReactionStep name .... (this IS necessary with namespaces)
			//
			for (int j = 0; j < fieldReactionSteps.length; j++){
				if (fieldReactionSteps[j].getName().equals(newSpeciesContextArray[i].getName())){
					throw new PropertyVetoException(fieldReactionSteps[j].getTerm()+" '"+fieldReactionSteps[j].getName()+"' in '"+fieldReactionSteps[j].getStructure().getName()+"' is already named '"+newSpeciesContextArray[i].getName()+"'",e);
				}
			}
			nameSet.add(newSpeciesContextArray[i].getName());
		}
		//
		// make sure that reactionParticipants point to speciesContext
		//
		for (int i=0;i<fieldReactionSteps.length;i++){
			ReactionParticipant rpArray[] = fieldReactionSteps[i].getReactionParticipants();
			for (int k = 0; k < rpArray.length; k++) {
				boolean bFound = false;
				for (int j=0;j<newSpeciesContextArray.length;j++){
					if (newSpeciesContextArray[j] == rpArray[k].getSpeciesContext()){
						bFound = true;
					}
				}
				if (!bFound){
					throw new PropertyVetoException("reaction '"+fieldReactionSteps[i].getName()+"' requires '"+rpArray[k].getSpeciesContext().getName()+"'",e);
				}
			}
		}
	}
	//
	//"reactionSteps" VetoableChanges were never checked before
	//
	if (e.getSource() == this && e.getPropertyName().equals("reactionSteps")){
		ReactionStep[] newReactionStepArr = (ReactionStep[])e.getNewValue();
		//
		//Check because a null could get this far and would throw a NullPointerException later
		//None of the other PropertyVeto checks do this.  Do We Want To Keep This????
		//
		for(int i =0;i<newReactionStepArr.length;i+= 1){
			if(newReactionStepArr[i] == null){
				throw new PropertyVetoException("Null cannot be added to ReactionStep",e);
			}
		}
		//
		// check that names are not duplicated and that no names are ReservedSymbols
		//because math generator complained if reactionsteps had same name
		//
		HashSet nameSet = new HashSet();
		for (int i=0;i<newReactionStepArr.length;i++){
			if (nameSet.contains(newReactionStepArr[i].getName())){
				throw new PropertyVetoException("multiple reactionSteps with name '"+newReactionStepArr[i].getName()+"' defined",e);
			}
			if (ReservedSymbol.fromString(newReactionStepArr[i].getName())!=null){
				throw new PropertyVetoException("cannot use a reserved symbol ('x','y','z','t') as a ReactionStep name",e);
			}
			//
			// make sure not to change name to that of an existing speciesContext ... (this IS necessary with namespaces).
			//
			SpeciesContext sc = getSpeciesContext(newReactionStepArr[i].getName());
			if (sc != null){
				throw new PropertyVetoException("a "+sc.getTerm()+" defined in '"+sc.getStructure().getName()+"' already uses name '"+newReactionStepArr[i].getName()+"'",e);
			}
			//
			// make sure not to change name to that of an existing kinetic parameter ... (is this still necessary with namespaces ????).
			//
			//for (int j = 0; j < fieldReactionSteps.length; j++){
				//if (fieldReactionSteps[j].getKinetics().getKineticsParameter(newReactionStepArr[i].getName())!=null){
					//throw new PropertyVetoException(fieldReactionSteps[j].getTerm()+" '"+fieldReactionSteps[j].getName()+"' in '"+fieldReactionSteps[j].getStructure().getName()+"' already has a parameter named '"+newReactionStepArr[i].getName()+"'",e);
				//}
			//}
			//
			// make sure not to change name of a ReactionStep to that of a Membrane Voltage name  ... (is this still necessary with namespaces ????).
			//
			for (int j = 0; j < fieldStructures.length; j++){
				if (fieldStructures[j] instanceof Membrane && ((Membrane)fieldStructures[j]).getMembraneVoltage().getName().equals(newReactionStepArr[i].getName())){
					throw new PropertyVetoException("name '"+newReactionStepArr[i].getName()+"' already defined as Membrane Voltage in Membrane '"+fieldStructures[j].getName()+"'",e);
				}
			}
			nameSet.add(newReactionStepArr[i].getName());
		}
		//
		// make sure that reactionParticipants point to speciesContext that exist
		//because reactionsteps could be added that had speciescontext that model didn't
		//
		for (int i=0;i<newReactionStepArr.length;i++){
			ReactionParticipant rpArray[] = newReactionStepArr[i].getReactionParticipants();
			for (int k = 0; k < rpArray.length; k++) {
				boolean bFound = false;
				for (int j=0;j<fieldSpeciesContexts.length;j++){
					if (fieldSpeciesContexts[j] == rpArray[k].getSpeciesContext()){
						bFound = true;
					}
				}
				if (!bFound){
					throw new PropertyVetoException("reaction '"+newReactionStepArr[i].getName()+"' requires '"+rpArray[k].getSpeciesContext().getName()+"'",e);
				}
			}
		}
	}

}


/**
 * This method was created by a SmartGuide.
 * @param ps java.io.PrintStream
 * @exception java.lang.Exception The exception description.
 */
public void writeTokens(java.io.PrintWriter pw) {
	String versionName = (getName()!=null)?getName():"unnamed_model";
	pw.println(VCMODL.Model+" "+versionName+" {");
	for (int i=0;i<fieldSpecies.length;i++){
		pw.println(VCMODL.Species+" "+fieldSpecies[i].getCommonName());
	}
	for (int i=0;i<fieldStructures.length;i++){
		fieldStructures[i].writeTokens(pw,this);
	}
	for (int i=0;i<fieldReactionSteps.length;i++){
		fieldReactionSteps[i].writeTokens(pw);	
	}
	for (int i=0;i<fieldDiagrams.length;i++){
		fieldDiagrams[i].write(pw);	
	}
	pw.println("}");
}
}