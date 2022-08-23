package org.vcell.sedml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom.Namespace;
//import org.jdom.Element;
import org.jlibsedml.Algorithm;
import org.jlibsedml.AlgorithmParameter;
import org.jlibsedml.ChangeAttribute;
import org.jlibsedml.ComputeChange;
import org.jlibsedml.Curve;
import org.jlibsedml.DataGenerator;
import org.jlibsedml.DataSet;
import org.jlibsedml.Libsedml;
import org.jlibsedml.Model;
import org.jlibsedml.Notes;
import org.jlibsedml.Parameter;
import org.jlibsedml.Plot2D;
import org.jlibsedml.Plot3D;
import org.jlibsedml.Range;
import org.jlibsedml.RepeatedTask;
import org.jlibsedml.Report;
import org.jlibsedml.SEDMLDocument;
import org.jlibsedml.SEDMLTags;
import org.jlibsedml.SedML;
import org.jlibsedml.SetValue;
import org.jlibsedml.SubTask;
import org.jlibsedml.Surface;
import org.jlibsedml.Task;
import org.jlibsedml.UniformRange;
import org.jlibsedml.UniformRange.UniformType;
import org.jlibsedml.UniformTimeCourse;
import org.jlibsedml.Variable;
import org.jlibsedml.VariableSymbol;
import org.jlibsedml.VectorRange;
import org.jlibsedml.XPathTarget;
import org.jlibsedml.modelsupport.KisaoOntology;
import org.jlibsedml.modelsupport.KisaoTerm;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.jlibsedml.modelsupport.SBMLSupport.CompartmentAttribute;
import org.jlibsedml.modelsupport.SBMLSupport.ParameterAttribute;
import org.jlibsedml.modelsupport.SBMLSupport.SpeciesAttribute;
import org.jlibsedml.modelsupport.SUPPORTED_LANGUAGE;
import org.jmathml.ASTCi;
import org.jmathml.ASTNode;
import org.jmathml.MathMLReader;
import org.sbml.libcombine.*;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.SimSpec;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.StructureSizeSolver;
import org.vcell.util.FileUtils;
import org.vcell.util.Pair;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.model.Model.ReservedSymbolRole;
import cbit.vcell.model.ModelProcess;
import cbit.vcell.model.ModelQuantity;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.VariableSymbolTable;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.NonspatialStochHybridOptions;
import cbit.vcell.solver.NonspatialStochSimOptions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class SEDMLExporter {
	private final static Logger logger = LogManager.getLogger(SEDMLExporter.class);

	private int sedmlLevel = 1;
	private int sedmlVersion = 2;
	private  SedML sedmlModel = null;
	private cbit.vcell.biomodel.BioModel vcBioModel = null;
	private ArrayList<String> sbmlFilePathStrAbsoluteList = new ArrayList<String>();
	private ArrayList<String> sedmlFilePathStrAbsoluteList = new ArrayList<String>();
	private List<Simulation> simsToExport = null;

	private static String DATAGENERATOR_TIME_NAME = "time";
	private static String DATAGENERATOR_TIME_SYMBOL = "t";
	
	public SEDMLExporter(BioModel argBiomodel, int argLevel, int argVersion, List<Simulation> argSimsToExport) {
		super();
		this.vcBioModel = argBiomodel;
		this.sedmlLevel = argLevel;
		this.sedmlVersion = argVersion;
		this.simsToExport = argSimsToExport;
	}
	
	public static void main(String[] args) {
//		if (args.length != 1) {
//			System.out.println("Usage:\n\t path_of_vcml_file\n" );
//	        System.exit(1);
//		} 
//		String pathName = args[0];
		String pathName = "c:\\dan\\SEDML\\SEDML2.vcml";
		try {
			String biomodelXmlStr = XmlUtil.getXMLString(pathName);
			BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(biomodelXmlStr.toString()));
			bioModel.refreshDependencies();
			
			// invoke the SEDMLEXporter
			SEDMLExporter sedmlExporter = new SEDMLExporter(bioModel, 1, 1, null);
			String absolutePath = "c:\\dan\\SEDML";
			String sedmlStr = sedmlExporter.getSEDMLFile(absolutePath, "SEDML2", false, false, false);
//			String absolutePath = ResourceUtil.getUserHomeDir().getAbsolutePath();
			String outputName = absolutePath+ "\\" + TokenMangler.mangleToSName(bioModel.getName()) + ".sedml";
			XmlUtil.writeXMLStringToFile(sedmlStr, outputName, true);
		} catch (IOException e) {
			throw new RuntimeException("Unable to read VCML file '" + pathName + "' into string.",e);
		} catch (XmlParseException e) {
			throw new RuntimeException("Unable to convert VCML file '" + pathName + "' to a biomodel.",e);
		}
	}
	
	public SEDMLDocument getSEDMLFile0(String sPath, String sBaseFileName, boolean bForceVCML, boolean bForceSBML, 
				boolean bFromOmex) {

		// Create an SEDMLDocument and create the SEDMLModel from the document, so that other details can be added to it in translateBioModel()
		SEDMLDocument sedmlDocument = new SEDMLDocument(this.sedmlLevel, this.sedmlVersion);
//		sedmlDocument.getSedMLModel().setAdditionalNamespaces(Arrays.asList(new Namespace[] { 
//                Namespace.getNamespace(SEDMLTags.SBML_NS_PREFIX, SEDMLTags.SBML_NS_L2V4)
//        }));

		final String SBML_NS = "http://www.sbml.org/sbml/level3/version2/core";
		final String SBML_NS_PREFIX = "sbml";
		final String VCML_NS = "http://sourceforge.net/projects/vcell/vcml";
		final String VCML_NS_PREFIX = "vcml";
		final String SPATIAL_NS = "https://sbml.org/documents/specifications/level-3/version-1/spatial";
		final String SPATIAL_NS_PREFIX = "spatial";
		
		List<Namespace> nsList = new ArrayList<>();
		Namespace ns = Namespace.getNamespace(SEDMLTags.MATHML_NS_PREFIX, SEDMLTags.MATHML_NS);
		nsList.add(ns);
		ns = Namespace.getNamespace(SBML_NS_PREFIX, SBML_NS);
		nsList.add(ns);
		ns = Namespace.getNamespace(VCML_NS_PREFIX, VCML_NS);
		nsList.add(ns);
		
		SimulationContext[] simContexts = vcBioModel.getSimulationContexts();
		for(SimulationContext sc : simContexts) {
			if(sc.getGeometry() != null && sc.getGeometry().getDimension() > 0) {
				ns = Namespace.getNamespace(SPATIAL_NS_PREFIX, SPATIAL_NS);
				nsList.add(ns);
				break;
			}
		}
		sedmlModel = sedmlDocument.getSedMLModel();
		sedmlModel.setAdditionalNamespaces(nsList);

		
		translateBioModelToSedML(sPath, sBaseFileName, bForceVCML, bForceSBML, bFromOmex);
		
		int models = sedmlModel.getModels().size();
		int tasks = sedmlModel.getTasks().size();
		int sims = sedmlModel.getSimulations().size();
		return sedmlDocument;
	}
	public String getSEDMLFile(String sPath, String sBaseFileName, boolean bForceVCML, boolean bForceSBML, boolean bFromOmex) {
		SEDMLDocument doc = getSEDMLFile0(sPath, sBaseFileName, bForceVCML, bForceSBML, bFromOmex);
		return doc.writeDocumentToString();
	}


	private void translateBioModelToSedML(String savePath, String sBaseFileName, boolean bForceVCML, boolean bForceSBML,
				boolean bFromOmex) {		// true if invoked for omex export, false if for sedml
		sbmlFilePathStrAbsoluteList.clear();
		// models
		try {
			SimulationContext[] simContexts = vcBioModel.getSimulationContexts();
			cbit.vcell.model.Model vcModel = vcBioModel.getModel();
			String sbmlLanguageURN = SUPPORTED_LANGUAGE.SBML_GENERIC.getURN();	// "urn:sedml:language:sbml";
			String vcmlLanguageURN = SUPPORTED_LANGUAGE.VCELL_GENERIC.getURN();	// "urn:sedml:language:vcml";
			String bioModelName = vcBioModel.getName();
			String bioModelID = TokenMangler.mangleToSName(bioModelName);
			//String usrHomeDirPath = ResourceUtil.getUserHomeDir().getAbsolutePath();
			SBMLSupport sbmlSupport = new SBMLSupport();		// to get Xpath string for variables.
			int simContextCnt = 0;	// for model count, task subcount
			boolean bSpeciesAddedAsDataGens = false;
			String sedmlNotesStr = "";

			for (SimulationContext simContext : simContexts) {

				// Export the application itself to SBML, with default overrides
				String sbmlString = null;
				int level = 3;
				int version = 1;
				boolean isSpatial = simContext.getGeometry().getDimension() > 0 ? true : false;
				Map<Pair <String, String>, String> l2gMap = null;		// local to global translation map

				boolean sbmlExportFailed = false;
				if(!bForceVCML) {	// we try to save to SBML
					try {
						// check if structure sizes are set for nonspatial models.
						if (!simContext.getGeometryContext().isAllSizeSpecifiedPositive() && simContext.getGeometry().getDimension()==0) {
							throw new RuntimeException("non-spatial SBML Model must have all absolute compartment sizes set (responsibility of SBMLExporter)");
						}
						Pair <String, Map<Pair <String, String>, String>> pair = XmlHelper.exportSBMLwithMap(vcBioModel, 3, 2, 0, isSpatial, simContext, null);
						sbmlString = pair.one;
						l2gMap = pair.two;
					} catch (Exception e) {
						logger.error("Export failed: " + e.getMessage(), e);
						sbmlExportFailed = true;
					}
				} else {	// we want to force VCML, we act as if saving to SBML failed
					sbmlExportFailed = true;
				}

				// applications that don't produce a proper sbml (for example NFSim or Smoldyn) are 
				// marked as failed, even if exporting to sbml didn't throw any exception
				if (simContext.getGeometry().getDimension() > 0 && simContext.getApplicationType() == Application.NETWORK_STOCHASTIC) {
					sbmlExportFailed = true;
				} else if(simContext.getApplicationType() == Application.RULE_BASED_STOCHASTIC) {
					sbmlExportFailed = true;
				}

				String simContextName = simContext.getName();
				String filePathStrAbsolute = null;
				String filePathStrRelative = null;
				String urn = null;
				String simContextId = null;
				
				if(bForceSBML == true && sbmlExportFailed == true) {
					continue;		// exclude this sim if sbml export failed and we are in sbml-only mode
				}
				
				if(sbmlExportFailed) {
					//						filePathStrAbsolute = Paths.get(savePath, bioModelName + ".vcml").toString();
					filePathStrAbsolute = Paths.get(savePath, sBaseFileName + ".vcml").toString();
					//						filePathStrRelative = bioModelName + ".vcml";
					filePathStrRelative = sBaseFileName + ".vcml";
					if(!bFromOmex) {	// the vcml file is managed elsewhere when called for omex
						String vcmlString = XmlHelper.bioModelToXML(vcBioModel);
						XmlUtil.writeXMLStringToFile(vcmlString, filePathStrAbsolute, true);
						sbmlFilePathStrAbsoluteList.add(filePathStrRelative);
					}
					urn = vcmlLanguageURN;
					sedmlModel.addModel(new Model(bioModelID, bioModelName, urn, filePathStrRelative));
				} else {
					//						filePathStrAbsolute = Paths.get(savePath, bioModelName + "_" + TokenMangler.mangleToSName(simContextName) + ".xml").toString();
					filePathStrAbsolute = Paths.get(savePath, sBaseFileName + "_" + TokenMangler.mangleToSName(simContextName) + ".xml").toString();
					//						filePathStrRelative = bioModelName + "_" +  TokenMangler.mangleToSName(simContextName) + ".xml";
					filePathStrRelative = sBaseFileName + "_" +  TokenMangler.mangleToSName(simContextName) + ".xml";
					XmlUtil.writeXMLStringToFile(sbmlString, filePathStrAbsolute, true);
					urn = sbmlLanguageURN;
					sbmlFilePathStrAbsoluteList.add(filePathStrRelative);
					simContextId = TokenMangler.mangleToSName(simContextName);
					sedmlModel.addModel(new Model(simContextId, simContextName, urn, filePathStrRelative));
				}

				MathMapping mathMapping = simContext.createNewMathMapping();
				MathSymbolMapping mathSymbolMapping = mathMapping.getMathSymbolMapping();

				// -------
				// create sedml objects (simulation, task, datagenerators, report, plot) for each simulation in simcontext 
				// -------
				

				int simCount = 0;
//				String taskRef = null;
				int overrideCount = 0;
				for (Simulation vcSimulation : simContext.getSimulations()) {
					// if we have a hash containing a subset of simulations to export
					// skip simulations not present in hash
					if (simsToExport != null && !simsToExport.contains(vcSimulation)) continue;

					// 1 -------> check compatibility
					// if simContext is non-spatial stochastic, check if sim is histogram; if so, skip it, it can't be encoded in sedml 1.x
					SolverTaskDescription simTaskDesc = vcSimulation.getSolverTaskDescription();
					if (simContext.getGeometry().getDimension() == 0 && simContext.isStoch()) {
						long numOfTrials = simTaskDesc.getStochOpt().getNumOfTrials();
						if (numOfTrials > 1) {
							String msg = "\n\t" + simContextName + " ( " + vcSimulation.getName() + " ) : export of non-spatial stochastic simulation with histogram option to SEDML not supported at this time.";
							sedmlNotesStr += msg;
							continue;
						}
					}
					
					// 2 ------->
					// create Algorithm and sedmlSimulation (UniformtimeCourse)
					SolverDescription vcSolverDesc = simTaskDesc.getSolverDescription();
					String kiSAOIdStr = vcSolverDesc.getKisao();
					Algorithm sedmlAlgorithm = new Algorithm(kiSAOIdStr);
					TimeBounds vcSimTimeBounds = simTaskDesc.getTimeBounds();
					double startingTime = vcSimTimeBounds.getStartingTime();
					String simName = vcSimulation.getName();
					UniformTimeCourse utcSim = new UniformTimeCourse(TokenMangler.mangleToSName(simName), simName, startingTime, startingTime, 
							vcSimTimeBounds.getEndingTime(), (int) simTaskDesc.getExpectedNumTimePoints(), sedmlAlgorithm);

					boolean enableAbsoluteErrorTolerance;		// --------- deal with error tolerance
					boolean enableRelativeErrorTolerance;
					if (vcSolverDesc.isSemiImplicitPdeSolver() || vcSolverDesc.isChomboSolver()) {
						enableAbsoluteErrorTolerance = false;
						enableRelativeErrorTolerance = true;
					} else if (vcSolverDesc.hasErrorTolerance()) {
						enableAbsoluteErrorTolerance = true;
						enableRelativeErrorTolerance = true;
					} else {
						enableAbsoluteErrorTolerance = false;
						enableRelativeErrorTolerance = false;
					}	
					if(enableAbsoluteErrorTolerance) {
						ErrorTolerance et = simTaskDesc.getErrorTolerance();
						String kisaoStr = ErrorTolerance.ErrorToleranceDescription.Absolute.getKisao();
						AlgorithmParameter sedmlAlgorithmParameter = new AlgorithmParameter(kisaoStr, et.getAbsoluteErrorTolerance()+"");
						sedmlAlgorithm.addAlgorithmParameter(sedmlAlgorithmParameter);
					}
					if(enableRelativeErrorTolerance) {
						ErrorTolerance et = simTaskDesc.getErrorTolerance();
						String kisaoStr = ErrorTolerance.ErrorToleranceDescription.Relative.getKisao();
						AlgorithmParameter sedmlAlgorithmParameter = new AlgorithmParameter(kisaoStr, et.getRelativeErrorTolerance()+"");
						sedmlAlgorithm.addAlgorithmParameter(sedmlAlgorithmParameter);
					}

					boolean enableDefaultTimeStep;		// ---------- deal with time step (code adapted from TimeSpecPanel.refresh()
					boolean enableMinTimeStep;
					boolean enableMaxTimeStep;
					if (vcSolverDesc.compareEqual(SolverDescription.StochGibson)) { // stochastic time
						enableDefaultTimeStep = false;
						enableMinTimeStep = false;
						enableMaxTimeStep = false;
					} else if(vcSolverDesc.compareEqual(SolverDescription.NFSim)) {
						enableDefaultTimeStep = false;
						enableMinTimeStep = false;
						enableMaxTimeStep = false;
					} else {
						// fixed time step solvers and non spatial stochastic solvers only show default time step.
						if (!vcSolverDesc.hasVariableTimestep() || vcSolverDesc.isNonSpatialStochasticSolver()) {
							enableDefaultTimeStep = true;
							enableMinTimeStep = false;
							enableMaxTimeStep = false;
						} else {
							// variable time step solvers shows min and max, but sundials solvers don't show min
							enableDefaultTimeStep = false;
							enableMinTimeStep = true;
							enableMaxTimeStep = true;			
							if (vcSolverDesc.hasSundialsTimeStepping()) {
								enableMinTimeStep = false;
							}
						}
					}
					TimeStep ts = simTaskDesc.getTimeStep();
					if(enableDefaultTimeStep) {
						String kisaoStr = TimeStep.TimeStepDescription.Default.getKisao();
						AlgorithmParameter sedmlAlgorithmParameter = new AlgorithmParameter(kisaoStr, ts.getDefaultTimeStep()+"");
						sedmlAlgorithm.addAlgorithmParameter(sedmlAlgorithmParameter);
					}
					if(enableMinTimeStep) {
						String kisaoStr = TimeStep.TimeStepDescription.Minimum.getKisao();
						AlgorithmParameter sedmlAlgorithmParameter = new AlgorithmParameter(kisaoStr, ts.getMinimumTimeStep()+"");
						sedmlAlgorithm.addAlgorithmParameter(sedmlAlgorithmParameter);
					}
					if(enableMaxTimeStep) {
						String kisaoStr = TimeStep.TimeStepDescription.Maximum.getKisao();
						AlgorithmParameter sedmlAlgorithmParameter = new AlgorithmParameter(kisaoStr, ts.getMaximumTimeStep()+"");
						sedmlAlgorithm.addAlgorithmParameter(sedmlAlgorithmParameter);
					}

					if(simTaskDesc.getSimulation().getMathDescription().isNonSpatialStoch()) {	// ------- deal with seed
						NonspatialStochSimOptions nssso = simTaskDesc.getStochOpt();
						if(nssso.isUseCustomSeed()) {
							String kisaoStr = SolverDescription.AlgorithmParameterDescription.Seed.getKisao();	// 488
							AlgorithmParameter sedmlAlgorithmParameter = new AlgorithmParameter(kisaoStr, nssso.getCustomSeed()+"");
							sedmlAlgorithm.addAlgorithmParameter(sedmlAlgorithmParameter);
						}
					} else {
						;	// (... isRuleBased(), isSpatial(), isMovingMembrane(), isSpatialHybrid() ...
					}

					if(vcSolverDesc == SolverDescription.HybridEuler || 						// -------- deal with hybrid solvers (non-spatial)
							vcSolverDesc == SolverDescription.HybridMilAdaptive ||
							vcSolverDesc == SolverDescription.HybridMilstein) {
						NonspatialStochHybridOptions nssho = simTaskDesc.getStochHybridOpt();

						String kisaoStr = SolverDescription.AlgorithmParameterDescription.Epsilon.getKisao();
						AlgorithmParameter sedmlAlgorithmParameter = new AlgorithmParameter(kisaoStr, nssho.getEpsilon()+"");
						sedmlAlgorithm.addAlgorithmParameter(sedmlAlgorithmParameter);

						kisaoStr = SolverDescription.AlgorithmParameterDescription.Lambda.getKisao();
						sedmlAlgorithmParameter = new AlgorithmParameter(kisaoStr, nssho.getLambda()+"");
						sedmlAlgorithm.addAlgorithmParameter(sedmlAlgorithmParameter);

						kisaoStr = SolverDescription.AlgorithmParameterDescription.MSRTolerance.getKisao();
						sedmlAlgorithmParameter = new AlgorithmParameter(kisaoStr, nssho.getMSRTolerance()+"");
						sedmlAlgorithm.addAlgorithmParameter(sedmlAlgorithmParameter);
					}
					if(vcSolverDesc == SolverDescription.HybridMilAdaptive) {					// --------- one more param for hybrid-adaptive
						NonspatialStochHybridOptions nssho = simTaskDesc.getStochHybridOpt();

						String kisaoStr = SolverDescription.AlgorithmParameterDescription.SDETolerance.getKisao();
						AlgorithmParameter sedmlAlgorithmParameter = new AlgorithmParameter(kisaoStr, nssho.getSDETolerance()+"");
						sedmlAlgorithm.addAlgorithmParameter(sedmlAlgorithmParameter);
					}

					// TODO: consider adding notes for the algorithm parameters, to provide human-readable description of kisao terms
					//						sedmlAlgorithm.addNote(createNotesElement(algorithmNotesStr));
					// TODO: even better, AlgorithmParameter in sed-ml should also have a human readable "name" field

					// add a note to utcSim to indicate actual solver name
					String simNotesStr = "Actual Solver Name : '" + vcSolverDesc.getDisplayLabel() + "'.";  
					utcSim.addNote(createNotesElement(simNotesStr));
					sedmlModel.addSimulation(utcSim);

					// 3 ------->
					// create Tasks
					Set<String> dataGeneratorTasksSet = new LinkedHashSet<>();	// tasks not referenced as subtasks by any other (repeated) task; only these will have data generators
					MathOverrides mathOverrides = new MathOverrides(vcSimulation, vcSimulation.getMathOverrides());
					if((sbmlExportFailed == false) && mathOverrides != null && mathOverrides.hasOverrides()) {
						String[] overridenConstantNames = mathOverrides.getOverridenConstantNames();
						String[] scannedConstantsNames = mathOverrides.getScannedConstantNames();
						HashMap<String, String> scannedParamHash = new HashMap<String, String>();
						HashMap<String, String> unscannedParamHash = new HashMap<String, String>();
						
						VariableSymbolTable varST = new VariableSymbolTable();
						String[] constantNames = mathOverrides.getAllConstantNames();
						final HashMap<String, Expression> substitutedConstants = new HashMap<>();
						{
							final ArrayList<Constant> overrides = new ArrayList<>();
							for (String constantName : constantNames) {
								overrides.add(new Constant(constantName, new Expression(mathOverrides.getActualExpression(constantName, 0))));
							}
							for (Constant override : overrides) {
								 varST.addVar(override);
							}
							for (Constant override : overrides) {
								 override.bind(varST);
							}
							for (Constant override : overrides) {
								 Expression flattened = MathUtilities.substituteFunctions(override.getExpression(), varST, true);
								 substitutedConstants.put(override.getName(), new Expression(flattened));
							}
						}
						
						// need to check for "leftover" overrides from parameter renaming or other model editing
						HashMap<String, String> missingParamHash = new HashMap<String, String>();
						for (String name : scannedConstantsNames) {
							if (!mathOverrides.isUnusedParameter(name)) {
								scannedParamHash.put(name, name);
							} else {
								missingParamHash.put(name, name);
							}
						}
						for (String name : overridenConstantNames) {
							if (!scannedParamHash.containsKey(name)) {
								if (!mathOverrides.isUnusedParameter(name)) {
									unscannedParamHash.put(name, name);
								} else {
									missingParamHash.put(name, name);
								}
							}
						}
						if (!missingParamHash.isEmpty()) {
							for (String missingParamName : missingParamHash.values()) {
								logger.error("WARNING: there is an override entry for non-existent parameter "+missingParamName);
							}
						}

						if (!unscannedParamHash.isEmpty() && scannedParamHash.isEmpty()) {
							// only parameters with simple overrides (numeric/expression) no scans
							// create new model with change for each parameter that has override; add simple task
							String overriddenSimContextId = simContextId + "_" + overrideCount;
							String overriddenSimContextName = simContextName + " modified";
							Model sedModel = new Model(overriddenSimContextId, overriddenSimContextName, sbmlLanguageURN, "#"+simContextId);
							overrideCount++;

							int variableCount = 0;
							for (String unscannedParamName : unscannedParamHash.values()) {
								SymbolTableEntry ste = getSymbolTableEntryForModelEntity(mathSymbolMapping, unscannedParamName);
								if(ste == null) {
									throw new RuntimeException("failed to retrieve vcell parameter from unscanned SEDML parameter " + unscannedParamName + " using mathSymbolMapping.");
								}
								Expression unscannedParamExpr = mathOverrides.getActualExpression(unscannedParamName, 0);
								if(unscannedParamExpr.isNumeric()) {
									// if expression is numeric, add ChangeAttribute to model created above
									XPathTarget targetXpath = getTargetAttributeXPath(ste, l2gMap);
									ChangeAttribute changeAttribute = new ChangeAttribute(targetXpath, unscannedParamExpr.infix());
									sedModel.addChange(changeAttribute);
								} else {
									
									Map<String, XPathTarget> symbolToTargetMap = new LinkedHashMap<>();
									// create setValue for unscannedParamName (which contains a scanned param in its expression)
									String[] symbols = unscannedParamExpr.getSymbols();
									for(String symbol : symbols) {
										SymbolTableEntry entry = getSymbolTableEntryForModelEntity(mathSymbolMapping, symbol);
										XPathTarget target = getTargetAttributeXPath(entry, l2gMap);
										symbolToTargetMap.put(symbol, target);
									}

									// non-numeric expression : add 'computeChange' to modified model
									XPathTarget targetXpath = getTargetAttributeXPath(ste, l2gMap);
									ComputeChange computeChange = new ComputeChange(targetXpath);
									
									Expression expr = new Expression(unscannedParamExpr);
									String[] exprSymbols = expr.getSymbols();
									for (String symbol : exprSymbols) {
										String varName = overriddenSimContextId + "_" + symbol + "_" + variableCount;
										Variable sedmlVar = new Variable(varName, varName, overriddenSimContextId, symbolToTargetMap.get(symbol).toString());
										expr.substituteInPlace(new Expression(symbol), new Expression(varName));
										computeChange.addVariable(sedmlVar);
										variableCount++;
									}
									ASTNode math = Libsedml.parseFormulaString(expr.infix());
									computeChange.setMath(math);
									sedModel.addChange(computeChange);
								}
							}
							sedmlModel.addModel(sedModel);

							String taskId = "tsk_" + simContextCnt + "_" + simCount;
							Task sedmlTask = new Task(taskId, vcSimulation.getName(), sedModel.getId(), utcSim.getId());
							dataGeneratorTasksSet.add(sedmlTask.getId());
							sedmlModel.addTask(sedmlTask);
//							taskRef = taskId;		// to be used later to add dataGenerators : one set of DGs per model (simContext).
							
						} else if (!scannedParamHash.isEmpty() && unscannedParamHash.isEmpty()) {
							// only parameters with scans
							String taskId = "tsk_" + simContextCnt + "_" + simCount;
							String ownerTaskId = taskId;
							Task sedmlTask = new Task(taskId, vcSimulation.getName(), simContextId, utcSim.getId());
							dataGeneratorTasksSet.add(sedmlTask.getId());
							sedmlModel.addTask(sedmlTask);

							int repeatedTaskIndex = 0;
							for (String scannedConstName : scannedConstantsNames) {
								String repeatedTaskId = "repTsk_" + simContextCnt + "_" + simCount + "_" + repeatedTaskIndex;
								String rangeId = "range_" + simContextCnt + "_" + simCount + "_" + scannedConstName;
//								if(repeatedTaskIndex == 0) {
//									taskRef = repeatedTaskId;
//								}
								RepeatedTask rt = new RepeatedTask(repeatedTaskId, repeatedTaskId, true, rangeId);
								dataGeneratorTasksSet.add(rt.getId());
								SubTask subTask = new SubTask("0", ownerTaskId);
								dataGeneratorTasksSet.remove(ownerTaskId);
								rt.addSubtask(subTask);
								ConstantArraySpec constantArraySpec = mathOverrides.getConstantArraySpec(scannedConstName);
								// list of Ranges, if sim is parameter scan.
								if(constantArraySpec != null) {
									Range r = null;
									//										System.out.println("     " + constantArraySpec.toString());
									if(constantArraySpec.getType() == ConstantArraySpec.TYPE_INTERVAL) {
										// ------ Uniform Range
										UniformType type = constantArraySpec.isLogInterval() ? UniformType.LOG : UniformType.LINEAR;
										r = new UniformRange(rangeId, constantArraySpec.getMinValue(), 
												constantArraySpec.getMaxValue(), constantArraySpec.getNumValues(), type);
										rt.addRange(r);
									} else {
										// ----- Vector Range
										cbit.vcell.math.Constant[] cs = constantArraySpec.getConstants();
										ArrayList<Double> values = new ArrayList<Double>();
										for (int i = 0; i < cs.length; i++){
											String value = cs[i].getExpression().infix();
											values.add(Double.parseDouble(value));
										}
										r = new VectorRange(rangeId, values);
										rt.addRange(r);
									}

									// list of Changes
									SymbolTableEntry ste = getSymbolTableEntryForModelEntity(mathSymbolMapping, scannedConstName);
									XPathTarget target = getTargetAttributeXPath(ste, l2gMap);
									//ASTNode math1 = new ASTCi(r.getId());		// was scannedConstName
									ASTNode math1 = Libsedml.parseFormulaString(r.getId());		// here the math is always the range id expression
									SetValue setValue = new SetValue(target, r.getId(), simContextId);
									setValue.setMath(math1);
									rt.addChange(setValue);
								} else {
									throw new RuntimeException("No scan ranges found for scanned parameter : '" + scannedConstName + "'.");
								}
								ownerTaskId = repeatedTaskId;
								repeatedTaskIndex++;
								
								sedmlModel.addTask(rt);
							}

						} else {
							// both scanned and simple parameters : create new model with change for each simple override; add RepeatedTask
							Map<String, RepeatedTask> rangeToRepeatedTaskHash = new LinkedHashMap<> ();
							List<RepeatedTask> repeatedTasksList = new ArrayList<> ();
							
							// create new model with change for each unscanned parameter that has override
							String overriddenSimContextId = simContextId + "_" + overrideCount;
							String overriddenSimContextName = simContextName + " modified";
							Model sedModel = new Model(overriddenSimContextId, overriddenSimContextName, sbmlLanguageURN, "#"+simContextId);
							overrideCount++;

							String taskId = "tsk_" + simContextCnt + "_" + simCount;
							String ownerTaskId = taskId;
							Task sedmlTask = new Task(taskId, vcSimulation.getName(), overriddenSimContextId, utcSim.getId());
							dataGeneratorTasksSet.add(sedmlTask.getId());
							sedmlModel.addTask(sedmlTask);

							// scanned parameters
							int repeatedTaskIndex = 0;
							int variableCount = 0;
							for (String scannedConstName : scannedConstantsNames) {
								String repeatedTaskId = "repTsk_" + simContextCnt + "_" + simCount + "_" + repeatedTaskIndex;
								String rangeId = "range_" + simContextCnt + "_" + simCount + "_" + scannedConstName;
//								if(repeatedTaskIndex == 0) {
//									taskRef = repeatedTaskId;
//								}
								RepeatedTask rt = new RepeatedTask(repeatedTaskId, repeatedTaskId, true, rangeId);
								dataGeneratorTasksSet.add(rt.getId());
								SubTask subTask = new SubTask("0", ownerTaskId);
								dataGeneratorTasksSet.remove(ownerTaskId);
								rt.addSubtask(subTask);
								ConstantArraySpec constantArraySpec = mathOverrides.getConstantArraySpec(scannedConstName);
								// list of Ranges, if sim is parameter scan.
								if(constantArraySpec != null) {
									Range r = null;
									//										System.out.println("     " + constantArraySpec.toString());
									if(constantArraySpec.getType() == ConstantArraySpec.TYPE_INTERVAL) {
										// ------ Uniform Range
										UniformType type = constantArraySpec.isLogInterval() ? UniformType.LOG : UniformType.LINEAR;
										r = new UniformRange(rangeId, constantArraySpec.getMinValue(), 
												constantArraySpec.getMaxValue(), constantArraySpec.getNumValues(), type);
										rt.addRange(r);
									} else {
										// ----- Vector Range
										cbit.vcell.math.Constant[] cs = constantArraySpec.getConstants();
										ArrayList<Double> values = new ArrayList<Double>();
										for (int i = 0; i < cs.length; i++){
											String value = cs[i].getExpression().infix();
											values.add(Double.parseDouble(value));
										}
										r = new VectorRange(rangeId, values);
										rt.addRange(r);
									}

									// use scannedParamHash to store rangeId for that param, since it might be needed if unscanned param has a scanned param in expr.
									if (scannedParamHash.get(scannedConstName).equals(scannedConstName)) {
										// the hash was originally populated as <scannedParamName, scannedParamName>. Replace 'value' with rangeId for scannedParam
										scannedParamHash.put(scannedConstName, r.getId());
										rangeToRepeatedTaskHash.put(r.getId(), rt);		// we'll need the right repeated task for this range later on, in the unscanned loop
									}

									// list of Changes
									SymbolTableEntry ste = getSymbolTableEntryForModelEntity(mathSymbolMapping, scannedConstName);
									XPathTarget target = getTargetAttributeXPath(ste, l2gMap);
									// TODO: math needs to refer to the parameter id, not name
									ASTNode math1 = Libsedml.parseFormulaString(r.getId());		// here the math is always the range id expression
									SetValue setValue = new SetValue(target, r.getId(), overriddenSimContextId);
									setValue.setMath(math1);
									rt.addChange(setValue);
								} else {
									throw new RuntimeException("No scan ranges found for scanned parameter : '" + scannedConstName + "'.");
								}
								ownerTaskId = repeatedTaskId;
								repeatedTaskIndex++;
								
								repeatedTasksList.add(rt);
							}

							// for unscanned parameter overrides
							for (String unscannedParamName : unscannedParamHash.values()) {
								SymbolTableEntry ste = getSymbolTableEntryForModelEntity(mathSymbolMapping, unscannedParamName);
								if(ste == null) {
									throw new RuntimeException("failed to retrieve vcell parameter from unscanned SEDML parameter " + unscannedParamName + " using mathSymbolMapping.");
								}
								Expression unscannedParamExpr = mathOverrides.getActualExpression(unscannedParamName, 0);
								if(unscannedParamExpr.isNumeric()) {
									// if expression is numeric, add ChangeAttribute to model created above
									XPathTarget targetXpath = getTargetAttributeXPath(ste, l2gMap);
									ChangeAttribute changeAttribute = new ChangeAttribute(targetXpath, unscannedParamExpr.infix());
									sedModel.addChange(changeAttribute);
								} else {
									// check for any scanned parameter in unscanned parameter expression
									String[] exprSymbols = unscannedParamExpr.getSymbols();
									boolean bHasScannedParameter = false;
									List<String> scannedParamNameInUnscannedParamExpList = new ArrayList<> ();
									for (String symbol : exprSymbols) {
										if (scannedParamHash.get(symbol) != null) {
											bHasScannedParameter = true;
											scannedParamNameInUnscannedParamExpList.add(new String(symbol));
										}
									}
									// (scanned parameter in expr) ? (add setValue for unscanned param in repeatedTask) : (add computeChange to modifiedModel)
									Map<String, XPathTarget> symbolToTargetMap = new LinkedHashMap<>();
									String[] symbols = unscannedParamExpr.getSymbols();
									for(String symbol : symbols) {
										SymbolTableEntry entry = getSymbolTableEntryForModelEntity(mathSymbolMapping, symbol);
										XPathTarget target = getTargetAttributeXPath(entry, l2gMap);
										symbolToTargetMap.put(symbol, target);
									}
									if (bHasScannedParameter) {
										// create setValue for unscannedParamName (which contains a scanned param in its expression)
										SymbolTableEntry entry = getSymbolTableEntryForModelEntity(mathSymbolMapping, unscannedParamName);
										XPathTarget target = getTargetAttributeXPath(entry, l2gMap);
										Set<String> rangeIdSet = new HashSet<>();
										for(String scannedParamNameInUnscannedParamExp : scannedParamNameInUnscannedParamExpList) {
											String rangeId = scannedParamHash.get(scannedParamNameInUnscannedParamExp);
											rangeIdSet.add(rangeId);	// all the ranges referred in the scannedParamNameInUnscannedParamExpList
										}
										for(String rangeId : rangeIdSet) {
											SetValue setValue = new SetValue(target, rangeId, overriddenSimContextId);	// @TODO: we have no range??
											Expression expr = new Expression(unscannedParamExpr);
											for(String symbol : symbols) {
												String symbolName = rangeId + "_" + symbol + "_" + variableCount;
												Variable sedmlVar = new Variable(symbolName, symbolName, overriddenSimContextId, symbolToTargetMap.get(symbol).toString());	// sbmlSupport.getXPathForSpecies(symbol));
												setValue.addVariable(sedmlVar);
												expr.substituteInPlace(new Expression(symbol), new Expression(symbolName));
												variableCount++;
											}
											ASTNode math = Libsedml.parseFormulaString(expr.infix());
											setValue.setMath(math);
											RepeatedTask rtRecovered = rangeToRepeatedTaskHash.get(rangeId);
											rtRecovered.addChange(setValue);
										}
									} else {
										// non-numeric expression : add 'computeChange' to modified model
										XPathTarget targetXpath = getTargetAttributeXPath(ste, l2gMap);
										ComputeChange computeChange = new ComputeChange(targetXpath);
										Expression expr = new Expression(unscannedParamExpr);
										for (String symbol : exprSymbols) {
											String varName = overriddenSimContextId + "_" + symbol + "_" + variableCount;
											Variable sedmlVar = new Variable(varName, varName, overriddenSimContextId, symbolToTargetMap.get(symbol).toString());
											expr.substituteInPlace(new Expression(symbol), new Expression(varName));
											computeChange.addVariable(sedmlVar);
											variableCount++;
										}
										ASTNode math = Libsedml.parseFormulaString(expr.infix());
										computeChange.setMath(math);
										sedModel.addChange(computeChange);
									}
								}
							}
							sedmlModel.addModel(sedModel);
							for(RepeatedTask rt : repeatedTasksList) {
								sedmlModel.addTask(rt);
							}
						}
					} else {						// no math overrides, add basic task.
						String taskId = "tsk_" + simContextCnt + "_" + simCount;
						
						// temporary workaround
						// TODO better fix
						simContextId = sbmlExportFailed? bioModelID : simContextId;
						
						Task sedmlTask = new Task(taskId, vcSimulation.getName(), simContextId, utcSim.getId());
						dataGeneratorTasksSet.add(sedmlTask.getId());
						sedmlModel.addTask(sedmlTask);
//						taskRef = taskId;		// to be used later to add dataGenerators : one set of DGs per model (simContext).
					}
					
					// 4 ------->
					// Create DataGenerators

					List<DataGenerator> dataGeneratorsOfSim = new ArrayList<> ();					
					Map<String, DataGenerator> timeDataGeneratorMap = new LinkedHashMap<>();
					for(String taskRef : dataGeneratorTasksSet) {
						// add one DataGenerator for 'time'
						String timeDataGenPrefix = DATAGENERATOR_TIME_NAME + "_" + taskRef;
						DataGenerator timeDataGen = sedmlModel.getDataGeneratorWithId(timeDataGenPrefix);
						org.jlibsedml.Variable timeVar = new org.jlibsedml.Variable(DATAGENERATOR_TIME_SYMBOL + "_" + taskRef, DATAGENERATOR_TIME_SYMBOL, taskRef, VariableSymbol.TIME);
						ASTNode math = Libsedml.parseFormulaString(DATAGENERATOR_TIME_SYMBOL + "_" + taskRef);
						timeDataGen = new DataGenerator(timeDataGenPrefix, timeDataGenPrefix, math);
						timeDataGen.addVariable(timeVar);
						sedmlModel.addDataGenerator(timeDataGen);
						dataGeneratorsOfSim.add(timeDataGen);
						timeDataGeneratorMap.put(taskRef, timeDataGen);
	
						// add dataGenerators for species
						// get species list from SBML model.
						//		        		Map<String, String> name2IdMap = new LinkedHashMap<> ();
						String dataGenIdPrefix = "dataGen_" + taskRef;
						if(sbmlExportFailed) {		// we try vcml export
							for(SpeciesContext sc : vcModel.getSpeciesContexts()) {
								String varName = sc.getName();
								String varId = varName + "_" + taskRef;
								//								name2IdMap.put(varName, varId);
								ASTNode varMath = Libsedml.parseFormulaString(varId);
								String dataGenId = dataGenIdPrefix + "_" + TokenMangler.mangleToSName(varName);
								DataGenerator dataGen = new DataGenerator(dataGenId, dataGenId, varMath);
								org.jlibsedml.Variable variable = new org.jlibsedml.Variable(varId, varName, taskRef, XmlHelper.getXPathForSpecies(varName));
								dataGen.addVariable(variable);
								sedmlModel.addDataGenerator(dataGen);
								dataGeneratorsOfSim.add(dataGen);
							}
						} else {
							String[] varNamesList = SimSpec.fromSBML(sbmlString).getVarsList();
							for (String varName : varNamesList) {
								String varId = varName + "_" + taskRef;
								//								name2IdMap.put(varName, varId);
								org.jlibsedml.Variable sedmlVar = new org.jlibsedml.Variable(varId, varName, taskRef, sbmlSupport.getXPathForSpecies(varName));
								ASTNode varMath = Libsedml.parseFormulaString(varId);
								String dataGenId = dataGenIdPrefix + "_" + TokenMangler.mangleToSName(varName);			//"dataGen_" + varCount; - old code
								DataGenerator dataGen = new DataGenerator(dataGenId, dataGenId, varMath);
								dataGen.addVariable(sedmlVar);
								sedmlModel.addDataGenerator(dataGen);
								dataGeneratorsOfSim.add(dataGen);
							}
						}
	
						// add DataGenerators for output functions here
						ArrayList<AnnotatedFunction> outputFunctions = simContext.getOutputFunctionContext().getOutputFunctionsList();
						for (AnnotatedFunction annotatedFunction : outputFunctions) {
							//							Expression originalFunctionExpression = annotatedFunction.getExpression();
							//							Expression modifiedFunctionExpr = new Expression(annotatedFunction.getExpression());
							//							System.out.println("Before: " + originalFunctionExpression);
							//							String[] symbols = modifiedFunctionExpr.getSymbols();
							//							for(String symbol : symbols) {
							//								String id = name2IdMap.get(symbol);
							//								if(id == null) {
							//									System.err.println("Could not find id for " + symbol);
							//								} else {
							//									modifiedFunctionExpr.substituteInPlace(new Expression(symbol), new Expression(id));
							//								}
							//							}
							//							System.out.println("After:  " + modifiedFunctionExpr);
							//							ASTNode funcMath = Libsedml.parseFormulaString(modifiedFunctionExpr.infix());
							String dataGenId = dataGenIdPrefix + "_" + TokenMangler.mangleToSName(annotatedFunction.getName());		//"dataGen_" + varCount; - old code
							String varId = TokenMangler.mangleToSName(annotatedFunction.getName()) + taskRef;
	
							if(sbmlExportFailed) {		// VCML
								Expression exp = new Expression(varId);
								ASTNode funcMath = Libsedml.parseFormulaString(exp.infix());
								DataGenerator dataGen = new DataGenerator(dataGenId, dataGenId, funcMath);
								org.jlibsedml.Variable sedmlVar = new org.jlibsedml.Variable(varId, annotatedFunction.getName(), taskRef, 
										XmlHelper.getXPathForOutputFunction(simContextName, annotatedFunction.getName()));
								dataGen.addVariable(sedmlVar);
								sedmlModel.addDataGenerator(dataGen);
								dataGeneratorsOfSim.add(dataGen);
							} else {					// SBML
	
							}
	
	
	
	
							//							String[] functionSymbols = originalFunctionExpression.getSymbols();
							//							for (String symbol : functionSymbols) {
							//								String symbolName = TokenMangler.mangleToSName(symbol);
							//								// try to get symbol from model, if null, try simContext.mathDesc
							//								SymbolTableEntry ste = vcModel.getEntry(symbol);
							//								if (ste == null) {
							//									ste = simContext.getMathDescription().getEntry(symbol);
							//								}
							//								if (ste instanceof SpeciesContext || ste instanceof Structure || ste instanceof ModelParameter) {
							//									XPathTarget targetXPath = getTargetXPath(ste, l2gMap);
							//									if(sbmlExportFailed) {		// VCML
							//										if(ste instanceof SpeciesContext) {
							////											String varId = symbolName + "_" + taskRef;
							////											org.jlibsedml.Variable sedmlVar = new org.jlibsedml.Variable(varId, symbolName, taskRef, XmlHelper.getXPathForSpecies(symbolName));
							////											dataGen.addVariable(sedmlVar);
							//										} else {
							//											System.err.println("Not a species");
							//										}
							//									} else {					// SBML
							////										String varId = symbolName + "_" + taskRef;
							////										org.jlibsedml.Variable sedmlVar = new org.jlibsedml.Variable(varId, symbolName, taskRef, targetXPath.getTargetAsString());
							////										dataGen.addVariable(sedmlVar);
							//									}
							//								} else {
							//									double value = 0.0;
							//									if (ste instanceof Function) {
							//										try {
							//											value = ste.getExpression().evaluateConstant();
							//										} catch (Exception e) {
							//											e.printStackTrace(System.out);
							//											throw new RuntimeException("Unable to evaluate function '" + ste.getName() + "' for output function '" + annotatedFunction.getName() + "'.", e);
							//										}
							//									} else {
							//										value = ste.getConstantValue();
							//									}
							//									Parameter sedmlParameter = new Parameter(symbolName, symbolName, value);
							//									dataGen.addParameter(sedmlParameter);
							//								}
							//							}
						}
					}

					// 5 ------->
					// create Report and Plot

					for(String taskRef : dataGeneratorTasksSet) {
						// add output to sedml Model : 1 plot2d for each non-spatial simulation with all vars (species/output functions) vs time (1 curve per var)
						// ignoring output for spatial deterministic (spatial stochastic is not exported to SEDML) and non-spatial stochastic applications with histogram
						if (!(simContext.getGeometry().getDimension() > 0)) {
							String plot2dId = "plot2d_" + TokenMangler.mangleToSName(vcSimulation.getName());
							String reportId = "report_" + TokenMangler.mangleToSName(vcSimulation.getName());
							//								String reportId = "__plot__" + plot2dId;
							String plotName = simContextName + "_" + simName + "_plot";
							Plot2D sedmlPlot2d = new Plot2D(plot2dId, plotName);
							Report sedmlReport = new Report(reportId, plotName);
	
							sedmlPlot2d.addNote(createNotesElement("Plot of all variables and output functions from application '" + simContext.getName() + "' ; simulation '" + vcSimulation.getName() + "' in VCell model"));
							sedmlReport.addNote(createNotesElement("Report of all variables and output functions from application '" + simContext.getName() + "' ; simulation '" + vcSimulation.getName() + "' in VCell model"));
							DataGenerator dgtime = sedmlModel.getDataGeneratorWithId(DATAGENERATOR_TIME_NAME + "_" + taskRef);
							String xDataRef = dgtime.getId();
							String xDatasetXId = "__data_set__" + plot2dId + dgtime.getId();
							DataSet dataSet = new DataSet(xDatasetXId, DATAGENERATOR_TIME_NAME, xDataRef, xDataRef);	// id, name, label, data generator reference
							sedmlReport.addDataSet(dataSet);
	
							// add a curve for each dataGenerator in SEDML model
							int curveCnt = 0;
							// String id, String name, ASTNode math
							for (DataGenerator dg : dataGeneratorsOfSim) {
								// no curve for time, since time is xDateReference
								if (dg.getId().equals(xDataRef)) {
									continue;
								}
								String curveId = "curve_" + plot2dId + "_" + dg.getName();
								String datasetYId = "__data_set__" + plot2dId + dg.getName();
								Curve curve = new Curve(curveId, dg.getName(), false, false, xDataRef, dg.getId());
								sedmlPlot2d.addCurve(curve);
								//									// id, name, label, dataRef
								//									// dataset id    <- unique id
								//									// dataset name  <- data generator name
								//									// dataset label <- dataset id
								DataSet yDataSet = new DataSet(datasetYId, dg.getName(), dg.getId(), dg.getId());
								sedmlReport.addDataSet(yDataSet);
								curveCnt++;
							}
							sedmlModel.addOutput(sedmlPlot2d);
							sedmlModel.addOutput(sedmlReport);
						} else {		// spatial deterministic
							if(simContext.getApplicationType().equals(Application.NETWORK_DETERMINISTIC)) {	// we ignore spatial stochastic (Smoldyn)
								if(bForceVCML) {
									String reportId = "_report_" + TokenMangler.mangleToSName(vcSimulation.getName());
									Report sedmlReport = new Report(reportId, simContext.getName() + "plots");
									String xDataRef = sedmlModel.getDataGeneratorWithId(DATAGENERATOR_TIME_NAME + "_" + taskRef).getId();
									String xDatasetXId = "datasetX_" + DATAGENERATOR_TIME_NAME + "_" +timeDataGeneratorMap.get(taskRef).getId();
									DataSet dataSetTime = new DataSet(xDatasetXId, xDataRef, xDatasetXId, xDataRef);
									sedmlReport.addDataSet(dataSetTime);
									int surfaceCnt = 0;
									for (DataGenerator dg : dataGeneratorsOfSim) {
										if (dg.getId().equals(xDataRef)) {
											continue;
										}
										//										String datasetYId = "datasetY_" + surfaceCnt;
										String datasetYId = "__data_set__" + surfaceCnt + "_" + dg.getName();
										DataSet yDataSet = new DataSet(datasetYId, dg.getName(), datasetYId, dg.getId());
										sedmlReport.addDataSet(yDataSet);
	
										surfaceCnt++;
									}
									sedmlModel.addOutput(sedmlReport);
								} else {	// spatial deterministic SBML
									// TODO: add surfaces to the plots
									String plot3dId = "plot3d_" + TokenMangler.mangleToSName(vcSimulation.getName());
									String reportId = "report_" + TokenMangler.mangleToSName(vcSimulation.getName());
									String plotName = simContext.getName() + "plots";
									Plot3D sedmlPlot3d = new Plot3D(plot3dId, plotName);
									Report sedmlReport = new Report(reportId, plotName);
	
									sedmlPlot3d.addNote(createNotesElement("Plot of all variables and output functions from application '" + simContext.getName() + "' ; simulation '" + vcSimulation.getName() + "' in VCell model"));
									sedmlReport.addNote(createNotesElement("Report of all variables and output functions from application '" + simContext.getName() + "' ; simulation '" + vcSimulation.getName() + "' in VCell model"));
									DataGenerator dgtime = sedmlModel.getDataGeneratorWithId(DATAGENERATOR_TIME_NAME + "_" + taskRef);
									String xDataRef = dgtime.getId();
									String xDatasetXId = "__data_set__" + plot3dId + dgtime.getId();
									DataSet dataSet = new DataSet(xDatasetXId, DATAGENERATOR_TIME_NAME, xDataRef, xDataRef);	// id, name, label, data generator reference
									sedmlReport.addDataSet(dataSet);
	
									// add a curve for each dataGenerator in SEDML model
									int curveCnt = 0;
									// String id, String name, ASTNode math
									for (DataGenerator dg : dataGeneratorsOfSim) {
										// no curve for time, since time is xDateReference
										if (dg.getId().equals(xDataRef)) {
											continue;
										}
										String curveId = "curve_" + plot3dId + "_" + dg.getName();
										String datasetYId = "__data_set__" + plot3dId + dg.getName();
	
										DataSet yDataSet = new DataSet(datasetYId, dg.getName(), dg.getId(), dg.getId());
										sedmlReport.addDataSet(yDataSet);
										curveCnt++;
									}
									sedmlModel.addOutput(sedmlReport);
								}
							}
						}
					}
					simCount++;
				} // end - for 'sims'
				simContextCnt++;
			}	// end - for 'simContexts'
			
        	// if sedmlNotesStr is not null, there were some applications that could not be exported to SEDML (eg., spatial stochastic). Create a notes element and add it to sedml Model.
        	if (sedmlNotesStr.length() > 0) {
	        	sedmlNotesStr = "\n\tThe following applications in the VCell model were not exported to VCell : " + sedmlNotesStr;
	        	sedmlModel.addNote(createNotesElement(sedmlNotesStr));
        	}
        	if(sedmlModel.getModels() != null && sedmlModel.getModels().size() > 0) {
        		logger.trace("Number of models in the sedml is " + sedmlModel.getModels().size());
        	}
	

		} catch (Exception e) {
			throw new RuntimeException("Error adding model to SEDML document : " + e.getMessage(), e);
		}
	}

	private double getReservedSymbolValue(SymbolTableEntry ste) {
		cbit.vcell.model.Model vcModel = vcBioModel.getModel();
		if (ste instanceof ReservedSymbol) {
			try {
				if (((ReservedSymbol) ste).getRole().compareTo(ReservedSymbolRole.TEMPERATURE) == 0) {
					return vcBioModel.getSimulationContext(0).getTemperatureKelvin();
				}
				if (((ReservedSymbol) ste).getRole().compareTo(ReservedSymbolRole.PI_CONSTANT) == 0) {
					return vcModel.getPI_CONSTANT().getExpression().evaluateConstant();
				}
				if (((ReservedSymbol) ste).getRole().compareTo(ReservedSymbolRole.FARADAY_CONSTANT) == 0) {
					return vcModel.getFARADAY_CONSTANT().getExpression().evaluateConstant();
				}
				if (((ReservedSymbol) ste).getRole().compareTo(ReservedSymbolRole.FARADAY_CONSTANT_NMOLE) == 0) {
					return vcModel.getFARADAY_CONSTANT_NMOLE().getExpression().evaluateConstant();
				}
				if (((ReservedSymbol) ste).getRole().compareTo(ReservedSymbolRole.N_PMOLE) == 0) {
					return vcModel.getN_PMOLE().getExpression().evaluateConstant();
				}
				if (((ReservedSymbol) ste).getRole().compareTo(ReservedSymbolRole.GAS_CONSTANT) == 0) {
					return vcModel.getGAS_CONSTANT().getExpression().evaluateConstant();
				}
				if (((ReservedSymbol) ste).getRole().compareTo(ReservedSymbolRole.K_GHK) == 0) {
					return vcModel.getK_GHK().getExpression().evaluateConstant();
				}
				if (((ReservedSymbol) ste).getRole().compareTo(ReservedSymbolRole.KMILLIVOLTS) == 0) {
					return vcModel.getKMILLIVOLTS().getExpression().evaluateConstant();
				}
				if (((ReservedSymbol) ste).getRole().compareTo(ReservedSymbolRole.KMOLE) == 0) {
					return vcModel.getKMOLE().getExpression().evaluateConstant();
				}
			} catch (Exception e) {
				throw new RuntimeException("Unable to get the value of (reserved) VCell parameter : '" + ste.getName() + "' : ", e);
			} 
		} 
		return 0;
	}

	private Notes createNotesElement(String notesStr) {
		// create some xhtml. E.g.,
		org.jdom.Element para = new org.jdom.Element("p");
		para.setText(notesStr);
		// create a notes element
		Notes note = new Notes(para);
		return note;
	}

	public static SymbolTableEntry getSymbolTableEntryForModelEntity(MathSymbolMapping mathSymbolMapping, String paramName) {
		cbit.vcell.math.Variable mathVar = mathSymbolMapping.findVariableByName(paramName);
		SymbolTableEntry[] stEntries = mathSymbolMapping.getBiologicalSymbol(mathVar);
		if (stEntries == null || stEntries.length == 0) {
			throw new NullPointerException("No matching biological symbol for : " + paramName);
		}
		
		// if the extra stes in the array are KineticsProxyParameters/ModelQuantities, remove them from array. Should be left with only one entry for overriddenConstantName
		if (stEntries.length > 1) {
			//
			// If there are more than one stEntries, usually, it is a regular ste (species, global parameter, structure, etc) together with 
			// kineticsProxyParameters (that have the regular ste as target) or Model quantities (structure size, membrane voltage). 
			// So filtering out the kinticProxyParametes should leave only the regular parameter, 
			// which is what we want. If there are more, then there is a problem.
			//
			ArrayList<SymbolTableEntry> steList = new ArrayList<SymbolTableEntry>(Arrays.asList(stEntries));
			for (int i = 0; i < stEntries.length; i++) {
				if (stEntries[i] instanceof ProxyParameter) {
					SymbolTableEntry ppTargetSte = ((ProxyParameter)stEntries[i]).getTarget();
					if (steList.contains(ppTargetSte) || ppTargetSte instanceof ModelQuantity) {
						steList.remove(stEntries[i]);
					}
				}
				if (stEntries[i] instanceof ModelQuantity) {
					if (steList.contains(stEntries[i])) {
						steList.remove(stEntries[i]);
					}
				}
			}
			// after removing proxy parameters, cannot have more than one ste in list
			if (steList.size() == 0) {
				throw new RuntimeException("No mapping entry for constant : '" + paramName + "'.");
			}
			if (steList.size() > 1) {
				throw new RuntimeException("Cannot have more than one mapping entry for constant : '" + paramName + "'.");
			}
			SymbolTableEntry[] stes = (SymbolTableEntry[])steList.toArray(new SymbolTableEntry[0]);
			return stes[0];
		} else {
			return stEntries[0];
		}
	}

	private XPathTarget getTargetXPath(SymbolTableEntry ste, Map<Pair <String, String>, String> l2gMap) {
		SBMLSupport sbmlSupport = new SBMLSupport();		// to get Xpath string for variables.
		XPathTarget targetXpath = null;
		if (ste instanceof SpeciesContext || ste instanceof SpeciesContextSpecParameter) {
			String name = ste.getName();
			if (ste instanceof SpeciesContextSpecParameter) {
				name = ((SpeciesContextSpecParameter)ste).getSpeciesContext().getName();
			}
			targetXpath = new XPathTarget(sbmlSupport.getXPathForSpecies(name));
		} else if (ste instanceof ModelParameter) {
			targetXpath = new XPathTarget(sbmlSupport.getXPathForGlobalParameter(ste.getName()));
		}  else if (ste instanceof Structure || ste instanceof Structure.StructureSize || (ste instanceof StructureMappingParameter && ((StructureMappingParameter)ste).getRole() == StructureMapping.ROLE_Size)) {
			String compartmentId = ste.getName();
			// can change compartment size or spatial dimension, but in vcell, we cannot change compartment dimension. 
			String compartmentAttr = "";
			if (ste instanceof Structure.StructureSize) { 
				compartmentId = ((StructureSize)ste).getStructure().getName();
				compartmentAttr = ((StructureSize)ste).getName();
			}
			if (ste instanceof StructureMappingParameter) {
				StructureMappingParameter smp = (StructureMappingParameter)ste;
				compartmentId = smp.getStructure().getName();
				if (smp.getRole() == StructureMapping.ROLE_Size) {
					compartmentAttr = smp.getName();
				}
			}
			if (compartmentAttr.length() < 1) {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForCompartment(compartmentId));
			} else if (compartmentAttr.equalsIgnoreCase("size")) {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForCompartment(compartmentId, CompartmentAttribute.size));
			} else {
				throw new RuntimeException("Unknown compartment attribute '" + compartmentAttr + "'; cannot get xpath target for compartment '" + compartmentId + "'.");
			}
		} else if (ste instanceof KineticsParameter) {
			KineticsParameter kp = (KineticsParameter)ste;
			String reactionID = kp.getKinetics().getReactionStep().getName();
			String parameterID = kp.getName();
			Pair<String, String> key = new Pair(reactionID, parameterID);
			String value = l2gMap.get(key);
			if(value == null) {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForKineticLawParameter(reactionID, parameterID));
			} else {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForGlobalParameter(value, ParameterAttribute.value));
			}
		} else {
			if(ste instanceof Membrane.MembraneVoltage) {
				String msg = "Export failed: This VCell model has membrane voltage; cannot be exported to SBML at this time";
				throw new RuntimeException(msg);
			} else {
				throw new RuntimeException("Unsupported entity in SBML model export: "+ste.getClass());
			}
		}
		return targetXpath;
	}
	
	private XPathTarget getTargetAttributeXPath(SymbolTableEntry ste, Map<Pair <String, String>, String> l2gMap) {
		SBMLSupport sbmlSupport = new SBMLSupport();		// to get Xpath string for variables.
		XPathTarget targetXpath = null;
		if (ste instanceof SpeciesContext || ste instanceof SpeciesContextSpecParameter) {
			String speciesId = ste.getName();
			// can change species initial concentration or amount 
			String speciesAttr = "";
			if (ste instanceof SpeciesContextSpecParameter) {
				SpeciesContextSpecParameter scsp = (SpeciesContextSpecParameter)ste;
				speciesId = (scsp).getSpeciesContext().getName();
				int role = scsp.getRole();
				if (role == SpeciesContextSpec.ROLE_InitialConcentration) {
					speciesAttr = scsp.getName(); 
				}
				if (role == SpeciesContextSpec.ROLE_InitialCount) {
					speciesAttr = scsp.getName(); 
				}
				if(role == SpeciesContextSpec.ROLE_DiffusionRate) {
					speciesAttr = scsp.getName(); 
				}
			}
			if (speciesAttr.length() < 1) {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForCompartment(speciesId));
			} else if (speciesAttr.equalsIgnoreCase("initialConcentration") || speciesAttr.equalsIgnoreCase("initConc")) {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForSpecies(speciesId, SpeciesAttribute.initialConcentration));
			} else if (speciesAttr.equalsIgnoreCase("initialCount") || speciesAttr.equalsIgnoreCase("initCount")) {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForSpecies(speciesId, SpeciesAttribute.initialAmount));
			} else if (speciesAttr.equalsIgnoreCase("diff")) {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForGlobalParameter(speciesId + "_" + speciesAttr, ParameterAttribute.value));
			} else {
				throw new RuntimeException("Unknown species attribute '" + speciesAttr + "'; cannot get xpath target for species '" + speciesId + "'.");
			}

