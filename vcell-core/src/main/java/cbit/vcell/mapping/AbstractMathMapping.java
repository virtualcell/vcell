package cbit.vcell.mapping;

import java.beans.PropertyVetoException;
import java.util.*;

import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.math.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;

import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.geometry.CompartmentSubVolume;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SimContextTransformer.SimContextTransformation;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.potential.ElectricalDevice;
import cbit.vcell.mapping.potential.MembraneElectricalDevice;
import cbit.vcell.mapping.potential.PotentialMapping;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ExpressionContainer;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelFunction;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelProcess;
import cbit.vcell.model.ModelQuantity;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.Expression.FunctionFilter;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.VCUnitEvaluator;
import cbit.vcell.solver.Simulation;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitException;

public abstract class AbstractMathMapping implements ScopedSymbolTable, UnitFactorProvider, IssueSource, MathMapping {

	private final static Logger logger = LogManager.getLogger(AbstractMathMapping.class);

	protected final NetworkGenerationRequirements networkGenerationRequirements;
	protected final MathMappingCallback callback;
	protected final SimulationContext simContext;
	private final SimContextTransformation transformation;
	private final IssueContext issueContext;
	protected Vector<Issue> localIssueList = new Vector<Issue>();
	protected MathMappingParameter[] fieldMathMappingParameters = new MathMappingParameter[0];
	protected MathMappingQuantity[] fieldMathMappingQuantities = new MathMappingQuantity[0];
	private NameScope nameScope = new MathMappingNameScope();
	protected PotentialMapping potentialMapping = null;
	private transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private transient java.beans.PropertyChangeSupport propertyChange;
	protected MathDescription mathDesc = null;
	protected MathSymbolMapping mathSymbolMapping = new MathSymbolMapping();
	private HashMap<String, Integer> localNameCountHash = new HashMap<String, Integer>();
	protected Vector<SpeciesContextMapping> speciesContextMappingList = new Vector<SpeciesContextMapping>();

	public AbstractMathMapping(SimulationContext simContext, MathMappingCallback callback, NetworkGenerationRequirements networkGenerationRequirements) {
		this.callback = callback;
		this.networkGenerationRequirements = networkGenerationRequirements;
		SimContextTransformer transformer = simContext.createNewTransformer();
		if (transformer != null){
			this.transformation = transformer.transform(simContext, callback, networkGenerationRequirements);
			this.simContext = transformation.transformedSimContext;
		}else{
			this.transformation = null;
			this.simContext = simContext;
		}
		this.issueContext = new IssueContext(ContextType.SimContext,simContext,null).newChildContext(ContextType.MathMapping,this);

	}

	public static final int NUM_PARAMETER_ROLES = 10;
	@SuppressWarnings("serial")
	public class MathMappingNameScope extends BioNameScope {
		private NameScope nameScopes[] = null;
		public MathMappingNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// return model namescope
			//
			ElectricalDevice electricalDevices[] = (potentialMapping!=null)?(potentialMapping.getElectricalDevices()):(new ElectricalDevice[0]);
			NameScope simContextChildren[] = simContext.getNameScope().getChildren();
			NameScope modelChildren[] = simContext.getModel().getNameScope().getChildren();
			int childCount = simContextChildren.length + modelChildren.length + electricalDevices.length;
			if (nameScopes==null || nameScopes.length != childCount){
				nameScopes = new NameScope[childCount];
				int index = 0;
				for (int i=0;i<electricalDevices.length;i++){
					nameScopes[index++] = electricalDevices[i].getNameScope();
				}
				for (int i=0;i<simContextChildren.length;i++){
					nameScopes[index++] = simContextChildren[i];
				}
				for (int i=0;i<modelChildren.length;i++){
					nameScopes[index++] = modelChildren[i];
				}
			}
			return nameScopes;
		}
		public String getName() {
			return "MathMapping_for_"+TokenMangler.fixTokenStrict(simContext.getName());
		}
		public NameScope getParent() {
			//logger.trace("MathMappingNameScope.getParent() returning null ... no parent");
			return null;
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return AbstractMathMapping.this;
		}
		public boolean isPeer(NameScope nameScope){
			if (super.isPeer(nameScope)){
				return true;
			}
			return (nameScope == getSimulationContext().getNameScope() || nameScope == getSimulationContext().getModel().getNameScope());
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.mathmappingType;
		}
	}


@SuppressWarnings("serial")
public class EventAssignmentOrRateRuleInitParameter extends MathMappingParameter {
	protected EventAssignmentOrRateRuleInitParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
		super(argName,argExpression,argRole,argVCUnitDefinition,null);
	}
	
	@Override
	public boolean compareEqual(Matchable obj){
		if (!super.compareEqual(obj)){
			return false;
		}
		if (!(obj instanceof EventAssignmentOrRateRuleInitParameter)){
			return false;
		}
		return true;
	}
}

@SuppressWarnings("serial")
public class KFluxParameter extends MathMappingParameter {

	private StructureMapping sourceStructureMapping = null;
	private StructureMapping targetStructureMapping = null;

	protected KFluxParameter(String argName, Expression argExpression, VCUnitDefinition argVCUnitDefinition, StructureMapping sourceStructureMapping, StructureMapping targetStructureMapping) {
		super(argName,argExpression,PARAMETER_ROLE_KFLUX,argVCUnitDefinition,sourceStructureMapping.getGeometryClass());
		this.sourceStructureMapping = sourceStructureMapping;
		this.targetStructureMapping = targetStructureMapping;
	}

	public StructureMapping getSourceStructureMapping() {
		return sourceStructureMapping;
	}

	public StructureMapping getTargetStructureMapping() {
		return targetStructureMapping;
	}

	
	@Override
	public boolean compareEqual(Matchable obj){
		if (!super.compareEqual(obj)){
			return false;
		}
		if (!(obj instanceof KFluxParameter)){
			return false;
		}
		KFluxParameter other = (KFluxParameter)obj;
		if (!Compare.isEqual(sourceStructureMapping, other.sourceStructureMapping)){
			return false;
		}
		if (!Compare.isEqual(targetStructureMapping, other.targetStructureMapping)){
			return false;
		}
		return true;
	}
}

public class MathMappingQuantity extends ModelQuantity {

	private final VCUnitDefinition unit;
	
	public MathMappingQuantity(String name, VCUnitDefinition unit) {
		super(name);
		this.unit = unit;
	}

	@Override
	public final boolean isUnitEditable() {
		return false;
	}

	@Override
	public final void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException {
		throw new RuntimeException("cannot set unit on MathMappingQuantity "+getName());
	}

	@Override
	public final NameScope getNameScope() {
		return AbstractMathMapping.this.getNameScope();
	}

	@Override
	public final VCUnitDefinition getUnitDefinition() {
		return unit;
	}
}

public class LocalizedDistanceToMembraneQuantity extends MathMappingQuantity {
	public final SurfaceClass surfaceClass;
	public final SubVolume subvolume;

	private LocalizedDistanceToMembraneQuantity(String name, SurfaceClass surfaceClass, SubVolume subvolume){
		super(name, getSimulationContext().getModel().getUnitSystem().getLengthUnit());
		this.surfaceClass = surfaceClass;
		this.subvolume = subvolume;
	}
}

public class LocalizedDirectionToMembraneQuantity extends MathMappingQuantity {
	public final SurfaceClass surfaceClass;
	public final SubVolume subvolume;
	public final QuantityComponent component;

	private LocalizedDirectionToMembraneQuantity(String name, SurfaceClass surfaceClass, SubVolume subvolume, QuantityComponent component){
		super(name, getSimulationContext().getModel().getUnitSystem().getLengthUnit());
		this.surfaceClass = surfaceClass;
		this.subvolume = subvolume;
		this.component = component;
	}
}

@SuppressWarnings("serial")
public class MathMappingParameter extends Parameter implements ExpressionContainer {
	
	private String fieldParameterName = null;
	private Expression fieldExpression = null;
	private VCUnitDefinition fieldVCUnitDefinition = null;
	private GeometryClass fieldGeometryClass = null;
	private int fieldRole = -1;

