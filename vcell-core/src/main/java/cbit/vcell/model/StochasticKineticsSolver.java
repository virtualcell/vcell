package cbit.vcell.model;

import cbit.vcell.parser.ExpressionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StochasticKineticsSolver {

    private final static Logger lg = LogManager.getLogger(StochasticKineticsSolver.class);

    public StochasticKineticsResults transformToStochastic(ReactionStep reactionStep) throws ExpressionException {
        Parameter ma_kf = null;
        Parameter ma_kr = null;
        if (reactionStep.getKinetics() instanceof MassActionKinetics ma){
            ma_kf = ma.getForwardRateParameter();
            ma_kr = ma.getReverseRateParameter();
        }
        try {
            MassActionSolver.MassActionFunction maResults = MassActionSolver.solveMassAction(ma_kf, ma_kr, reactionStep.getKinetics().getAuthoritativeParameter().getExpression(), reactionStep);
            StochasticKineticsResults results = new StochasticKineticsResults(maResults, maResults.getReactants(), maResults.getProducts());
            return results;
        } catch (Exception e) {
            lg.info("Failed to solve mass action kinetics for reaction step: " + reactionStep.getName() + ", " + e.getMessage());
            try {
                GeneralKineticsSolver.GeneralKineticsStochasticFunction gks = GeneralKineticsSolver.solveGeneralKineticsStochasticFunction(reactionStep);
                StochasticKineticsResults results = new StochasticKineticsResults(gks, gks.getReactants(), gks.getProducts());
                return results;
            } catch (ModelException ex) {
                lg.info("Failed to solve stochastic kinetics for reaction step: " + reactionStep.getName() + ", " + e.getMessage());
                throw new RuntimeException(ex);
            }
        }
    }
}
