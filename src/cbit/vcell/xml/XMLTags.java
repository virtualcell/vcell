package cbit.vcell.xml;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This class contains all the XML tags.
 */
public class XMLTags {
    //
    //CELLML-TAGS and SBML-TAGS
    public final static String CELLML_NAMESPACE_URI		 = "http://www.cellml.org/cellml/1.0#";
    public final static String CELLML_NAMESPACE_PREFIX	 = "cellml";
    public final static String CellmlRootNodeTag		 = "model";
	public final static String SbmlRootNodeTag			 = "sbml";
	public final static String SbmlAnnotationTag		 = "annotation";
	public final static String SbmlNotesTag				 = "notes";
	public final static String VCellInfoTag				 = "VCellInfo";

	// VCML tags
	public final static String VcmlRootNodeTag			 = "vcml";
	public static final String VCML_NS = "http://sourceforge.net/projects/vcell/vcml";
	public static final String VCML_NS_BLANK = "";

	//Tags related to MIRIAM
	public static final String HTML_XHTML_ATTR_TAG			= "xhtml";
	public static final String XHTML_URI					= "http://www.w3.org/1999/xhtml";
//	public static final String XHTML_HTML_BEGIN_TEMPLATE	= "<html "+HTML_XHTML_ATTR_TAG+"=\""+XHTML_URI+"\">\n<head><title></title></head>";
//	public static final String XHTML_HTML_END_TEMPLATE		= "</html>";
//	public static final String XHTML_HTML_BODY_BEGIN_TEMPLATE= XHTML_HTML_BEGIN_TEMPLATE+"<body>";
//	public static final String XHTML_HTML_BODY_END_TEMPLATE	= "</body>\n</html>";
//	public static final String XHTML_FULL_EMPTY_TEMPLATE	= XHTML_HTML_BODY_BEGIN_TEMPLATE+"<pre></pre>"+XHTML_HTML_BODY_END_TEMPLATE;
	public static final String XHTML_FULL_EMPTY_TEMPLATE	= "<html "+HTML_XHTML_ATTR_TAG+"=\""+XHTML_URI+"\">\n<head><title></title></head>\n<body><pre></pre></body>\n</html>";
	public static final String RDF_NAMESPACE_URI			= "http://www.w3.org/1999/02/22-rdf-syntax-ns#";	//xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
	public static final String RDF_NAMESPACE_PREFIX			= "rdf";
	public static final String RDF_RDF_NAME_TAG				= "RDF";
	public static final String RDF_DESCRIPTION_NAME_TAG		= "Description";
	public static final String RDF_LI_NAME_TAG				= "li";
	public static final String RDF_BAG_NAME_TAG				= "Bag";
	public static final String RDF_RESOURCE_ATTR_TAG		= "resource";
	public static final String RDF_PARSETYPE_ATTR_TAG		= "parseType";
	public static final String RDF_PARSETYPE_ATTR_DATE_VALUE= "Resource";
	public static final String DUBCORE_NAMESPACE_URI		= "http://purl.org/dc/elements/1.1/";				//xmlns:dc="http://purl.org/dc/elements/1.1/" 
	public static final String DUBCORE_NAMESPACE_PREFIX		= "dc"; 
	public static final String DUBCORE_CREATOR_NAME_TAG		= "creator"; 
	public static final String VCARD_NAMESPACE_URI			= "http://www.w3.org/2001/vcard-rdf/3.0#";			//xmlns:vCard="http://www.w3.org/2001/vcard-rdf/3.0#" 
	public static final String VCARD_NAMESPACE_PREFIX		= "vCard"; 
	public static final String VCARD_NAMEGROUP_NAME_TAG		= "N"; 
	public static final String VCARD_NAMEGROUP_GIVEN_NAME_TAG	= "Given"; 
	public static final String VCARD_NAMEGROUP_FAMILY_NAME_TAG	= "Family"; 
	public static final String VCARD_EMAIL_NAME_TAG			= "EMAIL"; 
	public static final String VCARD_ORGGROUP_NAME_TAG		= "ORG"; 
	public static final String VCARD_ORGGROUP_ORGNAME_NAME_TAG	= "Orgname"; 
	public static final String DUBCORETERMS_NAMESPACE_URI	= "http://purl.org/dc/terms/";						//xmlns:dcterms="http://purl.org/dc/terms/" 
	public static final String DUBCORETERMS_NAMESPACE_PREFIX= "dcterms"; 
	public static final String DUBCORETERMS_W3CDTF_NAME_TAG = "W3CDTF"; 
	public static final String BMBIOQUAL_NAMESPACE_URI	 	= "http://biomodels.net/biology-qualifiers/";		//xmlns:bqbiol="http://biomodels.net/biology-qualifiers/" 
	public static final String BMBIOQUAL_NAMESPACE_PREFIX 	= "bqbiol";
	public static final String BMMODELQUAL_NAMESPACE_URI 	= "http://biomodels.net/model-qualifiers/";			//xmlns:bqmodel="http://biomodels.net/model-qualifiers/"
	public static final String BMMODELQUAL_NAMESPACE_PREFIX = "bqmodel";
	
