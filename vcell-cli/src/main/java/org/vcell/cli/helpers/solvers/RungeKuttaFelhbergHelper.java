package org.vcell.cli.helpers.solvers;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ClientSimManager;
import cbit.vcell.math.MathException;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Discontinuity;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.*;
import cbit.vcell.solver.server.*;
import org.vcell.cli.CLIUtils;
import org.vcell.cli.helpers.sbml.SBMLSolverHelper;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import static cbit.vcell.simdata.SimDataConstants.LOGFILE_EXTENSION;
import static cbit.vcell.simdata.SimDataConstants.ODE_DATA_EXTENSION;

public class RungeKuttaFelhbergHelper {

    public static ODESolverResultSet solve(File outDir, String taskId, BioModel bioModel){
        String docName = bioModel.getName();
        Simulation sim = bioModel.getSimulation(0);

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

//        ODESolverResultSet resultSet = null;
//        ClientTaskStatusSupport support = new ClientTaskStatusSupport() {
//            @Override
//            public void setMessage(String message) {
//
//            }
//
//            @Override
//            public void setProgress(int progress) {
//
//            }
//
//            @Override
//            public int getProgress() {
//                return 0;
//            }
//
//            @Override
//            public boolean isInterrupted() {
//                return false;
//            }
//
//            @Override
//            public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
//
//            }
//        };
        ODESimData odeSimData = null;

        //            Solver solver = new RungeKuttaFehlbergSolver(simTask, outDir);
//
//            solver.addSolverListener(new SolverListener() {
//                public void solverStopped(SolverEvent event) {
//                    support.setMessage(event.getSimulationMessage().getDisplayMessage());
//                }
//                public void solverStarting(SolverEvent event) {
//                    String displayMessage = event.getSimulationMessage().getDisplayMessage();
//                    System.out.println(displayMessage);
//                    support.setMessage(displayMessage);
//                    if(displayMessage.equals(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT.getDisplayMessage())) {
//                        support.setProgress(75);
//                    } else if(displayMessage.equals(SimulationMessage.MESSAGE_SOLVER_RUNNING_INPUT_FILE.getDisplayMessage())) {
//                        support.setProgress(90);
//                    }
//                }
//                public void solverProgress(SolverEvent event) {
//                    support.setMessage("Running...");
//                    int progress = (int)(event.getProgress() * 100);
//                    support.setProgress(progress);
//                }
//                public void solverPrinted(SolverEvent event) {
//                    support.setMessage("Running...");
//                }
//                public void solverFinished(SolverEvent event) {
//                    support.setMessage(event.getSimulationMessage().getDisplayMessage());
//                }
//                public void solverAborted(SolverEvent event) {
//                    support.setMessage(event.getSimulationMessage().getDisplayMessage());
//                }
//            });
//
//            solver.startSolver();

        newSolver.startSolver();
        while (true){
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SolverStatus solverStatus = newSolver.getSolverStatus();
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        ODEDataBlock odeDataBlock = null;
        try {
            odeDataBlock = simData.getODEDataBlock();
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        odeSimData = odeDataBlock.getODESimData();

//            timeSeriesMultitrialData.addDataSet(odeSimData,trialIndex);


        return odeSimData;
    }
}