	protected MathMappingParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition, GeometryClass geometryClass) {
		if (argName == null){
			throw new IllegalArgumentException("parameter name is null");
		}
		if (argName.length()<1){
			throw new IllegalArgumentException("parameter name is zero length");
		}
		if (argExpression == null){
			throw new IllegalArgumentException("parameter name is null");
		}
		if (argRole<0 || argRole>=NUM_PARAMETER_ROLES){
			throw new IllegalArgumentException("parameter role is invalid");
		}
		this.fieldParameterName = argName;
		this.fieldExpression = argExpression;
		this.fieldRole = argRole;
		this.fieldVCUnitDefinition = argVCUnitDefinition;
		this.fieldGeometryClass = geometryClass;
	}

	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof MathMappingParameter)){
			return false;
		}
		MathMappingParameter mmp = (MathMappingParameter)obj;
		if (!super.compareEqual0(mmp)){
			return false;
		}			
		return true;
	}
	
	public GeometryClass getGeometryClass(){
		return this.fieldGeometryClass;
	}

	public double getConstantValue() throws ExpressionException {
		throw new ExpressionException("no expression defined for MathMappingParameter '"+fieldParameterName+"'");
	}      

	public Expression getExpression() {
		return this.fieldExpression;
	}

	public boolean isExpressionEditable(){
		return true;
	}

	public boolean isUnitEditable(){
		return false;
	}

	public boolean isNameEditable(){
		return true;
	}

	public int getIndex() {
		return -1;
	}

	public VCUnitDefinition getUnitDefinition() {
		return fieldVCUnitDefinition;
	}

	public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException {
		throw new RuntimeException("units are not editable");
	}
	
	public int getRole() {
		return fieldRole;
	}

	public String getName(){ 
		return this.fieldParameterName; 
	}   

	public NameScope getNameScope() {
		return AbstractMathMapping.this.getNameScope();
	}

	public void setExpression(Expression argExpression) {
		Expression oldValue = fieldExpression;
		fieldExpression = argExpression;
		super.firePropertyChange("expression", oldValue, argExpression);
	}

	public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
		String oldValue = fieldParameterName;
		super.fireVetoableChange("name", oldValue, name);
		fieldParameterName = name;
		super.firePropertyChange("name", oldValue, name);
	}


}

@SuppressWarnings("serial")
public class UnitFactorParameter extends MathMappingParameter {

	protected UnitFactorParameter(String argName, Expression argExpression, VCUnitDefinition argVCUnitDefinition) {
		super(argName,argExpression,PARAMETER_ROLE_UNITFACTOR,argVCUnitDefinition,null);
	}
}

@SuppressWarnings("serial")
public class ObservableConcentrationParameter extends MathMappingParameter {
	private RbmObservable observable = null;
	
	protected ObservableConcentrationParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition, RbmObservable argObservable, GeometryClass geometryClass) {
		super(argName,argExpression,argRole,argVCUnitDefinition,geometryClass);
		this.observable = argObservable;
	}

	public RbmObservable getObservable() {
		return observable;
	}
	
	@Override
	public boolean compareEqual(Matchable obj){
		if (!super.compareEqual(obj)){
			return false;
		}
		if (!(obj instanceof ObservableConcentrationParameter)){
			return false;
		}
		ObservableConcentrationParameter other = (ObservableConcentrationParameter)obj;
		if (!Compare.isEqual(observable, other.observable)){
			return false;
		}
		return true;
	}
}

@SuppressWarnings("serial")
public class ObservableCountParameter extends MathMappingParameter {
	private RbmObservable observable = null;
	
	protected ObservableCountParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition, RbmObservable argObservable, GeometryClass geometryClass) {
		super(argName,argExpression,argRole,argVCUnitDefinition,geometryClass);
		this.observable = argObservable;
	}

	public RbmObservable getObservable() {
		return observable;
	}
	@Override
	public boolean compareEqual(Matchable obj){
		if (!super.compareEqual(obj)){
			return false;
		}
		if (!(obj instanceof ObservableCountParameter)){
			return false;
		}
		ObservableCountParameter other = (ObservableCountParameter)obj;
		if (!Compare.isEqual(observable, other.observable)){
			return false;
		}
		return true;
	}
}

@SuppressWarnings("serial")
public class SpeciesConcentrationParameter extends MathMappingParameter {
	private SpeciesContext speciesContext = null;
	
	protected SpeciesConcentrationParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition, SpeciesContext argSpeciesContext, GeometryClass geometryClass) {
		super(argName,argExpression,argRole,argVCUnitDefinition,geometryClass);
		this.speciesContext = argSpeciesContext;
	}

	public SpeciesContext getSpeciesContext() {
		return speciesContext;
	}
	
	@Override
	public boolean compareEqual(Matchable obj){
		if (!super.compareEqual(obj)){
			return false;
		}
		if (!(obj instanceof SpeciesConcentrationParameter)){
			return false;
		}
		SpeciesConcentrationParameter other = (SpeciesConcentrationParameter)obj;
		if (!Compare.isEqual(speciesContext, other.speciesContext)){
			return false;
		}
		return true;
	}
}


@SuppressWarnings("serial")
public class SpeciesCountParameter extends MathMappingParameter {
	private SpeciesContext speciesContext = null;
	
	protected SpeciesCountParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition, SpeciesContext argSpeciesContext, GeometryClass geometryClass) {
		super(argName,argExpression,argRole,argVCUnitDefinition, geometryClass);
		this.speciesContext = argSpeciesContext;
	}

	public SpeciesContext getSpeciesContext() {
		return speciesContext;
	}
	@Override
	public boolean compareEqual(Matchable obj){
		if (!super.compareEqual(obj)){
			return false;
		}
		if (!(obj instanceof SpeciesCountParameter)){
			return false;
		}
		SpeciesCountParameter other = (SpeciesCountParameter)obj;
		if (!Compare.isEqual(speciesContext, other.speciesContext)){
			return false;
		}
		return true;
	}
}

@SuppressWarnings("serial")
public class ProbabilityParameter extends MathMappingParameter {
	
	private ModelProcess modelProcess = null;

	protected ProbabilityParameter(String argName, Expression argExpression, int Role,VCUnitDefinition argVCUnitDefinition, ModelProcess modelProcess, GeometryClass geometryClass) {
		super(argName,argExpression,Role,argVCUnitDefinition,geometryClass);
		this.modelProcess = modelProcess;
	}

	public ModelProcess getModelProcess() {
		return modelProcess;
	}

	@Override
	public boolean compareEqual(Matchable obj){
		if (!super.compareEqual(obj)){
			return false;
		}
		if (!(obj instanceof ProbabilityParameter)){
			return false;
		}
		ProbabilityParameter other = (ProbabilityParameter)obj;
		if (!Compare.isEqual(modelProcess, other.modelProcess)){
			return false;
		}
		return true;
	}
}


@SuppressWarnings("serial")
public class RateRuleRateParameter extends MathMappingParameter {
	protected RateRuleRateParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
		super(argName,argExpression,argRole,argVCUnitDefinition,null);
	}
}

public static final int PARAMETER_ROLE_KFLUX = 2;
public static final String PARAMETER_K_UNITFACTOR_PREFIX = "UnitFactor_";
private static final String PARAMETER_BOUNDARY_XM_SUFFIX = "_boundaryXm";
private static final String PARAMETER_BOUNDARY_XP_SUFFIX = "_boundaryXp";
private static final String PARAMETER_BOUNDARY_YM_SUFFIX = "_boundaryYm";
private static final String PARAMETER_BOUNDARY_YP_SUFFIX = "_boundaryYp";
private static final String PARAMETER_BOUNDARY_ZM_SUFFIX = "_boundaryZm";
private static final String PARAMETER_BOUNDARY_ZP_SUFFIX = "_boundaryZp";
public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONC_UNIT_PREFIX = "_init_";
static final String MATH_FUNC_SUFFIX_SPECIES_CONCENTRATION = "";
public static final String MATH_VAR_SUFFIX_SPECIES_COUNT = "_Count";
public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT = "_initCount";
static final String PARAMETER_PROBABLIITY_RATE_SUFFIX = "_probabilityRate";
static final String PARAMETER_VELOCITY_X_SUFFIX = "_velocityX";
static final String PARAMETER_VELOCITY_Y_SUFFIX = "_velocityY";
static final String PARAMETER_VELOCITY_Z_SUFFIX = "_velocityZ";
static final String PARAMETER_DIFFUSION_RATE_SUFFIX = "_diffusionRate";
static final String PARAMETER_TRANSMEMBRANE_CURRENT_PREFIX = "F_";
static final String PARAMETER_VOLTAGE_PREFIX = "V_";
static final String PARAMETER_TOTAL_CURRENT_PREFIX = "I_";
static final String PARAMETER_SPECIFIC_CAPACITANCE_PREFIX = "C_";
public static final String PARAMETER_SIZE_FUNCTION_PREFIX = "Size_";
static final String BIO_PARAM_SUFFIX_SPECIES_COUNT = "_temp_Count";
static final String BIO_PARAM_SUFFIX_SPECIES_CONCENTRATION = "_temp_Conc";
protected static final Logger lg = LogManager.getLogger(AbstractMathMapping.class);
public static final String PARAMETER_K_FLUX_PREFIX = "KFlux_";
public static final int PARAMETER_ROLE_P = 3;
public static final int PARAMETER_ROLE_SPECIES_CONCENRATION = 5;
public static final int PARAMETER_ROLE_SPECIES_COUNT = 6;
public static final int PARAMETER_ROLE_OBSERVABLE_CONCENTRATION = 7;
public static final int PARAMETER_ROLE_OBSERVABLE_COUNT = 8;
public static final int PARAMETER_ROLE_P_reverse = 4;
public static final String PARAMETER_MASS_CONSERVATION_PREFIX = "K_";
public static final String PARAMETER_MASS_CONSERVATION_SUFFIX = "_total";
public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_old = "_init";
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_uM = "_init_uM";
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_umol_l_1 = "_init_umol_l_1";
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_umol_dm_2 = "_init_umol_dm_2";
public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_old_molecules_per_um2 = "_init_molecules_per_um2";
public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_molecules_um_2 = "_init_molecules_um_2";
public static final int PARAMETER_ROLE_UNITFACTOR = 9;

