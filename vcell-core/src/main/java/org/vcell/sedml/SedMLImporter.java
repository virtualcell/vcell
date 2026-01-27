
package org.vcell.sedml;

import cbit.image.ImageException;
import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.mapping.*;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Variable;
//import cbit.vcell.model.*; Care, don't want namespace conflict with libsedml's Model class
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.ModelRelationVisitor;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLParser;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.*;
import cbit.vcell.solver.SolverDescription.AlgorithmParameterDescription;
import cbit.vcell.xml.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedML;
import org.jlibsedml.components.algorithm.Algorithm;
import org.jlibsedml.components.algorithm.AlgorithmParameter;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.model.Change;
import org.jlibsedml.components.model.ChangeAttribute;
import org.jlibsedml.components.model.ComputeChange;
import org.jlibsedml.components.model.Model;
import org.jlibsedml.components.Parameter;
import org.jlibsedml.*;
import org.jlibsedml.components.output.DataSet;
import org.jlibsedml.components.simulation.OneStep;
import org.jlibsedml.components.task.UniformRange.UniformType;
import org.jlibsedml.components.output.Output;
import org.jlibsedml.components.output.Report;
import org.jlibsedml.components.simulation.UniformTimeCourse;
import org.jlibsedml.components.task.*;
import org.jlibsedml.execution.ArchiveModelResolver;
import org.jlibsedml.execution.FileModelResolver;
import org.jlibsedml.execution.ModelResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.jlibsedml.modelsupport.SUPPORTED_LANGUAGE;
import org.jmathml.ASTNode;
import org.sbml.jsbml.SBase;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.sbml.vcell.SBMLSymbolMapping;
import org.vcell.sbml.vcell.SymbolContext;
import org.vcell.trace.Tracer;
import org.vcell.util.FileUtils;
import org.vcell.util.ISize;
import org.vcell.util.RelationVisitor;

import java.beans.PropertyVetoException;
import java.io.*;
import java.nio.charset.Charset;

import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Serves as a means to convert sedml documents into VCell BioModels
 */
public class SedMLImporter {
	private final static Logger logger = LogManager.getLogger(SedMLImporter.class);

	private final boolean disallowModifiedImport;
    private final boolean trySundialsAnyway;
	private final VCLogger transLogger;
    private final Map<String, SolverDescription> kisaoToSolverMapping;
    private final HashMap<BioModel, SBMLImporter> importMap;

    private SedMLDataContainer sedmlContainer;

	private String bioModelBaseName;
	private ArchiveComponents archiveComponents;
	private ModelResolver resolver;
	private SBMLSupport sbmlSupport;

	
	/**
	 * Builds the importer for future initialization
	 * 
	 * @param transLogger the VC logger to use
	 * @param disallowModifiedImport sets whether to import strictly as-is or with flexibility
	 */
	public SedMLImporter(VCLogger transLogger, boolean disallowModifiedImport) {
		this(transLogger, disallowModifiedImport, true);
	}

    /**
     * Builds the importer for future initialization
     *
     * @param transLogger the VC logger to use
     * @param disallowModifiedImport sets whether to import strictly as-is or with flexibility
     */
    public SedMLImporter(VCLogger transLogger, boolean disallowModifiedImport, boolean trySundialsAnyway) {
        this.transLogger = transLogger;
        this.disallowModifiedImport = disallowModifiedImport;
        this.trySundialsAnyway = trySundialsAnyway;
        this.kisaoToSolverMapping =  new HashMap<>();
        this.importMap = new HashMap<>();
        this.sedmlContainer = null;
    }

	/**
	 * Initialize the importer to process a specific set of SedML within a document or archive. The importer will attempt
     * to find any unsupported sedml during initialization, and in such case will fail except if the
     * <code>allowModifiedImport</code> flag is set to true
	 * @param fileWithSedmlToProcess the file containing SedML
	 * @param sedml the SedML to be processed
     *
	 * @throws IOException if the sedml archive can not be found, or the IO stream reading it failed
	 * @throws XMLException if the sedml has invalid xml.
     * @return the sedml data container to be used for this importer
	 */
	public SedMLDataContainer initialize(File fileWithSedmlToProcess, SedMLDataContainer sedml) throws XMLException, IOException {
		if (fileWithSedmlToProcess == null) throw new IllegalArgumentException("Source file of SedML can not be null!");
		if (sedml == null) throw new IllegalArgumentException("Provided SedML can not be null!");
        // Determine sedml to import off of.
		this.sedmlContainer = this.verifyOrModifyOrRejectSedml(sedml);
        // extract bioModel name from sedml (or sedml) file
		this.bioModelBaseName = FileUtils.getBaseName(fileWithSedmlToProcess.getAbsolutePath());
		if(fileWithSedmlToProcess.getPath().toLowerCase().endsWith("sedx")
				|| fileWithSedmlToProcess.getPath().toLowerCase().endsWith("omex")) {
			this.archiveComponents = Libsedml.readSEDMLArchive(Files.newInputStream(fileWithSedmlToProcess.toPath()));
		}
		this.resolver = new ModelResolver(this.sedmlContainer);
		if(this.archiveComponents != null) {
			ArchiveModelResolver amr = new ArchiveModelResolver(this.archiveComponents);
			amr.setSedmlPath(this.sedmlContainer.getPathForURI());
			this.resolver.add(amr);
		} else {
			this.resolver.add(new FileModelResolver()); // assumes absolute paths
			String sedmlRelativePrefix = fileWithSedmlToProcess.getParent() + File.separator;
			this.resolver.add(new RelativeFileModelResolver(sedmlRelativePrefix)); // in case model URIs are relative paths
		}
		this.sbmlSupport = new SBMLSupport();
        return this.sedmlContainer;
	}

    /**
     * Get the list of biomodels from the sedml initialized at construction time.
     */
    public Map<BioModel, SBMLSymbolMapping> getBioModels(){
        if (this.sedmlContainer == null) throw new IllegalStateException("Importer has not yet been initialized!");
		Set<BioModel> bioModels = new LinkedHashSet<>();

		Map<SId, BioModel> bioModelMap; // Holds all entries for all SEDML Models where some may reference the same BioModel
		Map<SId, Simulation> vcSimulations; // We will parse all tasks and create Simulations in BioModels
        SedMLDataContainer matchingSedmlContainer = this.sedmlContainer;
        SedML matchingSedml = matchingSedmlContainer.getSedML();
		try {
	        // iterate through all the elements and show them at the console
            this.printSEDMLSummary(matchingSedml.getModels(), matchingSedml.getSimulations(), matchingSedml.getTasks(), matchingSedml.getDataGenerators(), matchingSedml.getOutputs());
            // If we don't have models, we don't have anything to do
            if (this.sedmlContainer.getSedML().getModels().isEmpty()) return new HashMap<>(); // nothing to import

			// NB: We don't know how many BioModels we'll end up with,
			// 		as some model changes may be translatable as simulations with overrides
			bioModelMap = this.createBioModels(matchingSedml.getModels());
			Set<BioModel> uniqueBioModels = new HashSet<>(bioModelMap.values());

            // First, process tasks (a.k.a "base tasks", "actual tasks", "basic tasks", etc. NOT repeated tasks)
			vcSimulations = this.addStandardTasks(matchingSedmlContainer, bioModelMap);

			// now process repeated tasks, if any
			this.addRepeatedTasks(matchingSedml.getTasks(), vcSimulations);

			// we may have added simulations in addRepeatedTasks() {method above}, so let's make sure we add them.

			// finally try to pretty up simulation names
			for (BioModel bm : uniqueBioModels) {
				Simulation[] sims = bm.getSimulations();
				for (Simulation sim : sims) {
					String taskId = sim.getImportedTaskID();
                    SedBase sedBaseFound = matchingSedml.searchInTasksFor(new SId(taskId));
                    if (!(sedBaseFound instanceof AbstractTask task)) throw new RuntimeException("Unexpected non-task");
					if (task.getName() != null) {
						try {
							sim.setName(task.getName());
						} catch (PropertyVetoException e) {
							// we should never bomb out just for trying to set a pretty name
                            logger.warn("could not set pretty name for simulation {} from task {}", sim.getDisplayName(), task);
						}
					}
                }
			}
			// purge unused biomodels and applications


			for (BioModel doc : uniqueBioModels){
				for (int i = 0; i < doc.getSimulationContexts().length; i++) {
					if (doc.getSimulationContext(i).getName().startsWith("original_imported_")) {
						doc.removeSimulationContext(doc.getSimulationContext(i));
						i--;
					}
				}
				if (doc.getSimulationContexts().length > 0) bioModels.add(doc);
			}

			// try to consolidate SimContexts into fewer (possibly just one) BioModels
			// unlikely to happen from SED-MLs not originating from VCell, but very useful for round-tripping if so
			// TODO: maybe try to detect that and only try if of VCell origin
			bioModels = this.mergeBioModels(bioModels);

			// TODO: make imported BioModel(s) VCell-friendly
			// TODO: apply TransformMassActions to usedBiomodels
			// cannot do this for now, as it can be very expensive (hours!!)
			// also has serious memory issues (runs out of memory even with bumping up to Xmx12G
			return bioModels.stream().collect(Collectors.toMap(Function.identity(), this::getSBMLSymbolMapping));
		} catch (Exception e) {
			Tracer.failure(e,"Unable to initialize bioModel for the given selection: "+e.getMessage());
			throw new RuntimeException("Unable to initialize bioModel for the given selection\n"+e.getMessage(), e);
		}
	}

