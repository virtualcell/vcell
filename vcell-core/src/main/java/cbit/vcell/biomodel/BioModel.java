/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.util.*;
import java.util.function.Consumer;

import cbit.image.VCImage;
import cbit.vcell.mapping.*;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.model.rbm.MolecularType;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.PathwayModel;
import org.vcell.relationship.RelationshipModel;
import org.vcell.relationship.RelationshipObject;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.util.*;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.document.*;
import org.vcell.util.document.BioModelChildSummary.MathType;

import cbit.image.ImageException;
import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.Structure.SpringStructureEnum;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.Simulation;

/**
 * Insert the type's description here.
 * Creation date: (10/17/00 3:12:16 PM)
 *
 * @author:
 */
public class BioModel implements VCDocument, Matchable, VetoableChangeListener, PropertyChangeListener,
        Identifiable, IdentifiableProvider, IssueSource, Displayable, VCellSbmlName {
    public static final String PROPERTY_NAME_SIMULATION_CONTEXTS = "simulationContexts";
    public final static String SIMULATION_CONTEXT_DISPLAY_NAME = "Application";
    public final static String SIMULATION_DISPLAY_NAME = "Simulation";
    private Version version = null;
    private String name;
    private String sbmlId = null;
    private String sbmlName = null;

    protected transient VetoableChangeSupport vetoPropertyChange;
    protected transient PropertyChangeSupport propertyChange;
    private Model model = null;
    private final List<SimulationContext> simulationContexts = new ArrayList<>();
    private final List<Simulation> simulations = new ArrayList<>();
    private String fieldDescription = "";
    private VCMetaData vcMetaData;

    private final PathwayModel pathwayModel = new PathwayModel();
    private final RelationshipModel relationshipModel = new RelationshipModel();
    private static final Logger lg = LogManager.getLogger(BioModel.class);

    public BioModel(Version version, ModelUnitSystem modelUnitSystem){
        super();
        this.name = "NoName";
        this.vcMetaData = new VCMetaData(this, null);
        this.setModel(new Model("unnamed", modelUnitSystem));
        this.addVetoableChangeListener(this);
        this.addPropertyChangeListener(this);
        try {
            this.setVersion(version);
        } catch(PropertyVetoException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * BioModel constructor comment.
     */
    public BioModel(Version version){
        this(version, ModelUnitSystem.createDefaultVCModelUnitSystem());
    }

    public void transformLumpedToDistributed(){
        try {
            for(ReactionStep reactionStep : getModel().getReactionSteps()){
                if(!reactionStep.getKinetics().getKineticsDescription().isLumped()) continue;
                Kinetics origKinetics = reactionStep.getKinetics();
                // clone it for backup purposes
                origKinetics.setReactionStep(null);
                Kinetics clonedKinetics = (Kinetics) BeanUtils.cloneSerializable(origKinetics);
                origKinetics.setReactionStep(reactionStep);
                try {
                    DistributedKinetics.toDistributedKinetics((LumpedKinetics) origKinetics, false);
                    lg.info("transformed lumped reaction " + reactionStep.getName() + " to distributed");
                } catch(Exception e){
                    lg.warn("failed to transform lumped reaction " + reactionStep.getName() + " to distributed: " + e.getMessage());
                    // original kinetics may have been altered when the conversion failed, replace with clone
                    reactionStep.setKinetics(clonedKinetics);
                    clonedKinetics.setReactionStep(reactionStep);
                    this.refreshDependencies();
                }
            }
        } catch(Exception e){
            throw new SBMLImportException("failed to convert lumped reaction kinetics to distributed: " + e.getMessage(), e);
        }
    }

    public SimulationContext addNewSimulationContext(String newSimulationContextName, SimulationContext.Application app) throws java.beans.PropertyVetoException, ExpressionException, GeometryException, ImageException, IllegalMappingException, MappingException{
        Geometry geometry;
        if(Application.SPRINGSALAD == app){
            boolean compatibleCompartments = getModel().getStructures().length == 3;
            // springsalad needs exactly 3 compartments
            for(Structure struct : getModel().getStructures()){
                String name = struct.getName();
                if(!Structure.springStructureSet.contains(name)){
                    compatibleCompartments = false;    // names are hardcoded in SpringStructureEnum
                    break;
                }
            }
            if(!compatibleCompartments){    // just make a minimal geometry, we'll complain elsewhere
                geometry = new Geometry("non-spatial", 0);
                SimulationContext simContext = new SimulationContext(getModel(), geometry, null, null, app);
                simContext.setName(newSimulationContextName);
                addSimulationContext(simContext);
                return simContext;
            }

            geometry = new Geometry("spatial", 3);
            GeometrySpec geometrySpec = geometry.getGeometrySpec();
            geometrySpec.setOrigin(new org.vcell.util.Origin(0, 0, 0));
            geometrySpec.setExtent(new org.vcell.util.Extent(0.1, 0.1, 0.1));
            geometrySpec.addSubVolume(new AnalyticSubVolume(SpringStructureEnum.Intracellular.columnName, new cbit.vcell.parser.Expression("z<0.09")));
            geometrySpec.addSubVolume(new AnalyticSubVolume(SpringStructureEnum.Extracellular.columnName, new cbit.vcell.parser.Expression(1.0)));
            cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geometry.getGeometrySurfaceDescription());

            SimulationContext simContext = new SimulationContext(getModel(), geometry, null, null, app);
            simContext.setName(newSimulationContextName);
            addSimulationContext(simContext);

            Double charSize = simContext.getCharacteristicSize();
            simContext.setCharacteristicSize(charSize / 2.0);
            GeometryContext geoContext = simContext.getGeometryContext();
            Model model = geoContext.getModel();
            cbit.vcell.model.Structure[] structures = model.getStructures();
            for (Structure structure : structures) {                    // map the compartments
                if (!(structure instanceof Feature feature)) continue;
                String subVolumeType = feature.getName().equals(SpringStructureEnum.Extracellular.columnName) ?
                        SpringStructureEnum.Extracellular.columnName : SpringStructureEnum.Intracellular.columnName;
                geoContext.assignStructure(feature, geometrySpec.getSubVolume(subVolumeType));
            }
            GeometrySurfaceDescription geometrySurfaceDescription = geometry.getGeometrySurfaceDescription();
            geometrySurfaceDescription.updateAll();
            //SurfaceClass[] surfaceClasses = geometrySurfaceDescription.getSurfaceClasses();
            for (Structure structure : structures) {                    // map the membrane
                if (!(structure instanceof Membrane membrane)) continue;
                SubVolume svIntra = geometrySpec.getSubVolume(SpringStructureEnum.Intracellular.columnName);
                SubVolume svExtra = geometrySpec.getSubVolume(SpringStructureEnum.Extracellular.columnName);
                SurfaceClass surfaceClass = geometrySurfaceDescription.getSurfaceClass(svIntra, svExtra);
                geoContext.assignStructure(membrane, surfaceClass);
            }
            return simContext;
        } else {
            geometry = new Geometry("non-spatial", 0);
            SimulationContext simContext = new SimulationContext(getModel(), geometry, null, null, app);
            simContext.setName(newSimulationContextName);
            addSimulationContext(simContext);
            return simContext;
        }
    }

    public record VersionableInfo(VersionableType versionableType, String name, KeyValue key) {
        @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof VersionableInfo otherInfo)) return false;
                return Objects.equals(this.versionableType, otherInfo.versionableType)
                        && Objects.equals(this.name, otherInfo.name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(this.versionableType, this.name);
            }

            @Override
            public String toString() {
                return this.versionableType.getTypeName() + "(" + this.name + "):" + this.key;
            }
        }

    public List<VersionableInfo> gatherChildVersionableInfos(){
        Set<VersionableInfo> versionSet = new LinkedHashSet<>();
        versionSet.add(new VersionableInfo(VersionableType.Model, getModel().getName(), getModel().getKey()));
        for(SimulationContext sc : getSimulationContextsAsArray()){
            versionSet.add(new VersionableInfo(VersionableType.SimulationContext, sc.getName(), sc.getKey()));
            if(sc.getGeometry() != null){
                Geometry geo = sc.getGeometry();
                versionSet.add(new VersionableInfo(VersionableType.Geometry, geo.getName(), geo.getKey()));
                if(geo.getGeometrySpec().getImage() != null){
                    VCImage img = geo.getGeometrySpec().getImage();
                    versionSet.add(new VersionableInfo(VersionableType.VCImage, img.getName(), img.getKey()));
                }
            }
            if(sc.getMathDescription() != null){
                MathDescription math = sc.getMathDescription();
                versionSet.add(new VersionableInfo(VersionableType.MathDescription, math.getName(), math.getKey()));
            }
        }
        for(Simulation sim : getSimulationsAsArray()){
            versionSet.add(new VersionableInfo(VersionableType.VCImage, sim.getName(), sim.getKey()));
        }
        return new ArrayList<>(versionSet);
    }

    public final static class ClearVersion implements Consumer<Versionable> {
        @Override
        public void accept(Versionable versionable){
            versionable.clearVersion();
        }
    }

    public void visitChildVersionables(Consumer<Versionable> operation){
        operation.accept(getModel());
        for(SimulationContext sc : getSimulationContextsAsArray()){
            operation.accept(sc);
            if(sc.getGeometry() != null){
                operation.accept(sc.getGeometry());
                if(sc.getGeometry().getGeometrySpec().getImage() != null){
                    operation.accept(sc.getGeometry().getGeometrySpec().getImage());
                }
            }
            if(sc.getMathDescription() != null){
                operation.accept(sc.getMathDescription());
            }
        }
        for(Simulation sim : getSimulationsAsArray()){
            operation.accept(sim);
        }
    }

    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener){
        getPropertyChange().addPropertyChangeListener(listener);
    }


    public void addSimulation(Simulation simulation) throws java.beans.PropertyVetoException{
        if(this.contains(simulation)){
            throw new IllegalArgumentException("BioModel.addSimulation() simulation already present in BioModel");
        }
        this.getSimulations().add(simulation);
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/19/01 3:31:00 PM)
     *
     * @param simulationContext cbit.vcell.mapping.SimulationContext
     * @throws java.beans.PropertyVetoException The exception description.
     */
    public void addSimulationContext(SimulationContext simulationContext) throws java.beans.PropertyVetoException{
        if(contains(simulationContext)){
            throw new IllegalArgumentException("BioModel.addSimulationContext() simulationContext already present in BioModel");
        }
        this.getSimulationContexts().add(simulationContext);
    }


    /**
     * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener){
        this.getVetoPropertyChange().addVetoableChangeListener(listener);
    }


    /**
     * Insert the method's description here.
     * Creation date: (4/24/2003 3:39:06 PM)
     */
    public void clearVersion(){
        this.version = null;
    }


    /**
     * Insert the method's description here.
     * Creation date: (11/29/00 2:11:43 PM)
     *
     * @param obj cbit.util.Matchable
     * @return boolean
     */
    public boolean compareEqual(Matchable obj){
        if(!(obj instanceof BioModel bioModel)) return false;

        return Compare.isEqualOrNull(this.getName(), bioModel.getName())
                && Compare.isEqualOrNull(this.getSbmlName(), bioModel.getSbmlName())
                && Compare.isEqualOrNull(this.getSbmlId(), bioModel.getSbmlId())
                && Compare.isEqualOrNull(this.getDescription(), bioModel.getDescription())
                && this.getModel().compareEqual(bioModel.getModel())
                && this.getPathwayModel().compare((HashSet<BioPaxObject>) bioModel.getPathwayModel().getBiopaxObjects())
                && this.getRelationshipModel().compare((HashSet<RelationshipObject>) bioModel.getRelationshipModel().getRelationshipObjects(), bioModel)
                && Compare.isEqualOrNull(getSimulationContextsAsArray(), bioModel.getSimulationContextsAsArray())
                && Compare.isEqualOrNull(getSimulationsAsArray(), bioModel.getSimulationsAsArray())
                && this.getVCMetaData().compareEquals(bioModel.getVCMetaData());
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/17/01 12:51:23 PM)
     *
     * @param simulationContext cbit.vcell.mapping.SimulationContext
     * @return boolean
     */
    public boolean contains(SimulationContext simulationContext){
        try{
            return this.getSimulationContexts().contains(simulationContext);
        } catch (NullPointerException e){
            throw new IllegalArgumentException("simulationContext was null");
        }
    }


    public boolean contains(Simulation simulation){
        try {
            return this.getSimulations().contains(simulation);
        } catch (NullPointerException e){
            throw new IllegalArgumentException("simulation was null");
        }
    }

    public BioModelChildSummary createBioModelChildSummary(){

        SimulationContext[] simContexts = getSimulationContextsAsArray();

        String[] scNames = new String[simContexts.length];
        MathType[] appTypes = new MathType[simContexts.length];
        String[] scAnnots = new String[simContexts.length];
        String[] geoNames = new String[simContexts.length];
        int[] geoDims = new int[simContexts.length];
        String[][] simNames = new String[simContexts.length][];
        String[][] simAnnots = new String[simContexts.length][];

        for(int i = 0; i < simContexts.length; i += 1){
            scNames[i] = simContexts[i].getName();
            appTypes[i] = simContexts[i].getMathType();
            scAnnots[i] = simContexts[i].getDescription();
            geoNames[i] = simContexts[i].getGeometry().getName();
            geoDims[i] = simContexts[i].getGeometry().getDimension();

            Simulation[] sims = simContexts[i].getSimulations();
            simNames[i] = new String[sims.length];
            simAnnots[i] = new String[sims.length];
            for(int j = 0; j < sims.length; j += 1){
                simNames[i][j] = sims[j].getName();
                simAnnots[i][j] = sims[j].getDescription();
            }
        }
        return new BioModelChildSummary(scNames, appTypes, scAnnots, simNames, simAnnots, geoNames, geoDims);
    }

    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue){
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * The fireVetoableChange method was generated to support the vetoPropertyChange field.
     */
    public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException{
        this.getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
    }


    /**
     * Insert the method's description here.
     * Creation date: (3/18/2004 1:54:51 PM)
     *
     * @param newVersion cbit.sql.Version
     */
    public void forceNewVersionAnnotation(Version newVersion) throws PropertyVetoException{
        if(this.getVersion().getVersionKey().equals(newVersion.getVersionKey())){
            this.setVersion(newVersion);
        } else {
            throw new RuntimeException("biomodel.forceNewVersionAnnotation failed : version keys not equal");
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/12/2004 10:38:12 PM)
     *
     * @param issueList java.util.Vector
     */
    @Override
    public void gatherIssues(IssueContext issueContext, List<Issue> issueList){
        issueContext = issueContext.newChildContext(ContextType.BioModel, this);
        getModel().gatherIssues(issueContext, issueList);
        if(getNumSimulations() > 1000){
            String message = "VCell BioModels cannot have more than 1000 simulations in total across all applications";
            issueList.add(new Issue(this, issueContext, IssueCategory.InternalError, message, Issue.Severity.ERROR));
        }
        for(SimulationContext simulationContext : this.simulationContexts){
            boolean bIgnoreMathDescription = false;
            simulationContext.gatherIssues(issueContext, issueList, bIgnoreMathDescription);
        }
        if(this.sbmlName != null && this.sbmlName.isEmpty()){
            String message = "SbmlName cannot be an empty string.";
            issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
        }
        if(this.sbmlId != null && this.sbmlId.isEmpty()){
            String message = "SbmlId cannot be an empty string.";
            issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
        }
    }


    /**
     * Gets the description property (java.lang.String) value.
     *
     * @return The description property value.
     * @see #setDescription
     */
    @Deprecated
    public java.lang.String getDescription(){
        return this.fieldDescription;
    }


    /**
     * Insert the method's description here.
     * Creation date: (5/28/2004 3:13:04 PM)
     *
     * @return int
     */
    public VCDocumentType getDocumentType(){
        return VCDocumentType.BIOMODEL_DOC;
    }


    /**
     * Gets the model property (cbit.vcell.model.Model) value.
     *
     * @return The model property value.
     * @see #setModel
     */
    public cbit.vcell.model.Model getModel(){
        return this.model;
    }


    /**
     * Gets the name property (java.lang.String) value.
     *
     * @return The name property value.
     * @see #setName
     */
    public String getName(){
        return this.name;
    }

    public String getSbmlId(){
        return this.sbmlId;
    }

    public String getSbmlName(){
        return this.sbmlName;
    }

    /**
     * Insert the method's description here.
     * Creation date: (11/29/00 2:15:36 PM)
     *
     * @return int
     */
    public int getNumSimulationContexts(){
        if(getSimulationContextsAsArray() == null){
            return 0;
        }
        return getSimulationContextsAsArray().length;
    }


    /**
     * Insert the method's description here.
     * Creation date: (11/29/00 2:15:36 PM)
     *
     * @return int
     */
    public int getNumSimulations(){
        if(getSimulationsAsArray() == null){
            return 0;
        }
        return getSimulationsAsArray().length;
    }


    /**
     * Accessor for the propertyChange field.
     */
    protected java.beans.PropertyChangeSupport getPropertyChange(){
        if(this.propertyChange == null){
            this.propertyChange = new java.beans.PropertyChangeSupport(this);
        }
        return this.propertyChange;
    }


    public SimulationContext getSimulationContext(Simulation simulation) throws ObjectNotFoundException{
        if(simulation == null){
            throw new IllegalArgumentException("simulation was null");
        }
        if(!contains(simulation)){
            throw new IllegalArgumentException("simulation doesn't belong to this BioModel");
        }
        SimulationContext[] simContexts = getSimulationContextsAsArray();
        if(simContexts == null){
            return null;
        }
        for (SimulationContext simContext : simContexts) {
            if (simContext.getMathDescription() == simulation.getMathDescription()) {
                return simContext;
            }
        }
        throw new ObjectNotFoundException("could not find Application for simulation " + simulation.getName());
    }


    /**
     * Gets the simulationContexts property (cbit.vcell.mapping.SimulationContext[]) value as an array.
     *
     * @return The simulationContexts property value.
     * @see #setSimulationContexts
     */
    public SimulationContext[] getSimulationContextsAsArray(){
        return this.simulationContexts.toArray(SimulationContext[]::new);
    }

    /**
     * Gets the simulationContexts property (cbit.vcell.mapping.SimulationContext[]) value.
     *
     * @return The simulationContexts property value.
     * @see #setSimulationContexts
     */
    public List<SimulationContext> getSimulationContexts(){
        return this.simulationContexts;
    }


    /**
     * Gets the simulationContexts index property (cbit.vcell.mapping.SimulationContext) value.
     *
     * @param index The index value into the property array.
     * @return The simulationContexts property value.
     * @see #setSimulationContexts
     */
    public SimulationContext getSimulationContext(int index){
        return getSimulationContextsAsArray()[index];
    }

    public SimulationContext getSimulationContextsAsArray(String name){
        for(SimulationContext simContext : this.simulationContexts){
            if(simContext.getName().equals(name)){
                return simContext;
            }
        }
        return null;
    }

    public boolean hasSimulation(String simName){
        for(Simulation candidate : getSimulationsAsArray()){
            if(candidate.getName().contentEquals(simName)){
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the simulations property (cbit.vcell.solver.Simulation[]) value as an array.
     *
     * @return The simulations property value.
     * @see #setSimulations
     */
    public Simulation[] getSimulationsAsArray(){
        return this.simulations.toArray(Simulation[]::new);
    }

    /**
     * Gets the simulations property (cbit.vcell.solver.Simulation[]) value as an array.
     *
     * @return The simulations property value.
     * @see #setSimulations
     */
    public List<Simulation> getSimulations(){
        return this.simulations;
    }


    /**
     * Gets the simulations index property (cbit.vcell.solver.Simulation) value.
     *
     * @param index The index value into the property array.
     * @return The simulations property value.
     * @see #setSimulations
     */
    public Simulation getSimulation(int index){
        return getSimulationsAsArray()[index];
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/17/01 12:59:40 PM)
     *
     * @param simulationContext cbit.vcell.mapping.SimulationContext
     * @return cbit.vcell.solver.Simulation[]
     */
    public Simulation[] getSimulationsAsArray(SimulationContext simulationContext){
        if(simulationContext == null){
            throw new IllegalArgumentException("simulationContext was null");
        }
        if(!contains(simulationContext)){
            throw new IllegalArgumentException("simulationContext doesn't belong to this BioModel");
        }
        Simulation[] sims = getSimulationsAsArray();
        if(sims == null){
            return null;
        }
        Vector<Simulation> scSimList = new Vector<>();
        for (Simulation sim : sims) {
            if (sim.getMathDescription() == simulationContext.getMathDescription()) {
                scSimList.addElement(sim);
            }
        }
        Simulation[] scSimArray = new Simulation[scSimList.size()];
        scSimList.copyInto(scSimArray);
        return scSimArray;
    }


    /**
     * Gets the version property (cbit.sql.Version) value.
     *
     * @return The version property value.
     */
    public Version getVersion(){
        return this.version;
    }


    /**
     * Accessor for the vetoPropertyChange field.
     */
    protected java.beans.VetoableChangeSupport getVetoPropertyChange(){
        if(this.vetoPropertyChange == null){
            this.vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
        }
        return this.vetoPropertyChange;
    }


    /**
     * The hasListeners method was generated to support the propertyChange field.
     */
    public synchronized boolean hasListeners(java.lang.String propertyName){
        return getPropertyChange().hasListeners(propertyName);
    }


    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt){

        //
        // propagate mathDescription changes from SimulationContexts to Simulations
        //
        if(evt.getSource() instanceof SimulationContext && evt.getPropertyName().equals("mathDescription") && evt.getNewValue() != null){
            if(this.simulations != null){
                for (Simulation simulation : this.simulations) {
                    if (simulation.getMathDescription() != evt.getOldValue()) continue;
                    try {
                        simulation.setMathDescription((MathDescription) evt.getNewValue());
                    } catch (PropertyVetoException e) {
                        lg.error("error propagating math from SimulationContext '" + ((SimulationContext) evt.getSource()).getName() + "' " +
                                "to Simulation '" + simulation.getName(), e);
                    }
                }
            }
        }

        //
        // make sure that simulations and simulationContexts are listened to
        //
        if(evt.getSource() == this && evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_SIMULATIONS) && evt.getNewValue() != null){
            //
            // unregister for old
            //
            if(evt.getOldValue() != null){
                Simulation[] simulations = (Simulation[]) evt.getOldValue();
                for (Simulation simulation : simulations) {
                    simulation.removeVetoableChangeListener(this);
                    simulation.removePropertyChangeListener(this);
                }
            }
            //
            // register for new
            //
            if(evt.getOldValue() != null){
                Simulation[] simulations = (Simulation[]) evt.getNewValue();
                for (Simulation simulation : simulations) {
                    simulation.addVetoableChangeListener(this);
                    simulation.addPropertyChangeListener(this);
                }
            }
        }
        if(evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_CONTEXTS) && evt.getNewValue() != null){
            //
            // unregister for old
            //
            if(evt.getOldValue() != null){
                SimulationContext[] simulationContexts = (SimulationContext[]) evt.getOldValue();
                for (SimulationContext simulationContext : simulationContexts) {
                    simulationContext.removeVetoableChangeListener(this);
                    simulationContext.removePropertyChangeListener(this);
                }
            }
            //
            // register for new
            //
            if(evt.getOldValue() != null){
                SimulationContext[] simulationContexts = (SimulationContext[]) evt.getNewValue();
                for (SimulationContext simulationContext : simulationContexts) {
                    simulationContext.addVetoableChangeListener(this);
                    simulationContext.addPropertyChangeListener(this);
                }
            }
        }

        if(evt.getSource() == this.model && (evt.getPropertyName().equals(Model.PROPERTY_NAME_SPECIES_CONTEXTS)
                || evt.getPropertyName().equals(Model.PROPERTY_NAME_REACTION_STEPS))){
            //remove the relationship objects if the biomodelEntity objects were removed
            Set<BioModelEntityObject> removedObjects = this.relationshipModel.getBioModelEntityObjects();
            for(SpeciesContext sc : this.model.getSpeciesContexts()){
                removedObjects.remove(sc);
            }
            for(ReactionStep rs : this.model.getReactionSteps()){
                removedObjects.remove(rs);
            }
            for(MolecularType mt : this.model.getRbmModelContainer().getMolecularTypeList()){
                removedObjects.remove(mt);
            }
            for(ReactionRule rr : this.model.getRbmModelContainer().getReactionRuleList()){
                removedObjects.remove(rr);
            }
            this.relationshipModel.removeRelationshipObjects(removedObjects);
        }
        // adjust the relationship model when a molecule gets deleted
        if(evt.getSource() == this.model && (evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST))){
            @SuppressWarnings("unchecked")
            List<MolecularType> oldListCopy = new ArrayList<>((List<MolecularType>) evt.getOldValue());
            @SuppressWarnings("unchecked")
            List<MolecularType> newList = (List<MolecularType>) evt.getNewValue();
            if(newList != null && oldListCopy != null && oldListCopy.size() > newList.size()){
                // something got deleted
                oldListCopy.removeAll(newList);
                for(MolecularType removedMt : oldListCopy){
                    this.relationshipModel.removeRelationshipObject(removedMt);
                }
            }
        }
        if(evt.getSource() == this.model && (evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_REACTION_RULE_LIST))){
            @SuppressWarnings("unchecked")
            List<ReactionRule> oldListCopy = new ArrayList<>((List<ReactionRule>) evt.getOldValue());
            @SuppressWarnings("unchecked")
            List<ReactionRule> newList = (List<ReactionRule>) evt.getNewValue();
            if(newList != null && oldListCopy != null && oldListCopy.size() > newList.size()){
                // something got deleted
                oldListCopy.removeAll(newList);
                for(ReactionRule removedRr : oldListCopy){
                    this.relationshipModel.removeRelationshipObject(removedRr);
                }
            }
        }
        if(evt.getSource() == this.model && (evt.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_ENTITY_NAME))){
            firePropertyChange(Model.PROPERTY_NAME_MODEL_ENTITY_NAME, evt.getOldValue(), evt.getNewValue());
        }

    }

    public void repairLegacyModelProblems(){
        BioModelTransforms.repairLegacyProblems(this);
    }

    /**
     * Insert the method's description here.
     * Creation date: (4/12/01 11:24:12 AM)
     */
    public void refreshDependencies(){
        this.repairLegacyModelProblems();
        // listen to self
        this.removePropertyChangeListener(this);
        this.removeVetoableChangeListener(this);
        this.addPropertyChangeListener(this);
        this.addVetoableChangeListener(this);

        this.model.refreshDependencies();
        this.model.setVcMetaData(getVCMetaData());
        for (SimulationContext simContext : this.getSimulationContexts()){
            simContext.setBioModel(this);
            simContext.removePropertyChangeListener(this);
            simContext.removeVetoableChangeListener(this);
            simContext.addPropertyChangeListener(this);
            simContext.addVetoableChangeListener(this);
            simContext.refreshDependencies();
        }
        for (Simulation sim : this.getSimulations()){
            sim.removePropertyChangeListener(this);
            sim.removeVetoableChangeListener(this);
            sim.addPropertyChangeListener(this);
            sim.addVetoableChangeListener(this);
            sim.refreshDependencies();
        }
        this.updateSimulationOwners();
    }


    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener){
        this.getPropertyChange().removePropertyChangeListener(listener);
    }


    public void removeSimulation(Simulation simulation) throws java.beans.PropertyVetoException{
        if(!this.contains(simulation)){
            throw new IllegalArgumentException("BioModel.removeSimulation() simulation not present in BioModel");
        }
        this.getSimulations().remove(simulation);
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/19/01 3:31:00 PM)
     *
     * @param simulationContext cbit.vcell.mapping.SimulationContext
     * @throws java.beans.PropertyVetoException The exception description.
     */
    public void removeSimulationContext(SimulationContext simulationContext) throws java.beans.PropertyVetoException{
        if(!contains(simulationContext)){
            throw new IllegalArgumentException("BioModel.removeSimulationContext() simulationContext not present in BioModel");
        }
        this.getSimulationContexts().remove(simulationContext);
    }


    /**
     * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener){
        this.getVetoPropertyChange().removeVetoableChangeListener(listener);
    }


    /**
     * Sets the description property (java.lang.String) value.
     *
     * @param description The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getDescription
     */
    @Deprecated
    public void setDescription(java.lang.String description) throws java.beans.PropertyVetoException{
        String oldValue = this.fieldDescription;
        fireVetoableChange("description", oldValue, description);
        this.fieldDescription = description;
        firePropertyChange("description", oldValue, description);
    }


    /**
     * Sets the model property (cbit.vcell.model.Model) value.
     *
     * @param model The new value for the property.
     * @see #getModel
     */
    public void setModel(Model model){
        Model oldValue = this.model;
        this.model = model;
        if(oldValue != null){
            oldValue.removePropertyChangeListener(this);
        }
        if(model != null){
            model.setVcMetaData(getVCMetaData());
            model.addPropertyChangeListener(this);
        }
        firePropertyChange("model", oldValue, model);
    }


    /**
     * Sets the name property (java.lang.String) value.
     *
     * @param name The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getName
     */
    public void setName(java.lang.String name) throws java.beans.PropertyVetoException{
        String oldValue = this.name;
        fireVetoableChange("name", oldValue, name);
        this.name = name;
        firePropertyChange("name", oldValue, name);
    }

    public void setSbmlName(String newString) throws PropertyVetoException{
        String oldValue = this.sbmlName;
        String newValue = SpeciesContext.fixSbmlName(newString);

        fireVetoableChange("sbmlName", oldValue, newValue);
        this.sbmlName = newValue;
        firePropertyChange("sbmlName", oldValue, newValue);
    }

    public void setSbmlId(String newString) throws PropertyVetoException{    // setable only through SBML import
        this.sbmlId = newString;
    }

    /**
     * Sets the simulationContexts property (cbit.vcell.mapping.SimulationContext[]) value.
     *
     * @param newSimulationContexts The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getSimulationContextsAsArray
     */
    public void setSimulationContexts(SimulationContext[] newSimulationContexts) throws java.beans.PropertyVetoException{
        SimulationContext[] oldSimContexts = this.getSimulationContextsAsArray();
        fireVetoableChange(PROPERTY_NAME_SIMULATION_CONTEXTS, oldSimContexts, newSimulationContexts);
        for (SimulationContext oldSimContext : oldSimContexts){
//            oldSimContext.removePropertyChangeListener(this);
//            oldSimContext.removeVetoableChangeListener(this);
            oldSimContext.setBioModel(null);
        }
        this.getSimulationContexts().clear();
        for (SimulationContext newSimContext : newSimulationContexts){
            this.addSimulationContext(newSimContext);
//            newSimContext.addPropertyChangeListener(this);
//            newSimContext.addVetoableChangeListener(this);
            newSimContext.setBioModel(this);
        }
        updateSimulationOwners();
        firePropertyChange(PROPERTY_NAME_SIMULATION_CONTEXTS, oldSimContexts, newSimulationContexts);
    }


    /**
     * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
     *
     * @param newSimulations The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getSimulationsAsArray
     */
    public void setSimulations(Simulation[] newSimulations) throws java.beans.PropertyVetoException{
        Simulation[] oldSimulations = this.getSimulationsAsArray();
        this.fireVetoableChange(PropertyConstants.PROPERTY_NAME_SIMULATIONS, oldSimulations, newSimulations);
        for (Simulation oldSim : oldSimulations){
            oldSim.removePropertyChangeListener(this);
            oldSim.removeVetoableChangeListener(this);
            oldSim.setSimulationOwner(null); // make sure old simulation instances have null simulationOwners
        }
        this.getSimulations().clear();
        for (Simulation newSim : newSimulations){
            this.addSimulation(newSim);
            newSim.addPropertyChangeListener(this);
            newSim.addVetoableChangeListener(this);
        }
        this.updateSimulationOwners();
        firePropertyChange(PropertyConstants.PROPERTY_NAME_SIMULATIONS, oldSimulations, newSimulations);
    }

    private void updateSimulationOwners(){
        for(Simulation sim : this.simulations){
            try {
                sim.setSimulationOwner(getSimulationContext(sim));
            } catch(ObjectNotFoundException e){
                sim.setSimulationOwner(null);
                lg.error(e);
            }
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (11/14/00 3:49:12 PM)
     *
     * @param version cbit.sql.Version
     */
    private void setVersion(Version version) throws PropertyVetoException{
        this.version = version;
        if(version != null){
            setName(version.getName());
            setDescription(version.getAnnot());
        }
    }


    /**
     * This method was created by a SmartGuide.
     *
     * @return java.lang.String
     */
    @Override
    public String toString(){
        String desc = (getVersion() == null) ? getName() : getVersion().toString();
        return "BioModel@" + Integer.toHexString(hashCode()) + "(" + desc + ")";
    }


    /**
     * This method gets called when a constrained property is changed.
     *
     * @param evt a <code>PropertyChangeEvent</code> object describing the
     *            event source and the property that has changed.
     * @throws PropertyVetoException if the recipient wishes the property
     *                               change to be rolled back.
     */
    public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException{
        //
        // don't let SimulationContext's MathDescription be set to null if any Simulations depend on it, can't recover from this
        //
        if(evt.getSource() instanceof SimulationContext && evt.getPropertyName().equals("mathDescription") && evt.getNewValue() == null){
            if(this.simulations != null){
                for (Simulation simulation : this.simulations) {
                    if (simulation.getMathDescription() == evt.getOldValue()) {
                        throw new PropertyVetoException("error: simulation " + simulation.getName() + " will be orphaned, MathDescription set to null for Application " + ((SimulationContext) evt.getSource()).getName(), evt);
                    }
                }
            }
        }
        //
        // don't let a Simulation's MathDescription be set to null, can't recover from this
        //
        if(evt.getSource() instanceof Simulation && evt.getPropertyName().equals("mathDescription") && evt.getNewValue() == null){
            throw new PropertyVetoException("error: Simulation " + ((Simulation) evt.getSource()).getName() + " will be orphaned (MathDescription set to null)", evt);
        }
        //
        // don't let a Simulation change it's MathDescription unless that MathDescription is from an Application.
        // note that SimulationContext's MathDescription is changed first, then Simulation's MathDescription is updated
        // this is ALWAYS the order of events.
        //
        if(evt.getSource() instanceof Simulation && evt.getPropertyName().equals("mathDescription")){
            MathDescription newMathDescription = (MathDescription) evt.getNewValue();
            if(this.simulationContexts != null){
                boolean bMathFound = false;
                for (SimulationContext simulationContext : this.simulationContexts) {
                    if (simulationContext.getMathDescription() == newMathDescription) {
                        bMathFound = true;
                        break;
                    }
                }
                if(!bMathFound){
                    throw new PropertyVetoException("error: simulation " + ((Simulation) evt.getSource()).getName() + " will be orphaned (MathDescription doesn't belong to any Application", evt);
                }
            }
        }
        if(evt.getSource() == this && evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_SIMULATIONS) && evt.getNewValue() != null){
            //
            // check for name duplication
            //
            Simulation[] simulations = (Simulation[]) evt.getNewValue();
            for(int i = 0; i < simulations.length - 1; i++){
                for(int j = i + 1; j < simulations.length; j++){
                    if(simulations[i].getName().equals(simulations[j].getName())){
                        throw new PropertyVetoException(VCellNames.getName(simulations[i]) + " with name " + simulations[i].getName() + " already exists", evt);
                    }
                }
            }
            //
            // check for Simulations that don't map to any SimulationContext
            //
            for(int i = 0; simulations != null && i < simulations.length; i++){
                boolean bFound = false;
                for (SimulationContext simContext : this.getSimulationContexts()){
                    if (simulations[i].getMathDescription() == simContext.getMathDescription()) {
                        bFound = true;
                        break;
                    }
                }
                if(!bFound){
                    throw new PropertyVetoException("Setting Simulations, Simulation \"" + simulations[i].getName() + "\" has no corresponding MathDescription (so no Application)", evt);
                }
            }
        }
        if(evt.getSource() instanceof Simulation && evt.getPropertyName().equals("name") && evt.getNewValue() != null){
            //
            // check for name duplication
            //
            String simulationName = (String) evt.getNewValue();
            for (Simulation simulation : this.getSimulations()) {
                if (simulation.getName().equals(simulationName)) {
                    throw new PropertyVetoException(VCellNames.getName(simulation) + " with name " + simulationName + " already exists", evt);
                }
            }
        }
        if(evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_CONTEXTS) && evt.getNewValue() != null){
            //
            // check for name duplication
            //
            SimulationContext[] simulationContexts = (SimulationContext[]) evt.getNewValue();
            for(int i = 0; i < simulationContexts.length - 1; i++){
                for(int j = i + 1; j < simulationContexts.length; j++){
                    if(simulationContexts[i].getName().equals(simulationContexts[j].getName())){
                        throw new PropertyVetoException(VCellNames.getName(simulationContexts[i]) + " with name " + simulationContexts[i].getName() + " already exists", evt);
                    }
                }
            }

            // check for Simulations that don't map to any SimulationContext
            for (Simulation sim : this.getSimulations()){
                boolean bFound = false;
                for(int j = 0; simulationContexts != null && j < simulationContexts.length; j++){
                    if (sim.getMathDescription() == simulationContexts[j].getMathDescription()) {
                        bFound = true;
                        break;
                    }
                }
                if(!bFound){
                    throw new PropertyVetoException("Setting SimulationContexts, Simulation \"" + sim.getName() + "\" has no corresponding MathDescription (so no Application)", evt);
                }
            }
        } else if(evt.getSource() == this && evt.getPropertyName().equals("sbmlId")){
            // not editable, we use what we get from the importer
        } else if(evt.getSource() == this && evt.getPropertyName().equals("sbmlName")){
            String newName = (String) evt.getNewValue();
            if(newName == null){
                return;
            }
        }
        if(evt.getSource() instanceof SimulationContext && evt.getPropertyName().equals("name") && evt.getNewValue() != null){
            // check for name duplication
            String simulationContextName = (String) evt.getNewValue();
            for (SimulationContext simulationContext : this.getSimulationContexts()) {
                if (simulationContext.getName().equals(simulationContextName)) {
                    throw new PropertyVetoException(VCellNames.getName(simulationContext) + " with name " + simulationContextName + " already exists", evt);
                }
            }
        }
        TokenMangler.checkNameProperty(this, "BioModel", evt);
    }

    public VCMetaData getVCMetaData(){
        return this.vcMetaData;
    }

    public void setVCMetaData(VCMetaData vcMetaData){
        this.vcMetaData = vcMetaData;
    }

    public void populateVCMetadata(boolean bMetadataPopulated){
        // (recursively) populate the identifiables with free text annotations if 'bMetaDataPopulated' is false
        if(!bMetadataPopulated){
            String annotationText = this.getDescription();
            this.vcMetaData.setFreeTextAnnotation(this, annotationText);

            // now populate from model downwards
            if(this.model != null){
                this.model.populateVCMetadata(bMetadataPopulated);
            }
        }
    }

    public Identifiable getIdentifiableObject(VCID vcid){
        if(vcid.getClassName().equals(VCID.CLASS_BIOPAX_OBJECT)){
            String rdfId = vcid.getLocalName();
            return getPathwayModel().findBioPaxObject(rdfId);
        }
        if(vcid.getClassName().equals(VCID.CLASS_SPECIES_CONTEXT)){
            String localName = vcid.getLocalName();
            return getModel().getSpeciesContext(localName);
        }
        if(vcid.getClassName().equals(VCID.CLASS_SPECIES)){
            String localName = vcid.getLocalName();
            return getModel().getSpecies(localName);
        }
        if(vcid.getClassName().equals(VCID.CLASS_STRUCTURE)){
            String localName = vcid.getLocalName();
            return getModel().getStructure(localName);
        }
        if(vcid.getClassName().equals(VCID.CLASS_REACTION_STEP)){
            String localName = vcid.getLocalName();
            return getModel().getReactionStep(localName);
        }
        if(vcid.getClassName().equals(VCID.CLASS_MOLECULE)){
            String localName = vcid.getLocalName();
            return getModel().getRbmModelContainer().getMolecularType(localName);
        }
        if(vcid.getClassName().equals(VCID.CLASS_REACTION_RULE)){
            String localName = vcid.getLocalName();
            return getModel().getRbmModelContainer().getReactionRule(localName);
        }
        if(vcid.getClassName().equals(VCID.CLASS_OBSERVABLE)){
            String localName = vcid.getLocalName();
            return getModel().getRbmModelContainer().getObservable(localName);
        }
        if(vcid.getClassName().equals(VCID.CLASS_MODEL_PARAMETER)){
            String localName = vcid.getLocalName();
            return getModel().getModelParameter(localName);
        }
        if(vcid.getClassName().equals(VCID.CLASS_APPLICATION)){
            String localName = vcid.getLocalName();
            return getSimulationContextsAsArray(localName);
        }
        if(vcid.getClassName().equals(VCID.CLASS_SPECIES_CONTEXT_SPEC_PARAMETER)){
            String localName = vcid.getLocalName();
            String[] tokens = localName.split("\\.");
            // localName = scsParam.getSimulationContext().getName()+"."+scsParam.getSpeciesContext().getName()+"."+scsParam.getName();
            SimulationContext simContext = getSimulationContext(tokens[0]);
            if(simContext != null){
                SpeciesContext sc = getModel().getSpeciesContext(tokens[1]);
                if(sc != null){
                    SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
                    if(scs != null){
                        return scs.getParameterFromName(tokens[2]);
                    }
                }
            }
        }
        if(vcid.getClassName().equals(VCID.CLASS_STRUCTURE_MAPPING_PARAMETER)){
            String localName = vcid.getLocalName();
            String[] tokens = localName.split("\\.");
            // localName = smParam.getSimulationContext().getName()+"."+smParam.getStructure().getName()+"."+smParam.getName();
            SimulationContext simContext = getSimulationContext(tokens[0]);
            if(simContext != null){
                Structure struct = getModel().getStructure(tokens[1]);
                if(struct != null){
                    StructureMapping sm = simContext.getGeometryContext().getStructureMapping(struct);
                    if(sm != null){
                        return sm.getParameter(tokens[2]);
                    }
                }
            }
        }
        if(vcid.getClassName().equals(VCID.CLASS_SIMULATION_CONTEXT_PARAMETER)){
            String localName = vcid.getLocalName();
            String[] tokens = localName.split("\\.");
            // localName = scParam.getSimulationContext().getName()+"."+scParam.getName();
            SimulationContext simContext = getSimulationContext(tokens[0]);
            if(simContext != null){
                return simContext.getSimulationContextParameter(tokens[1]);
            }
        }
        if(vcid.getClassName().equals(VCID.CLASS_KINETICS_PARAMETER)){
            String localName = vcid.getLocalName();
            String[] tokens = localName.split("\\.");
            // localName = kParam.getKinetics().getReactionStep().getName()+"."+kParam.getName();
            ReactionStep reactionStep = getModel().getReactionStep(tokens[0]);
            if(reactionStep != null){
                return reactionStep.getKinetics().getKineticsParameter(tokens[1]);
            }
        }
        if(vcid.getClassName().equals(VCID.CLASS_BIOMODEL)){
            return this;
        }
        return null;
    }

    public VCID getVCID(Identifiable identifiable){
        if(identifiable == null){
            return null;
        }
        String localName;
        String className;
        if(identifiable instanceof SpeciesContext){
            localName = ((SpeciesContext) identifiable).getName();
            className = VCID.CLASS_SPECIES_CONTEXT;
        } else if(identifiable instanceof Species){
            localName = ((Species) identifiable).getCommonName();
            className = VCID.CLASS_SPECIES;
        } else if(identifiable instanceof Structure){
            localName = ((Structure) identifiable).getName();
            className = VCID.CLASS_STRUCTURE;
        } else if(identifiable instanceof ReactionStep){
            localName = ((ReactionStep) identifiable).getName();
            className = VCID.CLASS_REACTION_STEP;
        } else if(identifiable instanceof BioModel){
            localName = ((BioModel) identifiable).getName();
            className = VCID.CLASS_BIOMODEL;
        } else if(identifiable instanceof BioPaxObject){
            localName = ((BioPaxObject) identifiable).getID();
            className = VCID.CLASS_BIOPAX_OBJECT;
        } else if(identifiable instanceof MolecularType){
            localName = ((MolecularType) identifiable).getName();
            className = VCID.CLASS_MOLECULE;
        } else if(identifiable instanceof ReactionRule){
            localName = ((ReactionRule) identifiable).getName();
            className = VCID.CLASS_REACTION_RULE;
        } else if(identifiable instanceof RbmObservable){
            localName = ((RbmObservable) identifiable).getName();
            className = VCID.CLASS_OBSERVABLE;
        } else if(identifiable instanceof Model.ModelParameter){
            localName = ((Model.ModelParameter) identifiable).getName();
            className = VCID.CLASS_MODEL_PARAMETER;
        } else if(identifiable instanceof SimulationContext){
            localName = ((SimulationContext) identifiable).getName();
            className = VCID.CLASS_APPLICATION;
        } else if(identifiable instanceof Simulation){
            localName = ((Simulation) identifiable).getName();
            className = VCID.CLASS_SIMULATION;
        } else if(identifiable instanceof SpeciesContextSpec){
            localName = ((SpeciesContextSpec) identifiable).getDisplayName();
            className = VCID.CLASS_SPECIES_CONTEXT_SPEC;
        } else if(identifiable instanceof SpeciesContextSpec.SpeciesContextSpecParameter scsParam){
            SimulationContext simContext = scsParam.getSimulationContext();
            if(simContext == null){
                throw new RuntimeException("Entity no longer exist");
            }
            localName = simContext.getName() + "." + scsParam.getSpeciesContext().getName() + "." + scsParam.getName();
            className = VCID.CLASS_SPECIES_CONTEXT_SPEC_PARAMETER;
        } else if(identifiable instanceof StructureMapping.StructureMappingParameter smParam){
            localName = smParam.getSimulationContext().getName() + "." + smParam.getStructure().getName() + "." + smParam.getName();
            className = VCID.CLASS_STRUCTURE_MAPPING_PARAMETER;
        } else if(identifiable instanceof SimulationContext.SimulationContextParameter scParam){
            localName = scParam.getSimulationContext().getName() + "." + scParam.getName();
            className = VCID.CLASS_SIMULATION_CONTEXT_PARAMETER;
        } else if(identifiable instanceof Kinetics.KineticsParameter kParam){
            localName = kParam.getKinetics().getReactionStep().getName() + "." + kParam.getName();
            className = VCID.CLASS_KINETICS_PARAMETER;
        } else if(identifiable instanceof ReactionSpec){
            localName = ((ReactionSpec) identifiable).getDisplayName();
            className = VCID.CLASS_REACTION_SPEC;
        } else {
            throw new RuntimeException("unsupported Identifiable class");
        }
        localName = TokenMangler.mangleVCId(localName);
        VCID vcid;
        try {
            vcid = VCID.fromString(className + "(" + localName + ")");
        } catch(VCID.InvalidVCIDException e){
            throw new RuntimeException(e.getMessage(), e);
        }
        return vcid;
    }

    public Set<Identifiable> getAllIdentifiables(){
        HashSet<Identifiable> allIdenfiables = new HashSet<>();
        allIdenfiables.addAll(Arrays.asList(this.model.getSpecies()));
        allIdenfiables.addAll(Arrays.asList(this.model.getStructures()));
        allIdenfiables.addAll(Arrays.asList(this.model.getReactionSteps()));
        Set<BioPaxObject> biopaxObjects = getPathwayModel().getBiopaxObjects();
        allIdenfiables.addAll(biopaxObjects);

        allIdenfiables.addAll(this.getSimulations());
        for(SimulationContext sc : this.simulationContexts){
            allIdenfiables.addAll(Arrays.asList(sc.getSimulations()));
            // species context specs and their parameters
            List<SpeciesContextSpec> speciesContextSpecs = Arrays.asList(sc.getReactionContext().getSpeciesContextSpecs());
            allIdenfiables.addAll(speciesContextSpecs);
            allIdenfiables.addAll(speciesContextSpecs.stream().flatMap(scs -> Arrays.stream(scs.getParameters()))
                    .filter(p -> p instanceof Identifiable)
                    .map(p -> (Identifiable) p)
                    .toList());

            // structure mappings parameters
            List<StructureMapping> structureMappings = Arrays.asList(sc.getGeometryContext().getStructureMappings());
            //allIdenfiables.addAll(structureMappings); // not Identifiable
            allIdenfiables.addAll(structureMappings.stream().flatMap(scs -> Arrays.stream(scs.getParameters()))
                    .filter(p -> p instanceof Identifiable)
                    .map(p -> (Identifiable) p)
                    .toList());

            allIdenfiables.addAll(Arrays.asList(sc.getReactionContext().getReactionSpecs()));

        }

        allIdenfiables.addAll(this.model.getRbmModelContainer().getMolecularTypeList());
        allIdenfiables.addAll(this.model.getRbmModelContainer().getReactionRuleList());
        allIdenfiables.addAll(this.model.getRbmModelContainer().getObservableList());
        allIdenfiables.add(this);
        return allIdenfiables;
    }

    public String getFreeSimulationContextName(){
        int count = 0;
        while (true) {
            String name = SIMULATION_CONTEXT_DISPLAY_NAME + count;
            if(getSimulationContext(name) == null){
                return name;
            }
            count++;
        }
    }

    public String getFreeSimulationName(){
        int count = 0;
        while (true) {
            String name = SIMULATION_DISPLAY_NAME + count;
            if(getSimulation(name) == null){
                return name;
            }
            count++;
        }
    }

    public Simulation getSimulation(String name){
        for(Simulation simulation : this.simulations){
            if(simulation.getName().equals(name)){
                return simulation;
            }
        }
        return null;
    }

    public SimulationContext getSimulationContext(String name){
        for(SimulationContext simulationContext : this.simulationContexts){
            if(simulationContext.getName().equals(name)){
                return simulationContext;
            }
        }
        return null;
    }

    public List<SymbolTableEntry> findReferences(SymbolTableEntry symbolTableEntry){
        ArrayList<SymbolTableEntry> references = new ArrayList<>();
        HashSet<NameScope> visited = new HashSet<>();

        this.model.getNameScope().findReferences(symbolTableEntry, references, visited);

        for(SimulationContext simContext : this.simulationContexts){
            simContext.getNameScope().findReferences(symbolTableEntry, references, visited);
        }
        return references;
    }

    // ------------------------------------------------- Pathway and Relationship objects and utility functions
    public PathwayModel getPathwayModel(){
        return this.pathwayModel;
    }

    public RelationshipModel getRelationshipModel(){
        return this.relationshipModel;
    }

    public static void printObject(BioPaxObject bpo){
    }

    private static void printObject(RelationshipObject ro){
    }

    // displays on console the bioPaxObjects in the pathway model
    public static void printBpModelObjects(Set<BioPaxObject> bpoList){
        System.out.println(bpoList.size() + " bioPaxObjects in the pathway model");
        for(BioPaxObject bpo : bpoList){
            printObject(bpo);
        }
    }

    // displays on console the bioPaxObjects that are part of a relationship
    public static void printBpRelationshipObjects(Set<BioPaxObject> bpoList){
        System.out.println(bpoList.size() + " bioPaxObjects that are part of a relationship");
        for(BioPaxObject bpo : bpoList){
            printObject(bpo);
        }
    }

    // displays on console the relationship objects existing in the relationship model
    public static void printRelationships(Set<RelationshipObject> roList){
        System.out.println(roList.size() + " relationshipObjects in the relationship model");
        for(RelationshipObject ro : roList){
            printObject(ro);
        }
    }

    public static final String typeName = "BioModel";

    @Override
    public String getDisplayName(){
        return getName();
    }

    @Override
    public String getDisplayType(){
        return typeName;
    }

    public void updateAll(boolean bForceUpgrade) throws MappingException{
        refreshDependencies();
        for(SimulationContext simulationContext : getSimulationContextsAsArray()){
            simulationContext.updateAll(bForceUpgrade);
        }
    }
}