/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(java.lang.String identifierString) {
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
	ste = getNameScope().getExternalEntry(identifierString,this);
	if (ste==null){
		logger.info("MathMapping is unable to bind identifier '"+identifierString+"'");
	}
	return ste;
}

/**
 * Insert the method's description here.
 * Creation date: (11/8/2004 3:36:31 PM)
 * @return cbit.util.Issue
 */
public Issue[] getIssues() {	
	Vector<Issue> issueList = new Vector<Issue>();
	boolean bIgnoreMathDescription = false;
	getSimulationContext().gatherIssues(issueContext,issueList,bIgnoreMathDescription);
	getSimulationContext().getModel().gatherIssues(issueContext,issueList);
	issueList.addAll(localIssueList);
	Simulation[] sims = getSimulationContext().getSimulations();
	for(Simulation sim : sims) {
		sim.gatherIssues(issueContext, issueList);
	}
	return (Issue[])BeanUtils.getArray(issueList,Issue.class);
}

/**
 * Insert the method's description here.
 * Creation date: (4/4/2004 1:01:22 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getLocalEntry(java.lang.String identifier) {

	//
	// the MathMapping "nameScope" is the union of the Model and SimContext namescopes (with the addition of any locally defined parameters)
	//

	
	//
	// try "truely" local first
	//
	SymbolTableEntry localParamSTE = getMathMappingParameter(identifier);
	SymbolTableEntry localQuantitySTE = getMathMappingQuantity(identifier);

	//
	// try "model" next
	//
	SymbolTableEntry modelSTE = simContext.getModel().getLocalEntry(identifier);
	
	//
	// try "simContext" next
	//
	SymbolTableEntry simContextSTE = simContext.getLocalEntry(identifier);

	int resolutionCount = 0;
	SymbolTableEntry ste = null;
	if (localParamSTE!=null){ 
		resolutionCount++;
		ste = localParamSTE;
	}
	if (localQuantitySTE!=null){ 
		resolutionCount++;
		ste = localQuantitySTE;
	}
	if (modelSTE!=null){
		resolutionCount++;
		ste = modelSTE;
	}
	if (simContextSTE!=null && simContextSTE!=modelSTE){
		resolutionCount++;
		ste = simContextSTE;
	}

	if (resolutionCount==0 || resolutionCount==1){
		return ste;
	}else{
		StringBuffer buffer = new StringBuffer();
		buffer.append("identifier '"+identifier+"' ambiguous, resolved by [");
		if (localParamSTE!=null){
			buffer.append(" MathMappingParameter("+localParamSTE+")");
		}
		if (localQuantitySTE!=null){
			buffer.append(" MathMappingQuantity("+localQuantitySTE+")");
		}
		if (modelSTE!=null){
			buffer.append(" Model("+modelSTE+")");
		}
		if (simContextSTE!=null && simContextSTE!=modelSTE){
			buffer.append(" Application("+simContextSTE+")");
		}
		buffer.append(" ]");
		throw new RuntimeException(buffer.toString());
	}
}

/**
 * Insert the method's description here.
 * Creation date: (4/4/2004 1:01:22 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return nameScope;
}

public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {	
	simContext.getModel().getLocalEntries(entryMap);
	simContext.getLocalEntries(entryMap);
	for (SymbolTableEntry ste : fieldMathMappingParameters) {
		entryMap.put(ste.getName(), ste);
	}
	for (SymbolTableEntry ste : fieldMathMappingQuantities) {
		entryMap.put(ste.getName(), ste);
	}
}

public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	getNameScope().getExternalEntries(entryMap);
}

/**
 * Insert the method's description here.
 * Creation date: (4/4/01 12:19:27 PM)
 * @return cbit.vcell.mapping.SimulationContext
 */
public SimulationContext getSimulationContext() {
	return simContext;
}

/**
 * Gets the mathMappingParameters index property (cbit.vcell.mapping.MathMappingParameter) value.
 * @return The mathMappingParameters property value.
 * @param index The index value into the property array.
 */
public MathMappingParameter getMathMappingParameter(String argName) {
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		if (fieldMathMappingParameters[i].getName().equals(argName)){
			return fieldMathMappingParameters[i];
		}
	}
	return null;
}

public MathMappingQuantity getMathMappingQuantity(String argName) {
	for (int i = 0; i < fieldMathMappingQuantities.length; i++){
		if (fieldMathMappingQuantities[i].getName().equals(argName)){
			return fieldMathMappingQuantities[i];
		}
	}
	return null;
}

/**
 * Gets the mathMappingParameters property (MathMappingParameter[]) value.
 * @return The mathMappingParameters property value.
 */
public MathMappingParameter[] getMathMappingParameters() {
	return fieldMathMappingParameters;
}

public MathMappingQuantity[] getMathMappingQuantities() {
	return fieldMathMappingQuantities;
}

/**
 * This method was created in VisualAge.
 * @return Expression
 */
public Expression getUnitFactor(VCUnitDefinition unitFactor) {
	if (unitFactor.isEquivalent(getSimulationContext().getModel().getUnitSystem().getInstance_DIMENSIONLESS())){
		return new Expression(1.0);
	}
	for (MathMappingParameter p : fieldMathMappingParameters){
		if (p instanceof UnitFactorParameter && p.getUnitDefinition().isEquivalent(unitFactor)){
			return new Expression(p,getNameScope());
		}
	}

	Model model = simContext.getModel();
	Expression factor = ModelUnitConverter.getDimensionlessScaleFactor(unitFactor, model.getUnitSystem().getInstance_DIMENSIONLESS(), model.getKMOLE());
	String name = PARAMETER_K_UNITFACTOR_PREFIX + TokenMangler.fixTokenStrict(unitFactor.getSymbol().replace("-","_neg_"));
	UnitFactorParameter unitFactorParameter = new UnitFactorParameter(name, new Expression(factor), unitFactor);
	MathMappingParameter[] newMathMappingParameters = BeanUtils.addElement(this.fieldMathMappingParameters,unitFactorParameter);
	try {
		setMathMapppingParameters(newMathMappingParameters);
	}catch (java.beans.PropertyVetoException e){
		throw new RuntimeException(e.getMessage(), e);
	}
	return new Expression(unitFactorParameter,getNameScope());
}

/**
 * This method was created in VisualAge.
 * @return Expression
 */
public RationalExp getUnitFactorAsRationalExp(VCUnitDefinition unitFactor) {
	if (unitFactor.isEquivalent(getSimulationContext().getModel().getUnitSystem().getInstance_DIMENSIONLESS())){
		return RationalExp.ONE;
	}
	for (MathMappingParameter p : fieldMathMappingParameters){
		if (p instanceof UnitFactorParameter && p.getUnitDefinition().isEquivalent(unitFactor)){
			return new RationalExp(p.getName());
		}
	}

	Model model = simContext.getModel();
	Expression factorExp = ModelUnitConverter.getDimensionlessScaleFactor(unitFactor, model.getUnitSystem().getInstance_DIMENSIONLESS(), model.getKMOLE());
	RationalExp factorRationalExp = ModelUnitConverter.getDimensionlessScaleFactorAsRationalExp(unitFactor, model.getUnitSystem().getInstance_DIMENSIONLESS(), model.getKMOLE());
	String name = PARAMETER_K_UNITFACTOR_PREFIX + TokenMangler.fixTokenStrict(unitFactor.getSymbol().replace("-","_neg_"));
	UnitFactorParameter unitFactorParameter = new UnitFactorParameter(name, new Expression(factorExp), unitFactor);
	MathMappingParameter[] newMathMappingParameters = BeanUtils.addElement(this.fieldMathMappingParameters,unitFactorParameter);
	try {
		setMathMapppingParameters(newMathMappingParameters);
	}catch (java.beans.PropertyVetoException e){
		throw new RuntimeException(e.getMessage(), e);
	}
	return factorRationalExp;
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public final synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}

