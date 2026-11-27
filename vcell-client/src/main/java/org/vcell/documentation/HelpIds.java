package org.vcell.documentation;

/**
 * Help pages that VCell opens directly: from Help &gt; VCell Help (see {@link HelpContext}), a
 * {@link HelpButton}, or {@link HelpViewer#showHelp(String)}.
 *
 * <p>A page's id is its Markdown file name without ".md" (in vcell-client/UserDocumentation/pages).
 * HelpBookTest fails if a constant here names a page that does not exist, so renaming a page
 * cannot silently break contextual help.
 */
public final class HelpIds {
	// ------------------------------------------------------------------ getting started
	/** The introduction to VCell; what the Welcome window's Learn VCell button opens. */
	public static final String INTRODUCTION = "GeneralOverview";
	public static final String LOGIN = "Login";
	public static final String PERMISSIONS = "Permissions";
	public static final String SIMULATION_JOBS = "ViewSimStatus";
	public static final String EXPORTED_DATA_VIEWER = "ExportedDataViewer";

	// ------------------------------------------------------------------ databases
	public static final String MODEL_EXPLORER = "ModelExplorer";
	public static final String FIELD_DATA = "fieldData";

	// ------------------------------------------------------------------ physiology
	public static final String PHYSIOLOGY = "PhysOverview";
	public static final String REACTION_DIAGRAM = "ReactDiagView";
	public static final String REACTIONS = "ReactTabView";
	public static final String STRUCTURES = "PhysStructTabView";
	public static final String SPECIES = "SpeciesTabView";
	public static final String MOLECULES = "Molecules";
	public static final String OBSERVABLES = "Observables";

	// ------------------------------------------------------------------ parameters, functions and units
	public static final String PARAMETERS_AND_FUNCTIONS = "ParametersAndFunctions";
	public static final String PREDEFINED_EXPRESSIONS = "PredefinedConstAndMathFns";
	public static final String MODEL_UNITS = "ModelUnits";

	// ------------------------------------------------------------------ applications
	public static final String APPLICATIONS = "ApplicationOverview";
	public static final String GEOMETRY = "GeometryOverview";
	public static final String ANALYTIC_GEOMETRY = "geom_Analytic";
	public static final String GEOMETRY_FROM_IMAGES = "geom_from_images";
	public static final String STRUCTURE_MAPPING = "geom_struct_mapping";
	public static final String SPATIAL_STRUCTURE_MAPPING = "geom_spatial_mapping";
	public static final String NONSPATIAL_STRUCTURE_MAPPING = "geom_nonspatial_mapping";
	public static final String KINEMATICS = "Kinematics";
	public static final String SPECIFICATIONS = "SpecificationsOverview";
	public static final String SPECIES_SPECIFICATIONS = "SpeciesSpecifications";
	public static final String SPRINGSALAD_SPECIES_SPECIFICATIONS = "SSSpeciesSpecifications";
	public static final String REACTION_SPECIFICATIONS = "ReactionsSpecifications";
	public static final String SPRINGSALAD_REACTION_SPECIFICATIONS = "SSReactionsSpecifications";
	public static final String NETWORK_SPECIFICATIONS = "NetworkSpecifications";
	public static final String NETWORK_FREE_SPECIFICATIONS = "NetworkFreeSpecifications";
	public static final String PROTOCOLS = "ProtocolsOverview";
	public static final String EVENTS = "Events";
	public static final String ELECTRICAL = "Electrical";
	public static final String MICROSCOPE_MEASUREMENTS = "MicroscopeMeasurement";
	public static final String RATE_RULES = "RateRules";
	public static final String ASSIGNMENT_RULES = "AssignmentRules";

	// ------------------------------------------------------------------ simulations and results
	public static final String SIMULATIONS = "simulations";
	public static final String SIMULATION_EDITOR = "simulationEditor";
	public static final String NFSIM_SIMULATION_EDITOR = "NFSimSimulationEditor";
	public static final String NUMERICAL_SOLVERS = "numerical_solvers";
	public static final String OUTPUT_FUNCTIONS = "output_functions";
	public static final String GENERATED_MATH = "generated_math";
	public static final String SIMULATION_RESULTS = "SimulationViewerOverview";
	public static final String SPATIAL_RESULTS = "SpatialSimResultsOverview";
	public static final String SPATIAL_RESULTS_SLICE_VIEW = "SimResSpatialSliceView";
	public static final String SPATIAL_RESULTS_SURFACE_VIEW = "SimResultsSpatialSurfaceView";
	public static final String POST_PROCESSING_STATISTICS = "PostProcessingStats";
	public static final String POST_PROCESSING_IMAGES = "PostProcessingImage";
	public static final String EXPORTING_SPATIAL_RESULTS = "ExportingSpatialSimResults";
	public static final String IMAGEJ_PLUGIN = "ImageJDataViewerPlugin";
	public static final String NONSPATIAL_RESULTS = "SimCompartmentResults";
	public static final String STOCHASTIC_HISTOGRAMS = "SimResultsNonSpatialStochHistogram";
	public static final String SPRINGSALAD_RESULTS = "SSResults";

