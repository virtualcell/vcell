package cbit.vcell.solver;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.Version;
import static cbit.vcell.solver.SolverTaskDescription.TrialIndex;
import static cbit.vcell.solver.MathOverrides.ScanIndex;

import java.beans.PropertyVetoException;

public class SimulationTest {

    private Simulation createSimulation(int _scanCount, int numTrails) throws PropertyVetoException {
        if (_scanCount > 1) {
            throw new RuntimeException("scanCount > 1 is not yet implemented in this text fixture");
        }
        MathModel mathModel = new MathModel(null);
        MathDescription mathDescription = new MathDescription("dummy");
        Geometry geometry = new Geometry((Version)null, 0);
        mathDescription.setGeometry(geometry);
        mathModel.setMathDescription(mathDescription);
        Simulation sim = new Simulation(mathDescription, mathModel);
        SolverTaskDescription solverTaskDescription = new SolverTaskDescription(sim);
        solverTaskDescription.setNumTrials(numTrails);
        sim.setSolverTaskDescription(solverTaskDescription);
        return sim;
    }

    @Test
    public void testSimulationInstanceIndexing() throws PropertyVetoException {
        final Simulation sim_1_1 = createSimulation(1, 1);
        Assertions.assertEquals(1, sim_1_1.getNumJobs());
        Assertions.assertEquals(1, sim_1_1.getScanCount());
        Assertions.assertEquals(1, sim_1_1.getNumTrials());
        Assertions.assertEquals(0, sim_1_1.getJobIndex(ScanIndex.ZERO, TrialIndex.ZERO));

        Assertions.assertEquals(new ScanIndex(0), sim_1_1.getScanIndex(0));
        Assertions.assertEquals(new TrialIndex(0), sim_1_1.getTrialIndex(0));

        // trialIndex out of bounds
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                sim_1_1.getJobIndex(new ScanIndex(0), new TrialIndex(1)));
        // scanIndex out of bounds
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                sim_1_1.getJobIndex(new ScanIndex(1), new TrialIndex(0)));

        final Simulation sim_1_3 = createSimulation(1, 3);
        Assertions.assertEquals(3, sim_1_3.getNumJobs());
        Assertions.assertEquals(3, sim_1_3.getNumTrials());
        Assertions.assertEquals(3, sim_1_3.getNumJobs());
        Assertions.assertEquals(1, sim_1_3.getScanCount());

        Assertions.assertEquals(1, sim_1_1.getNumJobs());
        Assertions.assertEquals(3, sim_1_3.getNumJobs());

        Assertions.assertEquals(new ScanIndex(0), sim_1_3.getScanIndex(0));
        Assertions.assertEquals(new TrialIndex(0), sim_1_3.getTrialIndex(0));
        Assertions.assertEquals(0, sim_1_3.getJobIndex(new ScanIndex(0), new TrialIndex(0)));

        Assertions.assertEquals(new ScanIndex(0), sim_1_3.getScanIndex(1));
        Assertions.assertEquals(new TrialIndex(1), sim_1_3.getTrialIndex(1));
        Assertions.assertEquals(1, sim_1_3.getJobIndex(new ScanIndex(0), new TrialIndex(1)));

        Assertions.assertEquals(new ScanIndex(0), sim_1_3.getScanIndex(2));
        Assertions.assertEquals(new TrialIndex(2), sim_1_3.getTrialIndex(2));
        Assertions.assertEquals(2, sim_1_3.getJobIndex(new ScanIndex(0), new TrialIndex(2)));
    }

    @Test
    public void testStaticSimulationIndexing() throws PropertyVetoException {
        int[] trialCounts = {1, 2, 3, 4, 5};
        int[] scanCounts = {1, 2, 3, 4, 5};

        for (int numTrials : trialCounts) {
            for (int numScans : scanCounts) {
                Assertions.assertEquals(numTrials*numScans, Simulation.getNumJobs(numTrials, numScans));
                for (int jobIndex = 0; jobIndex < numTrials*numScans; jobIndex++) {
                    ScanIndex scanIndex = Simulation.getScanIndex(jobIndex, numScans);
                    TrialIndex trialIndex = Simulation.getTrialIndex(jobIndex, numScans);
                    int computedJobIndex = Simulation.getJobIndex(scanIndex, trialIndex, numScans);
                    Assertions.assertEquals(jobIndex, computedJobIndex);
                }
                for (int scan = 0; scan < numScans; scan++) {
                    for (int trial = 0; trial < numTrials; trial++) {
                        ScanIndex scanIndex = new ScanIndex(scan);
                        TrialIndex trialIndex = new TrialIndex(trial);
                        int jobIndex = Simulation.getJobIndex(scanIndex, trialIndex, numScans);
                        Assertions.assertEquals(scanIndex, Simulation.getScanIndex(jobIndex, numScans));
                        Assertions.assertEquals(trialIndex, Simulation.getTrialIndex(jobIndex, numScans));
                    }
                }
            }
        }
    }

}
