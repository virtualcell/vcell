package cbit.vcell.solver.stoch;

import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverExecutable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Each Hybrid integrator runs its own executable. All three used to resolve through HybridEuler, so
 * Milstein and adaptive Milstein silently ran Hybrid_EM.
 */
@Tag("Fast")
public class HybridSolverExecutableTest {

	private static String exeName(int integratorType) {
		SolverDescription sd = HybridSolver.solverDescriptionFor(integratorType);
		SolverExecutable.NameInfo[] nameInfos = sd.getSolverExecutable().getNameInfo();
		assertEquals(1, nameInfos.length);
		return nameInfos[0].exeName;
	}

	@Test
	public void eachIntegratorResolvesItsOwnExecutable() {
		assertEquals(SolverDescription.HybridEuler, HybridSolver.solverDescriptionFor(HybridSolver.EMIntegrator));
		assertEquals(SolverDescription.HybridMilstein, HybridSolver.solverDescriptionFor(HybridSolver.MilsteinIntegrator));
		assertEquals(SolverDescription.HybridMilAdaptive, HybridSolver.solverDescriptionFor(HybridSolver.AdaptiveMilsteinIntegrator));

		assertEquals("Hybrid_EM", exeName(HybridSolver.EMIntegrator));
		assertEquals("Hybrid_MIL", exeName(HybridSolver.MilsteinIntegrator));
		assertEquals("Hybrid_MIL_Adaptive", exeName(HybridSolver.AdaptiveMilsteinIntegrator));
	}
}
