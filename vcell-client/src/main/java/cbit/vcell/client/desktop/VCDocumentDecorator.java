package cbit.vcell.client.desktop;

import java.awt.Window;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.vcell.util.BeanUtils;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;
import org.vcell.util.ProgrammingException;
import org.vcell.util.document.VCDocument;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.QuickFixSimulation.CloseAction;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverDescription.SolverFeature;
import cbit.vcell.solver.SolverTaskDescription;

public abstract class VCDocumentDecorator {

	/**
	 * @param issueContext not null
	 * @param issueList destination for Issues
	 */
	public abstract void gatherIssues(IssueContext issueContext, List<Issue> issueList);


	/**
	 * get most appropriate decorator for client
	 * @param client not null
	 * @return most appropriate decorator for client
	 */
	public static VCDocumentDecorator getDecorator(VCDocument client) {
		if (client instanceof BioModel) {
			return new BioModelDecorator((BioModel) client);
		}
		if (client instanceof MathModel) {
			return new MathModelDecorator((MathModel) client);
		}
		return new GenericDecorator(client);
	}

	/**
	 * decorator for all VCDocuments without more specific implementation
	 */
	private static class GenericDecorator extends VCDocumentDecorator {
		private final VCDocument document;

		GenericDecorator(VCDocument document) {
			super();
			this.document = document;
		}

