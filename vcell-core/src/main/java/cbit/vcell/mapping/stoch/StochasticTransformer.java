package cbit.vcell.mapping.stoch;

import cbit.vcell.model.*;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StochasticTransformer {

    private final static Logger lg = LogManager.getLogger(StochasticTransformer.class);

    public static Expression substituteParameters(Expression exp, boolean substituteConst) throws ExpressionException
    {
        Expression result = new Expression(exp);
        boolean bSubstituted = true;
        while (bSubstituted) {
            bSubstituted = false;
            String symbols[] = result.getSymbols();
            for (int k = 0; symbols != null && k < symbols.length; k++) {
                SymbolTableEntry ste = result.getSymbolBinding(symbols[k]);
                if (ste instanceof ProxyParameter) {
                    ProxyParameter pp = (ProxyParameter)ste;
                    result.substituteInPlace(new Expression(pp,pp.getNameScope()), new Expression(pp.getTarget(),pp.getTarget().getNameScope()));
                    bSubstituted = true;
                }else if (ste instanceof Parameter){
                    Parameter kp = (Parameter)ste;
                    try {
                        Expression expKP = kp.getExpression();
                        if (!expKP.flatten().isNumeric() || substituteConst) {
                            result.substituteInPlace(new Expression(symbols[k]), new Expression(kp.getExpression()));
                            bSubstituted = true;
                        }
                    } catch (ExpressionException e1) {
                        lg.error(e1);
                        throw new ExpressionException(e1.getMessage());
                    }
                }else if (substituteConst && ste instanceof Model.ReservedSymbol){
                    Model.ReservedSymbol rs = (Model.ReservedSymbol)ste;
                    try {
                        if (rs.getExpression() != null)
                        {
                            result.substituteInPlace(new Expression(symbols[k]), new Expression(rs.getExpression()));
                            bSubstituted = true;
                        }
                    } catch (ExpressionException e1) {
                        lg.error(e1);
                        throw new ExpressionException(e1.getMessage());
                    }
                }

            }

        }
        return result;
    }

    public static StochasticFunction transformToStochastic(ReactionStep reactionStep) throws ExpressionException {
        Expression reactionRate = new Expression(reactionStep.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate), reactionStep.getNameScope());
        Parameter ma_kf = null;
        Parameter ma_kr = null;
        if (reactionStep.getKinetics() instanceof MassActionKinetics ma){
            ma_kf = ma.getForwardRateParameter();
            ma_kr = ma.getReverseRateParameter();
        }else if (reactionStep.getKinetics() instanceof GeneralPermeabilityKinetics gpk) {
            ma_kf = gpk.getPermeabilityParameter();
            ma_kr = gpk.getPermeabilityParameter();
        }
        try {
            return MassActionStochasticTransformer.solveMassAction(ma_kf, ma_kr, reactionRate, reactionStep);
        } catch (Exception e) {
            lg.info("Failed to solve mass action kinetics for reaction step: " + reactionStep.getName() + ", " + e.getMessage());
            try {
                return GeneralKineticsStochasticTransformer.solveGeneralKineticsStochasticFunction(reactionStep);
            } catch (ModelException ex) {
                lg.info("Failed to solve stochastic kinetics for reaction step: " + reactionStep.getName() + ", " + e.getMessage());
                throw new RuntimeException(ex);
            }
        }
    }
}
