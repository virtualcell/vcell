package org.vcell.cli.run;

import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Triplet;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class CLISolverListener implements SolverListener {
    private static final Logger lg = LogManager.getLogger(CLISolverListener.class);

    public final CompletableFuture<Triplet<Double, Integer, Exception>> solverResult;
    private final StringBuilder solverLog;
    private final String solverLabel;

    /**
     * Tracks a CLI execution of a solver, and offers a completable future for the result!
     * @param solverLabel what to call the solver this class listens to
     */
    public CLISolverListener(String solverLabel) {
        this.solverResult = new CompletableFuture<>();
        this.solverLog = new StringBuilder();
        this.solverLabel = solverLabel;
    }

    /**
     * Invoked when the solver aborts a calculation (abnormal termination).
     *
     * @param event indicates the solver and the event type
     */
    @Override
    public void solverAborted(SolverEvent event) {
        Double progress = event.getProgress();
        Exception fault = this.determineFault(event);
        this.solverResult.complete(new Triplet<>(progress, SolverEvent.SOLVER_ABORTED, fault));
    }

    private Exception determineFault(SolverEvent event){
        String sourceString = event.getSource().toString();
        String errorString = event.getSimulationMessage().getDisplayMessage();
        String formattedString = String.format("%s(%s): %s", this.solverLabel, sourceString, errorString);
        if (errorString.toLowerCase().contains("timed out")) return new TimeoutException(formattedString);
        else if (errorString.toLowerCase().contains("divide by zero")) return new DivideByZeroException(formattedString);
        else return new SolverException(formattedString);
    }

    /**
     * Invoked when the solver finishes a calculation (normal termination).
     *
     * @param event indicates the solver and the event type
     */
    @Override
    public void solverFinished(SolverEvent event) {
        Double progress = event.getProgress();
        String sourceString = event.getSource().toString();
        String successString = event.getSimulationMessage().toString();
        lg.info("{}({}) Success Message: {}", this.solverLabel, sourceString, successString);
        this.solverResult.complete(new Triplet<>(progress, SolverEvent.SOLVER_FINISHED, null));
    }

    /**
     * Invoked when the solver stores values in the result set.
     *
     * @param event indicates the solver and the event type
     */
    @Override
    public void solverPrinted(SolverEvent event) {
        this.solverLog.append(event.getSimulationMessage()).append("\n");
    }

    /**
     * Invoked when the solver stores values in the result set.
     *
     * @param event indicates the solver and the event type
     */
    @Override
    public void solverProgress(SolverEvent event) {
        lg.info("{}({}) progress: {}\t(\"{}\")", this.solverLabel, event.getSource().toString(), event.getProgress(), event.getSimulationMessage());
    }

    /**
     * Invoked when the solver begins a calculation.
     *
     * @param event indicates the solver and the event type
     */
    @Override
    public void solverStarting(SolverEvent event) {
        lg.info("{}({}) Beginning Simulation (\"{}\")", this.solverLabel, event.getSource().toString(), event.getSimulationMessage());
    }

    /**
     * Invoked when the solver stops a calculation, usually because
     * of a user-initiated stop call.
     *
     * @param event indicates the solver and the event type
     */
    @Override
    public void solverStopped(SolverEvent event) {
        lg.info("{}({}) Ending Simulation: (\"{}\")", this.solverLabel, event.getSource().toString(), event.getSimulationMessage());
    }
}