		@Override
		public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
			document.gatherIssues(issueContext, issueList);
		}
	}

	private static class BioModelDecorator extends VCDocumentDecorator {
		private final BioModel bioModel;
		/**
		 * see {@link #fetchSimSource(Simulation)}
		 */
		private ArrayList<SimulationSource> simSources;

		BioModelDecorator(BioModel bioModel) {
			super();
			this.bioModel = bioModel;
			simSources = new ArrayList<>();
		}

		@Override
		public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
			bioModel.gatherIssues(issueContext, issueList);
			for (SimulationContext sc : bioModel.getSimulationContexts()) {
				List<SolverFeature> requiredFeatures = buildRequiredFeatures(sc);
				for (Simulation sim : sc.getSimulations()) {
					checkRequired(issueContext, issueList, sim, requiredFeatures);
					sim.gatherIssues(issueContext, issueList);
				}
			}
		}

		/**
		 * model, as best as possible required features based on
		 * current state of BioModel; actual solver features determined by MathDescription
		 * @param sc not null
		 * @return approximation of list of required features
		 */
		private List<SolverFeature> buildRequiredFeatures(SimulationContext sc) {
			ArrayList<SolverFeature> sfList = new ArrayList<>();
			if (sc.getGeometry().getDimension() > 0) {
				sfList.add(SolverFeature.Feature_Spatial);
			}
			else {
				sfList.add(SolverFeature.Feature_NonSpatial);
			}
			switch (sc.getApplicationType()) {
			case NETWORK_DETERMINISTIC:
				sfList.add(SolverFeature.Feature_Deterministic);
				return sfList;
			case RULE_BASED_STOCHASTIC:
				sfList.add(SolverFeature.Feature_Rulebased);
				return sfList;
			case NETWORK_STOCHASTIC:
				//more analysis to determine whether hybrid or (pure) stochastic
			}
			//stochastic could also be hybrid
			boolean continuous = false;
			ReactionContext rc = sc.getReactionContext();
			for (SpeciesContextSpec scs : rc.getSpeciesContextSpecs()) {
				if (scs.isConstant()) {
					continue;
				}
				boolean fc = scs.isForceContinuous();
				continuous = continuous || fc;
				if (continuous) {
					break;
				}
			}
			if (continuous) {
				sfList.add(SolverFeature.Feature_Hybrid);
			}
			else {
				sfList.add(SolverFeature.Feature_Stochastic);
			}
			return sfList;
		}

		/**
		 * checked required features against Solver simulation currently set for,
		 * issue errors if there is mismatch
		 * @param issueContext
		 * @param issueList
		 * @param sim
		 * @param requiredFeatures
		 */
		private void checkRequired(IssueContext issueContext, List<Issue> issueList,Simulation sim, List<SolverFeature> requiredFeatures) {
			SolverDescription solvDesc = sim.getSolverTaskDescription().getSolverDescription();
			Set<SolverFeature> supportedFeatures = solvDesc.getSupportedFeatures();
			Set<SolverFeature> missingFeatures = new HashSet<>(requiredFeatures);
			missingFeatures.removeAll(supportedFeatures);

			String text = "Solver " + solvDesc.getDatabaseName() + " does not support the following required features: \n";
			for (SolverFeature sf : missingFeatures) {
				text += sf.getName() + "\n";
			}

			if (!missingFeatures.isEmpty()) {
				String tooltip = "The selected Solver " + solvDesc.getDisplayLabel() +
						" does not support the following required features: <br>";
				for (SolverFeature sf : missingFeatures) {
					tooltip += "&nbsp;&nbsp;&nbsp;" + sf.getName() + "<br>";
				}
				Collection<SolverDescription> goodSolvers = SolverDescription.getSolverDescriptions(requiredFeatures);
				assert goodSolvers != null;
				if (!goodSolvers.isEmpty()) {
					tooltip += "Please choose one of the solvers : <br>";
					for (SolverDescription sd : goodSolvers) {
						tooltip += "&nbsp;&nbsp;&nbsp;" + sd.getDisplayLabel() + "<br>";
					}
				}

				SimulationSource source = fetchSimSource(sim);
				source.setGoodSolvers(goodSolvers); //remember for if / when activateView is called
				Issue issue = new Issue(source,issueContext, IssueCategory.MathDescription_MathException, text, tooltip, Issue.Severity.ERROR);
				issueList.add(issue);
			}
			if(solvDesc.deprecated) {
				text = "Solver " + solvDesc.getDatabaseName() + " is deprecated";
				Issue issue = new Issue(sim, issueContext, IssueCategory.MathDescription_MathException, text, text, Issue.Severity.WARNING);
				issueList.add(issue);
			}
		}

		/**
		 * @param sim not null
		 * @return new or existing {@link SimulationSource} for sim
		 */
		private SimulationSource fetchSimSource(Simulation sim) {
			//implemented as sequential scan -- hash lookup might be faster
			Objects.requireNonNull(sim);
			for (SimulationSource ss : simSources) {
				if (ss.simulation == sim) {
					return ss;
				}
			}
			SimulationSource s = new SimulationSource(sim, simSources);
			simSources.add(s);
			return s;
		}
	}

	private static class MathModelDecorator extends VCDocumentDecorator {
		private final MathModel mathModel;

		MathModelDecorator(MathModel mathModel) {
			this.mathModel = mathModel;
		}

		@Override
		public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
			mathModel.getSimulationCollection().stream().forEach( (s) -> s.gatherIssues(issueContext, issueList));
		}
	}

	/**
	 * simulation sources, for BioModels only
	 */
	private static class SimulationSource implements DecoratedIssueSource {
		final Simulation simulation;
		/**
		 * cache relationship between {@link SimulationOwner} and {@link Simulation} because
		 * Simulation object may be disconnected from SimuationContext (e.g. if it's deleted)
		 */
		final String pathDesc;
		Collection<SolverDescription> goodSolvers = null;
		final String currentSolver;
		final Collection<SimulationSource> siblings;

		SimulationSource(Simulation simulation, Collection<SimulationSource> siblings) {
			super();
			this.simulation = simulation;
			SimulationOwner owner = simulation.getSimulationOwner();
			pathDesc = "App(" + owner.getName() + ") / Simulations";
			SolverTaskDescription td = simulation.getSolverTaskDescription();
			Objects.requireNonNull(td);
			SolverDescription sd = td.getSolverDescription();
			currentSolver = sd.getDisplayLabel();
			this.siblings = siblings;
		}

		void setGoodSolvers( Collection<SolverDescription> solverDescriptions) {
			this.goodSolvers = solverDescriptions;
		}

		/**
		 * exception safe setSolverDescription
		 * @param sd
		 * @throws ProgrammingException if {@link PropertyVetoException} thrown
		 */
		void setSolverDescription(SolverDescription sd) {
			try {
				SolverTaskDescription td = simulation.getSolverTaskDescription();
				td.setSolverDescription(sd);
			} catch (PropertyVetoException e) {
				throw new ProgrammingException("invalid solver description " + sd.getDisplayLabel() , e);
			}
		}

		@Override
		public void activateView(SelectionManager selectionManager) {
			SimulationContext sc = BeanUtils.downcast(SimulationContext.class, simulation.getSimulationOwner());
			Window activated = null;
			if (sc != null ) {
				ActiveView av = new ActiveView(sc, DocumentEditorTreeFolderClass.SIMULATIONS_NODE, ActiveViewID.simulations);
				selectionManager.followHyperlink(av,simulation);
				activated = av.getActivated();
			}
			if (goodSolvers.size() > 0) {
				final boolean useFixAll = siblings.size() > 1;
				QuickFixSimulation qfs = new QuickFixSimulation(activated,useFixAll,
						currentSolver + " does not support current model. Please select one of the following solvers:",
						goodSolvers);
				qfs.setVisible(true);
				CloseAction ca = qfs.getCloseAction();

				switch (ca) {
				case FIX:
				{
					SolverDescription ss = qfs.getSelectedSolver();
					setSolverDescription(ss);
					break;
				}
				case FIX_ALL:
				{
					SolverDescription ss = qfs.getSelectedSolver();
					for (SimulationSource source : siblings) {
						if (source.goodSolvers.contains(ss)) {
							source.setSolverDescription(ss);
						}
						else if (source.goodSolvers.size() == 1) {
							source.setSolverDescription(source.goodSolvers.iterator( ).next());
						}
					}

					break;
				}
				case CANCEL:

				}
			}
		}

		public String getDescription() {
			return simulation.getName();
		}

		@Override
		public String getSourcePath() {
			return pathDesc;
		}
	}


}