	//END
	
    //TAGS RELATED TO MATHMODEL
    public final static String MathModelTag = "MathModel";
    public final static String FilamentRegionVariableTag = "FilamentRegionVariable";
    public final static String MembraneRegionVariableTag = "MembraneRegionVariable";
    public final static String VolumeRegionVariableTag = "VolumeRegionVariable";
    public final static String MembraneRegionEquationTag = "MembraneRegionEquation";
    public final static String VolumeRegionEquationTag = "VolumeRegionEquation";
    public final static String UniformRateTag = "UniformRate";
    public final static String MembraneRateTag = "MembraneRate";
    public final static String VolumeRateTag = "VolumeRate";

    public final static String StochVolVariableTag = "StochasticVolumeVariable"; //stoch
    public final static String VarIniConditionTag = "VariableInitialCondition"; //stoch
    public final static String ActionTag = "Effect"; //stoch
    public final static String JumpProcessTag = "JumpProcess"; //stoch
    public final static String ProbabilityRateTag = "ProbabilityRate"; //stoch
    public final static String VarNameAttrTag = "VarName"; //stoch
    public final static String OperationAttrTag = "Operation"; //stoch
    public final static String StochAttrTag = "Stochastic"; //stoch , used with simulationspec Tag as an attribute
    public final static String ConcentrationAttrTag = "UseConcentration"; //used for stochastic application. store initial condition by concentration or number of particles.
    //END-MATHMODEL

    //TAGS RELATED TO VERSION
    public final static String VersionTag				= "Version";
    public final static String OwnerTag					= "Owner";
    public final static String IdentifierAttrTag		= "Identifier";
    public final static String AnnotationTag			= "Annotation";
    public final static String PrivacyAttrTag			= "Public";
    public final static String GroupAccessTag			= "GroupAccess";
    public final static String BranchIdAttrTag			= "BranchId";
    public final static String BranchPointRefTag		= "BranchPointRef";
    public final static String PreviousBranchIdAttrTag	= "PreviousBranchId";
    public final static String DateAttrTag				= "Date";
    public final static String FlagAttrTag				= "Archived";
    public final static String KeyValueAttrTag			= "KeyValue";
    public final static String HashAttrTag				= "Hash";
    public final static String UserTag					= "User";
    public final static String HiddenTag				= "IsHidden";
    public final static String FromVersionableTag		= "FromVersionable";
    public final static String ParentSimRefTag			= "ParentSimRef";
    //VERSION-TAGS END