/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue)
		throws java.beans.PropertyVetoException {
			getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
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
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}

/**
 * Sets the mathMappingParameters property (MathMappingParameter[]) value.
 * @param kineticsParameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getMathMappingParameters
 */
protected void setMathMapppingParameters(MathMappingParameter[] mathMappingParameters) throws java.beans.PropertyVetoException {
	MathMappingParameter[] oldValue = fieldMathMappingParameters;
	fireVetoableChange("mathMappingParameters", oldValue, mathMappingParameters);
	fieldMathMappingParameters = mathMappingParameters;
	firePropertyChange("mathMappingParameters", oldValue, mathMappingParameters);
}

protected void setMathMapppingQuantities(MathMappingQuantity[] mathMappingQuantities) {
	MathMappingParameter[] oldValue = fieldMathMappingParameters;
//	fireVetoableChange("mathMappingQuantities", oldValue, mathMappingQuantities);
	fieldMathMappingQuantities = mathMappingQuantities;
	firePropertyChange("mathMappingQuantities", oldValue, mathMappingQuantities);
}

/**
 * get the SimContextTransformation.
 * @return SimContextTransformation: may be null if no transformation required.
 */
public final SimContextTransformation getTransformation() {
	return transformation;
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 */
public final MathDescription getMathDescription(MathMappingCallback callback) throws MappingException, MathException, MatrixException, ModelException, ExpressionException {
	if (mathDesc==null){
		refresh(callback);
		mathSymbolMapping.reconcileVarNames(mathDesc);
	}
	return mathDesc;
}

protected abstract void refresh(MathMappingCallback callback2) throws MappingException, ExpressionException, MatrixException, MathException, ModelException;

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 */
public final MathDescription getMathDescription() throws MappingException, MathException, MatrixException, ModelException, ExpressionException {
	return getMathDescription(null);
}

protected void reconcileWithOriginalModel() throws MappingException,
		MathException, MatrixException, ExpressionException, ModelException {
			if (transformation == null){
				return;
			}
			mathSymbolMapping.reconcileVarNames(getMathDescription());
			mathSymbolMapping.transform(transformation);
			try {
				mathDesc.setGeometry(transformation.originalSimContext.getGeometry());
			} catch (PropertyVetoException e) {
				throw new MappingException(e.getMessage(), e);
			}
		}

protected SpeciesCountParameter getSpeciesCountParameter(SpeciesContext sc) {
	MathMappingParameter[] mmParams = getMathMappingParameters();
	for (int i = 0; i < mmParams.length; i++) {
		if ( (mmParams[i] instanceof SpeciesCountParameter) && (((SpeciesCountParameter)mmParams[i]).getSpeciesContext() == sc)) {
			return (SpeciesCountParameter)mmParams[i];
		}
	}
	return null;
}

protected SpeciesConcentrationParameter getSpeciesConcentrationParameter(SpeciesContext sc) {
	MathMappingParameter[] mmParams = getMathMappingParameters();
	for (int i = 0; i < mmParams.length; i++) {
		if ( (mmParams[i] instanceof SpeciesConcentrationParameter) && (((SpeciesConcentrationParameter)mmParams[i]).getSpeciesContext() == sc)) {
			return (SpeciesConcentrationParameter)mmParams[i];
		}
	}
	return null;
}

protected void refreshLocalNameCount() {
	localNameCountHash.clear();
	ReactionStep reactionSteps[] = simContext.getModel().getReactionSteps();
	for (int j = 0; j < reactionSteps.length; j++){
		KineticsParameter[] params = reactionSteps[j].getKinetics().getKineticsParameters(); 
		for (KineticsParameter kp : params){
			String name = kp.getName();
			if (localNameCountHash.containsKey(name)) {
				localNameCountHash.put(name, localNameCountHash.get(name) + 1);
			} else {
				localNameCountHash.put(name, 1);
			}
		}
	}
	List<ReactionRule> reactionRules = simContext.getModel().getRbmModelContainer().getReactionRuleList();
	for (ReactionRule reactionRule : reactionRules){
		LocalParameter[] params = reactionRule.getKineticLaw().getLocalParameters();
		for (LocalParameter kp : params){
			String name = kp.getName();
			if (localNameCountHash.containsKey(name)) {
				localNameCountHash.put(name, localNameCountHash.get(name) + 1);
			} else {
				localNameCountHash.put(name, 1);
			}
		}
	}
	SpeciesContext scs[] = simContext.getModel().getSpeciesContexts();
	for (SpeciesContext sc : scs) {
		String name = sc.getName();
		if (localNameCountHash.containsKey(name)) {
			localNameCountHash.put(name, localNameCountHash.get(name) + 1);
		} else {
			localNameCountHash.put(name, 1);
		}
	}
	ModelParameter mps[] = simContext.getModel().getModelParameters();
	for (ModelParameter mp : mps) {
		String name = mp.getName();
		if (localNameCountHash.containsKey(name)) {
			localNameCountHash.put(name, localNameCountHash.get(name) + 1);
		} else {
			localNameCountHash.put(name, 1);
		}
	}
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.SpeciesContextMapping
 * @param speciesContext SpeciesContext
 */
public SpeciesContextMapping getSpeciesContextMapping(SpeciesContext speciesContext) {
	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = enum1.nextElement();
		if (scm.getSpeciesContext()==speciesContext){
			return scm;
		}
	}
	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (10/26/2006 4:37:10 PM)
 * @return java.util.Vector
 */
protected Vector<SpeciesContextMapping> getSpeciesContextMappingList() {
	return speciesContextMappingList;
}

/**
 * This method was created in VisualAge.
 * @return java.util.Enumeration
 */
protected Enumeration<SpeciesContextMapping> getSpeciesContextMappings() {
	return speciesContextMappingList.elements();
}

/**
 * Substitutes appropriate variables for speciesContext bindings
 *
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param structureMapping cbit.vcell.mapping.StructureMapping
 */
protected Expression getIdentifierSubstitutions(Expression origExp, VCUnitDefinition desiredExpUnitDef,
		GeometryClass geometryClass) throws ExpressionException, MappingException {
			String symbols[] = origExp.getSymbols();
			if (symbols == null){
				return origExp;
			}
			VCUnitDefinition expUnitDef = null;
			try {
				VCUnitEvaluator unitEvaluator = new VCUnitEvaluator(simContext.getModel().getUnitSystem());
				expUnitDef = unitEvaluator.getUnitDefinition(origExp);
				if (desiredExpUnitDef == null){
					String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
					logger.warn("...........exp='"+expStr+"', desiredUnits are null");
					localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=[null], observed=["+expUnitDef.getSymbol()+"]",Issue.SEVERITY_WARNING));
				}else if (expUnitDef == null){
					String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
					logger.warn("...........exp='"+expStr+"', evaluated Units are null");
					localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+desiredExpUnitDef.getSymbol()+"], observed=[null]",Issue.SEVERITY_WARNING));
				}else if (desiredExpUnitDef.isTBD()){
					String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
//					logger.warn("...........exp='"+expStr+"', desiredUnits are ["+desiredExpUnitDef.getSymbol()+"] and expression units are ["+expUnitDef.getSymbol()+"]");
					localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+desiredExpUnitDef.getSymbol()+"], observed=["+expUnitDef.getSymbol()+"] for exp = "+expStr,Issue.SEVERITY_WARNING));
				}else if (!desiredExpUnitDef.isEquivalent(expUnitDef) && !expUnitDef.isTBD()){
					String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
//					logger.warn("...........exp='"+expStr+"', desiredUnits are ["+desiredExpUnitDef.getSymbol()+"] and expression units are ["+expUnitDef.getSymbol()+"]");
					localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+desiredExpUnitDef.getSymbol()+"], observed=["+expUnitDef.getSymbol()+"] for exp = "+expStr,Issue.SEVERITY_WARNING));
				}
			}catch (VCUnitException e){
				String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
				logger.warn("Unit exception: "+e.getMessage());
				localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+((desiredExpUnitDef!=null)?(desiredExpUnitDef.getSymbol()):("null"))+"], exception="+e.getMessage(),Issue.SEVERITY_WARNING));
			}catch (ExpressionException e){
				String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
				logger.error("exp='"+expStr+"' exception='"+e.getMessage()+"'", e);
				localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+((desiredExpUnitDef!=null)?(desiredExpUnitDef.getSymbol()):("null"))+"], exception="+e.getMessage(),Issue.SEVERITY_WARNING));
			}catch (Exception e){
				logger.error("error evaluating units for expression '"+origExp+": "+e.getMessage(), e);
				localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+((desiredExpUnitDef!=null)?(desiredExpUnitDef.getSymbol()):("null"))+"], exception="+e.getMessage(),Issue.SEVERITY_WARNING));
			}
			Expression newExp = new Expression(origExp);
			//
			// flatten user-defined functions
			//
			FunctionInvocation[] functionInvocations = newExp.getFunctionInvocations(new FunctionFilter() {	
				@Override
				public boolean accept(String functionName, FunctionType functionType) {
					return functionType == FunctionType.USERDEFINED;
				}
			});
			for (FunctionInvocation functionInvocation : functionInvocations){
				if (functionInvocation.getSymbolTableFunctionEntry() instanceof Model.ModelFunction){
					ModelFunction modelFunction = (ModelFunction)functionInvocation.getSymbolTableFunctionEntry();
					newExp.substituteInPlace(functionInvocation.getFunctionExpression(), modelFunction.getFlattenedExpression(functionInvocation));
				}
			}
			//
			// then substitute Math symbols for Biological symbols.
			//
			newExp.bindExpression(null);
			for (int i=0;i<symbols.length;i++){
				SymbolTableEntry ste = origExp.getSymbolBinding(symbols[i]);
				
				if (ste == null){
					throw new ExpressionBindingException("symbol '"+symbols[i]+"' not bound");
					//ste = simContext.getGeometryContext().getModel().getSpeciesContext(symbols[i]);
				}
				
				if (ste != null){
					String newName = getMathSymbol(ste,geometryClass);
					if (!newName.equals(symbols[i])){
						newExp.substituteInPlace(new Expression(symbols[i]),new Expression(newName));
					}
				}
			}
			return newExp;
		}

