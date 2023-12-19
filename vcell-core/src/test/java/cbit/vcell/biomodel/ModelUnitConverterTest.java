package cbit.vcell.biomodel;

import cbit.image.ImageException;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.mapping.IllegalMappingException;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.*;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Pair;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class ModelUnitConverterTest {

    @Test
    public void test_unit_factors() throws ExpressionException {
        Model.ReservedSymbol KMOLE = new Model("").getKMOLE();
        VCUnitSystem unitSystem = new VCUnitSystem() {};
        VCUnitDefinition dimensionless = unitSystem.getInstance_DIMENSIONLESS();

        ArrayList<Pair<VCUnitDefinition, Expression>> tests = new ArrayList<Pair<VCUnitDefinition, Expression>>();
        tests.add(new Pair<>(
                unitSystem.getInstance("umol.dm-2").divideBy(unitSystem.getInstance("molecules.um-2")),
                new Expression("(1.0E-5 * pow(KMOLE,1.0))")));

        tests.add(new Pair<>(
                unitSystem.getInstance("dm2.s-1.umol-1").divideBy(unitSystem.getInstance("um2.s-1.molecules-1")),
                new Expression("(100000.0 * pow(KMOLE, -1.0))")));

        for (Pair<VCUnitDefinition, Expression> test : tests){
            VCUnitDefinition nearly_dimensionless_unit = test.one;
            Expression expectedFactor = test.two;
            Expression factor = ModelUnitConverter.getDimensionlessScaleFactor(nearly_dimensionless_unit, dimensionless, KMOLE);
            assertEquals(expectedFactor.flattenSafe().infix(), factor.flattenSafe().infix());
        }
    }

    @Test
    public void test_MathOverrides_unit_factors_are_idempotent() throws IOException, XmlParseException, ExpressionException, MatrixException, ModelException, MathException, MappingException, PropertyVetoException, GeometryException, ImageException, IllegalMappingException {
        final double Kf_value_orig = 2.0;
        final double Kr_value_orig = 3.0;
        final double Kf_value_override_orig = 22.0;
        final double Kr_value_override_orig = 33.0;

        BioModel orig_biomodel = new BioModel(null);
        Model orig_model = orig_biomodel.getModel();
        Membrane membrane = orig_model.addMembrane("membrane");
        Feature cytosol = orig_model.addFeature("cytosol");
        Species species_m = orig_model.addSpecies(new Species("m",""));
        Species species_c = orig_model.addSpecies(new Species("c",""));
        SpeciesContext m0 = orig_model.addSpeciesContext(new SpeciesContext(species_m,membrane));
        SpeciesContext c0 = orig_model.addSpeciesContext(new SpeciesContext(species_c,cytosol));
        SimpleReaction membraneReaction = new SimpleReaction(orig_model, membrane, "r0", true, "notes");
        orig_model.addReactionStep(membraneReaction);

        membraneReaction.addReactant(m0,1);
        membraneReaction.addProduct(c0,1);
        Kinetics.KineticsParameter kf_param_orig = membraneReaction.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KForward);
        Kinetics.KineticsParameter kr_param_orig = membraneReaction.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KReverse);
        kf_param_orig.setExpression(new Expression(Kf_value_orig));
        kr_param_orig.setExpression(new Expression(Kr_value_orig));
        assertEquals("s-1", kf_param_orig.getUnitDefinition().getSymbol());
        assertEquals("molecules.um-2.s-1.uM-1", kr_param_orig.getUnitDefinition().getSymbol());

        SimulationContext orig_simContext = orig_biomodel.addNewSimulationContext("app1", SimulationContext.Application.NETWORK_DETERMINISTIC);
        orig_biomodel.updateAll(false);
        MathDescription orig_math = orig_simContext.getMathDescription();
        Simulation orig_sim = new Simulation(orig_math, orig_simContext);
        orig_simContext.addSimulation(orig_sim);
        Constant Kf_const = (Constant) orig_math.getVariable("Kf");
        Constant Kr_const = (Constant) orig_math.getVariable("Kr");
        assertNotNull(Kf_const);
        assertNotNull(Kr_const);
        orig_sim.getMathOverrides().putConstant(new Constant(Kf_const.getName(), new Expression(Kf_value_override_orig)));
        orig_sim.getMathOverrides().putConstant(new Constant(Kr_const.getName(), new Expression(Kr_value_override_orig)));

        // change unit system
        ModelUnitSystem modelUnitSystem = getModelUnitSystem_SBML_uM_um3();
        BioModel bioModel_sbmlUnits = ModelUnitConverter.createBioModelWithNewUnitSystem(orig_biomodel, modelUnitSystem);
        Model model_sbmlUnits = bioModel_sbmlUnits.getModel();

        final double Kf_value_sbmlUnits = 2.0;
        final double Kr_value_sbmlUnits = 3.0;
        final double Kf_value_override_sbmlUnits = 22.0;
        final double Kr_value_override_sbmlUnits = 33.0;

        // test unit transform on model parameters (Kf, Kr)
        Kinetics.KineticsParameter Kf_param_sbmlUnits = model_sbmlUnits.getReactionSteps(0).getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KForward);
        Kinetics.KineticsParameter Kr_param_sbmlUnits = model_sbmlUnits.getReactionSteps(0).getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KReverse);
        assertEquals("s-1", Kf_param_sbmlUnits.getUnitDefinition().getSymbol());
        assertEquals("um.s-1", Kr_param_sbmlUnits.getUnitDefinition().getSymbol());
        assertEquals("" + Kf_value_sbmlUnits, Kf_param_sbmlUnits.getExpression().infix(), "Kf_sbmlUnits value doesn't match");
        assertEquals("(" + Kr_value_sbmlUnits + " * KMOLE)", Kr_param_sbmlUnits.getExpression().infix(), "Kr_sbmlUnits value doesn't match");

        // test unit transform on math constants
        Simulation sim_sbmlUnits = bioModel_sbmlUnits.getSimulation(0);
        Constant Kf_const_sbmlUnits = (Constant) sim_sbmlUnits.getMathDescription().getVariable("Kf");
        Constant Kr_const_sbmlUnits = (Constant) sim_sbmlUnits.getMathDescription().getVariable("Kr");
        assertEquals("" + Kf_value_sbmlUnits, Kf_const_sbmlUnits.getExpression().infix(), "Kf_const_sbmlUnits value doesn't match");
        assertEquals("(" + Kr_value_sbmlUnits + " * KMOLE)", Kr_const_sbmlUnits.getExpression().infix(), "Kr_const_sbmlUnits value doesn't match");

        // test unit transform on math overrides
        Expression Kf_override_sbmlUnits = sim_sbmlUnits.getMathOverrides().getActualExpression("Kf", 0);
        Expression Kr_override_sbmlUnits = sim_sbmlUnits.getMathOverrides().getActualExpression("Kr", 0);
        assertEquals("" + Kf_value_override_sbmlUnits, Kf_override_sbmlUnits.infix(), "Kf_override_sbmlUnits value doesn't match");
        assertEquals("(" + Kr_value_override_sbmlUnits + " * KMOLE)", Kr_override_sbmlUnits.infix(), "Kr_override_sbmlUnits value doesn't match");

        //
        // test that math overrides for copied model are unchanged
        //
        Simulation copied_sim = bioModel_sbmlUnits.getSimulationContext(0).copySimulation(sim_sbmlUnits);
        Expression Kf_override_sbmlUnits_copy = copied_sim.getMathOverrides().getActualExpression("Kf", 0);
        Expression Kr_override_sbmlUnits_copy = copied_sim.getMathOverrides().getActualExpression("Kr", 0);
        assertEquals("" + Kf_value_override_sbmlUnits, Kf_override_sbmlUnits_copy.infix(), "Kf_override_sbmlUnits value doesn't match");
        assertEquals("(" + Kr_value_override_sbmlUnits + " * KMOLE)", Kr_override_sbmlUnits_copy.infix(), "Kr_override_sbmlUnits value doesn't match");
    }

    @Test
    public void test_VCell_to_SBML_conversion_uM_um3() throws IOException, XmlParseException, ExpressionException, MatrixException, ModelException, MathException, MappingException {
        BioModel bioModel_vcellUnits = getBioModelFromResource("MathOverrides_UnitSystem_vcell_units.vcml");

        BioModel expected_bioModel_sbmlUnts = getBioModelFromResource("MathOverrides_UnitSystem_sbml_units.vcml");
        expected_bioModel_sbmlUnts.refreshDependencies();
        MathDescription expectedMathDescription = expected_bioModel_sbmlUnts.getSimulationContext(0).createNewMathMapping().getMathDescription();

        ModelUnitSystem modelUnitSystem = getModelUnitSystem_SBML_uM_um3();
        BioModel bioModel_sbmlUnits = ModelUnitConverter.createBioModelWithNewUnitSystem(bioModel_vcellUnits, modelUnitSystem);
        MathDescription mathDescription = bioModel_sbmlUnits.getSimulationContext(0).getMathDescription();

        //
        // test that maths are equivalent (math for new SBML translation equivalent to math of SBML translation)
        //
        MathCompareResults mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), expectedMathDescription, mathDescription);
        boolean mathMatches = mathCompareResults.isEquivalent();
        assertTrue(mathMatches, "expecting biomodel translation to default SBML units to match: reason: " + mathCompareResults.toDatabaseStatus());

        //
        // test that math overrides same for new SBML translation and saved SBML translation
        //
        Simulation expectedSim = expected_bioModel_sbmlUnts.getSimulation(0);
        Simulation sim = bioModel_sbmlUnits.getSimulation(0);
        boolean overridesMatch = expectedSim.getMathOverrides().compareEqual(sim.getMathOverrides());
        assertTrue(overridesMatch, "expecting math overrides to be equivalent");
    }

    @Test
    public void test_SBML_to_VCell_conversion() throws IOException, XmlParseException, ExpressionException, MatrixException, ModelException, MathException, MappingException {
        BioModel bioModel_sbmlUnts = getBioModelFromResource("MathOverrides_UnitSystem_sbml_units.vcml");

        BioModel expected_bioModel_vcellUnits = getBioModelFromResource("MathOverrides_UnitSystem_vcell_units.vcml");
        expected_bioModel_vcellUnits.refreshDependencies();
        MathDescription expectedMathDescription = expected_bioModel_vcellUnits.getSimulationContext(0).createNewMathMapping().getMathDescription();

        BioModel bioModel_vcellUnits = ModelUnitConverter.createBioModelWithNewUnitSystem(bioModel_sbmlUnts, ModelUnitSystem.createDefaultVCModelUnitSystem());
        MathDescription mathDescription = bioModel_vcellUnits.getSimulationContext(0).getMathDescription();

        //
        // test that maths are equivalent (math for new SBML translation equivalent to math of SBML translation)
        //
        MathCompareResults mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), expectedMathDescription, mathDescription);
        boolean mathMatches = mathCompareResults.isEquivalent();
        assertTrue(mathMatches, "expecting biomodel translation to default SBML units to match: reason: " + mathCompareResults.toDatabaseStatus());

        //
        // test that math overrides same for new SBML translation and saved SBML translation
        //
        Simulation expectedSim = expected_bioModel_vcellUnits.getSimulation(0);
        Simulation sim = bioModel_vcellUnits.getSimulation(0);
        boolean overridesMatch = expectedSim.getMathOverrides().compareEqual(sim.getMathOverrides());
        assertTrue(overridesMatch, "expecting math overrides to be equivalent");
    }

    static class OverrideInfo {
        final String constantName;
        final String unitSymbol;
        final Expression origExp;
        final Expression overrideExp;

        OverrideInfo(String constantName, String unitSymbol, Expression origExp, Expression overideExp) {
            this.constantName = constantName;
            this.unitSymbol = unitSymbol;
            this.origExp = origExp;
            this.overrideExp = overideExp;
        }
    }

    @Test
    public void test_VCell_to_SBML_conversion_overrides_uM_um3() throws IOException, XmlParseException, ExpressionException, MatrixException, ModelException, MathException, MappingException {
        BioModel bioModel_vcellUnits = getBioModelFromResource("BioModel_overrides_std_units.vcml");
        {
            Simulation sim = bioModel_vcellUnits.getSimulation(0);
            MathOverrides mathOverrides = sim.getMathOverrides();

            LinkedHashMap<String, OverrideInfo> expectedVCUnits = new LinkedHashMap<>();
            expectedVCUnits.put("Kf_r0", new OverrideInfo("Kf_r0", "um2.s-1.molecules-1", new Expression(5.0), new Expression(21.0)));
            expectedVCUnits.put("Kf_r1", new OverrideInfo("Kf_r1", "s-1", new Expression(3.0), new Expression(22.0)));
            expectedVCUnits.put("Kr_r0", new OverrideInfo("Kr_r0", "s-1", new Expression(7.0), new Expression(23.0)));
            expectedVCUnits.put("Kr_r1", new OverrideInfo("Kr_r1", "s-1", new Expression(8.0), new Expression(24.0)));
            expectedVCUnits.put("s0_init_molecules_um_2", new OverrideInfo("s0_init_molecules_um_2", "molecules.um-2", new Expression(3.0), new Expression(11.0)));
            expectedVCUnits.put("s1_init_molecules_um_2", new OverrideInfo("s1_init_molecules_um_2", "molecules.um-2", new Expression(4.0), new Expression(12.0)));
            expectedVCUnits.put("s2_init_molecules_um_2", new OverrideInfo("s2_init_molecules_um_2", "molecules.um-2", new Expression(5.0), new Expression(13.0)));
            expectedVCUnits.put("s3_init_uM", new OverrideInfo("s3_init_uM", "uM", new Expression(1.0), new Expression(14.0)));
            expectedVCUnits.put("s4_init_uM", new OverrideInfo("s3_init_uM", "uM", new Expression(2.0), new Expression(15.0)));

            MathOverrides expectedMathOverrides = new MathOverrides(sim);
            for (Map.Entry<String, OverrideInfo> entry : expectedVCUnits.entrySet()) {
                expectedMathOverrides.putConstant(new Constant(entry.getKey(), entry.getValue().overrideExp));
            }
            boolean equiv = expectedMathOverrides.compareEqual(mathOverrides);
            if (!equiv) {
                for (String c : expectedMathOverrides.getOverridenConstantNames()) {
                    Constant constant = expectedMathOverrides.getConstant(c);
                    System.out.println("expected: " + constant.getName() + "=" + constant.getExpression().infix());
                }
                for (String c : mathOverrides.getOverridenConstantNames()) {
                    Constant constant = mathOverrides.getConstant(c);
                    System.out.println("parsed: " + constant.getName() + "=" + constant.getExpression().infix());
                }
            }
            assertTrue(equiv, "expected math overrides to match");
        }
        ModelUnitSystem modelUnitSystem = getModelUnitSystem_SBML_uM_um3();
        BioModel bioModel_sbmlUnits = ModelUnitConverter.createBioModelWithNewUnitSystem(bioModel_vcellUnits, modelUnitSystem);
        {
            Simulation sim = bioModel_sbmlUnits.getSimulation(0);
            MathOverrides mathOverrides = sim.getMathOverrides();

            LinkedHashMap<String, OverrideInfo> expectedVCUnits = new LinkedHashMap<>();
            expectedVCUnits.put("Kf_r0", new OverrideInfo("Kf_r0", "um2.s-1.molecules-1", new Expression("5.0 / KMOLE"), new Expression("21.0 / KMOLE")));
            expectedVCUnits.put("Kf_r1", new OverrideInfo("Kf_r1", "s-1", new Expression(3.0), new Expression(22.0)));
            expectedVCUnits.put("Kr_r0", new OverrideInfo("Kr_r0", "s-1", new Expression(7.0), new Expression(23.0)));
            expectedVCUnits.put("Kr_r1", new OverrideInfo("Kr_r1", "s-1", new Expression(8.0), new Expression(24.0)));
            expectedVCUnits.put("s0_init_uM_um", new OverrideInfo("s0_init_uM_um", "uM", new Expression("3.0 * KMOLE"), new Expression("11.0 * KMOLE")));
            expectedVCUnits.put("s1_init_uM_um", new OverrideInfo("s1_init_uM_um", "uM", new Expression("4.0 * KMOLE"), new Expression("12.0 * KMOLE")));
            expectedVCUnits.put("s2_init_uM_um", new OverrideInfo("s2_init_uM_um", "uM", new Expression("5.0 * KMOLE"), new Expression("13.0 * KMOLE")));
            expectedVCUnits.put("s3_init_uM", new OverrideInfo("s3_init_uM", "uM", new Expression(1.0), new Expression(14.0)));
            expectedVCUnits.put("s4_init_uM", new OverrideInfo("s3_init_uM", "uM", new Expression(2.0), new Expression(15.0)));

            MathOverrides expectedMathOverrides = new MathOverrides(sim);
            for (Map.Entry<String, OverrideInfo> entry : expectedVCUnits.entrySet()) {
                expectedMathOverrides.putConstant(new Constant(entry.getKey(), entry.getValue().overrideExp));
            }
            boolean equiv = expectedMathOverrides.compareEqual(mathOverrides);
            if (!equiv) {
                for (String c : expectedMathOverrides.getOverridenConstantNames()) {
                    Constant constant = expectedMathOverrides.getConstant(c);
                    System.out.println("expected: " + constant.getName() + "=" + constant.getExpression().infix());
                }
                for (String c : mathOverrides.getOverridenConstantNames()) {
                    Constant constant = mathOverrides.getConstant(c);
                    System.out.println("parsed: " + constant.getName() + "=" + constant.getExpression().infix());
                }
            }
            assertTrue(equiv, "expected math overrides to match");
        }

    }

    private ModelUnitSystem getModelUnitSystem_SBML_uM_um3() {
        VCUnitSystem tempUnitSystem = new VCUnitSystem() {};
        VCUnitDefinition substanceUnit = tempUnitSystem.getInstance("uM.um3");
        VCUnitDefinition volumeUnit = tempUnitSystem.getInstance("um3");
        VCUnitDefinition areaUnit = tempUnitSystem.getInstance("um2");
        VCUnitDefinition lengthUnit = tempUnitSystem.getInstance("um");
        VCUnitDefinition timeUnit = tempUnitSystem.getInstance("s");
        ModelUnitSystem modelUnitSystem = ModelUnitSystem.createSBMLUnitSystem(substanceUnit, volumeUnit, areaUnit, lengthUnit, timeUnit);
        return modelUnitSystem;
    }

    @Test
    public void test_VCell_to_SBML_conversion_overrides_default_SBML() throws IOException, XmlParseException, ExpressionException, MatrixException, ModelException, MathException, MappingException {
        BioModel bioModel_vcellUnits = getBioModelFromResource("BioModel_overrides_std_units.vcml");
        {
            Simulation sim = bioModel_vcellUnits.getSimulation(0);
            MathOverrides mathOverrides = sim.getMathOverrides();

            LinkedHashMap<String, OverrideInfo> expectedVCUnits = new LinkedHashMap<>();
            expectedVCUnits.put("Kf_r0", new OverrideInfo("Kf_r0", "um2.s-1.molecules-1", new Expression(5.0), new Expression(21.0)));
            expectedVCUnits.put("Kf_r1", new OverrideInfo("Kf_r1", "s-1", new Expression(3.0), new Expression(22.0)));
            expectedVCUnits.put("Kr_r0", new OverrideInfo("Kr_r0", "s-1", new Expression(7.0), new Expression(23.0)));
            expectedVCUnits.put("Kr_r1", new OverrideInfo("Kr_r1", "s-1", new Expression(8.0), new Expression(24.0)));
            expectedVCUnits.put("s0_init_molecules_um_2", new OverrideInfo("s0_init_molecules_um_2", "molecules.um-2", new Expression(3.0), new Expression(11.0)));
            expectedVCUnits.put("s1_init_molecules_um_2", new OverrideInfo("s1_init_molecules_um_2", "molecules.um-2", new Expression(4.0), new Expression(12.0)));
            expectedVCUnits.put("s2_init_molecules_um_2", new OverrideInfo("s2_init_molecules_um_2", "molecules.um-2", new Expression(5.0), new Expression(13.0)));
            expectedVCUnits.put("s3_init_uM", new OverrideInfo("s3_init_uM", "uM", new Expression(1.0), new Expression(14.0)));
            expectedVCUnits.put("s4_init_uM", new OverrideInfo("s3_init_uM", "uM", new Expression(2.0), new Expression(15.0)));

            MathOverrides expectedMathOverrides = new MathOverrides(sim);
            for (Map.Entry<String, OverrideInfo> entry : expectedVCUnits.entrySet()) {
                expectedMathOverrides.putConstant(new Constant(entry.getKey(), entry.getValue().overrideExp));
            }
            boolean equiv = expectedMathOverrides.compareEqual(mathOverrides);
            if (!equiv) {
                for (String c : expectedMathOverrides.getOverridenConstantNames()) {
                    Constant constant = expectedMathOverrides.getConstant(c);
                    System.out.println("expected: " + constant.getName() + "=" + constant.getExpression().infix());
                }
                for (String c : mathOverrides.getOverridenConstantNames()) {
                    Constant constant = mathOverrides.getConstant(c);
                    System.out.println("parsed: " + constant.getName() + "=" + constant.getExpression().infix());
                }
            }
            assertTrue(equiv, "expected math overrides to match");
        }
        BioModel bioModel_sbmlUnits = ModelUnitConverter.createBioModelWithSBMLUnitSystem(bioModel_vcellUnits);
        {
            Simulation sim = bioModel_sbmlUnits.getSimulation(0);
            MathOverrides mathOverrides = sim.getMathOverrides();

            LinkedHashMap<String, OverrideInfo> expectedVCUnits = new LinkedHashMap<>();
            expectedVCUnits.put("Kf_r0", new OverrideInfo("Kf_r0", "dm2.s-1.umol-1", new Expression("5.0 * 100000.0 / KMOLE"), new Expression("21.0 * 100000.0 / KMOLE")));
            expectedVCUnits.put("Kf_r1", new OverrideInfo("Kf_r1", "s-1", new Expression(3.0), new Expression(22.0)));
            expectedVCUnits.put("Kr_r0", new OverrideInfo("Kr_r0", "s-1", new Expression(7.0), new Expression(23.0)));
            expectedVCUnits.put("Kr_r1", new OverrideInfo("Kr_r1", "s-1", new Expression(8.0), new Expression(24.0)));
            expectedVCUnits.put("s0_init_umol_dm_2", new OverrideInfo("s0_init_umol_dm_2", "umol.dm-2", new Expression("3.0 * 1.0E-5 * KMOLE"), new Expression("11.0 * 1.0E-5 * KMOLE")));
            expectedVCUnits.put("s1_init_umol_dm_2", new OverrideInfo("s1_init_umol_dm_2", "umol.dm-2", new Expression("4.0 * 1.0E-5 * KMOLE"), new Expression("12.0 * 1.0E-5 * KMOLE")));
            expectedVCUnits.put("s2_init_umol_dm_2", new OverrideInfo("s2_init_umol_dm_2", "nmol.dm-2", new Expression("5.0 * 1.0E-5 * KMOLE"), new Expression("13.0 * 1.0E-5 * KMOLE")));
            expectedVCUnits.put("s3_init_umol_l_1", new OverrideInfo("s3_init_umol_l_1", "umol/l", new Expression(1.0), new Expression(14.0)));
            expectedVCUnits.put("s4_init_umol_l_1", new OverrideInfo("s4_init_umol_l_1", "umol/l", new Expression(2.0), new Expression(15.0)));

            MathOverrides expectedMathOverrides = new MathOverrides(sim);
            for (Map.Entry<String, OverrideInfo> entry : expectedVCUnits.entrySet()) {
                expectedMathOverrides.putConstant(new Constant(entry.getKey(), entry.getValue().overrideExp));
            }
            boolean equiv = expectedMathOverrides.compareEqual(mathOverrides);
            if (!equiv) {
                for (String c : expectedMathOverrides.getOverridenConstantNames()) {
                    Constant constant = expectedMathOverrides.getConstant(c);
                    System.out.println("expected: " + constant.getName() + "=" + constant.getExpression().flattenSafe().infix());
                }
                for (String c : mathOverrides.getOverridenConstantNames()) {
                    Constant constant = mathOverrides.getConstant(c);
                    System.out.println("parsed: " + constant.getName() + "=" + constant.getExpression().flattenSafe().infix());
                }
            }
            assertTrue(equiv, "expected math overrides to match");
        }

    }


    private static BioModel getBioModelFromResource(String fileName) throws IOException, XmlParseException {
        InputStream inputStream = ModelUnitConverterTest.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            String vcml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return XmlHelper.XMLToBioModel(new XMLSource(vcml));
        }
    }

}