    //Tags related to simulation
    //Begin-Simulation
    public final static String SimulationTag = "Simulation";
    public final static String SolverTaskDescriptionTag = "SolverTaskDescription";
    public final static String TaskTypeTag = "TaskType";
    public final static String KeepEveryTag = "KeepEvery";
    public final static String KeepAtMostTag = "KeepAtMost";
    public final static String TimeBoundTag = "TimeBound";
    public final static String TimeStepTag = "TimeStep";
    public final static String ErrorToleranceTag = "ErrorTolerance";
    public final static String AbsolutErrorToleranceTag = "Absolut";
    public final static String RelativeErrorToleranceTag = "Relative";
    public final static String StochSimOptionsTag = "StochSimOptions"; //stoch, added Jan 5th 2007, some options for stochastic simulation
    public final static String UseCustomSeedAttrTag = "UseCustomSeed"; //stoch
    public final static String CustomSeedAttrTag = "CustomSeed"; //stoch
    public final static String NumberOfTrialAttrTag = "NumberOfTrial"; //stoch
    //added 20th July, 2007 for simulation parameters of stochasitc hybrid solvers
    public final static String HybridEpsilonAttrTag = "Epsilon";//stoch
    public final static String HybridLambdaAttrTag = "Lambda";//stoch
    public final static String HybridMSRToleranceAttrTag = "MSRTolerance";//stoch
    public final static String HybridSDEToleranceAttrTag = "SDETolerance";//stoch
    public final static String MathOverridesTag = "MathOverrides";
    public final static String ConstantArraySpec = "ConstantArraySpec";
    public final static String MeshSpecTag = "MeshSpecification";
    public final static String SolverNameTag = "Solver";
    public final static String OutputOptionsTag = "OutputOptions";
    public final static String OutputTimesAttrTag = "OutputTimes";
    public final static String OutputTimeStepAttrTag = "OutputTimeStep";
	public final static String KeepEveryAttrTag = "KeepEvery";
	public final static String KeepAtMostAttrTag = "KeepAtMost";
	public final static String StopAtSpatiallyUniform = "StopAtSpatiallyUniform";
	public final static String DataProcessingInstructionsTag = "DataProcessingInstructions";
	public final static String DataProcessingScriptNameAttrTag = "DataProcessingScriptName";
    //End-Simulation

    // Begin electrical properties
    public final static String CurrentClampTag = "Current";
    public final static String VoltageClampTag = "Voltage";
    public final static String ElectrodeTag = "Electrode";
    public final static String ClampTag = "Clamp";
    public final static String ElectricalContextTag = "ElectricalContext";
    public final static String SpecificCapacitanceTag = "SpecificCapacitance";
    public final static String CalculateVoltageTag = "CalculateVoltage";
    public final static String MemVoltNameTag = "MembraneVoltage";
    public final static String InitialVoltageTag = "InitialVoltage";
    //End-electrical
    //BEGIN Species tags
    public final static String DBSpeciesTag				= "SpeciesBinding";
    public final static String CompoundTypeTag			= "Compound";
    public final static String EnzymeTypeTag			= "Enzyme";
    public final static String ProteinTypeTag			= "Protein";
    public final static String FormalSpeciesInfoTag		= "SpeciesInfo";
    public final static String DBFormalSpeciesTag		= "DBFormalSpecies";
    public final static String FormulaTag				= "Formula";
    public final static String CasIDTag					= "CasID";
    public final static String EnzymeTag				= "Enzyme";
    public final static String ECNumberTag				= "ECNumber";
    public final static String OrganismTag				= "Organism";
    public final static String AccessionTag				= "Accession";
    public final static String KeywordsTag				= "Keywords";
    public final static String DescriptionTag			= "Description";
    public final static String FormalIDTag				= "FormalID";
    public final static String SysNameTag				= "SysName";
    //END-species-tags
    //START Equations
    public final static String SolutionExpressionTag	= "Solution";
    public final static String SolutionTypeTag			= "SolutionType";
    public final static String UnknownTypeTag			= "Unknown";
    public final static String ConstructedTypeTag		= "Constructed";
    public final static String ExactTypeTag				= "Exact";
    //END-Equations
    //
    public final static String CompartmentBasedTypeTag	= "Compartmental";
    public final static String ImageBasedTypeTag		= "Image";
    public final static String AnalyticBasedTypeTag		= "Analytical";
    public final static String CoordinateTag = "Coordinate";
    public final static String PolyLineTypeTag = "PolyLine";
    public final static String SplineTypeTag = "Spline";
    public final static String ClosedAttrTag = "Closed";
    public final static String TypeAttrTag = "Type";
    public final static String CurveTag = "Curve";
    public final static String FilamentTag = "Filament";
    public final static String CharacteristicSizeTag = "CharacteristicSize";
    public final static String BoundariesTypesTag = "BoundariesTypes";
    public final static String VolumeVariableAttrTag = "VolumeVariable";
    public final static String PixelClassTag = "PixelClass";

