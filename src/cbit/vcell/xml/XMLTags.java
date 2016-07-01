/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.xml;

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
	public final static String FreeTextAnnotationTag	 = "FreeText";
	public final static String VCellRelatedInfoTag		 = "VCMLSpecific";

	// VCML tags
	public final static String VcmlRootNodeTag			 = "vcml";
	public static final String VCML_NS 					 = "http://sourceforge.net/projects/vcell/vcml";
	public static final String SBML_VCELL_NS 			 = "http://sourceforge.net/projects/vcell";
	public static final String VCML_NS_OLD 				 = "http://www.sbml.org/2001/ns/vcell";
	public static final String VCML_NS_BLANK 			 = "";
	public static final String VCELL_NS_PREFIX 			 = "vcell";
	public static final String SBML_SPATIAL_NS_PREFIX 	 = "spatial";

	//Tags related to MetaData
	public static final String HTML_XHTML_ATTR_TAG			= "xhtml";
	public static final String METADATA_NS					= "http://vcell.org/data";
	public static final String METADATA_SEP					= "#";
	public static final String METADATA_NS_EXTENDED			= METADATA_NS  + METADATA_SEP;
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
	public static final String BMBIOQUAL_NAMESPACE_URI	 	= "http://biomodels.net/biology-qualifiers#";		//xmlns:bqbiol="http://biomodels.net/biology-qualifiers/" 
	public static final String BMBIOQUAL_NAMESPACE_PREFIX 	= "bqbiol";
	public static final String BMMODELQUAL_NAMESPACE_URI 	= "http://biomodels.net/model-qualifiers#";			//xmlns:bqmodel="http://biomodels.net/model-qualifiers/"
	public static final String BMMODELQUAL_NAMESPACE_PREFIX = "bqmodel";
	public final static String TEXT_PROP					= "TEXT";
	public final static String RELATIONSHIP_PROP			= "RELATIONSHIP";
	public final static String NODEID_PROP					= "nodeID";

	
	// VCMEtaData/MIRIAM properties
	public static final String PROPERTY_ISVERSIONOF 		= "isVersionOf";
	public static final String PROPERTY_HASPART 			= "hasPart";
	public static final String PROPERTY_HASVERSION 			= "hasVersion";
	public static final String PROPERTY_IS					= "is";
	public static final String PROPERTY_ISDESCRIBEDBY	 	= "isDescribedBy";
	public static final String PROPERTY_ISHOMOLOGTO			= "isHomologTo";
	public static final String PROPERTY_ISPARTOF 			= "isPartOf";
	
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
    public final static String VarIniCount_OldTag = "VariableInitialCondition"; //stoch
	public final static String VarIniCountTag = "VariableInitialCount"; //stoch
	public final static String VarIniPoissonExpectedCountTag = "VariableInitialPoissonExpectedCount"; //stoch
    public final static String ActionTag = "Effect"; //stoch
    public final static String JumpProcessTag = "JumpProcess"; //stoch
    public final static String ProbabilityRateTag = "ProbabilityRate"; //stoch
    public final static String VarNameAttrTag = "VarName"; //stoch
    public final static String OperationAttrTag = "Operation"; //stoch
    public final static String StochAttrTag = "Stochastic"; //stoch , used with simulationspec Tag as an attribute
    public static final String RuleBasedAttrTag = "RuleBased"; //rule-based , used with simulationspec Tag as an attribute
    public final static String ConcentrationAttrTag = "UseConcentration"; //used for stochastic application. store initial condition by concentration or number of particles.
    public final static String RandomizeInitConditionTag = "RandomizeInitCondition"; //used for stochastic application. store boolean for randomizing initial condition
    public final static String InsufficientIterationsTag = "InsufficientIterations"; 		// used for flattening rule based reactions
    public final static String InsufficientMaxMoleculesTag = "InsufficientMaxMolecules";	// used for flattening rule based reactions
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
    public final static String SoftwareVersionAttrTag 	= "Version";
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
	public final static String RunParameterScanSerially = "RunParameterScanSerially";
    //End-Simulation

    // Begin electrical properties
    public final static String TotalCurrentClampTag = "TotalCurrent";
    public final static String CurrentDensityClampTag = "CurrentDensity";
    public final static String CurrentDensityClampTag_oldName = "Current";
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

    // Tags for CSGeometry
    public static final String CSGBasedTypeTag 					= "CSGObject";
    public static final String CSGPrimitiveTag					= "CSGPrimitive";
    public static final String CSGPrimitiveTypeTag				= "CSGPrimitiveType";
    public static final String CSGPseudoPrimitiveTag			= "CSGPseudoPrimitive";
    public static final String CSGObjectRefTag					= "CSGObjectRef";
    public static final String CSGSetOperatorTag				= "CSGSetOperator";
    public static final String CSGSetOperatorTypeTag			= "CSGSetOperatorType";
    public static final String CSGHomogeneousTransformationTag	= "CSGHomogeneousTransformation";
    public static final String CSGRotationTag					= "CSGRotation";
    public static final String CSGRotationXTag					= "CSGRotationX";
    public static final String CSGRotationYTag					= "CSGRotationY";
    public static final String CSGRotationZTag					= "CSGRotationZ";
    public static final String CSGRotationAngleInRadiansTag		= "CSGRotationAngleInRadians";
    public static final String CSGScaleTag						= "CSGScale";
    public static final String CSGScaleXTag						= "CSGScaleX";
    public static final String CSGScaleYTag						= "CSGScaleY";
    public static final String CSGScaleZTag						= "CSGScaleZ";
    public static final String CSGTranslationTag				= "CSGTranslation";
    public static final String CSGTranslationXTag				= "CSGTranslationX";
    public static final String CSGTranslationYTag				= "CSGTranslationY";
    public static final String CSGTranslationZTag				= "CSGTranslationZ";
    // end - tags for CSGeometry
    
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
    public final static String BEFORE_COMMENT_ATTR_TAG = "BeforeComment";
    public final static String AFTER_COMMENT_ATTR_TAG = "AfterComment";
    public final static String FunctionTag = "Function";
    public final static String VolumeFunctionTag = "VolumeFunction";
    public final static String MembraneFunctionTag = "MembraneFunction";
    
    public final static String AnnotatedFunctionTag = "AnnotatedFunction";
    public final static String ErrorStringTag = "ErrorString";
    public final static String FunctionTypeTag = "FunctionType";
    public final static String UserDefinedTag = "UserDefined";

    public final static String MembraneVariableTag = "MembraneVariable";
    public final static String FilamentVariableTag = "FilamentVariable";
    public final static String VolumeVariableTag = "VolumeVariable";
    public final static String DomainAttrTag = "Domain";
    public final static String LocationAttrTag = "Location";
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
    // tags for boundaryConditionValue and BoundaryConditionSpec to be used in CompartmentSubDomain and PDEquation.
    public final static String BoundaryConditionValueTag = "BoundaryConditionValue";
    public final static String BoundaryValueExpressionTag = "Value";
    public final static String BoundaryConditionSpecTag = "BoundaryConditionSpec";
    public final static String BoundarySubdomainNameTag = "BoundarySubdomainName";
    
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

    public final static String ReversibleAttrTag = "Reversible";
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
    public static final String KineticsTypeGeneralPermeability = "GeneralPermeabilityKinetics";
    public static final String KineticsTypeMacroscopic_Irr = "Macroscopic_IrrKinetics";
    public static final String KineticsTypeMicroscopic_Irr = "Microscopic_IrrKinetics";
    
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
    public final static String ParentFeatureTag = "ParentFeature";
    public static final String PositiveFeatureTag = "PositiveFeature";
    public static final String NegativeFeatureTag = "NegativeFeature";
    
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
    public final static String SurfaceClassTag = "SurfaceClass";
    public final static String SubVolume1RefAttrTag = "SubVolume1Ref";
    public final static String SubVolume2RefAttrTag = "SubVolume2Ref";
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
    public final static String GeometryClassAttrTag = "GeometryClass";
    public final static String VolumePerUnitAreaTag = "VolumePerUnitArea";
    public final static String VolumePerUnitVolumeTag = "VolumePerUnitVolume";
    public final static String ResolvedAttrTag = "Resolved";
    public final static String MembraneMappingTag = "MembraneMapping";
    public final static String MembraneAttrTag = "Membrane";
    public final static String SurfaceToVolumeRatioTag = "SurfaceToVolumeRatio";
    public final static String VolumeFractionTag = "VolumeFraction";
    public final static String AreaPerUnitAreaTag = "AreaPerUnitArea";
    public final static String AreaPerUnitVolumeTag = "AreaPerUnitVolume";
    
    public final static String SpeciesContextSpecTag = "LocalizedCompoundSpec";
    public final static String ForceConstantAttrTag = "ForceConstant";
    // public final static String EnableDiffusionAttrTag = "EnableDiffusion";
    public final static String SpatialAttrTag = "Spatial";
    public final static String WellMixedAttrTag = "WellMixed";
    public final static String ForceContinuousAttrTag = "ForceContinuous";
    public final static String ForceIndependentAttrTag = "ForceIndependent";
    public final static String ReactionSpecTag = "ReactionSpec";
    public final static String ReactionStepRefAttrTag = "ReactionStepRef";
    public final static String ReactionMappingAttrTag = "ReactionMapping";
    public static final String UseSymbolicJacobianAttrTag = "UseSymbolicJacobian";
    public static final String VCUnitDefinitionAttrTag = "Unit";

    public static final String ParamRoleAttrTag = "Role";
    public static final String ParamRoleUserDefinedTag = "user defined";
    public static final String ParamRolePotentialDifferenceTag = "potential difference";
    public static final String ParamRoleTotalCurrentTag = "current";
    public static final String ParamRoleTotalCurrentDensityOldNameTag = "total current";
    public static final String ParamRoleTotalCurrentDensityTag = "current density";
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
	public static final String GradientTag = "Gradient";

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

	// For events
	public final static String EventTag = "Event";
	public final static String BioEventsTag = "BioEvents";
	public final static String BioEventTag = "BioEvent";
	public final static String UseValuesFromTriggerTimeAttrTag = "UseValuesFromTriggerTime";
	public final static String TriggerTag = "Trigger";
	public final static String DelayTag = "Delay";
	public final static String EventAssignmentTag = "EventAssignment";
	public final static String EventAssignmentVariableAttrTag = "Variable";
  	public static final String BioEventTriggerTypeAttrTag = "TriggerType";
	
	
	// for rate rules
	public final static String RateRulesTag = "RateRules";
	public final static String RateRuleTag  = "RateRule";
	public final static String RateRuleVariableAttrTag  = "RateRuleVariable";
	
