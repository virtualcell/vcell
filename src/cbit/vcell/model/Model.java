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
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;

import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.VCUnitEvaluator;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitException;

@SuppressWarnings("serial")
public class Model implements Versionable, Matchable, PropertyChangeListener, VetoableChangeListener, Serializable, ScopedSymbolTable {
	
	public static interface Owner {
		public Model getModel();
	}
	
	public static final String PROPERTY_NAME_REACTION_STEPS = "reactionSteps";
	public static final String PROPERTY_NAME_STRUCTURES = "structures";
	public static final String PROPERTY_NAME_SPECIES_CONTEXTS = "speciesContexts";
	private static final String PROPERTY_NAME_SPECIES = "species";
	
	public static final String PROPERTY_NAME_RATERULEVARIABLES = "rateruleVariables";
	
	private Version version = null;
	protected transient PropertyChangeSupport propertyChange;
	private java.lang.String fieldName = new String("NoName");
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private java.lang.String fieldDescription = new String();
	private Structure[] fieldStructures = new Structure[0];
	private Species[] fieldSpecies = new Species[0];
	private SpeciesContext[] fieldSpeciesContexts = new SpeciesContext[0];
//	private RateRuleVariable[] fieldRateRuleVariables = new RateRuleVariable[0];
	private ReactionStep[] fieldReactionSteps = new ReactionStep[0];
	private Diagram[] fieldDiagrams = new Diagram[0];
	private ModelNameScope nameScope = new Model.ModelNameScope();
	private Model.ModelParameter[] fieldModelParameters = new Model.ModelParameter[0];
	private Model.ReservedSymbol[] fieldReservedSymbols = new Model.ReservedSymbol[0];
	private final ModelUnitSystem unitSystem;
	private transient VCMetaData vcMetaData = null;
	private StructureTopology structureTopology = new StructureTopology();
	private ElectricalTopology electricalTopology = new ElectricalTopology();

	public class StructureTopology implements Serializable, Matchable {
		private HashMap<Membrane,Feature> insideFeatures = new HashMap<Membrane, Feature>();
		private HashMap<Membrane,Feature> outsideFeatures = new HashMap<Membrane, Feature>();
		private HashMap<Feature,Membrane> enclosingMembrane = new HashMap<Feature, Membrane>();
		
		public StructureTopology(){
		}
		
//		public List<Structure> getChildStructures(Structure structure) {
//
//			ArrayList<Structure> childList = new ArrayList<Structure>();
//
//			for (int i=0;i<fieldStructures.length;i++){
//				if (getParentStructure(fieldStructures[i])==structure){
//					childList.add(fieldStructures[i]);
//				}
//			}
//			return childList;
//		}

		public Enumeration<Feature> getSubFeatures(Feature feature) 
		{
			Vector<Feature> subFeatures = new Vector<Feature>();
			Structure[] structures = getStructures();
			for (int i=0; i<structures.length; i++)
			{
				if((structures[i] instanceof Feature) && enclosedBy(structures[i], feature))
				{
					subFeatures.addElement((Feature)structures[i]);
				}
			}
			return subFeatures.elements();
		}

		
		public Membrane getMembrane(Feature feature1, Feature feature2){
			for (int i = 0; i < fieldStructures.length; i++) {
				if (fieldStructures[i] instanceof Membrane){
					Membrane membrane = (Membrane)fieldStructures[i];
					if (insideFeatures.get(membrane)==feature1 && outsideFeatures.get(membrane)==feature2){ 
						return membrane;
					}
					if (insideFeatures.get(membrane)==feature2 && outsideFeatures.get(membrane)==feature1){
						return membrane;
					}
				}
			}
			return null;
		}

		public Structure getParentStructure(Structure structure) {
			if (structure instanceof Membrane){
				return outsideFeatures.get(structure);
			}else{
				return enclosingMembrane.get(structure);
			}
		}


		public void setInsideFeature(Membrane membrane, Feature insideFeature){
			enclosingMembrane.put(insideFeature, membrane);
			insideFeatures.put(membrane, insideFeature);
		}
		public void setOutsideFeature(Membrane membrane, Feature outsideFeature){
			outsideFeatures.put(membrane, outsideFeature);
		}
		public void setMembrane(Feature feature, Membrane membrane){
			enclosingMembrane.put(feature, membrane);
			insideFeatures.put(membrane, feature);
		}
		
		public Membrane getMembrane(Feature feature){
			return enclosingMembrane.get(feature);
		}

		public Feature getInsideFeature(Membrane membrane) {
			return insideFeatures.get(membrane);
		}

		public Feature getOutsideFeature(Membrane membrane) {
			return outsideFeatures.get(membrane);
		}
		
		public void refresh() {
			Structure[] structures = getStructures();
			for (Structure struct : structures) {
				if (struct instanceof Feature) {
					Membrane enclosingMem = getMembrane((Feature)struct);
					if (enclosingMem != null && !contains(enclosingMem)) {
						enclosingMembrane.remove(struct);
						if (getInsideFeature(enclosingMem) != null) {
							insideFeatures.remove(enclosingMem);
						}
						if (getOutsideFeature(enclosingMem) != null) {
							outsideFeatures.remove(enclosingMem);
						}
					}
				} else if (struct instanceof Membrane) {
					Membrane membrane = (Membrane)struct;
					Feature insideFeature = getInsideFeature(membrane);
					if (insideFeature != null && !(contains(insideFeature))) {
						insideFeatures.remove(membrane);
						enclosingMembrane.remove(insideFeature);
					}
					Feature outsideFeature = getOutsideFeature(membrane);
					if (outsideFeature != null && !(contains(outsideFeature))) {
						outsideFeatures.remove(membrane);
					}
				}
			}
		}
		
		public boolean enclosedBy(Structure structure, Structure parentStructure){
			if (structure instanceof Feature){
				Feature feature = (Feature) structure;
				if (parentStructure == feature){
					return true;
				}	
				if (getMembrane(feature) != null){
					return enclosedBy(getMembrane(feature),parentStructure);
				}	
				return false;
			}else if (structure instanceof Membrane){
				Membrane membrane = (Membrane)structure;
				if (parentStructure == membrane){
					return true;
				}	
				return enclosedBy(getOutsideFeature(membrane),parentStructure);
			}else{
				throw new IllegalArgumentException("unexpected argument of StructureTopology.enclosedBy()");
			}
		}
		@Override
		public boolean compareEqual(Matchable object) {
			if (object instanceof StructureTopology){
				StructureTopology structTopology = (StructureTopology)object;
			
				if (!Compare.isEqual(insideFeatures, structTopology.insideFeatures)){
					return false;
				}
				
				if (!Compare.isEqual(outsideFeatures, structTopology.outsideFeatures)) {
					return false;
				}
	
				if (!Compare.isEqual(enclosingMembrane, structTopology.enclosingMembrane)){
					return false;
				}
				return true;
			}
			return false;
		}
		
		
		/*public String showStructureHierarchy() {
			StringBuffer strbuffer = new StringBuffer();
			ArrayList<Structure> structList = new ArrayList<Structure>(Arrays.asList(fieldStructures));

			//
			// gather top(s) ... should only have one
			//
			ArrayList<Structure> topList = new ArrayList<Structure>();
			for (Structure s : structList){
				if (getParentStructure(s) == null){
					topList.add(s);
				}
			}
			//
			// for each top, print tree
			//
			Stack<Structure> stack = new Stack<Structure>();
			for (int j=0;j<topList.size();j++){
				Structure top = topList.get(j);
				strbuffer.append(top.getName()+"\n");
				stack.push(top);
				while (true){
					//
					// find first remaining children of current parent and print
					//
					boolean bChildFound = false;
					for (int i=0;i<structList.size() && stack.size()>0;i++){
						Structure structure = structList.get(i);
						if (getParentStructure(structure) == stack.peek()){
							char padding[] = new char[4*stack.size()];
							for (int k=0;k<padding.length;k++) padding[k] = ' ';
							String pad = new String(padding);
							strbuffer.append(pad+structure.getName()+"\n");
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
			return strbuffer.toString();
		}*/
		
	}

	public class ElectricalTopology implements Serializable, Matchable {
		
		private HashMap<Membrane,Feature> positiveFeatures = new HashMap<Membrane, Feature>();
		private HashMap<Membrane,Feature> negativeFeatures = new HashMap<Membrane, Feature>();
		
		public ElectricalTopology() {
		}

		public void setPositiveFeature(Membrane membrane, Feature insideFeature){
			positiveFeatures.put(membrane, insideFeature);
		}
		public void setNegativeFeature(Membrane membrane, Feature outsideFeature){
			negativeFeatures.put(membrane, outsideFeature);
		}

		public Feature getPositiveFeature(Membrane membrane) {
			return positiveFeatures.get(membrane);
		}

		public Feature getNegativeFeature(Membrane membrane) {
			return negativeFeatures.get(membrane);
		}
		
		public void gatherIssues(List<Issue> issueList) {
			// check if membranes in model have positive and negative features set.
			for (Structure struct : getStructures()) {
				if (struct instanceof Membrane) {
					Membrane membrane = (Membrane)struct;
					String issueMsgPrefix = "Membrane '" + membrane.getName() + "' ";
					Feature positiveFeature = getPositiveFeature(membrane);
					if (positiveFeature == null) {
						issueList.add(new Issue(membrane, IssueCategory.MembraneElectricalPolarityError, "Positive feature of " + issueMsgPrefix + "not set. It is required for electrical mapping.", Issue.SEVERITY_WARNING));
					}
					Feature negativeFeature = getNegativeFeature(membrane);
					if (negativeFeature == null) {
						issueList.add(new Issue(membrane, IssueCategory.MembraneElectricalPolarityError, "Negative feature of " + issueMsgPrefix + "not set. It is required for electrical mapping.", Issue.SEVERITY_WARNING));
					}
					if (positiveFeature != null && negativeFeature != null && positiveFeature.compareEqual(negativeFeature)) {
						issueList.add(new Issue(membrane, IssueCategory.MembraneElectricalPolarityError, "Positive and Negative features of " + issueMsgPrefix + " cannot be the same.", Issue.SEVERITY_ERROR));
					}
				}
			}
		}
		
