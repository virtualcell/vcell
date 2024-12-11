/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

import cbit.vcell.math.MathUtilities;
import cbit.vcell.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.*;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.document.Identifiable;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.Kind;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.mapping.spatial.VolumeRegionObject;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;
import cbit.vcell.units.VCUnitDefinition;
import net.sourceforge.interval.ia_math.RealInterval;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.NamedColor;
import org.vcell.util.springsalad.GraphContinuity;

@SuppressWarnings("serial")
public class SpeciesContextSpec implements Matchable, ScopedSymbolTable, Serializable, SimulationContextEntity, IssueSource,
        Identifiable, Displayable {

    private final static Logger lg = LogManager.getLogger(SpeciesContextSpec.class);

    public static final String PARAMETER_NAME_PROXY_PARAMETERS = "proxyParameters";
    private static final String PROPERTY_NAME_WELL_MIXED = "wellMixed";
    private static final String PROPERTY_NAME_FORCECONTINUOUS = "forceContinuous";
    public static final String PROPERTY_NAME_SITE_ATTRIBUTE = "SiteAttributes";     // springsalad
    public static final String PROPERTY_NAME_LINK_LENGTH = "LinkLength";            // springsalad
    private static final int INITIAL_YZ_SITE_OFFSET = 4;

    public static final boolean TrackClusters = true;            // SpringSaLaD specific
    public static final boolean InitialLocationRandom = true;
    private static final String InitialLocationRandomString = "Random";
    private static final String InitialLocationSetString = "Set";
    public static final String SourceMoleculeString = "Source";    // molecule used in creation reaction subtype (reserved name)
    public static final String SinkMoleculeString = "Sink";        // molecule used in decay reaction subtype (reserved name)
    public static final String AnchorSiteString = "Anchor";        // required name for reserved special Site of membrane species
    public static final String AnchorStateString = "Anchor";        // required name for reserved special State of the Anchor site


    public class SpeciesContextSpecNameScope extends BioNameScope {
        private final NameScope children[] = new NameScope[0]; // always empty

        public SpeciesContextSpecNameScope(){
            super();
        }

        public NameScope[] getChildren(){
            //
            // no children to return
            //
            return children;
        }

        public String getName(){
            return SpeciesContextSpec.this.getSpeciesContext().getName() + "_scs";
        }

        public NameScope getParent(){
            if(SpeciesContextSpec.this.simulationContext != null){
                return SpeciesContextSpec.this.simulationContext.getNameScope();
            } else {
                return null;
            }
        }

        public ScopedSymbolTable getScopedSymbolTable(){
            return SpeciesContextSpec.this;
        }

        @Override
        public String getPathDescription(){
            if(simulationContext != null){
                return "App(" + simulationContext.getName() + ") / Species(" + getSpeciesContext().getName() + ")";
            }
            return null;
        }

        @Override
        public NamescopeType getNamescopeType(){
            return NamescopeType.speciesContextSpecType;
        }
    }

    public class SpeciesContextSpecParameter extends Parameter implements Identifiable, ExpressionContainer, IssueSource {
        private Expression fieldParameterExpression = null;
        private String fieldParameterName = null;
        private int fieldParameterRole = -1;
        private VCUnitDefinition fieldUnitDefinition = null;

        public SpeciesContextSpecParameter(String parmName, Expression argExpression, int argRole, VCUnitDefinition argUnitDefinition, String argDescription){
            super();
            fieldParameterName = parmName;
            fieldParameterExpression = argExpression;
            if(argRole >= 0 && argRole < NUM_ROLES){
                this.fieldParameterRole = argRole;

            } else {
                throw new IllegalArgumentException("parameter 'role' = " + argRole + " is out of range");
            }
            fieldUnitDefinition = argUnitDefinition;
            setDescription(argDescription);
        }

        public SimulationContext getSimulationContext(){
            return SpeciesContextSpec.this.simulationContext;
        }

        public SpeciesContextSpec getSpeciesContextSpec(){
            return SpeciesContextSpec.this;
        }

        public String getNullExpressionDescription(){
            if(fieldParameterRole == ROLE_BoundaryValueXm
                    || fieldParameterRole == ROLE_BoundaryValueXp
                    || fieldParameterRole == ROLE_BoundaryValueYm
                    || fieldParameterRole == ROLE_BoundaryValueYp
                    || fieldParameterRole == ROLE_BoundaryValueZm
                    || fieldParameterRole == ROLE_BoundaryValueZp){
                if(fieldUnitDefinition != null){
                    VCUnitDefinition lengthPerTimeUnitDefn = getLengthPerTimeUnit();
                    if(fieldUnitDefinition.isEquivalent(getSpeciesContext().getUnitDefinition())){
                        return "<html><i>&lt;initial value&gt;</i></html>";
                    } else if(fieldUnitDefinition.isEquivalent(getSpeciesContext().getUnitDefinition().multiplyBy(lengthPerTimeUnitDefn))){
                        return "<html><i>&lt;zero flux&gt;</i></html>";
                    }
                }
            } else if(fieldParameterRole == ROLE_VelocityX
                    || fieldParameterRole == ROLE_VelocityY
                    || fieldParameterRole == ROLE_VelocityZ){
                QuantityComponent component = null;
                if(fieldParameterRole == ROLE_VelocityX){
                    component = QuantityComponent.X;
                } else if(fieldParameterRole == ROLE_VelocityY){
                    component = QuantityComponent.Y;
                } else if(fieldParameterRole == ROLE_VelocityZ){
                    component = QuantityComponent.Z;
                }
                ArrayList<String> varNames = new ArrayList<String>();
                StructureMapping sm = simulationContext.getGeometryContext().getStructureMapping(speciesContext.getStructure());
                if(sm != null && sm.getGeometryClass() != null){
                    for(SpatialObject spatialObject : simulationContext.getSpatialObjects()){
                        if(spatialObject instanceof VolumeRegionObject){
                            VolumeRegionObject vro = (VolumeRegionObject) spatialObject;
                            if(vro.getSubVolume() == sm.getGeometryClass() && vro.isQuantityCategoryEnabled(QuantityCategory.InteriorVelocity)){
                                varNames.add(vro.getSpatialQuantity(QuantityCategory.InteriorVelocity, component).getName());
                            }
                        }
                    }
                }
                if(varNames.size() > 0){
                    return "<html><i>&lt;" + varNames.toString().replace("[", "").replace("]", "").replace(",", " or ") + "&gt;</i></html>";
                } else {
                    return "<html><i>&lt;0.0&gt;</i></html>";
                }
            }
            return null;
        }

        @Override
        public boolean compareEqual(Matchable obj){
            if(!(obj instanceof SpeciesContextSpecParameter)){
                return false;
            }
            SpeciesContextSpecParameter smp = (SpeciesContextSpecParameter) obj;
            if(!super.compareEqual0(smp)){
                return false;
            }
            if(fieldParameterRole != smp.fieldParameterRole){
                return false;
            }

            return true;
        }


        @Override
        public boolean relate(Relatable obj, RelationVisitor rv){
            if(!(obj instanceof SpeciesContextSpecParameter)){
                return false;
            }
            SpeciesContextSpecParameter smp = (SpeciesContextSpecParameter) obj;
            if(!super.relate0(smp, rv)){
                return false;
            }
            if(fieldParameterRole != smp.fieldParameterRole){
                return false;
            }

            return true;
        }

        public NameScope getNameScope(){
            return SpeciesContextSpec.this.getNameScope();
        }

        public boolean isExpressionEditable(){
            return true;
        }

        @Override
        public String getDescription(){
            return super.getDescription() + " for " + getSpeciesContext().getName();
        }

        public boolean isUnitEditable(){
            return false;
        }

        public boolean isNameEditable(){
            return false;
        }

        public VCUnitDefinition getUnitDefinition(){
            return fieldUnitDefinition;
        }

        public void setExpression(Expression expression) throws ExpressionBindingException{
            if(expression != null){
                expression = new Expression(expression);
                // Need to bind this expression, but changing the binding from SpeciesContextSpec.this to a symbolTable created on the fly.
                // This new symbolTable is this SpeciesContextSpec, but omits its parameters, so that the SpeciesContextSpecParameter
                // expression that is being bound cannot contain other speciesContextSpecParameters in its expression.
                expression.bindExpression(new SymbolTable() {
                    public SymbolTableEntry getEntry(String identifierString){
                        SymbolTableEntry ste = SpeciesContextSpec.this.getEntry(identifierString);
                        if(ste instanceof SpeciesContextSpecParameter){
                            throw new RuntimeException("\nCannot use a speciesContextSpec parameter (e.g., diff, initConc, Vel_X, etc.) in another speciesContextSpec parameter expression.");
                        }
                        return ste;
                    }

                    public void getEntries(Map<String, SymbolTableEntry> entryMap){
                        SpeciesContextSpec.this.getEntries(entryMap);
                    }
                });
            }
            Expression oldValue = fieldParameterExpression;
            fieldParameterExpression = expression;
            try {
                SpeciesContextSpec.this.cleanupParameters();
            } catch(ModelException | ExpressionException e){
                lg.error(e);
            }
            super.firePropertyChange("expression", oldValue, expression);
        }

        /**
         * return 0 if {@link #getRole()} initial concentration, diffusion rate, or initial count
         */
        @Override
        public Expression getDefaultExpression(){
            //functionality moved from ParameterPropertiesPanel Dec 2014
            switch(getRole()){
                case ROLE_InitialConcentration:
                case ROLE_DiffusionRate:
                case ROLE_InitialCount:
                    return new Expression(0);
                default:
                    return null;
            }
        }

        public double getConstantValue() throws ExpressionException{
            return fieldParameterExpression.evaluateConstant();
        }

        public void setName(java.lang.String name) throws java.beans.PropertyVetoException{
            String oldValue = fieldParameterName;
            super.fireVetoableChange("name", oldValue, name);
            fieldParameterName = name;
            super.firePropertyChange("name", oldValue, name);
        }

        public void setUnitDefinition(VCUnitDefinition unitDefinition) throws java.beans.PropertyVetoException{
            VCUnitDefinition oldValue = fieldUnitDefinition;
            super.fireVetoableChange("unitDefinition", oldValue, unitDefinition);
            fieldUnitDefinition = unitDefinition;
            super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
        }

        public String getName(){
            return fieldParameterName;
        }

        public Expression getExpression(){
            return fieldParameterExpression;
        }

        public int getIndex(){
            return -1;
        }

        public int getRole(){
            return fieldParameterRole;
        }

        public SpeciesContext getSpeciesContext(){
            return SpeciesContextSpec.this.getSpeciesContext();
        }
    }

    public class SpeciesContextSpecProxyParameter extends ProxyParameter {

        public SpeciesContextSpecProxyParameter(SymbolTableEntry target){
            super(target);
        }

        public NameScope getNameScope(){
            return SpeciesContextSpec.this.getNameScope();
        }

        @Override
        public boolean compareEqual(Matchable obj){
            if(!(obj instanceof SpeciesContextSpecProxyParameter)){
                return false;
            }
            SpeciesContextSpecProxyParameter other = (SpeciesContextSpecProxyParameter) obj;
            if(getTarget() instanceof Matchable && other.getTarget() instanceof Matchable &&
                    Compare.isEqual((Matchable) getTarget(), (Matchable) other.getTarget())){
                return true;
            } else {
                return false;
            }
        }


        @Override
        public boolean relate(Relatable obj, RelationVisitor rv){
            if(!(obj instanceof SpeciesContextSpecProxyParameter)){
                return false;
            }
            SpeciesContextSpecProxyParameter other = (SpeciesContextSpecProxyParameter) obj;
            if(getTarget() instanceof Relatable && other.getTarget() instanceof Relatable &&
                    ((Relatable) getTarget()).relate((Relatable) other.getTarget(), rv)){
                return true;
            } else {
                return false;
            }
        }


        @Override
        public String getDescription(){
            if(getTarget() instanceof SpeciesContext){
                return "Species Concentration";
            } else {
                return super.getDescription();
            }
        }

        @Override
        public void targetPropertyChange(PropertyChangeEvent evt){
            super.targetPropertyChange(evt);
            if(evt.getPropertyName().equals("name")){
                String oldName = (String) evt.getOldValue();
                String newName = (String) evt.getNewValue();
                try {
                    SpeciesContextSpecParameter newParameters[] = new SpeciesContextSpecParameter[fieldParameters.length];
                    System.arraycopy(fieldParameters, 0, newParameters, 0, fieldParameters.length);
                    //
                    // go through all parameters' expressions and replace references to 'oldName' with 'newName'
                    //
                    for(int i = 0; i < newParameters.length; i++){
                        Expression exp = getParameter(i).getExpression();
                        if(exp != null){
                            Expression newExp = new Expression(exp);
                            newExp.substituteInPlace(new Expression(oldName), new Expression(newName));
                            newParameters[i] = new SpeciesContextSpecParameter(newParameters[i].getName(), newExp, newParameters[i].getRole(), newParameters[i].getUnitDefinition(), newParameters[i].getDescription());
                        }
                    }
                    setParameters(newParameters);

                    //
                    // rebind all expressions
                    //
                    for(int i = 0; i < newParameters.length; i++){
                        if(newParameters[i].getExpression() != null){
                            newParameters[i].getExpression().bindExpression(SpeciesContextSpec.this);
                        }
                    }

                    //
                    // clean up dangling parameters (those not reachable from the 'required' parameters).
                    //
                    cleanupParameters();

                } catch(ModelException | ExpressionException | PropertyVetoException e1){
                    lg.error("property change failed", e1);
                }
            }
        }
    }

    private SpeciesContext speciesContext = null;
    private static final boolean DEFAULT_CLAMPED = false;
    //	private static final boolean DEFAULT_ENABLE_DIFFUSING = true;
    private static final Boolean DEFAULT_WELL_MIXED = false;
    private static final Boolean DEFAULT_FORCECONTINUOUS = false;
    private boolean bClamped = DEFAULT_CLAMPED;
    //	private boolean        bEnableDiffusing = DEFAULT_ENABLE_DIFFUSING;
    private Boolean bWellMixed = DEFAULT_WELL_MIXED;
    private Boolean bForceContinuous = DEFAULT_FORCECONTINUOUS;

    // SpringSaLaD specific entities
    public Set<MolecularInternalLinkSpec> internalLinkSet = new LinkedHashSet<>();
    private Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMap = new LinkedHashMap<>();
    // is2D flag, used by the solver for collision / overlapping calculations, exact meaning uncertain
    // membrane species may have it set to true, for compartment species is always false
    // for now we have it hardcoded to false and non-editable
    // TODO: slowly add it to export and persistence, eventually make it editable for membrane species
    // Obs: it's really an attribute of a MolecularTypePattern, or even better of a so called "MolecularTypePatternSpec",
    //      but since all species in SS are single molecule by design it's okay to have it here
    // TODO: add getIs2DSQL() and readIs2DSQL(), similar to getSiteAttributesSQL(), readSiteAttributesSQL
    private boolean is2D = false;

    // We implement Provenance as a means to use different PropertiesPanels when viewing SpeciesContextSpec properties
    // we want the general SCS panel for non-springsalad applications, and 2 special panels for
    // initial conditions - simplified initial conditions panel without the parameters table which make no sense for springsalad
    // site specifications properties panel = for springsalad site attributes (will contain the viewer)
    public transient Provenance provenance = Provenance.GeneralInitialConditions;
    public enum Provenance {		// SpringSaLaD specific
        GeneralInitialConditions,
        LangevinInitialConditions,
        LangevinSpecs;
//
//        final public String columnName;
//        private SpringStructureEnum(String columnName) {
//            this.columnName = columnName;
//        }
    }


    protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
    private SpeciesContextSpecParameter[] fieldParameters = null;
    private SpeciesContextSpecProxyParameter[] fieldProxyParameters = new SpeciesContextSpecProxyParameter[0];
    protected transient java.beans.PropertyChangeSupport propertyChange;
    protected transient SimulationContext simulationContext = null;
    private SpeciesContextSpecNameScope nameScope = new SpeciesContextSpecNameScope();
    public static final int ROLE_InitialConcentration = 0;
    public static final int ROLE_DiffusionRate = 1;
    public static final int ROLE_BoundaryValueXm = 2;
    public static final int ROLE_BoundaryValueXp = 3;
    public static final int ROLE_BoundaryValueYm = 4;
    public static final int ROLE_BoundaryValueYp = 5;
    public static final int ROLE_BoundaryValueZm = 6;
    public static final int ROLE_BoundaryValueZp = 7;
    public static final int ROLE_InitialCount = 8;
    public static final int ROLE_VelocityX = 9;
    public static final int ROLE_VelocityY = 10;
    public static final int ROLE_VelocityZ = 11;

    public static final int NUM_ROLES = 12;
    public static final String RoleTags[] = {
            VCMODL.InitialConcentration,
            VCMODL.DiffusionRate,
            VCMODL.BoundaryConditionXm,
            VCMODL.BoundaryConditionXp,
            VCMODL.BoundaryConditionYm,
            VCMODL.BoundaryConditionYp,
            VCMODL.BoundaryConditionZm,
            VCMODL.BoundaryConditionZp,
            VCMODL.InitialCount,
            VCMODL.VelocityX,
            VCMODL.VelocityY,
            VCMODL.VelocityZ
    };
    public static final String RoleNames[] = {
            "initConc",
            "diff",
            "BC_Xm",
            "BC_Xp",
            "BC_Ym",
            "BC_Yp",
            "BC_Zm",
            "BC_Zp",
            "initCount",
            "Vel_X",
            "Vel_Y",
            "Vel_Z"
    };
    public static final String RoleDescriptions[] = {
            "initial concentration",
            "diffusion constant",
            "Boundary Condition X-",
            "Boundary Condition X+",
            "Boundary Condition Y-",
            "Boundary Condition Y+",
            "Boundary Condition Z-",
            "Boundary Condition Z+",
            "initial count",
            "Velocity X",
            "Velocity Y",
            "Velocity Z"
    };
    private static RealInterval[] parameterBounds = {
            new RealInterval(0.0, Double.POSITIVE_INFINITY), // init concentration
            new RealInterval(0.0, Double.POSITIVE_INFINITY), // diff rate
            null,    // BC X-
            null,    // BC X+
            null,    // BC Y-
            null,    // BC Y+
            null,    // BC Z-
            null,    // BC Z+
            new RealInterval(0.0, Double.POSITIVE_INFINITY), // init amount
            null, // Velocity X
            null, // Velocity Y
            null  // Velocity Z
    };

    public SpeciesContextSpec(SpeciesContextSpec speciesContextSpec, SimulationContext argSimulationContext){
        this.speciesContext = speciesContextSpec.speciesContext;
        this.bClamped = speciesContextSpec.bClamped;
//	this.bEnableDiffusing = speciesContextSpec.bEnableDiffusing;
        this.bWellMixed = speciesContextSpec.bWellMixed;
        this.bForceContinuous = speciesContextSpec.bForceContinuous;
        this.simulationContext = argSimulationContext;
        fieldParameters = new SpeciesContextSpecParameter[speciesContextSpec.fieldParameters.length];
        for(int i = 0; i < speciesContextSpec.fieldParameters.length; i++){
            SpeciesContextSpecParameter otherParm = speciesContextSpec.fieldParameters[i];
            Expression otherParmExp = (otherParm.getExpression() == null) ? (null) : (new Expression(otherParm.getExpression()));
            fieldParameters[i] = new SpeciesContextSpecParameter(otherParm.getName(), otherParmExp, otherParm.getRole(), otherParm.getUnitDefinition(), otherParm.getDescription());
        }
        refreshDependencies();
    }


    public SpeciesContextSpec(SpeciesContext speciesContext, SimulationContext argSimulationContext){
        this.speciesContext = speciesContext;
        this.simulationContext = argSimulationContext;

        // avoid unit computation for case of micromolar
        fieldParameters = new SpeciesContextSpecParameter[NUM_ROLES];
        ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
        VCUnitDefinition fluxUnits = computeFluxUnit();
        VCUnitDefinition spContextUnit = speciesContext.getUnitDefinition();
        VCUnitDefinition stochasticSubstanceUnit = modelUnitSystem.getStochasticSubstanceUnit();

        if(argSimulationContext == null)//called from XmlReader.getSpeciesContextSpec(Element)
        {
            fieldParameters[ROLE_InitialConcentration] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialConcentration], null,
                    ROLE_InitialConcentration, spContextUnit,
                    RoleDescriptions[ROLE_InitialConcentration]);

            fieldParameters[ROLE_InitialCount] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialCount], null,
                    ROLE_InitialCount, stochasticSubstanceUnit,
                    RoleDescriptions[ROLE_InitialCount]);
        } else //called from ReactionContext.refreshSpeciesContextSpecs()
        {
            if(argSimulationContext.isUsingConcentration()){
                fieldParameters[ROLE_InitialConcentration] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialConcentration], new Expression(0),
                        ROLE_InitialConcentration, spContextUnit,
                        RoleDescriptions[ROLE_InitialConcentration]);

                fieldParameters[ROLE_InitialCount] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialCount], null,
                        ROLE_InitialCount, stochasticSubstanceUnit,
                        RoleDescriptions[ROLE_InitialCount]);
            } else {
                fieldParameters[ROLE_InitialConcentration] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialConcentration], null,
                        ROLE_InitialConcentration, spContextUnit,
                        RoleDescriptions[ROLE_InitialConcentration]);

                fieldParameters[ROLE_InitialCount] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialCount], new Expression(0),
                        ROLE_InitialCount, stochasticSubstanceUnit,
                        RoleDescriptions[ROLE_InitialCount]);
            }
        }
        fieldParameters[ROLE_DiffusionRate] = new SpeciesContextSpecParameter(RoleNames[ROLE_DiffusionRate], new Expression(0.0),
                ROLE_DiffusionRate, modelUnitSystem.getDiffusionRateUnit(),
                RoleDescriptions[ROLE_DiffusionRate]);

        fieldParameters[ROLE_BoundaryValueXm] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueXm], null,
                ROLE_BoundaryValueXm, fluxUnits,
                RoleDescriptions[ROLE_BoundaryValueXm]);

        fieldParameters[ROLE_BoundaryValueXp] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueXp], null,
                ROLE_BoundaryValueXp, fluxUnits,
                RoleDescriptions[ROLE_BoundaryValueXp]);

        fieldParameters[ROLE_BoundaryValueYm] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueYm], null,
                ROLE_BoundaryValueYm, fluxUnits,
                RoleDescriptions[ROLE_BoundaryValueYm]);

        fieldParameters[ROLE_BoundaryValueYp] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueYp], null,
                ROLE_BoundaryValueYp, fluxUnits,
                RoleDescriptions[ROLE_BoundaryValueYp]);

        fieldParameters[ROLE_BoundaryValueZm] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueZm], null,
                ROLE_BoundaryValueZm, fluxUnits,
                RoleDescriptions[ROLE_BoundaryValueZm]);

        fieldParameters[ROLE_BoundaryValueZp] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueZp], null,
                ROLE_BoundaryValueZp, fluxUnits,
                RoleDescriptions[ROLE_BoundaryValueZp]);
        VCUnitDefinition velocityUnit = getLengthPerTimeUnit();
        fieldParameters[ROLE_VelocityX] = new SpeciesContextSpecParameter(RoleNames[ROLE_VelocityX], null,
                ROLE_VelocityX, velocityUnit, RoleDescriptions[ROLE_VelocityX]);

        fieldParameters[ROLE_VelocityY] = new SpeciesContextSpecParameter(RoleNames[ROLE_VelocityY], null,
                ROLE_VelocityY, velocityUnit, RoleDescriptions[ROLE_VelocityY]);

        fieldParameters[ROLE_VelocityZ] = new SpeciesContextSpecParameter(RoleNames[ROLE_VelocityZ], null,
                ROLE_VelocityZ, velocityUnit, RoleDescriptions[ROLE_VelocityZ]);

        resetDefaults();
        refreshDependencies();
        if(argSimulationContext.getGeometryContext().getGeometry().getDimension() > 0){
            initializeForSpatial();
        }
    }


    private VCUnitDefinition getLengthPerTimeUnit(){
        ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
        VCUnitDefinition lengthPerTimeUnit = modelUnitSystem.getLengthUnit().divideBy(modelUnitSystem.getTimeUnit());
        return lengthPerTimeUnit;
    }

    public void initializeForSpatial(){
        if(getDiffusionParameter() != null && getDiffusionParameter().getExpression() != null && getDiffusionParameter().getExpression().isZero()){
            Expression e = null;
            ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
            VCUnitDefinition micronsqpersecond = modelUnitSystem.getInstance("um2.s-1");
            if(speciesContext.getStructure() instanceof Feature){
                RationalNumber rn = RationalNumber.getApproximateFraction(micronsqpersecond.convertTo(10, getDiffusionParameter().getUnitDefinition()));
                e = new Expression(rn.doubleValue());
            } else if(speciesContext.getStructure() instanceof Membrane){
                RationalNumber rn = RationalNumber.getApproximateFraction(micronsqpersecond.convertTo(0.1, getDiffusionParameter().getUnitDefinition()));
                e = new Expression(rn.doubleValue());
            } else {
                RationalNumber rn = RationalNumber.getApproximateFraction(micronsqpersecond.convertTo(1.0, getDiffusionParameter().getUnitDefinition()));
                e = new Expression(rn.doubleValue());
            }
            try {
                getDiffusionParameter().setExpression(e);
            } catch(ExpressionBindingException e1){
                throw new RuntimeException("Error while initializing diffusion rate, " + e1.getMessage(), e1);
            }
        }
    }

    public void initializeForSpringSaLaD(MolecularType molecularType) {
        // we need to make sure that the mcp in the siteAttributesMap:
        // Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMap = scs.getSiteAttributesMap();
        // are the same as the authoritative mcp from the physiology:
        // scs -> sc -> sp -> mtp -> mcp, mcp,...

        // step 1: we solve the scs -> sc -> sp -> mtp -> mcp, mcp sequence
        if(getSpeciesContext() != null) {
            SpeciesPattern sp = getSpeciesContext().getSpeciesPattern();
            if (sp == null) {
                return;
            }
            // in SpringSaLaD all seed species are single molecule, we don't use complexes
            MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);
            MolecularType mt = mtp.getMolecularType();
            if (molecularType != null && molecularType != mt) {
                return;        // this molecule is unchanged, nothing to do
            }

            boolean initialPass = false;
            // molecularType == null : full initialization
            // molecularType != null and molecularType == mt : this molecule has a different
            //   number of components or a different component order
            List<MolecularComponent> componentList = mt.getComponentList();

            // step 1.1: superficial sanity check, before changing anything
            final String saMapExceptionPrefix = "SiteAttributeMap of SpeciesContextSpec of Species '" + getSpeciesContext().getName() + "' ";
            if(siteAttributesMap == null || siteAttributesMap.isEmpty()) {
                initialPass = true; // map is empty, initialization phase
            } else {                // if map not empty, now we need to check
                                    // we don't fire exceptions, size mismatch may be correct if we delete or add sites to a molecule
                if (siteAttributesMap.size() > componentList.size()) {
                    System.out.println(saMapExceptionPrefix + "has obsolete attributes");
                } else if (siteAttributesMap.size() < componentList.size()) {
                    System.out.println(saMapExceptionPrefix + "has missing attributes");
                } else {
                    for (MolecularComponent mc : componentList) {
                        MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
                        if (!siteAttributesMap.containsKey(mcp)) {
                            System.out.println(saMapExceptionPrefix + "mismatching key found (MolecularComponentPattern)");
                        }
                    }
                }
            }
            // here the work is being done
            //
            // step 2: build the set of "old" mcp from the siteAttributesMap
            Set<MolecularComponentPattern> oldMcpSet = new LinkedHashSet<> ();
            for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> entry : siteAttributesMap.entrySet()) {
                oldMcpSet.add(entry.getKey());
            }

            // step 3.1: hard compare (instance match) of the authoritative mcp (from the physiology) with the mcp in siteAttributesMap
            for (MolecularComponent mc : componentList) {
                MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
                if (siteAttributesMap.containsKey(mcp)) {
                    oldMcpSet.remove(mcp);      // perfect instance match between physiology and siteAttributesMap, stays as is
                }
            }
            // step 3.2: soft compare (using compareEqual)