    private SedMLDataContainer verifyOrModifyOrRejectSedml(SedMLDataContainer providedSedmlContainer) {
        /*
        These are the cases we currently track:
        1) SedML requests a simulation type we don't currently support (e.g. SteadyState & Analysis)
        2) SedML requests a solver algorithm that we do not have exactly
        3) SedML requests repeated task with multiple subtasks
         */
        boolean shouldPrune = false;
        boolean disallowModifiedImport = this.disallowModifiedImport;
        SedMLDataContainer copiedSedmlContainer;
        if (this.disallowModifiedImport){
            copiedSedmlContainer = providedSedmlContainer;
        } else {
            SedMLDataContainer tmpContainer;
            try {
                tmpContainer = new SedMLDataContainer(providedSedmlContainer, true);
            } catch (CloneNotSupportedException e){
                logger.warn("could not clone sedml in provided SedMLDataContainer; will still attempt strict import.");
                tmpContainer = providedSedmlContainer;
                disallowModifiedImport = true;
            }
            copiedSedmlContainer = tmpContainer;
        }
        SedML copiedSedml = copiedSedmlContainer.getSedML();

        // Case 1: Verify all valid simulations
        logger.info("Simulation Type check: STARTED");
        Set<org.jlibsedml.components.simulation.Simulation> validSimulations = new HashSet<>();
        validSimulations.addAll(copiedSedml.getSimulations().stream().filter(UniformTimeCourse.class::isInstance).toList());
        validSimulations.addAll(copiedSedml.getSimulations().stream().filter(OneStep.class::isInstance).toList());
        if (validSimulations.size() != copiedSedml.getSimulations().size()){
            // We have some simulations we can't support
            Set<org.jlibsedml.components.simulation.Simulation> badSims = new HashSet<>(copiedSedml.getSimulations());
            badSims.removeAll(validSimulations);
            if (disallowModifiedImport){
                String badSimsString = badSims.stream().map(Object::toString).collect(Collectors.joining("\n\t"));
                String errMsg = "Provided SedML contains disallowed Simulations:\n\t" + badSimsString;
                logger.error("Simulation Type check: FAILED ...throwing exception...");
                throw new IllegalArgumentException(errMsg);
            }
            logger.info("Removing unsupported simulations from SedML");
            // Safe to modify `copiedSedmlContainer` / "copiedSedml" for rest of this scope; if statement ensures it's a deep copy
            for (org.jlibsedml.components.simulation.Simulation simToRemove: badSims){
                copiedSedml.getListOfSimulations().removeContent(simToRemove);
            }
            shouldPrune = true;
        }
        logger.info("Simulation Type check: PASSED");

        // Case 2: Solver Algorithm Check
        logger.info("Solver Algorithm check: STARTED");
        java.util.function.Function<org.jlibsedml.components.simulation.Simulation, Algorithm> SimulationDotGetAlgorithm = org.jlibsedml.components.simulation.Simulation::getAlgorithm;
        Set<String> requestedAlgorithms = copiedSedml.getSimulations().stream().map(SimulationDotGetAlgorithm).map(Algorithm::getKisaoID).collect(Collectors.toSet());
        Map<String, SolverDescription> solverMatches = new HashMap<>();
        for (String kisao : requestedAlgorithms) solverMatches.put(kisao, SolverUtilities.matchSolverWithKisaoId(kisao, this.disallowModifiedImport));
        Set<String> badMatches = new HashSet<>();
        for (String kisao : solverMatches.keySet()) if (solverMatches.get(kisao) == null) badMatches.add(kisao);
        if (!badMatches.isEmpty()){
            if (disallowModifiedImport){
                String badSimsString = String.join("\n\t", badMatches);
                String errMsg = "Under selected settings, provided SedML contains unmatchable Kisao Algorithms:\n\t" + badSimsString;
                logger.error("Simulation Algorithm check: FAILED ...throwing exception...");
                throw new IllegalArgumentException(errMsg);
            }
            // Safe to modify `copiedSedmlContainer` / "copiedSedml" for rest of this scope; if statement ensures it's a deep copy
            if (this.trySundialsAnyway){
                // give it a try anyway with our deterministic default solver
                logger.warn("Attempting to solve incompatible kisao algorithms with deterministic default solver: {}", SolverDescription.CombinedSundials);
                for (String kisao : requestedAlgorithms) solverMatches.putIfAbsent(kisao, SolverDescription.CombinedSundials);
            } else {
                // remove the offending simulations (and their unsupported algorithms)
                for (String badKisao : badMatches) solverMatches.remove(badKisao);
                logger.info("Removing simulations with unsupported kisao from SedML");
                for (org.jlibsedml.components.simulation.Simulation sim: copiedSedml.getSimulations()) {
                    String kisaoAlg = sim.getAlgorithm().getKisaoID();
                    if (!badMatches.contains(kisaoAlg)) continue;
                    copiedSedml.getListOfSimulations().removeContent(sim);
                }
                shouldPrune = true;
            }
        }
        this.kisaoToSolverMapping.putAll(solverMatches);
        logger.info("Solver Algorithm check: PASSED");

        // Case 3: Multi-SubTask Check
        //      -> In essence, a repeated task can apply to multiple "simulations" at once. This is problematic for us.
        logger.info("Multi-SubTask check: STARTED");
        List<RepeatedTask> repeatedTasks = copiedSedml.getTasks().stream().filter(RepeatedTask.class::isInstance).map(RepeatedTask.class::cast).toList();
        List<RepeatedTask> badRepeatedTasks = repeatedTasks.stream().filter((task)->task.getSubTasks().size() > 1).toList();
        if (!badRepeatedTasks.isEmpty()){
            // Uh-oh, multi-subTasks detected!
            if (this.disallowModifiedImport){
                String badSimsString = badRepeatedTasks.stream().map(Object::toString).collect(Collectors.joining("\n\t"));
                String errMsg = "Provided SedML contains disallowed RepeatedTasks with multi-subTasks:\n\t" + badSimsString;
                logger.info("Multi-SubTask check: FAILED ...throwing exception...");
                throw new IllegalArgumentException(errMsg);
            }
            // Safe to modify `copiedSedmlContainer` / "copiedSedml" for rest of this scope; if statement ensures it's a deep copy
            //
            // There one way we can keep the repeated task:
            // if the repeated task does something silly, like repeat the same task multiple times...for no good reason...,
            // we can just keep one single subtask, and keep the repeated task!
            for (RepeatedTask badRepeatedTask: badRepeatedTasks){
                Set<SId> uniqueSubTaskReferences = badRepeatedTask.getSubTasks().stream().map(SubTask::getId).collect(Collectors.toSet());
                if (uniqueSubTaskReferences.size() == 1){
                    SubTask subTaskToKeep = badRepeatedTask.getSubTasks().iterator().next();
                    for (SubTask subTask : badRepeatedTask.getSubTasks()){
                        if (subTask == subTaskToKeep) continue;
                        badRepeatedTask.getListOfSubTasks().removeContent(subTask);
                    }
                } else { // we have to remove the unsupported task
                    for (RepeatedTask taskToRemove: badRepeatedTasks){
                        copiedSedml.getListOfTasks().removeContent(taskToRemove);
                    }
                    shouldPrune = true;
                }
            }
        }
        logger.info("Multi-SubTask check: PASSED");

        if (shouldPrune) providedSedmlContainer.pruneSedML();
        return providedSedmlContainer;
    }

