package org.vcell.cli.run.results.solver;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.ode.AbstractJavaSolver;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultsSetReturnable;
import cbit.vcell.solver.server.Solver;
import org.jlibsedml.UniformTimeCourse;
import org.vcell.sbml.vcell.NonSpatialSBMLSimResults;
import org.vcell.sbml.vcell.SBMLSymbolMapping;

import java.util.List;

public class SolverNonSpatialExecutionRequest extends SolverExecutionRequest {
    private final SimulationOwner simulationOwner;

    protected SolverNonSpatialExecutionRequest(Solver requestedSolver, SolverDescription solverDescription, UniformTimeCourse utcSimRequested, SimulationJob simulationJob, String sedmlLocation) {
        super(simulationJob, requestedSolver, solverDescription, utcSimRequested, sedmlLocation);
        this.simulationOwner = simulationJob.getSimulation().getSimulationOwner();
        this.progressGeneralLog.append("Done!\n");
    }

    @Override
    public NonSpatialSBMLSimResults getResults(MathSymbolMapping mathMapping, SBMLSymbolMapping sbmlMapping) throws ExpressionException {
        if (!(this.solver instanceof ODESolverResultsSetReturnable OSRSRSolver)) throw new UnsupportedOperationException("Spatial results stored in non-spatial container");
        ODESolverResultSet resultSet = OSRSRSolver.getODESolverResultSet();
        this.processUserDefinedFunctions(resultSet, this.simulationOwner);
        return new NonSpatialSBMLSimResults(resultSet, this.utcSim, sbmlMapping, mathMapping, this.solver instanceof AbstractJavaSolver);
    }

    private void processUserDefinedFunctions(ODESolverResultSet odeSolverResultSet, SimulationOwner so) throws ExpressionException {
        if (odeSolverResultSet == null) return;
        // add output functions, if any, to result set
        List<AnnotatedFunction> funcs = so.getOutputFunctionContext().getOutputFunctionsList();
        if (funcs == null) return;
        for (AnnotatedFunction function : funcs) {
            FunctionColumnDescription fcd;
            String funcName = function.getName();
            Expression funcExp = function.getExpression();
            fcd = new FunctionColumnDescription(funcExp, funcName, null, function.getDisplayName(), true);
            odeSolverResultSet.checkFunctionValidity(fcd);
            odeSolverResultSet.addFunctionColumn(fcd);
        }
    }
}