//            Set <MolecularComponentPattern> mcpToAdd = new LinkedHashSet<> ();      // new mcp instances to add
            Set <MolecularComponentPattern> mcpToRemove = new LinkedHashSet<> ();   // obsolete instances of mcp to be replaced by the new above
            for (MolecularComponent mc : componentList) {
                MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
                if (siteAttributesMap.containsKey(mcp)) {
                    continue;       // dealt with it already
                }
                for(MolecularComponentPattern mcpOld : oldMcpSet) {
                    if(mcp.compareEqual(mcpOld)) {
//                        mcpToAdd.add(mcp);
                        mcpToRemove.add(mcpOld);
                        // we recover the sas from the obsolete mcpOld and transfer it to the new mcp
                        // since we do not loop through siteAttributesMap we can add right here
                        SiteAttributesSpec sas = siteAttributesMap.get(mcpOld);
                        siteAttributesMap.put(mcp, sas);
                        siteAttributesMap.remove(mcpOld);
                        break;      // can't be more than one
                    }
                }
            }
            oldMcpSet.removeAll(mcpToRemove);   // remove the old mcp we have just dealt with

            // step 3.3: whatever is left in the oldMcpSet doesn't exist anymore in the authoritative mcp list,
            // could be a mc that has been deleted
            for(MolecularComponentPattern mcpOld : oldMcpSet) {
                siteAttributesMap.remove(mcpOld);
            }
            oldMcpSet.clear();

            // step 3.4: we add any new instance of authoritative mcp not there yet, and we initialize with default sas
            boolean isMmembraneMolecule = false;
            boolean hasAnchor = false;
            int anchorIndex = 0;
            if(getSpeciesContext().getStructure().getName().equals(Structure.SpringStructureEnum.Membrane.columnName)) {
                isMmembraneMolecule = true;
                // in a membrane molecule, we arrange Sites left to the anchor as located in Extracellular,
                // the Anchor site is on the Membrane,
                // the sites right to the anchor are to be initialized as located in Intracellular
                for (int count = 0; count<componentList.size(); count++) {
                    MolecularComponent mc = componentList.get(count);
                    if(mc.getName().equals(SpeciesContextSpec.AnchorSiteString)) {
                        hasAnchor = true;
                        anchorIndex = count;
                    }
                }
                if(isMmembraneMolecule != true || hasAnchor != true) {
                    // useful just for debugging
                    System.out.println("membrane molecule must have an anchor.");
                }
            }
            for (int componentCount=0; componentCount < componentList.size(); componentCount++) {
                MolecularComponent mc = componentList.get(componentCount);
                MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
                if (siteAttributesMap.containsKey(mcp)) {
                    continue;       // exists, already dealt with
                }
                SiteAttributesSpec sas;     // we have no sas for this mcp in the siteAttributesMap, so we make one
                if(hasAnchor) {
                    if(componentCount < anchorIndex) {          // Extracellular
                        Structure struct = getSimulationContext().getModel().getStructure(Structure.SpringStructureEnum.Extracellular.columnName);
                        sas = new SiteAttributesSpec(this, mcp, struct);
                    } else if(componentCount > anchorIndex) {   // Intracellular
                        Structure struct = getSimulationContext().getModel().getStructure(Structure.SpringStructureEnum.Intracellular.columnName);
                        sas = new SiteAttributesSpec(this, mcp, struct);
                    } else {    // Anchor, the only site located on the Membrane
                        Structure struct = getSimulationContext().getModel().getStructure(Structure.SpringStructureEnum.Membrane.columnName);
                        sas = new SiteAttributesSpec(this, mcp, struct);
                    }
                } else {
                    sas = new SiteAttributesSpec(this, mcp, getSpeciesContext().getStructure());
                }
                if(initialPass == true) {
                    Coordinate coordinate = new Coordinate(0, INITIAL_YZ_SITE_OFFSET, INITIAL_YZ_SITE_OFFSET + (componentCount * 4));
                    sas.setCoordinate(coordinate);
                    NamedColor nextColor;
                    if(componentCount == anchorIndex) {
                        nextColor = Colors.DARKGRAY;
                    } else {
                        nextColor = Colors.COLORARRAY[componentCount];
                    }
                    sas.setColor(nextColor);
                } else {    // if this is a new site added to an existing molecule, the existing sites already have
                    ;       // attributes (like coordinates, diffusion rates, colors) and links.
                            // We cannot guess how the user will want to deal with the new site.
                }
                siteAttributesMap.put(mcp, sas);
            }
            // at this point the siteAttributesMap should be fully reconstructed

            // sanity checks, after (the current (up to date) instances can be obtained
            // only via SpeciesContext -> SpeciesPattern -> MolecularTypePattern -> MolecularComponentPattern
            // all the site attribute map elements and all internal link set elements must use the right instances of these mcp