//			targetXpath = new XPathTarget(sbmlSupport.getXPathForSpecies(speciesId));
		} else if (ste instanceof ModelParameter) {
			// can only change parameter value. 
			targetXpath = new XPathTarget(sbmlSupport.getXPathForGlobalParameter(ste.getName(), ParameterAttribute.value));
		// TODO: add xpath for VolulePerUnitVolume and AreaPerUnitArea, see SBMLSupport
			// use Ion's sample 3, with spatial app
		}  else if (ste instanceof Structure || ste instanceof Structure.StructureSize || ste instanceof StructureMappingParameter) {
			String compartmentId = ste.getName();
			// can change compartment size or spatial dimension, but in vcell, we cannot change compartment dimension. 
			String compartmentAttr = "";
			if (ste instanceof Structure.StructureSize) { 
				compartmentId = ((StructureSize)ste).getStructure().getName();
				compartmentAttr = ((StructureSize)ste).getName();
			}
			if (ste instanceof StructureMappingParameter) {
				StructureMappingParameter smp = (StructureMappingParameter)ste;
				compartmentId = smp.getStructure().getName();
				int role = ((StructureMappingParameter)ste).getRole();
				if (role == StructureMapping.ROLE_Size) {
					compartmentAttr = smp.getName();
				} else if(role == StructureMapping.ROLE_AreaPerUnitArea || role == StructureMapping.ROLE_VolumePerUnitVolume) {
					compartmentAttr = smp.getName();
				}
			}
			if (compartmentAttr.length() < 1) {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForCompartment(compartmentId));
			} else if (compartmentAttr.equalsIgnoreCase("size")) {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForCompartment(compartmentId, CompartmentAttribute.size));
			} else if(compartmentAttr.equalsIgnoreCase("AreaPerUnitArea") || compartmentAttr.equalsIgnoreCase("VolPerUnitVol")) {
				targetXpath = new XPathTarget(sbmlSupport.getXPathForCompartmentMapping(compartmentId, CompartmentAttribute.unitSize));
			} else {
				throw new RuntimeException("Unknown compartment attribute '" + compartmentAttr + "'; cannot get xpath target for compartment '" + compartmentId + "'.");
			}
		} else if (ste instanceof KineticsParameter) {
			KineticsParameter kp = (KineticsParameter)ste;
			String reactionID = kp.getKinetics().getReactionStep().getName();
			String parameterID = kp.getName();
			Pair<String, String> key = new Pair(reactionID, parameterID);
			String value = l2gMap.get(key);
			if(value == null) {
				// stays as local parameter
				targetXpath = new XPathTarget(sbmlSupport.getXPathForKineticLawParameter(reactionID, parameterID, ParameterAttribute.value));
			} else {
				// became a global in SBML, we need to refer to that global
				targetXpath = new XPathTarget(sbmlSupport.getXPathForGlobalParameter(value, ParameterAttribute.value));
			}
		} else {
			if(ste instanceof Membrane.MembraneVoltage) {
				String msg = "Export failed: This VCell model has membrane voltage; cannot be exported to SBML at this time";
				logger.error("redundant error log: "+msg);
				throw new RuntimeException(msg);
			} else {
				logger.error("redundant error log: "+"Entity should be SpeciesContext, Structure, ModelParameter : " + ste.getClass());
				throw new RuntimeException("Unsupported entity in SBML model export: "+ste.getClass());
			}
		}
		return targetXpath;
	}
	
