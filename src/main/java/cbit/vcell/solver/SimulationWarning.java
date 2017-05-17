package cbit.vcell.solver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.log4j.Logger;
import org.vcell.solver.smoldyn.SmoldynSurfaceTessellator;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.Severity;
import org.vcell.util.IssueContext;
import org.vcell.util.ThreadPrioritySetter;
import org.vcell.util.VCAssert;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.surface.Triangle;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.SubDomain;
import cbit.vcell.parser.Expression;
import cbit.vcell.resource.VCellExecutorService;
import edu.uchc.connjur.wb.ExecutionTrace;

/**
 * Implementation of simulation warnings
 */
class SimulationWarning implements PropertyChangeListener {
	// implement as separate class to facilate implementing in multiple branches
	private final Simulation simulation;
	private MathDescriptionAnalysisStrategy changeStrategy;
	private SolverDescription currentSolverDescription;

	private final static MathDescriptionAnalysisStrategy DEFAULT_STRATEGY = new DefaultMathDescriptionAnalysisStrategy();
	private static Logger lg = Logger.getLogger(SimulationWarning.class);

	protected SimulationWarning(Simulation simulation) {
		super();
		Objects.requireNonNull(simulation);
		this.simulation = simulation;
		changeStrategy = DEFAULT_STRATEGY;
		currentSolverDescription = null;
		simulation.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		VCAssert.assertTrue(evt.getSource() == simulation, "Wrong event source");
		String n = evt.getPropertyName();
		if (n == Simulation.PROPERTY_NAME_SOLVER_TASK_DESCRIPTION
				&& evt.getOldValue() != evt.getNewValue()) {
			SolverTaskDescription std = simulation.getSolverTaskDescription();
			if (std != null) {
				SolverDescription sd = std.getSolverDescription();
				if (sd != currentSolverDescription) {
					currentSolverDescription = sd;
					changeStrategy = changeStrategyFactory(currentSolverDescription);
					if (lg.isDebugEnabled()) {
						lg.debug("Using strategy " + changeStrategy.toString());
					}
				}
			}
			else {
				changeStrategy = DEFAULT_STRATEGY;
			}
			changeStrategy.solverTaskDescriptionChange();
		}
		if (n == Simulation.PROPERTY_NAME_MATH_DESCRIPTION) {
			Object nv = evt.getNewValue();
			if (nv != null) {
				VCAssert.assertTrue(nv instanceof MathDescription, Simulation.PROPERTY_NAME_MATH_DESCRIPTION + " has object type " + ExecutionTrace.justClassName(nv));
				MathDescription md = (MathDescription) nv;
				mathDescriptionChange(md);
			}
		}
	}

	/**
	 * forward to current {@link #changeStrategy}
	 *
	 * @param md
	 */
	void mathDescriptionChange(MathDescription md) {
		Objects.requireNonNull(md);
		changeStrategy.mathDescriptionChange(md);
	}