//            if(molecularType == null) {
//                return; // we don't need to do sanity check when we start clean (do we? does it hurt if we do? can it possibly fail?)
//            }
            if(siteAttributesMap.size() > componentList.size()) {
                throw new RuntimeException(saMapExceptionPrefix + "has obsolete attributes");
            } else if(siteAttributesMap.size() < componentList.size()) {
                throw new RuntimeException(saMapExceptionPrefix + "has missing attributes");
            } else {
                for (MolecularComponent mc : componentList) {
                    MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
                    if (!siteAttributesMap.containsKey(mcp)) {
                        throw new RuntimeException(saMapExceptionPrefix + "mismatching key found (MolecularComponentPattern)");
                    }
                }
            }
            //  ======== dealing with the links ====================================================
            //
            // if the list is empty, we just add a full set of default links
            if (getInternalLinkSet().isEmpty()) {
                for (int i = 0; i < componentList.size() - 1; i++) {
                    MolecularComponent mcOne = componentList.get(i);
                    MolecularComponent mcTwo = componentList.get(i + 1);
                    MolecularComponentPattern mcpOne = mtp.getMolecularComponentPattern(mcOne);
                    MolecularComponentPattern mcpTwo = mtp.getMolecularComponentPattern(mcTwo);
                    MolecularInternalLinkSpec link = new MolecularInternalLinkSpec(this, mcpOne, mcpTwo);
                    // TODO: set x,y,z instead, link will be computed
//				link.setLinkLength(2.0);
                    internalLinkSet.add(link);
                }
                return;     // initial initialization done, nothing else to do
            }

            // step 1.1: build the set of "old" mcp and mils from the siteAttributesMap
            oldMcpSet = new LinkedHashSet<> ();     // Set<MolecularComponentPattern>
            for(MolecularInternalLinkSpec oldMils : getInternalLinkSet()) {
                MolecularComponentPattern mcpOne = oldMils.getMolecularComponentPatternOne();
                MolecularComponentPattern mcpTwo = oldMils.getMolecularComponentPatternTwo();
                oldMcpSet.add(mcpOne);
                oldMcpSet.add(mcpTwo);
            }
            // step 1.2: we build set of authoritative MolecularComponentPattern from the SpeciesPattern
            Set<MolecularComponentPattern> authoritativeMcpSet = new LinkedHashSet<>();
            for (MolecularComponent mc : componentList) {
                MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
                authoritativeMcpSet.add(mcp);
            }

            // step 2: make a corresponding map between oldMcp and authoritative mcp
            Map <MolecularComponentPattern, MolecularComponentPattern> oldToNewMcp = new LinkedHashMap<> ();
            Set<MolecularComponentPattern> mcpToRemoveSet = new LinkedHashSet<> (); // these will be removed from authoritativeMcpSet
            // step 2.1: hard compare (instance match) of the authoritative mcp (from the physiology) with the mcp in siteAttributesMap
            for(MolecularComponentPattern oldMcp : oldMcpSet) {
                if (authoritativeMcpSet.contains(oldMcp)) {
                    oldToNewMcp.put(oldMcp, oldMcp);
                    mcpToRemoveSet.add(oldMcp);
                }
            }
            // once we have an instance match we don't want to again try a compareEqual() match,
            // which will obviously succed and risk producing duplicates
            authoritativeMcpSet.removeAll(mcpToRemoveSet);
            oldMcpSet.removeAll(mcpToRemoveSet);
            mcpToRemoveSet.clear();

            // step 2.2: soft compare, using compareEqual()
            for(MolecularComponentPattern oldMcp : oldMcpSet) {
                for(MolecularComponentPattern newMcp : authoritativeMcpSet) {
                    if(oldMcp.compareEqual(newMcp)) {
                        oldToNewMcp.put(oldMcp, newMcp);
                        mcpToRemoveSet.add(newMcp);
                    }
                }
            }
            // everything that matched one way or another is dealt with, whatever remains is unlinked sites
            // perhaps a newly added site
            authoritativeMcpSet.removeAll(mcpToRemoveSet);
            oldMcpSet.removeAll(mcpToRemoveSet);
            mcpToRemoveSet.clear();
            // any mcp still present in the authoritativeMcpSet was newly added
            // not sure if we need to do anything about it
            if(!authoritativeMcpSet.isEmpty()) {
                System.out.println("Unlinked mcp in authoritativeMcpSet");
            }

            // step 3: we iterate through the internal link set, we replace all mcs with the new mcs in oldToNewMcp
            // any mcp not found there was deleted, we must delete the link containing it
            Set<MolecularInternalLinkSpec> linksToRemove = new LinkedHashSet<> ();
            for(MolecularInternalLinkSpec oldMils : getInternalLinkSet()) {
                Pair<MolecularComponentPattern, MolecularComponentPattern> link = oldMils.getLink();
                MolecularComponentPattern mcpOne = oldMils.getMolecularComponentPatternOne();
                MolecularComponentPattern mcpTwo = oldMils.getMolecularComponentPatternTwo();
                if(!oldToNewMcp.containsKey(mcpOne) || !oldToNewMcp.containsKey(mcpTwo)) {
                    linksToRemove.add(oldMils);
                } else {
                    Pair<MolecularComponentPattern, MolecularComponentPattern> newLink = new Pair(oldToNewMcp.get(mcpOne), oldToNewMcp.get(mcpTwo));
                    oldMils.setLink(newLink);
                }
            }
            getInternalLinkSet().removeAll(linksToRemove);

            String ilSetExceptionPrefix = "InternalLinkSet of SpeciesContextSpec of Species '" + getSpeciesContext().getName() + "' ";
            // step 4: sanity check on links (after)
            // step 4.1: we build a new authoritativeMcpSet2 (the old authoritativeMcpSet still contains mcp that are unlinked)
            Set<MolecularComponentPattern> authoritativeMcpSet2 = new LinkedHashSet<>();
            for (MolecularComponent mc : componentList) {
                MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
                authoritativeMcpSet2.add(mcp);
            }
            // step 4.2: we can't assume how many links we have, even 0 is possible if we deleted them manually
            // hoever, any mcp instance present in a link must be valid (present in the authoritativeMcpSet)
            for(MolecularInternalLinkSpec link : getInternalLinkSet()) {
                MolecularComponentPattern mcpOne = link.getMolecularComponentPatternOne();
                MolecularComponentPattern mcpTwo = link.getMolecularComponentPatternTwo();
                if(!authoritativeMcpSet2.contains(mcpOne)) {
                    throw new RuntimeException(ilSetExceptionPrefix + "has invalid MolecularComponentPattern instance");
                }
                if(!authoritativeMcpSet2.contains(mcpTwo)) {
                    throw new RuntimeException(ilSetExceptionPrefix + "has invalid MolecularComponentPattern instance");
                }
            }
        }
    }

    public VCUnitDefinition computeFluxUnit(){
        return speciesContext.getUnitDefinition().multiplyBy(getLengthPerTimeUnit());
    }


    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener){
        getPropertyChange().addPropertyChangeListener(listener);
    }


    /**
     * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
     */
    public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener){
        getVetoPropertyChange().addVetoableChangeListener(listener);
    }

    public SpeciesContextSpecProxyParameter addProxyParameter(SymbolTableEntry symbolTableEntry){
        if(getParameterFromName(symbolTableEntry.getName()) != null){
            throw new RuntimeException("local parameter '" + symbolTableEntry.getName() + "' already exists");
        }
        if(getProxyParameter(symbolTableEntry.getName()) != null){
            throw new RuntimeException("referenced external symbol '" + symbolTableEntry.getName() + "' already exists");
        }
        SpeciesContextSpecProxyParameter newProxyParameter = new SpeciesContextSpecProxyParameter(symbolTableEntry);
        SpeciesContextSpecProxyParameter[] newProxyParameters = ArrayUtils.addElement(fieldProxyParameters, newProxyParameter);
        setProxyParameters(newProxyParameters);
        return newProxyParameter;
    }

    /**
     * @param object java.lang.Object
     * @return boolean
     */
    public boolean compareEqual(Matchable object){

        if(!(object instanceof SpeciesContextSpec)){
            return false;
        }
        SpeciesContextSpec scs = (SpeciesContextSpec) object;   // we know it's not null

        if(!Compare.isEqual(speciesContext, scs.speciesContext)){
            return false;
        }

        if(bClamped != scs.bClamped){
            return false;
        }

//	if (bEnableDiffusing != scs.bEnableDiffusing){
//		return false;
//	}

        if(bWellMixed != null && scs.bWellMixed != null){
            if(!bWellMixed.equals(scs.bWellMixed)){
                return false;
            }
        } else if(bWellMixed != null || scs.bWellMixed != null){
            return false; // one is specified and one isn't ...
        }

        if(bForceContinuous != null && scs.bForceContinuous != null){
            if(!bForceContinuous.equals(scs.bForceContinuous)){
                return false;
            }
        } else if(bForceContinuous != null || scs.bForceContinuous != null){
            return false; // one is specified and one isn't ...
        }

        if(!Compare.isEqual(fieldParameters, scs.fieldParameters)){
            return false;
        }

        if(internalLinkSet.size() != scs.internalLinkSet.size()) {      // springsalad MolecularInternalLinkSpec set
            return false;
        }
        for(MolecularInternalLinkSpec ourMils : internalLinkSet) {
            if (!containsIsomorph(ourMils, scs.internalLinkSet)) {
                return false;
            }
        }

        if(siteAttributesMap.size() != scs.siteAttributesMap.size()) {  // springsalad SiteAttributesSpec map
            return false;
        }
        for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> entry : siteAttributesMap.entrySet()) {
            if (!containsIsomorph(entry, siteAttributesMap)) {
                return false;
            }
        }

        return true;
    }

    private static boolean containsIsomorph(MolecularInternalLinkSpec ourMils, Set<MolecularInternalLinkSpec> theirMilsSet) {
        for(MolecularInternalLinkSpec theirMils : theirMilsSet) {
            if(ourMils.compareEqual(theirMils)) {
                return true;
            }
        }
        return false;
    }
    private static boolean containsIsomorph(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> ourEntry, Map<MolecularComponentPattern, SiteAttributesSpec> theirSasMap) {
        MolecularComponentPattern ourMcp = ourEntry.getKey();
        SiteAttributesSpec ourSas = ourEntry.getValue();
        for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> theirEntry : theirSasMap.entrySet()) {
            MolecularComponentPattern theirMcp = theirEntry.getKey();
            SiteAttributesSpec theirSas = theirEntry.getValue();
            // TODO: infinite loop if we compare the sas here, because that will compare the scs, which will again compare the sas, aso
            if(ourMcp.compareEqual(theirMcp)/* && ourSas.compareEqual(theirSas)*/) {
                // note that for the mcp we only compare the bond type, not the bond (which is mtp attribute)
                return true;
            }
        }
        return false;
    }

    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * The fireVetoableChange method was generated to support the vetoPropertyChange field.
     */
    public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException{
        getVetoPropertyChange().fireVetoableChange(evt);
    }


    /**
     * The fireVetoableChange method was generated to support the vetoPropertyChange field.
     */
    public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException{
        getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
    }


    /**
     * The fireVetoableChange method was generated to support the vetoPropertyChange field.
     */
    public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException{
        getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
    }


    /**
     * The fireVetoableChange method was generated to support the vetoPropertyChange field.
     */
    public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException{
        getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
    }


    /**
     * Insert the method's description here.
     * Creation date: (11/1/2005 10:03:46 AM)
     *
     * @param issueVector java.util.Vector
     */
    public void gatherIssues(IssueContext issueContext, List<Issue> issueVector){
        issueContext = issueContext.newChildContext(ContextType.SpeciesContextSpec, this);
        if(getSimulationContext().getGeometry().getDimension() > 0 && !isWellMixed()){
            try {
                Double cv = getDiffusionParameter().getConstantValue();
                if(cv < 0){
                    String msg = "A diffusion coefficient must be a positive number or zero.";
                    String tip = "A negative diffusion coefficient would be unphysical in molecular transport.";
                    issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
                } else if(cv == 0){
                    boolean isFoundInFlux = false;    // true if a species with diff coefficient 0 is a participant in a flux
                    SpeciesContext sc = getSpeciesContext();
                    StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(sc.getStructure());        // of species context
                    GeometryClass speciesGeometryClass = sm.getGeometryClass();
                    ReactionContext rc = getSimulationContext().getReactionContext();
                    ReactionSpec[] rsArray = rc.getReactionSpecs();
                    for(ReactionSpec rSpec : rsArray){
                        ReactionStep rs = rSpec.getReactionStep();
                        if(rSpec.isExcluded() || rs.getStructure() == null){
                            continue;            // we only care about reactions which are not excluded
                        }
                        GeometryClass reactionGeometryClass = getSimulationContext().getGeometryContext().getStructureMapping(rs.getStructure()).getGeometryClass();    // of reaction

                        // if the speciesContext is "inside" or "outside" the membrane
                        if(reactionGeometryClass instanceof SurfaceClass && speciesGeometryClass instanceof SubVolume && ((SurfaceClass) reactionGeometryClass).isAdjacentTo((SubVolume) speciesGeometryClass)){
                            for(ReactionParticipant p : rs.getReactionParticipants()){
                                if(p instanceof Product || p instanceof Reactant){
                                    SpeciesContextSpec candidate = rc.getSpeciesContextSpec(p.getSpeciesContext());
                                    if(candidate == this){
                                        isFoundInFlux = true;
                                        String msg = "The diffusion coefficient of a species in a trans-membrane reaction must be a positive number.";
                                        String tip = "Set the diffusion rate to a positive value or disable those trans-membrane reactions in Specifications-Reactions.";
                                        issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                                        break;
                                    }
                                }
                            }
                        }
                        if(isFoundInFlux){
                            break;        // we already made an issue for "this" having a diffusion coefficient 0 in a "flux", don't need more
                        }
                    }
                }
            } catch(ExpressionException e){
                ;        // not an error
            }
        }
        //
        // add constraints (simpleBounds) for predefined parameters
        //
        for(int i = 0; i < fieldParameters.length; i++){
            RealInterval simpleBounds = parameterBounds[fieldParameters[i].getRole()];
            if(simpleBounds != null){
                String parmName = fieldParameters[i].getNameScope().getName() + "." + fieldParameters[i].getName();
                issueVector.add(new SimpleBoundsIssue(fieldParameters[i], issueContext, simpleBounds, "parameter " + parmName + ": must be within " + simpleBounds.toString()));
            }
        }
        if(bForceContinuous && !bClamped && getSimulationContext().isStoch() && (getSimulationContext().getGeometry().getDimension() > 0)){    // if it's particle or constant we're good
            SpeciesContext sc = getSpeciesContext();
            ReactionContext rc = getSimulationContext().getReactionContext();
            ReactionSpec[] rsArray = rc.getReactionSpecs();
            for(ReactionSpec rs : rsArray){
                if(!rs.isExcluded()){    // we only care about reactions which are not excluded
                    boolean iAmParticipant = false;    // true if "this" is part of current reaction
                    boolean haveParticle = false;    // true if current reaction has at least a particle participant
                    ReactionStep step = rs.getReactionStep();
                    for(ReactionParticipant p : step.getReactionParticipants()){
                        if(p instanceof Product || p instanceof Reactant){
                            SpeciesContextSpec candidate = rc.getSpeciesContextSpec(p.getSpeciesContext());
                            if(candidate == this){
                                iAmParticipant = true;
                            } else if(!candidate.isForceContinuous() && !candidate.isConstant()){
                                haveParticle = true;
                            }
                        }
                    }
                    if(iAmParticipant && haveParticle){
                        String msg = "Continuous Species won't conserve mass in particle reaction " + rs.getReactionStep().getName() + ".";
                        String tip = "Mass conservation for reactions of binding between discrete and continuous species is handled approximately. <br>" +
                                "To avoid any algorithmic approximation, which may produce undesired results, the user is advised to indicate <br>" +
                                "the continuous species in those reactions as modifiers (i.e. 'catalysts') in the physiology.";
                        issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.SEVERITY_WARNING));
                        break;    // we issue warning as soon as we found the first reaction which satisfies criteria
                    }
                }
            }
        }
        if(!bForceContinuous && bClamped){
            if(getSimulationContext().isStoch() && (getSimulationContext().getGeometry().getDimension() > 0)){
                String msg = "Clamped Species must be continuous rather than particles.";
                String tip = "If choose 'clamped', must also choose 'forceContinuous'";
                issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.SEVERITY_ERROR));
            }
        }
        if(bForceContinuous && !bClamped){
            if(getSimulationContext().isStoch() && (getSimulationContext().getGeometry().getDimension() == 0)){
                String msg = "Non-constant species is forced continuous, not supported for nonspatial stochastic applications.";
                issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_ERROR));
            }
        }
        if(getSimulationContext().getAssignmentRules() != null && getSimulationContext().getAssignmentRules().length > 0){
            for(AssignmentRule ar : getSimulationContext().getAssignmentRules()){
                if(ar.getAssignmentRuleVar() == null){
                    continue;                // some assignment rule variable may be still not initialized
                }
                SpeciesContext sc = getSpeciesContext();
                if(sc.getName().equals(ar.getAssignmentRuleVar().getName())){
                    if(!bClamped){        // the assignment rule variables must be clamped
                        String msg = "Assignment rule species variables must be Clamped";
                        String tip = "Used in " + AssignmentRule.typeName + " '" + ar.getDisplayName() + "'";
                        issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
                    }
                }
            }
        }
        if(getSimulationContext().getRateRules() != null && getSimulationContext().getRateRules().length > 0){
            for(RateRule ar : getSimulationContext().getRateRules()){
                if(ar.getRateRuleVar() == null){
                    continue;                // some rate rule variable may be still not initialized
                }
                SpeciesContext sc = getSpeciesContext();
                if(sc.getName().equals(ar.getRateRuleVar().getName())){
                    if(!bClamped){        // the rate rule variables must be clamped
                        String msg = "Rate rule species variables must be Clamped";
                        String tip = "Used in " + RateRule.typeName + " '" + ar.getDisplayName() + "'";
                        issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
                    }
                }
            }
        }

        if(getSimulationContext().getApplicationType() == SimulationContext.Application.SPRINGSALAD){
            SpeciesContext sc = getSpeciesContext();
            if(sc != null && sc.getSpeciesPattern() != null){
                SpeciesPattern sp = sc.getSpeciesPattern();
                List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns();
                if(mtpList.size() != 1){
                    String msg = "SpringSaLaD requires all Species to be associated with exactly one MolecularType.";
                    String tip = msg;
                    issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                    return;
                }
                MolecularTypePattern mtp = mtpList.get(0);
                List<MolecularComponentPattern> mcpList = mtp.getComponentPatternList();
                if(SourceMoleculeString.equals(sc.getName()) || SinkMoleculeString.equals(sc.getName())){
                    if(mcpList.size() != 0){
                        String msg = "SpringSaLaD reserved Molecules '" + SourceMoleculeString + "' and '" + SinkMoleculeString + "' must not have any sites defined";
                        String tip = msg;
                        issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                        return;
                    }
                }
                if(mcpList.size() == 0){
                    String msg = "SpringSaLaD requires the MolecularType to have at least one Site.";
                    String tip = msg;
                    issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                    return;
                }
                for(MolecularComponentPattern mcp : mcpList){
                    MolecularComponent mc = mcp.getMolecularComponent();
                    List<ComponentStateDefinition> csd = mc.getComponentStateDefinitions();
                    if(csd.size() < 1){
                        String msg = "Each Site must have at least one State.";
                        String tip = msg;
                        issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                        return;
                    }
                }
                for(MolecularInternalLinkSpec mils : getInternalLinkSet()){
                    if(mils.getMolecularComponentPatternOne() == mils.getMolecularComponentPatternTwo()){
                        String msg = "Both sites of the Link are identical. A site cannot be linked to itself.";
                        String tip = msg;
                        issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                        return;
                    }
                }
                if(mcpList.size() > 1 && getInternalLinkSet().size() > 0) {
                    GraphContinuity.Graph graph = new GraphContinuity.Graph(mcpList.size());
                    Map<MolecularComponentPattern, Integer> mcpMap = new LinkedHashMap<> ();
                    for(int i=0; i<mcpList.size(); i++) {
                        MolecularComponentPattern mcp = mcpList.get(i);
                        mcpMap.put(mcp, i);
                    }
                    for(MolecularInternalLinkSpec mils : getInternalLinkSet()) {
                        Pair<MolecularComponentPattern, MolecularComponentPattern> link = mils.getLink();
                        int one = mcpMap.get(link.one);
                        int two = mcpMap.get(link.two);
                        graph.addEdge(one, two);
                    }
                    if(!graph.isConnected(GraphContinuity.Algorithm.DFS)) {     // let's use DFS!
                        String msg = "Link chain within the molecule has at least one discontinuity. One or more links are missing.";
                        String tip = msg;
                        issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                        return;
                    }
                }
                for(MolecularInternalLinkSpec candidate : getInternalLinkSet()){
                    for(MolecularInternalLinkSpec other : getInternalLinkSet()){
                        if(candidate == other){
                            continue;
                        }
                        if(candidate.compareEqual(other)){
                            String one = candidate.getMolecularComponentPatternOne().getMolecularComponent().getName();
                            String two = candidate.getMolecularComponentPatternTwo().getMolecularComponent().getName();
                            String msg = "Duplicate link: " + one + " :: " + two;
                            String tip = msg;
                            issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                            return;
                        }
                    }
                }
                if(mcpList.size() == 1){
                    String msg = "Internal Links are possible only when the Molecule has at least 2 sites.";
                    String tip = msg;
                    issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                }

                // if the species context is on membrane it must have a site named Anchor on the membrane, the other sites must NOT be on membrane
                Structure struct = sc.getStructure();
                MolecularComponentPattern mcpAnchor = null;
                if(struct instanceof Membrane) {
                    // this deals with the unified concept of anchoring
                    // any membrane molecule needs to have its molecular type anchored to one membrane only
                    // transport is not compatible with springsalad
                    // we assume there is only one membrane named Membrane (springsalad restriction)
                    {
                        String msg = "SpringSaLaD requires that the Molecule must be anchored to one Membrane only.";
                        String tip = msg;
                        MolecularType mt = mtp.getMolecularType();
                        if (mt.isAnchorAll() || mt.getAnchors().size() != 1) {
                            issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                            return;
                        } else {
                            Structure mtAnchorStruct = mt.getAnchors().stream().findFirst().get();  // get the first (and only) anchor
                            if (struct != mtAnchorStruct) {
                                issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                                return;
                            }
                        }
                    }
                    // TODO: this deals with the implementation that needs an explicit extra site named Anchor
                    boolean anchorExists = false;
                    for(MolecularComponentPattern mcp : mcpList){
                        MolecularComponent mc = mcp.getMolecularComponent();
                        if(AnchorSiteString.equals(mc.getName())){
                            anchorExists = true;
                            mcpAnchor = mcp;
                            SiteAttributesSpec sas = getSiteAttributesMap().get(mcp);
                            if(!(sas.getLocation() instanceof Membrane)){
                                String msg = "The reserved Site 'Anchor' must be located on a Membrane.";
                                String tip = msg;
                                issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                                return;
                            }
                        } else {    // all the other sites of a membrane species must not be on the membrane
                            SiteAttributesSpec sas = getSiteAttributesMap().get(mcp);
                            if(sas.getLocation() instanceof Membrane) {
                                String tip = "All the Sites of a Membrane species, other than the 'Anchor', must NOT be located on a Membrane.";
                                String msg = "Relocate the site '" + mc.getName() + "' inside a compartment, or rename it to 'Anchor'";
                                issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                                return;
                            }
                        }
                    }
                    // make sure that only one site is on the membrane
                    int numSitesOnMembrane = 0;
                    for(MolecularComponentPattern mcp : mcpList) {
                        SiteAttributesSpec sas = getSiteAttributesMap().get(mcp);
                        if(sas.getLocation() instanceof Membrane) {
                            numSitesOnMembrane++;
                        }
                        if(numSitesOnMembrane > 1) {
                            String msg = "Species localized on a membrane must have only one site on the membrane";
                            String tip = msg;
                            issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                            return;
                        }
                    }
                    if(anchorExists == false){
                        String msg = "Species localized on a membrane require a reserved site named 'Anchor'";
                        String tip = msg;
                        issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                        return;
                    } else {
                        // the anchor Site must have just one single State, also named Anchor
                        String msg = "The reserved Site 'Anchor' must have exactly one State named 'Anchor'.";
                        MolecularComponent mcAnchor = mcpAnchor.getMolecularComponent();
                        List<ComponentStateDefinition> csdAnchorList = mcAnchor.getComponentStateDefinitions();
                        if(csdAnchorList.size() != 1){
                            String tip = msg;
                            issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                            return;
                        }
                        ComponentStateDefinition csdAnchor = csdAnchorList.get(0);
                        if(!AnchorStateString.equals(csdAnchor.getName())){
                            String tip = msg;
                            issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                            return;
                        }
                    }
                    if(anchorExists == true) {
                        // sites at the "left" of the anchor (site z < anchor z) must be located in 'Extracellular'
                        // sites at the 'right' of the anchor (site z > anchor z) must be located in 'Intracellular'
                        boolean foundSomething = false;
                        SiteAttributesSpec sasAnchor = getSiteAttributesMap().get(mcpAnchor);
                        double zAnchor = sasAnchor.getCoordinate().getZ();
                        for(MolecularComponentPattern mcp : mcpList) {
                            MolecularComponent mc = mcp.getMolecularComponent();
                            SiteAttributesSpec sasCandidate = getSiteAttributesMap().get(mcp);
                            if(sasCandidate.getLocation() instanceof Membrane) {
                                continue;       // we skip self
                            }
                            double zCandidate = sasCandidate.getCoordinate().getZ();
                            if(sasCandidate.getLocation().getName().equals(Structure.SpringStructureEnum.Extracellular.columnName)) {
                                if(!(zCandidate < zAnchor)) {
                                    String tip = "Sites located in the 'Extracellular' structure must have their z-coordinate smaller that the one of the 'Anchor' site";
                                    String msg = "Relocate the site '" + mc.getName() + "' inside 'Intracellular', or decrease its 'z' coordinate to less than " + zAnchor;
                                    issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                                    foundSomething = true;
                                }
                            } else if(sasCandidate.getLocation().getName().equals(Structure.SpringStructureEnum.Intracellular.columnName)){
                                if(!(zCandidate > zAnchor)) {
                                    String tip = "Sites located in the 'Intracellular' structure must have their z-coordinate larger that the one of the 'Anchor' site";
                                    String msg = "Relocate the site '" + mc.getName() + "' inside 'Extracellular', or increase its 'z' coordinate to more than " + zAnchor;
                                    issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                                    foundSomething = true;
                                }
                            }
                        }
                        if(foundSomething == true) {
                            return;     // we show more than the 1st in this category, otherwise it'll be confusing
                        }
                    }
                } else {        // a species inside a Feature must NOT have a site named Anchor
                    for(MolecularComponentPattern mcp : mcpList){
                        MolecularComponent mc = mcp.getMolecularComponent();
                        if(AnchorSiteString.equals(mc.getName())){
                            String msg = "The reserved Site 'Anchor' can be used only for a Species located on a Membrane.";
                            String tip = msg;
                            issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                            return;
                        }
                    }
                }

                // each species context is associated with one and only one molecular type and viceversa (biunivocal correspondence)
                SpeciesContextSpec[] scsArray = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
                for(SpeciesContextSpec scsCandidate : scsArray){
                    SpeciesContext scCandidate = scsCandidate.getSpeciesContext();
                    if(sc == scCandidate){
                        continue;            // skip self
                    }
                    if(scCandidate.getSpeciesPattern() == null){
                        // invalid, we'll deal with it separately - skip for now since will generate a NPE
                        continue;
                    }
                    MolecularType mtCandidate = scCandidate.getSpeciesPattern().getMolecularTypePatterns().get(0).getMolecularType();
                    if(mtp.getMolecularType() == mtCandidate){
                        String msg = "There must be a biunivocal correspondence between the Species and the associated MolecularType.";
                        String tip = "In SpringSaLaD, two Seed Species cannot share the same Molecular Type.";
                        issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                        return;
                    }
                }

                // the species context and the molecular type must have the same name
                if(!sc.getName().equals(mtp.getMolecularType().getName())){
                    String msg = "The Species and the Molecular Type must share the same name.";
                    String tip = msg;
                    issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                    return;
                }

                // rate rules and assignment rules are not permitted
                // normally it is not possible to create RateRule or AssignmentRule using the vcell UI, the Protocols subpanels are disabled
                // we check this just in case the user is very crafty and finds a way
                boolean foundRuleMatch = false;
                if(simulationContext.getRateRules() != null && simulationContext.getRateRules().length > 0) {
                    for(RateRule rr : simulationContext.getRateRules()) {
                        if(rr.getRateRuleVar() == null) {
                            continue;
                        }
                        if(sc.getName().equals(rr.getRateRuleVar().getName())) {
                            String msg = "SpringSaLaD applications do not accept Rate Rules.";
                            String tip = msg;
                            issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                            return;
                        }
                    }
                }
                if(!foundRuleMatch && simulationContext.getAssignmentRules() != null && simulationContext.getAssignmentRules().length > 0) {
                    for(AssignmentRule rr : simulationContext.getAssignmentRules()) {
                        if(rr.getAssignmentRuleVar() == null) {
                            continue;
                        }
                        if(sc.getName().equals(rr.getAssignmentRuleVar().getName())) {
                            String msg = "SpringSaLaD applications do not accept Assignment Rules.";
                            String tip = msg;
                            issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
                            return;
                        }
                    }
                }
            } else {
                String msg = "SpringSaLaD requires all Species to be associated with a MolecularType.";
                String tip = "Associate a MolecularType to the Species in Physiology / Species panel.";
                issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
            }
        }
    }

    /**
     * @return cbit.vcell.parser.Expression
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryXmParameter(){
        return getParameterFromRole(ROLE_BoundaryValueXm);
    }


    /**
     * @return cbit.vcell.parser.Expression
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryXpParameter(){
        return getParameterFromRole(ROLE_BoundaryValueXp);
    }

    /**
     * @return cbit.vcell.parser.Expression
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryYmParameter(){
        return getParameterFromRole(ROLE_BoundaryValueYm);
    }

    /**
     * @return cbit.vcell.parser.Expression
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryYpParameter(){
        return getParameterFromRole(ROLE_BoundaryValueYp);
    }

    /**
     * @return cbit.vcell.parser.Expression
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryZmParameter(){
        return getParameterFromRole(ROLE_BoundaryValueZm);
    }

    /**
     * @return cbit.vcell.parser.Expression
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryZpParameter(){
        return getParameterFromRole(ROLE_BoundaryValueZp);
    }

    /**
     * @return cbit.vcell.parser.Expression
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getDiffusionParameter(){
        return getParameterFromRole(ROLE_DiffusionRate);
    }

    public SpeciesContextSpec.SpeciesContextSpecParameter getVelocityXParameter(){
        return getParameterFromRole(ROLE_VelocityX);
    }

    /**
     * @return cbit.vcell.parser.Expression
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getVelocityYParameter(){
        return getParameterFromRole(ROLE_VelocityY);
    }

    /**
     * @return cbit.vcell.parser.Expression
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getVelocityZParameter(){
        return getParameterFromRole(ROLE_VelocityZ);
    }

    /**
     * getEntry method comment.
     */
    public SymbolTableEntry getEntry(String identifierString){

        SymbolTableEntry ste = getLocalEntry(identifierString);
        if(ste != null){
            return ste;
        }

        ste = getNameScope().getExternalEntry(identifierString, this);

        if(ste instanceof SymbolTableFunctionEntry){
            return ste;
        }

        if(ste != null){
            if(ste instanceof SymbolTableFunctionEntry){
                return ste;
            } else {
                return addProxyParameter(ste);
            }
        }

        return null;
    }

    /**
     * @return java.lang.String
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getInitialConditionParameter(){
        SpeciesContextSpec.SpeciesContextSpecParameter initParam = null;
        if(getParameterFromRole(ROLE_InitialConcentration).getExpression() != null){
            initParam = getParameterFromRole(ROLE_InitialConcentration);
        } else if(getParameterFromRole(ROLE_InitialCount).getExpression() != null){
            initParam = getParameterFromRole(ROLE_InitialCount);
        }

        return initParam;
    }

    public SpeciesContextSpec.SpeciesContextSpecParameter getInitialConcentrationParameter(){
        return getParameterFromRole(ROLE_InitialConcentration);
    }

    public SpeciesContextSpec.SpeciesContextSpecParameter getInitialCountParameter(){
        return getParameterFromRole(ROLE_InitialCount);
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/8/2003 11:46:37 AM)
     *
     * @param identifier java.lang.String
     * @return SymbolTableEntry
     */
    public SymbolTableEntry getLocalEntry(java.lang.String identifier){
        SymbolTableEntry ste = null;

        ste = getParameterFromName(identifier);
        if(ste != null){
            return ste;
        }

        ste = getProxyParameter(identifier);
        if(ste != null){
            return ste;
        }

        return null;
    }


    /**
     * Insert the method's description here.
     * Creation date: (12/8/2003 11:46:37 AM)
     *
     * @return cbit.vcell.parser.NameScope
     */
    public NameScope getNameScope(){
        return nameScope;
    }


    public SpeciesContextSpec.SpeciesContextSpecParameter getParameterFromName(String name){
        for(int i = 0; i < fieldParameters.length; i++){
            if(fieldParameters[i].getName().equals(name)){
                return fieldParameters[i];
            }
        }
        return null;
    }


    /**
     * Insert the method's description here.
     * Creation date: (4/3/2004 10:48:38 AM)
     *
     * @param role int
     * @return cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
     */
    public SpeciesContextSpec.SpeciesContextSpecParameter getParameterFromRole(int role){
        for(int i = 0; i < fieldParameters.length; i++){
            if(fieldParameters[i].getRole() == role){
                return fieldParameters[i];
            }
        }
        return null;
    }


    /**
     * Gets the parameters property (cbit.vcell.model.Parameter[]) value.
     *
     * @return The parameters property value.
     * @see #setParameters
     */
    public Parameter[] getParameters(){
        return fieldParameters;
    }


    /**
     * Gets the parameters index property (cbit.vcell.model.Parameter) value.
     *
     * @param index The index value into the property array.
     * @return The parameters property value.
     * @see #setParameters
     */
    public Parameter getParameter(int index){
//	please reference getNumDisplayableParameters()(hardcoded number of parameters) to get more understanding.
        Parameter param = null;
        if(index == ROLE_InitialConcentration)//means take the initial conidtion parameter and concentration is being used
        {
            param = getInitialConditionParameter();

        } else if(index < getParameters().length){
            param = getParameters()[index]; //using iniAmount || other parameters now is one index greater than previous index(since iniAmount has added)
        } else {
            param = getProxyParameters()[index - getParameters().length];
        }
        return param;
    }

    public SpeciesContextSpecProxyParameter getProxyParameter(String pName){
        if(fieldProxyParameters == null){
            return null;
        }
        for(int i = 0; i < fieldProxyParameters.length; i++){
            SpeciesContextSpecProxyParameter parm = fieldProxyParameters[i];
            if(pName.equals(parm.getName())){
                return parm;
            }
        }
        return null;
    }

    public SpeciesContextSpecProxyParameter[] getProxyParameters(){
        return fieldProxyParameters;
    }

    /**
     * Accessor for the propertyChange field.
     */
    protected java.beans.PropertyChangeSupport getPropertyChange(){
        if(propertyChange == null){
            propertyChange = new java.beans.PropertyChangeSupport(this);
        }
        ;
        return propertyChange;
    }


    /**
     * Insert the method's description here.
     * Creation date: (4/3/2004 10:54:49 AM)
     *
     * @param boundaryLocation cbit.vcell.mapping.BoundaryLocation
     * @return int
     */
    private int getRole(BoundaryLocation boundaryLocation){
        int role = -1;
        switch(boundaryLocation.getNum()){
            case BoundaryLocation.BOUNDARY_XM:{
                role = ROLE_BoundaryValueXm;
                break;
            }
            case BoundaryLocation.BOUNDARY_XP:{
                role = ROLE_BoundaryValueXp;
                break;
            }
            case BoundaryLocation.BOUNDARY_YM:{
                role = ROLE_BoundaryValueYm;
                break;
            }
            case BoundaryLocation.BOUNDARY_YP:{
                role = ROLE_BoundaryValueYp;
                break;
            }
            case BoundaryLocation.BOUNDARY_ZM:{
                role = ROLE_BoundaryValueZm;
                break;
            }
            case BoundaryLocation.BOUNDARY_ZP:{
                role = ROLE_BoundaryValueZp;
                break;
            }
        }
        return role;
    }


    /**
     * This method was created by a SmartGuide.
     *
     * @return cbit.vcell.model.SpeciesContext
     */
    public SpeciesContext getSpeciesContext(){
        return speciesContext;
    }


    /**
     * This method was created by a SmartGuide.
     *
     * @return java.lang.String
     */
    public String getVCML(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("\t" + VCMODL.SpeciesContextSpec + " " + getSpeciesContext().getName() + " {\n");
        buffer.append("\t\t" + VCMODL.ForceConstant + " " + isConstant() + "\n");
        buffer.append("\t\t" + VCMODL.EnableDiffusion + " " + isDiffusing() + "\n");
        Expression init = getInitialConditionParameter().getExpression();
        if(init != null){
            buffer.append("\t\t" + VCMODL.InitialConcentration + " " + init.toString() + ";\n");
        }
        Expression diffRate = getDiffusionParameter().getExpression();
        if(diffRate != null){
            buffer.append("\t\t" + VCMODL.DiffusionRate + " " + diffRate.toString() + ";\n");
        }
        //
        // write BoundaryConditions
        //
        for(int i = BoundaryLocation.FIRST; i <= BoundaryLocation.LAST; i++){
            BoundaryLocation bl = BoundaryLocation.fromDirection(i);
            Expression boundExp = getParameterFromRole(getRole(bl)).getExpression();
            if(boundExp != null){
                buffer.append("\t\t" + VCMODL.BoundaryCondition + " " + bl.toString() + " " + boundExp.toString() + "\n");
            }
        }
        // write velocity x,y,z expressions
        Expression vel_x = getVelocityXParameter().getExpression();
        if(vel_x != null){
            buffer.append("\t\t" + VCMODL.VelocityX + " " + vel_x.toString() + ";\n");
        }
        Expression vel_y = getVelocityYParameter().getExpression();
        if(vel_y != null){
            buffer.append("\t\t" + VCMODL.VelocityY + " " + vel_y.toString() + ";\n");
        }
        Expression vel_z = getVelocityZParameter().getExpression();
        if(vel_z != null){
            buffer.append("\t\t" + VCMODL.VelocityZ + " " + vel_z.toString() + ";\n");
        }

        buffer.append("\t}\n");
        return buffer.toString();
    }


    /**
     * Accessor for the vetoPropertyChange field.
     */
    protected java.beans.VetoableChangeSupport getVetoPropertyChange(){
        if(vetoPropertyChange == null){
            vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
        }
        ;
        return vetoPropertyChange;
    }


    /**
     * The hasListeners method was generated to support the vetoPropertyChange field.
     */
    public synchronized boolean hasListeners(java.lang.String propertyName){
        return getVetoPropertyChange().hasListeners(propertyName);
    }


    /**
     * returns whether this species is 'clamped' to the value of it's initialConc or initialCount expressions.
     * Note that if clamped is true, the initialConc or initialCount parameter values can be functions of time.
     * <p>
     * In the future, we should either add clampedConc and clampedCount parameters or rename initialConc and initialCount
     * to clarify the modeling semantics.
     *
     * @return boolean
     * @deprecated This method name is confusing, use {@link SpeciesContextSpec#isClamped()} instead
     */
    @Deprecated
    public boolean isConstant(){
        return isClamped();
    }

    /**
     * returns whether this species is 'clamped' to the value of it's initialConc or initialCount expressions.
     * Note that if clamped is true, the initialConc or initialCount parameter values can be functions of time.
     * <p>
     * In the future, we should either add clampedConc and clampedCount parameters or rename initialConc and initialCount
     * to clarify the modeling semantics.
     *
     * @return boolean
     */
    public boolean isClamped(){
        return bClamped;
    }


    /**
     * @return boolean
     */
    public boolean isDiffusing(){
        if(bClamped){
            return false;
        }

        if(bWellMixed){
            return false;
        }

        try {
            double constantDiffRate = getDiffusionParameter().getExpression().evaluateConstant();
            return (constantDiffRate > 0.0);
        } catch(ExpressionException e){
        }

        //
        // can't evaluate to constant, so must be non-zero
        //
        return true;
    }

    public boolean isAdvecting(){
        if(bWellMixed || bClamped == true){
            return false;
        }
        // If all 3 velocity (x,y,z) parameters are null, there is no advection; return <FALSE>, else return <TRUE>
        Expression eX = getVelocityXParameter().getExpression();
        Expression eY = getVelocityYParameter().getExpression();
        Expression eZ = getVelocityZParameter().getExpression();
        if(eX != null || eY != null || eZ != null){
            return true;
        }
        if(getVelocityQuantities(QuantityComponent.X).length > 0){
            return true;
        }
        if(getVelocityQuantities(QuantityComponent.Y).length > 0){
            return true;
        }
        if(getVelocityQuantities(QuantityComponent.Z).length > 0){
            return true;
        }
        return false;
    }