//	private String getKiSAOIdFromSimulation(SolverDescription solverDesc) {
//        String kiSAOId = null;
//		try {
//			// Create KiSAOQueryMaker instance, which uses last version of kisao.owl ontology
//			// (URL: http://biomodels.net/kisao/KISAO).
//			net.biomodels.kisao.IKiSAOQueryMaker kisaoQuery = new net.biomodels.kisao.impl.KiSAOQueryMaker();
//			String solverName = solverDesc.getShortDisplayLabel();
//			String cvodeSolverName = SolverDescription.CVODE.getShortDisplayLabel();
//			if (!solverName.equalsIgnoreCase(cvodeSolverName)) {
//				Set<IRI> iriSet = kisaoQuery.searchByName(solverName);
//				while (kiSAOId == null && iriSet != null && iriSet.iterator().hasNext()) {
//		        	IRI iri = iriSet.iterator().next();
//					kiSAOId = kisaoQuery.getId(iri);
//		        	String kiSAOName = kisaoQuery.getName(iri);
//		        	// System.out.printf("solver name : " + kiSAOName + " (" + kiSAOId + ")\n");
//				}
//			}
//			// At this point, either the solver is CVODE or kiSAO id could not be found for non-CVODE solver
//			// for now : if a match for solver name is not found in KiSAO, set it to CVODE
//			if (kiSAOId == null) {
//		        IRI iri = kisaoQuery.searchByName(cvodeSolverName).iterator().next();
//				kiSAOId = kisaoQuery.getId(iri);
//	        	String kiSAOName = kisaoQuery.getName(iri);
//				// System.out.printf("solver name : " + kiSAOName + " (" + kiSAOId + ")\n");
//			}
//		} catch (OWLOntologyCreationException e) {
//			e.printStackTrace(System.err);
//		}
//		return kiSAOId;
//	}

	public static ASTNode getFormulaFromExpression(Expression expression) { 
		// Convert expression into MathML string
		String expMathMLStr = null;

		try {
			expMathMLStr = cbit.vcell.parser.ExpressionMathMLPrinter.getMathML(expression, false);
		} catch (java.io.IOException e) {
			throw new RuntimeException("Error converting expression to MathML string :" + e.getMessage(), e);
		} catch (cbit.vcell.parser.ExpressionException e1) {
			throw new RuntimeException("Error converting expression to MathML string :" + e1.getMessage(), e1);
		}

		// Use libSBMl routines to convert MathML string to MathML document and a libSBML-readable formula string
		MathMLReader mmlr = new MathMLReader();
		ASTNode mathNode = null;
		try {
			mathNode = mmlr.parseMathMLFromString(expMathMLStr);
		} catch (IOException e) {
			throw new RuntimeException("Error converting MathML string to ASTNode:" + e.getMessage(), e);
		}
		return mathNode;
//		return mathNode.deepCopy();
	}
	
	public void createManifest(String savePath, String fileName) {
		
		final String xmlnsAttribute = "http://identifiers.org/combine.specifications/omex-manifest";	 
		final String manifestFormat = "http://identifiers.org/combine.specifications/omex-manifest";
		final String sbmlFormat = "http://identifiers.org/combine.specifications/sbml";	 
		final String sedmlFormat = "http://identifiers.org/combine.specifications/sed-ml";	 
			
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("omexManifest");
			doc.appendChild(rootElement);
			rootElement.setAttribute("xmlns", xmlnsAttribute);
			
			// Content elements
			Element content = doc.createElement("content");
			rootElement.appendChild(content);
			content.setAttribute("location", "manifest.xml");
			content.setAttribute("format", manifestFormat);
			
			for (String s : sbmlFilePathStrAbsoluteList) {
				content = doc.createElement("content");
				rootElement.appendChild(content);
				content.setAttribute("location", s);
				content.setAttribute("format", sbmlFormat);
			}
			
			content = doc.createElement("content");
			rootElement.appendChild(content);
			content.setAttribute("location", fileName + ".sedml");
			content.setAttribute("format", sedmlFormat);
			content.setAttribute("master", "true");


//			Element staff = doc.createElement("staff");
//			rootElement.appendChild(staff);
//			// set attribute to staff element
//			Attr attr = doc.createAttribute("id");
//			attr.setValue("1");
//			staff.setAttributeNode(attr);
//
//			// firstname elements
//			Element firstname = doc.createElement("firstname");
//			firstname.appendChild(doc.createTextNode("yong"));
//			staff.appendChild(firstname);
//
//			// salary elements
//			Element salary = doc.createElement("salary");
//			salary.appendChild(doc.createTextNode("100000"));
//			staff.appendChild(salary);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			String manifestAbsolutePathName = Paths.get(savePath, "manifest.xml").toString();
			StreamResult result = new StreamResult(new File(manifestAbsolutePathName));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		  } catch (ParserConfigurationException pce) {
			logger.error(pce);
		  } catch (TransformerException tfe) {
			logger.error(tfe);
		  }
	}
	public void addSedmlFileToList(String sedmlFileName) {
		if(sedmlFileName != null && !sedmlFileName.isEmpty()) {
			sedmlFilePathStrAbsoluteList.add(sedmlFileName);
		}
	}
	

    public boolean createOmexArchive(String srcFolder, String sFileName) {
    try {
		//System.loadLibrary("combinej");
		CombineArchive archive = new CombineArchive();

    	
		for (String sd : sedmlFilePathStrAbsoluteList) {
			String s = Paths.get(srcFolder, sd).toString();
			archive.addFile(
					s,
					"./" + sd, // target file name
					KnownFormats.lookupFormat("sedml"),
					true // mark file as master
			);
    	}
		for (String sd : sbmlFilePathStrAbsoluteList) {
			archive.addFile(
					Paths.get(srcFolder, sd).toString(),
					"./" + sd, // target file name
					KnownFormats.lookupFormat("sbml"),
					false // mark file as master
			);
    	}

		archive.addFile(
				Paths.get(srcFolder, sFileName + ".vcml").toString(),
				"./" + sFileName + ".vcml",
				"http://purl.org/NET/mediatypes/application/vcml+xml",
				false
		);
		
        String[] files;
        File dir = new File(srcFolder);
        files = dir.list();
        for(String sd : files) {
        	if (sd.endsWith(".rdf")) {
        		archive.addFile(	
        				Paths.get(srcFolder, sd).toString(),
        				"./" + sd,
        				"http://identifiers.org/combine.specifications/omex-metadata",
        				false
        		);
        	}
        }

		archive.writeToFile(Paths.get(srcFolder, sFileName + ".omex").toString());

		// Removing files after archiving
		for (String sd : sbmlFilePathStrAbsoluteList) {
			Paths.get(srcFolder, sd).toFile().delete();
		}
		for (String sd : sedmlFilePathStrAbsoluteList) {
			Paths.get(srcFolder, sd).toFile().delete();
		}
		Paths.get(srcFolder, sFileName + ".vcml").toFile().delete();
        for(String sd : files) {
        	if (sd.endsWith(".rdf")) {
        		Paths.get(srcFolder, sd).toFile().delete();
        	}
        }

    } catch (Exception e) {
    	throw new RuntimeException("createZipArchive threw exception: " + e.getMessage());        
    }
    return true;
}

	// we know exactly which files we need to archive: those in sbmlFilePathStrAbsoluteList
	// each file is deleted after being added to archive
	private final int BUFFER = 2048;
	public boolean createZipArchive(String srcFolder, String sFileName) {
		try {
			BufferedInputStream origin = null;

			FileOutputStream    dest = new FileOutputStream(Paths.get(srcFolder, sFileName + ".sedx").toFile());
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[BUFFER];

			for (String sd : sbmlFilePathStrAbsoluteList) {
//				if(sd.startsWith(FileUtils.UNIX_CURRENT_FOLDER_SEPARATOR)) {
//					int pos = sd.indexOf(FileUtils.UNIX_CURRENT_FOLDER_SEPARATOR) + FileUtils.UNIX_CURRENT_FOLDER_SEPARATOR.length();
//					sd = sd.substring(pos);
//				}

				File f = Paths.get(srcFolder, sd).toFile();
				FileInputStream fi = new FileInputStream(f);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(sd);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
					out.flush();
				}
				fi.close();
				f.delete();
			}
			origin.close();
			out.flush();
			out.close();
		} catch (Exception e) {
			throw new RuntimeException("createZipArchive threw exception: " + e.getMessage());
		}
		return true;
	}

	private void dummy() {
//		// ------ Functional Range
//		r = new FunctionalRange(rangeId, "-1");
//		ASTNode fRangeMath = SEDMLExporter.getFormulaFromExpression(expression);
//		if(fRangeMath != null) {
//			Function func = new Function(fRangeMath);
//			((FunctionalRange) r).setMath(func);
//			
//			String[] symbols = expression.getSymbols();
//			for(String symbolName : symbols) {
//				//String symbolName = TokenMangler.mangleToSName(symbol);
//				
//				mathVar = mathSymbolMapping.findVariableByName(symbolName);
//				stEntries = mathSymbolMapping.getBiologicalSymbol(mathVar);
//				if(stEntries.length > 1) {
//					System.err.println("SymbolTableEntry size: " + stEntries.length + ", " + stEntries.toString());
//				} else if(stEntries.length == 0) {
//					System.err.println("SymbolTableEntry size: " + stEntries.length + " for " + symbolName);
//				}
//				SymbolTableEntry ste1 = stEntries[0];
//				//SymbolTableEntry ste1 = vcModel.getEntry(symbol);
//				if (ste1 instanceof SpeciesContext || ste1 instanceof Structure || ste1 instanceof ReservedSymbol) {
//					Variable sedmlVar = new Variable(symbolName, symbolName, taskRef, sbmlSupport.getXPathForSpecies(symbolName));
//					((FunctionalRange) r).addVariable(sedmlVar);
//				} else if (ste1 instanceof ModelParameter) {
//					Parameter sedmlParameter = new Parameter(symbolName, symbolName, ((ModelParameter)ste1).getConstantValue());
//					((FunctionalRange) r).addVariable(sedmlParameter);
//				} else if (ste1 instanceof KineticsParameter) {
//					Parameter sedmlParameter = new Parameter(symbolName, symbolName, ((KineticsParameter)ste1).getConstantValue());
//					((FunctionalRange) r).addVariable(sedmlParameter);
//				} else {
//					System.err.println("Entity should be SpeciesContext, ReservedSymbol, Structure, ModelParameter or KineticParameter: " + ste1.getClass());
//				}
//			}
//		}
//		t.addRange(r);

	}

}