/**
 * Substitutes appropriate variables for speciesContext bindings
 *
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param structureMapping cbit.vcell.mapping.StructureMapping
 */
public String getMathSymbol(SymbolTableEntry ste, GeometryClass geometryClass) throws MappingException {

	String mathSymbol = getMathSymbol0(ste,geometryClass);
	
	// check for ptential conflict with existing global parameter (which carries over the name from bio side to math side)
	ModelParameter[] modelParams = simContext.getModel().getModelParameters();
	
	if (!(ste instanceof ModelParameter) && !(ste instanceof ProxyParameter && ((ProxyParameter)ste).getTarget() instanceof ModelParameter)) {
		for (int i = 0; i < modelParams.length; i++) {
			if (modelParams[i].getName().equals(mathSymbol)) {
				throw new MappingException("Local parameter '" + ste.getName() + "' math namescope name is '"
						+ mathSymbol + "' and conflicts with existing global parameter named '" + mathSymbol
						+ "'.  Please rename either the local or the global parameter.");
			}
		} 
	}
	mathSymbolMapping.put(ste,mathSymbol);

	return mathSymbol;
}

/**
 * Substitutes appropriate variables for speciesContext bindings
 *
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param structureMapping cbit.vcell.mapping.StructureMapping
 */
protected final String getMathSymbol0(SymbolTableEntry ste, GeometryClass geometryClass)
		throws MappingException {
			String steName = ste.getName();
			if (ste instanceof Kinetics.KineticsParameter){
				Integer count = localNameCountHash.get(steName);
				if (count == null) {
					throw new MappingException("KineticsParameter " + steName + " not found in local name count");
				}
				// for now keep old style
				if (count>1 || steName.equals("J")){
					return steName+"_"+ste.getNameScope().getName();
					//return getNameScope().getSymbolName(ste);
				}else{
					return steName;
				}
				// will revert to this in the future
				// return steName+"_"+ste.getNameScope().getName();
			}
			if (ste instanceof LocalParameter && ((LocalParameter)ste).getNameScope() instanceof ReactionRule.ReactionRuleNameScope){
				Integer count = localNameCountHash.get(steName);
				if (count == null) {
					throw new MappingException("Reaction Rule Parameter " + steName + " not found in local name count");
				}
				// for now keep old style
				if (count>1 || steName.equals("J")){
					return steName+"_"+ste.getNameScope().getName();
					//return getNameScope().getSymbolName(ste);
				}else{
					return steName;
				}
				// will revert to this in the future
				// return steName+"_"+ste.getNameScope().getName();
			}
			if (ste instanceof ProbabilityParameter){ //be careful here, to see if we need mangle the reaction name
				ProbabilityParameter probParm = (ProbabilityParameter)ste;
				return probParm.getName() + PARAMETER_PROBABLIITY_RATE_SUFFIX;
			}
			if (ste instanceof SpeciesConcentrationParameter){
				SpeciesConcentrationParameter concParm = (SpeciesConcentrationParameter)ste;
				return concParm.getSpeciesContext().getName() + MATH_FUNC_SUFFIX_SPECIES_CONCENTRATION;
			}
			if (ste instanceof SpeciesCountParameter){
				SpeciesCountParameter countParm = (SpeciesCountParameter)ste;
				return countParm.getSpeciesContext().getName() + MATH_VAR_SUFFIX_SPECIES_COUNT;
			}
			if (ste instanceof ObservableConcentrationParameter){
				ObservableConcentrationParameter concParm = (ObservableConcentrationParameter)ste;
				return concParm.getObservable().getName() + MATH_FUNC_SUFFIX_SPECIES_CONCENTRATION;
			}
			if (ste instanceof ObservableCountParameter){
				ObservableCountParameter countParm = (ObservableCountParameter)ste;
				return countParm.getObservable().getName() + MATH_VAR_SUFFIX_SPECIES_COUNT;
			}
			if (ste instanceof RbmObservable){
				RbmObservable observable = (RbmObservable)ste;
				return observable.getName() + MATH_FUNC_SUFFIX_SPECIES_CONCENTRATION;
			}
			if (ste instanceof EventAssignmentOrRateRuleInitParameter){
				EventAssignmentOrRateRuleInitParameter eventInitParm = (EventAssignmentOrRateRuleInitParameter)ste;
				return eventInitParm.getName(); // + MATH_FUNC_SUFFIX_EVENTASSIGN_OR_RATE_INIT;
			}
		
			if (ste instanceof RateRuleRateParameter){
				RateRuleRateParameter rateRuleRateParm = (RateRuleRateParameter)ste;
				return rateRuleRateParm.getName(); // + MATH_FUNC_SUFFIX_RATERULE_RATE;
			}
		
			if (ste instanceof Model.ReservedSymbol){
				return steName;
			}
			if (ste instanceof Membrane.MembraneVoltage){
				return steName;
			}
			if (ste instanceof Structure.StructureSize){
				Structure structure = ((Structure.StructureSize)ste).getStructure();
				StructureMapping.StructureMappingParameter sizeParameter = simContext.getGeometryContext().getStructureMapping(structure).getSizeParameter();
				return getMathSymbol(sizeParameter,geometryClass);
			}
			if (ste instanceof ProxyParameter){
				ProxyParameter pp = (ProxyParameter)ste;
				return getMathSymbol0(pp.getTarget(),geometryClass);
			}
			// 
			if (ste instanceof ModelParameter) {
				ModelParameter mp = (ModelParameter)ste;
				return mp.getName();
			}
			if (ste instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
				SpeciesContextSpec.SpeciesContextSpecParameter scsParm = (SpeciesContextSpec.SpeciesContextSpecParameter)ste;
				SpeciesContext speciesContext = ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext();
				SpeciesContextMapping scm = getSpeciesContextMapping(speciesContext);
				String speciesContextVarName = null;
				if (scm.getVariable()!=null){
					speciesContextVarName = scm.getVariable().getName();
				}else{
					speciesContextVarName = speciesContext.getName();
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_InitialConcentration){
					return speciesContextVarName+ MATH_FUNC_SUFFIX_SPECIES_INIT_CONC_UNIT_PREFIX + TokenMangler.fixTokenStrict(scsParm.getUnitDefinition().getSymbol());
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_InitialCount){
					return speciesContextVarName+ MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT;
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_DiffusionRate){
					return speciesContextVarName+PARAMETER_DIFFUSION_RATE_SUFFIX;
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueXm){
					return speciesContextVarName+PARAMETER_BOUNDARY_XM_SUFFIX;
			}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueXp){
					return speciesContextVarName+PARAMETER_BOUNDARY_XP_SUFFIX;
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueYm){
					return speciesContextVarName+PARAMETER_BOUNDARY_YM_SUFFIX;
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueYp){
					return speciesContextVarName+PARAMETER_BOUNDARY_YP_SUFFIX;
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueZm){
					return speciesContextVarName+PARAMETER_BOUNDARY_ZM_SUFFIX;
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueZp){
					return speciesContextVarName+PARAMETER_BOUNDARY_ZP_SUFFIX;
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_VelocityX){
					return speciesContextVarName+PARAMETER_VELOCITY_X_SUFFIX;
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_VelocityY){
					return speciesContextVarName+PARAMETER_VELOCITY_Y_SUFFIX;
				}
				if (scsParm.getRole()==SpeciesContextSpec.ROLE_VelocityZ){
					return speciesContextVarName+PARAMETER_VELOCITY_Z_SUFFIX;
				}
			}
			if (ste instanceof ElectricalDevice.ElectricalDeviceParameter){
				ElectricalDevice.ElectricalDeviceParameter edParm = (ElectricalDevice.ElectricalDeviceParameter)ste;
				ElectricalDevice electricalDevice = (ElectricalDevice)edParm.getNameScope().getScopedSymbolTable();
				if (electricalDevice instanceof MembraneElectricalDevice){
					String nameWithScope = ((MembraneElectricalDevice)electricalDevice).getMembraneMapping().getMembrane().getNameScope().getName();
					if (edParm.getRole()==ElectricalDevice.ROLE_TotalCurrent){
						return PARAMETER_TOTAL_CURRENT_PREFIX+nameWithScope;
					}
					if (edParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrent){
						return PARAMETER_TRANSMEMBRANE_CURRENT_PREFIX+nameWithScope;
					}
				//}else if (electricalDevice instanceof CurrentClampElectricalDevice) {
					//if (edParm.getRole()==ElectricalDevice.ROLE_TotalCurrentDensity){
						//return "I_"+((CurrentClampElectricalDevice)electricalDevice).getCurrentClampStimulus().getNameScope().getName();
					//}
					//if (edParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrentDensity){
						//return "F_"+((CurrentClampElectricalDevice)electricalDevice).getCurrentClampStimulus().getNameScope().getName();
					//}
				//}else if (electricalDevice instanceof VoltageClampElectricalDevice) {
					//if (edParm.getRole()==ElectricalDevice.ROLE_TotalCurrentDensity){
						//return "I_"+((VoltageClampElectricalDevice)electricalDevice).getVoltageClampStimulus().getNameScope().getName();
					//}
					//if (edParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrentDensity){
						//return "F_"+((VoltageClampElectricalDevice)electricalDevice).getVoltageClampStimulus().getNameScope().getName();
					//}
				}
			}
			if (ste instanceof LocalParameter && ((LocalParameter)ste).getNameScope() instanceof ElectricalStimulus.ElectricalStimulusNameScope){
				LocalParameter esParm = (LocalParameter)ste;
				String nameWithScope = esParm.getNameScope().getName();
				if (esParm.getRole()==ElectricalStimulus.ElectricalStimulusParameterType.TotalCurrent){
					return PARAMETER_TOTAL_CURRENT_PREFIX+nameWithScope;
				} else if (esParm.getRole()==ElectricalStimulus.ElectricalStimulusParameterType.Voltage){
					return PARAMETER_VOLTAGE_PREFIX+nameWithScope;
				}
			}
			if (ste instanceof StructureMapping.StructureMappingParameter){
				StructureMapping.StructureMappingParameter smParm = (StructureMapping.StructureMappingParameter)ste;
				Structure structure = ((StructureMapping)(smParm.getNameScope().getScopedSymbolTable())).getStructure();
				String nameWithScope = structure.getNameScope().getName();
				int role = smParm.getRole();
				if (role==StructureMapping.ROLE_InitialVoltage){
					return smParm.getName();
				} else if (role==StructureMapping.ROLE_SpecificCapacitance){
					return PARAMETER_SPECIFIC_CAPACITANCE_PREFIX+nameWithScope;
				} else if (role==StructureMapping.ROLE_Size){
					if (simContext.getGeometry().getDimension() == 0) {
						// if geometry is compartmental, make sure compartment sizes are set if referenced in model.
						if (smParm.getExpression() == null || smParm.getExpression().isZero()) {
							throw new MappingException("\nIn non-spatial application '" + getSimulationContext().getName() + "', " +
									"size of structure '" + structure.getName() + "' must be assigned a " +
									"positive value if referenced in the model.\n\nPlease go to 'Structure Mapping' tab to check the size.");
						}
					}
					return PARAMETER_SIZE_FUNCTION_PREFIX+nameWithScope;
				} else if (role==StructureMapping.ROLE_AreaPerUnitArea){
					return "AreaPerUnitArea_"+nameWithScope;
				} else if (role==StructureMapping.ROLE_AreaPerUnitVolume){
					return "AreaPerUnitVolume_"+nameWithScope;
				} else if (role==StructureMapping.ROLE_VolumePerUnitArea){
					return "VolumePerUnitArea_"+nameWithScope;
				} else if (role==StructureMapping.ROLE_VolumePerUnitVolume){
					return "VolumePerUnitVolume_"+nameWithScope;
				}
			}
			//
			// substitute Variable or Function if a SpeciesContext
			//
			if (ste instanceof SpeciesContext){
				SpeciesContext sc = (SpeciesContext)ste;
				SpeciesContextMapping scm = getSpeciesContextMapping(sc);
				if (scm == null) {
					throw new RuntimeException("Species '" + sc.getName() + "' is referenced in model but may have been deleted. " +
							"Find its references in '" + GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_BIOMODEL_PARAMETERS + "'.");
				}		
				//
				// for reactions mapped to a subvolume
				//
				if (geometryClass instanceof SubVolume){
					//
					// for any SpeciesContext, replace Symbol name with Variable name
					//				
					if (scm.getVariable()!=null && !scm.getVariable().getName().equals(steName)){
						return scm.getVariable().getName();
					}
				//
				// for reactions within a surface, may need "_INSIDE" or "_OUTSIDE" for jump condition
				//
				}else if (geometryClass instanceof SurfaceClass){
					//
					// if the speciesContext is also within the surface, replace SpeciesContext name with Variable name
					//
					StructureMapping sm = simContext.getGeometryContext().getStructureMapping(sc.getStructure());
					if (sm.getGeometryClass() == geometryClass){
						if (scm.getVariable()!=null && !(scm.getVariable().getName().equals(ste.getName()))){
							return scm.getVariable().getName();
						}
					//
					// if the speciesContext is "inside" or "outside" the membrane
					//
					} else if (sm.getGeometryClass() instanceof SubVolume && ((SurfaceClass)geometryClass).isAdjacentTo((SubVolume)sm.getGeometryClass())) {
						SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
						if (!scs.isConstant()){
							if (!scs.isDiffusing() && !scs.isWellMixed()){
								throw new MappingException("Enable diffusion in Application '" + simContext.getName() 
										+  "'. This must be done for any species (e.g '" + sc.getName() + "') in flux reactions.\n\n" 
										+ "To save or run simulations, set the diffusion rate to a non-zero " +
												"value in Initial Conditions or disable those reactions in Specifications->Reactions.");
							}
						}
						if (scm.getVariable()!=null){
							return scm.getVariable().getName();
						}
					} else {
						throw new MappingException("species '"+sc.getName()+"' interacts with surface '"+geometryClass.getName()+"', but is not mapped spatially adjacent");
					}
				}
			}
			return getNameScope().getSymbolName(ste);
		}