///**
// * @return boolean
// * @deprecated (no longer used)
// */
//public final boolean isEnableDiffusing() {
//	return bEnableDiffusing;
//}

    public final Boolean isWellMixed(){
        return bWellMixed;
    }

    public final Boolean isForceContinuous(){
        return bForceContinuous;
    }

    /**
     * This method was created in VisualAge.
     *
     * @param parm cbit.vcell.model.Parameter
     * @return boolean
     */
    private boolean isReferenced(Parameter parm) throws ModelException, ExpressionException{

        if(fieldParameters == null){
            return false;
        }

        if(parm instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
            SpeciesContextSpecParameter scsParm = (SpeciesContextSpecParameter) parm;
            for(int i = 0; i < fieldParameters.length; i++){
                if(fieldParameters[i] == parm){
                    return true;
                }
            }
            return false;
        } else if(parm instanceof SpeciesContextSpecProxyParameter){
            if(fieldParameters != null){
                for(int i = 0; i < fieldParameters.length; i++){
                    Expression exp = fieldParameters[i].getExpression();
                    if(exp != null){
                        String[] symbols = exp.getSymbols();
                        for(int j = 0; symbols != null && j < symbols.length; j++){
                            if(AbstractNameScope.getStrippedIdentifier(symbols[j]).equals(parm.getName())){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method was created in VisualAge.
     */
    private final void cleanupParameters() throws ModelException, ExpressionException{
        //
        // for each parameter, see if it is used, if not delete it
        //
        if(fieldProxyParameters != null){
            for(int i = 0; i < fieldProxyParameters.length; i++){
                if(!isReferenced(fieldProxyParameters[i])){
                    removeProxyParameter(fieldProxyParameters[i]);
                    i--;
                }
            }
        }
    }

    /**
     *
     */
    public void refreshDependencies(){
        for(int i = 0; i < fieldParameters.length; i++){
            try {
                if(fieldParameters[i].getExpression() != null){
                    fieldParameters[i].getExpression().bindExpression(this);
                }
            } catch(ExpressionException e){
                lg.error("error binding expression '" + fieldParameters[i].getExpression().infix() + "', " + e.getMessage());
            }
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (4/3/2004 11:13:04 AM)
     *
     * @param parameter cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
     */
    private void removeParameter(SpeciesContextSpec.SpeciesContextSpecParameter parameter){
    }

    protected void removeProxyParameter(SpeciesContextSpecProxyParameter parameter){
        for(int i = 0; i < fieldProxyParameters.length; i++){
            if(fieldProxyParameters[i] == parameter){
                SpeciesContextSpecProxyParameter[] newProxyParameters = ArrayUtils.removeFirstInstanceOfElement(fieldProxyParameters, parameter);
                setProxyParameters(newProxyParameters);
                return;
            }
        }
        throw new RuntimeException(parameter.getName() + "' not found");
    }


    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener){
        getPropertyChange().removePropertyChangeListener(listener);
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
     *
     */
    private void resetDefaults(){
        bClamped = DEFAULT_CLAMPED;
//	if (getSpeciesContext().getStructure() instanceof Feature || getSpeciesContext().getStructure() instanceof Membrane){
//		bEnableDiffusing = DEFAULT_ENABLE_DIFFUSING;
//	}else{
//		bEnableDiffusing = false;
//	}
        bWellMixed = DEFAULT_WELL_MIXED;
        bForceContinuous = DEFAULT_FORCECONTINUOUS;
    }


    /**
     * sets the speciesContext 'clamped' property which specifies the interpretation of the initialConc and initialCount parameter expressions.
     * Note if clamped is true, the initialConc or initialCount parameter values can be functions of time.
     * <p>
     * In the future, we should either add clampedConc and clampedCount parameters or rename initialConc and initialCount
     * to clarify the modeling semantics.
     *
     * @return boolean
     * @deprecated This method name is confusing, use {@link SpeciesContextSpec#setClamped(boolean)} instead
     */
    @Deprecated
    public void setConstant(boolean isClamped){
        setClamped(isClamped);
    }

    /**
     * sets the speciesContext 'clamped' property which specifies the interpretation of the initialConc and initialCount parameter expressions.
     * Note if clamped is true, the initialConc or initialCount parameter values can be functions of time.
     * <p>
     * In the future, we should either add clampedConc and clampedCount parameters or rename initialConc and initialCount
     * to clarify the modeling semantics.
     *
     * @return boolean
     */
    public void setClamped(boolean isClamped){
        boolean oldDiffusing = isDiffusing();
        boolean oldConstant = bClamped;
        this.bClamped = isClamped;
        firePropertyChange("constant", oldConstant, isClamped); // to support legacy/deprecated usage
        firePropertyChange("clamped", oldConstant, isClamped);
        firePropertyChange("diffusing", oldDiffusing, isDiffusing());
    }

///**
// * @param isDiffusing boolean
// * @deprecated
// */
//public void setEnableDiffusing(boolean isEnableDiffusing) throws MappingException {
//	boolean oldEnableDiffusing = bEnableDiffusing;
//
//	this.bEnableDiffusing = isEnableDiffusing;
//
//	firePropertyChange("enableDiffusing",new Boolean(oldEnableDiffusing), new Boolean(isEnableDiffusing));
//}


    public void setWellMixed(boolean bWellMixed){
        Boolean oldValue = this.bWellMixed;
        this.bWellMixed = bWellMixed;
        firePropertyChange(PROPERTY_NAME_WELL_MIXED, oldValue, bWellMixed);
    }

    public void setForceContinuous(boolean bForceContinuous){
        Boolean oldValue = this.bForceContinuous;
        this.bForceContinuous = bForceContinuous;
        firePropertyChange(PROPERTY_NAME_FORCECONTINUOUS, oldValue, bForceContinuous);
    }


    /**
     * Sets the parameters property (cbit.vcell.model.Parameter[]) value.
     *
     * @param parameters The new value for the property.
     * @throws java.beans.PropertyVetoException The exception description.
     * @see #getParameters
     */
    private void setParameters(SpeciesContextSpecParameter[] parameters) throws java.beans.PropertyVetoException{
        SpeciesContextSpecParameter[] oldValue = fieldParameters;
        fireVetoableChange("parameters", oldValue, parameters);
        fieldParameters = parameters;
        try {
            cleanupParameters();
        } catch(ModelException | ExpressionException e){
            lg.error(e);
        }
        firePropertyChange("parameters", oldValue, parameters);
    }

    private void setProxyParameters(SpeciesContextSpecProxyParameter[] proxyParameters){
        SpeciesContextSpecProxyParameter[] oldValue = fieldProxyParameters;
        fieldProxyParameters = proxyParameters;
        firePropertyChange(PARAMETER_NAME_PROXY_PARAMETERS, oldValue, proxyParameters);
    }


    /**
     * Insert the method's description here.
     * Creation date: (4/23/2004 7:25:49 AM)
     *
     * @param newSimulationContext cbit.vcell.mapping.SimulationContext
     */
    public void setSimulationContext(SimulationContext newSimulationContext){
        simulationContext = newSimulationContext;
    }


    /**
     * @param sc cbit.vcell.model.SpeciesContext
     */
    void setSpeciesContextReference(SpeciesContext sc) throws MappingException{

        if(this.speciesContext == sc){
            return;
        }

        if(sc.compareEqual(this.speciesContext)){
            this.speciesContext = sc;
        } else {
            throw new MappingException("replacing speciesContext that is not the same");
        }
    }

    public void setIs2D(boolean is2D) {
        this.is2D = is2D;
    }
    public boolean getIs2D() {
        return this.is2D;
    }

    /**
     * This method was created by a SmartGuide.
     *
     * @return java.lang.String
     */
    public String toString(){
        StringBuffer sb = new StringBuffer();

        sb.append(getClass().getName() + "\n");
        if(speciesContext != null){
            sb.append("speciesContext = '" + speciesContext + "'\n");
        }

        return sb.toString();
    }

    private double computeStructureSize() throws ExpressionException, MappingException{
        Structure structure = getSpeciesContext().getStructure();
        StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(structure);
        double structSize = 0;
        if(getSimulationContext().getGeometry().getDimension() == 0){
            Expression sizeParameterExpression = sm.getSizeParameter().getExpression();
            if(sizeParameterExpression == null || sizeParameterExpression.isZero()){
                throw new RuntimeException("\nIn application '" + getSimulationContext().getName() + "', " +
                        "size of structure '" + structure.getName() + "' is required to convert " +
                        "concentration to number of particles.\n\nPlease go to 'Structure Mapping' tab to check the size.");
            }
            structSize = sizeParameterExpression.evaluateConstant();
        } else {
            StructureMapping structureMapping = getSimulationContext().getGeometryContext().getStructureMapping(structure);
            GeometryClass gc = structureMapping.getGeometryClass();
            if(gc == null){
                throw new MappingException("model structure '" + structure.getName() + "' not mapped to a geometry subdomain");
            }

            try {
                GeometricRegion[] geometricRegions = getSimulationContext().getGeometry().getGeometrySurfaceDescription().getGeometricRegions(gc);
                if(geometricRegions == null){
                    throw new MappingException("Geometry is not updated.");
                }

                for(GeometricRegion gr : geometricRegions){
                    structSize += gr.getSize();
                }
            } catch(Exception ex){

            }
        }
        return structSize;
    }

    public Expression convertConcentrationToParticles(Expression iniConcentration) throws ExpressionException, MappingException{
        Expression iniParticlesExpr = null;
        Structure structure = getSpeciesContext().getStructure();
        double structSize = computeStructureSize();

        if(structure instanceof Membrane){
            // convert concentration(particles/area) to number of particles
            // particles = iniConcentration(molecules/um2)*size(um2)
            try {
                iniParticlesExpr = new Expression(iniConcentration.evaluateConstant() * structSize);
            } catch(ExpressionException e){
                iniParticlesExpr = Expression.mult(iniConcentration, new Expression(structSize)).flatten();
            }
        } else {
            // convert concentration(particles/volume) to number of particles
            // particles = [iniConcentration(uM)*size(um3)]/KMOLE
            // @Note : 'kMole' variable here is used only as a var name, it does not represent the previously known ReservedSymbol KMOLE.
            ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
            VCUnitDefinition volSubstanceToStochastic = modelUnitSystem.getStochasticSubstanceUnit().divideBy(modelUnitSystem.getVolumeSubstanceUnit());
            double volSubstanceToStochasticScale = volSubstanceToStochastic.getDimensionlessScale().doubleValue();
            try {
                iniParticlesExpr = new Expression(iniConcentration.evaluateConstant() * structSize * volSubstanceToStochasticScale);
                BigDecimal bd = new BigDecimal(iniParticlesExpr.evaluateConstant());
                bd = bd.round(new MathContext(15));
                iniParticlesExpr = new Expression(bd.doubleValue());
            } catch(RuntimeException | ExpressionException e){
                Expression numeratorExpr = Expression.mult(iniConcentration, new Expression(structSize));
                Expression exp = new Expression(volSubstanceToStochasticScale);
                iniParticlesExpr = Expression.mult(numeratorExpr, exp).flatten();
            }
        }
        return iniParticlesExpr;
    }

    public Expression convertParticlesToConcentration(Expression iniParticles) throws ExpressionException, MappingException{
        Expression iniConcentrationExpr = null;
        Structure structure = getSpeciesContext().getStructure();
        double structSize = computeStructureSize();

        if(structure instanceof Membrane){
            // convert number of particles to concentration(particles/area)
            // iniConcentration(molecules/um2) = particles/size(um2)
            try {
                iniConcentrationExpr = new Expression((iniParticles.evaluateConstant() * 1.0) / structSize);
            } catch(ExpressionException e){
                iniConcentrationExpr = Expression.div(iniParticles, new Expression(structSize)).flatten();
            }
        } else {
            // convert concentration(particles/volume) to number of particles
            // particles = [iniParticles(uM)/size(um3)]*KMOLE
            // @Note : 'kMole' variable here is used only as a var name, it does not represent the previously known ReservedSymbol KMOLE.
            ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
            VCUnitDefinition stochasticToVolSubstance = modelUnitSystem.getVolumeSubstanceUnit().divideBy(modelUnitSystem.getStochasticSubstanceUnit());
            double stochasticToVolSubstanceScale = stochasticToVolSubstance.getDimensionlessScale().doubleValue();
            try {
                iniConcentrationExpr = new Expression((iniParticles.evaluateConstant() * stochasticToVolSubstanceScale / structSize));
            } catch(ExpressionException e){
                Expression numeratorExpr = Expression.mult(iniParticles, new Expression(stochasticToVolSubstanceScale));
                Expression denominatorExpr = new Expression(structSize);
                iniConcentrationExpr = Expression.div(numeratorExpr, denominatorExpr).flatten();
            }
        }
        return iniConcentrationExpr;
    }

    public SimulationContext getSimulationContext(){
        return simulationContext;
    }

    public void getLocalEntries(Map<String, SymbolTableEntry> entryMap){
        for(SymbolTableEntry ste : fieldProxyParameters){
            entryMap.put(ste.getName(), ste);
        }
        for(SymbolTableEntry ste : fieldParameters){
            entryMap.put(ste.getName(), ste);
        }
    }

    public void getEntries(Map<String, SymbolTableEntry> entryMap){
        getNameScope().getExternalEntries(entryMap);
    }

    public boolean hasTransport(){
        if(isConstant() || isWellMixed() || simulationContext == null
                || simulationContext.getGeometry() == null || simulationContext.getGeometry().getDimension() == 0){
            return false;
        }
        int dimension = simulationContext.getGeometry().getDimension();
        SpeciesContext speciesContext = getSpeciesContext();
        if(speciesContext.getStructure() instanceof Membrane){
            if(dimension > 1 && !getDiffusionParameter().getExpression().isZero()){
                return true;
            }
        } else if(speciesContext.getStructure() instanceof Feature){
            if(!getDiffusionParameter().getExpression().isZero()){
                return true;
            }

            if(getVelocityXParameter().getExpression() != null && !getVelocityXParameter().getExpression().isZero()){
                return true;
            }
            SpatialQuantity[] velX_quantities = getVelocityQuantities(QuantityComponent.X);
            if(velX_quantities.length > 0){
                return true;
            }
            if(dimension > 1){
                if(getVelocityYParameter().getExpression() != null && !getVelocityYParameter().getExpression().isZero()){
                    return true;
                }

                if(dimension > 2){
                    if(getVelocityZParameter().getExpression() != null && !getVelocityZParameter().getExpression().isZero()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<SpeciesContextSpecParameter> computeApplicableParameterList(){
        List<SpeciesContextSpecParameter> speciesContextSpecParameterList = new ArrayList<SpeciesContextSpecParameter>();
        speciesContextSpecParameterList.add(getInitialConditionParameter());
        if(getSimulationContext().getApplicationType() == Application.SPRINGSALAD) {
            return speciesContextSpecParameterList;
        }
        int dimension = simulationContext.getGeometry().getDimension();
        if(!isConstant() && !isWellMixed() && dimension > 0){
            // diffusion
            speciesContextSpecParameterList.add(getDiffusionParameter());
        }
        if(hasTransport()){
            SpeciesContext speciesContext = getSpeciesContext();
            if(speciesContext.getStructure() instanceof Membrane){

                // boundary condition
                speciesContextSpecParameterList.add(getBoundaryXmParameter());
                speciesContextSpecParameterList.add(getBoundaryXpParameter());
                speciesContextSpecParameterList.add(getBoundaryYmParameter());
                speciesContextSpecParameterList.add(getBoundaryYpParameter());

                if(dimension > 2){
                    speciesContextSpecParameterList.add(getBoundaryZmParameter());
                    speciesContextSpecParameterList.add(getBoundaryZpParameter());
                }

            } else if(speciesContext.getStructure() instanceof Feature){
                // boundary condition
                speciesContextSpecParameterList.add(getBoundaryXmParameter());
                speciesContextSpecParameterList.add(getBoundaryXpParameter());

                if(dimension > 1){
                    speciesContextSpecParameterList.add(getBoundaryYmParameter());
                    speciesContextSpecParameterList.add(getBoundaryYpParameter());


                    if(dimension > 2){
                        speciesContextSpecParameterList.add(getBoundaryZmParameter());
                        speciesContextSpecParameterList.add(getBoundaryZpParameter());
                    }
                }

                // velocity
                speciesContextSpecParameterList.add(getVelocityXParameter());
                if(dimension > 1){
                    speciesContextSpecParameterList.add(getVelocityYParameter());

                    if(dimension > 2){
                        speciesContextSpecParameterList.add(getVelocityZParameter());
                    }
                }
            }
        }
        return speciesContextSpecParameterList;
    }

    public Set<MolecularInternalLinkSpec> getInternalLinkSet(){
        return internalLinkSet;
    }

    public void setInternalLinkSet(Set<MolecularInternalLinkSpec> internalLinkSet){
        this.internalLinkSet = internalLinkSet;
    }


    public Map<MolecularComponentPattern, SiteAttributesSpec> getSiteAttributesMap(){
        return siteAttributesMap;
    }

    public void setSiteAttributesMap(Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMap){
        this.siteAttributesMap = siteAttributesMap;
    }

    public SpatialQuantity[] getVelocityQuantities(QuantityComponent component){
        ArrayList<SpatialQuantity> velocityQuantities = new ArrayList<SpatialQuantity>();
        for(SpatialObject spatialObject : simulationContext.getSpatialObjects(speciesContext.getStructure())){
            if(spatialObject instanceof VolumeRegionObject){
                if(((VolumeRegionObject) spatialObject).isQuantityCategoryEnabled(QuantityCategory.InteriorVelocity)){
                    velocityQuantities.add(((VolumeRegionObject) spatialObject).getSpatialQuantity(QuantityCategory.InteriorVelocity, component));
                }
            }
        }
        return velocityQuantities.toArray(new SpatialQuantity[0]);
    }

    @Override
    public Kind getSimulationContextKind(){
        return SimulationContext.Kind.SPECIFICATIONS_KIND;
    }

    public void writeData(StringBuilder sb){                // SpringSaLaD exporting the species / molecule information
        if(getSimulationContext().getApplicationType() != Application.SPRINGSALAD){
            sb.append("\n");
            return;
        }
        // ----------------------------------------------------------------------------------
//	private Set<MolecularInternalLinkSpec> internalLinkSet = new LinkedHashSet<> ();			// SpringSaLaD
//	private Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMap = new LinkedHashMap<> ();
//	for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> entry : getSiteAttributesMap().entrySet()) {
//		MolecularComponentPattern key = entry.getKey();
//		SiteAttributesSpec sas = entry.getValue();

        Geometry geometry = getSimulationContext().getGeometry();
        GeometryContext geometryContext = getSimulationContext().getGeometryContext();
        GeometrySpec geometrySpec = geometry.getGeometrySpec();
        ReactionContext reactionContext = getSimulationContext().getReactionContext();
        SpeciesContextSpec[] speciesContextSpecs = reactionContext.getSpeciesContextSpecs();
        ReactionSpec[] reactionSpecs = reactionContext.getReactionSpecs();
        ReactionRuleSpec[] reactionRuleSpecs = reactionContext.getReactionRuleSpecs();
        String name = getSpeciesContext().getName();
        if(SourceMoleculeString.equals(name) || SinkMoleculeString.equals(name)){
            return;        // the solver doesn't need to know about Sink and Source
        }
        SpeciesContextSpecParameter initialCountParameter = getInitialCountParameter();
        SpeciesPattern sp = getSpeciesContext().getSpeciesPattern();
        if(sp == null || sp.getMolecularTypePatterns() == null || sp.getMolecularTypePatterns().isEmpty()){
            sb.append("MOLECULE: \"" + getSpeciesContext().getName() + "\" " + "ERROR");
            sb.append("\n");
            sb.append("\n");
            return;
        }
        MolecularType mt = sp.getMolecularTypePatterns().get(0).getMolecularType();
        List<MolecularComponent> componentList = mt.getComponentList();
        int dimension = geometry.getDimension();

        // compare the next few lines with LangevinLngvWriter.writeSpeciesInfo() where we have a math
        // here we must assume that the expression is numeric
        Expression count = initialCountParameter.getExpression();
        String scount;
        try {
            double ddd = count.evaluateConstant();
            scount = Integer.toString((int)ddd);
        } catch (Exception e) {
            throw new RuntimeException("Initial concentration must be a number");
        }

        sb.append("MOLECULE: \"" + getSpeciesContext().getName() + "\" " + getSpeciesContext().getStructure().getName() +
                " Number " + scount +
                " Site_Types " + componentList.size() + " Total" + "_Sites " + siteAttributesMap.size() +
                " Total_Links " + internalLinkSet.size() + " is2D " + (dimension == 2 ? true : false));    // TODO: molecule is flat, unrelated to geometry
        sb.append("\n");
        sb.append("{");
        sb.append("\n");

        for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> entry : siteAttributesMap.entrySet()){
            SiteAttributesSpec sas = entry.getValue();
            sb.append("     ");
            sas.writeType(sb);        // writeMolecularComponent
        }
        sb.append("\n");
        for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> entry : siteAttributesMap.entrySet()){
            SiteAttributesSpec sas = entry.getValue();
            sb.append("     ");
            sas.writeSite(sb);
        }
        sb.append("\n");
        for(MolecularInternalLinkSpec mils : internalLinkSet){
            sb.append("     ");
            mils.writeLink(sb);
        }

        sb.append("\n");
        sb.append("     Initial_Positions: ");
        if(InitialLocationRandom){
            sb.append(InitialLocationRandomString);
            sb.append("\n");
//	} else {
//		sb.append(InitialLocationSetString);
//		sb.append("\n");
//		sb.append("     x: " + IOHelp.printArrayList(initialCondition.getXIC(), 5));
//		sb.append("\n");
//		sb.append("     y: " + IOHelp.printArrayList(initialCondition.getYIC(), 5));
//		sb.append("\n");
//		sb.append("     z: " + IOHelp.printArrayList(initialCondition.getZIC(), 5));
//		sb.append("\n");
        }
        sb.append("}");
        sb.append("\n");
        sb.append("\n");
        return;
    }

    public String getFilename(){    // SpringSaLaD specific, external file with molecule information
        return null;    // not implemented
    }


    public static final String typeName = "SpeciesContextSpec";

    @Override
    public String getDisplayName(){
        if(getSpeciesContext() != null){
            return getSpeciesContext().getDisplayName();
        }
        return ("?");
    }

    @Override
    public String getDisplayType(){
        return typeName;
    }

}