		public void populateFromStructureTopology() {
			// if the positive & negative features for the membranes are already set, do not override using struct topology.
			Structure[] structures = getStructures();
			for (Structure struct : structures) {
				if (struct instanceof Membrane) {
					Membrane membrane = (Membrane)struct;
					Feature positiveFeatureFromStructTopology = getStructureTopology().getInsideFeature(membrane);
					Feature positiveFeatureFromElectricalTopology = getPositiveFeature(membrane);
					if (positiveFeatureFromStructTopology != null) {
						if (positiveFeatureFromElectricalTopology != null && !contains(positiveFeatureFromElectricalTopology)) {
							// if there is an entry in the positiveFeature hashMap for the membrane, but the feature is not in model, remove entry in hashMap
							electricalTopology.positiveFeatures.remove(membrane);
						} else {
							// if positiveFeature from structTopology != null, and the membrane does not have an entry in positiveFeatures hashMap, add it.
							electricalTopology.setPositiveFeature(membrane, positiveFeatureFromStructTopology);
						}
					} else {
						// if there is no positiveFeature from structTopology, and the membrane's entry in positiveFeatures hashMap is not in the model, remove the entry.
						if (positiveFeatureFromElectricalTopology != null && !contains(positiveFeatureFromElectricalTopology)) {
							electricalTopology.positiveFeatures.remove(membrane);
						}
					} 
					
					Feature negativeFeatureFromStructTopology = getStructureTopology().getOutsideFeature(membrane);
					Feature negativeFeatureFromElectricalTopology = getNegativeFeature(membrane);
					if (negativeFeatureFromStructTopology != null) {
						if (negativeFeatureFromElectricalTopology != null && !contains(negativeFeatureFromElectricalTopology)) {
							// if there is an entry in the negativeFeature hashMap for the membrane, but the feature is not in model, remove entry in hashMap
							electricalTopology.negativeFeatures.remove(membrane);
						} else {
							// if negativeFeature from structTopology != null, and the membrane does not have an entry in negativeFeatures hashMap, add it.
							electricalTopology.setNegativeFeature(membrane, negativeFeatureFromStructTopology);
						}
					} else {
						// if there is no negativeFeature from structTopology, and the membrane's entry in negativeFeatures hashMap is not in the model, remove the entry.
						if (negativeFeatureFromElectricalTopology != null && !contains(negativeFeatureFromElectricalTopology)) {
							electricalTopology.negativeFeatures.remove(membrane);
						}
					} 
				}
			}
		}
		
		public void refresh() {
			// if any membrane has been removed, remove its entry in the positiveFetures and negativeFeatures hashMap (separately?)
			Set<Membrane> membranesSet = positiveFeatures.keySet();
			Iterator<Membrane> membranesIter = membranesSet.iterator();
			while (membranesIter.hasNext()) {
				Membrane membrane = membranesIter.next();
				if (!contains(membrane)) {
					membranesIter.remove();
					// positiveFeatures.remove(membrane);
				}
			}
			membranesSet = negativeFeatures.keySet();
			membranesIter = membranesSet.iterator();
			while (membranesIter.hasNext()) {
				Membrane membrane = membranesIter.next();
				if (!contains(membrane)) {
					membranesIter.remove();
					// negativeFeatures.remove(membrane);
				}
			}

			// now populate electrical topology (+ve features and -ve features hashMap based on structureTopology, if a heirarchy exists.
			populateFromStructureTopology();
		}
		
		@Override
		public boolean compareEqual(Matchable object) {
			if (object instanceof ElectricalTopology){
				ElectricalTopology electricalTopology = (ElectricalTopology)object;
			
				if (!Compare.isEqual(positiveFeatures, electricalTopology.positiveFeatures)){
					return false;
				}
				
				if (!Compare.isEqual(negativeFeatures, electricalTopology.negativeFeatures)) {
					return false;
				}
	
				return true;
			}
			return false;
		}
	}
	
	public class ModelNameScope extends BioNameScope {
		public ModelNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// return list of reactionNameScopes
			//
			NameScope nameScopes[] = new NameScope[Model.this.fieldReactionSteps.length+Model.this.fieldStructures.length];
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
		public NameScope getParent() {
			//System.out.println("ModelNameScope.getParent() returning null ... no parent");
			return null;
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return Model.this;
		}
		public boolean isPeer(NameScope nameScope){
			if (super.isPeer(nameScope)) {
				return true;
			}
			return ((nameScope instanceof BioNameScope) && (((BioNameScope)nameScope).getNamescopeType() == NamescopeType.mathmappingType) && nameScope.isPeer(this));
		}
		@Override
		public String getPathDescription() {
			return "Global";
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.modelType;
		}
	}

	public enum ReservedSymbolRole {
		TIME, 
		X, 
		Y, 
		Z, 
		TEMPERATURE, 
		PI_CONSTANT, 
		FARADAY_CONSTANT, 
		FARADAY_CONSTANT_NMOLE, 
		N_PMOLE, 
		K_GHK, 
		GAS_CONSTANT, 
		KMILLIVOLTS, 
		KMOLE
	};

	public ReservedSymbol getTIME() {
		return getReservedSymbolByRole(ReservedSymbolRole.TIME);
	}


		public ReservedSymbol getX() {
			return getReservedSymbolByRole(ReservedSymbolRole.X);
		}


		public ReservedSymbol getY() {
			return getReservedSymbolByRole(ReservedSymbolRole.Y);
		}


		public ReservedSymbol getZ() {
			return getReservedSymbolByRole(ReservedSymbolRole.Z);
		}


		public ReservedSymbol getTEMPERATURE() {
			return getReservedSymbolByRole(ReservedSymbolRole.TEMPERATURE);
		}


		public ReservedSymbol getPI_CONSTANT() {
			return getReservedSymbolByRole(ReservedSymbolRole.PI_CONSTANT);
		}


		public ReservedSymbol getFARADAY_CONSTANT() {
			return getReservedSymbolByRole(ReservedSymbolRole.FARADAY_CONSTANT);
		}


		public ReservedSymbol getFARADAY_CONSTANT_NMOLE() {
			return getReservedSymbolByRole(ReservedSymbolRole.FARADAY_CONSTANT_NMOLE);
		}


		public ReservedSymbol getN_PMOLE() {
			return getReservedSymbolByRole(ReservedSymbolRole.N_PMOLE);
		}


		public ReservedSymbol getK_GHK() {
			return getReservedSymbolByRole(ReservedSymbolRole.K_GHK);
		}


		public ReservedSymbol getGAS_CONSTANT() {
			return getReservedSymbolByRole(ReservedSymbolRole.GAS_CONSTANT);
		}


		public ReservedSymbol getKMILLIVOLTS() {
			return getReservedSymbolByRole(ReservedSymbolRole.KMILLIVOLTS);
		}

		/** @deprecated : used only for backward compatibility */
		public ReservedSymbol getKMOLE() {
			return getReservedSymbolByRole(ReservedSymbolRole.KMOLE);
		}
	 
	 
	public class ReservedSymbol implements EditableSymbolTableEntry, Serializable
	{
	   private String name = null;
	   private Expression constantValue = null;
	   private String description = null;
	   private VCUnitDefinition unitDefinition = null;
	   private ReservedSymbolRole role = null;

	private ReservedSymbol(ReservedSymbolRole role, String argName, String argDescription, VCUnitDefinition argUnitDefinition, Expression argConstantValue){
		this.role = role;
		this.name = argName;
		this.unitDefinition = argUnitDefinition;
		this.constantValue = argConstantValue;
		this.description = argDescription;
	}         


	public boolean equals(Object obj) {
		if (!(obj instanceof ReservedSymbol)){
			return false;
		}
		ReservedSymbol rs = (ReservedSymbol)obj;
		if (!rs.name.equals(name)){
			return false;
		}
		return true;
	}

	public double getConstantValue() throws ExpressionException {
		throw new ExpressionException(getName()+" is not constant");
	}

	public ReservedSymbolRole getRole(){
		return role;
	}

	   public final String getDescription() 
	   { 
		  return description; 
	   }      


	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.parser.Expression
	 * @exception java.lang.Exception The exception description.
	 */
	public Expression getExpression() {
		return constantValue;
	}


	/**
	 * This method was created in VisualAge.
	 * @return int
	 */
	public int getIndex() {
		return -1;
	}


	   public final String getName() 
	   { 
		  return name; 
	   }      


	public NameScope getNameScope() {
		return Model.this.getNameScope();
	}


	public VCUnitDefinition getUnitDefinition() {
		return unitDefinition;
	}


	public int hashCode() {
		return name.hashCode();
	}

	public boolean isConstant() {
		return false;  //constantValue!=null;
	}


	public String toString()
	{
		return getName();
	}


	public boolean isDescriptionEditable() {
		return false;
	}


	public boolean isExpressionEditable() {
		return false;
	}


	public boolean isNameEditable() {
		return false;
	}


	public boolean isUnitEditable() {
		return false;
	}


	public void setDescription(String description) throws PropertyVetoException {
		throw new RuntimeException("cannot change description of a reserved symbol");
	}


	public void setExpression(Expression expression) throws PropertyVetoException, ExpressionBindingException {
		throw new RuntimeException("cannot change the value of a reserved symbol");
	}


	public void setName(String name) throws PropertyVetoException {
		throw new RuntimeException("cannot rename a reserved symbols");
	}


	public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException {
		throw new RuntimeException("cannot change unit of a reserved symbol");
	}


	public boolean isX() {
		return (this.role.equals(ReservedSymbolRole.X));
	}

	public boolean isY() {
		return (this.role.equals(ReservedSymbolRole.Y));
	}
	public boolean isZ() {
		return (this.role.equals(ReservedSymbolRole.Z));
	}
	public boolean isTime() {
		return (this.role.equals(ReservedSymbolRole.TIME));
	}

	}
	
	public static final int ROLE_UserDefined	= 0;
	public static final int NUM_ROLES		= 1;
	public static final String RoleDesc = "user defined";
	
	public static final String PROPERTY_NAME_MODEL_PARAMETERS = "modelParameters";
	
	public class ModelParameter extends Parameter implements ExpressionContainer {
		
		private String fieldParameterName = null;
		private Expression fieldParameterExpression = null;
		private int fieldParameterRole = -1;
		private VCUnitDefinition fieldUnitDefinition = null;
		private String modelParameterAnnotation;
		
		private static final String MODEL_PARAMETER_DESCRIPTION = "Global Parameter";
		
