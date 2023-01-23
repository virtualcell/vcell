package cbit.vcell.biomodel;

import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Model;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BioModelTransforms {

    private final static Logger lg = LogManager.getLogger(BioModelTransforms.class);

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
    private final static double FARADAY_CONSTANT_old = 9.648e4;
    private final static double FARADAY_CONSTANT_NMOLE_old = 9.648e-5;
    private final static double N_PMOLE_value_old = 6.02e11;
    private final static double GAS_CONSTANT_old = 8314.0;
    private final static double KMOLE_value_old = 1.0/602.0;

    private final static double FARADAY_CONSTANT_new = 9.64853321e4;
    private final static double FARADAY_CONSTANT_NMOLE_new = 9.64853321e-5;
    private final static double N_PMOLE_new = 6.02214179e11;
    private final static double GAS_CONSTANT_new = 8314.46261815;
    private final static double KMOLE_new = 1.0/602.214179;

    public static void restoreOldReservedSymbolsIfNeeded(BioModel bioModel) {

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
        assert transformed_FARADAYS_CONSTANT.getExpression().compareEqual(new Expression(FARADAY_CONSTANT_new));
        transformed_FARADAYS_CONSTANT.getExpression().substituteInPlace(transformed_FARADAYS_CONSTANT.getExpression(),new Expression(FARADAY_CONSTANT_old));

        Model.ReservedSymbol transformed_FARADAYS_CONSTANT_NMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.FARADAY_CONSTANT_NMOLE);
        assert transformed_FARADAYS_CONSTANT_NMOLE.getExpression().compareEqual(new Expression(FARADAY_CONSTANT_NMOLE_new));
        transformed_FARADAYS_CONSTANT_NMOLE.getExpression().substituteInPlace(transformed_FARADAYS_CONSTANT_NMOLE.getExpression(),new Expression(FARADAY_CONSTANT_NMOLE_old));

        Model.ReservedSymbol transformed_N_PMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.N_PMOLE);
        assert transformed_N_PMOLE.getExpression().compareEqual(new Expression(N_PMOLE_new));
        transformed_N_PMOLE.getExpression().substituteInPlace(transformed_N_PMOLE.getExpression(),new Expression(N_PMOLE_value_old));

        Model.ReservedSymbol transformed_GAS_CONSTANT = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.GAS_CONSTANT);
        assert transformed_GAS_CONSTANT.getExpression().compareEqual(new Expression(GAS_CONSTANT_new));
        transformed_GAS_CONSTANT.getExpression().substituteInPlace(transformed_GAS_CONSTANT.getExpression(),new Expression(GAS_CONSTANT_old));

        Model.ReservedSymbol transformed_KMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.KMOLE);
        assert ExpressionUtils.functionallyEquivalent(transformed_KMOLE.getExpression(),new Expression(KMOLE_new));
        transformed_KMOLE.getExpression().substituteInPlace(transformed_KMOLE.getExpression(),new Expression(KMOLE_value_old));

        // need to call Kinetics.updateGeneratedExpressions() - invoked by refreshDependencies().
        assert ExpressionUtils.value_molecules_per_uM_um3_NUMERATOR.equals(ExpressionUtils.latest_molecules_per_uM_um3_NUMERATOR);
        ExpressionUtils.value_molecules_per_uM_um3_NUMERATOR = ExpressionUtils.legacy_molecules_per_uM_um3_NUMERATOR;
        bioModel.refreshDependencies();
    }

    public static void restoreLatestReservedSymbols(BioModel bioModel) {
        // restore latest values of KMOLE and other ReservedSymbols (and misc static constants)
        Model.ReservedSymbol transformed_FARADAYS_CONSTANT = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.FARADAY_CONSTANT);
        if (!transformed_FARADAYS_CONSTANT.getExpression().compareEqual(new Expression(FARADAY_CONSTANT_new))) {
            transformed_FARADAYS_CONSTANT.getExpression().substituteInPlace(transformed_FARADAYS_CONSTANT.getExpression(), new Expression(FARADAY_CONSTANT_new));
        }

        Model.ReservedSymbol transformed_FARADAYS_CONSTANT_NMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.FARADAY_CONSTANT_NMOLE);
        if (!transformed_FARADAYS_CONSTANT_NMOLE.getExpression().compareEqual(new Expression(FARADAY_CONSTANT_NMOLE_new))) {
            transformed_FARADAYS_CONSTANT_NMOLE.getExpression().substituteInPlace(transformed_FARADAYS_CONSTANT_NMOLE.getExpression(), new Expression(FARADAY_CONSTANT_NMOLE_new));
        }

        Model.ReservedSymbol transformed_N_PMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.N_PMOLE);
        if (!transformed_N_PMOLE.getExpression().compareEqual(new Expression(N_PMOLE_new))) {
            transformed_N_PMOLE.getExpression().substituteInPlace(transformed_N_PMOLE.getExpression(), new Expression(N_PMOLE_new));
        }

        Model.ReservedSymbol transformed_GAS_CONSTANT = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.GAS_CONSTANT);
        if (!transformed_GAS_CONSTANT.getExpression().compareEqual(new Expression(GAS_CONSTANT_new))) {
            transformed_GAS_CONSTANT.getExpression().substituteInPlace(transformed_GAS_CONSTANT.getExpression(), new Expression(GAS_CONSTANT_new));
        }

        Model.ReservedSymbol transformed_KMOLE = bioModel.getModel().getReservedSymbolByRole(Model.ReservedSymbolRole.KMOLE);
        if (!ExpressionUtils.functionallyEquivalent(transformed_KMOLE.getExpression(),new Expression(KMOLE_new))) {
            transformed_KMOLE.getExpression().substituteInPlace(transformed_KMOLE.getExpression(), new Expression(KMOLE_value_old));
        }

        ExpressionUtils.value_molecules_per_uM_um3_NUMERATOR = ExpressionUtils.latest_molecules_per_uM_um3_NUMERATOR;
        bioModel.refreshDependencies();
    }
    }
}