//	public final static String RateRuleVariablesTag = "RateRuleVariables";
//	public final static String RateRuleVariableTag  = "RateRuleVariable";
//	public static final String RoleVariableRateTag  = "variable rate";	
	
	// for database storage of simContext (application) related components
	public final static String ApplicationComponents = "AppComponents";
	public static final String ApplicationSpecificFlagsTag = "ApplicationSpecificFlags";
	
	public final static String MembraneRandomVariableTag = "MembraneRandomVariable";
	public final static String VolumeRandomVariableTag = "VolumeRandomVariable";
	public final static String RandomVariableSeedTag = "IntegerSeed";
	public final static String GaussianDistributionTag = "GaussianDistribution";
	public final static String GaussianDistributionMeanTag = "Mean";
	public final static String GaussianDistributionStandardDeviationTag = "StandardDeviation";
	public final static String UniformDistributionTag = "UniformDistribution";
	public final static String UniformDistributionMinimumTag = "Minimum";
	public final static String UniformDistributionMaximumTag = "Maximum";
	
	// For DataSymbols/DataContext
	public static final String DataContextTag = "DataContext";			// array of data symbols
	public static final String FieldDataSymbolTag = "FieldDataSymbol";	// the element
	// members
	public static final String DataSymbolNameTag = "DataSymbolName";
	public static final String DataSymbolTypeTag = "DataSymbolType";
	public static final String VCUnitDefinitionTag = "VCUnitDefinition";
	public static final String ExternalDataIdentifierTag = "ExternalDataIdentifier";
	public static final String FieldItemNameTag = "FieldItemName";
	public static final String FieldItemTypeTag = "FieldItemType";
	public static final String FieldItemTimeTag = "FieldItemTime";
	
	// bngl (rbm)
	public static final String RbmModelContainerTag = "RbmModelContainer";
	public static final String RbmMolecularTypeListTag = "MolecularTypeList";
	public static final String RbmSeedSpeciesListTag = "SeedSpeciesList";
	public static final String RbmObservableListTag = "RbmObservableList";
	public static final String RbmReactionRuleListTag = "ReactionRuleList";
	public static final String RbmMolecularTypeTag = "MolecularType";
	public static final String RbmMolecularTypePatternTag = "MolecularTypePattern";
	public static final String RbmParticipantPatternMatchTag = "ParticipantMatch";
	public static final String RbmMolecularComponentTag = "MolecularComponent";
	public static final String RbmMolecularComponentStatePatternTag = "ComponentStatePattern";
	public static final String RbmMolecularComponentStateDefinitionTag = "ComponentStateDefinition";
	public static final String RbmMolecularComponentPatternTag = "ComponentPattern";
	public static final String RbmStateAttrTag = "State";
	public static final String RbmBondTag = "Bond";
	public static final String RbmBondTypeAttrTag = "BondType";
	public static final String RbmMolecularTypeAllowableStateTag = "AllowableState";
	public static final String RbmMolecularTypeAnyTag = "Any";
	public static final String RbmSeedSpeciesTag = "SeedSpecies";
	public static final String RbmInitialConditionTag = "InitialCondition";
	public static final String RbmSpeciesPatternTag = "SpeciesPattern";
	public static final String RbmObservableTag = "Observable";
	public static final String RbmObservableTypeTag = "ObservableType";
	public static final String RbmReactionRuleTag = "ReactionRule";
	public static final String RbmReactantPatternTag = "ReactantPattern";
	public static final String RbmProductPatternTag = "ProductPattern";
	public static final String RbmNetworkConstraintsTag = "NetworkConstraints";
	public static final String RbmMaxIterationTag = "RbmMaxIteration";
	public static final String RbmMaxMoleculesPerSpeciesTag = "RbmMaxMoleculesPerSpecies";
	public static final String RbmMaxStoichiometryTag = "MaxStoichiometry";
	public static final String RbmIntegerAttrTag = "Integer";
	public static final String RbmMolecularTypeAnchorAllAttrTag = "AnchorAll";
	public static final String RbmMolecularTypeAnchorTag = "Anchor";
	public static final String RbmIndexAttrTag = "Index";
	public static final String RbmReactionRuleLabelTag = "ReactionRuleLabel";
	public static final String RbmReactionRuleReversibleTag = "ReactionRuleReversible";
	
	public static final String RbmKineticTypeAttrTag = "RbmKineticType";
	public static final String RbmKineticTypeMassAction = "MassAction";
	public static final String RbmKineticTypeMichaelisMenten = "MichaelisMenten";
	public static final String RbmKineticTypeSaturable = "Saturable";
	
	public static final String RbmMassActionKfAttrTag_DEPRECATED = "MassActionKf";
	public static final String RbmMassActionKrAttrTag_DEPRECATED = "MassActionKr";
	public static final String RbmMichaelisMentenKcatAttrTag_DEPRECATED = "MichaelisMentenKcat";
	public static final String RbmMichaelisMentenKmAttrTag_DEPRECATED = "MichaelisMentenKm";
	public static final String RbmSaturableVmaxAttrTag_DEPRECATED = "SaturableVmax";
	public static final String RbmSaturableKsAttrTag_DEPRECATED = "SaturableKs";	
	public static final String RbmMassActionKfRole = "MassActionKf";
	public static final String RbmMassActionKrRole = "MassActionKr";
	public static final String RbmMichaelisMentenKcatRole = "MichaelisMentenKcat";
	public static final String RbmMichaelisMentenKmRole = "MichaelisMentenKm";
	public static final String RbmSaturableVmaxRole = "SaturableVmax";
	public static final String RbmSaturableKsRole = "SaturableKs";
	public static final String RbmRuleRateRole = "RuleRate";
	public static final String RbmUserDefinedRole = "UserDefined";
	public static final String RbmReactantPatternsListTag = "ReactantPatternsList";
	public static final String RbmProductPatternsListTag = "ProductPatternsList";
	// reaction rule mapping in application components and application XML
	public static final String ReactionRuleSpecsTag = "ReactionRuleSpecs";
	public static final String ReactionRuleSpecTag = "ReactionRuleSpec";
	public static final String ReactionRuleRefAttrTag = "ReactionRuleRef";
	public static final String ReactionRuleMappingAttrTag = "ReactionRuleMapping";
	// bngl (math)
	public static final String StateAttrTag = "State";
	public static final String BondAttrTag = "Bond";
	public static final String ParticleMolecularTypeAllowableStateTag = "AllowableState";
	public static final String ParticleMolecularComponentPatternTag = "Component";
	public static final String ParticleMolecularTypePatternTag = "MolecularType";
	public static final String ParticleMolecularTypePatternMatchLabelAttrTag = "MatchLabel";
	public static final String VolumeParticleSpeciesPatternTag = "VolumeParticleSpeciesPattern";
	public static final String VolumeParticleSpeciesPatternsTag = "VolumeParticleSpeciesPatterns";
	public static final String VolumeParticleObservableTag = "VolumeParticleObservable";
	public static final String MembraneParticleObservableTag = "MembraneParticleObservable";
	public static final String ParticleObservableTag = "ParticleObservable";
	public static final String ParticleMolecularTypeTag = "ParticleMolecularType";
	// Smoldyn
	public final static String VolumeParticleVariableTag = "VolumeParticleVariable";
	public final static String MembraneParticleVariableTag = "MembraneParticleVariable";
	public final static String ParticleJumpProcessTag = "ParticleJumpProcess";
	public final static String ParticleProbabilityRateTag = "ParticleProbabilityRate";
	public final static String MacroscopicRateConstantTag = "MacroscopicRateConstant";
	public final static String InteractionRadiusTag = "InteractionRadius";
	public final static String SelectedParticleTag = "SelectedParticle";
	public final static String ProcessSymmetryFactorAttrTag = "ProcessSymmetryFactor";
	public final static String ParticlePropertiesTag 		= "ParticleProperties"; // particle
	public final static String ParticleInitialCountTag			= "ParticleInitialCount"; // particle
	public final static String ParticleInitialCountTag_old			= "ParticleInitial"; // particle
	public final static String ParticleCountTag			= "ParticleCount"; // particle
	public final static String ParticleLocationXTag		= "ParticleLocationX"; // particle
	public final static String ParticleLocationYTag		= "ParticleLocationY"; // particle
	public final static String ParticleLocationZTag		= "ParticleLocationZ"; // particle
	public final static String ParticleDiffusionTag		= "ParticleDiffusion"; // particle
	public final static String ParticleDriftXTag		= "ParticleDriftX"; // particle
	public final static String ParticleDriftYTag		= "ParticleDriftY"; // particle
	public final static String ParticleDriftZTag		= "ParticleDriftZ"; // particle
	public final static String SmoldynSimulationOptions	= "SmoldynSimulationOptions";
	public final static String SmoldynSimulationOptions_randomSeed	= "RandomSeed";
	public final static String SmoldynSimulationOptions_accuracy	= "Accuracy";
	public final static String SmoldynSimulationOptions_high_res	= "HighResolutionSample";
	public final static String SmoldynSimulationOptions_saveParticleFiles	= "saveParticleFiles";
	public final static String SmoldynSimulationOptions_gaussianTableSize	= "gaussianTableSize";
	
	public final static String NFSimSimulationOptions	= "NFSimSimulationOptions";				// =========================================
	public final static String NFSimSimulationOptions_observableComputationOff	= "ObservableComputationOff";
	public final static String NFSimSimulationOptions_moleculeDistance	= "MoleculeDistance";
	public final static String NFSimSimulationOptions_aggregateBookkeeping	= "AggregateBookkeeping";
	public final static String NFSimSimulationOptions_maxMoleculesPerType	= "MaxMoleculesPerType";
	public final static String NFSimSimulationOptions_equilibrateTime	= "EquilibrateTime";
	public final static String NFSimSimulationOptions_randomSeed	= "RandomSeed";
	public final static String NFSimSimulationOptions_preventIntraBonds	= "PreventIntraBonds";

	public final static String ParticleInitialConcentrationTag			= "ParticleInitialConcentration"; // particle
	public final static String ParticleDistributionTag			= "ParticleDistribution"; // particle

	public final static String SundialsSolverOptions	= "SundialsSolverOptions";
	public final static String SundialsSolverOptions_maxOrderAdvection	= "maxOrderAdvection";
	

	public final static String PathwayModelTag	= "pathwayModel";
	public final static String RelationshipModelTag	= "relationshipModel";
	public static final String relationshipObjectTag = "RelationshipObject";
	public static final String bioPaxObjectIdTag = "bioPaxObjectID";
	public static final String bioModelObjectIdTag = "bioModelObjectID";
	
	// Post Processing Block
	public final static String PostProcessingBlock = "PostProcessing";
	public final static String ExplicitDataGenerator = "Explicit";
	public final static String ProjectionDataGenerator = "Projection";
	public final static String ProjectionAxis = "Axis";
	public final static String ProjectionOperation = "Operation";
	public final static String ConvolutionDataGenerator = "Convolution";
	public final static String Kernel = "Kernel";
	public final static String KernelType_Gaussian = "Gaussian";
	public final static String KernelGaussianSigmaXY = "SigmaXY";
	public final static String KernelGaussianSigmaZ = "SigmaZ";
	
	// Microscope Measurement
	public final static String MicroscopeMeasurement = "MicroscopeMeasurement";
	public final static String ConvolutionKernel = "ConvolutionKernel";
	public final static String FluorescenceSpecies = "FluorescenceSpecies";
	public final static String ConvolutionKernel_Type_ProjectionZKernel = "ProjectionZKernel";	
	public final static String ConvolutionKernel_Type_GaussianConvolutionKernel = "GaussianConvolutionKernel";
	
	// Mode unit system
	public final static String ModelUnitSystemTag = "ModelUnitSystem";
	public final static String VolumeSubstanceUnitTag = "VolumeSubstanceUnit";
	public final static String MembraneSubstanceUnitTag = "MembraneSubstanceUnit";
	public final static String LumpedReactionSubstanceUnitTag = "LumpedReactionSubstanceUnit";
	public final static String VolumeUnitTag = "VolumeUnit";
	public final static String AreaUnitTag = "AreaUnit";
	public final static String LengthUnitTag = "LengthUnit";
	public final static String TimeUnitTag = "TimeUnit";
	
	//ChomboSolverSpec Option 
  public final static String ChomboSolverSpec = "ChomboSolverSpec";
  public final static String MaxBoxSizeTag = "MaxBoxSize";
  public final static String ViewLevelTag = "ViewLevel";
  public final static String FillRatioTag = "FillRatio";
  public final static String SaveVCellOutput = "SaveVCellOutput";
  public final static String SaveChomboOutput = "SaveChomboOutput";
  public final static String ROIExpressionTag = "ROIExpression";
  public final static String MeshRefinementTag = "MeshRefinement";
  public final static String RefinementRoiTag = "RefinementRoi";
  public final static String RefinementRoiTypeAttrTag = "Type";
  public final static String RefineRatios = "Ratios";
  public final static String RefineRoiLevelAttrTag = "Level";
  public final static String TagsGrowAttrTag = "TagsGrow";
  public final static String RefinementBoxTag = "RefinementBox";
  public final static String TimeIntervalTag = "TimeInterval";
	public final static String ActivateFeatureUnderDevelopment = "ActivateFeatureUnderDevelopment";
	public final static String SmallVolfracThreshold = "SmallVolfracThreshold";
  
  //Parallel processing
  public final static String NUM_PROCESSORS = "NumberProcessors";

}
