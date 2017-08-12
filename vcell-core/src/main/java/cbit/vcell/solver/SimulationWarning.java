package cbit.vcell.solver;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.log4j.Logger;
import org.vcell.solver.smoldyn.SmoldynSurfaceTessellator;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.Severity;
import org.vcell.util.IssueContext;
import org.vcell.util.ThreadPrioritySetter;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.surface.Triangle;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.SubDomain;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

/**
 * Implementation of simulation warnings
 */
class SimulationWarning {
	
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
		
	static Logger lg = Logger.getLogger(SimulationWarning.class);
	
	public static void gatherIssues(Simulation simulation, IssueContext issueContext, List<Issue> issueList){
		if (simulation.getSolverTaskDescription().getSolverDescription() == SolverDescription.Smoldyn){
			try {
				SolverTaskDescription std = simulation.getSolverTaskDescription();
				TimeStep ts = std.getTimeStep();
				double timeStep = ts.getDefaultTimeStep();
				Map<MembraneSubDomain, List<DiffusionValue>> diffusionValuesMap = analyzeDiffusion(simulation, timeStep, issueContext, issueList);
				analyzeArea(simulation, timeStep, diffusionValuesMap, issueContext, issueList);
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Empirically determined adjustment factor to make warning based on mesh sizing more conservative than that determined
	 * by actual tesellation
	 */
	private static final double PRECHECK_LIMIT_ADJUST = 0.6;

	/**
	 * make sure diffusion expressions are constants, store for later use
	 * @throws ExpressionException 
	 */
	private static Map<MembraneSubDomain, List<DiffusionValue>> analyzeDiffusion(Simulation simulation, double timeStep, IssueContext issueContext, List<Issue> issueList) throws ExpressionException {

		Map<MembraneSubDomain, List<DiffusionValue>> diffusionValuesMap = new IdentityHashMap<>();

		diffusionValuesMap.clear();
		MutableDouble value = new MutableDouble();
		MathDescription cm = simulation.getMathDescription();
		Objects.requireNonNull(cm);
		MathDescription localMath = new MathDescription(cm);
		SimulationSymbolTable symTable = new SimulationSymbolTable(simulation, -999);
		Map<MembraneSubDomain, List<DiffusionValue>> dvMap = new HashMap<>();
		double maxDiffValue = Double.MIN_VALUE;
		List<DiffusionValue> diffusionList = new ArrayList<>();
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
					}
				} else {
					String s = "Smoldyn only supports constant diffusion, " + name + " is variable";
					Issue i = new Issue(simulation, issueContext, IssueCategory.SMOLYDN_DIFFUSION, s,s,Severity.ERROR);
					issueList.add(i);
				}
			}
			if (isMembrane && !diffusionList.isEmpty()) {
				dvMap.put((MembraneSubDomain) sd, diffusionList);
			}
		}
		diffusionValuesMap.putAll(dvMap);
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
		boolean warn = (timeStep > limit);
		if (lg.isDebugEnabled()) {
			lg.debug("Min delta " + minDelta + ", min area " + minArea + " time limit " + limit + " timeStep " + timeStep + " -> warn = " + warn);
		}
		if (warn) {
			String s = "Time step " + timeStep + " may be too large, performing further analysis ...";
			Issue i = new Issue(simulation, issueContext, IssueCategory.SMOLYDN_DIFFUSION, s,s,Severity.WARNING);
			issueList.add(i);
		}
		lg.debug("end of diffusion analysis");
		return diffusionValuesMap;
	}

	private static void analyzeArea(Simulation simulation, double timeStep, Map<MembraneSubDomain, List<DiffusionValue>> diffusionValuesMap, IssueContext issueContext, List<Issue> issueList) {
		double dx = simulation.getMeshSpecification().getDx(true);
		double dy = simulation.getMeshSpecification().getDy(true);
		double dz = simulation.getMeshSpecification().getDz(true);
		int dim = simulation.getMathDescription().getGeometry().getDimension();
		if (dim<3){
			throw new RuntimeException("suggested timestep analysis for smoldyn not implemented for "+dim+"D simulations");
		}
		double smallestQuadArea = Math.min(Math.min(dx*dy, dy*dz), dx*dz);
		double averageTriangleSize = smallestQuadArea/4;   // 1/2 for quad to triangle, 1/2 for smoother surface
		for (Entry<MembraneSubDomain, List<DiffusionValue>> entry : diffusionValuesMap.entrySet()) {
			MembraneSubDomain msd = entry.getKey();
			for (DiffusionValue dv : entry.getValue()) {
				double ad = averageTriangleSize / dv.value;
				boolean warn = (timeStep >= ad);
				if (lg.isDebugEnabled()) {
					lg.debug("average area " + averageTriangleSize + " diffusion " + dv.value + " time step limit " + ad + "timeStep " + timeStep + " -> warn " + warn);
				}
				if (warn) {
					DecimalFormat f = new DecimalFormat("0.##E0");
					String limit = f.format(ad);
					String m = "Solver time step " + timeStep
							+ " may yield inaccurate results for given mesh size and diffusion coefficient of species "
							+ dv.name + " on membrane " + msd.getName()
							+ "; it is recommended that the time step not exceed " + limit;
					Issue i = new Issue(simulation, issueContext, IssueCategory.SMOLYDN_DIFFUSION, m,m,Severity.WARNING);
					issueList.add(i);
					if (lg.isDebugEnabled()) {
						lg.debug("Warning " + m);
					}
				}
			}
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
	private static boolean isConstant(Expression e, MutableDouble value) {
		try {
			double d = e.evaluateConstant();
			value.setValue(d);
		} catch (Exception exc) {
			return false;
		}
		return true;
	}

}
