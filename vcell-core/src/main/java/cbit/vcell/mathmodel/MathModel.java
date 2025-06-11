/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mathmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Arrays;
import java.util.List;

import cbit.vcell.solver.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.*;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.document.*;
import org.vcell.util.document.BioModelChildSummary.MathType;

import cbit.vcell.biomodel.VCellNames;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.VCMODL;

/**
 * Insert the type's description here.
 * Creation date: (10/17/00 3:12:16 PM)
 *
 * @author:
 */
@SuppressWarnings("serial")
public class MathModel implements VCDocument, SimulationOwner, Matchable, VetoableChangeListener, PropertyChangeListener, IssueSource, Versionable {
    private final static Logger lg = LogManager.getLogger(MathModel.class);

    public static final String PROPERTY_NAME_MATH_DESCRIPTION = "mathDescription";
    private static final String TIME_UNIT_STRING = "time units";

    private Version fieldVersion = null;
    private java.lang.String fieldName = new String("NoName");
    protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
    protected transient java.beans.PropertyChangeSupport propertyChange;
    private MathDescription fieldMathDescription = null;
    private final OutputFunctionContext outputFunctionContext = new OutputFunctionContext(this);
    private Simulation[] fieldSimulations = new Simulation[0];
    private java.lang.String fieldDescription = new String();
    private static UnitInfo mathUnitInfo = null;

    private transient boolean tempSmoldynWarningAcknowledged;