    public final static String BioModelTag = "BioModel";

    public final static String NameTag = "Name";

    public final static String MathDescriptionTag = "MathDescription";
    public final static String ConstantTag = "Constant";
    public final static String ExpressionTag = "Expression";
    public final static String NameAttrTag = "Name";
    public final static String FunctionTag = "Function";
    public final static String MembraneVariableTag = "MembraneVariable";
    public final static String FilamentVariableTag = "FilamentVariable";
    public final static String VolumeVariableTag = "VolumeVariable";
    public final static String CompartmentSubDomainTag = "CompartmentSubDomain";
    public final static String FilamentSubDomainTag = "FilamentSubDomain";
    public final static String PriorityAttrTag = "Priority";
    public final static String MembraneSubDomainTag = "MembraneSubDomain";
    public final static String BoundaryTypeTag = "BoundaryType";
    public final static String BoundaryAttrTag = "Boundary";
    public final static String BoundaryAttrValueXm = "Xm";
    public final static String BoundaryAttrValueXp = "Xp";
    public final static String BoundaryAttrValueYm = "Ym";
    public final static String BoundaryAttrValueYp = "Yp";
    public final static String BoundaryAttrValueZm = "Zm";
    public final static String BoundaryAttrValueZp = "Zp";
    public final static String BoundaryTypeAttrTag = "Type";
    public final static String BoundaryTypeAttrValueNeumann = "Neumann";
    public final static String BoundaryTypeAttrValueDirichlet = "Dirichlet";
    public final static String BoundaryExpressionTag = "BoundaryExpression";
    public final static String BoundariesTag = "Boundaries";
    public final static String PdeEquationTag = "PdeEquation";
    public final static String OdeEquationTag = "OdeEquation";
    public final static String DiffusionTag = "Diffusion";
    public final static String InitialTag = "Initial";// older model before August,2008, assume the initial condition is in concentration
    //initial concentration and initial number of particles are available for stochastic application since August 2008.
    public final static String InitialConcentrationTag = "InitialConcentration"; 
    public final static String InitialAmountTag = "InitialCount";
	//    public final static String ConstructedTag = "Constructed";
	//    public final static String ExactTag = "Exact";
    public final static String JumpConditionTag = "JumpCondition";
    public final static String InFluxTag = "InFlux";
    public final static String OutFluxTag = "OutFlux";
    public final static String FastSystemTag = "FastSystem";
    public final static String FastInvariantTag = "FastInvariant";
    public final static String FastRateTag = "FastRate";
    public final static String InsideCompartmentTag = "InsideCompartment";
    public final static String OutsideCompartmentTag = "OutsideCompartment";
    public final static String MeshTag = "Mesh";
    public final static String SizeTag = "Size";
    public final static String XAttrTag = "X";
    public final static String YAttrTag = "Y";
    public final static String ZAttrTag = "Z";
    public final static String TaskDescriptionTag = "TaskDescription";
    public final static String OutputTag = "OutputTag";
    public final static String UnsteadyTag = "Unsteady";
    // I erased the tag of Unsteadytag 
    public final static String SteadyTag = "Steady";
    //I erased the tag of Steadytag
    public final static String TimeStepAttrTag = "TimeStep";
    public final static String DefaultTimeAttrTag = "DefaultTime";
    public final static String StartTimeAttrTag = "StartTime";
    public final static String EndTimeAttrTag = "EndTime";
    public final static String MaxTimeAttrTag = "MaxTime";
    public final static String MinTimeAttrTag = "MinTime";
    public final static String ToleranceAttrTag = "Tolerance";

    public final static String OptimizationSpecificationTag = "OptimizationSpecification";
    public final static String OptimizationVariableTag = "OptimizationVariable";
    public final static String LowerBoundTag = "LowerBound";
    public final static String UpperBoundTag = "UpperBound";
    public final static String InitialGuessTag = "InitialGuess";
    public final static String ObjectiveFunctionTag = "ObjectiveFunction";
    public final static String ConstraintTag = "Constraint";
    public final static String ConstraintTypeAttrTag = "ConstraintType";