/**
 * Insert the method's description here.
 * Creation date: (11/2/2005 4:42:01 PM)
 * @return cbit.vcell.math.Variable
 * @param name java.lang.String
 * @param exp cbit.vcell.parser.Expression
 */
protected Variable newFunctionOrConstant(String name, Expression exp, GeometryClass geometryClass) {
	if (exp.isNumeric()) {
		return new Constant(name, exp);
	}else{
		//
		// even if this expression is not numeric, if it is only a simple function of KMOLE - then make it a constant
		// this allows MathOverrides to contain simple unit conversion factors as needed.
		//
		String[] symbols = exp.getSymbols();
		String KMOLE_name = Model.ReservedSymbolRole.KMOLE.name();
		if (symbols!=null && symbols.length==1 && symbols[0].equals(KMOLE_name)){
			try {
				exp.getSubstitutedExpression(new Expression(KMOLE_name), new Expression(1.0)).flatten().evaluateConstant();
				return new Constant(name, exp);
			} catch (ExpressionException e) {
				logger.warn("unexpected: Variable "+name+"='"+exp.infix()+"' contains only "+KMOLE_name+" but failed evaluate to Constant");
			}
		}
		if (geometryClass!=null){
			return new Function(name,exp,new Domain(geometryClass));
		}else{
			return new Function(name,exp,null);
		}
	}
}