    /**
     * BioModel constructor comment.
     */
    public MathModel(Version version){
        super();
        addVetoableChangeListener(this);
        addPropertyChangeListener(this);
        setMathDescription(new MathDescription("unnamed"));
        try {
            setVersion(version);
        } catch(PropertyVetoException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    @Override
    public UnitInfo getUnitInfo() throws UnsupportedOperationException{
        if(mathUnitInfo == null){
            mathUnitInfo = new UnitInfo(TIME_UNIT_STRING);
        }
        return mathUnitInfo;
    }


    public Simulation addNewSimulation(String simNamePrefix) throws java.beans.PropertyVetoException{
        MathDescription math = getMathDescription();
        if(math == null){
            throw new RuntimeException("Can't create Simulation, math not created");
        }
        //
        // get free name for new Simulation.
        //
        Simulation sims[] = getSimulations();
        String newSimName = null;
        for(int i = 0; newSimName == null && i < 100; i++){
            String proposedName = simNamePrefix + i;
            boolean bFound = false;
            for(int j = 0; sims != null && !bFound && j < sims.length; j++){
                if(sims[j].getName().equals(proposedName)){
                    bFound = true;
                }
            }
            if(!bFound){
                newSimName = proposedName;
            }
        }
        if(newSimName == null){
            throw new RuntimeException("failed to find name for new Simulation");
        }

        //
        // create new Simulation and add to MathModel.
        //
        Simulation newSimulation = new Simulation(math, this);
        newSimulation.setName(newSimName);

        addSimulation(newSimulation);

        return newSimulation;
    }


    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener){
        getPropertyChange().addPropertyChangeListener(listener);
    }


    public void addSimulation(Simulation simulation) throws java.beans.PropertyVetoException{
        if(contains(simulation)){
            throw new IllegalArgumentException("MathModel.addSimulation() simulation already present in MathModel");
        }
        if(getNumSimulations() == 0){
            setSimulations(new Simulation[]{simulation});
        } else {
            setSimulations(ArrayUtils.addElement(fieldSimulations, simulation));
        }

    }


    /**
     * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener){
        getVetoPropertyChange().addVetoableChangeListener(listener);
    }


    /**
     * Insert the method's description here.
     * Creation date: (4/24/2003 3:40:07 PM)
     */
    public void clearVersion(){
        fieldVersion = null;
    }


    /**
     * Insert the method's description here.
     * Creation date: (11/29/00 2:11:43 PM)
     *
     * @param obj cbit.util.Matchable
     * @return boolean
     */
    public boolean compareEqual(Matchable obj){
        if(!(obj instanceof MathModel)){
            return false;
        }
        MathModel mathModel = (MathModel) obj;
        if(!Compare.isEqualOrNull(getName(), mathModel.getName())){
            return false;
        }
        if(!Compare.isEqualOrNull(getDescription(), mathModel.getDescription())){
            return false;
        }
        if(!getMathDescription().compareEqual(mathModel.getMathDescription())){
            return false;
        }
        if(!Compare.isEqualOrNull(getSimulations(), mathModel.getSimulations())){
            return false;
        }
        if(!outputFunctionContext.compareEqual(mathModel.outputFunctionContext)){
            return false;
        }

        return true;
    }


    public boolean contains(Simulation simulation){
        if(simulation == null){
            throw new IllegalArgumentException("simulation was null");
        }
        Simulation sims[] = getSimulations();
        if(sims == null){
            return false;
        }
        boolean bFound = false;
        for(int i = 0; i < sims.length; i++){
            if(sims[i] == simulation){
                bFound = true;
            }
        }
        return bFound;
    }

    public Simulation copySimulation(Simulation simulation) throws java.beans.PropertyVetoException{
        MathDescription math = getMathDescription();
        if(math == null){
            throw new RuntimeException("Can't create Simulation, math not created");
        }
        //
        // get free name for new Simulation.
        //
        Simulation sims[] = getSimulations();
        String newSimName = null;
        for(int i = 0; newSimName == null && i < 100; i++){
            String proposedName = "Copy of " + simulation.getName() + ((i > 0) ? (" " + i) : (""));
            boolean bFound = false;
            for(int j = 0; !bFound && j < sims.length; j++){
                if(sims[j].getName().equals(proposedName)){
                    bFound = true;
                }
            }
            if(!bFound){
                newSimName = proposedName;
            }
        }
        if(newSimName == null){
            throw new RuntimeException("failed to find name for new Simulation");
        }

        //
        // create new Simulation and add to MathModel.
        //
        Simulation newSimulation = new Simulation(simulation);
        newSimulation.setName(newSimName);

        addSimulation(newSimulation);

        return newSimulation;
    }

    public MathModelChildSummary createMathModelChildSummary(){

        MathType modelType = getMathDescription().getMathType();
        String geoName = getMathDescription().getGeometry().getName();
        int geoDim = getMathDescription().getGeometry().getDimension();

        Simulation[] sims = getSimulations();
        String[] simNames = new String[sims.length];
        String[] simAnnots = new String[sims.length];
        for(int i = 0; i < sims.length; i += 1){
            simNames[i] = sims[i].getName();
            simAnnots[i] = sims[i].getDescription();
        }

        return new MathModelChildSummary(modelType, geoName, geoDim, simNames, simAnnots);

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
        getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
    }


    /**
     * Insert the method's description here.
     * Creation date: (3/18/2004 1:54:51 PM)
     *
     * @param newVersion cbit.sql.Version
     */
    public void forceNewVersionAnnotation(Version newVersion) throws PropertyVetoException{
        if(getVersion().getVersionKey().equals(newVersion.getVersionKey())){
            setVersion(newVersion);
        } else {
            throw new RuntimeException("MathModel.forceNewVersionAnnotation failed : version keys not equal");
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


    public VCDocumentType getDocumentType(){
        return VCDocumentType.MATHMODEL_DOC;
    }


    public MathDescription getMathDescription(){
        return fieldMathDescription;
    }


    public java.lang.String getName(){
        return fieldName;
    }


    public int getNumSimulations(){
        if(getSimulations() == null){
            return 0;
        }
        return getSimulations().length;
    }


    protected java.beans.PropertyChangeSupport getPropertyChange(){
        if(propertyChange == null){
            propertyChange = new java.beans.PropertyChangeSupport(this);
        }
        ;
        return propertyChange;
    }


    public Simulation[] getSimulations(){
        return fieldSimulations;
    }

    public List<Simulation> getSimulationCollection(){
        return Arrays.asList(fieldSimulations);
    }

    public Simulation getSimulations(int index){
        return getSimulations()[index];
    }


    public java.lang.String getVCML() throws Exception{
        StringBuffer buffer = new StringBuffer();
        String name = (getName() != null) ? getName() : "unnamedMathModel";
        buffer.append(VCMODL.MathModel + " " + name + " {\n");

        //
        // write MathDescription
        //
        buffer.append(getMathDescription().getVCML_database() + "\n");

        //
        // write Simulations
        //
        if(fieldSimulations != null){
            for(int i = 0; i < this.fieldSimulations.length; i++){
                buffer.append(fieldSimulations[i].getVCML() + "\n");
            }
        }

        buffer.append("}\n");
        return buffer.toString();
    }


    public Version getVersion(){
        return fieldVersion;
    }


    protected java.beans.VetoableChangeSupport getVetoPropertyChange(){
        if(vetoPropertyChange == null){
            vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
        }
        ;
        return vetoPropertyChange;
    }


    public java.lang.String getXML(){
        return null;
    }


    public synchronized boolean hasListeners(java.lang.String propertyName){
        return getPropertyChange().hasListeners(propertyName);
    }


    public void propertyChange(java.beans.PropertyChangeEvent evt){

        //
        // propagate mathDescription changes from SimulationContexts to Simulations
        //
        if(evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_MATH_DESCRIPTION)){
            Geometry oldGeometry = null;
            Geometry newGeometry = null;
            MathDescription oldValue = (MathDescription) evt.getOldValue();
            if(oldValue != null){
                oldValue.removePropertyChangeListener(this);
                oldGeometry = oldValue.getGeometry();
            }
            MathDescription newValue = (MathDescription) evt.getNewValue();
            if(newValue != null){
                newValue.addPropertyChangeListener(this);
                newGeometry = newValue.getGeometry();
                if(fieldSimulations != null){
                    for(int i = 0; i < fieldSimulations.length; i++){
                        if(fieldSimulations[i].getMathDescription() == evt.getOldValue()){
                            try {
                                fieldSimulations[i].setMathDescription((MathDescription) evt.getNewValue());
                            } catch(PropertyVetoException e){
                                lg.warn("error propagating math to Simulation '" + fieldSimulations[i].getName(), e);
                            }
                        }
                    }
                }
            }
            if(oldGeometry != newGeometry){
                firePropertyChange(GeometryOwner.PROPERTY_NAME_GEOMETRY, oldGeometry, newGeometry);
            }
        }
        if(evt.getSource() == getMathDescription() && evt.getPropertyName().equals(GeometryOwner.PROPERTY_NAME_GEOMETRY)){
            firePropertyChange(GeometryOwner.PROPERTY_NAME_GEOMETRY, evt.getOldValue(), evt.getNewValue());
        }

        //
        // make sure that simulations and simulationContexts are listened to
        //
        if(evt.getSource() == this && evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_SIMULATIONS) && evt.getNewValue() != null){
            //
            // unregister for old
            //
            if(evt.getOldValue() != null){
                Simulation simulations[] = (Simulation[]) evt.getOldValue();
                for(int i = 0; i < simulations.length; i++){
                    simulations[i].removeVetoableChangeListener(this);
                    simulations[i].removePropertyChangeListener(this);
                }
            }
            //
            // register for new
            //
            if(evt.getOldValue() != null){
                Simulation simulations[] = (Simulation[]) evt.getNewValue();
                for(int i = 0; i < simulations.length; i++){
                    simulations[i].addVetoableChangeListener(this);
                    simulations[i].addPropertyChangeListener(this);
                }
            }
        }

    }


    public void refreshDependencies(){
        removePropertyChangeListener(this);
        removeVetoableChangeListener(this);
        addPropertyChangeListener(this);
        addVetoableChangeListener(this);
        if(getMathDescription() != null){
            getMathDescription().removePropertyChangeListener(this);
            getMathDescription().addPropertyChangeListener(this);
        }


        fieldMathDescription.refreshDependencies();
        fieldMathDescription.removeVetoableChangeListener(this);
        fieldMathDescription.removePropertyChangeListener(this);
        fieldMathDescription.addVetoableChangeListener(this);
        fieldMathDescription.addPropertyChangeListener(this);

        outputFunctionContext.refreshDependencies();

        for(int i = 0; i < fieldSimulations.length; i++){
            fieldSimulations[i].removeVetoableChangeListener(this);
            fieldSimulations[i].removePropertyChangeListener(this);
            fieldSimulations[i].addVetoableChangeListener(this);
            fieldSimulations[i].addPropertyChangeListener(this);
            fieldSimulations[i].setSimulationOwner(this);
        }
    }


    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener){
        getPropertyChange().removePropertyChangeListener(listener);
    }