    public final static String FluxStepTag = "FluxStep";
    public final static String StructureAttrTag = "Structure";
    public final static String FluxCarrierAttrTag = "FluxCarrier";
    public final static String FluxCarrierValenceAttrTag = "FluxCarrierValence";
    public final static String FluxOptionAttrTag = "FluxOption";
    public final static String FluxOptionMolecularOnly = "MolecularOnly";
    public final static String FluxOptionElectricalOnly = "ElectricalOnly";
    public final static String FluxOptionMolecularAndElectrical = "MolecularAndElectrical";
    public final static String SimpleReactionTag = "SimpleReaction";
    public final static String CatalystTag = "Modifier";
    public final static String ReactantTag = "Reactant";
    public final static String ProductTag = "Product";
    public final static String SpeciesContextRefAttrTag = "LocalizedCompoundRef";
    public final static String StoichiometryAttrTag = "Stoichiometry";
    public final static String KineticsTag = "Kinetics";
    public final static String KineticsTypeAttrTag = "KineticsType";
    public final static String KineticsTypeGeneralKinetics = "GeneralKinetics";
    public final static String KineticsTypeGeneralCurrentKinetics = "GeneralCurrentKinetics";
    public final static String KineticsTypeMassAction = "MassAction";
    public final static String KineticsTypeNernst = "NernstKinetics";
    public final static String KineticsTypeGHK = "GHKKinetics";
    public final static String KineticsTypeHMM_Irr = "HMMIrreversible";
    public final static String KineticsTypeHMM_Rev = "HMMReversible";
    public final static String KineticsTypeGeneralTotal_oldname = "GeneralTotalKinetics";
    public final static String KineticsTypeGeneralLumped = "GeneralLumpedKinetics";
    public final static String KineticsTypeGeneralCurrentLumped = "GeneralCurrentLumpedKinetics";
    
    public final static String PseudoSteadyAttrTag = "PseudoSteady";
    public final static String ModelParametersTag = "ModelParameters";
    public final static String OutputFunctionsTag = "OutputFunctions";
    public final static String ParameterTag = "Parameter";
    public final static String ExpressionAttrTag = "ExpressionAttr";
    public final static String RateTag = "Rate";
    public final static String ReactionRateTag = "ReactionRate";
    public final static String ReactionTag = "Reaction";
    public final static String FeatureTag = "Feature";
    public final static String MembraneTag = "Membrane";
    public final static String SpeciesContextTag = "LocalizedCompound";
	public final static String HasOverrideAttrTag = "OverrideName";
    public final static String SpeciesRefAttrTag = "CompoundRef";
    public final static String InsideFeatureTag = "InsideFeature";
    public final static String OutsideFeatureTag = "OutsideFeature";
    public final static String SpeciesTag = "Compound";
	//    public final static String FormalNameAttrTag = "FormalName"; 4/21/03; specie name scheme changed
	public final static String CommonNameAttrTag = "CommonName";
    public final static String OwnerKeyAttrTag = "OwnerKey";
    public final static String ModelTag = "Model";
    public final static String DiagramTag = "Diagram";
    public final static String SpeciesContextShapeTag = "LocalizedCompoundShape";
    public final static String SimpleReactionShapeTag = "SimpleReactionShape";
    public final static String FluxReactionShapeTag = "FluxReactionShape";
    public final static String SimpleReactionRefAttrTag = "SimpleReactionRef";
    public final static String FluxReactionRefAttrTag = "FluxReactionRef";
    public final static String LocationXAttrTag = "LocationX";
    public final static String LocationYAttrTag = "LocationY";

