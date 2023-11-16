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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
//import org.vcell.model.rbm.RbmParameter;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.*;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.document.Identifiable;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;

import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry.FunctionArgType;
import cbit.vcell.parser.VCUnitEvaluator;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitException;

@SuppressWarnings("serial")
public class Model implements Versionable, Matchable, Relatable, PropertyChangeListener, VetoableChangeListener, Serializable, ScopedSymbolTable, IssueSource {
    private final static Logger lg = LogManager.getLogger(Model.class);

    public interface Owner {
        Model getModel();
    }

    public static final String PROPERTY_NAME_REACTION_STEPS = "reactionSteps";
    public static final String PROPERTY_NAME_STRUCTURES = "structures";
    public static final String PROPERTY_NAME_SPECIES_CONTEXTS = "speciesContexts";
    private static final String PROPERTY_NAME_SPECIES = "species";
    public static final String PROPERTY_NAME_MODEL_ENTITY_NAME = "modelEntityName";

    public static final String PROPERTY_NAME_RATERULEVARIABLES = "rateruleVariables";

    private Version version;
    protected transient PropertyChangeSupport propertyChange;
    private java.lang.String fieldName = "NoName";
    protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
    private java.lang.String fieldDescription = "";
    private Structure[] structures = new Structure[0];
    private Species[] species = new Species[0];
    private SpeciesContext[] speciesContexts = new SpeciesContext[0];
    //	private RateRuleVariable[] fieldRateRuleVariables = new RateRuleVariable[0];
    private ReactionStep[] reactionSteps = new ReactionStep[0];
    private Diagram[] diagrams = new Diagram[0];
    private ModelNameScope nameScope = new Model.ModelNameScope();
    private Model.ModelFunction[] fieldModelFunctions = new Model.ModelFunction[0];
    private Model.ModelParameter[] modelParameters = new Model.ModelParameter[0];
    private Model.ReservedSymbol[] fieldReservedSymbols = new Model.ReservedSymbol[0];
    private final ModelUnitSystem unitSystem;
    private transient VCMetaData vcMetaData = null;
    private StructureTopology structureTopology = new StructureTopology();
    private ElectricalTopology electricalTopology = new ElectricalTopology();

    private final RbmModelContainer rbmModelContainer = new RbmModelContainer();
    //private final RbmModelContainer rbmModelContainer = new org.vcell.model.rbm.simple.RbmModelContainerSimple(ModelUnitSystem.createDefaultVCModelUnitSystem());

    public interface ElectricalTopologyListener {
        void electricalTopologyChanged(ElectricalTopology electricalTopology);
    }

    private transient ArrayList<ElectricalTopologyListener> transientElectricalTopologyListeners = null;

    private ArrayList<ElectricalTopologyListener> getElectricalTopologyListeners(){
        if(transientElectricalTopologyListeners == null){
            transientElectricalTopologyListeners = new ArrayList<>();
        }
        return transientElectricalTopologyListeners;
    }

    public void addElectricalTopologyListener(ElectricalTopologyListener listener){
        if(!getElectricalTopologyListeners().contains(listener)){
            getElectricalTopologyListeners().add(listener);
        }
    }

    public void removeElectricalTopologyListener(ElectricalTopologyListener listener){
        getElectricalTopologyListeners().remove(listener);
    }

    private void fireElectricalTopologyChanged(ElectricalTopology argElectricalTopology){
        List<ElectricalTopologyListener> listeners = getElectricalTopologyListeners();
        for(ElectricalTopologyListener listener : listeners){
            listener.electricalTopologyChanged(argElectricalTopology);
        }
    }

    public class StructureTopology implements Serializable, Matchable {
        private Map<Membrane, Feature> insideFeatures = new HashMap<>();
        private Map<Membrane, Feature> outsideFeatures = new HashMap<>();
        private Map<Feature, Membrane> enclosingMembrane = new HashMap<>();

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

        public Enumeration<Feature> getSubFeatures(Feature feature){
            Vector<Feature> subFeatures = new Vector<>();
            Structure[] structures = getStructures();
            for (Structure structure : structures) {
                if ((structure instanceof Feature) && enclosedBy(structure, feature)) {
                    subFeatures.addElement((Feature) structure);
                }
            }
            return subFeatures.elements();
        }


        public Membrane getMembrane(Feature feature1, Feature feature2){
            for (Structure fieldStructure : structures) {
                if (fieldStructure instanceof Membrane membrane) {
                    if (insideFeatures.get(membrane) == feature1 && outsideFeatures.get(membrane) == feature2) {
                        return membrane;
                    }
                    if (insideFeatures.get(membrane) == feature2 && outsideFeatures.get(membrane) == feature1) {
                        return membrane;
                    }
                }
            }
            return null;
        }

