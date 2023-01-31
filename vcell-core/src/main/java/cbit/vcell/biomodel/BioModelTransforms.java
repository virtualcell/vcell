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

    public static void repairLegacyProblems(BioModel bioModel){
        reconcileLegacyAbsRelSizes(bioModel);
    }

    public static void reconcileLegacyAbsRelSizes(BioModel bioModel) {
        /**
         * if both relative and absolute sizes exist in structure mappings of legacy models,
         * then remove either absolute sizes or relative sizes (whichever way is consistent with MathDescription).
         */
        Model.StructureTopology structureTopology = bioModel.getModel().getStructureTopology();
        if (structureTopology.isEmpty()){
            return;
        }

        for (SimulationContext simulationContext : bioModel.getSimulationContexts()){
            MathDescription mathDescription = simulationContext.getMathDescription();
            if (mathDescription==null){
                continue;
            }
            StructureMapping[] structureMappings = simulationContext.getGeometryContext().getStructureMappings();
            ArrayList<String> relativeSizesDefinedAsConstantsInMath = new ArrayList<String>();
            ArrayList<String> absoluteSizesDefinedAsConstantsInMath = new ArrayList<String>();
            boolean relativeSizesDefinedInModel = false;
            boolean absoluteSizesDefinedInModel = false;
            for (StructureMapping sm : structureMappings){
                StructureMapping.StructureMappingParameter unitSizeParameter = sm.getUnitSizeParameter();
                if (unitSizeParameter!=null && unitSizeParameter.getExpression()!=null){
                    absoluteSizesDefinedInModel = true;
                    String mathName = null;
                    switch (unitSizeParameter.getRole()){
                        case StructureMapping.ROLE_AreaPerUnitArea:{
                            mathName = "AreaPerUnitArea_" + sm.getStructure().getName();
                            break;
                        }
                        case StructureMapping.ROLE_AreaPerUnitVolume:{
                            mathName = "AreaPerUnitVolume_" + sm.getStructure().getName();
                            break;
                        }
                        case StructureMapping.ROLE_VolumePerUnitVolume:{
                            mathName = "VolumePerUnitVolume_" + sm.getStructure().getName();
                            break;
                        }
                        case StructureMapping.ROLE_VolumePerUnitArea:{
                            mathName = "VolumePerUnitArea_" + sm.getStructure().getName();
                            break;
                        }
                    }
                    if (mathName != null){
                        Variable unitSize = mathDescription.getVariable(mathName);
                        if (unitSize instanceof Constant){
                            absoluteSizesDefinedAsConstantsInMath.add(unitSize.getName());
                        }
                    }
                }
                if (sm.getSizeParameter()!=null && sm.getSizeParameter().getExpression()!=null){
                    absoluteSizesDefinedInModel = true;
                    Variable size = mathDescription.getVariable("Size_"+sm.getStructure().getName());
                    if (size instanceof Constant){
                        absoluteSizesDefinedAsConstantsInMath.add(size.getName());
                    }
                }
                if (sm instanceof MembraneMapping){
                    MembraneMapping mm = (MembraneMapping)sm;
                    if (mm.getSurfaceToVolumeParameter() != null && mm.getSurfaceToVolumeParameter().getExpression()!=null) {
                        relativeSizesDefinedInModel = true;
                        Variable surfToVol = mathDescription.getVariable("SurfToVol_"+mm.getMembrane().getName());
                        if (surfToVol instanceof Constant){
                            relativeSizesDefinedAsConstantsInMath.add(surfToVol.getName());
                        }
                    }
                    if (mm.getVolumeFractionParameter() != null && mm.getVolumeFractionParameter().getExpression()!=null) {
                        relativeSizesDefinedInModel = true;
                        Variable volFract = mathDescription.getVariable("VolFract_"+structureTopology.getInsideFeature(mm.getMembrane()).getName());
                        if (volFract instanceof Constant){
                            relativeSizesDefinedAsConstantsInMath.add(volFract.getName());
                        }
                    }
                }
            }
            if (relativeSizesDefinedInModel && absoluteSizesDefinedInModel){
                // check to see if absolute size constants or relative size constants are used in Functions
                boolean bAbsoluteSizesReferenced = false;
                boolean bRelativeSizesReferenced = false;
                for (Variable var : mathDescription.getVariableList()){
                    if (var instanceof Function){
                        Function function = (Function) var;
                        String[] referencedSymbols = function.getExpression().getSymbols();
                        if (referencedSymbols!=null) {
                            for (String referencedSymbol : referencedSymbols) {
                                if (absoluteSizesDefinedAsConstantsInMath.contains(referencedSymbol)) {
                                    bAbsoluteSizesReferenced = true;
                                }
                                if (relativeSizesDefinedAsConstantsInMath.contains(referencedSymbol)) {
                                    bRelativeSizesReferenced = true;
                                }
                            }
                        }
                    }
                }
                if (!bRelativeSizesReferenced && !bAbsoluteSizesReferenced){
                    return;  // very simple math, nothing to do
                }
                if (bRelativeSizesReferenced && bAbsoluteSizesReferenced){
//                    throw new RuntimeException("could not reconcile structure sizes, both relative and absolute sizes used in Math");
                    lg.warn("skipping: could not reconcile structure sizes, both relative and absolute sizes used in Math for BioModel("+bioModel.getVersion()+"):"+simulationContext.getName());
                }
                try {
                    if (bRelativeSizesReferenced && !bAbsoluteSizesReferenced) {
                        // throw away absolute sizes
                        for (StructureMapping sm : structureMappings) {
                            if (sm.getUnitSizeParameter() != null && sm.getUnitSizeParameter().getExpression() != null) {
                                sm.getUnitSizeParameter().setExpression(null);
                            }
                            if (sm.getSizeParameter() != null && sm.getSizeParameter().getExpression() != null) {
                                sm.getSizeParameter().setExpression(null);
                            }
                        }
                    } else if (bAbsoluteSizesReferenced && !bRelativeSizesReferenced) {
                        // throw away relative sizes
                        for (StructureMapping sm : structureMappings) {
                            if (sm instanceof MembraneMapping) {
                                MembraneMapping mm = (MembraneMapping) sm;
                                if (mm.getSurfaceToVolumeParameter() != null && mm.getSurfaceToVolumeParameter().getExpression() != null) {
                                    mm.getSurfaceToVolumeParameter().setExpression(null);
                                }
                                if (mm.getVolumeFractionParameter() != null && mm.getVolumeFractionParameter().getExpression() != null) {
                                    mm.getVolumeFractionParameter().setExpression(null);
                                }
                            }
                        }
                    }
                } catch (ExpressionBindingException e){
                    throw new RuntimeException("failed to reconcile structure mapping sizes", e);
                }
            }
        }
    }

}