	/**
	 * forward to current {@link #changeStrategy}
	 *
	 * @param issueContext
	 * @param issueList
	 */
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		changeStrategy.gatherIssues(issueContext, issueList);
	}

	/**
	 *
	 * @param sd
	 *            non null
	 * @return {@link MathDescriptionAnalysisStrategy} appropriate for sd
	 */
	private MathDescriptionAnalysisStrategy changeStrategyFactory(SolverDescription sd) {
		Objects.requireNonNull(sd);
		switch (sd) {
		case Smoldyn:
			return new SmoldynChangeStrategy();
		default:
			return DEFAULT_STRATEGY;
		}
	}

	private interface MathDescriptionAnalysisStrategy {

		void mathDescriptionChange(MathDescription md);
		void solverTaskDescriptionChange( );

		void gatherIssues(IssueContext issueContext, List<Issue> issueList);

	}

	private static class DefaultMathDescriptionAnalysisStrategy implements MathDescriptionAnalysisStrategy {
		@Override
		public void mathDescriptionChange(MathDescription md) { }

		@Override
		public void gatherIssues(IssueContext issueContext, List<Issue> issueList) { }

		@Override
		public void solverTaskDescriptionChange() { }
	}

	private class SmoldynChangeStrategy implements MathDescriptionAnalysisStrategy {
		/**
		 * Empirically determined adjustment factor to make warning based on mesh sizing more conservative than that determined
		 * by actual tesellation
		 */
		private static final double PRECHECK_LIMIT_ADJUST = 0.6;

		/**
		 * means diffusion analysis needs to occur if true
		 */
		private final AtomicBoolean dirty;

		private ScheduledFuture<?> analyzer;
		/**
		 * multi-threaded accesses guarded by synchronized({@link #errors}
		 */
		private final List<String> errors;
		/**
		 * multi-threaded accesses also guarded by synchronized({@link #errors}
		 */
		private final List<String> warnings;
		/**
		 * store {@link MembraneSubDomain}s and their diffusion values
		 */
		private final Map<MembraneSubDomain, List<DiffusionValue>> diffusionValuesMap;
		private final AtomicReference<MathDescription> lastMath;
		private DecimalFormat formatter;

		SmoldynChangeStrategy() {
			dirty = new AtomicBoolean(false);
			analyzer = null;
			errors = new ArrayList<>();
			warnings = Collections.synchronizedList(new ArrayList<>());
			diffusionValuesMap = Collections.synchronizedMap(new IdentityHashMap<>());
			lastMath = new AtomicReference<>();
			formatter = null;
		}

		/**
		 * cancel any prior scheduled invocation, run {@link #analyzeArea()}
		 * after specified time
		 */
		private void sched() {
			ScheduledFuture<?> prior = analyzer;
			analyzer = VCellExecutorService.get().schedule(() -> analyzeArea(), 1, TimeUnit.SECONDS);
			if (prior != null) {
				prior.cancel(true);
			}
		}

		@Override
		public void solverTaskDescriptionChange() {
			lastMath.set(null);
			mathDescriptionChange(simulation.getMathDescription());
		}

		@Override
		public void mathDescriptionChange(MathDescription md) {
			if (lastMath.get() == md) {
				return;
			}
			synchronized (errors) {
				errors.clear();
				warnings.clear();
			}
			analyzeDiffusion();
			lastMath.set(md);
			if (dirty.get()) {
				return;
			}
			dirty.set(true);
			sched();
		}

		/**
		 * @return time step of simulation
		 */
		private double getTimeStep() {
			SolverTaskDescription std = simulation.getSolverTaskDescription();
			TimeStep ts = std.getTimeStep();
			double timeStep = ts.getDefaultTimeStep();
			return timeStep;
		}

		/**
		 * make sure diffusion expressions are constants, store for later use
		 */
		void analyzeDiffusion() {
			try {
				diffusionValuesMap.clear();
				MutableDouble value = new MutableDouble();
				MathDescription cm = simulation.getMathDescription();
				Objects.requireNonNull(cm);
				MathDescription localMath = new MathDescription(cm);
				SimulationSymbolTable symTable = new SimulationSymbolTable(simulation, -999);
				Map<MembraneSubDomain, List<DiffusionValue>> dvMap = new HashMap<>();
				double maxDiffValue = Double.MIN_VALUE;
				List<DiffusionValue> diffusionList = new ArrayList<>();
				synchronized (errors) {
					for (SubDomain sd : localMath.getSubDomainCollection()) {
						final boolean isMembrane = sd instanceof MembraneSubDomain;
						diffusionList.clear();
						for (ParticleProperties pp : sd.getParticleProperties()) {
							String name = pp.getVariable().getName();
							Expression diffExp = pp.getDiffusion();
							Expression flattened = MathUtilities.substituteFunctions(diffExp, symTable).flatten();
							if (isConstant(flattened, value)) {
								if (isMembrane) {
									DiffusionValue dv = new DiffusionValue(name, value.doubleValue());
									maxDiffValue = Math.max(maxDiffValue, dv.value);
									diffusionList.add(dv);
									if (lg.isDebugEnabled()) {
										lg.debug("Added " + dv);
									}
								}
							} else {
								String s = "Smoldyn only supports constant diffusion, " + name + " is variable";
								lg.debug(s);
								errors.add(s);
							}
						}
						if (isMembrane && !diffusionList.isEmpty()) {
							dvMap.put((MembraneSubDomain) sd, diffusionList);
						}
					}
					if (errors.isEmpty()) { // don't bother with warnings unless
											// diffusions are constants
						diffusionValuesMap.putAll(dvMap);
						/*
						 * How about adding a �pre-run� message �Time step ___
						 * may be too large, please check warnings generated
						 * during/after the run � (or something like that)? The
						 * message would be generated if delta_t>[min(delta_x,
						 * delta_y, delta_z,)]^2/max(D_1, D_2, �), where D_1,
						 * D_2, � are diffusion coefficients of the membrane
						 * variables.
						 *
						 */
						MeshSpecification ms = simulation.getMeshSpecification();
						Geometry g = ms.getGeometry();
						int dim = g.getDimension();
						double minDelta = Double.MAX_VALUE;
						switch (dim) {
						case 3:
							minDelta = Math.min(minDelta, ms.getDz(true));
							// fall-through
						case 2:
							minDelta = Math.min(minDelta, ms.getDy(true));
							// fall-through
						case 1:
							minDelta = Math.min(minDelta, ms.getDx(true));
							break;
						default:
							throw new RuntimeException("Invalid dimension " + dim + " for smoldyn solver");
						}
						double minArea = minDelta * minDelta / 2;
						double limit = PRECHECK_LIMIT_ADJUST * minArea / maxDiffValue;
						double timeStep = getTimeStep();
						boolean warn = (timeStep > limit);
						if (lg.isDebugEnabled()) {
							lg.debug("Min delta " + minDelta + ", min area " + minArea + " time limit " + limit + " timeStep " + timeStep + " -> warn = " + warn);
						}
						if (warn) {
							String s = "Time step " + timeStep + " may be too large, performing further analysis ...";
							lg.debug(s);
							warnings.add(s);
						}
					}
				}
				lg.debug("end of diffusion analysis");
			} catch (Exception e) {
				lg.warn("analysis diffusion exception", e);
			}
		}

		void analyzeArea() {
			boolean bDirty = dirty.get();
			lg.debug("analyzeArea( ) with dirty " + bDirty);
			if (bDirty) {
				dirty.set(false);
				sched();
				return;
			}
			List<String> workingList = new ArrayList<>();
			try (ThreadPrioritySetter tps = new ThreadPrioritySetter(Thread.MIN_PRIORITY)) {
				double timeStep = getTimeStep();
				SmoldynSurfaceTessellator sst = new SmoldynSurfaceTessellator(simulation);
				for (Entry<MembraneSubDomain, List<DiffusionValue>> entry : diffusionValuesMap.entrySet()) {
					MembraneSubDomain msd = entry.getKey();
					List<Triangle> tessellation = sst.getTessellation(msd.getName());
					/*
					 * performance note -- for a test case, the parallel stream was slightly faster than sequential; the traditional
					 * loop was slightly faster than either stream; less than a factor of 3 and on the order of 3 milliseconds
					 */
					double sum = tessellation.parallelStream().mapToDouble( (t) -> t.getArea()).sum( );
					double avg = sum / tessellation.size();
					for (DiffusionValue dv : entry.getValue()) {
						double ad = avg / dv.value;
						boolean warn = (timeStep >= ad);
						if (lg.isDebugEnabled()) {
							lg.debug("average area " + avg + " diffusion " + dv.value + " time step limit " + ad + "timeStep " + timeStep + " -> warn " + warn);
						}
						if (warn) {
							DecimalFormat f = formatter();
							String limit = f.format(ad);
							String m = "Solver time step " + timeStep
									+ " may yield inaccurate results for given mesh size and diffusion coefficient of species "
									+ dv.name + " on membrane " + msd.getName()
									+ "; it is recommended that the time step not exceed " + limit;
							workingList.add(m);
							if (lg.isDebugEnabled()) {
								lg.debug("Warning " + m);
							}
						}
					}
				}
				synchronized (errors) { // replace prelim warnings with more
										// detailed
					warnings.clear();
					warnings.addAll(workingList);
				}

				lg.debug("end of area analysis");
			} catch (Exception e) {
				lg.warn("analysis exception", e);
			}
		}

		/**
		 * determine if constant
		 *
		 * @param e
		 *            non null
		 * @param value
		 *            out parameter
		 * @return true if constant and value via parameter
		 */
		boolean isConstant(Expression e, MutableDouble value) {
			try {
				double d = e.evaluateConstant();
				value.setValue(d);
			} catch (Exception exc) {
				return false;
			}
			return true;
		}

		/**
		 * @return lazily created formatter
		 */
		DecimalFormat formatter() {
			if (formatter == null) {
				formatter = new DecimalFormat("0.##E0");
			}
			return formatter;
		}

		@Override
		public String toString() {
			return "Smoldyn change strategy";
		}

		/**
		 * transfer {@link #warnings} to issueList
		 *
		 * @param issueContext
		 * @param issueList
		 *            ask for more time if dirty
		 */
		@Override
		public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
			if (dirty.get()) {
				issueContext.needMoreTime();
				lg.debug("Smoldyn strategy requesting more time to gather issues");
			}
			lg.debug("Smoldyn strategy gathering issues");
			Objects.requireNonNull(issueList);
			synchronized (errors) {
				for (String text : errors) {
					Issue i = new Issue(simulation, issueContext, IssueCategory.SMOLYDN_DIFFUSION, text,text,Severity.ERROR);
					issueList.add(i);
				}
				for (String text : warnings) {
					Issue i = new Issue(simulation, issueContext, IssueCategory.SMOLYDN_DIFFUSION, text,text,Severity.WARNING);
					issueList.add(i);
				}
			}
		}
	}

	/**
	 * variable names and their diffusion values
	 */
	private static class DiffusionValue {
		final String name;
		final double value;

		DiffusionValue(String name, double value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public String toString() {
			return "DiffusionValue [name=" + name + ", value=" + value + "]";
		}
	}
}