private Expression substituteGlobalParameters(Expression exp)
		throws ExpressionException {
			Expression exp2 = new Expression(exp);
			//
			// do until no more globals to substitute
			//
			int count = 0;
			ModelParameter[] modelParams = simContext.getModel().getModelParameters();
			while (true){
				if (count++ > 30){
					throw new ExpressionBindingException("infinite loop in eliminating function nesting");
				}
				//
				// get all symbols (identifiers), make list of globals used
				//
				String[] symbols = exp2.getSymbols();
				Vector<ModelParameter> globalsVector = new Vector<ModelParameter>();
				if (symbols != null) {
					for (int i = 0; i < symbols.length; i++) {
						for (int j = 0; j < modelParams.length; j++) {
							if (symbols[i].equals(modelParams[j].getName())) {
								globalsVector.addElement(modelParams[j]);
							}
						}
					}
				}
				//
				// if no more globals, done!
				//
				if (globalsVector.size()==0){
					break;
				}
				
				//
				// substitute out all globals at this level
				//
				for (int i = 0; i < globalsVector.size(); i++){
					ModelParameter mp = globalsVector.elementAt(i);
					Expression mpExp = new Expression(mp.getName());
					exp2.substituteInPlace(mpExp,mp.getExpression());
				}
			}
		//	exp2.bindExpression(simContext.getModel());
			return exp2;
		}

protected GeometryClass getDefaultGeometryClass(Expression expr)
		throws ExpressionException, MappingException {
			GeometryClass geometryClass = null;
			if (simContext.getGeometry().getDimension() == 0) {
				return null;
			}
			
			String[] symbols = expr.getSymbols();
			// if expr has no symbols, model param cannot be localized to a domain (its a const).
			if (symbols == null) {
				return null;
			} else {
				Expression modelParamExpr = substituteGlobalParameters(expr); 
				symbols = modelParamExpr.getSymbols();
				for (int k = 0; symbols != null && k < symbols.length; k++) {
					Structure symbolStructure = null;
					SymbolTableEntry ste = modelParamExpr.getSymbolBinding(symbols[k]);
					if (ste instanceof SpeciesContext) {
						symbolStructure = ((SpeciesContext)ste).getStructure();
					} else if (ste instanceof StructureSize) {
						symbolStructure = ((StructureSize)ste).getStructure();
					} else if (ste instanceof MembraneVoltage) {
						symbolStructure = ((MembraneVoltage)ste).getMembrane();
					}
					if (symbolStructure != null) {
						StructureMapping sm = simContext.getGeometryContext().getStructureMapping(symbolStructure);
						GeometryClass symbolGeomClass = sm.getGeometryClass();
						
						if (geometryClass == null) {
							geometryClass = symbolGeomClass;
						} else {
							if (geometryClass != symbolGeomClass) {
								if (geometryClass instanceof SurfaceClass) {
									if (symbolGeomClass instanceof SurfaceClass) {
										throw new MappingException("The expression '" + expr.infix() + "' references variables in surface domain '" + geometryClass.getName() + "' & surface domain '" + symbolGeomClass.getName() + "' that cannot be evaluated.");
									} else if (symbolGeomClass instanceof SubVolume){
										// geomClass : surfaceClass; symbolGeomClass : subVol 
										if (!((SurfaceClass)geometryClass).isAdjacentTo((SubVolume)symbolGeomClass)) {
											throw new MappingException("The expression '" + expr.infix() + "' references variables in surface domain '" + geometryClass.getName() + "' & volume domain '" + symbolGeomClass.getName() + "' that cannot be evaluated.");
										}
									} else {
										throw new MappingException("unexpected geometry class : " + symbolGeomClass.getClass());
									}
								} else if (geometryClass instanceof SubVolume){	// geometryClass is a SubVolume
									if (symbolGeomClass instanceof SubVolume) {
										// check if adjacent; if so, choose separating membrane.
										SurfaceClass surfaceClass = simContext.getGeometry().getGeometrySurfaceDescription().getSurfaceClass((SubVolume)symbolGeomClass, (SubVolume)geometryClass);
										if (surfaceClass != null) {
											geometryClass = surfaceClass;
										} else {
											throw new MappingException("The expression '" + expr.infix() + "' references variables in volume domain '" + geometryClass.getName() + "' & volume domain '" + symbolGeomClass.getName() + "' that cannot be evaluated.");
										}
									} else {
										// geomClass : subVol; symbolGeomClass = surfaceClass
										SurfaceClass surfaceSymbolGeomClass = (SurfaceClass)symbolGeomClass;
										if (!surfaceSymbolGeomClass.isAdjacentTo((SubVolume)geometryClass)) {
											throw new MappingException("The expression '" + expr.infix() + "' references variables in surface domain '" + surfaceSymbolGeomClass.getName() + "' & volume domain '" + geometryClass.getName() + "' that cannot be evaluated.");
										}else{
											geometryClass = symbolGeomClass;
										}
									}
								} else {
									throw new MappingException("unexpected geometry class : " + geometryClass.getClass());
								}
							}
						}
					}
				}
			}
			return geometryClass;
		}

final LocalizedDistanceToMembraneQuantity addLocalizedDistanceToMembraneQuantity(String name, SurfaceClass surfaceClass, SubVolume subVolume) {
	LocalizedDistanceToMembraneQuantity newQuantity = new LocalizedDistanceToMembraneQuantity(name, surfaceClass, subVolume);
	MathMappingQuantity previousQuantity = getMathMappingQuantity(name);
	if(previousQuantity != null){
		logger.info("MathMappingQuantity addLocalizedDistanceToMembraneQuantity found duplicate parameter for name "+name);
		if(!previousQuantity.compareEqual(newQuantity)){
			throw new RuntimeException("MathMappingParameter addLocalizedDistanceToMembraneQuantity found duplicate parameter for name '"+name+"'.");
		}
		return (LocalizedDistanceToMembraneQuantity)previousQuantity;
	}
	MathMappingQuantity newQuantities[] = (MathMappingQuantity[])BeanUtils.addElement(fieldMathMappingQuantities,newQuantity);
	setMathMapppingQuantities(newQuantities);
	return newQuantity;
}

final LocalizedDirectionToMembraneQuantity addLocalizedDirectionToMembraneQuantity(String name, SurfaceClass surfaceClass, SubVolume subVolume, QuantityComponent component) {
	LocalizedDirectionToMembraneQuantity newQuantity = new LocalizedDirectionToMembraneQuantity(name, surfaceClass, subVolume, component);
	MathMappingQuantity previousQuantity = getMathMappingQuantity(name);
	if(previousQuantity != null){
		logger.info("MathMappingQuantity addLocalizedDirectionToMembraneQuantity found duplicate parameter for name "+name);
		if(!previousQuantity.compareEqual(newQuantity)){
			throw new RuntimeException("MathMappingParameter addLocalizedDirectionToMembraneQuantity found duplicate parameter for name '"+name+"'.");
		}
		return (LocalizedDirectionToMembraneQuantity)previousQuantity;
	}
	MathMappingQuantity newQuantities[] = (MathMappingQuantity[])BeanUtils.addElement(fieldMathMappingQuantities,newQuantity);
	setMathMapppingQuantities(newQuantities);
	return newQuantity;
}

