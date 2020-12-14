package org.jlibsedml;
/**
 * This class contains all the XML tags and attribute names in a SEDML document.
 */
public class SEDMLTags {
    
    private SEDMLTags(){}
    
	// SBML, MathML, SEDML namespaces
	public static final String SEDML_L1V1_NS = "http://sed-ml.org/";
	public static final String SEDML_L1V2_NS = "http://sed-ml.org/sed-ml/level1/version2";
	public static final String SBML_NS = "http://www.sbml.org/sbml/level2";
	public static final String SBML_NS_L2V4 = "http://www.sbml.org/sbml/level2/version4";
	public static final String MATHML_NS = "http://www.w3.org/1998/Math/MathML";
	public static final String XHTML_NS = "http://www.w3.org/1999/xhtml";
	
	// namespace prefixes:
	public static final String MATHML_NS_PREFIX = "math";
	public static final String SBML_NS_PREFIX = "sbml";
	
    public static final String ROOT_NODE_TAG			= "sedML";
    public static final String SED						= "Sed";
    public static final String VERSION_TAG				= "version";
	public static final String LEVEL_TAG 				= "level";

    
    public static final String NOTES					= "notes";
    public static final String ANNOTATION				= "annotation";
    public static final String META_ID_ATTR_NAME		= "metaid";
    public static final String MODELS					= "listOfModels";
    public static final String SIMS						= "listOfSimulations";
    public static final String TASKS					= "listOfTasks";
    public static final String DATAGENERATORS			= "listOfDataGenerators";
    public static final String OUTPUTS					= "listOfOutputs";

    // model attributes
    public static final String MODEL_TAG				= "model";
    public static final String MODEL_ATTR_ID			= "id";
    public static final String MODEL_ATTR_NAME			= "name";
    public static final String MODEL_ATTR_LANGUAGE		= "language";
    public static final String MODEL_ATTR_SOURCE		= "source";
    // types of model changes
    public static final String CHANGES					= "listOfChanges";
    public static final String CHANGE_ATTRIBUTE			= "changeAttribute";
    public static final String CHANGE_XML				= "changeXML";
    public static final String ADD_XML					= "addXML";
    public static final String REMOVE_XML				= "removeXML";
    public static final String NEW_XML                  = "newXML";
    public static final String COMPUTE_CHANGE			= "computeChange";
    public static final String COMPUTE_CHANGE_VARS      = "listOfVariables";
    public static final String COMPUTE_CHANGE_PARAMS    = "listOfParameters";

    // change attributes
    public static final String CHANGE_ATTR_TARGET		= "target";
    public static final String CHANGE_ATTR_NEWVALUE		= "newValue";
    public static final String CHANGE_ATTR_NEWXML		= "newXML";
    public static final String CHANGE_ATTR_MATH			= "math";

    // simulation attributes
    public static final String SIM_ATTR_ID				= "id";
    public static final String SIM_ATTR_NAME			= "name";
    public static final String SIM_ATTR_ALGORITM		= "algorithm";
    // types of simulations
    public static final String SIM_UTC					= "uniformTimeCourse";
    public static final String SIM_ANY                  = "anySimulation";
    public static final String SIM_OS                   = "oneStep";
    public static final String SIM_SS                   = "steadyState";
    
    //algorithm element
    public static final String ALGORITHM_TAG                = "algorithm";
    public static final String ALGORITHM_ATTR_KISAOID       = "kisaoID";
    public static final String ALGORITHM_PARAMETER_TAG      = "algorithmParameter";
    public static final String ALGORITHM_PARAMETER_LIST     = "listOfAlgorithmParameters";
    public static final String ALGORITHM_PARAMETER_KISAOID  = "kisaoID";
    public static final String ALGORITHM_PARAMETER_VALUE    = "value";
    
    // uniform time course attributes
    public static final String UTCA_INIT_T				= "initialTime";
    public static final String UTCA_OUT_START_T			= "outputStartTime";
    public static final String UTCA_OUT_END_T			= "outputEndTime";
    public static final String UTCA_POINTS_NUM			= "numberOfPoints";
    
    // one step attributes
    public static final String OS_STEP                  = "step";
    
    // task attributes
    public static final String TASK_TAG					= "task";
    public static final String TASK_ATTR_ID				= "id";
    public static final String TASK_ATTR_NAME			= "name";
    public static final String TASK_ATTR_MODELREF		= "modelReference";
    public static final String TASK_ATTR_SIMREF			= "simulationReference";
    // repeated task attributes
    public static final String REPEATED_TASK_TAG        = "repeatedTask";
    public static final String REPEATED_TASK_RESET_MODEL    = "resetModel";
    public static final String REPEATED_TASK_ATTR_RANGE     = "range";      // should be REPEATED_TASK_ATTR_RANGEREF
    public static final String REPEATED_TASK_RANGES_LIST    = "listOfRanges";
    public static final String REPEATED_TASK_CHANGES_LIST   = "listOfChanges";
    public static final String REPEATED_TASK_SUBTASKS_LIST  = "listOfSubTasks";
    public static final String SUBTASK_TAG                  = "subTask";
    public static final String SUBTASK_ATTR_ORDER           = "order";
    public static final String SUBTASK_ATTR_TASK            = "task";
    public static final String DEPENDENTTASK_TAG            = "dependentTask";
    public static final String DEPENDENT_TASK_SUBTASKS_LIST = "listOfDependentTasks";