	private Set<BioModel> mergeBioModels(Set<BioModel> bioModelSet) {
		if (bioModelSet.size() <= 1) return bioModelSet;
        List<BioModel> bioModels = new ArrayList<>(bioModelSet);
		// for now just try if they *ALL* have matchable model
		// this should be the case if dealing with exported SEDML/OMEX from VCell BioModel with multiple applications
		
		// temporary kludge for dealing with BioModels that have Applications with disabled reactions
		// TODO export disabled reactions with specific zero kinetic law multiplier
		BioModel bmMax = bioModels.get(0);
		boolean differentRxNo = false;
		for (int i = 1; i < bioModels.size(); i++) {
			if (bioModels.get(i).getModel().getReactionSteps().length != bmMax.getModel().getReactionSteps().length) {
				differentRxNo = true;				
			}
			if (bioModels.get(i).getModel().getReactionSteps().length > bmMax.getModel().getReactionSteps().length) {
				bmMax = bioModels.get(i);
			}
		}
		if (differentRxNo) {
			for (BioModel bioModel : bioModels) {
				if (bioModel.getModel().getReactionSteps().length < bmMax.getModel().getReactionSteps().length) {
					try {
						BioModel clonedBM = XmlHelper.cloneBioModel(bmMax);
						// try to add missing reactions
						// these may use unique global params, so try to first add missing model params, if any
						for (int l = 0; l < clonedBM.getModel().getModelParameters().length; l++) {
							if (bioModel.getModel().getModelParameter(clonedBM.getModel().getModelParameters()[l].getName()) == null) {
								bioModel.getModel().addModelParameter(clonedBM.getModel().getModelParameters()[l]);
							}
						}
						// now try to add the missing reactions
						for (int j = 0; j < clonedBM.getModel().getReactionSteps().length; j++) {
							if (bioModel.getModel().getReactionStep(clonedBM.getModel().getReactionSteps()[j].getName()) == null) {
								ReactionStep rxToAdd = clonedBM.getModel().getReactionSteps()[j];
								// must update pointers to objects in current model
								ReactionParticipant[] rxPart = rxToAdd.getReactionParticipants();
								for (ReactionParticipant reactionParticipant : rxPart) {
									String speciesContextName = reactionParticipant.getSpeciesContext().getName();
									SpeciesContext sc = bioModel.getModel().getSpeciesContext(speciesContextName);
									reactionParticipant.setSpeciesContext(sc);
								}
								rxToAdd.setStructure(bioModel.getModel().getStructure(rxToAdd.getStructure().getName()));
								rxToAdd.setModel(bioModel.getModel());
								bioModel.getModel().addReactionStep(rxToAdd);
								// set the added reactions as disabled in simcontext(s)
								for (int k = 0; k < bioModel.getSimulationContexts().length; k++) {
									bioModel.getSimulationContexts()[k].getReactionContext().getReactionSpec(clonedBM.getModel().getReactionSteps()[j]).setReactionMapping(ReactionSpec.EXCLUDED);
								}
							}
						}
						bioModel.refreshDependencies();
					} catch (XmlParseException | PropertyVetoException e) {
						logger.error(e);
						return bioModelSet;
					}
				}
			}
		}
		
		BioModel bm0 = bioModels.get(0);
		for (int i = 1; i < bioModels.size(); i++) {
			logger.debug("--------------------\ncomparing model from `{}`\n with model from `{}`\n--------------------", bioModels.get(i), bm0);
			RelationVisitor rvNotStrict = new ModelRelationVisitor(false);
			boolean equivalent = bioModels.get(i).getModel().relate(bm0.getModel(),rvNotStrict);
			logger.debug("Equivalent => {}", equivalent);
			if (!equivalent) return bioModelSet;
		}
		// all have matchable model, try to merge by pooling SimContexts
		Document dom;
		Xmlproducer xmlProducer = new Xmlproducer(false);
		try {
			dom = XmlHelper.bioModelToXMLDocument(bm0,false);
			Element root = dom.getRootElement();
			Element bioModel = root.getChild("BioModel", root.getNamespace());
			for (int i = 1; i < bioModels.size(); i++) {
				// get XML of SimContext here and insert into baseXML
				SimulationContext[] simContexts = bioModels.get(i).getSimulationContexts();
				for (SimulationContext sc : simContexts) {
					Element scElement = xmlProducer.getXML(sc, bioModels.get(i));
					XmlUtil.setDefaultNamespace(scElement, root.getNamespace());
					bioModel.addContent(scElement);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return bioModelSet;
		}
		// re-read XML into a single BioModel and replace docs List
		BioModel mergedBioModel;
		try {
			String mergedXML = XmlUtil.xmlToString(dom, true);
			mergedBioModel = XmlHelper.XMLToBioModel(new XMLSource(mergedXML));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return bioModelSet;
		}
		// merge succeeded, replace the list
		return Set.of(mergedBioModel);
		// TODO more work here if we want to generalize
	}

	private void createOverrides(Simulation newSimulation, List<Change> changes) throws SEDMLImportException {
		for (Change change : changes) {
			String targetID = this.sbmlSupport.getIdFromXPathIdentifer(change.getTargetXPath().getTargetAsString());
			SimulationContext importedSimcontext; SimulationContext convertedSimcontext;
			SimulationContext currentSC = (SimulationContext)newSimulation.getSimulationOwner();
			if (((SimulationContext)newSimulation.getSimulationOwner()).getApplicationType()
					== Application.NETWORK_STOCHASTIC) {
				importedSimcontext = currentSC.getBioModel().getSimulationContext(0);
				convertedSimcontext = (SimulationContext)newSimulation.getSimulationOwner();
			} else {
				importedSimcontext = (SimulationContext)newSimulation.getSimulationOwner();
				convertedSimcontext = null;
			}
			Variable vcVar = this.resolveMathVariable(importedSimcontext, convertedSimcontext, targetID);
			if (this.isMathVariableNonConstantValued(vcVar)) {
				logger.error("target in change "+change+" could not be resolved to Constant, overrides not applied");
				continue;
			}
			try {
				Expression exp;
				if (change.isChangeAttribute()) {
					exp = new Expression(((ChangeAttribute)change).getNewValue());
				} else if (change.isComputeChange() || change.isSetValue()) {
					ComputeChange cc = (ComputeChange)change;
					ASTNode math = cc.getMath();
					exp = new ExpressionMathMLParser(null).fromMathML(math, "t");
					
					// Substitute SED-ML parameters
					List<Parameter> params = cc.getParameters();
					System.out.println(params);
					for (Parameter param : params) {
						exp.substituteInPlace(new Expression(param.getIdAsString()), new Expression(param.getValue()));
					}
					
					// Substitute SED-ML variables (which reference SBML entities)
					List<org.jlibsedml.components.Variable> vars = cc.getVariables();
					System.out.println(vars);
					for (org.jlibsedml.components.Variable var : vars) {
						String sbmlID = this.sbmlSupport.getIdFromXPathIdentifer(var.getTarget());
						vcVar = this.resolveMathVariable(importedSimcontext, convertedSimcontext, sbmlID);
						if (this.isMathVariableNonConstantValued(vcVar)){
							throw new SEDMLImportException("could not evaluate var '"+vcVar.getName()+" as a constant");
						}
						exp.substituteInPlace(new Expression(var.getIdAsString()), new Expression(vcVar.getName()));
					}
				} else {
					logger.error("unsupported change "+change+" encountered, overrides not applied");
					continue;
				}
				exp = this.scaleIfChanged(exp, targetID, importedSimcontext, convertedSimcontext);
				exp = exp.simplifyJSCL();
				Constant constant = new Constant(vcVar.getName(),exp);
				newSimulation.getMathOverrides().putConstant(constant);
			} catch (ExpressionException e) {
				String message = "expression in change %s could not be resolved to Constant, overrides not applied";
				logger.error(String.format(message, change));
			}
		}
	}

	private Expression scaleIfChanged (Expression exp, String targetID, SimulationContext importedSC, SimulationContext convertedSC) throws ExpressionException, SEDMLImportException {
		SBMLImporter sbmlImporter = this.importMap.get(importedSC.getBioModel());
		SBMLSymbolMapping sbmlMap = sbmlImporter.getSymbolMapping();
		SBase targetSBase = sbmlMap.getMappedSBase(targetID);
		Expression origExp = sbmlMap.getRuleSBMLExpression(targetSBase, SymbolContext.INITIAL);
		Expression currentExp = sbmlMap.getSte(targetSBase, SymbolContext.INITIAL).getExpression();
		if (origExp != null && currentExp != null && !currentExp.isZero()) {
			Expression factor = new Expression(Expression.div(origExp, currentExp)).flattenSafe();
			String[] symbols = factor.getSymbols();
			if (symbols != null) {
				for (String sbString : symbols) {
					Variable vcVar = this.resolveMathVariable(importedSC, convertedSC, sbString);
					if (this.isMathVariableNonConstantValued(vcVar)){
						throw new SEDMLImportException("cannot solve for constant valued scale factor, '"+vcVar+"' is not constant");
					}
					factor.substituteInPlace(new Expression(sbString), new Expression(vcVar.getName()));
				}
			}
			if (!factor.isOne()) {
				return Expression.div(exp, factor).simplifyJSCL();
			}
		}

		return exp;
	}

	private boolean isMathVariableNonConstantValued(Variable var) {
		boolean varIsConstant = (var instanceof Constant);
		if (var instanceof cbit.vcell.math.Function) {
			try {
				var.getExpression().evaluateConstantWithSubstitution();
				varIsConstant = true;
			} catch (Exception e) {
				logger.warn("Substituted constant evaluation failed", e);
			}
		}
		return !varIsConstant;
	}

	private Variable resolveMathVariable(SimulationContext importedSimContext, SimulationContext convertedSimContext,
										 String SBMLTargetID) {
		// finds name of math-side Constant corresponding to SBML entity, if there is one
		// returns null if there isn't

		MathSymbolMapping mathSymbolMapping = (MathSymbolMapping)importedSimContext.getMathDescription().getSourceSymbolMapping();
		SBMLSymbolMapping sbmlMap = this.getSBMLSymbolMapping(importedSimContext.getBioModel());
		SBase targetSBase = sbmlMap.getMappedSBase(SBMLTargetID);
		if (targetSBase == null){
			throw new SBMLImportException("couldn't find SBase with sid="+SBMLTargetID+" in SBMLSymbolMapping");
		}
		SymbolTableEntry biologicalSymbolTableEntry = sbmlMap.getSte(targetSBase, SymbolContext.INITIAL);
		Variable var = mathSymbolMapping.getVariable(biologicalSymbolTableEntry);
		boolean varIsConstant = (var instanceof Constant);
		if (var instanceof cbit.vcell.math.Function){
			try {
				var.getExpression().evaluateConstantWithSubstitution();
				varIsConstant = true;
			} catch (Exception e){
				logger.warn("Substituted constant evaluation failed", e);
			}
		}
		// if simcontext was converted to stochastic then species init constants use different names
		if (convertedSimContext != null && biologicalSymbolTableEntry instanceof SpeciesContextSpecParameter speciesContextSpecParameter) {
            if (speciesContextSpecParameter.getRole() == SpeciesContextSpec.ROLE_InitialConcentration
					|| speciesContextSpecParameter.getRole() == SpeciesContextSpec.ROLE_InitialCount) {
				String spcName = speciesContextSpecParameter.getSpeciesContext().getName();
				SpeciesContextSpec scs = null;
				for (int i = 0; i < convertedSimContext.getReactionContext().getSpeciesContextSpecs().length; i++) {
					scs = convertedSimContext.getReactionContext().getSpeciesContextSpecs()[i];
					if (scs.getSpeciesContext().getName().equals(spcName)) {
						break;
					}
				}
				if (scs == null) throw new RuntimeException("SpeciesContextSpec is unexpectedly null");
				SpeciesContextSpecParameter convertedSCSP = scs.getInitialConditionParameter();
				var = (convertedSimContext.getMathDescription().getSourceSymbolMapping()).getVariable(convertedSCSP);
				return var;
			}
		}
		return var;
	}

    private Map<SId, Simulation> addStandardTasks(SedMLDataContainer sedmlContainer, Map<SId, BioModel> bioModelMap)
            throws PropertyVetoException, SEDMLImportException, ImageException, GeometryException, ExpressionException, MappingException {
        Map<SId, Simulation> vcSimulations = new HashMap<>();
        SedML sedml = sedmlContainer.getSedML();
        // Creating one VCell Simulation for each SED-ML actual Task
        // 		(RepeatedTasks get added either on top of or separately as parameter scan overrides)
        for (AbstractTask selectedTask : sedml.getTasks()) {
            if (selectedTask instanceof RepeatedTask) continue; // Repeated tasks refer to regular tasks, so first we need to create simulations for all regular tasks
            if (!(selectedTask instanceof Task baseTask)) throw new RuntimeException("Unsupported task " + selectedTask);

            // the SedML simulation will become the vCell simulation
            SedBase sedBaseSimFound = sedml.searchInSimulationsFor(baseTask.getSimulationReference());
            if(!(sedBaseSimFound instanceof UniformTimeCourse utcSimulation)) { // only UTC sims supported
                String baseTaskName = String.format("%s(%s)", baseTask.getName() == null ? "" : baseTask.getName(), baseTask.getId());
                logger.error("task '{}' is being skipped, it references an unsupported simulation type: {}", baseTaskName, sedBaseSimFound.getClass().getSimpleName());
                continue;
            }

            // the "original" model referred to by the task; almost always sbml we can import as physiology
            SedBase sedBaseModelFound = sedml.searchInModelsFor(baseTask.getModelReference());
            if(!(sedBaseModelFound instanceof Model sedmlModel)) {
                String baseTaskName = String.format("%s(%s)", baseTask.getName() == null ? "" : baseTask.getName(), baseTask.getId());
                logger.error("Model reference of task `{}` is invalid", baseTaskName);
                continue;
            }
            // can be sbml or vcml
            String sedmlOriginalModelLanguage = sedmlModel.getLanguage();
            // this will be used in the BioModel name
            String sedmlOriginalModelName = sedmlModel.getName() != null ? sedmlModel.getName() : "";

            // at this point we assume that the sedml simulation, algorithm and kisaoID are all valid

            // identify the vCell solvers that would match best the sedml solver kisao id
            String kisaoID = utcSimulation.getAlgorithm().getKisaoID();
            SolverDescription solverDescription = this.kisaoToSolverMapping.get(kisaoID);
            if (solverDescription == null)
                throw new IllegalStateException("kisao id was not properly mapped at initialization time!");

            // find out everything else we need about the application we're going to use,
            // as some more info will be needed when we parse the sbml file
            boolean bSpatial = false;
            Application appType = Application.NETWORK_DETERMINISTIC;
            Set<SolverDescription.SolverFeature> sfList = solverDescription.getSupportedFeatures();
            for(SolverDescription.SolverFeature sf : sfList) {
                switch(sf) {
                    case Feature_Rulebased:
                        if(appType != Application.SPRINGSALAD) {
                            // springs(alad) type takes precedence
                            appType = Application.RULE_BASED_STOCHASTIC;
                        }
                        break;
                    case Feature_Stochastic:
                        appType = Application.NETWORK_STOCHASTIC;
                        break;
                    case Feature_Deterministic:
                        appType = Application.NETWORK_DETERMINISTIC;
                        break;
                    case Feature_Springs:
                        appType = Application.SPRINGSALAD;
                        break;
                    case Feature_Spatial:
                        bSpatial = true;
                        break;
                    default:
                        break;
                }
            }

            BioModel bioModel = bioModelMap.get(sedmlModel.getId());

            // if language is VCML, we don't need to create Applications and Simulations in BioModel
            // we allow a subset of SED-ML Simulation settings (may have been edited) to override existing BioModel Simulation settings

            if(sedmlOriginalModelLanguage.contentEquals(SUPPORTED_LANGUAGE.VCELL_GENERIC.getURN())) {
                Simulation theSimulation = null;
                for (Simulation sim : bioModel.getSimulations()) {
                    if (sim.getName().equals(baseTask.getName())) {
                        logger.trace(" --- selected task - name: " + baseTask.getName() + ", id: " + baseTask.getId());
                        sim.setImportedTaskID(baseTask.getId().string());
                        theSimulation = sim;
                        break;	// found the one, no point to continue the for loop
                    }
                }if(theSimulation == null) {
                    logger.error("Couldn't match sedml task '" + baseTask.getName() + "' with any biomodel simulation");
                    // TODO: should we throw an exception?
                    continue;	// should never happen
                }

                SolverTaskDescription simTaskDesc = theSimulation.getSolverTaskDescription();
                this.translateTimeBounds(simTaskDesc, utcSimulation);
                continue;
            }

            // if language is SBML, we must create Simulations
            // we may need to also create Applications (if the default one from SBML import is not the right type)

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
                // this happens if we need a NETWORK_STOCHASTIC application
                String modelPlusContextName = String.format("%s_%d", sedmlOriginalModelName, existingSimulationContexts.length);
                matchingSimulationContext = SimulationContext.copySimulationContext(
                        bioModel.getSimulationContext(0),
                        modelPlusContextName, bSpatial, appType);
                bioModel.addSimulationContext(matchingSimulationContext);
                try {
                    String importedSCName = bioModel.getSimulationContext(0).getName();
                    bioModel.getSimulationContext(0).setName("original_imported_"+importedSCName);
                    matchingSimulationContext.setName(importedSCName);
                } catch (PropertyVetoException e) {
                    // we should never bomb out just for trying to set a pretty name
                    logger.warn("could not set pretty name on application from name of model "+sedmlModel);
                }
            }
            matchingSimulationContext.refreshDependencies();
            MathMappingCallback callback = new MathMappingCallbackTaskAdapter(null);
            matchingSimulationContext.refreshMathDescription(callback, NetworkGenerationRequirements.ComputeFullStandardTimeout);

            // making the new vCell simulation based on the sedml simulation
            Simulation newSimulation = new Simulation(matchingSimulationContext.getMathDescription(), matchingSimulationContext);

            // See note below this immediately following section
            String newSimName = baseTask.getId().string();
            if(SedMLUtil.getName(baseTask) != null) {
                newSimName += "_" + SedMLUtil.getName(baseTask);
            }
            newSimulation.setName(newSimName);
            newSimulation.setImportedTaskID(baseTask.getId().string());
            vcSimulations.put(baseTask.getId(), newSimulation);

				/* NOTE: Before, we checked if the selected task was an instance of a task; we no longer need to check
					that because we logically confirm that earlier on. If we ever get to this point and need an 'else':

				 newSimulation.setName(SEDMLUtil.getName(utcSimulation)+"_"+SEDMLUtil.getName(baseTask));
				 */

            // we identify the type of sedml simulation (uniform time course, etc.)
            // and set the vCell simulation parameters accordingly
            SolverTaskDescription simTaskDesc = newSimulation.getSolverTaskDescription();
            simTaskDesc.setSolverDescription(solverDescription);


            this.translateTimeBounds(simTaskDesc, utcSimulation);
            this.translateAlgorithmParams(simTaskDesc, utcSimulation);

            newSimulation.setSolverTaskDescription(simTaskDesc);
            newSimulation.setDescription(SedMLUtil.getName(baseTask));
            bioModel.addSimulation(newSimulation);
            newSimulation.refreshDependencies();

            // finally, add MathOverrides if referenced model has specified compatible changes
            if (!sedmlModel.getChanges().isEmpty() && this.canTranslateToOverrides(bioModel, sedmlModel)) {
                this.createOverrides(newSimulation, sedmlModel.getChanges());
            }

        }
        return vcSimulations;
    }

	private void addRepeatedTasks(List<AbstractTask> listOfTasks, Map<SId, Simulation> vcSimulations) throws ExpressionException, PropertyVetoException, SEDMLImportException {
		for (AbstractTask abstractedRepeatedTask : listOfTasks) {
			if (!(abstractedRepeatedTask instanceof RepeatedTask repeatedTask)) continue;

            if (!repeatedTask.getResetModel() || repeatedTask.getSubTasks().size() != 1) { // if removed, see RunUtils.prepareNonspatialHdf5()
                logger.error("sequential RepeatedTask not yet supported, task {} is being skipped", SedMLUtil.getName(abstractedRepeatedTask));
				continue;
			}

			Task baseTask = this.getBaseTask(repeatedTask);
			Simulation originalSim = vcSimulations.get(baseTask.getId());
			if (originalSim == null){
				String baseTaskName = String.format("%s(%s)", baseTask.getName() == null ? "" : baseTask.getName(), baseTask.getId());
				String repeatedTaskName = String.format("%s(%s)", abstractedRepeatedTask.getName() == null ? "" : abstractedRepeatedTask.getName(), abstractedRepeatedTask.getId());
				logger.warn("VCSimulation for base task `{}` was not found;skipping RepeatedTask `{}`", baseTaskName, repeatedTaskName);
				continue;
			}
			Simulation simulation = new Simulation(originalSim); // make a copy of the original simulation
			SimulationContext importedSimcontext, convertedSimcontext;
			SimulationContext currentSC = (SimulationContext)simulation.getSimulationOwner();
			if (currentSC.getApplicationType() == Application.NETWORK_STOCHASTIC) {
				importedSimcontext = currentSC.getBioModel().getSimulationContext(0);
				convertedSimcontext = (SimulationContext)simulation.getSimulationOwner();
			} else {
				importedSimcontext = (SimulationContext)simulation.getSimulationOwner();
				convertedSimcontext = null;
			}

			List<SetValue> changes = repeatedTask.getChanges();
			List<Change> functions = new ArrayList<>();
			for (SetValue change : changes) {
				Range range = repeatedTask.getRange(change.getRangeReference());
				ASTNode math = change.getMath();
				Expression exp = new ExpressionMathMLParser(null).fromMathML(math, "t");
				if (exp.infix().equals(range.getId().string())) {
					// add a parameter scan to the simulation referred by the actual task
					ConstantArraySpec scanSpec;
					String targetID = this.sbmlSupport
							.getIdFromXPathIdentifer(change.getTargetXPath().getTargetAsString());
					Variable constantValuedVar = this.resolveMathVariable(importedSimcontext, convertedSimcontext, targetID);
					if (this.isMathVariableNonConstantValued(constantValuedVar)){
						throw new SEDMLImportException("expecting vcell var '"+constantValuedVar.getName()+"' " +
								"mapped to SBML target '"+targetID+"' to be constant valued");
					}
					if (range instanceof UniformRange ur) {
                        scanSpec = ConstantArraySpec.createIntervalSpec(constantValuedVar.getName(),
								""+Math.min(ur.getStart(), ur.getEnd()), ""+Math.max(ur.getStart(), ur.getEnd()),
								ur.getNumberOfSteps(), ur.getType().equals(UniformType.LOG));
					} else if (range instanceof VectorRange vr) {
                        String[] values = new String[vr.getNumElements()];
						for (int i = 0; i < values.length; i++) {
							values[i] = Double.toString(vr.getElementAt(i));
						}
						scanSpec = ConstantArraySpec.createListSpec(constantValuedVar.getName(), values);
					} else if (range instanceof FunctionalRange fr){
                        Range index = repeatedTask.getRange(fr.getRange());
						if (index instanceof UniformRange ur) {
                            ASTNode frMath = fr.getMath();
							Expression frExpMin = new ExpressionMathMLParser(null).fromMathML(frMath, "t");
							Expression frExpMax = new ExpressionMathMLParser(null).fromMathML(frMath, "t");

							// Substitute Range values
							frExpMin.substituteInPlace(new Expression(ur.getIdAsString()), new Expression(ur.getStart()));
							frExpMax.substituteInPlace(new Expression(ur.getIdAsString()), new Expression(ur.getEnd()));

							// Substitute SED-ML parameters
							List<Parameter> params = fr.getParameters();
							System.out.println(params);
							for (Parameter param : params) {
								frExpMin.substituteInPlace(new Expression(param.getIdAsString()), new Expression(param.getValue()));
								frExpMax.substituteInPlace(new Expression(param.getIdAsString()), new Expression(param.getValue()));
							}

							// Substitute SED-ML variables (which reference SBML entities)
							List<org.jlibsedml.components.Variable> vars = fr.getVariables();
							System.out.println(vars);
							for (org.jlibsedml.components.Variable var : vars) {
								String sbmlID = this.sbmlSupport.getIdFromXPathIdentifer(var.getTarget());
								Variable vcVar = this.resolveMathVariable(importedSimcontext, convertedSimcontext, sbmlID);
								if (this.isMathVariableNonConstantValued(vcVar)){
									throw new SEDMLImportException("expecting vcell var '"+constantValuedVar.getName()+"' " +
											"mapped to SBML target '"+sbmlID+"' to be constant valued");
								}
								frExpMin.substituteInPlace(new Expression(var.getIdAsString()), new Expression(vcVar.getName()));
								frExpMax.substituteInPlace(new Expression(var.getIdAsString()), new Expression(vcVar.getName()));
							}
							frExpMin = this.scaleIfChanged(frExpMin, targetID, importedSimcontext, convertedSimcontext);
							frExpMax = this.scaleIfChanged(frExpMax, targetID, importedSimcontext, convertedSimcontext);
							frExpMin = frExpMin.simplifyJSCL();
							frExpMax = frExpMax.simplifyJSCL();
							String minValueExpStr = frExpMin.infix();
							String maxValueExpStr = frExpMax.infix();
							scanSpec = ConstantArraySpec.createIntervalSpec(constantValuedVar.getName(), minValueExpStr, maxValueExpStr, ur.getNumberOfSteps(), ur.getType().equals(UniformType.LOG));
						} else if (index instanceof VectorRange vr) {
                            ASTNode frMath = fr.getMath();
							Expression expFact = new ExpressionMathMLParser(null).fromMathML(frMath, "t");
							// Substitute SED-ML parameters
							List<Parameter> params = fr.getParameters();
							System.out.println(params);
							for (Parameter param : params) {
								expFact.substituteInPlace(new Expression(param.getIdAsString()), new Expression(param.getValue()));
							}
							// Substitute SED-ML variables (which reference SBML entities)
                            List<org.jlibsedml.components.Variable> vars = fr.getVariables();
							System.out.println(vars);
							for (org.jlibsedml.components.Variable var : vars) {
								String sbmlID = this.sbmlSupport.getIdFromXPathIdentifer(var.getTarget());
								Variable vcVar = this.resolveMathVariable(importedSimcontext, convertedSimcontext, sbmlID);
								if (this.isMathVariableNonConstantValued(vcVar)){
									throw new SEDMLImportException("expecting vcell var '"+constantValuedVar.getName()+"' " +
											"mapped to SBML target '"+sbmlID+"' to be constant valued");
								}
								expFact.substituteInPlace(new Expression(var.getIdAsString()), new Expression(vcVar.getName()));
							}
							expFact = expFact.simplifyJSCL();
							String[] values = new String[vr.getNumElements()];
							for (int i = 0; i < values.length; i++) {
								Expression expFinal = new Expression(expFact);
								// Substitute Range values
								expFinal.substituteInPlace(new Expression(vr.getIdAsString()), new Expression(vr.getElementAt(i)));
								expFinal = this.scaleIfChanged(expFinal, targetID, importedSimcontext, convertedSimcontext);
								expFinal = expFinal.simplifyJSCL();
								values[i] = expFinal.infix();
							}
							scanSpec = ConstantArraySpec.createListSpec(constantValuedVar.getName(), values);
						} else {
							// we only support FunctionalRange with intervals and lists
                            logger.error("FunctionalRange does not reference UniformRange or VectorRange, task {} is being skipped", SedMLUtil.getName(abstractedRepeatedTask));
							continue;
						}
					} else {
                        logger.error("unsupported Range class found, task {} is being skipped", SedMLUtil.getName(abstractedRepeatedTask));
						continue;
					}
					MathOverrides mo = simulation.getMathOverrides();
					mo.putConstantArraySpec(scanSpec);
				} else {
					functions.add(change);
				}
			}
			/*
			this.createOverrides(simulation, functions);
			// we didn't bomb out, so update the simulation
			simulation.setImportedTaskID(abstractedRepeatedTask.getId());
			simulation.setName(SEDMLUtil.getName(this.sedml.getSimulation(baseTask.getSimulationReference()))
					+ "_" + SEDMLUtil.getName(repeatedTask));
			SimulationOwner simOwner = vcSimulations.get(baseTask.getId()).getSimulationOwner();
			if (!(simOwner instanceof SimulationContext)) throw new RuntimeException("Unexpected sim owner");
			SimulationContext simContext = (SimulationContext) simOwner;
			vcSimulations.put(repeatedTask.getId(), simulation);
			simContext.getBioModel().addSimulation(simulation); // since we cloned, we don't need another sim context

			 */

			simulation.setImportedTaskID(abstractedRepeatedTask.getIdAsString());

			//TODO: Need version info on new sim!!!
			SId targetId;
			SimulationOwner simOwner = vcSimulations.get(baseTask.getId()).getSimulationOwner();
			if (!(simOwner instanceof SimulationContext simContext)) throw new RuntimeException("Unexpected sim owner");
            // We need to determine if the 'base simulation' is needed for an output
			// if we don't we should remove the entry in the map.
			if (this.simulationIsNeededAsOutput(vcSimulations.get(baseTask.getId()))){
				targetId = repeatedTask.getId();
                SedBase foundBase = this.sedmlContainer.getSedML().searchInSimulationsFor(baseTask.getSimulationReference());
                if (!(foundBase instanceof org.jlibsedml.components.simulation.Simulation sedmlSim)) throw new RuntimeException("Unexpected non-simulation");
				simulation.setName(SedMLUtil.getName(sedmlSim) + "_" + SedMLUtil.getName(repeatedTask));
			} else {
				targetId = baseTask.getId();
				simContext.getBioModel().removeSimulation(vcSimulations.get(baseTask.getId()));
				vcSimulations.remove(baseTask.getId());
			}
			this.createOverrides(simulation, functions);
			vcSimulations.put(targetId, simulation);
			simContext.getBioModel().addSimulation(simulation); // since we cloned, we don't need another sim context


		} // end for loop
	}

	// We need to process biomodels that may depend on other biomodels, either with changes or with references!
	private Map<SId, BioModel> createBioModels(List<Model> models) {
		final String MODEL_RESOLUTION_ERROR = "Unresolvable Model(s) encountered. Either there is incompatible " +
				"/ unsupported SED-ML features, or there are unresolvable references.";

		// Order which type of models to process first based on least-to-greatest priority
		Map<SId, BioModel> idToBiomodelMap = new HashMap<>();
		List<Model> basicModels = new LinkedList<>(); // No overrides, no references. These come first!
		LinkedList<Queue<Model>> advancedModelsList = new LinkedList<>(); // works as both queue and list!

		// Initialize the advanced models list (effectively a "2D-Array")
		for (ADVANCED_MODEL_TYPES amt : ADVANCED_MODEL_TYPES.values()){
            logger.trace("Initializing {}", amt.toString());
			advancedModelsList.add(new LinkedList<>());
		}
		
		// Group models by type for processing order
		for (Model model : models){
			if (model.getSourceAsString().startsWith("#")){
				advancedModelsList.get(ADVANCED_MODEL_TYPES.REFERENCING_MODELS.ordinal()).add(model);
			} else if (!model.getChanges().isEmpty()) {
				advancedModelsList.get(ADVANCED_MODEL_TYPES.CHANGED_MODELS.ordinal()).add(model);
			} else {
				basicModels.add(model);
			}
		}

		// Streamline advanced models
		for (int i = 0; i < advancedModelsList.size(); i++){
			if (advancedModelsList.get(i).isEmpty())
				advancedModelsList.remove(i--); // have to adjust the iterator
		}

		// Process basic models
		for (Model model : basicModels){
			SId referenceId = SedMLImporter.getSedMLReferenceId(model);
			idToBiomodelMap.put(model.getId(), this.getModelReference(referenceId, model, idToBiomodelMap));
		}

		// Process advanced models
		while(!advancedModelsList.isEmpty()){
			int count = 0;
			Queue<Model> advancedModels = advancedModelsList.remove();
			StringBuilder errorsReported = new StringBuilder();
			while (!advancedModels.isEmpty()){
				// If we're unable to resolve the current set of models, put them into the next set.
				if (count >= advancedModels.size()){
					if (!advancedModelsList.isEmpty()) {
						advancedModelsList.element().addAll(advancedModels); // Move this set to the next set to process
						break;
					} else {
						String message = String.format("Errors reported: %s", errorsReported);
						Exception errorFilledException = new Exception(message);
						throw new RuntimeException(MODEL_RESOLUTION_ERROR + " " + message, errorFilledException);
					}
				}

				// Try and process the model
				Model nextModel = advancedModels.remove();
				SId referenceId = SedMLImporter.getSedMLReferenceId(nextModel);
				BioModel bioModel;
				try {
					bioModel = this.getModelReference(referenceId, nextModel, idToBiomodelMap);
					idToBiomodelMap.put(nextModel.getId(), bioModel);
					count = 0;
				} catch (RuntimeException e){
					Tracer.failure(e, "Error processing model: " + nextModel.getId() + " - " + e.getMessage());
					errorsReported.append(e.getMessage()).append("\n");
					advancedModels.add(nextModel);
					count++;
				}
			}
		}
		
		return idToBiomodelMap;
	}

	private static SId getSedMLReferenceId(Model model){
		String referenceId = model.getSourceAsString();
		return referenceId.startsWith("#") ? new SId(referenceId.substring(1)) : null;
	}

	private BioModel getModelReference(SId sedmlReference, Model model, Map<SId, BioModel> idToBiomodelMap) {
		// Were we given a reference ID? We need to check if the parent was processed yet.
        if (sedmlReference == null) return this.importModel(model);
        boolean canTranslate;
        BioModel parentBiomodel = idToBiomodelMap.get(sedmlReference);

        if (parentBiomodel == null) throw new RuntimeException("The parent hasn't been processed yet!");

        canTranslate = this.canTranslateToOverrides(parentBiomodel, model);
        if (canTranslate) return parentBiomodel;

		return this.importModel(model);
	}

	private boolean canTranslateToOverrides(BioModel refBM, Model mm) {
		List<Change> changes = mm.getChanges();
		// XML changes can't be translated to math overrides, only attribute changes and compute changes can
		for (Change change : changes) {
			if (change.isAddXML() || change.isChangeXML() || change.isRemoveXML()) return false;			
		}
		// check whether all targets have addressable Constants on the Math side
		for (Change change : changes) {
			String sbmlID = this.sbmlSupport.getIdFromXPathIdentifer(change.getTargetXPath().toString());
			SimulationContext simulationContext = refBM.getSimulationContext(0);
			Variable vcVar = this.resolveMathVariable(simulationContext, null, sbmlID);
			if (this.isMathVariableNonConstantValued(vcVar)) {
				logger.warn("mapped changeAttribute for SBML ID "+sbmlID+" mapped to non-constant VCell variable '"+vcVar.getName()+"'");
				return false;
			}
		}
		return true;
	}

	private BioModel importModel(Model mm) {
		BioModel bioModel;
		SId sedmlOriginalModelId = mm.getId();
		String sedmlOriginalModelLanguage = mm.getLanguage();
		String modelXML = this.resolver.getXMLStringRepresentationOfModel(mm); // source with all the changes applied
		if (modelXML == null) {
			throw new RuntimeException("Resolver could not find model: "+mm.getId());
		}
		String bioModelName = this.bioModelBaseName + "_" + this.sedmlContainer.getFileName() + "_" + sedmlOriginalModelId.string();
		try {
			if(sedmlOriginalModelLanguage.contentEquals(SUPPORTED_LANGUAGE.VCELL_GENERIC.getURN())) {	// vcml
				XMLSource vcmlSource = new XMLSource(modelXML);
				bioModel = XmlHelper.XMLToBioModel(vcmlSource);
				bioModel.setName(bioModelName);
				try {
					bioModel.getVCMetaData().createBioPaxObjects(bioModel);
				} catch (Exception e) {
					logger.error("failed to make BioPax objects", e);
				}
			} else {				// we assume it's sbml, if it's neither import will fail
				InputStream sbmlSource = IOUtils.toInputStream(modelXML, Charset.defaultCharset());
				boolean bValidateSBML = false;
				SBMLImporter sbmlImporter = new SBMLImporter(sbmlSource, this.transLogger, bValidateSBML);
				bioModel = sbmlImporter.getBioModel();
				bioModel.setName(bioModelName);
				bioModel.getSimulationContext(0).setName(mm.getName() != null? mm.getName() : mm.getIdAsString());
				bioModel.updateAll(false);
				this.importMap.put(bioModel, sbmlImporter);
			}
			return bioModel;
		} catch (PropertyVetoException e){
			String message = "PropertyVetoException occurred: " + e.getMessage();
			throw new RuntimeException(message, e);
		} catch (XmlParseException e){
			String message = "XmlParseException occurred: " + e.getMessage();
			throw new RuntimeException(message, e);
		} catch (VCLoggerException e){
			String message = "VCLoggerException occurred: " + e.getMessage();
			throw new RuntimeException(message, e);
		} catch (MappingException e){
			String message = "MappingException occurred: " + e.getMessage();
			throw new RuntimeException(message, e);
		} catch (IllegalArgumentException e) {
			String message = "IllegalArgumentException occurred: " + e.getMessage();
			throw new RuntimeException(message, e);
		} catch (Exception e) {
			logger.error("Unknown error occurred:" + e.getMessage());
			throw e;
		}
	}

	private void printSEDMLSummary(List<Model> mmm, List<org.jlibsedml.components.simulation.Simulation> sss,
                                   List<AbstractTask> ttt, List<DataGenerator> ddd, List<Output> ooo) {
		for(Model mm : mmm) {
		    logger.trace("sedml model: "+mm.toString());
		    List<Change> listOfChanges = mm.getChanges();
		    logger.debug("There are " + listOfChanges.size() + " changes in model "+mm.getId());
		}
		for(org.jlibsedml.components.simulation.Simulation ss : sss) {
		    logger.trace("sedml simulation: "+ss.toString());
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

	private void translateTimeBounds(SolverTaskDescription simTaskDesc, org.jlibsedml.components.simulation.Simulation sedmlSimulation) throws PropertyVetoException {
		TimeBounds timeBounds;
		TimeStep timeStep = new TimeStep();
		double outputTimeStep; // = 0.1;
		int outputNumberOfPoints; // = 1;
		// we translate initial time to zero, we provide output for the duration of the simulation
		// because we can't select just an interval the way the SEDML simulation can
		double initialTime = ((UniformTimeCourse) sedmlSimulation).getInitialTime();
		double outputStartTime = ((UniformTimeCourse) sedmlSimulation).getOutputStartTime();
		double outputEndTime = ((UniformTimeCourse) sedmlSimulation).getOutputEndTime();
		outputNumberOfPoints = ((UniformTimeCourse) sedmlSimulation).getNumberOfSteps();
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
	
	private void translateAlgorithmParams(SolverTaskDescription simTaskDesc, org.jlibsedml.components.simulation.Simulation sedmlSimulation) throws PropertyVetoException {
		TimeStep timeStep = simTaskDesc.getTimeStep();
		Algorithm algorithm = sedmlSimulation.getAlgorithm();
		String kisaoID = algorithm.getKisaoID();
		ErrorTolerance errorTolerance = new ErrorTolerance();
		List<AlgorithmParameter> sedmlAlgorithmParameters = algorithm.getAlgorithmParameters();
		for (AlgorithmParameter sedmlAlgorithmParameter : sedmlAlgorithmParameters) {

			String apKisaoID = sedmlAlgorithmParameter.getKisaoID();
			String apValue = sedmlAlgorithmParameter.getValue();
			if (apKisaoID == null) {
				throw new RuntimeException("Null KisaoID algorithm parameter for algorithm '" + kisaoID + "'");
			} else if (apKisaoID.isEmpty()){
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
				if (simTaskDesc.getSolverDescription() == SolverDescription.ForwardEuler) {
					timeStep.setDefaultTimeStep(value/10);
				}else{
					timeStep.setDefaultTimeStep(value);
				}
			} else if(apKisaoID.contentEquals(TimeStep.TimeStepDescription.Maximum.getKisao())) {
				double value = Double.parseDouble(apValue);
				timeStep.setMaximumTimeStep(value);
			} else if(apKisaoID.contentEquals(TimeStep.TimeStepDescription.Minimum.getKisao())) {
				double value = Double.parseDouble(apValue);
				timeStep.setMinimumTimeStep(value);
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.PDEMeshSize.getKisao())) {
				//
				// temporary measure to encode meshSize (abuses KISAO_0000326)
				//
				ISize value = ISize.fromTemporaryKISAOvalue(apValue);
				simTaskDesc.getSimulation().getMeshSpecification().setSamplingSize(value);
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.Seed.getKisao())) {		// custom seed
				if(simTaskDesc.getSimulation().getMathDescription().isNonSpatialStoch()) {
					NonspatialStochSimOptions nonspatialSSO = simTaskDesc.getStochOpt();
					int value = Integer.parseInt(apValue);
					nonspatialSSO.setCustomSeed(value);
				} else {
					logger.error("Algorithm parameter '" + AlgorithmParameterDescription.Seed.getDescription() +"' is only supported for nonspatial stochastic simulations");
				}
				// some arguments used only for non-spatial hybrid solvers
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.Epsilon.getKisao())) {
				NonspatialStochHybridOptions nonspatialSHO = simTaskDesc.getStochHybridOpt();
				nonspatialSHO.setEpsilon(Double.parseDouble(apValue));
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.Lambda.getKisao())) {
				NonspatialStochHybridOptions nonspatialSHO = simTaskDesc.getStochHybridOpt();
				nonspatialSHO.setLambda(Double.parseDouble(apValue));
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.MSRTolerance.getKisao())) {
				NonspatialStochHybridOptions nonspatialSHO = simTaskDesc.getStochHybridOpt();
				nonspatialSHO.setMSRTolerance(Double.parseDouble(apValue));
			} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.SDETolerance.getKisao())) {
				NonspatialStochHybridOptions nonspatialSHO = simTaskDesc.getStochHybridOpt();
				nonspatialSHO.setSDETolerance(Double.parseDouble(apValue));
			} else {
				logger.warn("Algorithm parameter with kisao id '" + apKisaoID + "' not supported at this time, skipping.");
			}
		}
		simTaskDesc.setErrorTolerance(errorTolerance);
	}

	private Task getBaseTask(RepeatedTask repeatedTask){
		// Can we not have multiple base Tasks? If we have multiple sub-tasks,
		// then couldn't there be more than one base Task?
		SubTask st = repeatedTask.getSubTasks().iterator().next(); // single subtask
		SId taskId = st.getTask();

		// find the base-task, by recursively checking the task being referenced until it's not a repeated task
        SedBase sedmlElement = this.sedmlContainer.getSedML().searchInTasksFor(taskId);
        if (sedmlElement instanceof Task basicTask) return basicTask;
        if (sedmlElement instanceof RepeatedTask referredTask) return this.getBaseTask(referredTask);
        throw new RuntimeException("Unexpected non-Task");
	}

	private boolean simulationIsNeededAsOutput(Simulation sim){
		// We need to do a bit of digging to get to the bottom of this: if our simulation isn't connected to an output
		// we do not need it for output purposes. This is important in the import step, as we get all types of models.
        SedML sedml = this.sedmlContainer.getSedML();

        List<Report> reportList = sedml.getOutputs().stream().filter(Report.class::isInstance).map(Report.class::cast).toList();

		for (Report report : reportList){
			Set<SId> neededTaskReferences = new HashSet<>();
			for (DataSet ds : report.getDataSets()){
				for (DataGenerator dataGenerator : sedml.getDataGenerators()){
					if (!ds.getDataReference().equals(dataGenerator.getId())) continue;
					for (org.jlibsedml.components.Variable var : dataGenerator.getVariables()){
						neededTaskReferences.add(var.getTaskReference());
					}
				}
			}

			for (AbstractTask task : sedml.getTasks()){
				if (neededTaskReferences.contains(task.getId()) && task.getIdAsString().equals(sim.getImportedTaskID())) return true;
			}

		}
		// We couldn't find the imported sim ID in the list of needed reports. VCell probably doesn't need it directly.
		return false;
	}

	public SBMLSymbolMapping getSBMLSymbolMapping(BioModel bioModel){
		SBMLImporter sbmlImporter = this.importMap.get(bioModel);
        return (sbmlImporter != null) ? sbmlImporter.getSymbolMapping() : null;
	}

	private enum ADVANCED_MODEL_TYPES {
		CHANGED_MODELS, REFERENCING_MODELS
	}
}