        public Structure getParentStructure(Structure structure){
            if(structure instanceof Membrane){
                return outsideFeatures.get(structure);
            } else {
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

        public Feature getInsideFeature(Membrane membrane){
            return insideFeatures.get(membrane);
        }

        public Feature getOutsideFeature(Membrane membrane){
            return outsideFeatures.get(membrane);
        }

        public void refresh(){
            Structure[] structures = getStructures();
            for(Structure struct : structures){
                if(struct instanceof Feature){
                    Membrane enclosingMem = getMembrane((Feature) struct);
                    if(enclosingMem != null && !contains(enclosingMem)){
                        enclosingMembrane.remove(struct);
                        if(getInsideFeature(enclosingMem) != null){
                            insideFeatures.remove(enclosingMem);
                        }
                        if(getOutsideFeature(enclosingMem) != null){
                            outsideFeatures.remove(enclosingMem);
                        }
                    }
                } else if(struct instanceof Membrane membrane){
                    Feature insideFeature = getInsideFeature(membrane);
                    if(insideFeature != null && !(contains(insideFeature))){
                        insideFeatures.remove(membrane);
                        enclosingMembrane.remove(insideFeature);
                    }
                    Feature outsideFeature = getOutsideFeature(membrane);
                    if(outsideFeature != null && !(contains(outsideFeature))){
                        outsideFeatures.remove(membrane);
                    }
                }
            }
        }

        public boolean enclosedBy(Structure structure, Structure parentStructure){
            if(structure instanceof Feature feature){
                if(parentStructure == feature){
                    return true;
                }
                if(getMembrane(feature) != null){
                    return enclosedBy(getMembrane(feature), parentStructure);
                }
                return false;
            } else if(structure instanceof Membrane membrane){
                if(parentStructure == membrane){
                    return true;
                }
                return enclosedBy(getOutsideFeature(membrane), parentStructure);
            } else {
                throw new IllegalArgumentException("unexpected argument of StructureTopology.enclosedBy()");
            }
        }

        @Override
        public boolean compareEqual(Matchable object){
            return object instanceof StructureTopology structTopology
                    && Compare.isEqual(insideFeatures, structTopology.insideFeatures)
                    && Compare.isEqual(outsideFeatures, structTopology.outsideFeatures)
                    && Compare.isEqual(enclosingMembrane, structTopology.enclosingMembrane);

        }

        public boolean isEmpty(){
            return insideFeatures.size() + outsideFeatures.size() + enclosingMembrane.size() == 0;
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

        private HashMap<Membrane, Feature> positiveFeatures = new HashMap<>();
        private HashMap<Membrane, Feature> negativeFeatures = new HashMap<>();

        public ElectricalTopology(){
        }

        public void setPositiveFeature(Membrane membrane, Feature insideFeature){
            positiveFeatures.put(membrane, insideFeature);
            fireElectricalTopologyChanged(this);
        }

        public void setNegativeFeature(Membrane membrane, Feature outsideFeature){
            negativeFeatures.put(membrane, outsideFeature);
            fireElectricalTopologyChanged(this);
        }

        public Feature getPositiveFeature(Membrane membrane){
            return positiveFeatures.get(membrane);
        }

        public Feature getNegativeFeature(Membrane membrane){
            return negativeFeatures.get(membrane);
        }

        public void gatherIssues(IssueContext issueContext, List<Issue> issueList){
            // check if membranes in model have positive and negative features set.
            for(Structure struct : getStructures()){
                if(struct instanceof Membrane membrane){
                    ArrayList<ReactionStep> electricalReactions = new ArrayList<>();
                    for(ReactionStep reactionStep : getReactionSteps()){
                        if(reactionStep.getStructure() == membrane){
                            if(reactionStep.hasElectrical()){
                                electricalReactions.add(reactionStep);
                            }
                        }
                    }
                    String issueMsgPrefix = "Membrane '" + membrane.getName() + "' ";
                    Feature positiveFeature = getPositiveFeature(membrane);
                    Feature negativeFeature = getNegativeFeature(membrane);

                    if(!electricalReactions.isEmpty()){

                        StringBuilder reactionNames = new StringBuilder();
                        for(ReactionStep rs : electricalReactions){
                            reactionNames.append(rs.getName()).append(",");
                        }
                        reactionNames.deleteCharAt(reactionNames.length() - 1);

                        if(positiveFeature == null){
                            issueList.add(new Issue(membrane, issueContext, IssueCategory.MembraneElectricalPolarityError, "Positive compartment of " + issueMsgPrefix + "is required for electrical reactions (" + reactionNames + ").", Issue.SEVERITY_ERROR));
                        }
                        if(negativeFeature == null){
                            issueList.add(new Issue(membrane, issueContext, IssueCategory.MembraneElectricalPolarityError, "Negative compartment of " + issueMsgPrefix + "is required for electrical reactions (" + reactionNames + ").", Issue.SEVERITY_ERROR));
                        }
                    }
                    if(positiveFeature != null && negativeFeature != null && positiveFeature.compareEqual(negativeFeature)){
                        issueList.add(new Issue(membrane, issueContext, IssueCategory.MembraneElectricalPolarityError, "Positive and Negative features of " + issueMsgPrefix + " cannot be the same.", Issue.SEVERITY_ERROR));
                    }

                }
            }
        }

        public void populateFromStructureTopology(){
            // if the positive & negative features for the membranes are already set, do not override using struct topology.
            boolean bChanged = false;
            Structure[] structures = getStructures();
            for(Structure struct : structures){
                if(struct instanceof Membrane membrane){
                    Feature positiveFeatureFromStructTopology = getStructureTopology().getInsideFeature(membrane);
                    Feature positiveFeatureFromElectricalTopology = getPositiveFeature(membrane);
                    if(positiveFeatureFromStructTopology != null){
                        if(positiveFeatureFromElectricalTopology != null && !contains(positiveFeatureFromElectricalTopology)){
                            // if there is an entry in the positiveFeature hashMap for the membrane, but the feature is not in model, remove entry in hashMap
                            positiveFeatures.remove(membrane);
                            bChanged = true;
                        } else {
                            // if positiveFeature from structTopology != null, and the membrane does not have an entry in positiveFeatures hashMap, add it.
                            setPositiveFeature(membrane, positiveFeatureFromStructTopology);
                            bChanged = true;
                        }
                    } else {
                        // if there is no positiveFeature from structTopology, and the membrane's entry in positiveFeatures hashMap is not in the model, remove the entry.
                        if(positiveFeatureFromElectricalTopology != null && !contains(positiveFeatureFromElectricalTopology)){
                            positiveFeatures.remove(membrane);
                            bChanged = true;
                        }
                    }

                    Feature negativeFeatureFromStructTopology = getStructureTopology().getOutsideFeature(membrane);
                    Feature negativeFeatureFromElectricalTopology = getNegativeFeature(membrane);
                    if(negativeFeatureFromStructTopology != null){
                        if(negativeFeatureFromElectricalTopology != null && !contains(negativeFeatureFromElectricalTopology)){
                            // if there is an entry in the negativeFeature hashMap for the membrane, but the feature is not in model, remove entry in hashMap
                            negativeFeatures.remove(membrane);
                            bChanged = true;
                        } else {
                            // if negativeFeature from structTopology != null, and the membrane does not have an entry in negativeFeatures hashMap, add it.
                            setNegativeFeature(membrane, negativeFeatureFromStructTopology);
                            bChanged = true;
                        }
                    } else {
                        // if there is no negativeFeature from structTopology, and the membrane's entry in negativeFeatures hashMap is not in the model, remove the entry.
                        if(negativeFeatureFromElectricalTopology != null && !contains(negativeFeatureFromElectricalTopology)){
                            negativeFeatures.remove(membrane);
                            bChanged = true;
                        }
                    }
                }
            }
            if(bChanged){
                fireElectricalTopologyChanged(this);
            }
        }

        public void refresh(){
            // if any membrane has been removed, remove its entry in the positiveFetures and negativeFeatures hashMap (separately?)
            boolean bChanged = false;
            Set<Membrane> membranesSet = positiveFeatures.keySet();
            Iterator<Membrane> membranesIter = membranesSet.iterator();
            while (membranesIter.hasNext()) {
                Membrane membrane = membranesIter.next();
                if(!contains(membrane)){
                    membranesIter.remove();
                    bChanged = true;
                    // positiveFeatures.remove(membrane);
                }
            }
            membranesSet = negativeFeatures.keySet();
            membranesIter = membranesSet.iterator();
            while (membranesIter.hasNext()) {
                Membrane membrane = membranesIter.next();
                if(!contains(membrane)){
                    membranesIter.remove();
                    bChanged = true;
                    // negativeFeatures.remove(membrane);
                }
            }

            if(bChanged){
                fireElectricalTopologyChanged(this);
            }
            // now populate electrical topology (+ve features and -ve features hashMap based on structureTopology, if a heirarchy exists.
            populateFromStructureTopology();
        }

        @Override
        public boolean compareEqual(Matchable object){
            return object instanceof ElectricalTopology eTopology
                    && Compare.isEqual(positiveFeatures, eTopology.positiveFeatures)
                    && Compare.isEqual(negativeFeatures, eTopology.negativeFeatures);
        }
    }

    public class ModelNameScope extends BioNameScope {
        public ModelNameScope(){
            super();
        }

        public NameScope[] getChildren(){
            //
            // return list of reactionNameScopes
            //
            NameScope[] nameScopes = new NameScope[Model.this.reactionSteps.length + Model.this.structures.length + Model.this.getRbmModelContainer().getReactionRuleList().size()];
            int j = 0;
            for (ReactionStep fieldReactionStep : Model.this.reactionSteps) {
                nameScopes[j++] = fieldReactionStep.getNameScope();
            }
            for (Structure fieldStructure : Model.this.structures) {
                nameScopes[j++] = fieldStructure.getNameScope();
            }
            if(!Model.this.getRbmModelContainer().isEmpty()){
                for(int i = 0; i < Model.this.getRbmModelContainer().getReactionRuleList().size(); i++){
                    nameScopes[j++] = Model.this.getRbmModelContainer().getReactionRule(i).getNameScope();
                }
            }
            return nameScopes;
        }

        public String getName(){
            return TokenMangler.fixTokenStrict(Model.this.getName());
        }

        public NameScope getParent(){
            //System.out.println("ModelNameScope.getParent() returning null ... no parent");
            return null;
        }

        public ScopedSymbolTable getScopedSymbolTable(){
            return Model.this;
        }

        public boolean isPeer(NameScope nameScope){
            if(super.isPeer(nameScope)){
                return true;
            }
            return ((nameScope instanceof BioNameScope) && (((BioNameScope) nameScope).getNamescopeType() == NamescopeType.mathmappingType) && nameScope.isPeer(this));
        }

        @Override
        public String getPathDescription(){
            return "Global";
        }

        @Override
        public NamescopeType getNamescopeType(){
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
//		NaN
    }

    public ReservedSymbol getTIME(){
        return getReservedSymbolByRole(ReservedSymbolRole.TIME);
    }


    public ReservedSymbol getX(){
        return getReservedSymbolByRole(ReservedSymbolRole.X);
    }


    public ReservedSymbol getY(){
        return getReservedSymbolByRole(ReservedSymbolRole.Y);
    }


    public ReservedSymbol getZ(){
        return getReservedSymbolByRole(ReservedSymbolRole.Z);
    }


    public ReservedSymbol getTEMPERATURE(){
        return getReservedSymbolByRole(ReservedSymbolRole.TEMPERATURE);
    }


    public ReservedSymbol getPI_CONSTANT(){
        return getReservedSymbolByRole(ReservedSymbolRole.PI_CONSTANT);
    }


    public ReservedSymbol getFARADAY_CONSTANT(){
        return getReservedSymbolByRole(ReservedSymbolRole.FARADAY_CONSTANT);
    }


    public ReservedSymbol getFARADAY_CONSTANT_NMOLE(){
        return getReservedSymbolByRole(ReservedSymbolRole.FARADAY_CONSTANT_NMOLE);
    }


    public ReservedSymbol getN_PMOLE(){
        return getReservedSymbolByRole(ReservedSymbolRole.N_PMOLE);
    }


    public ReservedSymbol getK_GHK(){
        return getReservedSymbolByRole(ReservedSymbolRole.K_GHK);
    }


    public ReservedSymbol getGAS_CONSTANT(){
        return getReservedSymbolByRole(ReservedSymbolRole.GAS_CONSTANT);
    }


    public ReservedSymbol getKMILLIVOLTS(){
        return getReservedSymbolByRole(ReservedSymbolRole.KMILLIVOLTS);
    }

//		public ReservedSymbol getNaN() {
//			return getReservedSymbolByRole(ReservedSymbolRole.NaN);
//		}

    /**
     * @deprecated : used only for backward compatibility
     */
    public ReservedSymbol getKMOLE(){
        return getReservedSymbolByRole(ReservedSymbolRole.KMOLE);
    }


    public class ReservedSymbol implements EditableSymbolTableEntry, Serializable {
        private String name;
        private Expression constantValue;
        private String description;
        private VCUnitDefinition unitDefinition;
        private ReservedSymbolRole role;

        private ReservedSymbol(ReservedSymbolRole role, String argName, String argDescription, VCUnitDefinition argUnitDefinition, Expression argConstantValue){
            this.role = role;
            this.name = argName;
            this.unitDefinition = argUnitDefinition;
            this.constantValue = argConstantValue;
            this.description = argDescription;
        }


        public boolean equals(Object obj){
            return obj instanceof ReservedSymbol rs
                    && rs.name.equals(name);
        }

        public double getConstantValue() throws ExpressionException{
            throw new ExpressionException(getName() + " is not constant");
        }

        public ReservedSymbolRole getRole(){
            return role;
        }

        public final String getDescription(){
            return description;
        }


        public Expression getExpression(){
            return constantValue;
        }


        public int getIndex(){
            return -1;
        }


        public final String getName(){
            return name;
        }


        public NameScope getNameScope(){
            return Model.this.getNameScope();
        }


        public VCUnitDefinition getUnitDefinition(){
            return unitDefinition;
        }


        public int hashCode(){
            return name.hashCode();
        }

        public boolean isConstant(){
            return false;  //constantValue!=null;
        }


        public String toString(){
            return getName();
        }


        public boolean isDescriptionEditable(){
            return false;
        }


        public boolean isExpressionEditable(){
            return false;
        }


        public boolean isNameEditable(){
            return false;
        }


        public boolean isUnitEditable(){
            return false;
        }


        public void setDescription(String description) throws ModelPropertyVetoException{
            throw new RuntimeException("cannot change description of a reserved symbol");
        }


        public void setExpression(Expression expression) throws ExpressionBindingException{
            throw new RuntimeException("cannot change the value of a reserved symbol");
        }


        public void setName(String name) throws ModelPropertyVetoException{
            throw new RuntimeException("cannot rename a reserved symbols");
        }


        public void setUnitDefinition(VCUnitDefinition unit) throws ModelPropertyVetoException{
            throw new RuntimeException("cannot change unit of a reserved symbol");
        }


        public boolean isX(){
            return (this.role.equals(ReservedSymbolRole.X));
        }

        public boolean isY(){
            return (this.role.equals(ReservedSymbolRole.Y));
        }

        public boolean isZ(){
            return (this.role.equals(ReservedSymbolRole.Z));
        }

        public boolean isTime(){
            return (this.role.equals(ReservedSymbolRole.TIME));
        }

        public boolean isTemperature(){
            return (this.role.equals(ReservedSymbolRole.TEMPERATURE));
        }


        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener){
        }


        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener){
        }

    }

    public static final int ROLE_UserDefined = 0;
    public static final int NUM_ROLES = 1;
    public static final String RoleDesc = "user defined";

    public static final String PROPERTY_NAME_MODEL_PARAMETERS = "modelParameters";
    public static final String PROPERTY_NAME_MODEL_FUNCTIONS = "modelFunctions";

    public class ModelFunction extends SimpleSymbolTableFunctionEntry {

        public ModelFunction(String funcName, String[] argNames, Expression expression, VCUnitDefinition vcUnitDefinition){
            super(funcName, argNames, getFunctionArgTypes(argNames.length), expression, vcUnitDefinition, Model.this.getNameScope());
        }

        public Expression getFlattenedExpression(FunctionInvocation functionInvocation) throws ExpressionException{
            Expression exp = new Expression(getExpression());
            for(int i = 0; i < getNumArguments(); i++){
                String argName = getArgNames()[i];
                exp.substituteInPlace(new Expression(argName), functionInvocation.getArguments()[i]);
            }
            return exp;
        }

        public String getDescription(){
            return "user defined";
        }


    }

    private static FunctionArgType[] getFunctionArgTypes(int numArgs){
        FunctionArgType[] functionArgTypes = new FunctionArgType[numArgs];
        Arrays.fill(functionArgTypes, FunctionArgType.NUMERIC);
        return functionArgTypes;
    }

    public class ModelParameter extends Parameter implements ExpressionContainer, IssueSource, Displayable, Identifiable, VCellSbmlName {

        private String fieldParameterName;
        private String sbmlId = null;
        private String sbmlName = null;
        private Expression fieldParameterExpression;
        private int fieldParameterRole;
        private VCUnitDefinition fieldUnitDefinition;
        private String modelParameterAnnotation;

        private static final String MODEL_PARAMETER_DESCRIPTION = "user defined";

        public ModelParameter(String argName, Expression expression, int argRole, VCUnitDefinition argUnitDefinition){
            if(argName == null){
                throw new IllegalArgumentException("parameter name is null");
            }
            if(argName.isEmpty()){
                throw new IllegalArgumentException("parameter name is zero length");
            }
            this.fieldParameterName = argName;
            this.fieldParameterExpression = expression;
            this.fieldUnitDefinition = argUnitDefinition;
            if(argRole >= 0 && argRole < NUM_ROLES){
                this.fieldParameterRole = argRole;
            } else {
                throw new IllegalArgumentException("parameter 'role' = " + argRole + " is out of range");
            }
            super.setDescription(MODEL_PARAMETER_DESCRIPTION);
        }


        public String getModelParameterAnnotation(){
            return modelParameterAnnotation;
        }

        public void setModelParameterAnnotation(String modelParameterAnnotation){
            this.modelParameterAnnotation = modelParameterAnnotation;
        }

        @Override
        public boolean compareEqual(Matchable obj){
            return obj instanceof ModelParameter mp
                    && super.compareEqual0(mp)
                    && fieldParameterRole == mp.fieldParameterRole
                    && Compare.isEqualOrNull(getSbmlId(), mp.getSbmlId())
                    && Compare.isEqualOrNull(getSbmlName(), mp.getSbmlName());
        }


        @Override
        public boolean relate(Relatable obj, RelationVisitor rv){
            return obj instanceof ModelParameter mp
                    && super.relate0(mp, rv)
                    && fieldParameterRole == mp.fieldParameterRole
                    && rv.relateOrNull(getSbmlId(), mp.getSbmlId())
                    && rv.relateOrNull(getSbmlName(), mp.getSbmlName());
        }

        @Override
        public boolean isExpressionEditable(){
            return true;
        }

        @Override
        public boolean isUnitEditable(){
            return true;
        }

        @Override
        public boolean isNameEditable(){
            return true;
        }

        @Override
        public double getConstantValue() throws ExpressionException{
            return this.fieldParameterExpression.evaluateConstant();
        }

        @Override
        public Expression getExpression(){
            return this.fieldParameterExpression;
        }

        public int getIndex(){
            return -1;
        }

        public String getName(){
            return this.fieldParameterName;
        }

        public String getSbmlId(){
            return this.sbmlId;
        }

        public String getSbmlName(){
            return this.sbmlName;
        }

        public NameScope getNameScope(){
            return Model.this.nameScope;
        }

        public int getRole(){
            return this.fieldParameterRole;
        }

        public VCUnitDefinition getUnitDefinition(){
            return fieldUnitDefinition;
        }

        public void setUnitDefinition(VCUnitDefinition unitDefinition){
            VCUnitDefinition oldValue = fieldUnitDefinition;
            fieldUnitDefinition = unitDefinition;
            super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
        }

        public void setExpression(Expression expression){
            Expression oldValue = fieldParameterExpression;
            fieldParameterExpression = expression;
            super.firePropertyChange("expression", oldValue, expression);
        }

        public void setName(java.lang.String name) throws java.beans.PropertyVetoException{
            String oldValue = fieldParameterName;
            super.fireVetoableChange("name", oldValue, name);
            fieldParameterName = name;
            super.firePropertyChange("name", oldValue, name);
        }

        public void setSbmlId(String newValue) throws PropertyVetoException{
            String oldValue = this.sbmlId;
            fireVetoableChange("sbmlId", oldValue, newValue);
            this.sbmlId = newValue;
            firePropertyChange("sbmlId", oldValue, newValue);
        }

        public void setSbmlName(String newString) throws PropertyVetoException{
            String oldValue = this.sbmlName;
            String newValue = SpeciesContext.fixSbmlName(newString);

            fireVetoableChange("sbmlName", oldValue, newValue);
            this.sbmlName = newValue;
            firePropertyChange("sbmlName", oldValue, newValue);
        }

        private static final String typeName = "GlobalParameter";

        @Override
        public final String getDisplayName(){
            return getName();
        }

        @Override
        public final String getDisplayType(){
            return typeName;
        }
    }

    public class RbmModelContainer implements Matchable, Serializable, IssueSource {
        private List<MolecularType> molecularTypeList = new ArrayList<>();
        private List<ReactionRule> reactionRuleList = new ArrayList<>();
        private List<RbmObservable> observableList = new ArrayList<>();
        public static final String PROPERTY_NAME_MOLECULAR_TYPE_LIST = "molecularTypeList";
        public static final String PROPERTY_NAME_OBSERVABLE_LIST = "observableList";
        public static final String PROPERTY_NAME_FUNCTION_LIST = "functionList";
        public static final String PROPERTY_NAME_REACTION_RULE_LIST = "reactionRuleList";

        public boolean isEmpty(){
            return molecularTypeList.isEmpty()
                    && reactionRuleList.isEmpty()
                    && observableList.isEmpty();
        }

        // reaction rules are the only entities with "wildcards"
        // as long as we don't have rules we can allow multiple compartments, for example
        public boolean hasRules(){
            return !reactionRuleList.isEmpty();
        }

        public void gatherIssues(IssueContext issueContext, List<Issue> issueList){
            if(this.isEmpty()){
                issueList.add(new Issue(Model.this, issueContext, IssueCategory.InternalError, "RbmModelContainer is empty. No action needs to be taken.", Issue.SEVERITY_INFO));
                return;
            }

            if(issueList == null){
                return;
            }
            if(molecularTypeList == null){
                issueList.add(new Issue(this, issueContext, IssueCategory.RbmMolecularTypesTableBad, MolecularType.typeName + " List is null", Issue.SEVERITY_ERROR));
            } else {
                for(MolecularType entity : molecularTypeList){
                    entity.gatherIssues(issueContext, issueList);
                }
            }
            if(observableList == null){
                issueList.add(new Issue(this, issueContext, IssueCategory.RbmObservablesTableBad, "Observable List is null", Issue.SEVERITY_ERROR));
            } else {
                for(RbmObservable entity : observableList){
                    entity.gatherIssues(issueContext, issueList);
                }
            }
            if(reactionRuleList == null){
                issueList.add(new Issue(this, issueContext, IssueCategory.RbmReactionRulesTableBad, "Reaction Rule List is null", Issue.SEVERITY_ERROR));
            } else {
                for(ReactionRule entity : reactionRuleList){
                    entity.gatherIssues(issueContext, issueList);
                }
            }
//			if(networkConstraints == null) {
//				issueList.add(new Issue(this, issueContext, IssueCategory.RbmNetworkConstraintsBad, "Network Constraints is null", Issue.SEVERITY_ERROR));
//			} else {
//				networkConstraints.gatherIssues(issueContext, issueList);
//			}
        }

        public boolean isDeleteAllowed(MolecularType mt, MolecularComponent mc, ComponentStateDefinition cs){
            for(ReactionRule rr : getReactionRuleList()){
                for(ProductPattern pp : rr.getProductPatterns()){
                    if(cantDelete(mt, mc, cs, pp.getSpeciesPattern().getMolecularTypePatterns())){
                        return false;
                    }
                }
                for(ReactantPattern rp : rr.getReactantPatterns()){
                    if(cantDelete(mt, mc, cs, rp.getSpeciesPattern().getMolecularTypePatterns())){
                        return false;
                    }
                }
            }
            for(SpeciesContext sc : Model.this.getSpeciesContexts()){
                if(cantDelete(mt, mc, cs, sc.getSpeciesPattern().getMolecularTypePatterns())){
                    return false;
                }
            }
            for(RbmObservable o : getObservableList()){
                for(SpeciesPattern sp : o.getSpeciesPatternList()){
                    if(cantDelete(mt, mc, cs, sp.getMolecularTypePatterns())){
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean canDelete(MolecularType molecularType, MolecularComponent molecularComponent, ComponentStateDefinition componentStateDefinition, List<MolecularTypePattern> molecularTypePatternList){
            for(MolecularTypePattern mtp : molecularTypePatternList){
                MolecularType mt1 = mtp.getMolecularType();
                if(!(molecularType.getName().equals(mt1.getName()))) continue;
                for(MolecularComponentPattern mcp : mtp.getComponentPatternList()){
                    if(mcp.isImplied()){
                        continue;
                    }
                    if(!(mcp.getMolecularComponent().getName().equals(molecularComponent.getName()))) continue;
                    //System.out.println(mcp.toString());
                    if(mcp.getComponentStatePattern() == null) continue;
                    if(mcp.getComponentStatePattern().getComponentStateDefinition() == null) continue;
                    if(mcp.getComponentStatePattern().getComponentStateDefinition().getName().equals(componentStateDefinition.getName())){
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean cantDelete(MolecularType molecularType, MolecularComponent molecularComponent,
                                   ComponentStateDefinition componentStateDefinition, List<MolecularTypePattern> molecularTypePatternList){
            for(MolecularTypePattern mtp : molecularTypePatternList){
                MolecularType mt = mtp.getMolecularType();
                if(!(molecularType.getName().equals(mt.getName()))) continue;
                for(MolecularComponentPattern mcp : mtp.getComponentPatternList()){
                    if(mcp.isImplied()) continue;
                    if(!(mcp.getMolecularComponent().getName().equals(molecularComponent.getName()))) continue;
                    //System.out.println(mcp.toString());
                    ComponentStatePattern csp = mcp.getComponentStatePattern();
                    if(csp == null) continue;
                    ComponentStateDefinition csd = csp.getComponentStateDefinition();
                    if(csd == null) continue;
                    if(csd.getName().equals(componentStateDefinition.getName()))
                        return true;
                }
            }
            return false;
        }

        public void findComponentUsage(MolecularType mt, MolecularComponent mc, Map<String, Pair<Displayable, SpeciesPattern>> usedHere){
            for(ReactionRule rr : getReactionRuleList()){
                rr.findComponentUsage(mt, mc, usedHere);
            }
            for(SpeciesContext sc : Model.this.getSpeciesContexts()){
                sc.findComponentUsage(mt, mc, usedHere);
            }
            for(RbmObservable o : getObservableList()){
                o.findComponentUsage(mt, mc, usedHere);
            }
        }

        public void findStateUsage(MolecularType mt, MolecularComponent mc, ComponentStateDefinition csd,
                                   Map<String, Pair<Displayable, SpeciesPattern>> usedHere){
            for(ReactionRule rr : getReactionRuleList()){
                rr.findStateUsage(mt, mc, csd, usedHere);
            }
            for(SpeciesContext sc : Model.this.getSpeciesContexts()){
                sc.findStateUsage(mt, mc, csd, usedHere);
            }
            for(RbmObservable o : getObservableList()){
                o.findStateUsage(mt, mc, csd, usedHere);
            }
        }


        // deletes the molecular component from everywhere it's being used
        public boolean delete(MolecularType mt, MolecularComponent mc){
            boolean ret;
            for(ReactionRule rr : getReactionRuleList()){
                ret = rr.deleteComponentFromPatterns(mt, mc);
                if(!ret){
                    return false;
                }
            }
            for(SpeciesContext sc : Model.this.getSpeciesContexts()){
                ret = sc.deleteComponentFromPatterns(mt, mc);
                if(!ret){
                    return false;
                }
            }
            for(RbmObservable o : getObservableList()){
                ret = o.deleteComponentFromPatterns(mt, mc);
                if(!ret){
                    return false;
                }
            }
            return true;
        }

        // deletes State from everywhere it's being used
        public boolean delete(MolecularType mt, MolecularComponent mc, ComponentStateDefinition csd){
            boolean ret;
            for(ReactionRule rr : getReactionRuleList()){
                ret = rr.deleteStateFromPatterns(mt, mc, csd);
                if(!ret){
                    return false;
                }
            }
            for(SpeciesContext sc : Model.this.getSpeciesContexts()){
                ret = sc.deleteStateFromPatterns(mt, mc, csd);
                if(!ret){
                    return false;
                }
            }
            for(RbmObservable o : getObservableList()){
                ret = o.deleteStateFromPatterns(mt, mc, csd);
                if(!ret){
                    return false;
                }
            }
            return true;
        }

        public boolean isDeleteAllowed(MolecularType mt, Map<String, Pair<Displayable, SpeciesPattern>> usedHere){
            boolean isAllowed = true;
            for(ReactionRule rrr : getReactionRuleList()){
                for(ProductPattern ppp : rrr.getProductPatterns()){
                    if(cantDelete(rrr, mt, ppp.getSpeciesPattern(), usedHere)){
                        isAllowed = false;
                    }
                }
                for(ReactantPattern rpp : rrr.getReactantPatterns()){
                    if(cantDelete(rrr, mt, rpp.getSpeciesPattern(), usedHere)){
                        isAllowed = false;
                    }
                }
            }
            for(SpeciesContext sc : Model.this.getSpeciesContexts()){
                if(!sc.hasSpeciesPattern()){
                    continue;
                }
                if(cantDelete(sc, mt, sc.getSpeciesPattern(), usedHere)){
                    isAllowed = false;
                }
            }
            for(RbmObservable o : getObservableList()){
                for(SpeciesPattern sp : o.getSpeciesPatternList()){
                    if(cantDelete(o, mt, sp, usedHere)){
                        isAllowed = false;
                    }
                }
            }
            return isAllowed;
        }

        private boolean cantDelete(Displayable o, MolecularType mt, SpeciesPattern sp, Map<String, Pair<Displayable, SpeciesPattern>> usedHere){
            boolean cant = false;
            for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
                if(mt != mtp.getMolecularType()) continue;
                String key = sp.getDisplayName();
                key = o.getDisplayType() + o.getDisplayName() + key;
                usedHere.put(key, new Pair<>(o, sp));
                cant = true;
            }
            return cant;
        }

        public void addMolecularType(MolecularType molecularType, boolean makeObservable) throws ModelException, PropertyVetoException{
            if(getMolecularType(molecularType.getName()) != null){
                throw new ModelException(molecularType.getDisplayType() + " '" + molecularType.getDisplayName() + "' already exists!");
            }
            ArrayList<MolecularType> newValue = new ArrayList<>(molecularTypeList);
            newValue.add(molecularType);
            setMolecularTypeList(newValue);

            if(makeObservable){        // we also make an observable to go with the newly created molecular type
                RbmObservable o = createObservable(RbmObservable.ObservableType.Molecules, molecularType);
                MolecularTypePattern mtp = new MolecularTypePattern(molecularType, true);
                SpeciesPattern sp = new SpeciesPattern();
                sp.addMolecularTypePattern(mtp);
                o.addSpeciesPattern(sp);
                addObservable(o);
            }
        }

        public void addMolecularTypes(List<MolecularType> molecularTypes) throws ModelException, PropertyVetoException{
            ArrayList<MolecularType> newValue = new ArrayList<>(molecularTypeList);
            newValue.addAll(molecularTypes);
            setMolecularTypeList(newValue);
        }

        public boolean removeMolecularType(MolecularType molecularType) throws PropertyVetoException{
            if(!molecularTypeList.contains(molecularType)){
                return false;
            }
            ArrayList<MolecularType> newValue = new ArrayList<>(molecularTypeList);
            newValue.remove(molecularType);
            setMolecularTypeList(newValue);
            return true;
        }

        public void deleteMolecularType(String molecularTypeName) throws PropertyVetoException{
            MolecularType molecularType = getMolecularType(molecularTypeName);
            if(molecularType == null){
                return;
            }
            ArrayList<MolecularType> newValue = new ArrayList<>(molecularTypeList);
            newValue.remove(molecularType);
            setMolecularTypeList(newValue);
        }

        public boolean removeReactionRule(ReactionRule reactionRule) throws PropertyVetoException{
            if(!reactionRuleList.contains(reactionRule)){
                return false;
            }
            ArrayList<ReactionRule> newValue = new ArrayList<>(reactionRuleList);
            newValue.remove(reactionRule);
            setReactionRules(newValue);
            return true;
        }

        public MolecularType getMolecularType(String molecularTypeName){
            for(MolecularType molecularType : this.molecularTypeList){
                if(molecularType.getName().equals(molecularTypeName)){
                    return molecularType;
                }
            }
            return null;
        }

        public List<RbmObservable> getObservableList(){
            return new ArrayList<>(observableList);
        }

        public List<Parameter> getConstantOrFunctionList(boolean bConstant, ModelParameter[] modelParameters){
            Map<String, Boolean> constantMap = getConstantMap(modelParameters);
            ArrayList<Parameter> selectedParameters = new ArrayList<>();
            for(Parameter p : Model.this.modelParameters){
                //
                // check that it is not a constant valued function.
                //
                boolean bConst = constantMap.get(p.getName());
                if(bConstant == bConst){
                    selectedParameters.add(p);
                }
            }
            return Collections.unmodifiableList(selectedParameters);
        }

        public List<Parameter> getParameterList(){
            return getConstantOrFunctionList(true, Model.this.modelParameters);
        }

        public List<Parameter> getFunctionList(){
            return getConstantOrFunctionList(false, Model.this.modelParameters);
        }

        private Map<String, Boolean> getConstantMap(ModelParameter[] parameters){
            ArrayList<ModelParameter> unprocessed = new ArrayList<>(Arrays.asList(parameters));
            HashMap<String, Boolean> constantMap = new HashMap<>();
            Iterator<ModelParameter> unprocessedIter = unprocessed.iterator();

            //  assigns the parameters without symbols as constants (the simple case first).
            while (unprocessedIter.hasNext()) {
                ModelParameter unprocessedParam = unprocessedIter.next();
                Expression expression = unprocessedParam.getExpression();
                String[] symbols = expression.getSymbols();
                // if expression has no symbols, must be a constant.
                if(symbols == null || symbols.length == 0){
                    unprocessedIter.remove();
                    constantMap.put(unprocessedParam.getName(), true);
                }
            }

            // assign non-trivial expressions where all referenced parameters have been assigned ... then iterate.
            int iterationCount = 0;
            final int MAX_ITERATIONS = 20;
            while (!unprocessed.isEmpty() && iterationCount < MAX_ITERATIONS) {
                unprocessedIter = unprocessed.iterator();
                while (unprocessedIter.hasNext()) {
                    ModelParameter unprocessedParam = unprocessedIter.next();
                    Expression expression = unprocessedParam.getExpression();
                    String[] symbols = expression.getSymbols();
                    // check if all referenced symbols have been classified as constant or variable,
                    // if any variable then exp is variable
                    boolean bHasUnknown = false;
                    boolean bHasConstant = false;
                    boolean bHasVariable = false;
                    for(String symbol : symbols){
                        SymbolTableEntry ste = expression.getSymbolBinding(symbol);
                        // refers to an unprocessed parameter ... skip
                        if(unprocessed.contains(ste)){
                            bHasUnknown = true;
                        } else {
                            Boolean bConstant = constantMap.get(ste.getName());
                            if(bConstant != null){
                                if(bConstant){
                                    bHasConstant = true;
                                } else {
                                    bHasVariable = true;
                                }
                            } else {
                                // refers to a symbol which is not a modelParameter (e.g. species concentration ... etc).
                                try {
                                    if(ste.isConstant()){
                                        bHasConstant = true;
                                    } else {
                                        bHasVariable = true;
                                    }
                                } catch(ExpressionException e){
                                    bHasVariable = true;
                                }
                            }
                        }
                    }
                    // if have any unprocessed dependencies, then skip it for now.
                    if(!bHasUnknown){
                        constantMap.put(unprocessedParam.getName(), !bHasVariable);
                        unprocessedIter.remove();
                    }
                }
                if(iterationCount >= MAX_ITERATIONS){
                    throw new RuntimeException("getConstantMap() failed to terminate, maybe a cyclic dependenty exists");
                }
            }
            return constantMap;
        }

        public final void setMolecularTypeList(List<MolecularType> newValue) throws PropertyVetoException{
            List<MolecularType> oldValue = molecularTypeList;
            fireVetoableChange(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST, oldValue, newValue);
            if(oldValue != null){
                for(MolecularType mt : oldValue){
                    mt.removePropertyChangeListener(Model.this);
                    mt.removeVetoableChangeListener(Model.this);
                    mt.setModel(null);
                }
            }
            this.molecularTypeList = newValue;
            if(newValue != null){
                for(MolecularType mt : newValue){
                    mt.addPropertyChangeListener(Model.this);
                    mt.addVetoableChangeListener(Model.this);
                    mt.setModel(Model.this);
                }
            }
            firePropertyChange(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST, oldValue, newValue);
        }

        public final void setReactionRules(List<ReactionRule> newValue) throws PropertyVetoException{
            List<ReactionRule> oldValue = reactionRuleList;
            fireVetoableChange(RbmModelContainer.PROPERTY_NAME_REACTION_RULE_LIST, oldValue, newValue);
            if(oldValue != null){
                for(ReactionRule reactionRule : oldValue){
                    reactionRule.removePropertyChangeListener(Model.this);
                    reactionRule.removeVetoableChangeListener(Model.this);
                    reactionRule.setModel(null);
                }
            }
            this.reactionRuleList = newValue;
            if(newValue != null){
                for(ReactionRule reactionRule : newValue){
                    reactionRule.addPropertyChangeListener(Model.this);
                    reactionRule.addVetoableChangeListener(Model.this);
                    reactionRule.setModel(Model.this);
                }
            }
            firePropertyChange(RbmModelContainer.PROPERTY_NAME_REACTION_RULE_LIST, oldValue, newValue);
        }

        public MolecularType createMolecularType(){
            int count = 0;
            String name;
            while (true) {
                name = "MT" + count;
                if(getMolecularType(name) == null){
                    break;
                }
                count++;
            }
            return new MolecularType(name, Model.this);
        }

        public RbmObservable createObservable(RbmObservable.ObservableType type){
            return createObservable(type, null);
        }

        public RbmObservable createObservable(RbmObservable.ObservableType type, MolecularType mt){
            return createObservable(type, mt, null);
        }

        public RbmObservable createObservable(RbmObservable.ObservableType type, MolecularType mt, Structure structure){
            String name;
            String nameRoot;
            String namePostfix;

            if(mt == null){
                nameRoot = "O";
                namePostfix = "";
            } else {
                nameRoot = "O";
                namePostfix = "_" + mt.getName() + "_tot";
            }
            int count = 0;
            while (true) {
                name = nameRoot + count + namePostfix;
                if(Model.isNameUnused(name, Model.this)){
                    break;
                }
                count++;
            }

            int size = structures.length;
            if(size > 0 && structure == null){
                structure = getStructure(0);
            }
            return new RbmObservable(Model.this, name, structure, type);
        }

        public Parameter createParameter(){
            String name;
            for (int count = 0; ; count++){
                name = "param" + count;
                if(getParameter(name) == null){
                    break;
                }
            }
            return new ModelParameter(name, null, ROLE_UserDefined, unitSystem.getInstance_DIMENSIONLESS());
        }

        public ReactionRule createReactionRule(Structure structure){
            String name = getReactionName();
            return createReactionRule(name, structure, true);
        }

        public ReactionRule createReactionRule(String label, Structure structure, boolean bReversible){
            return new ReactionRule(Model.this, label, structure, bReversible);
        }

        public final void setObservableList(List<RbmObservable> newValue) throws PropertyVetoException{
            List<RbmObservable> oldValue = observableList;
            fireVetoableChange(RbmModelContainer.PROPERTY_NAME_OBSERVABLE_LIST, oldValue, newValue);
            if(oldValue != null){
                for(RbmObservable mt : oldValue){
                    mt.removePropertyChangeListener(Model.this);
                    mt.removeVetoableChangeListener(Model.this);
                    mt.setModel(null);
                }
            }
            this.observableList = newValue;
            if(newValue != null){
                for(RbmObservable mt : newValue){
                    mt.addPropertyChangeListener(Model.this);
                    mt.addVetoableChangeListener(Model.this);
                    mt.setModel(Model.this);
                }
            }
            firePropertyChange(RbmModelContainer.PROPERTY_NAME_OBSERVABLE_LIST, oldValue, newValue);
        }

        public List<MolecularType> getMolecularTypeList(){
            return molecularTypeList;
        }

        public void addObservable(RbmObservable observable) throws ModelException, PropertyVetoException{
            if(getObservable(observable.getName()) != null){
                throw new ModelException("Observable '" + observable.getName() + "' already exists!");
            }
            List<RbmObservable> newValue = new ArrayList<>(observableList);
            newValue.add(observable);
            setObservableList(newValue);
        }

        public Parameter addFunction(String name, Expression expression, VCUnitDefinition unitDefinition) throws PropertyVetoException{
            return Model.this.addModelParameter(new ModelParameter(name, expression, ROLE_UserDefined, unitDefinition));
        }

        public Parameter addParameter(String name, Expression expression, VCUnitDefinition unitDefinition) throws ModelException, PropertyVetoException{
            return Model.this.addModelParameter(new ModelParameter(name, expression, ROLE_UserDefined, unitDefinition));
        }

        public RbmObservable getObservable(String obName){
            for(RbmObservable observable : this.observableList){
                if(observable.getName().equals(obName)){
                    return observable;
                }
            }
            return null;
        }

        public Parameter getFunction(String obName){
            ModelParameter p = Model.this.getModelParameter(obName);
            if(p != null){
                List<Parameter> parameters = getParameterList();
                if(parameters.contains(p)){
                    return p;
                }
            }
            return null;
        }

        public Parameter getParameter(String obName){
            return Model.this.getModelParameter(obName);
        }

        public boolean removeObservable(RbmObservable observable) throws PropertyVetoException{
            if(!observableList.contains(observable)){
                return false;
            }
            ArrayList<RbmObservable> newValue = new ArrayList<>(observableList);
            newValue.remove(observable);
            setObservableList(newValue);
            return true;
        }

        public boolean removeParameter(Parameter parameter) throws PropertyVetoException{
            if(!Model.this.contains((ModelParameter) parameter)){
                return false;
            }
            Model.this.removeModelParameter((ModelParameter) parameter);
            return true;
        }

        public void addReactionRule(ReactionRule reactionRule) throws PropertyVetoException{
            List<ReactionRule> newValue = new ArrayList<>(reactionRuleList);
            newValue.add(reactionRule);
            setReactionRules(newValue);
        }

        public void addReactionRules(List<ReactionRule> reactionRules) throws PropertyVetoException{
            List<ReactionRule> newValue = new ArrayList<>(reactionRuleList);
            newValue.addAll(reactionRules);
            setReactionRules(newValue);
        }

        public List<ReactionRule> getReactionRuleList(){
            return reactionRuleList;
        }

        public SymbolTable getSymbolTable(){
            return Model.this;
        }

        public boolean compareEqual(Matchable aThat){
            return this == aThat || aThat instanceof RbmModelContainer that
                    && Compare.isEqual(molecularTypeList, that.getMolecularTypeList())
                    && Compare.isEqual(reactionRuleList, that.getReactionRuleList())
                    && Compare.isEqual(observableList, that.getObservableList());
        }

        public ReactionRule getReactionRule(String name){
            if(name == null){
                return null;
            }
            for(ReactionRule r : reactionRuleList){
                if(name.equals(r.getName())){
                    return r;
                }
            }
            return null;
        }

        public ReactionRule getReactionRule(int index){
            if(index < 0){
                return null;
            }
            if((reactionRuleList == null) || reactionRuleList.isEmpty()){
                return null;
            }
            return reactionRuleList.get(index);
        }

        public void adjustSpeciesContextPatterns(MolecularType mt, MolecularComponent mc){
            System.out.println("adjust for " + mt.getName() + ", " + mc.getName());
            Model model = Model.this;
            for(SpeciesContext sc : model.getSpeciesContexts()){
                if(!sc.hasSpeciesPattern()){
                    continue;
                }
                SpeciesPattern sp = sc.getSpeciesPattern();
                for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
                    if(mtp.getMolecularType() != mt){
                        continue;
                    }
                    boolean found = false;
                    for(MolecularComponentPattern mcp : mtp.getComponentPatternList()){
                        // MolecularTypePattern.getComponentPatternList repairs the mtp list of components
                        // by adding or removing mcp, so that they match the components of the mt
                        if(mcp.getMolecularComponent() == mc && mcp.getBondType() == BondType.Possible){
                            // we correct the bond type since it's a seed species, bond type can't be "Possible"
                            // obs: "Exists" is also illegal, but we leave it be and raise an issue
                            mcp.setBondType(BondType.None);
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        // this should never happen
                        MolecularComponentPattern mcp = new MolecularComponentPattern(mc);
                        mcp.setBondType(BondType.None);
                        mtp.getComponentPatternList().add(mcp);
                        sc.firePropertyChange(PROPERTY_NAME_SPECIES_CONTEXTS, sc, sc);
                    }
                }
            }
        }

        public void adjustObservablesPatterns(MolecularType mt, MolecularComponent mc){
            System.out.println("adjust for " + mt.getName() + ", " + mc.getName());
            Model model = Model.this;
            RbmModelContainer rbmmc = model.getRbmModelContainer();
            List<RbmObservable> ol = rbmmc.getObservableList();
            if(ol == null || ol.isEmpty()){
                return;
            }
            for(RbmObservable o : ol){
                List<SpeciesPattern> spList = o.getSpeciesPatternList();
                for(SpeciesPattern sp : spList){

                    for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
                        if(mtp.getMolecularType() != mt){
                            continue;
                        }
                        boolean found = false;
                        for(MolecularComponentPattern mcp : mtp.getComponentPatternList()){
                            if(mcp.getMolecularComponent() == mc){
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            MolecularComponentPattern mcp = new MolecularComponentPattern(mc);
                            mcp.setBondType(BondType.Possible);
                            mtp.getComponentPatternList().add(mcp);
                            o.firePropertyChange(PROPERTY_NAME_SPECIES_CONTEXTS, o, o);
                        }
                    }
                }
            }
        }

        public void adjustSpeciesPatterns(MolecularType mt, MolecularComponent mc, ComponentStateDefinition csd){
            System.out.println("adjust for " + mt.getName() + ", " + mc.getName() + ", " + csd.getName());

            for(SpeciesContext sc : getSpeciesContexts()){
                if(!sc.hasSpeciesPattern()){
                    continue;
                }
                SpeciesPattern sp = sc.getSpeciesPattern();
                for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
                    if(mtp.getMolecularType() != mt){
                        continue;
                    }
                    for(MolecularComponentPattern mcp : mtp.getComponentPatternList()){
                        if(mcp.getMolecularComponent() != mc){
                            continue;
                        }
                        ComponentStatePattern csp = mcp.getComponentStatePattern();
                        if(csp == null){
                            csp = new ComponentStatePattern();
                            mcp.setComponentStatePattern(csp);
                            // do not return, multiple observables may be defined for this molecule, component, etc
                        }
                    }
                }
            }
        }

        public void adjustObservablesPatterns(MolecularType mt, MolecularComponent mc, ComponentStateDefinition csd){
            System.out.println("adjust for " + mt.getName() + ", " + mc.getName() + ", " + csd.getName());
            Model model = Model.this;
            RbmModelContainer rbmmc = model.getRbmModelContainer();
            List<RbmObservable> ol = rbmmc.getObservableList();
            if(ol == null || ol.isEmpty()){
                return;
            }
            for(RbmObservable o : ol){
                List<SpeciesPattern> spList = o.getSpeciesPatternList();
                for(SpeciesPattern sp : spList){

                    for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
                        if(mtp.getMolecularType() != mt){
                            continue;
                        }
                        for(MolecularComponentPattern mcp : mtp.getComponentPatternList()){
                            if(mcp.getMolecularComponent() != mc){
                                continue;
                            }
                            ComponentStatePattern csp = mcp.getComponentStatePattern();
                            if(csp == null){
                                csp = new ComponentStatePattern();
                                mcp.setComponentStatePattern(csp);
                                // do not return, multiple observables may be defined for this molecule, component, etc
                            }
                        }
                    }
                }
            }
        }

        public void adjustRulesPatterns(MolecularType mt, MolecularComponent mc, ComponentStateDefinition csd){
            System.out.println("adjust for " + mt.getName() + ", " + mc.getName());
            Model model = Model.this;
            RbmModelContainer rbmmc = model.getRbmModelContainer();

            List<ReactionRule> rlList = rbmmc.getReactionRuleList();
            if(rlList == null || rlList.isEmpty()){
                return;
            }
            for(ReactionRule rl : rlList){
                List<ReactantPattern> rpList = rl.getReactantPatterns();
                for(ReactantPattern rp : rpList){
                    SpeciesPattern sp = rp.getSpeciesPattern();
                    for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
                        if(mtp.getMolecularType() != mt){
                            continue;
                        }
                        for(MolecularComponentPattern mcp : mtp.getComponentPatternList()){
                            if(mcp.getMolecularComponent() != mc){
                                continue;
                            }
                            ComponentStatePattern csp = mcp.getComponentStatePattern();
                            if(csp == null){
                                csp = new ComponentStatePattern();
                                mcp.setComponentStatePattern(csp);
                            }
                        }
                    }
                }
                List<ProductPattern> ppList = rl.getProductPatterns();
                for(ProductPattern pp : ppList){
                    SpeciesPattern sp = pp.getSpeciesPattern();
                    for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
                        if(mtp.getMolecularType() != mt){
                            continue;
                        }
                        for(MolecularComponentPattern mcp : mtp.getComponentPatternList()){
                            if(mcp.getMolecularComponent() != mc){
                                continue;
                            }
                            ComponentStatePattern csp = mcp.getComponentStatePattern();
                            if(csp == null){
                                csp = new ComponentStatePattern();
                                mcp.setComponentStatePattern(csp);
                            }
                        }
                    }
                }
            }
        }

        public void adjustRulesPatterns(MolecularType mt, MolecularComponent mc){
            System.out.println("adjust for " + mt.getName() + ", " + mc.getName());
            Model model = Model.this;
            RbmModelContainer rbmmc = model.getRbmModelContainer();

            List<ReactionRule> rlList = rbmmc.getReactionRuleList();
            if(rlList == null || rlList.isEmpty()){
                return;
            }
            for(ReactionRule rl : rlList){
                List<ReactantPattern> rpList = rl.getReactantPatterns();
                for(ReactantPattern rp : rpList){
                    SpeciesPattern sp = rp.getSpeciesPattern();
                    for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
                        if(mtp.getMolecularType() != mt){
                            continue;
                        }
                        boolean found = false;
                        for(MolecularComponentPattern mcp : mtp.getComponentPatternList()){
                            if(mcp.getMolecularComponent() == mc){
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            MolecularComponentPattern mcp = new MolecularComponentPattern(mc);
                            mcp.setBondType(BondType.Possible);
                            mtp.getComponentPatternList().add(mcp);
                            rl.firePropertyChange(PROPERTY_NAME_SPECIES_CONTEXTS, rl, rl);
                        }
                    }
                }

                List<ProductPattern> ppList = rl.getProductPatterns();
                for(ProductPattern pp : ppList){
                    SpeciesPattern sp = pp.getSpeciesPattern();
                    for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()){
                        if(mtp.getMolecularType() != mt){
                            continue;
                        }
                        boolean found = false;
                        for(MolecularComponentPattern mcp : mtp.getComponentPatternList()){
                            if(mcp.getMolecularComponent() == mc){
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            MolecularComponentPattern mcp = new MolecularComponentPattern(mc);
                            mcp.setBondType(BondType.Possible);
                            mtp.getComponentPatternList().add(mcp);
                            rl.firePropertyChange(PROPERTY_NAME_SPECIES_CONTEXTS, rl, rl);
                        }
                    }
                }
            }
        }
    }

    public Model(Version argVersion){
        this(argVersion, ModelUnitSystem.createDefaultVCModelUnitSystem());
    }

    public Model(Version argVersion, ModelUnitSystem argUnitSystem){
        this.version = argVersion;
        if(argVersion != null){
            fieldName = argVersion.getName();
            fieldDescription = argVersion.getAnnot();
        }
        addPropertyChangeListener(this);
        addVetoableChangeListener(this);

        // initialize a unit system
        this.unitSystem = argUnitSystem;

        // initialize the reserved symbols
        fieldReservedSymbols = createReservedSymbols();

        try {
            this.fieldModelFunctions = new Model.ModelFunction[]{
                    new Model.ModelFunction("sum2", new String[]{"a", "b"}, new Expression("a+b"), unitSystem.getInstance_DIMENSIONLESS()),
                    new Model.ModelFunction("sum3", new String[]{"a", "b", "c"}, new Expression("a+b+c"), unitSystem.getInstance_DIMENSIONLESS())
            };
        } catch(ExpressionException e){
            lg.error(e);
        }
    }

    public Model(String argName){
        this(argName, ModelUnitSystem.createDefaultVCModelUnitSystem());
    }


    public Model(String argName, ModelUnitSystem argModelUnitSystem){
        this.fieldName = argName;
        this.version = null;
        addPropertyChangeListener(this);
        addVetoableChangeListener(this);

        // initialize a unit system
        this.unitSystem = argModelUnitSystem;

        // initialize the reserved symbols
        fieldReservedSymbols = createReservedSymbols();

        try {
            this.fieldModelFunctions = new Model.ModelFunction[]{
                    new Model.ModelFunction("sum2", new String[]{"a", "b"}, new Expression("a+b"), unitSystem.getInstance_DIMENSIONLESS()),
                    new Model.ModelFunction("sum3", new String[]{"a", "b", "c"}, new Expression("a+b+c"), unitSystem.getInstance_DIMENSIONLESS())
            };
        } catch(ExpressionException e){
            lg.error(e);
        }
    }

    private final static Map<ReservedSymbolRole, Double> reservedConstantsMap = new HashMap<>() {
        {
            put(ReservedSymbolRole.PI_CONSTANT, Math.PI);
            put(ReservedSymbolRole.FARADAY_CONSTANT, 9.64853321e4);            // exactly 96485.3321233100184 C/mol
            put(ReservedSymbolRole.FARADAY_CONSTANT_NMOLE, 9.64853321e-5);    // was 9.648
            put(ReservedSymbolRole.N_PMOLE, 6.02214179e11);
            put(ReservedSymbolRole.K_GHK, 1e-9);
            put(ReservedSymbolRole.GAS_CONSTANT, 8314.46261815);            // exactly 8314.46261815324  (was 8314.0)
            put(ReservedSymbolRole.KMILLIVOLTS, 1000.0);
            put(ReservedSymbolRole.KMOLE, 1.0 / 602.214179);
//		put(ReservedSymbolRole.NaN, 0.0);
        }
    };

    private ReservedSymbol[] createReservedSymbols(){
        return new ReservedSymbol[]{
                new ReservedSymbol(ReservedSymbolRole.TIME, "t", "time", unitSystem.getTimeUnit(), null),
                new ReservedSymbol(ReservedSymbolRole.X, "x", "x coord", unitSystem.getLengthUnit(), null),
                new ReservedSymbol(ReservedSymbolRole.Y, "y", "y coord", unitSystem.getLengthUnit(), null),
                new ReservedSymbol(ReservedSymbolRole.Z, "z", "z coord", unitSystem.getLengthUnit(), null),
                new ReservedSymbol(ReservedSymbolRole.TEMPERATURE, "_T_", "temperature", unitSystem.getTemperatureUnit(), null),

                new ReservedSymbol(ReservedSymbolRole.PI_CONSTANT, "_PI_", "PI constant", unitSystem.getInstance_DIMENSIONLESS(), new Expression(reservedConstantsMap.get(ReservedSymbolRole.PI_CONSTANT))),
                new ReservedSymbol(ReservedSymbolRole.FARADAY_CONSTANT, "_F_", "Faraday constant", unitSystem.getFaradayConstantUnit(), new Expression(reservedConstantsMap.get(ReservedSymbolRole.FARADAY_CONSTANT))),
                new ReservedSymbol(ReservedSymbolRole.FARADAY_CONSTANT_NMOLE, "_F_nmol_", "Faraday constant", unitSystem.getFaradayConstantNMoleUnit(), new Expression(reservedConstantsMap.get(ReservedSymbolRole.FARADAY_CONSTANT_NMOLE))),
                new ReservedSymbol(ReservedSymbolRole.N_PMOLE, "_N_pmol_", "Avagadro Num (scaled)", unitSystem.getN_PMoleUnit(), new Expression(reservedConstantsMap.get(ReservedSymbolRole.N_PMOLE))),
                new ReservedSymbol(ReservedSymbolRole.K_GHK, "_K_GHK_", "GHK unit scale", unitSystem.getK_GHKUnit(), new Expression(reservedConstantsMap.get(ReservedSymbolRole.K_GHK))),
                new ReservedSymbol(ReservedSymbolRole.GAS_CONSTANT, "_R_", "Gas Constant", unitSystem.getGasConstantUnit(), new Expression(reservedConstantsMap.get(ReservedSymbolRole.GAS_CONSTANT))),
                new ReservedSymbol(ReservedSymbolRole.KMILLIVOLTS, "K_millivolts_per_volt", "voltage scale", unitSystem.getK_mV_perV_Unit(), new Expression(reservedConstantsMap.get(ReservedSymbolRole.KMILLIVOLTS))),
                new ReservedSymbol(ReservedSymbolRole.KMOLE, "KMOLE", "Flux unit conversion", unitSystem.getKMoleUnit(), new Expression(reservedConstantsMap.get(ReservedSymbolRole.KMOLE)))
//		new ReservedSymbol(ReservedSymbolRole.NaN, "_NaN_", "Not a number", unitSystem.getInstance_DIMENSIONLESS(), new Expression(reservedConstantsMap.get(ReservedSymbolRole.NaN)))
        };
    }


//public ModelParameter createModelParameter(String name, Expression expr, int role, VCUnitDefinition units) {
//	ModelParameter modelParameter = new ModelParameter(name, expr, role, units);
//	return modelParameter;
//}

    public ModelParameter addModelParameter(Model.ModelParameter modelParameter) throws PropertyVetoException{
//	if (!contains(modelParameter)){
        Model.ModelParameter[] newModelParameters = ArrayUtils.addElement(modelParameters, modelParameter);
        setModelParameters(newModelParameters);
//	}
        return modelParameter;
    }

    public ModelFunction addModelFunction(Model.ModelFunction modelFunction) throws PropertyVetoException{
//	if (!contains(modelParameter)){
        Model.ModelFunction[] newModelFunctions = ArrayUtils.addElement(fieldModelFunctions, modelFunction);
        setModelFunctions(newModelFunctions);
//	}
        return modelFunction;
    }


    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener){
        getPropertyChange().addPropertyChangeListener(listener);
    }


    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
        getPropertyChange().addPropertyChangeListener(propertyName, listener);
    }

    public Feature addFeature(String featureName) throws ModelException, PropertyVetoException{
        Structure structure = getStructure(featureName);
        if(structure != null){
            throw new ModelException("adding feature '" + featureName + "', structure already exists with that name");
        }
        Feature newFeature = new Feature(featureName);
        Structure[] newStructures = ArrayUtils.addElement(structures, newFeature);
        setStructures(newStructures);
        return newFeature;
    }

    public Membrane addMembrane(String membraneName) throws ModelException, PropertyVetoException{
        Structure structure = getStructure(membraneName);
        if(structure != null){
            throw new ModelException("adding membrane '" + membraneName + "', structure already exists with that name");
        }
        Membrane newMembrane = new Membrane(membraneName);
        Structure[] newStructures = ArrayUtils.addElement(structures, newMembrane);
        setStructures(newStructures);
        return newMembrane;
    }


    public ReactionStep addReactionStep(ReactionStep reactionStep) throws PropertyVetoException{
        if(!contains(reactionStep)){
            setReactionSteps(ArrayUtils.addElement(reactionSteps, reactionStep));
        }
        return reactionStep;
    }


    public Species addSpecies(Species species) throws PropertyVetoException{
        if(!contains(species)){
            Species[] newSpecies = ArrayUtils.addElement(this.species, species);
            setSpecies(newSpecies);
        }
        return species;
    }


    /**
     * This method was created by a SmartGuide.
     *
     * @param species cbit.vcell.model.Species
     */
    public SpeciesContext addSpeciesContext(Species species, Structure structure) throws Exception{
        if(species != getSpecies(species.getCommonName())){
            throw new Exception("species " + species.getCommonName() + " not found in model");
        }
        SpeciesContext speciesContext = getSpeciesContext(species, structure);
        if(speciesContext != null){
            throw new Exception("speciesContext for " + species.getCommonName() + " within " + structure.getName() + " already defined");
        }

        speciesContext = new SpeciesContext(species, structure);
        speciesContext.setModel(this);
        return addSpeciesContext(speciesContext);
    }

    public SpeciesContext addSpeciesContext(SpeciesContext speciesContext) throws PropertyVetoException{

        if(!contains(speciesContext.getSpecies())){
            throw new RuntimeException("species " + speciesContext.getSpecies().getCommonName() + " not found in model");
        }
        //  JMW and JCS added 26 June 2002: need to also check for structures
        if(!contains(speciesContext.getStructure())){
            throw new RuntimeException("structure " + speciesContext.getStructure().getName() + " not found in model");
        }
        if(getSpeciesContext(speciesContext.getSpecies(), speciesContext.getStructure()) != null){
            throw new RuntimeException("speciesContext for " + speciesContext.getSpecies().getCommonName() + " within " + speciesContext.getStructure().getName() + " already defined");
        }
        if(!contains(speciesContext)){
            SpeciesContext[] newArray = ArrayUtils.addElement(speciesContexts, speciesContext);
            speciesContext.setModel(this);
            setSpeciesContexts(newArray);
        }
        return speciesContext;
    }


    /**
     * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener){
        getVetoPropertyChange().addVetoableChangeListener(listener);
    }


    /**
     * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener){
        getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
    }


    /**
     * Insert the method's description here.
     * Creation date: (4/24/2003 3:32:45 PM)
     */
    public void clearVersion(){
        version = null;
    }


    @Override
    public boolean compareEqual(Matchable object){
        return object instanceof Model model
                && Compare.isEqual(getName(), model.getName())
                && Compare.isEqual(getDescription(), model.getDescription())
                && Compare.isEqual(speciesContexts, model.speciesContexts)
                && Compare.isEqual(species, model.species)
                && Compare.isEqual(structures, model.structures)
                && Compare.isEqual(reactionSteps, model.reactionSteps)
                && Compare.isEqualStrict(diagrams, model.diagrams)
                && Compare.isEqual(modelParameters, model.modelParameters)
                && Compare.isEqual(unitSystem, model.unitSystem)
                && Compare.isEqual(structureTopology, model.structureTopology)
                && Compare.isEqual(electricalTopology, model.electricalTopology)
                && Compare.isEqual(rbmModelContainer, model.rbmModelContainer);
    }


    @Override
    public boolean relate(Relatable object, RelationVisitor rv){
        return object instanceof Model model
                && rv.relate(getName(), model.getName())
                && rv.relate(getDescription(), model.getDescription())
                && rv.relate(speciesContexts, model.speciesContexts)
                && rv.relate(species, model.species)
                && rv.relate(structures, model.structures)
                && rv.relate(reactionSteps, model.reactionSteps)
                && rv.relateStrict(diagrams, model.diagrams)
                && rv.relate(modelParameters, model.modelParameters)
                && rv.relate(unitSystem, model.unitSystem)
                && rv.relate(structureTopology, model.structureTopology)
                && rv.relate(electricalTopology, model.electricalTopology)
                && rv.relate(rbmModelContainer, model.rbmModelContainer);
    }


    public boolean contains(Diagram diagram){
        for (Diagram value : diagrams) {
            if (value == diagram) return true;
        }
        return false;
    }


    public boolean contains(ModelParameter modelParameter){
        for (ModelParameter parameter : modelParameters) {
            if (parameter == modelParameter) return true;
        }
        return false;
    }

    public boolean contains(ReactionStep reactionStep){
        for (ReactionStep step : reactionSteps) {
            if (step == reactionStep) return true;
        }
        return false;
    }


    public boolean contains(Species species){
        for (Species value : this.species) {
            if (value == species) return true;
        }
        return false;
    }


    public boolean contains(SpeciesContext speciesContext){
        for (SpeciesContext context : speciesContexts) {
            if (context == speciesContext) return true;
        }
        return false;
    }


    public boolean contains(Structure structure){
        for (Structure value : structures) {
            if (value == structure)
                return true;
        }
        return false;
    }


    public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException{
        getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
    }

    public void gatherIssues(IssueContext issueContext, List<Issue> issueList){
        issueContext = issueContext.newChildContext(ContextType.Model, this);
        //
        // check for unknown units (TBD) and unit consistency
        //
        try {
            for(ModelParameter modelParameter : modelParameters){
                Expression exp = modelParameter.getExpression();
                String[] symbols = exp.getSymbols();
                if(symbols == null) continue;
                String issueMsgPrefix = "Global parameter '" + modelParameter.getName() + "' ";
                for (String symbol : symbols) {
                    SymbolTableEntry ste = exp.getSymbolBinding(symbol);
                    if (ste == null) {
                        issueList.add(new Issue(modelParameter, issueContext, IssueCategory.ModelParameterExpressionError, issueMsgPrefix + "references undefined symbol '" + symbol + "'", Issue.SEVERITY_ERROR));
                    } else if (ste instanceof SpeciesContext sc && !this.contains(sc)) {
                        issueList.add(new Issue(modelParameter, issueContext, IssueCategory.ModelParameterExpressionError, issueMsgPrefix + "references undefined species '" + symbol + "'", Issue.SEVERITY_ERROR));
                    } else if (ste instanceof ModelParameter mp && !this.contains(mp)) {
                        issueList.add(new Issue(modelParameter, issueContext, IssueCategory.ModelParameterExpressionError, issueMsgPrefix + "references undefined global parameter '" + symbol + "'", Issue.SEVERITY_ERROR));
                    }
                }
            }
            //
            // determine unit consistency for each expression
            //
            for (ModelParameter modelParameter : modelParameters) {
                try {
                    VCUnitEvaluator unitEvaluator = new VCUnitEvaluator(getUnitSystem());
                    VCUnitDefinition paramUnitDef = modelParameter.getUnitDefinition();
                    VCUnitDefinition expUnitDef = unitEvaluator.getUnitDefinition(modelParameter.getExpression());
                    if (paramUnitDef == null) {
                        issueList.add(new Issue(modelParameter, issueContext, IssueCategory.Units, "defined unit is null", Issue.SEVERITY_WARNING));
                    } else if (expUnitDef == null) {
                        issueList.add(new Issue(modelParameter, issueContext, IssueCategory.Units, "computed unit is null", Issue.SEVERITY_WARNING));
                    } else if (paramUnitDef.isTBD()) {
                        issueList.add(new Issue(modelParameter, issueContext, IssueCategory.Units, "unit is undefined (" + unitSystem.getInstance_TBD().getSymbol() + ")", Issue.SEVERITY_WARNING));
                    } else if (!paramUnitDef.isEquivalent(expUnitDef) && !expUnitDef.isTBD()) {
                        issueList.add(new Issue(modelParameter, issueContext, IssueCategory.Units, "unit mismatch, computed = [" + expUnitDef.getSymbol() + "]", Issue.SEVERITY_WARNING));
                    }
                } catch (VCUnitException | ExpressionException e) {
                    issueList.add(new Issue(modelParameter, issueContext, IssueCategory.Units, "units inconsistent: " + e.getMessage(), Issue.SEVERITY_WARNING));
                }
            }
        } catch(Throwable e){
            lg.error(e);
            issueList.add(new Issue(this, issueContext, IssueCategory.Units, "unexpected exception: " + e.getMessage(), Issue.SEVERITY_WARNING));
        }

        //
        // get issues for all Structures
        //
        for(Structure struct : structures){
            struct.gatherIssues(issueContext, issueList);
        }

        //
        // get issues from all ReactionSteps
        //
        for (ReactionStep reactionStep : reactionSteps) {
            reactionStep.gatherIssues(issueContext, issueList);
        }
        //
        // get issues from species contexts (species patterns)
        //
        int seedSpeciesCount = 0;
        for (SpeciesContext speciesContext : speciesContexts) {
            if (speciesContext.hasSpeciesPattern()) {
                seedSpeciesCount++;
            }
            speciesContext.gatherIssues(issueContext, issueList);
        }
        if(seedSpeciesCount == 0 && !rbmModelContainer.getReactionRuleList().isEmpty()){
            String msg = "At least one seed species is required.";
            issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.WARNING));
        }

        //
        // get issues for symbol name clashes (specifically structures with same voltage names or structure names)
        //
        HashSet<SymbolTableEntry> steHashSet = new HashSet<>();
        gatherLocalEntries(steHashSet);
        Iterator<SymbolTableEntry> iter = steHashSet.iterator();
        Hashtable<String, SymbolTableEntry> symbolHashtable = new Hashtable<>();
        while (iter.hasNext()) {
            SymbolTableEntry ste = iter.next();
            SymbolTableEntry existingSTE = symbolHashtable.get(ste.getName());
            if(existingSTE != null){
                issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "model symbol \"" + ste.getName() + "\" is used within \"" + ste.getNameScope().getName() + "\" and \"" + existingSTE.getNameScope().getName() + "\"", Issue.SEVERITY_ERROR));
            } else {
                symbolHashtable.put(ste.getName(), ste);
            }
        }