    // set value
    public static final String SET_VALUE                    = "setValue";
    public static final String SET_VALUE_ATTR_TARGET        = "target";
    public static final String SET_VALUE_ATTR_RANGE_REF     = "range";
    public static final String SET_VALUE_ATTR_MODEL_REF     = "modelReference";

    // ranges
    public static final String RANGE_ATTR_ID                = "id";
    public static final String VECTOR_RANGE_TAG             = "vectorRange";
    public static final String VECTOR_RANGE_VALUE_TAG       = "value";
    public static final String UNIFORM_RANGE_TAG            = "uniformRange";
    public static final String UNIFORM_RANGE_ATTR_START     = "start";
    public static final String UNIFORM_RANGE_ATTR_END       = "end";
    public static final String UNIFORM_RANGE_ATTR_NUMP      = "numberOfPoints";
    public static final String UNIFORM_RANGE_ATTR_TYPE      = "type";
    public static final String FUNCTIONAL_RANGE_TAG         = "functionalRange";
    public static final String FUNCTIONAL_RANGE_INDEX       = "range";
    public static final String FUNCTIONAL_RANGE_VAR_LIST    = "listOfVariables";
    public static final String FUNCTIONAL_RANGE_PAR_LIST    = "listOfParameters";
    public static final String FUNCTION_TAG                 = "function";
    public static final String FUNCTION_MATH_TAG            = "math";

    
    // data generator attributes and children
    public static final String DATAGENERATOR_TAG            = "dataGenerator";
    public static final String DATAGEN_ATTR_ID              = "id";
    public static final String DATAGEN_ATTR_NAME            = "name";
    public static final String DATAGEN_ATTR_MATH            = "math";
    public static final String DATAGEN_ATTR_VARS_LIST       = "listOfVariables";
    public static final String DATAGEN_ATTR_PARAMS_LIST     = "listOfParameters";
    public static final String DATAGEN_ATTR_VARIABLE        = "variable";
    public static final String DATAGEN_ATTR_PARAMETER       = "parameter";
   
    // types of outputs
    public static final String OUTPUT_P2D					= "plot2D";
    public static final String OUTPUT_P3D					= "plot3D";
    public static final String OUTPUT_REPORT				= "report";
    // outputs attributes and children
    public static final String OUTPUT_ID					= "id";
    public static final String OUTPUT_NAME					= "name";
    public static final String OUTPUT_CURVES_LIST			= "listOfCurves";
    public static final String OUTPUT_SURFACES_LIST			= "listOfSurfaces";
    public static final String OUTPUT_DATASETS_LIST			= "listOfDataSets";
    public static final String OUTPUT_CURVE					= "curve";
    public static final String OUTPUT_SURFACE				= "surface";
    public static final String OUTPUT_DATASET				= "dataSet";
    public static final String OUTPUT_LOG_X					= "logX";
    public static final String OUTPUT_LOG_Y					= "logY";
    public static final String OUTPUT_LOG_Z					= "logZ";
    public static final String OUTPUT_DATA_REFERENCE		= "dataReference";
    public static final String OUTPUT_DATA_REFERENCE_X		= "xDataReference";
    public static final String OUTPUT_DATA_REFERENCE_Y		= "yDataReference";
    public static final String OUTPUT_DATA_REFERENCE_Z		= "zDataReference";
    public static final String OUTPUT_DATASET_LABEL		    = "label";
    
    
    // variable attributes
    public static final String VARIABLE_ID					= "id";
    public static final String VARIABLE_NAME				= "name";
    public static final String VARIABLE_TARGET				= "target";
    public static final String VARIABLE_SYMBOL				= "symbol";
    public static final String VARIABLE_TASK				= "taskReference";
    public static final String VARIABLE_MODEL				= "modelReference";
    
    // parameter attributes
    public static final String PARAMETER_ID					= "id";
    public static final String PARAMETER_NAME				= "name";
    public static final String PARAMETER_VALUE				= "value";
    
    // object kind
	public static final String CHANGE_ATTRIBUTE_KIND 		= "ChangeAttribute";
	public static final String CHANGE_XML_KIND 				= "ChangeXML";
	public static final String ADD_XML_KIND 				= "AddXML";
	public static final String REMOVE_XML_KIND 				= "RemoveXML";
    public static final String COMPUTE_CHANGE_KIND          = "ComputeChange";
    public static final String SET_VALUE_KIND               = "SetValue";
	public static final String DATAGEN_VARIABLE_KIND 		= "DataGenVariable";		// refers to a task
	public static final String CHANGE_MATH_VARIABLE_KIND 	= "ChangeMathVariable";		// refers to a model
	public static final String PLOT2D_KIND 					= "Plot2D";					// refers to a data generator
	public static final String PLOT3D_KIND 					= "Plot3D";
	public static final String REPORT_KIND 					= "Report";
    public static final String SIMUL_UTC_KIND               = "uniformTimeCourse";
    public static final String SIMUL_OS_KIND                = "oneStep";
    public static final String SIMUL_SS_KIND                = "steadyState";
    public static final String SIMUL_ANY_KIND				= "anySimulation";
    
    
	
	

}