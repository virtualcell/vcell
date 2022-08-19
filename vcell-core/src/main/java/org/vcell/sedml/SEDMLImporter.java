
package org.vcell.sedml;

import java.beans.PropertyVetoException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cbit.vcell.parser.ExpressionMathMLParser;
import cbit.vcell.parser.LambdaFunction;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.legacy.core.CategoryUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jlibsedml.AbstractTask;
import org.jlibsedml.Algorithm;
import org.jlibsedml.AlgorithmParameter;
import org.jlibsedml.ArchiveComponents;
import org.jlibsedml.Change;
import org.jlibsedml.ChangeAttribute;
import org.jlibsedml.ComputeChange;
import org.jlibsedml.DataGenerator;
import org.jlibsedml.Libsedml;
import org.jlibsedml.Model;
import org.jlibsedml.Output;
import org.jlibsedml.Range;
import org.jlibsedml.RepeatedTask;
import org.jlibsedml.SedML;
import org.jlibsedml.SetValue;
import org.jlibsedml.SubTask;
import org.jlibsedml.Task;
import org.jlibsedml.UniformRange;
import org.jlibsedml.UniformRange.UniformType;
import org.jlibsedml.UniformTimeCourse;
import org.jlibsedml.VectorRange;
import org.jlibsedml.XMLException;
import org.jlibsedml.XPathTarget;
import org.jlibsedml.execution.ArchiveModelResolver;
import org.jlibsedml.execution.FileModelResolver;
import org.jlibsedml.execution.ModelResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.jlibsedml.modelsupport.SUPPORTED_LANGUAGE;
import org.jmathml.ASTNode;
import org.jmathml.ASTNumber;
import org.jmathml.ASTToXMLElementVisitor;
import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.math.compiler.LibSBMLFormulaCompiler;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.FileUtils;
import org.vcell.util.document.VCDocument;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.Constant;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ErrorTolerance.ErrorToleranceDescription;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.NonspatialStochHybridOptions;
import cbit.vcell.solver.NonspatialStochSimOptions;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverDescription.AlgorithmParameterDescription;
import cbit.vcell.solver.SolverDescription.SolverFeature;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.TimeStep.TimeStepDescription;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class SEDMLImporter {

	private final static Logger logger = LogManager.getLogger(SEDMLImporter.class);
	
	private SedML sedml;
	private ExternalDocInfo externalDocInfo;
	private boolean exactMatchOnly;
	
	private VCLogger transLogger;
	private List<BioModel> docs;
	private String bioModelBaseName;
	private ArchiveComponents ac;
	private ModelResolver resolver;
	private SBMLSupport sbmlSupport;
	
	private HashMap<BioModel, SBMLImporter> importMap = new HashMap<BioModel, SBMLImporter>();

	
	public  SEDMLImporter(VCLogger transLogger, ExternalDocInfo externalDocInfo, SedML sedml, boolean exactMatchOnly) throws FileNotFoundException, XMLException {
		this.transLogger = transLogger;
		this.externalDocInfo = externalDocInfo;
		this.sedml = sedml;
		this.exactMatchOnly = exactMatchOnly;
		
		initialize();
	}
	
	private void initialize() throws FileNotFoundException, XMLException {
		bioModelBaseName = FileUtils.getBaseName(externalDocInfo.getFile().getAbsolutePath());		// extract bioModel name from sedx (or sedml) file
		if(externalDocInfo.getFile().getPath().toLowerCase().endsWith("sedx") || externalDocInfo.getFile().getPath().toLowerCase().endsWith("omex")) {
			ac = Libsedml.readSEDMLArchive(new FileInputStream(externalDocInfo.getFile().getPath()));
		}
		resolver = new ModelResolver(sedml);
		if(ac != null) {
			resolver.add(new ArchiveModelResolver(ac));
		} else {
			resolver.add(new FileModelResolver()); // assumes absolute paths
			String sedmlRelativePrefix = externalDocInfo.getFile().getParent() + File.separator;
			resolver.add(new RelativeFileModelResolver(sedmlRelativePrefix)); // in case model URIs are relative paths
		}
		sbmlSupport = new SBMLSupport();
	}

	public  List<BioModel> getBioModels() throws Exception {
		docs = new ArrayList<BioModel>();
		try {
	        // iterate through all the elements and show them at the console
	        List<org.jlibsedml.Model> mmm = sedml.getModels();
	        if (mmm.isEmpty()) {
	        	// nothing to import
	        	return docs;
	        }
	        List<org.jlibsedml.Simulation> sss = sedml.getSimulations();
	        List<AbstractTask> ttt = sedml.getTasks();
	        List<DataGenerator> ddd = sedml.getDataGenerators();
	        List<Output> ooo = sedml.getOutputs();
	        printSEDMLSummary(mmm, sss, ttt, ddd, ooo);
	        
			// We don't know how many BioModels we'll end up with as some model changes may be translatable as simulations with overrides
	        // The HashMap below has entries for all SEDML Models where some may reference the same BioModel
	        // The docs field will have a List of all unique BioModels
			HashMap<String, BioModel> bmMap = new HashMap<String, BioModel>();
			createBioModels(mmm, bmMap);
 	        
			// We will parse all tasks and create Simulations in BioModels
			// Creating one VCell Simulation for each SED-ML actual Task (RepeatedTasks get added as parameter scan overrides)
	    	org.jlibsedml.Simulation sedmlSimulation = null;	// this will become the vCell simulation
	    	org.jlibsedml.Model sedmlModel = null;		// the "original" model referred to by the task
	    	String sedmlOriginalModelName = null;				// this will be used in the BioModel name
	    	String sedmlOriginalModelLanguage = null;			// can be sbml or vcml
			
			HashMap<String, Simulation> vcSimulations = new HashMap<String, Simulation>();
			for (AbstractTask selectedTask : ttt) {
				if(selectedTask instanceof Task) {
					sedmlModel = sedml.getModelWithId(selectedTask.getModelReference());
					sedmlSimulation = sedml.getSimulation(selectedTask.getSimulationReference());
					sedmlOriginalModelLanguage = sedmlModel.getLanguage();
				} else if(selectedTask instanceof RepeatedTask) {
					// Repeated tasks refer to regular tasks
					// We need simulations to be created for all regular tasks before we can process repeated tasks
					continue;
				} else {
					throw new RuntimeException("Unexpected task " + selectedTask);
				}
				// only UTC sims supported
				if(!(sedmlSimulation instanceof UniformTimeCourse)) {
					logger.error("task '" + selectedTask.getName() + "' is being skipped, it references an unsupported simulation type: "+sedmlSimulation);
					continue;
				}

				// at this point we assume that the sedml simulation, algorithm and kisaoID are all valid

				// identify the vCell solvers that would match best the sedml solver kisao id
				String kisaoID = sedmlSimulation.getAlgorithm().getKisaoID();
				// try to find a match in the ontology tree
				SolverDescription solverDescription = SolverUtilities.matchSolverWithKisaoId(kisaoID, exactMatchOnly);
				if (solverDescription != null) {
					logger.info("Task (id='"+selectedTask.getId()+"') is compatible, solver match found in ontology: '" + kisaoID + "' matched to " + solverDescription);
				} else {
					// give it a try anyway with our deterministic default solver
					solverDescription = SolverDescription.CombinedSundials;
					logger.error("Task (id='"+selectedTask.getId()+")' is not compatible, no equivalent solver found in ontology for requested algorithm '"+kisaoID + "'; trying with deterministic default solver "+solverDescription);
				}
				// find out everything else we need about the application we're going to use,
				// some of the info will be needed when we parse the sbml file
				boolean bSpatial = false;
				Application appType = Application.NETWORK_DETERMINISTIC;
				Set<SolverDescription.SolverFeature> sfList = solverDescription.getSupportedFeatures();
				for(SolverDescription.SolverFeature sf : sfList) {
					switch(sf) {
						case Feature_Rulebased:
							appType = Application.RULE_BASED_STOCHASTIC;
							break;
						case Feature_Stochastic:
							appType = Application.NETWORK_STOCHASTIC;
							break;
						case Feature_Deterministic:
							appType = Application.NETWORK_DETERMINISTIC;
							break;
						case Feature_Spatial:
							bSpatial = true;
							break;
						default:
							break;
					}
				}

				BioModel bioModel = bmMap.get(sedmlModel.getId());
				
				// if language is VCML, we don't need to create Applications and Simulations in BioModel
				// we allow a subset of SED-ML Simulation settings (may have been edited) to override existing BioModel Simulation settings
				
				if(sedmlOriginalModelLanguage.contentEquals(SUPPORTED_LANGUAGE.VCELL_GENERIC.getURN())) {
					Simulation theSimulation = null;
					for (Simulation sim : bioModel.getSimulations()) {
						if (sim.getName().equals(selectedTask.getName())) {
							logger.trace(" --- selected task - name: " + selectedTask.getName() + ", id: " + selectedTask.getId());
							sim.setImportedTaskID(selectedTask.getId());
							theSimulation = sim;
							break;	// found the one, no point to continue the for loop
						}
					}if(theSimulation == null) {
						logger.error("Couldn't match sedml task '" + selectedTask.getName() + "' with any biomodel simulation");
						// TODO: should we throw an exception?
						continue;	// should never happen
					}
					
					SolverTaskDescription simTaskDesc = theSimulation.getSolverTaskDescription();
					translateTimeBounds(simTaskDesc, sedmlSimulation);
					continue;
				}
				
				// if language is SBML, we must create Simulations
				// we may need to also create Applications, since the default one from SBML import may not be the right type)
				
				// see first if there is a suitable application type for the specified kisao
				// if not, we add one by doing a "copy as" to the right type
				SimulationContext[] existingSimulationContexts = bioModel.getSimulationContexts();
				SimulationContext matchingSimulationContext = null;
				for (SimulationContext simContext : existingSimulationContexts) {
					if (simContext.getApplicationType().equals(appType) && ((simContext.getGeometry().getDimension() > 0) == bSpatial)) {
						matchingSimulationContext = simContext;
						break;
					}
				}
				if (matchingSimulationContext == null) {
					matchingSimulationContext = SimulationContext.copySimulationContext(existingSimulationContexts[0], sedmlOriginalModelName+"_"+existingSimulationContexts.length, bSpatial, appType);
					bioModel.addSimulationContext(matchingSimulationContext);
				}
				matchingSimulationContext.refreshDependencies();
				MathMappingCallback callback = new MathMappingCallbackTaskAdapter(null);
				matchingSimulationContext.refreshMathDescription(callback, NetworkGenerationRequirements.ComputeFullStandardTimeout);

				// making the new vCell simulation based on the sedml simulation
				Simulation newSimulation = new Simulation(matchingSimulationContext.getMathDescription());
				newSimulation.setSimulationOwner(matchingSimulationContext);
				if (selectedTask instanceof Task) {
					String newSimName = selectedTask.getId();
					if(SEDMLUtil.getName(selectedTask) != null) {
						newSimName += "_" + SEDMLUtil.getName(selectedTask);
					}
					newSimulation.setName(newSimName);
					newSimulation.setImportedTaskID(selectedTask.getId());
					vcSimulations.put(selectedTask.getId(), newSimulation);
				} else {
					newSimulation.setName(SEDMLUtil.getName(sedmlSimulation)+"_"+SEDMLUtil.getName(selectedTask));
				}
				
				// we identify the type of sedml simulation (uniform time course, etc)
				// and set the vCell simulation parameters accordingly
				SolverTaskDescription simTaskDesc = newSimulation.getSolverTaskDescription();
				if(solverDescription != null) {
					simTaskDesc.setSolverDescription(solverDescription);
				}

				translateTimeBounds(simTaskDesc, sedmlSimulation);
				translateAlgorithmParams(simTaskDesc, sedmlSimulation);
				
				newSimulation.setSolverTaskDescription(simTaskDesc);
				newSimulation.setDescription(SEDMLUtil.getName(selectedTask));
				bioModel.addSimulation(newSimulation);
				newSimulation.refreshDependencies();
				
				// finally, add MathOverrides if referenced model has specified compatible changes
				if (!sedmlModel.getListOfChanges().isEmpty() && canTranslateToOverrides(bioModel, sedmlModel)) {
					createOverrides(newSimulation, sedmlModel);
				}
				
			}
			// now process repeated tasks, if any
			addRepeatedTasks(ttt, vcSimulations);
			return docs;
		} catch (Exception e) {
			throw new RuntimeException("Unable to initialize bioModel for the given selection\n"+e.getMessage(), e);
		}
	}

	private void createOverrides(Simulation newSimulation, Model sedmlModel) throws ExpressionException, IOException {
		// TODO Auto-generated method stub
		List<Change> changes = sedmlModel.getListOfChanges();
		for (Change change : changes) {
			String targetID = sbmlSupport.getIdFromXPathIdentifer(change.getTargetXPath().getTargetAsString());
			String vcConstantName = resolveConstant(((SimulationContext)newSimulation.getSimulationOwner()).getBioModel(), targetID);
			Expression exp = null;
			if (change.isChangeAttribute()) {
				exp = new Expression(((ChangeAttribute)change).getNewValue());
			} else if (change.isComputeChange()) {
				ComputeChange cc = (ComputeChange)change;
				ASTNode math = cc.getMath();
				exp = new ExpressionMathMLParser(null).fromMathML(math, "t");
				// TODO SEDML-declared variables with mangled unique IDs will need to be substituted
				// then get final expression
			} else {
				logger.error("unsupported change encountered, overrides not applied");
				return;
			}
			Constant constant = new Constant(vcConstantName,exp);
			newSimulation.getMathOverrides().putConstant(constant);
		}
	}

	private String resolveConstant(BioModel bioModel, String SBMLtargetID) {
		// TODO Auto-generated method stub
		return SBMLtargetID; //placeholder
	}

	private void addRepeatedTasks(List<AbstractTask> ttt, HashMap<String, Simulation> vcSimulations)
			throws ExpressionException {
		for (AbstractTask selectedTask : ttt) {
			if (selectedTask instanceof RepeatedTask) {
				RepeatedTask rt = (RepeatedTask)selectedTask;
				if (!rt.getResetModel() || rt.getSubTasks().size() != 1) {
					logger.error("sequential RepeatedTask not yet supported, task "+SEDMLUtil.getName(selectedTask)+" is being skipped");
					continue;
				}
				if (rt.getChanges().size() != 1) {
					logger.error("lockstep multiple parameter scans are not yet supported, task "+SEDMLUtil.getName(selectedTask)+" is being skipped");
					continue;
				}
				AbstractTask referredTask;
				// find the actual Task, which can be directly referred or indirectly through a chain of RepeatedTasks (in case of multiple parameter scans)
				do {
					SubTask st = rt.getSubTasks().entrySet().iterator().next().getValue(); // single subtask
					String taskId = st.getTaskId();
					referredTask = sedml.getTaskWithId(taskId);
					if (referredTask instanceof RepeatedTask) rt = (RepeatedTask)referredTask;
				} while (referredTask instanceof RepeatedTask);
				Task actualTask = (Task)referredTask;
				Simulation simulation = vcSimulations.get(actualTask.getId());
				// now add a parameter scan to the simulation referred by the actual task
				ConstantArraySpec scanSpec = null;
				rt = (RepeatedTask)selectedTask;
				SetValue change = rt.getChanges().get(0); // single param scan
				String targetID = sbmlSupport.getIdFromXPathIdentifer(change.getTargetXPath().getTargetAsString());
				Range range = rt.getRange(change.getRangeReference());
				// TODO start
				// need to map the SBML target to VCell constant name
				String constant = targetID; // placeholder
				// TODO end
				if (range instanceof UniformRange) {
					UniformRange ur = (UniformRange)range;
					scanSpec = ConstantArraySpec.createIntervalSpec(constant, Math.min(ur.getStart(), ur.getEnd()), Math.max(ur.getStart(), ur.getEnd()), ur.getNumberOfPoints(), ur.getType().equals(UniformType.LOG));
				} else if (range instanceof VectorRange) {
					VectorRange vr = (VectorRange)range;
					String[] values = new String[vr.getNumElements()];
					for (int i = 0; i < values.length; i++) {
						values[i] = Double.toString(vr.getElementAt(i));
					}
					scanSpec = ConstantArraySpec.createListSpec(constant, values);
				} else {
					logger.error("unsupported Range class found, task "+SEDMLUtil.getName(selectedTask)+" is being skipped");
					continue;						
				}
				MathOverrides mo = simulation.getMathOverrides();
				mo.putConstantArraySpec(scanSpec);
			}
		}
	}

	private void createBioModels(List<org.jlibsedml.Model> mmm, HashMap<String, BioModel> bmMap) {
		// first go through models without changes which are unique and must be imported as new BioModel/SimContext
		for(Model mm : mmm) {
			if (mm.getListOfChanges().isEmpty()) {
				String id = mm.getId();
				bmMap.put(id, importModel(mm));
			}
		}
		// now go through models with changes and see if they refer to another model within this sedml
		for(Model mm : mmm) {
			if (!mm.getListOfChanges().isEmpty()) {
				String refID = null;
				if (mm.getSource().startsWith("#")) {
					refID = mm.getSource().substring(1);
					// direct reference within sedml
				} else {
					// need to check if it is an indirect reference (another model using same source URI)
					for (Model model : sedml.getModels()) {
						if (model != mm && model.getSource().equals(mm.getSource())) {
							refID = model.getId();
						}
					}
				}
				if (refID != null) {
					BioModel refBM = bmMap.get(refID);
					if (refBM == null) {
						//TODO at some point we need to check for sequential application of changes and apply in chain
						throw new RuntimeException("Model " + mm
								+ " refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)");
					}
					boolean translateToOverrides = canTranslateToOverrides(refBM, mm);
					if (!translateToOverrides) {
						bmMap.put(mm.getId(), importModel(mm));
					} else {
						bmMap.put(mm.getId(), refBM);
					} 
				} else {
					bmMap.put(mm.getId(), importModel(mm));
				}
			}
		}
	}

	private boolean canTranslateToOverrides(BioModel refBM, Model mm) {
		// TODO Actually check if true
		return true; //should work for all VCell-exported SED-MLs
	}

	private BioModel importModel(Model mm) {
		BioModel bioModel = null;
		String sedmlOriginalModelName = mm.getId();
		String sedmlOriginalModelLanguage = mm.getLanguage();
		String modelXML = resolver.getModelString(mm); // source with all the changes applied
		String bioModelName = bioModelBaseName + "_" + sedml.getFileName() + "_" + sedmlOriginalModelName;
		try {
			if(sedmlOriginalModelLanguage.contentEquals(SUPPORTED_LANGUAGE.VCELL_GENERIC.getURN())) {	// vcml
				XMLSource vcmlSource = new XMLSource(modelXML);
				bioModel = (BioModel)XmlHelper.XMLToBioModel(vcmlSource);
				bioModel.setName(bioModelName);
				try {
					bioModel.getVCMetaData().createBioPaxObjects(bioModel);
				} catch (Exception e) {
					logger.error("failed to make BioPax objects", e);
				}
				docs.add(bioModel);
			} else {				// we assume it's sbml, if it's neither import will fail
				InputStream sbmlSource = IOUtils.toInputStream(modelXML);
				VCDocument vcDoc = null;
				boolean bValidateSBML = false;
				SBMLImporter sbmlImporter = new SBMLImporter(sbmlSource,transLogger,bValidateSBML);
				bioModel = (BioModel)sbmlImporter.getBioModel();
				bioModel.refreshDependencies();			
				bioModel.setName(bioModelName);
				docs.add(bioModel);
				importMap.put(bioModel, sbmlImporter);
			}
		} catch (XmlParseException e) {
			// TODO Auto-generated catch block 
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return bioModel;
	}

	private void printSEDMLSummary(List<org.jlibsedml.Model> mmm, List<org.jlibsedml.Simulation> sss,
			List<AbstractTask> ttt, List<DataGenerator> ddd, List<Output> ooo) {
		for(Model mm : mmm) {
		    logger.trace("sedml model: "+mm.toString());
		    List<Change> listOfChanges = mm.getListOfChanges();
		    logger.debug("There are " + listOfChanges.size() + " changes in model "+mm.getId());
		}
		for(org.jlibsedml.Simulation ss : sss) {
		    logger.trace("sedml simulaton: "+ss.toString());
		}
		for(AbstractTask tt : ttt) {
		    logger.trace("sedml task: "+tt.toString());
		}
		for(DataGenerator dd : ddd) {
		    logger.trace("sedml dataGenerator: "+dd.toString());
		}
		for(Output oo : ooo) {
		    logger.trace("sedml output: "+oo.toString());
		}
	}

	private void translateTimeBounds(SolverTaskDescription simTaskDesc, org.jlibsedml.Simulation sedmlSimulation) throws PropertyVetoException {
		TimeBounds timeBounds = new TimeBounds();
		TimeStep timeStep = new TimeStep();
		double outputTimeStep = 0.1;
		int outputNumberOfPoints = 1;
		// we translate initial time to zero, we provide output for the duration of the simulation
		// because we can't select just an interval the way the SEDML simulation can
		double initialTime = ((UniformTimeCourse) sedmlSimulation).getInitialTime();
		double outputStartTime = ((UniformTimeCourse) sedmlSimulation).getOutputStartTime();
		double outputEndTime = ((UniformTimeCourse) sedmlSimulation).getOutputEndTime();
		outputNumberOfPoints = ((UniformTimeCourse) sedmlSimulation).getNumberOfPoints();
		outputTimeStep = (outputEndTime - outputStartTime) / outputNumberOfPoints;
		timeBounds = new TimeBounds(0, outputEndTime - initialTime);

		OutputTimeSpec outputTimeSpec = new UniformOutputTimeSpec(outputTimeStep);
		simTaskDesc.setTimeBounds(timeBounds);
		simTaskDesc.setTimeStep(timeStep);
		if (simTaskDesc.getSolverDescription().supports(outputTimeSpec)) {
			simTaskDesc.setOutputTimeSpec(outputTimeSpec);
		} else {
			simTaskDesc.setOutputTimeSpec(new DefaultOutputTimeSpec(1,Integer.max(DefaultOutputTimeSpec.DEFAULT_KEEP_AT_MOST, outputNumberOfPoints)));
		}

	}
	
	private void translateAlgorithmParams(SolverTaskDescription simTaskDesc, org.jlibsedml.Simulation sedmlSimulation) throws PropertyVetoException {
		TimeStep timeStep = simTaskDesc.getTimeStep();
		Algorithm algorithm = sedmlSimulation.getAlgorithm();
		String kisaoID = algorithm.getKisaoID();
		ErrorTolerance errorTolerance = new ErrorTolerance();
		List<AlgorithmParameter> sedmlAlgorithmParameters = algorithm.getListOfAlgorithmParameters();
		for(AlgorithmParameter sedmlAlgorithmParameter : sedmlAlgorithmParameters) {

			String apKisaoID = sedmlAlgorithmParameter.getKisaoID();
			String apValue = sedmlAlgorithmParameter.getValue();
			if(apKisaoID == null || apKisaoID.isEmpty()) {
				logger.error("Undefined KisaoID algorithm parameter for algorithm '" + kisaoID + "'");
			}

			// we don't check if the recognized algorithm parameters are valid for our algorithm
			// we just use any parameter we find for the solver task description we have, assuming the exporting code did all the checks
			// WARNING: if our algorithm is the result of a guess, this may be wrong
			// TODO: use the proper ontology for the algorithm parameters kisao id
			if(apKisaoID.contentEquals(ErrorTolerance.ErrorToleranceDescription.Absolute.getKisao())) {
				double value = Double.parseDouble(apValue);
				errorTolerance.setAbsoluteErrorTolerance(value);
			} else if(apKisaoID.contentEquals(ErrorTolerance.ErrorToleranceDescription.Relative.getKisao())) {
				double value = Double.parseDouble(apValue);
				errorTolerance.setRelativeErrorTolerance(value);
			} else if(apKisaoID.contentEquals(TimeStep.TimeStepDescription.Default.getKisao())) {
				double value = Double.parseDouble(apValue);
				timeStep.setDefaultTimeStep(value);
			} else if(apKisaoID.contentEquals(TimeStep.TimeStepDescription.Maximum.getKisao())) {
				double value = Double.parseDouble(apValue);
				timeStep.setMaximumTimeStep(value);
			} else if(apKisaoID.contentEquals(TimeStep.TimeStepDescription.Minimum.getKisao())) {
				double value = Double.parseDouble(apValue);
				timeStep.setMinimumTimeStep(value);
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.Seed.getKisao())) {		// custom seed
				if(simTaskDesc.getSimulation().getMathDescription().isNonSpatialStoch()) {
					NonspatialStochSimOptions nssso = simTaskDesc.getStochOpt();
					int value = Integer.parseInt(apValue);
					nssso.setCustomSeed(value);
				} else {
					logger.error("Algorithm parameter '" + AlgorithmParameterDescription.Seed.getDescription() +"' is only supported for nonspatial stochastic simulations");
				}
				// some arguments used only for non-spatial hybrid solvers
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.Epsilon.getKisao())) {
				NonspatialStochHybridOptions nssho = simTaskDesc.getStochHybridOpt();
				nssho.setEpsilon(Double.parseDouble(apValue));
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.Lambda.getKisao())) {
				NonspatialStochHybridOptions nssho = simTaskDesc.getStochHybridOpt();
				nssho.setLambda(Double.parseDouble(apValue));
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.MSRTolerance.getKisao())) {
				NonspatialStochHybridOptions nssho = simTaskDesc.getStochHybridOpt();
				nssho.setMSRTolerance(Double.parseDouble(apValue));
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.SDETolerance.getKisao())) {
				NonspatialStochHybridOptions nssho = simTaskDesc.getStochHybridOpt();
				nssho.setSDETolerance(Double.parseDouble(apValue));
			} else {
				logger.error("Algorithm parameter with kisao id '" + apKisaoID + "' not supported at this time, skipping.");
			}
		}
		simTaskDesc.setErrorTolerance(errorTolerance);
	}
}