		public ModelParameter(String argName, Expression expression, int argRole, VCUnitDefinition argUnitDefinition) {
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
			super.setDescription(MODEL_PARAMETER_DESCRIPTION);
		}


		public String getModelParameterAnnotation() {
			return modelParameterAnnotation;
		}
		public void setModelParameterAnnotation(String modelParameterAnnotation) {
			this.modelParameterAnnotation = modelParameterAnnotation;
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

		public double getConstantValue() throws ExpressionException {
			return this.fieldParameterExpression.evaluateConstant();
		}      


		public Expression getExpression() {
			return this.fieldParameterExpression;
		}


		public int getIndex() {
			return -1;
		}


		public String getName(){ 
			return this.fieldParameterName; 
		}   


		public NameScope getNameScope() {
			return Model.this.nameScope;
		}

		public int getRole() {
			return this.fieldParameterRole;
		}

		public VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}

		public void setUnitDefinition(VCUnitDefinition unitDefinition) {
			VCUnitDefinition oldValue = fieldUnitDefinition;
			fieldUnitDefinition = unitDefinition;
			super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
		}
		public void setExpression(Expression expression) throws java.beans.PropertyVetoException {
			Expression oldValue = fieldParameterExpression;
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
		this(argVersion, ModelUnitSystem.createDefaultVCModelUnitSystem());
	}
	
	public Model(Version argVersion, ModelUnitSystem argUnitSystem) {
		this.version = argVersion;
		if (argVersion != null){
			fieldName = argVersion.getName();
			fieldDescription = argVersion.getAnnot();
		}
		addPropertyChangeListener(this);
		addVetoableChangeListener(this);
		
		// initialize a unit system
			this.unitSystem = argUnitSystem;
		
		// initialize the reserved symbols
		fieldReservedSymbols = createReservedSymbols();
	}      	
	
	public Model(String argName) {
		this(argName, ModelUnitSystem.createDefaultVCModelUnitSystem());
	}  