final ObservableConcentrationParameter addObservableConcentrationParameter(String name, Expression expr, int role,
		VCUnitDefinition unitDefn, RbmObservable argObservable) throws PropertyVetoException {
		
			GeometryClass geometryClass = simContext.getGeometryContext().getStructureMapping(argObservable.getStructure()).getGeometryClass();
			ObservableConcentrationParameter newParameter = new ObservableConcentrationParameter(name,expr,role,unitDefn,argObservable,geometryClass);
			MathMappingParameter previousParameter = getMathMappingParameter(name);
			if(previousParameter != null){
				logger.info("MathMappingParameter addObservableConcentrationParameter found duplicate parameter for name "+name);
				if(!previousParameter.compareEqual(newParameter)){
					throw new RuntimeException("MathMappingParameter addObservableConcentrationParameter found duplicate parameter for name '"+name+"'.");
				}
				return (ObservableConcentrationParameter)previousParameter;
			}
			//expression.bindExpression(this);
			MathMappingParameter newParameters[] = (MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
			setMathMapppingParameters(newParameters);
			return newParameter;
		}

final ObservableCountParameter addObservableCountParameter(String name, Expression expr, int role,
		VCUnitDefinition unitDefn, RbmObservable argObservable) throws PropertyVetoException {
		
			GeometryClass geometryClass = simContext.getGeometryContext().getStructureMapping(argObservable.getStructure()).getGeometryClass();
			ObservableCountParameter newParameter = new ObservableCountParameter(name,expr,role,unitDefn,argObservable,geometryClass);
			MathMappingParameter previousParameter = getMathMappingParameter(name);
			if(previousParameter != null){
				logger.info("MathMappingParameter addConcentrationParameter found duplicate parameter for name "+name);
				if(!previousParameter.compareEqual(newParameter)){
					if (!(previousParameter instanceof SpeciesCountParameter)){
						throw new RuntimeException("MathMappingParameter addObservableConcentrationParameter found duplicate parameter for name '"+name+"'.");
					}
				}
				return (ObservableCountParameter)previousParameter;
			}
			//expression.bindExpression(this);
			MathMappingParameter newParameters[] = (MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
			setMathMapppingParameters(newParameters);
			return newParameter;
		}

final SpeciesConcentrationParameter addSpeciesConcentrationParameter(String name, Expression expr, int role,
		VCUnitDefinition unitDefn, SpeciesContext argSpeciesContext) throws PropertyVetoException {
		
			GeometryClass geometryClass = simContext.getGeometryContext().getStructureMapping(argSpeciesContext.getStructure()).getGeometryClass();
			SpeciesConcentrationParameter newParameter = new SpeciesConcentrationParameter(name,expr,role,unitDefn,argSpeciesContext,geometryClass);
			MathMappingParameter previousParameter = getMathMappingParameter(name);
			if(previousParameter != null){
				logger.info("MathMappingParameter addConcentrationParameter found duplicate parameter for name "+name);
				if(!previousParameter.compareEqual(newParameter)){
					throw new RuntimeException("MathMappingParameter addConcentrationParameter found duplicate parameter for name '"+name+"'.");
				}
				return (SpeciesConcentrationParameter)previousParameter;
			}
			//expression.bindExpression(this);
			MathMappingParameter newParameters[] = (MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
			setMathMapppingParameters(newParameters);
			return newParameter;
		}

protected final SpeciesCountParameter addSpeciesCountParameter(String name, Expression expr, int role, VCUnitDefinition unitDefn, SpeciesContext argSpeciesContext) throws PropertyVetoException {

	GeometryClass geometryClass = simContext.getGeometryContext().getStructureMapping(argSpeciesContext.getStructure()).getGeometryClass();
	SpeciesCountParameter newParameter = new SpeciesCountParameter(name,expr,role,unitDefn,argSpeciesContext,geometryClass);
	MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		logger.info("MathMappingParameter addCountParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMappingParameter addCountParameter found duplicate parameter for name '"+name+"'.");
		}
		return (SpeciesCountParameter)previousParameter;
	}
	//expression.bindExpression(this);
	MathMappingParameter newParameters[] = (MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 12:44:00 AM)
 * @return cbit.vcell.mapping.MathMappingParameter
 * @param name java.lang.String
 * @param expression cbit.vcell.parser.Expression
 * @param role int
 */
final ProbabilityParameter addProbabilityParameter(String name, Expression expression, int role,
		VCUnitDefinition unitDefinition, ModelProcess argModelProcess) throws java.beans.PropertyVetoException,
		ExpressionBindingException {
		
			GeometryClass geometryClass = null;
			if (argModelProcess.getStructure()!=null){
				geometryClass = simContext.getGeometryContext().getStructureMapping(argModelProcess.getStructure()).getGeometryClass();
			}
			ProbabilityParameter newParameter = new ProbabilityParameter(name,expression,role,unitDefinition,argModelProcess,geometryClass);
			MathMappingParameter previousParameter = getMathMappingParameter(name);
			if(previousParameter != null){
				logger.info("MathMappingParameter addProbabilityParameter found duplicate parameter for name "+name);
				if(!previousParameter.compareEqual(newParameter)){
					throw new RuntimeException("MathMappingParameter addProbabilityParameter found duplicate parameter for name "+name);
				}
				return (ProbabilityParameter)previousParameter;
			}
			//expression.bindExpression(this);
			MathMappingParameter newParameters[] = (MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
			setMathMapppingParameters(newParameters);
			return newParameter;
		}

/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 4:28:57 PM)
 * @return cbit.vcell.mapping.MathSymbolMapping
 */
public MathSymbolMapping getMathSymbolMapping() throws MappingException, MathException,	MatrixException, ModelException, ExpressionException {
			
		mathSymbolMapping.reconcileVarNames(getMathDescription());
		
		return mathSymbolMapping;
	}

/**
 * Gets the mathMappingParameters index property (cbit.vcell.mapping.MathMappingParameter) value.
 * @return The mathMappingParameters property value.
 * @param index The index value into the property array.
 */
public MathMappingParameter getMathMappingParameters(int index) {
	return getMathMappingParameters()[index];
}

/**
 * This method was created in VisualAge.
 * @return Expression
 */
public KFluxParameter getFluxCorrectionParameter(StructureMapping sourceStructureMapping, StructureMapping targetStructureMapping)
		throws MappingException, ExpressionException {
			for (int i = 0; i < fieldMathMappingParameters.length; i++){
				if (fieldMathMappingParameters[i] instanceof KFluxParameter){
					KFluxParameter kfluxParameter = (KFluxParameter)fieldMathMappingParameters[i];
					if (kfluxParameter.getSourceStructureMapping() == sourceStructureMapping &&
						kfluxParameter.getTargetStructureMapping() == targetStructureMapping){
						return kfluxParameter;
					}
				}
			}
			//
			// not found, add new parameter
			//
			String sourceName = sourceStructureMapping.getStructure().getNameScope().getName();
			String targetName = targetStructureMapping.getStructure().getNameScope().getName();
			Parameter sourceSizeParameter = null;
			Parameter targetSizeParameter = null;
			if (sourceStructureMapping.getGeometryClass() instanceof CompartmentSubVolume){
				sourceSizeParameter = sourceStructureMapping.getSizeParameter();
			}else{
				sourceSizeParameter = sourceStructureMapping.getUnitSizeParameter();
			}
			if (targetStructureMapping.getGeometryClass() instanceof CompartmentSubVolume){
				targetSizeParameter = targetStructureMapping.getSizeParameter();
				if (targetSizeParameter==null || targetSizeParameter.getExpression()==null){
					throw new MappingException("structure mapping sizes not set for application "+simContext.getName());
				}
			}else{
				targetSizeParameter = targetStructureMapping.getUnitSizeParameter();
			}
			Expression fluxCorrectionExp = Expression.div(new Expression(sourceSizeParameter,simContext.getNameScope()),
										                  new Expression(targetSizeParameter,simContext.getNameScope()));
			VCUnitDefinition sourceSizeUnit = sourceSizeParameter.getUnitDefinition();
			VCUnitDefinition targetSizeUnit = targetSizeParameter.getUnitDefinition();
			VCUnitDefinition unit = sourceSizeUnit.divideBy(targetSizeUnit);
			fluxCorrectionExp.bindExpression(this);
			String parameterName = PARAMETER_K_FLUX_PREFIX+sourceName+"_"+targetName;
			KFluxParameter kFluxParameter = new KFluxParameter(parameterName,fluxCorrectionExp, unit, sourceStructureMapping,targetStructureMapping);
			MathMappingParameter[] newMathMappingParameters = (MathMappingParameter[])BeanUtils.addElement(this.fieldMathMappingParameters,kFluxParameter);
			try {
				setMathMapppingParameters(newMathMappingParameters);
			}catch (java.beans.PropertyVetoException e){
				throw new RuntimeException(e.getMessage(), e);
			}
			return kFluxParameter;
		}

/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 12:44:00 AM)
 * @return cbit.vcell.mapping.MathMappingParameter
 * @param name java.lang.String
 * @param expression cbit.vcell.parser.Expression
 * @param role int
 */
final MathMappingParameter addMathMappingParameter(String name, Expression expression, int role,
		VCUnitDefinition unitDefinition, GeometryClass geometryClass) throws java.beans.PropertyVetoException,
		ExpressionBindingException {
		
			MathMappingParameter newParameter = new MathMappingParameter(name,expression,role,unitDefinition, geometryClass);
			MathMappingParameter previousParameter = getMathMappingParameter(name);
			if(previousParameter != null){
				logger.info("MathMappingParameter addMathMappingParameter found duplicate parameter for name "+name);
				if(!previousParameter.compareEqual(newParameter)){
					throw new RuntimeException("MathMappingParameter addMathMappingParameter found duplicate parameter for name "+name);
				}
				return previousParameter;
			}
			expression.bindExpression(this);
			MathMappingParameter newParameters[] = (MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
			setMathMapppingParameters(newParameters);
			return newParameter;
		}

}