    public void removeSimulation(Simulation simulation) throws java.beans.PropertyVetoException{
        if(!contains(simulation)){
            throw new IllegalArgumentException("MathModel.removeSimulation() simulation not present in MathModel");
        }
        setSimulations(ArrayUtils.removeFirstInstanceOfElement(fieldSimulations, simulation));
    }


    /**
     * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener){
        getVetoPropertyChange().removeVetoableChangeListener(listener);
    }


    public void setDescription(java.lang.String description) throws java.beans.PropertyVetoException{
        String oldValue = fieldDescription;
        fireVetoableChange("description", oldValue, description);
        fieldDescription = description;
        firePropertyChange("description", oldValue, description);
    }


    public void setMathDescription(MathDescription mathDescription){
        MathDescription oldValue = fieldMathDescription;
        fieldMathDescription = mathDescription;
        firePropertyChange(PROPERTY_NAME_MATH_DESCRIPTION, oldValue, mathDescription);
    }


    /**
     * Sets the name property (java.lang.String) value.
     *
     * @param name The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getName
     */
    public void setName(java.lang.String name) throws java.beans.PropertyVetoException{
        String oldValue = fieldName;
        fireVetoableChange("name", oldValue, name);
        fieldName = name;
        firePropertyChange("name", oldValue, name);
    }