    public final static String GeometryTag = "Geometry"; 
    public final static String DimensionAttrTag = "Dimension";
    public final static String ExtentTag = "Extent";
    public final static String OriginTag = "Origin";
    public final static String ImageTag = "Image";
    public final static String SubVolumeTag = "SubVolume";
    public final static String HandleAttrTag = "Handle";
    public final static String AnalyticExpressionTag = "AnalyticExpression";
    public final static String ImagePixelValueTag = "ImagePixelValue";
    //public final static String CompartmentalAttrTag = "Compartmental";
    public final static String ImageDataTag = "ImageData";
    public final static String CompressedSizeTag = "CompressedSize";

    public final static String SimulationSpecTag = "SimulationSpec";
    public final static String GeometryContextTag = "GeometryContext";
    public final static String ReactionContextTag = "ReactionContext";
    public final static String FeatureMappingTag = "FeatureMapping";
    public final static String FeatureAttrTag = "Feature";
    public final static String SubVolumeAttrTag = "SubVolume";
    public final static String ResolvedAttrTag = "Resolved";
    public final static String MembraneMappingTag = "MembraneMapping";
    public final static String MembraneAttrTag = "Membrane";
    public final static String SurfaceToVolumeRatioTag = "SurfaceToVolumeRatio";
    public final static String VolumeFractionTag = "VolumeFraction";
    public final static String SpeciesContextSpecTag = "LocalizedCompoundSpec";
    public final static String ForceConstantAttrTag = "ForceConstant";
    public final static String EnableDiffusionAttrTag = "EnableDiffusion";
    public final static String ForceIndependentAttrTag = "ForceIndependent";
    public final static String ReactionSpecTag = "ReactionSpec";
    public final static String ReactionStepRefAttrTag = "ReactionStepRef";
    public final static String ReactionMappingAttrTag = "ReactionMapping";
    public static final String UseSymbolicJacobianAttrTag = "UseSymbolicJacobian";
    public static final String VCUnitDefinitionAttrTag = "Unit";

    public static final String ParamRoleAttrTag = "Role";
    public static final String ParamRoleUserDefinedTag = "user defined";
	public static final String ParamRoleReactionRateTag = "reaction rate";
	public static final String ParamRoleInwardCurrentTag = "inward current density";
//	public static final String ParamRoleForwardRateTag = "forward rate constant";
//	public static final String ParamRoleReverseRateTag = "reverse rate constant";
//	public static final String ParamRoleKmTag = "Km (1/2 max)";
//	public static final String ParamRoleMaxReactionRateTag = "max reaction rate";
//	public static final String ParamRoleKmForwardTag = "Km forward";
//	public static final String ParamRoleMaxForwardRateTag = "max forward rate";
//	public static final String ParamRoleKmReverseTag = "Km reverese";
//	public static final String ParamRoleMaxReverseRateTag = "max reverse rate";
//	public static final String ParamRolePermeabilityTag = "permeability";
//	public static final String ParamRoleConductivityTag = "conductivity";
//	public static final String ParamRoleAssumedCompartmentSizeTag = "assumedCompartmentSize";
//	public static final String ParamRoleTotalRateTag = "totalRate";
	public static final String DefaultReactionRateSymbol = "J";
	public static final String VelocityTag = "Velocity";

	//Surface Description elements
	public static final String SurfaceDescriptionTag = "SurfaceDescription";
	public static final String MembraneRegionTag = "MembraneRegion";
	public static final String VolumeRegionTag = "VolumeRegion";
	public static final String CutoffFrequencyAttrTag = "CutoffFrequency";
	public static final String NumSamplesXAttrTag = "NumSamplesX";
	public static final String NumSamplesYAttrTag = "NumSamplesY";
	public static final String NumSamplesZAttrTag = "NumSamplesZ";
	public static final String VolumeRegion_1AttrTag = "VolumeRegion1";
	public static final String VolumeRegion_2AttrTag = "VolumeRegion2";
	public static final String SizeAttrTag = "Size";
	public static final String RegionIDAttrTag = "RegionID";

	public static final String AnalysisTaskListTag = "AnalysisTaskList";
	public static final String ParameterEstimationTaskTag = "ParameterEstimationTask";

	public final static String EventTag = "Event";
	public final static String BioEventsTag = "BioEvents";

/**
 * XMLTags default constructor.
 */
public XMLTags() {
	super();
}
}