	public Model(String argName, ModelUnitSystem argModelUnitSystem) {
		this.fieldName = argName;
		this.version = null;
		addPropertyChangeListener(this);
		addVetoableChangeListener(this);
	
		// initialize a unit system
		this.unitSystem = argModelUnitSystem;

		// initialize the reserved symbols
		fieldReservedSymbols = createReservedSymbols();
	}      

private ReservedSymbol[] createReservedSymbols() {
	return  new ReservedSymbol[]{ 
			new ReservedSymbol(ReservedSymbolRole.TIME, "t","time", unitSystem.getTimeUnit(), null), 
			new ReservedSymbol(ReservedSymbolRole.X, "x","x coord", unitSystem.getLengthUnit(), null),  
			new ReservedSymbol(ReservedSymbolRole.Y, "y","y coord", unitSystem.getLengthUnit(), null), 
			new ReservedSymbol(ReservedSymbolRole.Z, "z","z coord", unitSystem.getLengthUnit(), null),	
			new ReservedSymbol(ReservedSymbolRole.TEMPERATURE, "_T_","temperature", unitSystem.getTemperatureUnit(), null), 
			new ReservedSymbol(ReservedSymbolRole.PI_CONSTANT, "_PI_","PI constant", unitSystem.getInstance_DIMENSIONLESS(),new Expression(Math.PI)),  
			new ReservedSymbol(ReservedSymbolRole.FARADAY_CONSTANT, "_F_","Faraday constant",unitSystem.getFaradayConstantUnit(), new Expression(9.648e4)),  
			new ReservedSymbol(ReservedSymbolRole.FARADAY_CONSTANT_NMOLE, "_F_nmol_","Faraday constant", unitSystem.getFaradayConstantNMoleUnit(),new Expression(9.648e-5)), 
			new ReservedSymbol(ReservedSymbolRole.N_PMOLE, "_N_pmol_","Avagadro Num (scaled)",unitSystem.getN_PMoleUnit(),new Expression(6.02e11)), 
			new ReservedSymbol(ReservedSymbolRole.K_GHK, "_K_GHK_","GHK unit scale",unitSystem.getK_GHKUnit(), new Expression(1e-9)), 
			new ReservedSymbol(ReservedSymbolRole.GAS_CONSTANT, "_R_","Gas Constant",unitSystem.getGasConstantUnit(), new Expression(8314.0)), 
			new ReservedSymbol(ReservedSymbolRole.KMILLIVOLTS, "K_millivolts_per_volt","voltage scale", unitSystem.getK_mV_perV_Unit(), new Expression(1000)), 
			new ReservedSymbol(ReservedSymbolRole.KMOLE, "KMOLE", "Flux unit conversion", unitSystem.getKMoleUnit(), new Expression(new RationalNumber(1, 602)))
	};
}


//public ModelParameter createModelParameter(String name, Expression expr, int role, VCUnitDefinition units) {
//	ModelParameter modelParameter = new ModelParameter(name, expr, role, units);
//	return modelParameter;
//}   

public ModelParameter addModelParameter(Model.ModelParameter modelParameter) throws PropertyVetoException {
//	if (!contains(modelParameter)){
		Model.ModelParameter newModelParameters[] = (Model.ModelParameter[])BeanUtils.addElement(fieldModelParameters,modelParameter);
		setModelParameters(newModelParameters);
//	}	
	return modelParameter;
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

public Feature addFeature(String featureName) throws ModelException, PropertyVetoException {
	Structure structure = getStructure(featureName);
	if (structure!=null) {
		throw new ModelException("adding feature '"+featureName+"', structure already exists with that name");
	}
	Feature newFeature = new Feature(featureName);
	Structure newStructures[] = (Structure[])BeanUtils.addElement(fieldStructures,newFeature);
	setStructures(newStructures);
	return newFeature;
}

public Membrane addMembrane(String membraneName) throws ModelException, PropertyVetoException {
	Structure structure = getStructure(membraneName);
	if (structure!=null) {
		throw new ModelException("adding membrane '"+membraneName+"', structure already exists with that name");
	}
	Membrane newMembrane = new Membrane(membraneName);
	Structure newStructures[] = (Structure[])BeanUtils.addElement(fieldStructures,newMembrane);
	setStructures(newStructures);
	return newMembrane;
}



public ReactionStep addReactionStep(ReactionStep reactionStep) throws PropertyVetoException {
	if (!contains(reactionStep)) {
		setReactionSteps((ReactionStep[])BeanUtils.addElement(fieldReactionSteps,reactionStep));
	}
	return reactionStep;
}


public Species addSpecies(Species species) throws PropertyVetoException {
	if (!contains(species)){
		Species newSpecies[] = (Species[])BeanUtils.addElement(fieldSpecies,species);
		setSpecies(newSpecies);
	}
	return species;
}   


/**
 * This method was created by a SmartGuide.
 * @param species cbit.vcell.model.Species
 */
public SpeciesContext addSpeciesContext(Species species, Structure structure) throws Exception {
	if (species != getSpecies(species.getCommonName())){
		throw new Exception("species "+species.getCommonName()+" not found in model");
	}
	SpeciesContext speciesContext = getSpeciesContext(species, structure);
	if (speciesContext != null){
		throw new Exception("speciesContext for "+species.getCommonName()+" within "+structure.getName()+" already defined");
	}
	
	speciesContext = new SpeciesContext(species,structure);
	speciesContext.setModel(this);
	return addSpeciesContext(speciesContext);
}


/**
 * This method was created by a SmartGuide.
 * @param structure cbit.vcell.model.Structure
 */
public SpeciesContext addSpeciesContext(SpeciesContext speciesContext) throws PropertyVetoException {
	
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
	return speciesContext;
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
	if (!Compare.isEqualStrict(fieldDiagrams, model.fieldDiagrams)){
		return false;
	}
	if (!Compare.isEqual(fieldModelParameters, model.fieldModelParameters)){
		return false;
	}
	if (!Compare.isEqual(unitSystem, model.unitSystem)){
		return false;
	}
	if (!Compare.isEqual(structureTopology, model.structureTopology)){
		return false;
	}
	if (!Compare.isEqual(electricalTopology, model.electricalTopology)){
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
		if (fieldDiagrams[i] == diagram){
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
		if (fieldModelParameters[i] == modelParameter){
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
		if (fieldReactionSteps[i] == reactionStep){
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
		if (fieldSpecies[i] == species){
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
		if (fieldSpeciesContexts[i] == speciesContext){
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
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 10:38:12 PM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(List<Issue> issueList) {
	//
	// check for unknown units (TBD) and unit consistency
	//
	try {
		for (ModelParameter modelParameter : fieldModelParameters){
			Expression exp = modelParameter.getExpression();
			String symbols[] = exp.getSymbols();
			if (symbols!=null) { 
				String issueMsgPrefix = "Global parameter '" + modelParameter.getName() + "' "; 
				for (int j = 0; j < symbols.length; j++){
					SymbolTableEntry ste = exp.getSymbolBinding(symbols[j]);
					if (ste == null) {
						issueList.add(new Issue(modelParameter,IssueCategory.ModelParameterExpressionError, issueMsgPrefix + "references undefined symbol '" + symbols[j]+"'",Issue.SEVERITY_ERROR));
					} else if (ste instanceof SpeciesContext) {
						if (!contains((SpeciesContext)ste)) {
							issueList.add(new Issue(modelParameter,IssueCategory.ModelParameterExpressionError, issueMsgPrefix + "references undefined species '" + symbols[j]+"'",Issue.SEVERITY_ERROR));
						}						
					} else if (ste instanceof ModelParameter) {
						if (!contains((ModelParameter)ste)) {
							issueList.add(new Issue(modelParameter,IssueCategory.ModelParameterExpressionError, issueMsgPrefix + "references undefined global parameter '" + symbols[j]+"'",Issue.SEVERITY_ERROR));
						}
					}
				}
			}
		}
		//
		// determine unit consistency for each expression
		//
		for (int i = 0; i < fieldModelParameters.length; i++){
			try {
				VCUnitEvaluator unitEvaluator = new VCUnitEvaluator(getUnitSystem());
				VCUnitDefinition paramUnitDef = fieldModelParameters[i].getUnitDefinition();
				VCUnitDefinition expUnitDef = unitEvaluator.getUnitDefinition(fieldModelParameters[i].getExpression());
				if (paramUnitDef == null){
					issueList.add(new Issue(fieldModelParameters[i], IssueCategory.Units,"defined unit is null",Issue.SEVERITY_WARNING));
				} else if (expUnitDef == null){
					issueList.add(new Issue(fieldModelParameters[i], IssueCategory.Units,"computed unit is null",Issue.SEVERITY_WARNING));
				} else if (paramUnitDef.isTBD()) {
					issueList.add(new Issue(fieldModelParameters[i], IssueCategory.Units,"unit is undefined (" + unitSystem.getInstance_TBD().getSymbol() + ")",Issue.SEVERITY_WARNING));
				} else if (!paramUnitDef.isEquivalent(expUnitDef) && !expUnitDef.isTBD()){
					issueList.add(new Issue(fieldModelParameters[i], IssueCategory.Units,"unit mismatch, computed = ["+expUnitDef.getSymbol()+"]",Issue.SEVERITY_WARNING));
				}
			}catch (VCUnitException e){
				issueList.add(new Issue(fieldModelParameters[i],IssueCategory.Units,"units inconsistent: "+e.getMessage(),Issue.SEVERITY_WARNING));
			}catch (ExpressionException e){
				issueList.add(new Issue(fieldModelParameters[i],IssueCategory.Units,"units inconsistent: "+e.getMessage(),Issue.SEVERITY_WARNING));
			}
		}
	}catch (Throwable e){
		issueList.add(new Issue(this,IssueCategory.Units,"unexpected exception: "+e.getMessage(),Issue.SEVERITY_WARNING));
	}
	
	//
	// get issues from all ReactionSteps
	//
	for (int i = 0; i < fieldReactionSteps.length; i++){
		fieldReactionSteps[i].gatherIssues(issueList);
	}
	
	//
	// get issues for symbol name clashes (specifically structures with same voltage names or structure names)
	//
	HashSet<SymbolTableEntry> steHashSet = new HashSet<SymbolTableEntry>();
	gatherLocalEntries(steHashSet);
	Iterator<SymbolTableEntry> iter = steHashSet.iterator();
	Hashtable<String,SymbolTableEntry> symbolHashtable = new Hashtable<String, SymbolTableEntry>();
	while (iter.hasNext()){
		SymbolTableEntry ste = iter.next();
		SymbolTableEntry existingSTE = symbolHashtable.get(ste.getName());
		if (existingSTE!=null){
			issueList.add(new Issue(this,IssueCategory.Identifiers, "model symbol \""+ste.getName()+"\" is used within \""+ste.getNameScope().getName()+"\" and \""+existingSTE.getNameScope().getName()+"\"",Issue.SEVERITY_ERROR));
		}else{
			symbolHashtable.put(ste.getName(),ste);
		}
	}
	
	//
	// gather issues for electrical topology (unspecified +ve or -ve features, or +ve feature == -ve feature
	//
	getElectricalTopology().gatherIssues(issueList);
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
public Diagram[] getDiagrams() {
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
public SymbolTableEntry getEntry(java.lang.String identifierString) {
	
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
	return getNameScope().getExternalEntry(identifierString,this);
}

public ReservedSymbol[] getReservedSymbols() {
	return fieldReservedSymbols;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public Feature createFeature() {
	int count=0;
	String featureName = null;
	while (true) {
		featureName = "c" + count;
		if (getStructure(featureName) == null){
			break;
		}	
		count++;
	}
	try {
		return addFeature(featureName);
	} catch (ModelException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
private String getFreeMembraneName() {
	int count=0;
	while (true) {
		String memName = Structure.TYPE_NAME_MEMBRANE + count;
		if (getStructure(memName) == null) {
			return memName;
		}
		count++;
	}
}

/**
 * @return java.lang.String
 */
public SimpleReaction createSimpleReaction(Structure structure) {
	String reactionStepName = getFreeReactionName();
	try {
		SimpleReaction simpleReaction = new SimpleReaction(this, structure, reactionStepName);
		addReactionStep(simpleReaction);
		return simpleReaction;
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


public String getFreeReactionName() {
	int count=0;
	String reactionStepName = null;
	while (true) {
		reactionStepName = "r" + count;
		if (getReactionStep(reactionStepName) == null){
			break;
		}
	
		count++;
	}
	return reactionStepName;
}

public FluxReaction createFluxReaction(Membrane membrane) {
	int count=0;
	String reactionStepName = null;
	while (true) {
		reactionStepName = "flux" + count;
		if (getReactionStep(reactionStepName) == null){
			break;
		}
	
		count++;
	}
	try {
		FluxReaction fluxReaction = new FluxReaction(this, membrane, null, reactionStepName);
		addReactionStep(fluxReaction);
		return fluxReaction;
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}

/**
 * @return java.lang.String
 * @throws PropertyVetoException 
 */
public SpeciesContext createSpeciesContext(Structure structure) {
	int count=0;
	String speciesName = null;
	while (true) {
		speciesName = "s" + count;	
		if (getSpecies(speciesName) == null && getSpeciesContext(speciesName) == null) {
			break;
		}	
		count++;
	}
	try {
		SpeciesContext speciesContext = new SpeciesContext(new Species(speciesName, null), structure);
		speciesContext.setName(speciesName);
		addSpecies(speciesContext.getSpecies());
		addSpeciesContext(speciesContext);
		return speciesContext;
	} catch (PropertyVetoException ex) {
		ex.printStackTrace(System.out);
		throw new RuntimeException(ex.getMessage());
	}
}

public ModelParameter createModelParameter() {
	String globalParamName = null;
	int count=0;
	while (true){
		globalParamName = "g" + count;
		if (getModelParameter(globalParamName)==null) {
			break;
		}
		count++;
	}
	try {
		ModelParameter modelParameter = new ModelParameter(globalParamName, new Expression(0), Model.ROLE_UserDefined, unitSystem.getInstance_TBD());
		addModelParameter(modelParameter);
		return modelParameter;
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}

/**
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
public SymbolTableEntry getLocalEntry(java.lang.String identifier) {
	
	for (ReservedSymbol rs : fieldReservedSymbols) {
		if (identifier.equals(rs.getName())) {
			return rs;
		}
	}
	
	// look through the global/model parameters
	for (int i = 0; i < fieldModelParameters.length; i++) {
		if (fieldModelParameters[i].getName().equals(identifier)) {
			return getModelParameter(identifier);
		}
	}
	
	//
	// get Voltages from structures
	//
	for (int i = 0; i < fieldStructures.length; i++){
		if (fieldStructures[i] instanceof Membrane){
			Membrane.MembraneVoltage membraneVoltage = ((Membrane)fieldStructures[i]).getMembraneVoltage();
			if (membraneVoltage.getName().equals(identifier)){
				return membraneVoltage;
			}
		}
	}
	
	//
	// get Sizes from structures
	//
	for (int i = 0; i < fieldStructures.length; i++){
		Structure.StructureSize structureSize = fieldStructures[i].getStructureSize();
		if (structureSize.getName().equals(identifier)){
			return structureSize;
		}
	}
	
	return getSpeciesContext(identifier);
}

/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 10:03:05 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public void gatherLocalEntries(Set<SymbolTableEntry> symbolTableEntries) {

	for (int i = 0; i < fieldReservedSymbols.length; i++) {
		symbolTableEntries.add(fieldReservedSymbols[i]);
	}

	for (int i = 0; i < fieldModelParameters.length; i++) {
		symbolTableEntries.add(fieldModelParameters[i]);
	}
	
	for (int i = 0; i < fieldStructures.length; i++){
		symbolTableEntries.add(fieldStructures[i].getStructureSize());
		if (fieldStructures[i] instanceof Membrane){
			symbolTableEntries.add(((Membrane)fieldStructures[i]).getMembraneVoltage());
		}
	}
	
	for (int i = 0; i < fieldSpeciesContexts.length; i++){
		symbolTableEntries.add(fieldSpeciesContexts[i]);
	}
}


/**
 * Gets the modelParameters property (cbit.vcell.model.ModelParameter[]) value.
 * @return The modelParameters property value.
 * @see #setModelParameters
 */
public Model.ModelParameter[] getModelParameters() {
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
public NameScope getNameScope() {
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

public int getNumReactions() {
	return fieldReactionSteps.length;
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
public ReactionStep[] getReactionSteps() {
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
public Species[] getSpecies() {
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
public Species[] getSpecies(DBSpecies dbSpecies) {
	if (dbSpecies == null){
		throw new IllegalArgumentException("DBSpecies was null");
	}
	Vector<Species> speciesList = new Vector<Species>();
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

public ModelParameter getModelParameter(String glParamName)
{
	if (glParamName == null){
		return null;
	}	
	for (int i=0;i<fieldModelParameters.length;i++){
		if (glParamName.equals(fieldModelParameters[i].getName())){
			return fieldModelParameters[i];
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
	if (!contains(species)) {
		throw new RuntimeException("Species '" + species.getCommonName() + "' not found in model; " +
				"Could not retrieve speciesContext '" + species.getCommonName() + "_" + structure.getName() + "'.");
	}
	if (!contains(structure)) {
		throw new RuntimeException("Structure '" + structure.getName() + "' not found in model; " +
				"Could not retrieve speciesContext '" + species.getCommonName() + "_" + structure.getName() + "'.");
	}
	for (int i=0;i<fieldSpeciesContexts.length;i++){
		if ((fieldSpeciesContexts[i].getSpecies() == species) && 
			(fieldSpeciesContexts[i].getStructure() == structure)){
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
public SpeciesContext[] getSpeciesContexts() {
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
	Vector<SpeciesContext> scList = new Vector<SpeciesContext>();
	
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
	// Feature outsideFeature = (Feature)movingMembrane.getParentStructure();
	Feature outsideFeature = (Feature)structureTopology.getParentStructure(movingMembrane);
	SpeciesContext[] outSC = getSpeciesContexts(outsideFeature);
	Vector<SpeciesContext> neededSC = new Vector<SpeciesContext>();
	for(int i=0;i<fieldReactionSteps.length;i+= 1){
		if(fieldReactionSteps[i].getStructure() == movingMembrane){
			for(int j=0;j<outSC.length;j+= 1){
				if(fieldReactionSteps[i].countNumReactionParticipants(outSC[j]) > 0){
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
	Vector<String> nameList = new Vector<String>();
	for (int i=0;i<fieldSpecies.length;i++){
		nameList.add(fieldSpecies[i].getCommonName());
	}
	String names[] = new String[nameList.size()];
	nameList.copyInto(names);
	return names;
}               

public ElectricalTopology getElectricalTopology(){
	return electricalTopology;
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
public Structure[] getStructures() {
	return fieldStructures;
}


/**
 * Gets the structures index property (cbit.vcell.model.Structure) value.
 * @return The structures property value.
 * @param index The index value into the property array.
 * @see #setStructures
 */
public Structure getStructure(int index) {
	return getStructures()[index];
}

public StructureTopology getStructureTopology(){
	return structureTopology ;
}

//wei's code
public ArrayList<Membrane> getMembranes(){
	ArrayList<Membrane> membranes = new ArrayList<Membrane>();
	for (int i = 0; i < fieldStructures.length; i++) {
		if (fieldStructures[i] instanceof Membrane){
			membranes.add( (Membrane)fieldStructures[i]);
			
		}
	}
	return membranes;
}
// done



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
		if (fieldReactionSteps[i].countNumReactionParticipants(speciesContext) > 0){
			return true;
		}
	}
	return false;
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
			fieldDiagrams[i].renameNode((String)evt.getOldValue(),(String)evt.getNewValue());
		}
	}
	if (evt.getSource() instanceof ReactionStep && evt.getPropertyName().equals("name")){
		for (int i = 0; i < fieldDiagrams.length; i++){
			fieldDiagrams[i].renameNode((String)evt.getOldValue(),(String)evt.getNewValue());
		}
	}
	
	if (evt.getSource() instanceof ModelParameter && evt.getPropertyName().equals("name")){
		for (int i = 0; i < fieldModelParameters.length; i++){
			try {
				Expression exp = fieldModelParameters[i].getExpression();
				Expression renamedExp = exp.renameBoundSymbols(getNameScope());
				if (!renamedExp.compareEqual(exp)) {
					fieldModelParameters[i].setExpression(renamedExp);
				}
			} catch (ExpressionBindingException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException(e.getMessage());
			} catch (PropertyVetoException e2) {
				e2.printStackTrace(System.out);
				throw new RuntimeException(e2.getMessage());
			}
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
		fieldStructures[i].getStructureSize().removePropertyChangeListener(this);
		fieldStructures[i].getStructureSize().removeVetoableChangeListener(this);
		fieldStructures[i].getStructureSize().addPropertyChangeListener(this);
		fieldStructures[i].getStructureSize().addVetoableChangeListener(this);
		if (fieldStructures[i] instanceof Membrane){
			((Membrane)fieldStructures[i]).getMembraneVoltage().removePropertyChangeListener(this);
			((Membrane)fieldStructures[i]).getMembraneVoltage().removeVetoableChangeListener(this);
			((Membrane)fieldStructures[i]).getMembraneVoltage().addPropertyChangeListener(this);
			((Membrane)fieldStructures[i]).getMembraneVoltage().addVetoableChangeListener(this);
		}
		fieldStructures[i].setModel(this);
	}
	
	for (int i=0;i<fieldSpecies.length;i++){
		fieldSpecies[i].removeVetoableChangeListener(this);
		fieldSpecies[i].addVetoableChangeListener(this);
		fieldSpecies[i].refreshDependencies();
	}
	
	for (int i=0;i<fieldSpeciesContexts.length;i++){
		fieldSpeciesContexts[i].removePropertyChangeListener(this);
		fieldSpeciesContexts[i].removeVetoableChangeListener(this);
		fieldSpeciesContexts[i].addPropertyChangeListener(this);
		fieldSpeciesContexts[i].addVetoableChangeListener(this);
		fieldSpeciesContexts[i].setModel(this);
		fieldSpeciesContexts[i].refreshDependencies();
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
	
	for (int i=0;i<fieldModelParameters.length;i++){
		fieldModelParameters[i].removeVetoableChangeListener(this);
		fieldModelParameters[i].removePropertyChangeListener(this);
		fieldModelParameters[i].addVetoableChangeListener(this);
		fieldModelParameters[i].addPropertyChangeListener(this);
		try {
			fieldModelParameters[i].getExpression().bindExpression(this);
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error binding global parameter '" + fieldModelParameters[i].getName() + "' to model."  + e.getMessage());
		}

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


public void removeStructure(Structure removedStructure) throws PropertyVetoException {

	if (removedStructure == null){
		throw new RuntimeException("structure is null");
	}	
	if (!contains(removedStructure)){
		throw new RuntimeException("structure "+removedStructure.getName()+" not found");
	}
	
	//Check that the feature is empty
	String errorMessage = null;
	for (int i=0;i<fieldReactionSteps.length;i++){
	if (fieldReactionSteps[i].getStructure() == removedStructure){
			errorMessage = "cannot contain Reactions";
			break;
		}
	}
	for (int i=0;i<fieldSpeciesContexts.length;i++){
	if (fieldSpeciesContexts[i].getStructure() == removedStructure){
			errorMessage = "cannot contain Species";
			break;
		}
	}

	if(errorMessage != null){
		throw new RuntimeException("Remove model compartment Error\n\nStructure to be removed : '"+removedStructure.getName()+"' : "+errorMessage+".");
	}

	//
	// remove this structure
	//
	Structure newStructures[] = (Structure[])fieldStructures.clone();
	newStructures = (Structure[])BeanUtils.removeElement(newStructures,removedStructure);
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
public void setDiagrams(Diagram[] diagrams) throws java.beans.PropertyVetoException {
	Diagram[] oldValue = fieldDiagrams;
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
public void setModelParameters(ModelParameter[] modelParameters) throws java.beans.PropertyVetoException {
	ModelParameter[] oldValue = fieldModelParameters;
	fireVetoableChange(Model.PROPERTY_NAME_MODEL_PARAMETERS, oldValue, modelParameters);
	fieldModelParameters = modelParameters;
	firePropertyChange(Model.PROPERTY_NAME_MODEL_PARAMETERS, oldValue, modelParameters);
	
	ModelParameter newValue[] = modelParameters;
	if (oldValue != null) {
		for (int i=0;i<oldValue.length;i++){	
			oldValue[i].removePropertyChangeListener(this);
			oldValue[i].removeVetoableChangeListener(this);
		}
	}
	if (newValue != null) {
		for (int i=0;i<newValue.length;i++){
			newValue[i].addPropertyChangeListener(this);
			newValue[i].addVetoableChangeListener(this);
		}
	}
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
public void setReactionSteps(ReactionStep[] reactionSteps) throws java.beans.PropertyVetoException {
	ReactionStep[] oldValue = fieldReactionSteps;
	fireVetoableChange(PROPERTY_NAME_REACTION_STEPS, oldValue, reactionSteps);
	HashSet<ReactionStep> oldReactions = new HashSet<ReactionStep>(Arrays.asList(this.fieldReactionSteps));
	HashSet<ReactionStep> newReactions = new HashSet<ReactionStep>(Arrays.asList(reactionSteps));
	HashSet<ReactionStep> reactionsAdded = new HashSet<ReactionStep>(newReactions);
	reactionsAdded.removeAll(oldReactions);
	HashSet<ReactionStep> reactionsRemoved = new HashSet<ReactionStep>(oldReactions);
	reactionsRemoved.removeAll(newReactions);	

	fieldReactionSteps = reactionSteps;

	for (ReactionStep rs : reactionsRemoved){
		rs.removePropertyChangeListener(this);
		rs.removeVetoableChangeListener(this);
		rs.getKinetics().removePropertyChangeListener(this);
		rs.getKinetics().removeVetoableChangeListener(this);
		rs.setModel(null);
	}
	for (ReactionStep rs : reactionsAdded){
		rs.removePropertyChangeListener(this);
		rs.addPropertyChangeListener(this);
		rs.removeVetoableChangeListener(this);
		rs.addVetoableChangeListener(this);
		rs.getKinetics().removePropertyChangeListener(this);
		rs.getKinetics().addPropertyChangeListener(this);
		rs.getKinetics().removeVetoableChangeListener(this);
		rs.getKinetics().addVetoableChangeListener(this);
		rs.setModel(this);
		try {
			rs.rebindAllToModel(this);
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	firePropertyChange(PROPERTY_NAME_REACTION_STEPS, oldValue, reactionSteps);
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
		firePropertyChange(PROPERTY_NAME_REACTION_STEPS, null, fieldReactionSteps);
	};
}


/**
 * Sets the species property (cbit.vcell.model.Species[]) value.
 * @param species The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSpecies
 */
public void setSpecies(Species[] species) throws java.beans.PropertyVetoException {
	Species[] oldValue = fieldSpecies;
	fireVetoableChange(PROPERTY_NAME_SPECIES, oldValue, species);
	fieldSpecies = species;
	firePropertyChange(PROPERTY_NAME_SPECIES, oldValue, species);
	
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
		firePropertyChange(PROPERTY_NAME_SPECIES, null, fieldSpecies);
	};
}


/**
 * Sets the speciesContexts property (cbit.vcell.model.SpeciesContext[]) value.
 * @param speciesContexts The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSpeciesContexts
 */
public void setSpeciesContexts(SpeciesContext[] speciesContexts) throws java.beans.PropertyVetoException {
	SpeciesContext[] oldValue = fieldSpeciesContexts;
	fireVetoableChange(PROPERTY_NAME_SPECIES_CONTEXTS, oldValue, speciesContexts);
	fieldSpeciesContexts = speciesContexts;
	for (int i=0;i<speciesContexts.length;i++){	
		speciesContexts[i].setModel(this);
	}

	firePropertyChange(PROPERTY_NAME_SPECIES_CONTEXTS, oldValue, speciesContexts);

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

	HashSet<Species> oldSpeciesSet = new HashSet<Species>();
	for (int i = 0; i < oldValue.length; i++) {
		oldSpeciesSet.add(oldValue[i].getSpecies());
	}
	HashSet<Species> newSpeciesSet = new HashSet<Species>();
	for (int i = 0; i < newValue.length; i++) {
		newSpeciesSet.add(newValue[i].getSpecies());
	}
	
	oldSpeciesSet.removeAll(newSpeciesSet);
	Iterator<Species> spIterator = oldSpeciesSet.iterator();
	while (spIterator.hasNext()) {
		try{
			removeSpecies(spIterator.next());
		}catch(Throwable e){
			e.printStackTrace(System.out);
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
		firePropertyChange(PROPERTY_NAME_SPECIES_CONTEXTS, null, fieldSpeciesContexts);
	};
}


/**
 * Sets the structures property (cbit.vcell.model.Structure[]) value.
 * @param structures The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getStructures
 */
public void setStructures(Structure[] structures) throws java.beans.PropertyVetoException {
	Structure[] oldValue = fieldStructures;
	fireVetoableChange(PROPERTY_NAME_STRUCTURES, oldValue, structures);
	fieldStructures = structures;
	for (int i=0;i<structures.length;i++){	
		structures[i].setModel(this);
	}
	
	// if structures changed, structureTopology and electrical topology might need to be adjusted
	structureTopology.refresh();
	electricalTopology.refresh();
	
	refreshDiagrams();
	firePropertyChange(PROPERTY_NAME_STRUCTURES, oldValue, structures);


	Structure newValue[] = structures;
	for (int i=0;i<oldValue.length;i++){	
		oldValue[i].removePropertyChangeListener(this);
		oldValue[i].removeVetoableChangeListener(this);
		oldValue[i].setModel(null);
		oldValue[i].getStructureSize().removePropertyChangeListener(this);
		oldValue[i].getStructureSize().removeVetoableChangeListener(this);
		if (oldValue[i] instanceof Membrane){
			((Membrane)oldValue[i]).getMembraneVoltage().removePropertyChangeListener(this);
			((Membrane)oldValue[i]).getMembraneVoltage().removeVetoableChangeListener(this);
		}
	}
	for (int i=0;i<newValue.length;i++){	
		newValue[i].addPropertyChangeListener(this);
		newValue[i].addVetoableChangeListener(this);
		newValue[i].setModel(this);
		newValue[i].getStructureSize().addPropertyChangeListener(this);
		newValue[i].getStructureSize().addVetoableChangeListener(this);
		if (newValue[i] instanceof Membrane){
			((Membrane)newValue[i]).getMembraneVoltage().addPropertyChangeListener(this);
			((Membrane)newValue[i]).getMembraneVoltage().addVetoableChangeListener(this);
		}
	}
	
//	showStructureHierarchy();
}




/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	return "Model@"+Integer.toHexString(hashCode())+"("+((version!=null)?version.toString():getName())+")";
}


public ReservedSymbol getReservedSymbolByName(String name) {
	for (ReservedSymbol rs : fieldReservedSymbols) {
		if (rs.getName().equals(name)) {
			return rs;
		}
	}
	return null;	
}

public ReservedSymbol getReservedSymbolByRole(ReservedSymbolRole role) {
	for (ReservedSymbol rs : fieldReservedSymbols) {
		if (rs.getRole().equals(role)) {
			return rs;
		}
	}
	return null;	
}

/**
 * This method was created in VisualAge.
 * @param e java.beans.PropertyChangeEvent
 * @exception java.beans.PropertyVetoException The exception description.
 */
public void vetoableChange(PropertyChangeEvent e) throws java.beans.PropertyVetoException {
	if (e.getSource() instanceof Structure){
		if (e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
			if (getStructure((String)e.getNewValue())!=null){
				throw new PropertyVetoException("another structure already using name "+e.getNewValue(),e);
			}
		}
	}
	if (e.getSource() instanceof ReactionStep){
		if (e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
			String newName = (String)e.getNewValue();
			if (getReactionStep(newName)!=null){
				throw new PropertyVetoException("another reaction step is already using name '"+newName+"'",e);
			}
			// validateNamingConflicts("Reaction",ReactionStep.class, newName, e);
		}
	}
	if (e.getSource() instanceof SpeciesContext){
		if (e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
			String newName = (String)e.getNewValue();
			SpeciesContext sc = getSpeciesContext(newName);
			if (sc != null){
				throw new PropertyVetoException("another "+SpeciesContext.getTerm()+" defined in '"+sc.getStructure().getName()+"' already uses name '"+e.getNewValue()+"'",e);
			}
			validateNamingConflicts("SpeciesContext",SpeciesContext.class, newName, e);
		}
	}
	if (e.getSource() instanceof MembraneVoltage){
		if (e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
			String newName = (String)e.getNewValue();
			SymbolTableEntry existingSTE = getLocalEntry(newName);
			if (existingSTE instanceof MembraneVoltage){
				throw new PropertyVetoException("new name \""+newName+"\" conflicts with the voltage parameter name for membrane \""+((MembraneVoltage)existingSTE).getMembrane().getName()+"\"",e);
			}
			validateNamingConflicts("MembraneVoltage",MembraneVoltage.class, newName, e);
		}
	}
	if (e.getSource() instanceof StructureSize){
		if (e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
			String newName = (String)e.getNewValue();
			SymbolTableEntry existingSTE = getLocalEntry(newName);
			if (existingSTE instanceof StructureSize){
				throw new PropertyVetoException("new name \""+newName+"\" conflicts with the size parameter name for structure \""+((StructureSize)existingSTE).getStructure().getName()+"\"",e);
			}
			validateNamingConflicts("StructureSize",StructureSize.class, newName, e);
		}
	}
	if (e.getSource() instanceof ModelParameter){
		if (e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
			String newName = (String)e.getNewValue();
			if (getModelParameter(newName)!=null){
				throw new PropertyVetoException("Model Parameter with name '"+newName+"' already exists.",e);
			}
			validateNamingConflicts("Model Parameter", ModelParameter.class, newName, e);
		}
	}

	if (e.getSource() instanceof Species){
		if (e.getPropertyName().equals("commonName") && !e.getOldValue().equals(e.getNewValue())){
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
			if (getReservedSymbolByName(commonName)!=null){
				throw new PropertyVetoException("cannot use reserved symbol '"+commonName+"' as a Species common name",e);
			}
		}
	}

	if (e.getSource() == this && e.getPropertyName().equals(PROPERTY_NAME_STRUCTURES)){
		Structure topStructure = null;
		Structure newStructures[] = (Structure[])e.getNewValue();
		if (newStructures==null){
			throw new PropertyVetoException("structures cannot be null",e);
		}
		//
		// look for duplicates of structure name, structure size name, membrane voltage name within new "structures" array
		// and look for symbol conflicts for StructureSize name and for MembraneVoltage name in existing "local" symbols. 
		//
		HashSet<String> structNameSet = new HashSet<String>();
		HashSet<String> structSymbolSet = new HashSet<String>();
		for (int i=0;i<newStructures.length;i++){
			
			String newStructureName = newStructures[i].getName();
			if (structNameSet.contains(newStructureName)){
				throw new PropertyVetoException("multiple structures with name '"+newStructureName+"' defined",e);
			}
			structNameSet.add(newStructureName);
			
			if (newStructures[i] instanceof Membrane){
				String newMembraneVoltageName = ((Membrane)newStructures[i]).getMembraneVoltage().getName();
				if (structSymbolSet.contains(newMembraneVoltageName)){
					//throw new PropertyVetoException("membrane '"+newStructureName+"' has Voltage name '"+newMembraneVoltageName+"' that conflicts with another Voltage name or Size name",e);
				}
				structSymbolSet.add(newMembraneVoltageName);
				validateNamingConflicts("MembraneVoltage",MembraneVoltage.class, newMembraneVoltageName, e);
			}
			
			String newStructureSizeName = newStructures[i].getStructureSize().getName();
			if (structSymbolSet.contains(newStructureSizeName)){
				throw new PropertyVetoException("structure '"+newStructureName+"' has structure Size name '"+newStructureSizeName+"' that conflicts with another Voltage name or Size name",e);
			}
			structSymbolSet.add(newStructureSizeName);
			validateNamingConflicts("StructureSize",StructureSize.class, newStructureSizeName, e);
		}
	}
	
	if (e.getSource() == this && e.getPropertyName().equals(PROPERTY_NAME_SPECIES)){
		Species newSpeciesArray[] = (Species[])e.getNewValue();
		if (newSpeciesArray==null){
			throw new PropertyVetoException("species cannot be null",e);
		}
		//
		// check that names are not duplicated and that no common names are ReservedSymbols
		//
		HashSet<String> commonNameSet = new HashSet<String>();
		for (int i=0;i<newSpeciesArray.length;i++){
			String commonName = newSpeciesArray[i].getCommonName();
			if (commonNameSet.contains(commonName)){
				throw new PropertyVetoException("multiple species with common name '"+commonName+"' defined",e);
			}
			if (getReservedSymbolByName(commonName)!=null){
				throw new PropertyVetoException("cannot use reserved symbol '"+commonName+"' as a Species common name",e);
			}
			commonNameSet.add(commonName);
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

	if (e.getSource() == this && e.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_PARAMETERS)){
		ModelParameter[] newModelParams = (ModelParameter[])e.getNewValue();
		//
		// check that names are not duplicated and that no common names are ReservedSymbols
		//
		HashSet<String> namesSet = new HashSet<String>();
		for (int i=0;i<newModelParams.length;i++){
			if (namesSet.contains(newModelParams[i].getName())){
				throw new PropertyVetoException("Multiple model/global parameters with same name '"+newModelParams[i].getName()+"' defined",e);
			}
			namesSet.add(newModelParams[i].getName());
			
			validateNamingConflicts("Model Parameter", ModelParameter.class, newModelParams[i].getName(), e);
		}
		//
		// make sure that kinetics of reactionSteps do not refer to modelParameter to be deleted 
		// Find this model parameter, missing from 'newModelParams'; loop thro' all reactionStep kinetics to determine if it is used
		//
		ModelParameter[] oldModelParameters = (ModelParameter[])e.getOldValue();
		if (oldModelParameters.length > newModelParams.length) {
			// if 'newModelParameter' is smaller than 'oldModelParameter', one of the modelParams has been removed, find the missing one
			ModelParameter missingMP = null;
			for (int i = 0; i < oldModelParameters.length; i++) {
				if (!BeanUtils.arrayContains(newModelParams, oldModelParameters[i])) {
					missingMP = oldModelParameters[i];
				}
			}
			// use this missing model parameter (to be deleted) to determine if it is used in any reaction kinetic parameters. 
			if (missingMP != null) {
				Vector<String> referencingRxnsVector = new Vector<String>();
				for (int i=0;i<fieldReactionSteps.length;i++){
					KineticsParameter[] kParams = fieldReactionSteps[i].getKinetics().getKineticsParameters();
					for (int k = 0; k < kParams.length; k++) {
						if (kParams[k].getExpression().hasSymbol(missingMP.getName()) && (fieldReactionSteps[i].getKinetics().getProxyParameter(missingMP.getName()) != null)) {
							referencingRxnsVector.add(fieldReactionSteps[i].getName());
							break;
						}
					}
				}
				// if there are any reactionSteps referencing the global, list them all in error msg.
				if (referencingRxnsVector.size() > 0) {
					String msg = "Model Parameter '" + missingMP.getName() + "' is used in reaction(s): ";
					for (int i = 0; i < referencingRxnsVector.size(); i++) {
						msg = msg + "'" + referencingRxnsVector.elementAt(i) + "'";
						if (i < referencingRxnsVector.size()-1) {
							msg = msg + ", "; 
						} else {
							msg = msg + ". ";
						}
					}
					msg = msg + "\n\nCannot delete '" + missingMP.getName() + "'.";
					throw new PropertyVetoException(msg,e);
				}
				// At this point, it is not referenced in a reactionStep, find out if the missing model is used in other model parameters
				// Enough to check in newModelParams array
				Vector<String> referencingModelParamsVector = new Vector<String>();
				for (int i = 0; i < newModelParams.length; i++) {
					if (newModelParams[i].getExpression().hasSymbol(missingMP.getName())) {
						referencingModelParamsVector.add(newModelParams[i].getName());
					}
				}
				// if there are any model parameters referencing the global, list them all in error msg.
				if (referencingModelParamsVector.size() > 0) {
					String msg = "Model Parameter '" + missingMP.getName() + "' is used in expression of other model parameter(s): ";
					for (int i = 0; i < referencingModelParamsVector.size(); i++) {
						msg = msg + "'" + referencingModelParamsVector.elementAt(i) + "'";
						if (i < referencingModelParamsVector.size()-1) {
							msg = msg + ", "; 
						} else {
							msg = msg + ". ";
						}
					}
					msg = msg + "\n\nCannot delete '" + missingMP.getName() + "'.";
					throw new PropertyVetoException(msg,e);
				}
			}
		}
	}
	
	if (e.getSource() == this && e.getPropertyName().equals(PROPERTY_NAME_SPECIES_CONTEXTS)){
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
		HashSet<String> nameSet = new HashSet<String>();
		for (int i=0;i<newSpeciesContextArray.length;i++){
			if (nameSet.contains(newSpeciesContextArray[i].getName())){
				throw new PropertyVetoException("multiple speciesContexts with name '"+newSpeciesContextArray[i].getName()+"' defined",e);
			}
			nameSet.add(newSpeciesContextArray[i].getName());
			
			validateNamingConflicts("SpeciesContext",newSpeciesContextArray[i].getClass(), newSpeciesContextArray[i].getName(), e);
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
						break;
					}
				}
				if (!bFound){
					throw new PropertyVetoException("reaction '"+fieldReactionSteps[i].getName()+"' requires '"+rpArray[k].getSpeciesContext().getName()+"'",e);
				}
			}
		}
	}
	
	if (e.getSource() == this && e.getPropertyName().equals(PROPERTY_NAME_REACTION_STEPS)){
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
		HashSet<String> nameSet = new HashSet<String>();
		for (int i=0;i<newReactionStepArr.length;i++){
			String rxnName = newReactionStepArr[i].getName();
			if (nameSet.contains(rxnName)){
				throw new PropertyVetoException("multiple reactionSteps with name '"+rxnName+"' defined",e);
			}
			if (getReservedSymbolByName(rxnName)!=null){
				throw new PropertyVetoException("cannot use reserved symbol '"+rxnName+"' as a Reaction name",e);
			}
			nameSet.add(rxnName);

			// validateNamingConflicts("Reaction", ReactionStep.class, newReactionStepArr[i].getName(), e);
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
						break;
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
 * if newSTE is null, then newName is the proposed name of a reaction
 * else newSTE is the symbol to be added.
 */
private void validateNamingConflicts(String symbolDescription, Class<?> newSymbolClass, String newSymbolName, PropertyChangeEvent e) throws PropertyVetoException {
	//
	// validate lexicon
	//
	if (newSymbolName==null){
		throw new PropertyVetoException(symbolDescription+" name is null.",e);
	}
	if (newSymbolName.length()<1){
		throw new PropertyVetoException(symbolDescription+" name is empty (zero length).",e);
	}
	if (!newSymbolName.equals(TokenMangler.fixTokenStrict(newSymbolName))){
		throw new PropertyVetoException(symbolDescription+" '"+newSymbolName+"' not legal identifier, try '"+TokenMangler.fixTokenStrict(newSymbolName)+"'.",e);
	}
	
	//
	// make sure not to change name of a "global" symbol to that of a ReactionStep name .... (this IS necessary with namespaces)
	//
	if (!newSymbolClass.equals(ReactionStep.class)){
		for (int j = 0; j < fieldReactionSteps.length; j++){
			if (fieldReactionSteps[j].getName().equals(newSymbolName)){
				throw new PropertyVetoException("conflict with "+symbolDescription+" '"+newSymbolName+"', name already used for "+fieldReactionSteps[j].getTerm()+" '"+fieldReactionSteps[j].getName()+"' in structure '"+fieldReactionSteps[j].getStructure().getName()+"'.",e);
			}
		}
	}
	//
	// make sure not to change to name of any other symbol in 'model' namespace (or friendly namespaces)
	//
	SymbolTableEntry localSTE = getLocalEntry(newSymbolName);
	if (localSTE == null){
		return;
	}
	//
	// if the existing (local) symbol and the new symbol are of the same type (same class) then ignore any conflict.
	//
	if (newSymbolClass.equals(localSTE.getClass())){
		return;
	}
	
	//
	// old and new symbols of different type but same name, throw exception 
	//
	if (localSTE instanceof StructureSize){
		throw new PropertyVetoException("conflict with "+symbolDescription+" '"+newSymbolName+"', name already used for Size in Structure '"+((StructureSize)localSTE).getStructure().getName()+"'",e);
	}else if (localSTE instanceof MembraneVoltage){
		throw new PropertyVetoException("conflict with "+symbolDescription+" '"+newSymbolName+"', name already used for Voltage Context in Structure '"+((MembraneVoltage)localSTE).getMembrane().getName()+"'",e);
	}else if (localSTE instanceof SpeciesContext){
		throw new PropertyVetoException("conflict with "+symbolDescription+" '"+newSymbolName+"', name already used for Species Context in Structure '"+((SpeciesContext)localSTE).getStructure().getName()+"'",e);
	}else if (localSTE instanceof MembraneVoltage){
		throw new PropertyVetoException("conflict with "+symbolDescription+" '"+newSymbolName+"', name already used for Membrane Voltage in Membrane '"+((Membrane.MembraneVoltage)localSTE).getMembrane().getName()+"'",e);
	}else if (localSTE instanceof StructureSize){
		throw new PropertyVetoException("conflict with "+symbolDescription+" '"+newSymbolName+"', name already used for Structure Size in Structure '"+((Structure.StructureSize)localSTE).getStructure().getName()+"'",e);
	}else if (localSTE instanceof ModelParameter){
		throw new PropertyVetoException("conflict with "+symbolDescription+" '"+newSymbolName+"', name already used for Model Parameter",e);
	}else if (localSTE instanceof ReservedSymbol){
		throw new PropertyVetoException("conflict with "+symbolDescription+" '"+newSymbolName+"', name already used for a reserved symbol (e.g. 'x','y','z','t','KMOLE','_T_','_F_','_R_', ...)",e);
	}else{
		throw new PropertyVetoException("conflict with "+symbolDescription+" '"+newSymbolName+"', name already used for a '"+localSTE.getClass().getName()+"' in context '"+localSTE.getNameScope().getName()+"'",e);
	}
	
}


public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
	for (SymbolTableEntry ste : fieldSpeciesContexts) {
		entryMap.put(ste.getName(), ste);
	}
	for (Structure s : fieldStructures) {
		Structure.StructureSize structureSize = s.getStructureSize();
		entryMap.put(structureSize.getName(), structureSize);
		if (s instanceof Membrane){
			Membrane.MembraneVoltage membraneVoltage = ((Membrane)s).getMembraneVoltage();
			entryMap.put(membraneVoltage.getName(), membraneVoltage);
		}
	}
	for (SymbolTableEntry ste : fieldModelParameters) {
		entryMap.put(ste.getName(), ste);
	}
	
	for (ReservedSymbol rs : fieldReservedSymbols) {
		if ( (rs != getX()) || (rs != getY()) || (rs != getZ()) ) {
			entryMap.put(rs.getName(), rs);
		}
	}
}


public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	getNameScope().getExternalEntries(entryMap);	
}



public VCMetaData getVcMetaData() {
	return vcMetaData;
}


public void setVcMetaData(VCMetaData vcMetaData) {
	this.vcMetaData = vcMetaData;
}

public void populateVCMetadata(boolean bMetadataPopulated) {
	// populate free text for identifiables (species, reactionSteps, structures)
	if (!bMetadataPopulated) {
		for (int i = 0; i < fieldSpecies.length; i++) {
			vcMetaData.setFreeTextAnnotation(fieldSpecies[i], fieldSpecies[i].getAnnotation());
			if(fieldSpecies[i].getDBSpecies() != null){
				DBSpecies dbSpecies = fieldSpecies[i].getDBSpecies();
				try{
					if(dbSpecies.getFormalSpeciesInfo().getFormalSpeciesType().equals(FormalSpeciesType.compound)){
						//urn:miriam:kegg.compound
						MiriamResource resource = vcMetaData.getMiriamManager().createMiriamResource(
							"urn:miriam:kegg.compound:"+dbSpecies.getFormalSpeciesInfo().getFormalID());
						Set<MiriamResource> miriamResources = new HashSet<MiriamResource>();
						miriamResources.add(resource);
						vcMetaData.getMiriamManager().addMiriamRefGroup(fieldSpecies[i], MIRIAMQualifier.BIO_isVersionOf,
								miriamResources);
					}else if(dbSpecies.getFormalSpeciesInfo().getFormalSpeciesType().equals(FormalSpeciesType.enzyme)){
						//urn:miriam:ec-code
						MiriamResource resource = vcMetaData.getMiriamManager().createMiriamResource(
								"urn:miriam:ec-code:"+dbSpecies.getFormalSpeciesInfo().getFormalID());
							Set<MiriamResource> miriamResources = new HashSet<MiriamResource>();
							miriamResources.add(resource);
							vcMetaData.getMiriamManager().addMiriamRefGroup(fieldSpecies[i], MIRIAMQualifier.BIO_isVersionOf,
									miriamResources);

					}else if(dbSpecies.getFormalSpeciesInfo().getFormalSpeciesType().equals(FormalSpeciesType.protein)){
						MiriamResource resource = vcMetaData.getMiriamManager().createMiriamResource(
								"urn:miriam:uniprot:"+dbSpecies.getFormalSpeciesInfo().getFormalID());
							Set<MiriamResource> miriamResources = new HashSet<MiriamResource>();
							miriamResources.add(resource);
							vcMetaData.getMiriamManager().addMiriamRefGroup(fieldSpecies[i], MIRIAMQualifier.BIO_isVersionOf,
									miriamResources);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		for (int i = 0; i < fieldReactionSteps.length; i++) {
			vcMetaData.setFreeTextAnnotation(fieldReactionSteps[i], fieldReactionSteps[i].getAnnotation());
		}
		
//		// No annotation in structures for the moment.
//		for (int i = 0; i < fieldStructures.length; i++) {
//			vcMetaData.setFreeTextAnnotation(fieldStructures[i], fieldStructures[i].getAnnotation());
//		}
	}		
}

/**
 * This method is modified on Nov 20, 2007. We got to go through the MassActionSolver and FluxSolver here to make sure that everything
 * is being checked before processing further. However, this makes the function become heavy.
 * The function is being referened in four different places, which are ClientRequestManager.runSimulations(), BioModelEditor.newApplication(),
 * SimulationContext.SimulationContext(SimulationContext, boolean) and StochMathMapping.refreshMathDescription().
 * Creation date: (11/16/2006 4:55:16 PM)
 * @return java.lang.String
 */
public String isValidForStochApp()
{
	String returnStr = ""; //sum of all the issues
	String exceptionReacStr = ""; //exception msg from MassActionSolver when parsing kinetics for reactions/fluxes.
	String unTransformableStr = ""; //all untransformable reactions/fluxes, which have kinetic laws rather than General and MassAction.
	cbit.vcell.model.ReactionStep[] reacSteps = getReactionSteps();
	// Mass Action and centain form of general Flux can be automatically transformed.
	for (int i = 0; (reacSteps != null) && (i < reacSteps.length); i++)
	{
		int numCatalyst = 0;
		ReactionParticipant[] reacParts = reacSteps[i].getReactionParticipants(); 
		for(ReactionParticipant rp : reacParts)
		{
			if(rp instanceof Catalyst)
			{
				numCatalyst ++;
			}
		}
		if((reacParts.length - numCatalyst) > 0)
		{
			if(((reacSteps[i] instanceof SimpleReaction) && 
					!reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction) &&
					!reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General) &&
					!reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.Macroscopic_irreversible) &&
					!reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.Microscopic_irreversible)) 
				||
			  ((reacSteps[i] instanceof FluxReaction) && 
					!reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General) && 
					!reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.GeneralPermeability)))
			{
				unTransformableStr = unTransformableStr + " " + reacSteps[i].getName() + ",";
			}
			else
			{
				if(reacSteps[i].getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL || reacSteps[i].getPhysicsOptions() == ReactionStep.PHYSICS_ELECTRICAL_ONLY)
				{
					unTransformableStr = unTransformableStr + " " + reacSteps[i].getName() + "(has electric current),";
				}
				else
				{
					if(reacSteps[i] instanceof SimpleReaction)
					{
						Expression rateExp = reacSteps[i].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
						if(reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction) || 
								reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General))
						{
							try{
								MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(rateExp, reacSteps[i]);
							}catch(Exception e)
							{
								exceptionReacStr = exceptionReacStr + " " + reacSteps[i].getName() + " error: " + e.getMessage() + "\n";
							}
						}
					}
					else // flux described by General density function
					{
						if(reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General) ||
						   reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.GeneralPermeability))
						{
							Expression rateExp = reacSteps[i].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
							try{
								MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(rateExp, (FluxReaction)reacSteps[i]);
							}catch(Exception e)
							{
								exceptionReacStr = exceptionReacStr + " " + reacSteps[i].getName() + " error: " + e.getMessage() + "\n";
							}
						} 
					}
				}
			}
		}
	}
	
	if(unTransformableStr.length() > 0)
	{
		returnStr = returnStr + unTransformableStr.substring(0,(unTransformableStr.length()-1)) + " are unable to be intepreted to mass action form. \nTherefore," +
				"they are not suitable for stochastic application.\n\n" +
				"Reactions described by mass action law(all) or General law(certain forms),\n" +
				"or fluxes described by general desity function(certain forms) and general\n" +
				"permeability(certain forms) can be inteprested to mass action form.\n\n"+
				"All rate laws that include electric current are not suitable for stochastic application.";
	}
	if(exceptionReacStr.length() > 0)
	{
		returnStr = returnStr + exceptionReacStr;
	}
	return returnStr;
}

	public void removeObject(Object object) throws PropertyVetoException {
		if(object instanceof Feature || object instanceof Membrane) { removeStructure((Structure) object); }
		else if(object instanceof ModelParameter) { removeModelParameter((ModelParameter) object); }
		else if(object instanceof ReactionStep) { removeReactionStep((ReactionStep) object); }
		else if(object instanceof Species) { removeSpecies((Species) object); }
		else if(object instanceof SpeciesContext) { removeSpeciesContext((SpeciesContext) object); }
	}
	
	public ModelUnitSystem getUnitSystem() {
		return unitSystem;
	}
	
	public Membrane createMembrane() {
		int count=0;
		String membraneName = null;
		while (true) {
			membraneName = "m" + count;
			if (getStructure(membraneName) == null){
				break;
			}	
			count++;
		}
		try {
			return addMembrane(membraneName);
		} catch (ModelException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}

	/** 
	 * RateRuleVariable related methods: 
	 * 
	 *    /
	
	public RateRuleVariable addRateRuleVariable(String name, Structure structure, ModelParameter parameter, int parameterRole) throws Exception {
		RateRuleVariable rateRuleVar = getRateRuleVariable(name);
		if (rateRuleVar != null){
			throw new Exception("RateRuleVariable '" + name + "' already defined");
		}
		rateRuleVar = new RateRuleVariable(name, structure, parameter, parameterRole);
		rateRuleVar.setModel(this);
		return addRateRuleVariable(rateRuleVar);
	}


	public RateRuleVariable addRateRuleVariable(RateRuleVariable rateRuleVar) throws PropertyVetoException {
		
		if (rateRuleVar.getStructure() != null && !contains(rateRuleVar.getStructure())){
			throw new RuntimeException("structure "+rateRuleVar.getStructure().getName()+" not found in model");
		}
		if (getRateRuleVariable(rateRuleVar.getName())!=null){
			throw new RuntimeException("RateRuleVariable '"+ rateRuleVar.getName() + "' already defined");
		}
		if (!contains(rateRuleVar)){
			RateRuleVariable[] newArray = (RateRuleVariable[])BeanUtils.addElement(fieldRateRuleVariables,rateRuleVar);
			rateRuleVar.setModel(this);
			setRateRuleVariables(newArray);
		}
		return rateRuleVar;
	}

	public void removeRateRuleVariable(RateRuleVariable rateRuleVar) throws PropertyVetoException {
		if (contains(rateRuleVar)){
			RateRuleVariable[] newRateRules = (RateRuleVariable[])BeanUtils.removeElement(fieldRateRuleVariables, rateRuleVar);
			setRateRuleVariables(newRateRules);
		}
	}

	public boolean contains(RateRuleVariable rateRuleVar) {
		for (int i = 0; i < fieldRateRuleVariables.length; i++){
			if (fieldRateRuleVariables[i] == rateRuleVar){
				return true;
			}
		}
		return false;
	}

	public RateRuleVariable[] getRateRuleVariables() {
		return fieldRateRuleVariables;
	}
	
	public RateRuleVariable getRateRuleVariable(int index) {
		return getRateRuleVariables()[index];
	}

	public RateRuleVariable getRateRuleVariable(String rateRuleVarName) {
		if (fieldRateRuleVariables != null) {
			for (int i=0;i<fieldRateRuleVariables.length;i++){
				if (fieldRateRuleVariables[i].getName().equals(rateRuleVarName)){
					return fieldRateRuleVariables[i];
				}
			}
		}
		return null;
	}

	public void setRateRuleVariables(RateRuleVariable[] rateRuleVars) throws java.beans.PropertyVetoException {
		RateRuleVariable[] oldValue = fieldRateRuleVariables;
		fireVetoableChange(PROPERTY_NAME_RATERULEVARIABLES, oldValue, rateRuleVars);
		fieldRateRuleVariables = rateRuleVars;
		firePropertyChange(PROPERTY_NAME_RATERULEVARIABLES, oldValue, rateRuleVars);

		RateRuleVariable newValue[] = rateRuleVars;
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
	}

	public RateRuleVariable createRateRuleVariable(Structure structure) throws PropertyVetoException {
		int count=0;
		String rateRuleVarName = null;
		while (true) {
			rateRuleVarName = "rateRuleVar" + count;	
			if (getRateRuleVariable(rateRuleVarName) == null) {
				break;
			}	
			count++;
		}
		try {
			RateRuleVariable rateRuleVar = null;
			ModelParameter modelParameter = new ModelParameter(rateRuleVarName, new Expression(0.0), ROLE_UserDefined, getUnitSystem().getInstance_TBD());
			if (structure == null) {
				rateRuleVar = new RateRuleVariable(rateRuleVarName, modelParameter, RateRuleVariable.ROLE_VariableRate);
			} else {
				rateRuleVar = new RateRuleVariable(rateRuleVarName, structure, modelParameter, RateRuleVariable.ROLE_VariableRate);
			}
			addRateRuleVariable(rateRuleVar);
			return rateRuleVar;
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String getFreeRateRuleVariableName() {
		int count=0;
		String rateRuleVarName = null;
		while (true) {
			rateRuleVarName = "r" + count;
			if (getRateRuleVariable(rateRuleVarName) == null){
				break;
			}
		
			count++;
		}
		return rateRuleVarName;
	}
	
	*/
}