    /**
     * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
     *
     * @param simulations The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getSimulations
     */
    public void setSimulations(Simulation[] simulations) throws java.beans.PropertyVetoException{
        Simulation[] oldValue = fieldSimulations;
        fireVetoableChange(PropertyConstants.PROPERTY_NAME_SIMULATIONS, oldValue, simulations);

        if(oldValue != null){
            for(Simulation sim : oldValue){
                sim.setSimulationOwner(null);
            }
        }

        fieldSimulations = simulations;

        if(fieldSimulations != null){
            for(Simulation sim : fieldSimulations){
                sim.setSimulationOwner(this);
            }
        }

        firePropertyChange(PropertyConstants.PROPERTY_NAME_SIMULATIONS, oldValue, simulations);
    }


    /**
     * Insert the method's description here.
     * Creation date: (11/14/00 3:49:12 PM)
     *
     * @param version cbit.sql.Version
     */
    private void setVersion(Version version) throws PropertyVetoException{
        this.fieldVersion = version;
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
    public String toString(){
        String desc = (getVersion() == null) ? getName() : getVersion().toString();
        return "MathModel@" + Integer.toHexString(hashCode()) + "(" + desc + ")";
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
        if(evt.getSource() == this && evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_SIMULATIONS) && evt.getNewValue() != null){
            //
            // check for name duplication
            //
            Simulation simulations[] = (Simulation[]) evt.getNewValue();
            for(int i = 0; i < simulations.length - 1; i++){
                for(int j = i + 1; j < simulations.length; j++){
                    if(simulations[i].getName().equals(simulations[j].getName())){
                        throw new PropertyVetoException(VCellNames.getName(simulations[i]) + " with name " + simulations[i].getName() + " already exists", evt);
                    }
                }
            }
        }
        if(evt.getSource() instanceof Simulation && evt.getPropertyName().equals("name") && evt.getNewValue() != null){
            //
            // check for name duplication
            //
            String simulationName = (String) evt.getNewValue();
            for(int i = 0; i < fieldSimulations.length; i++){
                if(fieldSimulations[i].getName().equals(simulationName)){
                    throw new PropertyVetoException(VCellNames.getName(fieldSimulations[i]) + " with name " + simulationName + " already exists", evt);
                }
            }
        }

        TokenMangler.checkNameProperty(this, "MathModel", evt);
    }


    public OutputFunctionContext getOutputFunctionContext(){
        return outputFunctionContext;
    }

    @Override
    public MathOverridesResolver getMathOverridesResolver(){
        return null;
    }

    public Geometry getGeometry(){
        return getMathDescription().getGeometry();
    }

    @Override
    public void gatherIssues(IssueContext issueContext, List<Issue> issueList){
        issueContext = issueContext.newChildContext(ContextType.MathModel, this);
        GeometrySpec geometrySpec = getGeometry().getGeometrySpec();
        if(geometrySpec != null){
            geometrySpec.gatherIssues(issueContext, getGeometry(), issueList);
        }
        fieldMathDescription.gatherIssues(issueContext, issueList);
        outputFunctionContext.gatherIssues(issueContext, issueList);
//		for (Simulation simulation : fieldSimulations){
//			simulation.gatherIssues(issueContext,issueList);
//		}
    }

    public Simulation getSimulation(String chosenSimulationName){
        for(Simulation sim : getSimulations()){
            if(sim.getName().equals(chosenSimulationName)){
                return sim;
            }
        }
        return null;
    }


    @Override
    public Issue gatherIssueForMathOverride(IssueContext issueContext, Simulation simulation, String overriddenConstantName){
        //issueContext = issueContext.newChildContext(ContextType.MathModel, this);
        // no semantics for another override
        return null;
    }

    /**
     * temporary class pending resolution of Smoldyn surface membrane inaccuracy
     */
    public class TempSmoldynWarningAPI {
        private TempSmoldynWarningAPI(){
        }

        /**
         * @return true if acknowledged already
         */
        public boolean isWarningAcknowledged(){
            return tempSmoldynWarningAcknowledged;
        }

        /**
         * set acknowledge to true
         */
        public void acknowledge(){
            tempSmoldynWarningAcknowledged = true;
        }

        /**
         * @return parent model
         */
        public MathModel getModel(){
            return MathModel.this;
        }
    }
}
