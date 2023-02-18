package cbit.vcell.biomodel;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.*;
import cbit.vcell.model.*;
import cbit.vcell.parser.*;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.TokenMangler;

import java.beans.PropertyVetoException;
import java.util.*;
import java.util.stream.Collectors;

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

    public static boolean restoreOldReservedSymbolsIfNeeded(BioModel bioModel) {

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
            return false;
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
        return true;
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
            transformed_KMOLE.getExpression().substituteInPlace(transformed_KMOLE.getExpression(), new Expression(KMOLE_new));
        }

        ExpressionUtils.value_molecules_per_uM_um3_NUMERATOR = ExpressionUtils.latest_molecules_per_uM_um3_NUMERATOR;
        bioModel.refreshDependencies();
    }

    public static void repairLegacyProblems(BioModel bioModel){
        reconcileLegacyAbsRelSizes(bioModel);
        fixLegacyMassAction(bioModel); // no susbstrate and non-zero kf or no products - capture original intent of model.
    }

    private static void fixLegacyMassAction(BioModel bioModel) {
        //
        // non-zero mass action Kf parameters should have been ignored if no reactants (zero-order source term).
        // non-zero mass action Kr parameters should have been ignored if no products (zero-order degradation term).
        //
        // early versions of VCell treated these parameters as constant terms (e.g. Kf * Product_of(reactants_i) -> Kf, if no reactants)
        // recent versions of VCell ignore these non-zero parameter terms as violating the expected semantics of "Mass Action" kinetics from Chemistry (and a warning is presented to the user).
        //
        // Here we transform the legacy BioModel as needed to preserve the original intent of the legacy models (should be mathematically equivalent)
        // 1) if the parameter WAS used as a source term in any math description - then convert the kinetic law to GeneralKinetics and preserve the math.
        // 2) if the parameter WAS NOT used as a source term in any math description - then set it to 0.0 upon loading - leave it as-is in the database.
        //

        // collect list of non-zero mass action reaction parameters (Kf, Kr) that should no longer be used (if no reactants or no products)
        Map<String, Kinetics.KineticsParameter> potentialMassActionSourceParameters = new HashMap<>();
        for (ReactionStep rs : bioModel.getModel().getReactionSteps()){
            if (rs.getKinetics() instanceof MassActionKinetics){
                MassActionKinetics massActionKinetics = (MassActionKinetics) rs.getKinetics();
                if (rs.getNumReactants() == 0) {
                    Kinetics.KineticsParameter forwardRateParameter = massActionKinetics.getForwardRateParameter();
                    if (!forwardRateParameter.getExpression().isZero()) {
                        String varName = TokenMangler.fixTokenStrict(forwardRateParameter.getName()+"_"+rs.getName());
                        potentialMassActionSourceParameters.put(varName, forwardRateParameter); // Kf_r0
                        potentialMassActionSourceParameters.put(forwardRateParameter.getName(), forwardRateParameter); // Kf
                    }
                }
                if (rs.getNumProducts() == 0) {
                    Kinetics.KineticsParameter reverseRateParameter = massActionKinetics.getReverseRateParameter();
                    if (!reverseRateParameter.getExpression().isZero()) {
                        String varName = TokenMangler.fixTokenStrict(reverseRateParameter.getName()+"_"+rs.getName());
                        potentialMassActionSourceParameters.put(varName, reverseRateParameter);
                        potentialMassActionSourceParameters.put(reverseRateParameter.getName(), reverseRateParameter);
                    }
                }
            }
        }
        Set<Kinetics.KineticsParameter> referencedParameters = new HashSet<>();
        for (SimulationContext simulationContext : bioModel.getSimulationContexts()){
            final MathDescription math = simulationContext.getMathDescription();
            if (math == null){
                break;
            }
            SymbolTable symbolTable = new SymbolTable(){
                @Override
                public SymbolTableEntry getEntry(String identifierString) {
                    SymbolTableEntry ste = math.getEntry(identifierString);
                    if (ste != null){
                        Kinetics.KineticsParameter kineticsParameter = potentialMassActionSourceParameters.get(ste.getName());
                        if (kineticsParameter != null){
                            referencedParameters.add(kineticsParameter);
                        }
                    }
                    return ste;
                }
                @Override
                public void getEntries(Map<String, SymbolTableEntry> entryMap) {
                    math.getEntries(entryMap);
                }
            };
            for (Function function : Collections.list(math.getFunctions())){
                if (!function.getName().startsWith("J_")){
                    continue;
                }
                Expression exp = function.getExpression();
                try {
                    // side-effect is to visit all symbol references and populate 'referencedParameters' set.
                    exp.bindExpression(symbolTable);
                }catch (ExpressionException e){
                    lg.warn("failed to bind expression");
                }
            }
//            List<Expression> allExpressions = new ArrayList<>();
//            math.getAllExpressions(allExpressions);
//            for (Expression exp : allExpressions){
//                try {
//                    // side-effect is to visit all symbol references and populate 'referencedParameters' set.
//                    exp.bindExpression(symbolTable);
//                }catch (ExpressionException e){
//                    lg.warn("failed to bind expression");
//                }
//            }
        }
        HashSet<Kinetics.KineticsParameter> uniquePotentialMASourceParameters = new HashSet<>(potentialMassActionSourceParameters.values());
        for (Kinetics.KineticsParameter kp : uniquePotentialMASourceParameters){
            if (referencedParameters.contains(kp)){
                //
                // respect legacy intepretation of zero-order term by transforming legacy MassActionKinetics into
                // corresponding GeneralKinetics (including the zero-order term).
                //
                MassActionKinetics maKinetics = (MassActionKinetics) kp.getKinetics();
                ReactionStep rs = maKinetics.getReactionStep();
                Expression forwardTerm = new Expression(maKinetics.getForwardRateParameter(), rs.getNameScope());
                for (Reactant reactant : rs.getReactants()){
                    forwardTerm = Expression.mult(forwardTerm, new Expression(reactant.getSpeciesContext(), rs.getModel().getNameScope())); // to be replaced by proxy parameters as needed
                }
                Expression reverseTerm = new Expression(maKinetics.getReverseRateParameter(), rs.getNameScope());
                for (Product product : rs.getProducts()){
                    reverseTerm = Expression.mult(reverseTerm, new Expression(product.getSpeciesContext(), rs.getModel().getNameScope())); // to be replaced by proxy parameters as needed
                }
                Expression newGeneralKineticsRateExp = Expression.add(forwardTerm,Expression.negate(reverseTerm));
                List<Kinetics.KineticsParameter> origUserDefinedParameters = Arrays.stream(maKinetics.getKineticsParameters())
                        .filter(p -> p.getRole() == Kinetics.ROLE_UserDefined).collect(Collectors.toList());
                try {
                    GeneralKinetics generalKinetics = new GeneralKinetics(rs);
                    newGeneralKineticsRateExp.bindExpression(null);

                    Kinetics.KineticsParameter origKf = maKinetics.getForwardRateParameter();
                    Expression origKfExp = new Expression(origKf.getExpression());
                    origKfExp.bindExpression(null);

                    Kinetics.KineticsParameter origKr = maKinetics.getReverseRateParameter();
                    Expression origKrExp = new Expression(origKr.getExpression());
                    origKrExp.bindExpression(null);

                    // transfer original build-in parameters (J, Kf, Kr) to General Kinetics
                    generalKinetics.setParameterValue(generalKinetics.getReactionRateParameter(), newGeneralKineticsRateExp);
                    generalKinetics.setParameterValue(generalKinetics.getKineticsParameter(origKf.getName()), origKfExp);
                    generalKinetics.setParameterValue(generalKinetics.getKineticsParameter(origKr.getName()), origKrExp);

                    // set Values of user-defined parameters into the new Kinetics object
                    for (Kinetics.KineticsParameter origUserDefinedParameter : origUserDefinedParameters) {
                        Kinetics.KineticsParameter newUserDefinedParam = generalKinetics.getKineticsParameter(origUserDefinedParameter.getName());
                        if (newUserDefinedParam != null){
                            Expression newExp = new Expression(origUserDefinedParameter.getExpression());
                            newExp.bindExpression(null);
                            generalKinetics.setParameterValue(newUserDefinedParam, newExp);
                        }else{
                            lg.error("failed to transform legacy mass action kinetics for reaction '"+rs.getName()+"', " +
                                    "user defined parameter '"+origUserDefinedParameter.getName()+"' not found in new Kinetics");
                        }
                    }
                    rs.setKinetics(generalKinetics);
                    rs.rebindAllToModel(rs.getModel());
                }catch (ExpressionException | PropertyVetoException | ModelException e){
                    lg.error("failed to transform legacy mass action kinetics for reaction '"+rs.getName()+"'", e);
                }
            }else{
                // reinforce modern interpretation - set ambiguous mass action parameter to zero (will continue to be ignored by current math generation)
                kp.setExpression(new Expression(0.0));
            }
        }
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

    public static void applyMathOverrides(Simulation simulation, BioModel bioModel) throws MappingException, ExpressionException {
        if (simulation == null) {
            throw new RuntimeException("simulation was null");
        }
        SimulationContext simulationContext = null;
        for (SimulationContext sc : bioModel.getSimulationContexts()) {
            if (sc.getSimulation(simulation.getName()) == simulation) {
                simulationContext = sc;
                break;
            }
        }
        if (simulationContext == null) {
            throw new RuntimeException("simulation " + simulation.getName() + " instance not found in BioModel");
        }
        simulationContext.updateAll(false);
        MathOverrides mathOverrides = simulation.getMathOverrides();
        for (String overriddenConstantName : mathOverrides.getOverridenConstantNames()) {
            if (mathOverrides.isScan(overriddenConstantName)) {
                lg.warn("skipping application of math override for "+overriddenConstantName+" in simulation "+simulation.getName()+" in biomodel "+bioModel.getName());
                continue;
            }
            Variable var = simulationContext.getMathDescription().getVariable(overriddenConstantName);
            if (var == null){
                throw new RuntimeException("variable '"+overriddenConstantName+"' not found in math");
            }
            SymbolTableEntry[] ste = simulationContext.getMathDescription().getSourceSymbolMapping().getBiologicalSymbol(var);
            if (ste == null || ste.length != 1){
                throw new RuntimeException("biological symbol table entry for variable '"+overriddenConstantName+"'");
            }
            Expression defaultExpression = mathOverrides.getDefaultExpression(overriddenConstantName);
            Expression actualExpression = mathOverrides.getActualExpression(overriddenConstantName,0).flatten();
            if (!actualExpression.isNumeric()){
                throw new RuntimeException("applying non-numeric overrides to a biomodel not yet supported: "+overriddenConstantName+": default = "+defaultExpression.infix()+", actual = "+actualExpression.infix());
            }
            String msg = "found math '"+overriddenConstantName+"' as bio '"+Arrays.asList(ste)+"', now have to apply the override: from '"+defaultExpression.infix()+"' to '"+actualExpression+"'";
            if (ste[0] instanceof EditableSymbolTableEntry && ((EditableSymbolTableEntry)ste[0]).isExpressionEditable()){
                EditableSymbolTableEntry editableSTE = (EditableSymbolTableEntry) ste[0];
                editableSTE.setExpression(new Expression(actualExpression));
            }else{
                if (ste[0] instanceof Membrane.MembraneVoltage){
                    Membrane.MembraneVoltage membraneVoltage = (Membrane.MembraneVoltage) ste[0];
                    MembraneMapping membraneMapping = (MembraneMapping) simulationContext.getGeometryContext().getStructureMapping(membraneVoltage.getMembrane());
                    membraneMapping.getInitialVoltageParameter().setExpression(actualExpression);
                }else {
                    throw new RuntimeException("biomodel ste '" + ste[0] + "' not editable");
                }
            }
            System.out.println(msg);
        }
    }

}
