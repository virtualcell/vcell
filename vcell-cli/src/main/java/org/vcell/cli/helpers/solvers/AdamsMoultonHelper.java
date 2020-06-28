package org.vcell.cli.helpers.solvers;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ClientSimManager;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverStatus;
import org.vcell.cli.CLIUtils;
import org.vcell.stochtest.TimeSeriesMultitrialData;
import org.vcell.util.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AdamsMoultonHelper {

    public static ODESolverResultSet solve(File outDir, String taskId, BioModel bioModel){
        String docName = bioModel.getName();


        Simulation sim = new TempSimulation(bioModel.getSimulation(0), false);
        SimulationContext simContext = null;
        simContext = bioModel.getSimulationContext(0);

        SimulationJob simJob = new SimulationJob(sim, 0, null);
        SimulationTask simTask = new SimulationTask(simJob, 0);
        Solver newSolver = null;
        try {
            newSolver = ClientSimManager.createQuickRunSolver(outDir, simTask);
        } catch (SolverException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ODESimData odeSimData = null;

        if (newSolver != null) {
            newSolver.startSolver();
        }
        while (true){
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SolverStatus solverStatus = null;
            if (newSolver != null) {
                solverStatus = newSolver.getSolverStatus();
            }
            if (solverStatus != null) {
                if (solverStatus.getStatus() == SolverStatus.SOLVER_ABORTED) {
                    throw new RuntimeException(solverStatus.getSimulationMessage().getDisplayMessage());
                }
                if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
                        solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
                        solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
                    break;
                }
            }
        }
        SimulationData simData = null;
        try {
            simData = new SimulationData(simTask.getSimulationJob().getVCDataIdentifier(), outDir, null, null);
        } catch (IOException | DataAccessException e) {
            e.printStackTrace();
        }


        ArrayList<String> varNameList = new ArrayList<String>();
        for (SpeciesContextSpec scs : simContext.getReactionContext().getSpeciesContextSpecs()){
            varNameList.add(scs.getSpeciesContext().getName());
        }
        String[] varNames = varNameList.toArray(new String[0]);

        OutputTimeSpec outputTimeSpec = sim.getSolverTaskDescription().getOutputTimeSpec();

        ArrayList<Double> sampleTimeList = new ArrayList<Double>();
        if (outputTimeSpec instanceof UniformOutputTimeSpec){
            double endingTime = sim.getSolverTaskDescription().getTimeBounds().getEndingTime();
            double dT = ((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep();
            int currTimeIndex=0;
            while (currTimeIndex*dT <= (endingTime+1e-8)){
                sampleTimeList.add(currTimeIndex*dT);
                currTimeIndex++;
            }
        }
        double[] sampleTimes = new double[sampleTimeList.size()];
        for (int i=0;i<sampleTimes.length;i++){
            sampleTimes[i] = sampleTimeList.get(i);
        }



        TimeSeriesMultitrialData sampleDataDeterministic = new TimeSeriesMultitrialData(taskId,varNames, sampleTimes, 1);

        CLIUtils.saveTimeSeriesMultitrialDataAsCSV(sampleDataDeterministic, outDir);
        return odeSimData;
    }
}
