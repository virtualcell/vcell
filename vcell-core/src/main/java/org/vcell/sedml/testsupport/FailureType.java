package org.vcell.sedml.testsupport;

/**
 * Why an OMEX archive did not produce the results it should have.
 *
 * <p>This is the <b>shared vocabulary</b> between the nightly baseline
 * ({@link OmexTestCase}) and the desktop client's prediction of which BioModels archives will
 * open ({@code vcell-client/src/main/resources/bioModelsNetInfo.xml}). The two files answer
 * different questions and only the first is a source of truth; the second is derived from it,
 * and {@code BioModelsNetInfoTest} decides which values here mean "will not open".
 *
 * <p><b>Three kinds of thing are mixed in this list, and only the naming distinguishes
 * them.</b> Nothing enforces the distinction, so consumers that care have to encode it
 * themselves -- which is why {@code BioModelsNetInfoTest} carries its own explicit set rather
 * than testing a property of the value:
 *
 * <ul>
 *   <li><b>Capability statements</b> -- VCell does not support this modelling feature, and a
 *       user hitting it needs to be told what is unsupported rather than shown a stack trace.
 *       Named {@code UNSUPPORTED_*}, plus {@link #OPERATION_NOT_SUPPORTED}; and, by their
 *       comments only, {@link #SEDML_SBML_LEVEL_CHANGE} and
 *       {@link #NESTED_SEDML_REPEATED_TASK}. These are stable: they change when we decide to
 *       support something, not run to run.</li>
 *   <li><b>Defects</b> -- we tried and something broke. {@link #MATH_GENERATION_FAILURE},
 *       {@link #NULL_POINTER_EXCEPTION}, {@link #DIVIDE_BY_ZERO}, {@link #SOLVER_FAILURE} and
 *       most of the rest. These are ours to fix, and a case can move between them as the code
 *       changes -- or, for a handful of numerically borderline models, between runs.</li>
 *   <li><b>Operational</b> -- we did not really get an answer. {@link #TOO_SLOW}, and
 *       {@link #UNCATEGORIZED_FAULT}, which means only "no classification rule matched" and
 *       so is a gap in this list rather than a property of the model.</li>
 * </ul>
 *
 * <p>When adding a value, put the kind in the name: an {@code UNSUPPORTED_} prefix is read as
 * a promise to the user about VCell's scope, so do not use it for something we intend to fix.
 */
@SuppressWarnings("unused")
public enum FailureType {
    ARRAY_INDEX_OUT_OF_BOUNDS,
    BAD_EULER_FORWARD,
    DIVIDE_BY_ZERO,
    EXPRESSIONS_DIFFERENT,
    EXPRESSION_BINDING,
    GEOMETRY_SPEC_DIFFERENT,
    HDF5_FILE_ALREADY_EXISTS, // reports.h5 file already exists, so action is blocked. Fixed in branch to be merged in.
    MATHOVERRIDES_SurfToVol,
    MATH_GENERATION_FAILURE,
    MATH_OVERRIDES_A_FUNCTION,
    MATH_OVERRIDES_INVALID,
    NESTED_SEDML_REPEATED_TASK, // We can do a repeated task of a normal task, but not another repeated task.
    NULL_POINTER_EXCEPTION,
    OPERATION_NOT_SUPPORTED, // VCell simply doesn't have the necessary features to run this archive.
    SBML_IMPORT_FAILURE,
    SEDML_IMPORT_FAILURE,
    SEDML_DIFF_NUMBER_OF_BIOMODELS,
    SEDML_ERRONEOUS_UNIT_SYSTEM,
    SEDML_ERROR_CONSTRUCTING_SIMCONTEXT,
    SEDML_MATH_OVERRIDE_NAMES_DIFFERENT,
    SEDML_MATH_OVERRIDE_NOT_EQUIVALENT,
    SEDML_NONSPATIAL_STOCH_HISTOGRAM,
    SEDML_NO_MODELS_IN_OMEX,
    SEDML_SIMCONTEXT_NOT_FOUND_BY_NAME,
    SEDML_SIMULATION_NOT_FOUND_BY_NAME,
    SEDML_UNSUPPORTED_ENTITY,
    SEDML_UNSUPPORTED_MODEL_REFERENCE, // Model refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)
    SEDML_NON_UTC_SIMULATION_FOUND,
    SEDML_SBML_LEVEL_CHANGE, // unsupported SBML Level change within the SED-ML
    TOO_SLOW,
    UNCATEGORIZED_FAULT,
    UNITS_EXCEPTION,
    UNKNOWN_IDENTIFIER,
    SEDML_NO_SEDMLS_TO_EXECUTE,
    SEDML_PREPROCESS_FAILURE,
    UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM,
    UNSUPPORTED_NON_INT_STOCH,
    UNSUPPORTED_NON_NUMERIC_STOCH,
    SBML_XML_NODE_FAILURE,
    /**
     * The model imported and simulated, and then results export failed to map an SBML symbol back
     * to the VCell entity it came from. Distinct from {@link #SBML_IMPORT_FAILURE}: the failure is
     * at the far end of the pipeline, so a case landing here has got further than one that failed
     * to import, not less far.
     */
    SBML_RESULTS_MAPPING_FAILURE,
    SOLVER_FAILURE,
    UNSUPPORTED_DELAY_SBML,
    UNSUPPORTED_NON_CONSTANT_COMPARTMENTS,
    BIOMODEL_IMPORT_SEDML_FAILURE
}
