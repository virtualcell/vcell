package cbit.vcell.biomodel;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Model;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionUtils;

public class BioModelTransforms {
    public static void restoreOldReservedSymbolsIfNeeded(BioModel bioModel) {
        /**
         *   old values for reserved symbols (prior to commit on 8/9/2019)
         *
         * 			new ReservedSymbol(ReservedSymbolRole.FARADAY_CONSTANT, "_F_","Faraday constant",unitSystem.getFaradayConstantUnit(), new Expression(9.648e4)),
         * 			new ReservedSymbol(ReservedSymbolRole.FARADAY_CONSTANT_NMOLE, "_F_nmol_","Faraday constant", unitSystem.getFaradayConstantNMoleUnit(),new Expression(9.648e-5)),
         * 			new ReservedSymbol(ReservedSymbolRole.N_PMOLE, "_N_pmol_","Avagadro Num (scaled)",unitSystem.getN_PMoleUnit(),new Expression(6.02e11)),
         * 			new ReservedSymbol(ReservedSymbolRole.GAS_CONSTANT, "_R_","Gas Constant",unitSystem.getGasConstantUnit(), new Expression(8314.0)),
         * 			new ReservedSymbol(ReservedSymbolRole.KMOLE, "KMOLE", "Flux unit conversion", unitSystem.getKMoleUnit(), new Expression(new RationalNumber(1, 602)))
         *
         * 	new values for reserved symbols (after commit on 8/9/2019)
         *
         * 	        put(Model.ReservedSymbolRole.FARADAY_CONSTANT, 9.64853321e4);			// exactly 96485.3321233100184 C/mol
         *          put(Model.ReservedSymbolRole.FARADAY_CONSTANT_NMOLE, 9.64853321e-5);	// was 9.648
         *          put(Model.ReservedSymbolRole.N_PMOLE, 6.02214179e11);
         *          put(Model.ReservedSymbolRole.GAS_CONSTANT, 8314.46261815);			// exactly 8314.46261815324  (was 8314.0)
         *          put(Model.ReservedSymbolRole.KMOLE, 1.0/602.214179);
         */
        final double FARADAY_CONSTANT_old = 9.648e4;
        final double FARADAY_CONSTANT_NMOLE_old = 9.648e-5;
        final double N_PMOLE_value_old = 6.02e11;
        final double GAS_CONSTANT_old = 8314.0;
        final double KMOLE_value_old = 1.0/602.0;

        // restore old values of KMOLE and other ReservedSymbols if present in original Math
        // this ensures that newly generated math descriptions will use the same values
        boolean bHasOldKMOLE = false;
        for (SimulationContext simulationContext : bioModel.getSimulationContexts()){
            MathDescription orig_math = simulationContext.getMathDescription();
            Variable orig_math_KMOLE = orig_math.getVariable("KMOLE");
            if (orig_math_KMOLE != null && orig_math_KMOLE.getExpression()!=null){
                if (ExpressionUtils.functionallyEquivalent(orig_math_KMOLE.getExpression(), new Expression(1.0/602.0))){
                    bHasOldKMOLE = true;
                    break;
                }
            }
        }

        if (!bHasOldKMOLE){
            return;
        }

        // set old values for physical constants for backward compatibility
        Model.ReservedSymbol transformed_FARADAYS_CONSTANT = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.FARADAY_CONSTANT);
        transformed_FARADAYS_CONSTANT.getExpression().substituteInPlace(transformed_FARADAYS_CONSTANT.getExpression(),new Expression(FARADAY_CONSTANT_old));

        Model.ReservedSymbol transformed_FARADAYS_CONSTANT_NMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.FARADAY_CONSTANT_NMOLE);
        transformed_FARADAYS_CONSTANT_NMOLE.getExpression().substituteInPlace(transformed_FARADAYS_CONSTANT_NMOLE.getExpression(),new Expression(FARADAY_CONSTANT_NMOLE_old));

        Model.ReservedSymbol transformed_N_PMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.N_PMOLE);
        transformed_N_PMOLE.getExpression().substituteInPlace(transformed_N_PMOLE.getExpression(),new Expression(N_PMOLE_value_old));

        Model.ReservedSymbol transformed_GAS_CONSTANT = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.GAS_CONSTANT);
        transformed_GAS_CONSTANT.getExpression().substituteInPlace(transformed_GAS_CONSTANT.getExpression(),new Expression(GAS_CONSTANT_old));

        Model.ReservedSymbol transformed_KMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.KMOLE);
        transformed_KMOLE.getExpression().substituteInPlace(transformed_KMOLE.getExpression(),new Expression(KMOLE_value_old));
    }
}