        //
        // gather issues for electrical topology (unspecified +ve or -ve features, or +ve feature == -ve feature
        //
        getElectricalTopology().gatherIssues(issueContext, issueList);

        //
        // gather issues for the Rbm Model
        //
        if(rbmModelContainer == null){
            issueList.add(new Issue(this, issueContext, IssueCategory.InternalError, "Rbm Model Container is null", Issue.SEVERITY_WARNING));
        } else {
            rbmModelContainer.gatherIssues(issueContext, issueList);
        }
    }


    /**
     * Gets the description property (java.lang.String) value.
     *
     * @return The description property value.
     * @see #setDescription
     */
    public java.lang.String getDescription(){
        return fieldDescription;
    }


    /**
     * This method was created in VisualAge.
     *
     * @param structure cbit.vcell.model.Structure
     * @return cbit.vcell.model.Diagram
     */
    public Diagram getDiagram(Structure structure) throws RuntimeException{
        for (Diagram diagram : diagrams) {
            if (diagram.getStructure() == structure) {
                return diagram;
            }
        }
        if(getStructure(structure.getName()) == null){
            throw new RuntimeException("structure " + structure.getName() + " not present in model");
        }
        return null;
    }


    /**
     * Gets the diagrams property (cbit.vcell.model.Diagram[]) value.
     *
     * @return The diagrams property value.
     * @see #setDiagrams
     */
    public Diagram[] getDiagrams(){
        return diagrams;
    }


    /**
     * Gets the diagrams index property (cbit.vcell.model.Diagram) value.
     *
     * @param index The index value into the property array.
     * @return The diagrams property value.
     * @see #setDiagrams
     */
    public Diagram getDiagrams(int index){
        return getDiagrams()[index];
    }


    /**
     * getEntry method comment.
     */
    public SymbolTableEntry getEntry(java.lang.String identifierString){

        SymbolTableEntry ste = getLocalEntry(identifierString);
        if(ste != null){
            return ste;
        }
        return getNameScope().getExternalEntry(identifierString, this);
    }

    public ReservedSymbol[] getReservedSymbols(){
        return fieldReservedSymbols;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return java.lang.String
     */
    public Feature createFeature(){
        String featureName;
        for (int count = 0;; count++){
            featureName = "c" + count;
            if(getStructure(featureName) == null) break;
        }

        try {
            return addFeature(featureName);
        } catch(ModelException | PropertyVetoException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public SimpleReaction createSimpleReaction(Structure structure){
        String reactionStepName = getReactionName();
        try {
            SimpleReaction simpleReaction = new SimpleReaction(this, structure, reactionStepName, true);
            addReactionStep(simpleReaction);
            return simpleReaction;
        } catch(PropertyVetoException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public boolean reactionNameNotInUse(String candidateName){
        return (getReactionStep(candidateName) == null) && (getRbmModelContainer().getReactionRule(candidateName) == null);
    }

    public String getReactionName(String prefix, String suffix){
        String candidate = prefix + "_" + suffix;
        if(reactionNameNotInUse(candidate)){
            return candidate;
        }
        String reactionName;
        for (int count = 0;;count++){
            reactionName = prefix + count + "_" + suffix;
            if((getReactionStep(reactionName) == null) && (getRbmModelContainer().getReactionRule(reactionName) == null)){
                break;
            }
        }
        return reactionName;
    }

    public String getReactionName(String candidate){        // used only in the rulebased transformer
        if(reactionNameNotInUse(candidate) && getSpeciesContext(candidate) == null){
            return candidate;
        }
        String reactionName;
        for (int count = 0;;count++){
            reactionName = candidate + count;
            if((getReactionStep(reactionName) != null) || (getRbmModelContainer().getReactionRule(reactionName) != null)) continue;
            if(getSpeciesContext(reactionName) == null) return reactionName;
        }
    }

    public String getReactionName(){
        String reactionName;
        for (int count = 0;; count++){
            reactionName = "r" + count;
            if((getReactionStep(reactionName) == null) && (getRbmModelContainer().getReactionRule(reactionName) == null)){
                return reactionName;
            }
        }
    }

    public FluxReaction createFluxReaction(Membrane membrane){

        String reactionStepName;
        for (int count = 0;; count++){
            reactionStepName = "flux" + count;
            if(getReactionStep(reactionStepName) == null) break;
        }

        try {
            FluxReaction fluxReaction = new FluxReaction(this, membrane, null, reactionStepName, true);
            addReactionStep(fluxReaction);
            return fluxReaction;
        } catch(PropertyVetoException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean isNameUnused(String name, Model model){
        return model.getSpecies(name) == null && model.getSpeciesContext(name) == null && model.getModelParameter(name) == null && model.getReactionStep(name) == null && model.getRbmModelContainer().getObservable(name) == null && model.getRbmModelContainer().getReactionRule(name) == null;
    }

    public SpeciesContext createSpeciesContext(Structure structure){
        return createSpeciesContext(structure, null);
    }

    public SpeciesContext createSpeciesContext(Structure structure, SpeciesPattern speciesPattern){

        String nameRoot = "s";
        String speciesName;
        if(speciesPattern != null){    // for seed species we generate a name from the species pattern
            nameRoot = speciesPattern.toString();
            nameRoot = nameRoot.replaceAll("[!?~]+", "");
            nameRoot = TokenMangler.fixTokenStrict(nameRoot);
            while (nameRoot.endsWith("_")) { // clean all the '_' at the end, if any
                nameRoot = nameRoot.substring(0, nameRoot.length() - 1);
            }
            speciesName = nameRoot;
            if(!Model.isNameUnused(speciesName, this)){
                nameRoot += "_";
                for (int count = 0;; count++){
                    speciesName = nameRoot + count;
                    if(Model.isNameUnused(speciesName, this)){
                        break;
                    }
                }
            }
        } else {            // for plain species it works as before
            for (int count = 0;; count++){
                speciesName = nameRoot + count;
                if(Model.isNameUnused(speciesName, this)){
                    break;
                }
            }
        }
//	System.out.println(speciesName);
        try {
            SpeciesContext speciesContext = new SpeciesContext(new Species(speciesName, null), structure, speciesPattern);
            speciesContext.setName(speciesName);
            addSpecies(speciesContext.getSpecies());
            addSpeciesContext(speciesContext);
            return speciesContext;
        } catch(PropertyVetoException ex){
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public ModelParameter createModelParameter(){
        String globalParamName;
        for(int count = 0;; count++){
            globalParamName = "g" + count;
            if(getModelParameter(globalParamName) == null){
                break;
            }
        }
        try {
            ModelParameter modelParameter = new ModelParameter(globalParamName, new Expression(0), Model.ROLE_UserDefined, unitSystem.getInstance_TBD());
            addModelParameter(modelParameter);
            return modelParameter;
        } catch(PropertyVetoException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @return cbit.sql.KeyValue
     */
    public KeyValue getKey(){
        return (getVersion() != null) ? (getVersion().getVersionKey()) : null;
    }


    public Kinetics.KineticsParameter getKineticsParameter(String kineticsParameterName){
        for (ReactionStep reactionStep : reactionSteps) {
            KineticsParameter parm = reactionStep.getKinetics().getKineticsParameter(kineticsParameterName);
            if (parm != null) {
                return parm;
            }
        }
        return null;
    }


    public SymbolTableEntry getLocalEntry(java.lang.String identifier){

        for(ReservedSymbol rs : fieldReservedSymbols){
            if(identifier.equals(rs.getName())){
                return rs;
            }
        }

        // look through the global/model parameters
        for (ModelParameter modelParameter : modelParameters) {
            if (modelParameter.getName().equals(identifier)) {
                return modelParameter;
            }
        }

        // look through the global/model functions
        for (ModelFunction fieldModelFunction : fieldModelFunctions) {
            if (fieldModelFunction.getName().equals(identifier)) {
                return fieldModelFunction;
            }
        }

        //
        // get Voltages from structures
        //
        for (Structure structure : structures) {
            if (structure instanceof Membrane) {
                MembraneVoltage membraneVoltage = ((Membrane) structure).getMembraneVoltage();
                if (membraneVoltage.getName().equals(identifier)) {
                    return membraneVoltage;
                }
            }
        }

        //
        // get Sizes from structures
        //
        for (Structure structure : structures) {
            StructureSize structureSize = structure.getStructureSize();
            if (structureSize.getName().equals(identifier)) {
                return structureSize;
            }
        }

        //
        // get Observable from Rulebased Model Container
        //
        RbmObservable observable = rbmModelContainer.getObservable(identifier);
        if(observable != null){
            return observable;
        }

        return getSpeciesContext(identifier);
    }

    public void gatherLocalEntries(Set<SymbolTableEntry> symbolTableEntries){

        Collections.addAll(symbolTableEntries, fieldReservedSymbols);

        Collections.addAll(symbolTableEntries, modelParameters);

        for (Structure structure : structures) {
            symbolTableEntries.add(structure.getStructureSize());
            if (structure instanceof Membrane membrane) {
                symbolTableEntries.add(membrane.getMembraneVoltage());
            }
        }

        Collections.addAll(symbolTableEntries, speciesContexts);
    }


    /**
     * Gets the modelParameters property (cbit.vcell.model.ModelParameter[]) value.
     *
     * @return The modelParameters property value.
     * @see #setModelParameters
     */
    public Model.ModelParameter[] getModelParameters(){
        return modelParameters;
    }


    /**
     * Gets the modelParameters index property (cbit.vcell.model.ModelParameter) value.
     *
     * @param index The index value into the property array.
     * @return The modelParameters property value.
     * @see #setModelParameters
     */
    public ModelParameter getModelParameters(int index){
        return getModelParameters()[index];
    }


    /**
     * Gets the name property (java.lang.String) value.
     *
     * @return The name property value.
     * @see #setName
     */
    public java.lang.String getName(){
        return fieldName;
    }


    /**
     * Insert the method's description here.
     * Creation date: (8/27/2003 10:03:05 PM)
     *
     * @return cbit.vcell.parser.NameScope
     */
    public NameScope getNameScope(){
        return nameScope;
    }


    /**
     * This method was created by a SmartGuide.
     *
     * @return int
     */
    public int getNumSpecies(){
        return species.length;
    }


    /**
     * This method was created by a SmartGuide.
     *
     * @return int
     */
    public int getNumSpeciesContexts(){
        return speciesContexts.length;
    }


    /**
     * This method was created by a SmartGuide.
     *
     * @return int
     */
    public int getNumStructures(){
        return structures.length;
    }

    public int getNumReactions(){
        return reactionSteps.length;
    }


    /**
     * Accessor for the propertyChange field.
     */
    protected PropertyChangeSupport getPropertyChange(){
        if(propertyChange == null){
            propertyChange = new java.beans.PropertyChangeSupport(this);
        }
        return propertyChange;
    }


    /**
     * This method was created in VisualAge.
     *
     * @param reactionStepName java.lang.String
     * @return cbit.vcell.model.ReactionStep
     */
    public ReactionStep getReactionStep(String reactionStepName){
        if(reactionStepName == null){
            return null;
        }
        for (ReactionStep reactionStep : reactionSteps) {
            if (reactionStep.getName().equals(reactionStepName)) {
                return reactionStep;
            }
        }
        return null;
    }

    public int getNumModelProcesses(){
        return getNumReactions() + getRbmModelContainer().getReactionRuleList().size();
    }

    public ModelProcess[] getModelProcesses(){
        ArrayList<ModelProcess> processes = new ArrayList<>();
        processes.addAll(Arrays.asList(reactionSteps));
        processes.addAll(rbmModelContainer.getReactionRuleList());
        return processes.toArray(new ModelProcess[0]);
    }

    /**
     * Gets the reactionSteps property (cbit.vcell.model.ReactionStep[]) value.
     *
     * @return The reactionSteps property value.
     * @see #setReactionSteps
     */
    public ReactionStep[] getReactionSteps(){
        return reactionSteps;
    }


    /**
     * Gets the reactionSteps index property (cbit.vcell.model.ReactionStep) value.
     *
     * @param index The index value into the property array.
     * @return The reactionSteps property value.
     * @see #setReactionSteps
     */
    public ReactionStep getReactionSteps(int index){
        return getReactionSteps()[index];
    }


    /**
     * Gets the species property (cbit.vcell.model.Species[]) value.
     *
     * @return The species property value.
     * @see #setSpecies
     */
    public Species[] getSpecies(){
        return species;
    }


    public Species getSpecies(int index){
        return getSpecies()[index];
    }


    public Species[] getSpecies(DBSpecies dbSpecies){
        if(dbSpecies == null) throw new IllegalArgumentException("DBSpecies was null");

        Vector<Species> speciesList = new Vector<>();
        for(Species fieldSpecies : species){
            if(fieldSpecies.getDBSpecies() != null && fieldSpecies.getDBSpecies().compareEqual(dbSpecies)){
                speciesList.add(fieldSpecies);
            }
        }
        return speciesList.toArray(Species[]::new);
    }


    public Species getSpecies(String speciesName){
        if(speciesName == null){
            return null;
        }
        for(Species fieldSpecy : species){
            if(speciesName.equals(fieldSpecy.getCommonName())){
                return fieldSpecy;
            }
        }
        return null;
    }

    public ModelParameter getModelParameter(String glParamName){
        if(glParamName == null){
            return null;
        }
        for (ModelParameter modelParameter : modelParameters) {
            if (glParamName.equals(modelParameter.getName())) {
                return modelParameter;
            }
        }
        return null;
    }

    /**
     * This method was created by a SmartGuide.
     *
     * @param species cbit.vcell.model.Species
     * @return cbit.vcell.model.SpeciesContext
     */
    public SpeciesContext getSpeciesContext(Species species, Structure structure){
        if(!contains(species)){
            throw new RuntimeException("Species '" + species.getCommonName() + "' not found in model; " +
                    "Could not retrieve speciesContext '" + species.getCommonName() + "_" + structure.getName() + "'.");
        }
        if(!contains(structure)){
            throw new RuntimeException("Structure '" + structure.getName() + "' not found in model; " +
                    "Could not retrieve speciesContext '" + species.getCommonName() + "_" + structure.getName() + "'.");
        }
        for (SpeciesContext speciesContext : speciesContexts) {
            if ((speciesContext.getSpecies() == species) &&
                    (speciesContext.getStructure() == structure)) {
                return speciesContext;
            }
        }
        return null;
    }


    public SpeciesContext getSpeciesContext(String speciesContextName){
        for (SpeciesContext speciesContext : speciesContexts) {
            if (speciesContext.getName().equals(speciesContextName)) {
                return speciesContext;
            }
        }
        return null;
    }


    public SpeciesContext[] getSpeciesContexts(){
        return speciesContexts;
    }


    public SpeciesContext getSpeciesContexts(int index){
        return getSpeciesContexts()[index];
    }


    public SpeciesContext[] getSpeciesContexts(Structure structure){
        Vector<SpeciesContext> scList = new Vector<>();

        for (SpeciesContext speciesContext : speciesContexts) {
            if (speciesContext.getStructure().equals(structure)) {
                scList.addElement(speciesContext);
            }
        }

        SpeciesContext[] scArray = new SpeciesContext[scList.size()];
        scList.copyInto(scArray);
        return scArray;
    }

    public SpeciesContext getSpeciesContextByPattern(SpeciesPattern sp){
        for(SpeciesContext sc : speciesContexts){
            if(!sc.hasSpeciesPattern()){
                continue;
            }
            if(sc.getSpeciesPattern().compareEqual(sp)){
                return sc;
            }
        }
        return null;
    }

    public SpeciesContext getSpeciesContextByPattern(String speciesPattern){
        for(SpeciesContext sc : speciesContexts){
            if(!sc.hasSpeciesPattern()){
                continue;
            }
            if(sc.getSpeciesPattern().toString().equals(speciesPattern)){
                return sc;
            }
        }
        return null;
    }


    public SpeciesContext[] getSpeciesContextsNeededByMovingMembrane(Membrane movingMembrane){

        //Find any species that are needed by reactions in the membrane of movingFeature
        // Feature outsideFeature = (Feature)movingMembrane.getParentStructure();
        Feature outsideFeature = (Feature) structureTopology.getParentStructure(movingMembrane);
        SpeciesContext[] outSC = getSpeciesContexts(outsideFeature);
        Vector<SpeciesContext> neededSC = new Vector<>();
        for (ReactionStep reactionStep : reactionSteps) {
            if (reactionStep.getStructure() != movingMembrane) continue;
            for (SpeciesContext speciesContext : outSC) {
                if (reactionStep.countNumReactionParticipants(speciesContext) == 0) continue;
                if (!neededSC.contains(speciesContext)) {
                    neededSC.add(speciesContext);
                }
            }
        }

        if(!neededSC.isEmpty()){
            SpeciesContext[] scArr = new SpeciesContext[neededSC.size()];
            neededSC.copyInto(scArr);
            return scArr;
        }

        return null;
    }


    public String[] getSpeciesNames(){
        Vector<String> nameList = new Vector<>();
        for (Species value : species) {
            nameList.add(value.getCommonName());
        }
        String[] names = new String[nameList.size()];
        nameList.copyInto(names);
        return names;
    }

    public ElectricalTopology getElectricalTopology(){
        return electricalTopology;
    }

    public Structure getStructure(String structureName){
        if(structureName == null){
            return null;
        }
        for (Structure structure : structures) {
            if (structureName.equals(structure.getName())) {
                return structure;
            }
        }
        return null;
    }


    /**
     * Gets the structures property (cbit.vcell.model.Structure[]) value.
     *
     * @return The structures property value.
     * @see #setStructures
     */
    public Structure[] getStructures(){
        return structures;
    }


    /**
     * Gets the structures index property (cbit.vcell.model.Structure) value.
     *
     * @param index The index value into the property array.
     * @return The structures property value.
     * @see #setStructures
     */
    public Structure getStructure(int index){
        return getStructures()[index];
    }

    public StructureTopology getStructureTopology(){
        return structureTopology;
    }

//wei's code

    /**
     * @return non-null list of membranes, may be empty
     */
    public ArrayList<Membrane> getMembranes(){
        ArrayList<Membrane> membranes = new ArrayList<>();
        for (Structure structure : structures) {
            if (structure instanceof Membrane) {
                membranes.add((Membrane) structure);

            }
        }
        return membranes;
    }
// done


    /**
     * This method was created in VisualAge.
     *
     * @return Version
     */
    public Version getVersion(){
        return version;
    }


    /**
     * Accessor for the vetoPropertyChange field.
     */
    protected java.beans.VetoableChangeSupport getVetoPropertyChange(){
        if(vetoPropertyChange == null){
            vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
        }
        return vetoPropertyChange;
    }


    /**
     * The hasListeners method was generated to support the propertyChange field.
     */
    public synchronized boolean hasListeners(String propertyName){
        return getPropertyChange().hasListeners(propertyName);
    }


    /**
     * This method was created in VisualAge.
     *
     * @param speciesContext cbit.vcell.model.SpeciesContext
     * @return boolean
     */
    public boolean isUsed(SpeciesContext speciesContext){
        for (ReactionStep reactionStep : reactionSteps) {
            if (reactionStep.countNumReactionParticipants(speciesContext) > 0) {
                return true;
            }
        }
        return false;
    }


    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt){
        if(evt.getSource() instanceof ReactionStep && evt.getPropertyName().equals("kinetics")){
            Kinetics oldKinetics = (Kinetics) evt.getOldValue();
            Kinetics newKinetics = (Kinetics) evt.getNewValue();
            if(oldKinetics != null){
                oldKinetics.removePropertyChangeListener(this);
                oldKinetics.removeVetoableChangeListener(this);
            }
            if(newKinetics != null){
                newKinetics.addPropertyChangeListener(this);
                newKinetics.addVetoableChangeListener(this);
            }
        }
        if(evt.getSource() instanceof SpeciesContext && evt.getPropertyName().equals("name")){
            for (Diagram diagram : diagrams) {
                diagram.renameNode((String) evt.getOldValue(), (String) evt.getNewValue());
            }
        }
        if(evt.getSource() instanceof ReactionStep && evt.getPropertyName().equals("name")){
            for (Diagram diagram : diagrams) {
                diagram.renameNode((String) evt.getOldValue(), (String) evt.getNewValue());
            }
        }

        // if we rename a molecular type, we try to find rename the observable which was automatically created with the molecular type
        // if we can't find any candidate or we find too many candidates we wisely (and silently) do nothing
        if(evt.getSource() instanceof MolecularType && evt.getPropertyName().equals("name")){
            List<RbmObservable> candidates = new ArrayList<>();
            String oldName = (String) evt.getOldValue();
            String newName = (String) evt.getNewValue();
            for(RbmObservable candidate : getRbmModelContainer().getObservableList()){
                if(candidate.getName().contains(oldName) && candidate.getName().startsWith("O") && candidate.getName().endsWith("_tot")){
                    candidates.add(candidate);
                }
            }
            if(candidates.isEmpty()){
                System.out.println("no candidates to rename");
            } else if(candidates.size() > 1){
                System.out.println("too many candidates to rename");
            } else {
                RbmObservable candidate = candidates.get(0);
                lg.info("renaming --- " + candidate.getName());
                try {
                    String prefix = candidate.getName().substring(0, candidate.getName().indexOf("_"));
                    candidate.setName(prefix + "_" + newName + "_tot");
                } catch(PropertyVetoException e){
                    lg.error("Cannot rename observable " + candidate.getName() + ", " + e.getMessage(), e);
                }
            }
        }

        //
        // if any symbolTableEntry within this model namescope changes (e.g. SpeciesContext, ModelParameter, etc...) then we have to update
        // the expressions in the ModelParameters (they might be referenced).
        //
        if((evt.getSource() instanceof ModelParameter ||
                evt.getSource() instanceof SpeciesContext ||
                evt.getSource() instanceof RbmObservable ||
                evt.getSource() instanceof MembraneVoltage ||
                evt.getSource() instanceof StructureSize)
                && evt.getPropertyName().equals("name")){
            for (ModelParameter modelParameter : modelParameters) {
                try {
                    Expression exp = modelParameter.getExpression();
                    Expression renamedExp = exp.renameBoundSymbols(getNameScope());
                    if (!renamedExp.compareEqual(exp)) {
                        modelParameter.setExpression(renamedExp);
                    }
                } catch (ExpressionBindingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }

        if((evt.getSource() instanceof ModelParameter ||
                evt.getSource() instanceof SpeciesContext ||
                evt.getSource() instanceof StructureSize)
                && evt.getPropertyName().equals("name")){
            firePropertyChange(PROPERTY_NAME_MODEL_ENTITY_NAME, evt.getOldValue(), evt.getNewValue());
        }

        if(evt.getSource() instanceof MolecularType && evt.getPropertyName().equals(MolecularType.PROPERTY_NAME_COMPONENT_LIST)){
            // redirected message that serves a very narrow role specifically for springsalad applications
            firePropertyChange(MolecularType.PROPERTY_NAME_COMPONENT_LIST, evt.getSource(), evt.getNewValue());
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/22/00 10:50:08 PM)
     */
    public void refreshDependencies(){

        removePropertyChangeListener(this);
        removeVetoableChangeListener(this);
        addVetoableChangeListener(this);
        addPropertyChangeListener(this);

        for (Structure structure : structures) {
            structure.removePropertyChangeListener(this);
            structure.removeVetoableChangeListener(this);
            structure.addPropertyChangeListener(this);
            structure.addVetoableChangeListener(this);
            structure.getStructureSize().removePropertyChangeListener(this);
            structure.getStructureSize().removeVetoableChangeListener(this);
            structure.getStructureSize().addPropertyChangeListener(this);
            structure.getStructureSize().addVetoableChangeListener(this);
            if (structure instanceof Membrane) {
                ((Membrane) structure).getMembraneVoltage().removePropertyChangeListener(this);
                ((Membrane) structure).getMembraneVoltage().removeVetoableChangeListener(this);
                ((Membrane) structure).getMembraneVoltage().addPropertyChangeListener(this);
                ((Membrane) structure).getMembraneVoltage().addVetoableChangeListener(this);
            }
            structure.setModel(this);
        }

        for (Species value : species) {
            value.removeVetoableChangeListener(this);
            value.addVetoableChangeListener(this);
            value.refreshDependencies();
        }

        for (SpeciesContext speciesContext : speciesContexts) {
            speciesContext.removePropertyChangeListener(this);
            speciesContext.removeVetoableChangeListener(this);
            speciesContext.addPropertyChangeListener(this);
            speciesContext.addVetoableChangeListener(this);
            speciesContext.setModel(this);
            speciesContext.refreshDependencies();
        }

        for (ReactionStep reactionStep : reactionSteps) {
            reactionStep.removePropertyChangeListener(this);
            reactionStep.removeVetoableChangeListener(this);
            reactionStep.getKinetics().removePropertyChangeListener(this);
            reactionStep.getKinetics().removeVetoableChangeListener(this);
            reactionStep.getKinetics().addPropertyChangeListener(this);
            reactionStep.getKinetics().addVetoableChangeListener(this);
            reactionStep.addPropertyChangeListener(this);
            reactionStep.addVetoableChangeListener(this);
            reactionStep.setModel(this);
            try {
                reactionStep.rebindAllToModel(this);
            } catch (Exception e) {
                lg.error(e.getMessage(), e);
            }
            reactionStep.refreshDependencies();
        }

        for (ModelParameter modelParameter : modelParameters) {
            modelParameter.removeVetoableChangeListener(this);
            modelParameter.removePropertyChangeListener(this);
            modelParameter.addVetoableChangeListener(Parameter.PROPERTYNAME_NAME, this);
            modelParameter.addPropertyChangeListener(this);
            try {
                modelParameter.getExpression().bindExpression(this);
            } catch (ExpressionBindingException e) {
                throw new RuntimeException("Error binding global parameter '" + modelParameter.getName() + "' to model: " + e.getMessage(), e);
            }
        }

        for(int i = 0; i < getRbmModelContainer().getReactionRuleList().size(); i++){
            ReactionRule reactionRule = getRbmModelContainer().getReactionRule(i);
            reactionRule.removePropertyChangeListener(this);
            reactionRule.removeVetoableChangeListener(this);
            reactionRule.getKineticLaw().removePropertyChangeListener(this);
            //reactionRule.getKineticLaw().removeVetoableChangeListener(this);
            reactionRule.getKineticLaw().addPropertyChangeListener(this);
            //reactionRule.getKineticLaw().addVetoableChangeListener(this);
            reactionRule.addPropertyChangeListener(this);
            reactionRule.addVetoableChangeListener(this);
            reactionRule.setModel(this);
            try {
                reactionRule.rebindAllToModel(this);
            } catch(Exception e){
                lg.error(e.getMessage(), e);
            }
            reactionRule.refreshDependencies();
        }
        for(RbmObservable observable : getRbmModelContainer().getObservableList()){
            observable.removePropertyChangeListener(this);
            observable.removeVetoableChangeListener(this);
            observable.addPropertyChangeListener(this);
            observable.addVetoableChangeListener(this);
            observable.setModel(this);
        }
        for(MolecularType molType : getRbmModelContainer().getMolecularTypeList()){
            molType.removePropertyChangeListener(this);
            molType.removeVetoableChangeListener(this);
            molType.addPropertyChangeListener(this);
            molType.addVetoableChangeListener(this);
            molType.setModel(this);
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (3/21/2001 4:38:17 PM)
     */
    private void refreshDiagrams(){
        //
        // removed diagrams for those structures that were removed
        //
        boolean bChangedDiagrams = false;
        Diagram[] newDiagrams = diagrams.clone();
        for(Diagram fieldDiagram : diagrams){
            if(!contains(fieldDiagram.getStructure())){
                newDiagrams =
                        ArrayUtils.removeFirstInstanceOfElement(newDiagrams, fieldDiagram);
                bChangedDiagrams = true;
            }
        }
        //
        // add new diagrams for new structures
        //
        for(Structure fieldStructure : structures){
            if(getDiagram(fieldStructure) == null){
                newDiagrams = ArrayUtils.addElement(newDiagrams, new Diagram(fieldStructure, fieldStructure.getName()));
                bChangedDiagrams = true;
            }
        }

        if(bChangedDiagrams){
            try {
                setDiagrams(newDiagrams);
            } catch(PropertyVetoException e){
                throw new RuntimeException(e.getMessage(), e);
            }
        }

    }


    public void removeStructure(Structure removedStructure, boolean canRemoveLast) throws PropertyVetoException{

        if(removedStructure == null){
            throw new RuntimeException("structure is null");
        }
        if(!contains(removedStructure)){
            throw new RuntimeException("structure " + removedStructure.getName() + " not found");
        }
        if(structures.length == 1 && !canRemoveLast){
            throw new RuntimeException("Remove model compartment Error. Cannot remove the last compartment.");
        }

        //Check that the feature is empty
        String prefix = "Remove model compartment Error\n\nStructure to be removed : '" + removedStructure.getName() + "' : ";
        for (ReactionStep reactionStep : reactionSteps) {
            if (reactionStep.getStructure() != removedStructure) continue;
            throw new RuntimeException(prefix + "cannot contain Reactions.");
        }
        for (SpeciesContext speciesContext : speciesContexts) {
            if (speciesContext.getStructure() != removedStructure) continue;
            throw new RuntimeException(prefix + "cannot contain Reactions.");
        }
        for(ReactionRule rr : getRbmModelContainer().getReactionRuleList()){
            if(rr.getStructure() == removedStructure){
                throw new RuntimeException(prefix + "cannot contain Reaction Rules.");
            }

            for(ReactantPattern rp : rr.getReactantPatterns()){
                if(rp.getStructure() != removedStructure) continue;
                throw new RuntimeException(prefix + "cannot contain Reactant Patterns.");

            }
            for(ProductPattern pp : rr.getProductPatterns()){
                if(pp.getStructure() != removedStructure) continue;
                throw new RuntimeException(prefix + "cannot contain Product Patterns.");
            }
        }
        for(RbmObservable o : getRbmModelContainer().getObservableList()){
            if(o.getStructure() != removedStructure) continue;
            throw new RuntimeException(prefix + "cannot contain Observables.");

        }
        for(MolecularType mt : getRbmModelContainer().getMolecularTypeList()){
            if(!mt.getAnchors().contains(removedStructure)) continue;
            throw new RuntimeException(prefix + "cannot be Molecule anchor.");
        }
        //
        // remove this structure
        //
        Structure[] newStructures = structures.clone();
        newStructures = ArrayUtils.removeFirstInstanceOfElement(newStructures, removedStructure);
        setStructures(newStructures);
    }


    public void removeModelParameter(Model.ModelParameter modelParameter) throws PropertyVetoException{

        if(modelParameter == null){
            return;
        }
        if(contains(modelParameter)){
            Model.ModelParameter[] newModelParameters = ArrayUtils.removeFirstInstanceOfElement(modelParameters, modelParameter);
            setModelParameters(newModelParameters);
        }
    }

    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener){
        getPropertyChange().removePropertyChangeListener(listener);
    }


    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener){
        getPropertyChange().removePropertyChangeListener(propertyName, listener);
    }


    /**
     * This method was created in VisualAge.
     *
     * @param reactionStep cbit.vcell.model.ReactionStep
     */
    public void removeReactionStep(ReactionStep reactionStep) throws PropertyVetoException{
        if(contains(reactionStep)){
            setReactionSteps(ArrayUtils.removeFirstInstanceOfElement(reactionSteps, reactionStep));
        }
    }


    public void removeSpecies(Species species) throws PropertyVetoException{

        if(species == null){
            return;
        }
        if(contains(species)){
            Species[] newSpeciesArray = ArrayUtils.removeFirstInstanceOfElement(this.species, species);
            setSpecies(newSpeciesArray);
        }
    }


    public void removeSpeciesContext(SpeciesContext speciesContext) throws PropertyVetoException{
        if(contains(speciesContext)){
            SpeciesContext[] newSpeciesContexts = ArrayUtils.removeFirstInstanceOfElement(speciesContexts, speciesContext);
            setSpeciesContexts(newSpeciesContexts);
        }
    }


    /**
     * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener){
        getVetoPropertyChange().removeVetoableChangeListener(listener);
    }


    /**
     * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener){
        getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
    }


    /**
     * Sets the description property (java.lang.String) value.
     *
     * @param description The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getDescription
     */
    public void setDescription(java.lang.String description) throws java.beans.PropertyVetoException{
        String oldValue = fieldDescription;
        fireVetoableChange("description", oldValue, description);
        fieldDescription = description;
        firePropertyChange("description", oldValue, description);
    }


    /**
     * Sets the diagrams property (cbit.vcell.model.Diagram[]) value.
     *
     * @param diagrams The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getDiagrams
     */
    public void setDiagrams(Diagram[] diagrams) throws java.beans.PropertyVetoException{
        Diagram[] oldValue = this.diagrams;
        fireVetoableChange("diagrams", oldValue, diagrams);
        this.diagrams = diagrams;
        firePropertyChange("diagrams", oldValue, diagrams);
    }


    /**
     * Sets the diagrams index property (cbit.vcell.model.Diagram[]) value.
     *
     * @param index    The index value into the property array.
     * @param diagrams The new value for the property.
     * @see #getDiagrams
     */
    public void setDiagrams(int index, Diagram diagrams){
        Diagram oldValue = this.diagrams[index];
        this.diagrams[index] = diagrams;
        if(oldValue != null && !oldValue.equals(diagrams)){
            firePropertyChange("diagrams", null, this.diagrams);
        }
    }


    public void setModelParameters(ModelParameter[] modelParameters) throws java.beans.PropertyVetoException{
        ModelParameter[] oldValue = this.modelParameters;
        fireVetoableChange(Model.PROPERTY_NAME_MODEL_PARAMETERS, oldValue, modelParameters);
        this.modelParameters = modelParameters;
        firePropertyChange(Model.PROPERTY_NAME_MODEL_PARAMETERS, oldValue, modelParameters);

//System.out.print("vcModel model parameters [");
//for (ModelParameter p : oldValue){
//System.out.print(" "+p.getName()+" ");
//}
//System.out.print("]  -->  [");
//for (ModelParameter p : modelParameters){
//System.out.print(" "+p.getName()+" ");
//}
//System.out.println("]");
//if (oldValue.length>0 && modelParameters.length==0){
//System.out.println("removed model parameters.");
//}

        if(oldValue != null){
            for (ModelParameter modelParameter : oldValue) {
                modelParameter.removePropertyChangeListener(this);
                modelParameter.removeVetoableChangeListener(this);
            }
        }
        if(modelParameters != null){
            for (ModelParameter modelParameter : modelParameters) {
                modelParameter.addPropertyChangeListener(this);
                modelParameter.addVetoableChangeListener(Parameter.PROPERTYNAME_NAME, this);
            }
        }
    }


    public void setModelFunctions(ModelFunction[] modelFunctions) throws java.beans.PropertyVetoException{
        ModelFunction[] oldValue = fieldModelFunctions;
        fireVetoableChange(Model.PROPERTY_NAME_MODEL_FUNCTIONS, oldValue, modelFunctions);
        fieldModelFunctions = modelFunctions;
        firePropertyChange(Model.PROPERTY_NAME_MODEL_FUNCTIONS, oldValue, modelFunctions);

//	ModelFunction newValue[] = modelFunctions;
//	if (oldValue != null) {
//		for (int i=0;i<oldValue.length;i++){
//			oldValue[i].removePropertyChangeListener(this);
//			oldValue[i].removeVetoableChangeListener(this);
//		}
//	}
//	if (newValue != null) {
//		for (int i=0;i<newValue.length;i++){
//			newValue[i].addPropertyChangeListener(this);
//			newValue[i].addVetoableChangeListener(Function.PROPERTYNAME_NAME, this);
//		}
//	}
    }

    public ModelFunction[] getModelFunctions(){
        return this.fieldModelFunctions;
    }


    public void setName(java.lang.String name) throws java.beans.PropertyVetoException{
        String oldValue = fieldName;
        fireVetoableChange("name", oldValue, name);
        fieldName = name;
        firePropertyChange("name", oldValue, name);
    }

    public void setReactionSteps(ReactionStep[] reactionSteps) throws java.beans.PropertyVetoException{
        ReactionStep[] oldValue = this.reactionSteps;
        fireVetoableChange(PROPERTY_NAME_REACTION_STEPS, oldValue, reactionSteps);
        HashSet<ReactionStep> oldReactions = new HashSet<>(Arrays.asList(this.reactionSteps));
        HashSet<ReactionStep> newReactions = new HashSet<>(Arrays.asList(reactionSteps));
        HashSet<ReactionStep> reactionsAdded = new HashSet<>(newReactions);
        reactionsAdded.removeAll(oldReactions);
        HashSet<ReactionStep> reactionsRemoved = new HashSet<>(oldReactions);
        reactionsRemoved.removeAll(newReactions);

        this.reactionSteps = reactionSteps;

        for(ReactionStep rs : reactionsRemoved){
            rs.removePropertyChangeListener(this);
            rs.removeVetoableChangeListener(this);
            rs.getKinetics().removePropertyChangeListener(this);
            rs.getKinetics().removeVetoableChangeListener(this);
            rs.setModel(null);
        }
        for(ReactionStep rs : reactionsAdded){
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
            } catch(Exception e){
                lg.error(e.getMessage(), e);
            }
        }
        firePropertyChange(PROPERTY_NAME_REACTION_STEPS, oldValue, reactionSteps);
    }


    /**
     * Sets the reactionSteps index property (cbit.vcell.model.ReactionStep[]) value.
     *
     * @param index         The index value into the property array.
     * @param reactionSteps The new value for the property.
     * @see #getReactionSteps
     */
    public void setReactionSteps(int index, ReactionStep reactionSteps){
        ReactionStep oldValue = this.reactionSteps[index];
        this.reactionSteps[index] = reactionSteps;
        if(oldValue != null && !oldValue.equals(reactionSteps)){
            firePropertyChange(PROPERTY_NAME_REACTION_STEPS, null, this.reactionSteps);
        }
    }


    /**
     * Sets the species property (cbit.vcell.model.Species[]) value.
     *
     * @param species The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getSpecies
     */
    public void setSpecies(Species[] species) throws java.beans.PropertyVetoException{
        Species[] oldValue = this.species;
        fireVetoableChange(PROPERTY_NAME_SPECIES, oldValue, species);
        this.species = species;
        firePropertyChange(PROPERTY_NAME_SPECIES, oldValue, species);

        for (Species value : oldValue) {
            //oldValue[i].removePropertyChangeListener(this);
            value.removeVetoableChangeListener(this);
        }
        for (Species value : species) {
            //newValue[i].addPropertyChangeListener(this);
            value.addVetoableChangeListener(this);
        }
    }


    /**
     * Sets the species index property (cbit.vcell.model.Species[]) value.
     *
     * @param index   The index value into the property array.
     * @param species The new value for the property.
     * @see #getSpecies
     */
    public void setSpecies(int index, Species species){
        Species oldValue = this.species[index];
        this.species[index] = species;
        if(oldValue != null && !oldValue.equals(species)){
            firePropertyChange(PROPERTY_NAME_SPECIES, null, this.species);
        }
    }


    /**
     * Sets the speciesContexts property (cbit.vcell.model.SpeciesContext[]) value.
     *
     * @param speciesContexts The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getSpeciesContexts
     */
    public void setSpeciesContexts(SpeciesContext[] speciesContexts) throws java.beans.PropertyVetoException{
        SpeciesContext[] oldValue = this.speciesContexts;
        fireVetoableChange(PROPERTY_NAME_SPECIES_CONTEXTS, oldValue, speciesContexts);
        this.speciesContexts = speciesContexts;
        for (SpeciesContext speciesContext : speciesContexts) {
            speciesContext.setModel(this);
        }

        firePropertyChange(PROPERTY_NAME_SPECIES_CONTEXTS, oldValue, speciesContexts);

        for (SpeciesContext speciesContext : oldValue) {
            speciesContext.removePropertyChangeListener(this);
            speciesContext.removeVetoableChangeListener(this);
            speciesContext.setModel(null);
        }
        for (SpeciesContext speciesContext : speciesContexts) {
            speciesContext.addPropertyChangeListener(this);
            speciesContext.addVetoableChangeListener(this);
            speciesContext.setModel(this);
        }
        //
        //Remove orphaned Species but only for SpeciesContext that were in old and not in new
        //The API should be changed so that species cannot be added or retrieved independently of SpeciesContexts
        //

        HashSet<Species> oldSpeciesSet = new HashSet<>();
        for (SpeciesContext speciesContext : oldValue) {
            oldSpeciesSet.add(speciesContext.getSpecies());
        }
        HashSet<Species> newSpeciesSet = new HashSet<>();
        for (SpeciesContext speciesContext : speciesContexts) {
            newSpeciesSet.add(speciesContext.getSpecies());
        }

        oldSpeciesSet.removeAll(newSpeciesSet);
        Iterator<Species> spIterator = oldSpeciesSet.iterator();
        while (spIterator.hasNext()) {
            try {
                removeSpecies(spIterator.next());
            } catch(Throwable e){
                lg.error(e);
            }
        }
    }


    /**
     * Sets the speciesContexts index property (cbit.vcell.model.SpeciesContext[]) value.
     *
     * @param index           The index value into the property array.
     * @param speciesContexts The new value for the property.
     * @see #getSpeciesContexts
     */
    public void setSpeciesContexts(int index, SpeciesContext speciesContexts){
        SpeciesContext oldValue = this.speciesContexts[index];
        speciesContexts.setModel(this);
        this.speciesContexts[index] = speciesContexts;
        if(oldValue != null && !oldValue.equals(speciesContexts)){
            firePropertyChange(PROPERTY_NAME_SPECIES_CONTEXTS, null, this.speciesContexts);
        }
    }


    /**
     * Sets the structures property (cbit.vcell.model.Structure[]) value.
     *
     * @param structures The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getStructures
     */
    public void setStructures(Structure[] structures) throws java.beans.PropertyVetoException{
        Structure[] oldValue = this.structures;
        fireVetoableChange(PROPERTY_NAME_STRUCTURES, oldValue, structures);
        this.structures = structures;
        for (Structure structure : structures) {
            structure.setModel(this);
        }

        // if structures changed, structureTopology and electrical topology might need to be adjusted
        structureTopology.refresh();
        electricalTopology.refresh();

        refreshDiagrams();
        firePropertyChange(PROPERTY_NAME_STRUCTURES, oldValue, structures);


        for (Structure structure : oldValue) {
            structure.removePropertyChangeListener(this);
            structure.removeVetoableChangeListener(this);
            structure.setModel(null);
            structure.getStructureSize().removePropertyChangeListener(this);
            structure.getStructureSize().removeVetoableChangeListener(this);
            if (structure instanceof Membrane) {
                ((Membrane) structure).getMembraneVoltage().removePropertyChangeListener(this);
                ((Membrane) structure).getMembraneVoltage().removeVetoableChangeListener(this);
            }
        }
        for (Structure structure : structures) {
            structure.addPropertyChangeListener(this);
            structure.addVetoableChangeListener(this);
            structure.setModel(this);
            structure.getStructureSize().addPropertyChangeListener(this);
            structure.getStructureSize().addVetoableChangeListener(this);
            if (structure instanceof Membrane) {
                ((Membrane) structure).getMembraneVoltage().addPropertyChangeListener(this);
                ((Membrane) structure).getMembraneVoltage().addVetoableChangeListener(this);
            }
        }

//	showStructureHierarchy();
    }


    /**
     * This method was created by a SmartGuide.
     *
     * @return java.lang.String
     */
    public String toString(){
        return "Model@" + Integer.toHexString(hashCode()) + "(" + ((version != null) ? version.toString() : getName()) + ")";
    }


    public ReservedSymbol getReservedSymbolByName(String name){
        for(ReservedSymbol rs : fieldReservedSymbols){
            if(rs.getName().equals(name)){
                return rs;
            }
        }
        return null;
    }

    public ReservedSymbol getReservedSymbolByRole(ReservedSymbolRole role){
        for(ReservedSymbol rs : fieldReservedSymbols){
            if(rs.getRole().equals(role)){
                return rs;
            }
        }
        return null;
    }

    public void vetoableChange(PropertyChangeEvent e) throws ModelPropertyVetoException{
        if(e.getSource() instanceof Structure){
            if(e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
                if(getStructure((String) e.getNewValue()) != null){
                    throw new ModelPropertyVetoException("another structure already using name " + e.getNewValue(), e);
                }
            }
        }
        if(e.getSource() instanceof ReactionStep){
            if(e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
                String newName = (String) e.getNewValue();
                if(getReactionStep(newName) != null){
                    throw new ModelPropertyVetoException("another reaction step is already using name '" + newName + "'", e);
                }
                // validateNamingConflicts("Reaction",ReactionStep.class, newName, e);
            }
        }
        if(e.getSource() instanceof ReactionRule && e.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)){
            if(!e.getOldValue().equals(e.getNewValue())){
                String newName = (String) e.getNewValue();
                if(getRbmModelContainer().getReactionRule(newName) != null){
                    throw new ModelPropertyVetoException("another reaction rule is already using name '" + newName + "'", e);
                }
                validateNamingConflicts("Rule", ReactionRule.class, newName, e);
            }
        }

        if(e.getSource() instanceof SpeciesContext){
            if(e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
                String newName = (String) e.getNewValue();
                SpeciesContext sc = getSpeciesContext(newName);
                if(sc != null){
                    throw new ModelPropertyVetoException("another " + SpeciesContext.getTerm() + " defined in '" + sc.getStructure().getName() + "' already uses name '" + e.getNewValue() + "'", e);
                }
                validateNamingConflicts("SpeciesContext", SpeciesContext.class, newName, e);
            }
        }
        if(e.getSource() instanceof MembraneVoltage){
            if(e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
                String newName = (String) e.getNewValue();
                SymbolTableEntry existingSTE = getLocalEntry(newName);
                if(existingSTE instanceof MembraneVoltage){
                    throw new ModelPropertyVetoException("new name \"" + newName + "\" conflicts with the voltage parameter name for membrane \"" + ((MembraneVoltage) existingSTE).getMembrane().getName() + "\"", e);
                }
                validateNamingConflicts("MembraneVoltage", MembraneVoltage.class, newName, e);
            }
        }
        if(e.getSource() instanceof StructureSize){
            if(e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
                String newName = (String) e.getNewValue();
                SymbolTableEntry existingSTE = getLocalEntry(newName);
                if(existingSTE instanceof StructureSize){
                    throw new ModelPropertyVetoException("new name \"" + newName + "\" conflicts with the size parameter name for structure \"" + ((StructureSize) existingSTE).getStructure().getName() + "\"", e);
                }
                validateNamingConflicts("StructureSize", StructureSize.class, newName, e);
            }
        }
        if(e.getSource() instanceof ModelParameter){
            if(e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
                String newName = (String) e.getNewValue();
                if(getModelParameter(newName) != null){
                    throw new ModelPropertyVetoException("Model Parameter with name '" + newName + "' already exists.", e);
                }
                validateNamingConflicts("Model Parameter", ModelParameter.class, newName, e);
            }
        }
        if(e.getSource() instanceof MolecularType){
            if(e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
                String newName = (String) e.getNewValue();
                if(getRbmModelContainer().getMolecularType(newName) != null){
                    throw new ModelPropertyVetoException(MolecularType.typeName + " with name '" + newName + "' already exists.", e);
                }
                validateNamingConflicts(((MolecularType) e.getSource()).getDisplayType(), MolecularType.class, newName, e);
            }
        }
        if(e.getSource() instanceof RbmObservable){
            if(e.getPropertyName().equals("name") && !e.getOldValue().equals(e.getNewValue())){
                String newName = (String) e.getNewValue();
                if(getRbmModelContainer().getObservable(newName) != null){
                    throw new ModelPropertyVetoException(((RbmObservable) e.getSource()).getDisplayType() + " with name '" + newName + "' already exists.", e);
                }
                validateNamingConflicts(((RbmObservable) e.getSource()).getDisplayType(), RbmObservable.class, newName, e);
            }
        }

        if(e.getSource() instanceof Species){
            if(e.getPropertyName().equals("commonName") && !e.getOldValue().equals(e.getNewValue())){
                String commonName = (String) e.getNewValue();
                if(commonName == null){
                    throw new ModelPropertyVetoException("species name cannot be null", e);
                }
                //
                // check that new name is not duplicated and that new Name isn't ReservedSymbols
                //
                if(getSpecies(commonName) != null){
                    throw new ModelPropertyVetoException("Species with common name '" + commonName + "' already defined", e);
                }
                if(getReservedSymbolByName(commonName) != null){
                    throw new ModelPropertyVetoException("cannot use reserved symbol '" + commonName + "' as a Species common name", e,
                            ModelPropertyVetoException.Category.RESERVED_SYMBOL);
                }
            }
        }

        if(e.getSource() == this && e.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)){
            ArrayList<MolecularType> newMolecularTypes = (ArrayList<MolecularType>) e.getNewValue();
            if(newMolecularTypes == null){
                throw new ModelPropertyVetoException(MolecularType.typeName + " list cannot be null", e);
            }
            for(MolecularType mt : getRbmModelContainer().getMolecularTypeList()){
                validateNamingConflicts(MolecularType.typeName, MolecularType.class, mt.getName(), e);
            }
        }

        if(e.getSource() == this && e.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_OBSERVABLE_LIST)){
            ArrayList<RbmObservable> newObservables = (ArrayList<RbmObservable>) e.getNewValue();
            if(newObservables == null){
                throw new ModelPropertyVetoException(RbmObservable.typeName + " list cannot be null", e);
            }
            for(RbmObservable observable : getRbmModelContainer().getObservableList()){
                validateNamingConflicts(RbmObservable.typeName, RbmObservable.class, observable.getName(), e);
            }
        }

        if(e.getSource() == this && e.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_REACTION_RULE_LIST)){
            ArrayList<ReactionRule> newReactionRules = (ArrayList<ReactionRule>) e.getNewValue();
            if(newReactionRules == null){
                throw new ModelPropertyVetoException(ReactionRule.typeName + " list cannot be null", e);
            }
            for(ReactionRule rr : getRbmModelContainer().getReactionRuleList()){
                validateNamingConflicts(ReactionRule.typeName, ReactionRule.class, rr.getName(), e);
            }
        }

        if(e.getSource() == this && e.getPropertyName().equals(PROPERTY_NAME_STRUCTURES)){
            Structure[] newStructures = (Structure[]) e.getNewValue();
            if(newStructures == null){
                throw new ModelPropertyVetoException("structures cannot be null", e);
            }
            //
            // look for duplicates of structure name, structure size name, membrane voltage name within new "structures" array
            // and look for symbol conflicts for StructureSize name and for MembraneVoltage name in existing "local" symbols.
            //
            HashSet<String> structNameSet = new HashSet<>();
            HashSet<String> structSymbolSet = new HashSet<>();
            for (Structure newStructure : newStructures) {

                String newStructureName = newStructure.getName();
                if (structNameSet.contains(newStructureName)) {
                    throw new ModelPropertyVetoException("multiple structures with name '" + newStructureName + "' defined", e);
                }
                structNameSet.add(newStructureName);

                if (newStructure instanceof Membrane) {
                    String newMembraneVoltageName = ((Membrane) newStructure).getMembraneVoltage().getName();
                    if (structSymbolSet.contains(newMembraneVoltageName)) {
                        //throw new ModelPropertyVetoException("membrane '"+newStructureName+"' has Voltage name '"+newMembraneVoltageName+"' that conflicts with another Voltage name or Size name",e);
                    }
                    structSymbolSet.add(newMembraneVoltageName);
                    validateNamingConflicts("MembraneVoltage", MembraneVoltage.class, newMembraneVoltageName, e);
                }

                String newStructureSizeName = newStructure.getStructureSize().getName();
                if (structSymbolSet.contains(newStructureSizeName)) {
                    throw new ModelPropertyVetoException("structure '" + newStructureName + "' has structure Size name '" + newStructureSizeName + "' that conflicts with another Voltage name or Size name", e);
                }
                structSymbolSet.add(newStructureSizeName);
                validateNamingConflicts("StructureSize", StructureSize.class, newStructureSizeName, e);
            }
        }

        if(e.getSource() == this && e.getPropertyName().equals(PROPERTY_NAME_SPECIES)){
            Species[] newSpeciesArray = (Species[]) e.getNewValue();
            if(newSpeciesArray == null){
                throw new ModelPropertyVetoException("species cannot be null", e);
            }
            //
            // check that names are not duplicated and that no common names are ReservedSymbols
            //
            HashSet<String> commonNameSet = new HashSet<>();
            for (Species value : newSpeciesArray) {
                String commonName = value.getCommonName();
                if (commonNameSet.contains(commonName)) {
                    throw new ModelPropertyVetoException("multiple species with common name '" + commonName + "' defined", e);
                }
                if (getReservedSymbolByName(commonName) != null) {
                    throw new ModelPropertyVetoException("cannot use reserved symbol '" + commonName + "' as a Species common name", e,
                            ModelPropertyVetoException.Category.RESERVED_SYMBOL);
                }
                commonNameSet.add(commonName);
            }
            //
            // if species deleted, must not have any SpeciesContexts that need it
            //
            for (SpeciesContext sc : speciesContexts) {
                boolean bFound = false;
                for (Species value : newSpeciesArray) {
                    if (value == sc.getSpecies()) {
                        bFound = true;
                    }
                }
                if (!bFound) {
                    throw new ModelPropertyVetoException("species[] missing '" + sc.getSpecies().getCommonName() + "' referenced in SpeciesContext '" + sc.getName() + "'", e);
                }
            }
        }

        if(e.getSource() == this && e.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_PARAMETERS)){
            ModelParameter[] newModelParams = (ModelParameter[]) e.getNewValue();
            //
            // check that names are not duplicated and that no common names are ReservedSymbols
            //
            HashSet<String> namesSet = new HashSet<>();
            for (ModelParameter modelParam : newModelParams) {
                if (namesSet.contains(modelParam.getName())) {
                    throw new ModelPropertyVetoException("Multiple model/global parameters with same name '" + modelParam.getName() + "' defined", e);
                }
                namesSet.add(modelParam.getName());

                validateNamingConflicts("Model Parameter", ModelParameter.class, modelParam.getName(), e);
            }
            //
            // make sure that kinetics of reactionSteps do not refer to modelParameter to be deleted
            // Find this model parameter, missing from 'newModelParams'; loop thro' all reactionStep kinetics to determine if it is used
            //
            ModelParameter[] oldModelParameters = (ModelParameter[]) e.getOldValue();
            if(oldModelParameters.length > newModelParams.length){
                // if 'newModelParameter' is smaller than 'oldModelParameter', one of the modelParams has been removed, find the missing one
                ModelParameter missingMP = null;
                for(ModelParameter oldModelParameter : oldModelParameters){
                    if(!ArrayUtils.arrayContains(newModelParams, oldModelParameter)){
                        missingMP = oldModelParameter;
                    }
                }
                // use this missing model parameter (to be deleted) to determine if it is used in any reaction kinetic parameters.
                if(missingMP != null){
                    Vector<String> referencingRxnsVector = new Vector<>();
                    for(ReactionStep fieldReactionStep : reactionSteps){
                        KineticsParameter[] kParams = fieldReactionStep.getKinetics().getKineticsParameters();
                        for(KineticsParameter kParam : kParams){
                            if(kParam.getExpression().hasSymbol(missingMP.getName()) && (fieldReactionStep.getKinetics().getProxyParameter(missingMP.getName()) != null)){
                                referencingRxnsVector.add(fieldReactionStep.getName());
                                break;
                            }
                        }
                    }
                    // if there are any reactionSteps referencing the global, list them all in error msg.
                    if(!referencingRxnsVector.isEmpty()){
                        StringBuilder msg = new StringBuilder("Model Parameter '" + missingMP.getName() + "' is used in reaction(s): ");
                        for(int i = 0; i < referencingRxnsVector.size(); i++){
                            msg.append("'").append(referencingRxnsVector.elementAt(i)).append("'");
                            if(i < referencingRxnsVector.size() - 1){
                                msg.append(", ");
                            } else {
                                msg.append(". ");
                            }
                        }
                        msg.append("\n\nCannot delete '").append(missingMP.getName()).append("'.");
                        throw new ModelPropertyVetoException(msg.toString(), e);
                    }
                    // At this point, it is not referenced in a reactionStep, find out if the missing model is used in other model parameters
                    // Enough to check in newModelParams array
                    Vector<String> referencingModelParamsVector = new Vector<>();
                    for(ModelParameter newModelParam : newModelParams){
                        if(newModelParam.getExpression().hasSymbol(missingMP.getName())){
                            referencingModelParamsVector.add(newModelParam.getName());
                        }
                    }
                    // if there are any model parameters referencing the global, list them all in error msg.
                    if(!referencingModelParamsVector.isEmpty()){
                        StringBuilder msg = new StringBuilder("Model Parameter '" + missingMP.getName() + "' is used in expression of other model parameter(s): ");
                        for(int i = 0; i < referencingModelParamsVector.size(); i++){
                            msg.append("'").append(referencingModelParamsVector.elementAt(i)).append("'");
                            if(i < referencingModelParamsVector.size() - 1){
                                msg.append(", ");
                            } else {
                                msg.append(". ");
                            }
                        }
                        msg.append("\n\nCannot delete '").append(missingMP.getName()).append("'.");
                        throw new ModelPropertyVetoException(msg.toString(), e);
                    }
                }
            }
        }

        if(e.getSource() == this && e.getPropertyName().equals(PROPERTY_NAME_SPECIES_CONTEXTS)){
            SpeciesContext[] newSpeciesContextArray = (SpeciesContext[]) e.getNewValue();
            if(newSpeciesContextArray == null){
                throw new ModelPropertyVetoException("speciesContexts cannot be null", e);
            }
            //
            // check that the species and structure for each speciesContext already exist.
            //
            for(SpeciesContext speciesContext : newSpeciesContextArray){
                if(!contains(speciesContext.getSpecies())){
                    throw new ModelPropertyVetoException("can't add speciesContext '" + speciesContext.getName() + "' before species '" + speciesContext.getSpecies().getCommonName() + "'", e);
                }
                if(!contains(speciesContext.getStructure())){
                    throw new ModelPropertyVetoException("can't add speciesContext '" + speciesContext.getName() + "' before structure '" + speciesContext.getStructure().getName() + "'", e);
                }
            }
            //
            // check that names are not duplicated and that no names are ReservedSymbols
            //
            HashSet<String> nameSet = new HashSet<>();
            for(SpeciesContext speciesContext : newSpeciesContextArray){
                if(nameSet.contains(speciesContext.getName())){
                    throw new ModelPropertyVetoException("multiple speciesContexts with name '" + speciesContext.getName() + "' defined", e);
                }
                nameSet.add(speciesContext.getName());

                validateNamingConflicts("SpeciesContext", speciesContext.getClass(), speciesContext.getName(), e);
            }
            //
            // make sure that reactionParticipants point to speciesContext
            //
            for(ReactionStep fieldReactionStep : reactionSteps){
                ReactionParticipant[] rpArray = fieldReactionStep.getReactionParticipants();
                for(ReactionParticipant reactionParticipant : rpArray){
                    boolean bFound = false;
                    for(SpeciesContext speciesContext : newSpeciesContextArray){
                        if(speciesContext == reactionParticipant.getSpeciesContext()){
                            bFound = true;
                            break;
                        }
                    }
                    if(!bFound){
                        throw new ModelPropertyVetoException("reaction '" + fieldReactionStep.getName() + "' requires '" + reactionParticipant.getSpeciesContext().getName() + "'", e);
                    }
                }
            }
        }

        if(e.getSource() == this && e.getPropertyName().equals(PROPERTY_NAME_REACTION_STEPS)){
            ReactionStep[] newReactionStepArr = (ReactionStep[]) e.getNewValue();
            //
            //Check because a null could get this far and would throw a NullPointerException later
            //None of the other PropertyVeto checks do this.  Do We Want To Keep This????
            //
            for(ReactionStep step : newReactionStepArr){
                if(step == null){
                    throw new ModelPropertyVetoException("Null cannot be added to ReactionStep", e);
                }
            }
            //
            // check that names are not duplicated and that no names are ReservedSymbols
            //because math generator complained if reactionsteps had same name
            //
            HashSet<String> nameSet = new HashSet<>();
            for(ReactionStep reactionStep : newReactionStepArr){
                String rxnName = reactionStep.getName();
                if(nameSet.contains(rxnName)){
                    throw new ModelPropertyVetoException("multiple reactionSteps with name '" + rxnName + "' defined", e);
                }
                if(getReservedSymbolByName(rxnName) != null){
                    throw new ModelPropertyVetoException("cannot use reserved symbol '" + rxnName + "' as a Reaction name", e,
                            ModelPropertyVetoException.Category.RESERVED_SYMBOL);
                }
                nameSet.add(rxnName);

                // validateNamingConflicts("Reaction", ReactionStep.class, newReactionStepArr[i].getName(), e);
            }
            //
            // make sure that reactionParticipants point to speciesContext that exist
            //because reactionsteps could be added that had speciescontext that model didn't
            //
            for(ReactionStep reactionStep : newReactionStepArr){
                ReactionParticipant[] rpArray = reactionStep.getReactionParticipants();
                for(ReactionParticipant reactionParticipant : rpArray){
                    boolean bFound = false;
                    for(SpeciesContext fieldSpeciesContext : speciesContexts){
                        if(fieldSpeciesContext == reactionParticipant.getSpeciesContext()){
                            bFound = true;
                            break;
                        }
                    }
                    if(!bFound){
                        String rName = reactionStep.getName();
                        String sName;
                        if(rpArray != null && reactionParticipant != null && reactionParticipant.getSpeciesContext() != null && reactionParticipant.getSpeciesContext().getName() != null){
                            sName = reactionParticipant.getSpeciesContext().getName();
                            throw new ModelPropertyVetoException("Reaction '" + rName + "' requires '" + sName + "'", e);
                        } else {
                            throw new ModelPropertyVetoException("Reaction '" + rName + "' has a null participant", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * if newSTE is null, then newName is the proposed name of a reaction
     * else newSTE is the symbol to be added.
     */
    private void validateNamingConflicts(String symbolDescription, Class<?> newSymbolClass, String newSymbolName, PropertyChangeEvent e)
            throws ModelPropertyVetoException{
        //
        // validate lexicon
        //
        if(newSymbolName == null){
            throw new ModelPropertyVetoException(symbolDescription + " name is null.", e);
        }
        if(newSymbolName.isEmpty()){
            throw new ModelPropertyVetoException(symbolDescription + " name is empty (zero length).", e);
        }
        if(!newSymbolName.equals(TokenMangler.fixTokenStrict(newSymbolName))){
            throw new ModelPropertyVetoException(symbolDescription + " '" + newSymbolName + "' not legal identifier, try '" + TokenMangler.fixTokenStrict(newSymbolName) + "'.", e);
        }

        // Molecular Type is an abstract concept, hence the name can be reused (in a species for example)
        //
        // We actually can't verify anything because setMolecularTypeList() will fire a PROPERTY_NAME_MOLECULAR_TYPE_LIST with names
        // in the old list which will also be present in the new list, hence we'll always throw exception we shouldn't
        if(newSymbolClass.equals(MolecularType.class)){
            return;
        }

        //
        // Make sure not to change name of a "global" symbol to that of a ReactionStep name, ReactionRule name, or MolecularType name.
        // These things are not really in the global namespace, but it would be confusing to allow reuse of names.
        //
        if(!newSymbolClass.equals(ReactionStep.class)){
            for (ReactionStep reactionStep : reactionSteps) {
                if (reactionStep.getName().equals(newSymbolName)) {
                    throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for " + reactionStep.getDisplayType() + " '" + reactionStep.getName() + "' in structure '" + reactionStep.getStructure().getName() + "'.", e);
                }
            }
        }
        if(!newSymbolClass.equals(ReactionRule.class)){
            for(ReactionRule rr : rbmModelContainer.reactionRuleList){
                if(rr.getName().equals(newSymbolName)){
                    throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for " + rr.getDisplayType() + " '" + rr.getName() + "' in structure '" + rr.getStructure().getName() + "'.", e);
                }
            }
        }
// TODO: may be too strict, keep it commented for now
//	if (newSymbolClass.equals(ReactionStep.class) || newSymbolClass.equals(ReactionRule.class)){
//		for (MolecularType mt : rbmModelContainer.molecularTypeList){
//			if (mt.getName().equals(newSymbolName)){
//				throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for " + mt.getDisplayType() + " '" + mt.getName() + "'.",e);
//			}
//		}
//	}

        //
        // make sure not to change to name of any other symbol in 'model' namespace (or friendly namespaces)
        //
        SymbolTableEntry localSTE = getLocalEntry(newSymbolName);
        if(localSTE == null){
            return;
        }
        //
        // if the existing (local) symbol and the new symbol are of the same type (same class) then ignore any conflict.
        //
        if(newSymbolClass.equals(localSTE.getClass())){
            return;
        }

        //
        // old and new symbols of different type but same name, throw exception
        //
        if(localSTE instanceof StructureSize){
            throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for Size in Structure '" + ((StructureSize) localSTE).getStructure().getName() + "'", e);
        } else if(localSTE instanceof MembraneVoltage){
            throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for Voltage Context in Structure '" + ((MembraneVoltage) localSTE).getMembrane().getName() + "'", e);
        } else if(localSTE instanceof SpeciesContext){
            throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for Species Context in Structure '" + ((SpeciesContext) localSTE).getStructure().getName() + "'", e);
        } else if(localSTE instanceof MembraneVoltage){
            throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for Membrane Voltage in Membrane '" + ((Membrane.MembraneVoltage) localSTE).getMembrane().getName() + "'", e);
        } else if(localSTE instanceof StructureSize){
            throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for Structure Size in Structure '" + ((Structure.StructureSize) localSTE).getStructure().getName() + "'", e);
        } else if(localSTE instanceof ModelParameter){
            throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for Model Parameter", e);
        } else if(localSTE instanceof ReservedSymbol){
            throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for a reserved symbol (e.g. 'x','y','z','t','KMOLE','_T_','_F_','_R_', ...)", e);
        } else {
            throw new ModelPropertyVetoException("conflict with " + symbolDescription + " '" + newSymbolName + "', name already used for a '" + localSTE.getClass().getName() + "' in context '" + localSTE.getNameScope().getName() + "'", e);
        }

    }


    public void getLocalEntries(Map<String, SymbolTableEntry> entryMap){
        for(SymbolTableEntry ste : speciesContexts){
            entryMap.put(ste.getName(), ste);
        }
        for(Structure s : structures){
            Structure.StructureSize structureSize = s.getStructureSize();
            entryMap.put(structureSize.getName(), structureSize);
            if(s instanceof Membrane){
                Membrane.MembraneVoltage membraneVoltage = ((Membrane) s).getMembraneVoltage();
                entryMap.put(membraneVoltage.getName(), membraneVoltage);
            }
        }
        for(SymbolTableEntry ste : modelParameters){
            entryMap.put(ste.getName(), ste);
        }

        for(Model.ModelFunction ste : fieldModelFunctions){
            entryMap.put(ste.getName(), ste);
        }

        for(ReservedSymbol rs : fieldReservedSymbols){
            if((rs != getX()) || (rs != getY()) || (rs != getZ())){
                entryMap.put(rs.getName(), rs);
            }
        }

        for(RbmObservable observable : rbmModelContainer.getObservableList()){
            entryMap.put(observable.getName(), observable);
        }

    }


    public void getEntries(Map<String, SymbolTableEntry> entryMap){
        getNameScope().getExternalEntries(entryMap);
    }


    public VCMetaData getVcMetaData(){
        return vcMetaData;
    }


    public void setVcMetaData(VCMetaData vcMetaData){
        this.vcMetaData = vcMetaData;
    }

    public void populateVCMetadata(boolean bMetadataPopulated){
        // populate free text for identifiables (species, reactionSteps, structures)
        if(bMetadataPopulated) return;
        for (Species value : species) {
            vcMetaData.setFreeTextAnnotation(value, value.getAnnotation());
            if (value.getDBSpecies() == null) continue;
            DBSpecies dbSpecies = value.getDBSpecies();
            try {
                if (dbSpecies.getFormalSpeciesInfo().getFormalSpeciesType().equals(FormalSpeciesType.compound)) {
                    //urn:miriam:kegg.compound
                    MiriamResource resource = vcMetaData.getMiriamManager().createMiriamResource(
                            "urn:miriam:kegg.compound:" + dbSpecies.getFormalSpeciesInfo().getFormalID());
                    Set<MiriamResource> miriamResources = new HashSet<MiriamResource>();
                    miriamResources.add(resource);
                    vcMetaData.getMiriamManager().addMiriamRefGroup(value, MIRIAMQualifier.BIO_isVersionOf,
                            miriamResources);
                } else if (dbSpecies.getFormalSpeciesInfo().getFormalSpeciesType().equals(FormalSpeciesType.enzyme)) {
                    //urn:miriam:ec-code
                    MiriamResource resource = vcMetaData.getMiriamManager().createMiriamResource(
                            "urn:miriam:ec-code:" + dbSpecies.getFormalSpeciesInfo().getFormalID());
                    Set<MiriamResource> miriamResources = new HashSet<MiriamResource>();
                    miriamResources.add(resource);
                    vcMetaData.getMiriamManager().addMiriamRefGroup(value, MIRIAMQualifier.BIO_isVersionOf,
                            miriamResources);

                } else if (dbSpecies.getFormalSpeciesInfo().getFormalSpeciesType().equals(FormalSpeciesType.protein)) {
                    MiriamResource resource = vcMetaData.getMiriamManager().createMiriamResource(
                            "urn:miriam:uniprot:" + dbSpecies.getFormalSpeciesInfo().getFormalID());
                    Set<MiriamResource> miriamResources = new HashSet<MiriamResource>();
                    miriamResources.add(resource);
                    vcMetaData.getMiriamManager().addMiriamRefGroup(value, MIRIAMQualifier.BIO_isVersionOf,
                            miriamResources);
                }
            } catch (Exception e) {
                lg.error(e.getMessage(), e);
            }
        }
        for (ReactionStep reactionStep : reactionSteps) {
            vcMetaData.setFreeTextAnnotation(reactionStep, reactionStep.getAnnotation());
        }

//		// No annotation in structures for the moment.
//		for (int i = 0; i < fieldStructures.length; i++) {
//			vcMetaData.setFreeTextAnnotation(fieldStructures[i], fieldStructures[i].getAnnotation());
//		}
    }

    public final RbmModelContainer getRbmModelContainer(){
        return rbmModelContainer;
    }

    /**
     * This method is modified on Nov 20, 2007. We got to go through the MassActionSolver and FluxSolver here to make sure that everything
     * is being checked before processing further. However, this makes the function become heavy.
     * The function is being referened in four different places, which are ClientRequestManager.runSimulations(), BioModelEditor.newApplication(),
     * SimulationContext.SimulationContext(SimulationContext, boolean) and StochMathMapping.refreshMathDescription().
     * Creation date: (11/16/2006 4:55:16 PM)
     *
     * @return java.lang.String
     */
    public String isValidForStochApp(){
        String returnStr = ""; //sum of all the issues
        String exceptionReacStr = ""; //exception msg from MassActionSolver when parsing kinetics for reactions/fluxes.
        String unTransformableStr = ""; //all untransformable reactions/fluxes, which have kinetic laws rather than General and MassAction.
        cbit.vcell.model.ReactionStep[] reacSteps = getReactionSteps();
        // Mass Action and centain form of general Flux can be automatically transformed.
        for(int i = 0; (reacSteps != null) && (i < reacSteps.length); i++){
            int numCatalyst = 0;
            ReactionParticipant[] reacParts = reacSteps[i].getReactionParticipants();
            for(ReactionParticipant rp : reacParts){
                if(rp instanceof Catalyst){
                    numCatalyst++;
                }
            }
            if((reacParts.length - numCatalyst) > 0){
                if(((reacSteps[i] instanceof SimpleReaction) &&
                        !reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction) &&
                        !reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General) &&
                        !reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.Macroscopic_irreversible) &&
                        !reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.Microscopic_irreversible))
                        ||
                        ((reacSteps[i] instanceof FluxReaction) &&
                                !reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General) &&
                                !reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.GeneralPermeability))){
                    unTransformableStr = unTransformableStr + " " + reacSteps[i].getName() + ",";
                } else {
                    if(reacSteps[i].getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL || reacSteps[i].getPhysicsOptions() == ReactionStep.PHYSICS_ELECTRICAL_ONLY){
                        unTransformableStr = unTransformableStr + " " + reacSteps[i].getName() + "(has electric current),";
                    } else {
                        if(reacSteps[i] instanceof SimpleReaction){
                            Expression rateExp = reacSteps[i].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
                            if(reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction) ||
                                    reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General)){
                                try {
                                    MassActionSolver.solveMassAction(null, null, rateExp, reacSteps[i]);
                                } catch(Exception e){
                                    exceptionReacStr = exceptionReacStr + " " + reacSteps[i].getName() + " error: " + e.getMessage() + "\n";
                                    unTransformableStr = unTransformableStr + " " + reacSteps[i].getName() + ",";
                                }
                            }
                        } else { // flux described by General density function
                            if(reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General) ||
                                    reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.GeneralPermeability)){
                                Expression rateExp = reacSteps[i].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
                                try {
                                    MassActionSolver.solveMassAction(null, null, rateExp, (FluxReaction) reacSteps[i]);
                                } catch(Exception e){
                                    exceptionReacStr = exceptionReacStr + " " + reacSteps[i].getName() + " error: " + e.getMessage() + "\n";
                                    unTransformableStr = unTransformableStr + " " + reacSteps[i].getName() + ",";
                                }
                            }
                        }
                    }
                }
            }
        }

        if(!unTransformableStr.isEmpty()){
            returnStr = returnStr + unTransformableStr.substring(0, (unTransformableStr.length() - 1)) + " are unable to be intepreted to mass action form. \nTherefore," +
                    "they are not suitable for stochastic application.\n\n" +
                    "Reactions described by mass action law(all) or General law(certain forms),\n" +
                    "or fluxes described by general desity function(certain forms) and general\n" +
                    "permeability(certain forms) can be interpreted to mass action form.\n\n" +
                    "All rate laws that include electric current are not suitable for stochastic application.";
        }
        if(!exceptionReacStr.isEmpty()){
            returnStr = returnStr + "\n\n detailed error:\n\n" + exceptionReacStr;
        }
        return returnStr;
    }

    public void removeObject(Object object) throws PropertyVetoException{
        if(object instanceof Feature || object instanceof Membrane){
            removeStructure((Structure) object, false);
        } else if(object instanceof ModelParameter){
            removeModelParameter((ModelParameter) object);
        } else if(object instanceof ReactionStep){
            removeReactionStep((ReactionStep) object);
        } else if(object instanceof Species){
            removeSpecies((Species) object);
        } else if(object instanceof SpeciesContext){
            removeSpeciesContext((SpeciesContext) object);
        }
    }

    public ModelUnitSystem getUnitSystem(){
        return unitSystem;
    }

    public Membrane createMembrane(){
        int count = 0;
        String membraneName = null;
        while (true) {
            membraneName = "m" + count;
            if(getStructure(membraneName) == null){
                break;
            }
            count++;
        }
        try {
            return addMembrane(membraneName);
        } catch(ModelException | PropertyVetoException e){
            throw new RuntimeException(e.getMessage(), e);
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


     public RateRuleVariable addRateRuleVariable(RateRuleVariable rateRuleVar) throws ModelPropertyVetoException {

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

     public void removeRateRuleVariable(RateRuleVariable rateRuleVar) throws ModelPropertyVetoException {
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

     public RateRuleVariable createRateRuleVariable(Structure structure) throws ModelPropertyVetoException {
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
     } catch (ModelPropertyVetoException e) {
     lg.error(e);
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