	// ------------------------------------------------------------------ fitting
	public static final String PARAMETER_ESTIMATION = "PEOverview";
	public static final String PARAMETER_ESTIMATION_METHODS = "parameterEstimationMethods";
	public static final String PARAMETER_ESTIMATION_USAGE = "parameterEstimationUsage";

	// ------------------------------------------------------------------ pathways
	public static final String PATHWAYS = "PathwayOverview";
	public static final String PATHWAY_DIAGRAM = "PathwayDiagramView";
	public static final String PATHWAY_OBJECTS = "PathwayObjectsView";
	public static final String BIOPAX_SUMMARY = "BiopaxSummaryView";
	public static final String BIOPAX_TREE = "BiopaxTreeView";
	public static final String PATHWAY_COMMONS = "PathwayCommonsPanel";
	public static final String PATHWAY_PREVIEW = "PathwayImportTable";

	// ------------------------------------------------------------------ properties panes
	public static final String PROPERTIES_PANES = "PropertiesPaneOverview";
	public static final String REACTION_PROPERTIES = "PP_Reactions";
	public static final String MEMBRANE_FLUX_PROPERTIES = "PP_MemFlux";
	public static final String REACTION_RULE_PROPERTIES = "PP_ReactionRulesEditor";
	public static final String REACTION_RULE_KINETICS = "PP_ReactionRulesKinetics";
	public static final String SPECIES_PROPERTIES = "PP_Species";
	public static final String MOLECULE_PROPERTIES = "PP_Molecules";
	public static final String OBSERVABLE_PROPERTIES = "PP_Observables";
	public static final String STRUCTURE_PROPERTIES = "PP_Structures";
	public static final String PARAMETER_PROPERTIES = "PP_Parameters";
	public static final String STOICHIOMETRY_PROPERTIES = "PP_Stoichiometries";
	public static final String APPLICATION_PROPERTIES = "PP_ApplicationProps";
	public static final String DATABASE_FILE_INFO = "PP_VCellDatabaseInfo";
	public static final String ANNOTATIONS = "PP_Annotations";
	public static final String PROBLEMS = "PP_Problems";
	public static final String PATHWAY_PROPERTIES = "PP_Pathways";
	public static final String POINT_OBJECT_KINEMATICS = "PP_KinematicsPointObject";
	public static final String SURFACE_OBJECT_KINEMATICS = "PP_KinematicsSurfaceObject";
	public static final String VOLUME_OBJECT_KINEMATICS = "PP_KinematicsVolumeObject";
	public static final String POINT_PROCESS_KINEMATICS = "PP_KinematicsPointLocationProcess";
	public static final String SURFACE_PROCESS_KINEMATICS = "PP_KinematicsSurfaceProcess";
	public static final String VOLUME_PROCESS_KINEMATICS = "PP_KinematicsVolumeProcess";

	// ------------------------------------------------------------------ Virtual FRAP
	public static final String VIRTUAL_FRAP = "VFrapIntroduction";

	// ------------------------------------------------------------------ linked from windows (see HelpCoverageTest)
	public static final String OPEN = "Open";
	public static final String SAVE_AS = "SaveAs";
	public static final String IMPORT_BNGL = "ImportBNGL";
	public static final String SPATIAL_RESULTS_PLOTS = "SimResultsKymograph";
	public static final String SPATIAL_RESULTS_ROI_TOOLS = "SpatialSimResultsDrawingTools";
	public static final String VFRAP_DEFINING_ROIS = "VFrapDefiningROIs";
	public static final String VFRAP_SIMULATION = "VFrapSpatialSimulation";
	public static final String VERSION_MISMATCH = "VersionMismatch";
	public static final String VCELL_PREFERENCES = "VCellPreferences";
	public static final String COMPARE_APPLICATIONS = "CompareApplications";
	public static final String REACTION_SEARCH = "ReactionSearch";
	public static final String QUICK_FIX = "QuickFixSimulation";
	public static final String MATH_MODEL = "MathModelOverview";
	public static final String MATH_MODEL_VCML_EDITOR = "MathModelVCMLEditor";
	public static final String MEMBRANE_SPECIFICATIONS = "MembraneSpecifications";
	public static final String LANGEVIN_SOLVER_OPTIONS = "LangevinSolverOptions";
	public static final String SMOLDYN_SOLVER_OPTIONS = "SmoldynSolverOptions";
	public static final String MASS_CONSERVATION_REDUCTION = "MassConservationReduction";
	public static final String SIMULATION_TIME_LIMIT = "SimulationTimeLimit";

	private HelpIds() {
	}
}
