package cbit.vcell.biomodel;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.units.UnitSystemProvider;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModelUnitConverterTest {

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
        Assert.assertTrue("expecting biomodel translation to default SBML units to match: reason: "+mathCompareResults.toDatabaseStatus(), mathMatches);

        //
        // test that math overrides same for new SBML translation and saved SBML translation
        //
        Simulation expectedSim = expected_bioModel_sbmlUnts.getSimulation(0);
        Simulation sim = bioModel_sbmlUnits.getSimulation(0);
        boolean overridesMatch = expectedSim.getMathOverrides().compareEqual(sim.getMathOverrides());
        Assert.assertTrue("expecting math overrides to be equivalent", overridesMatch);
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
        Assert.assertTrue("expecting biomodel translation to default SBML units to match: reason: "+mathCompareResults.toDatabaseStatus(), mathMatches);

        //
        // test that math overrides same for new SBML translation and saved SBML translation
        //
        Simulation expectedSim = expected_bioModel_vcellUnits.getSimulation(0);
        Simulation sim = bioModel_vcellUnits.getSimulation(0);
        boolean overridesMatch = expectedSim.getMathOverrides().compareEqual(sim.getMathOverrides());
        Assert.assertTrue("expecting math overrides to be equivalent", overridesMatch);
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
            Assert.assertTrue("expected math overrides to match", equiv);
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
            Assert.assertTrue("expected math overrides to match", equiv);
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
            Assert.assertTrue("expected math overrides to match", equiv);
        }
        BioModel bioModel_sbmlUnits = ModelUnitConverter.createBioModelWithSBMLUnitSystem(bioModel_vcellUnits);
        {
            Simulation sim = bioModel_sbmlUnits.getSimulation(0);
            MathOverrides mathOverrides = sim.getMathOverrides();

            LinkedHashMap<String, OverrideInfo> expectedVCUnits = new LinkedHashMap<>();
            expectedVCUnits.put("Kf_r0", new OverrideInfo("Kf_r0", "um2.s-1.molecules-1", new Expression("500.0 / KMOLE"), new Expression("2100.0 / KMOLE")));
            expectedVCUnits.put("Kf_r1", new OverrideInfo("Kf_r1", "s-1", new Expression(3.0), new Expression(22.0)));
            expectedVCUnits.put("Kr_r0", new OverrideInfo("Kr_r0", "s-1", new Expression(7.0), new Expression(23.0)));
            expectedVCUnits.put("Kr_r1", new OverrideInfo("Kr_r1", "s-1", new Expression(8.0), new Expression(24.0)));
            expectedVCUnits.put("s0_init_umol_dm_2", new OverrideInfo("s0_init_umol_dm_2", "umol/l", new Expression("0.03 * KMOLE"), new Expression("0.11 * KMOLE")));
            expectedVCUnits.put("s1_init_umol_dm_2", new OverrideInfo("s1_init_umol_dm_2", "umol/l", new Expression("0.04 * KMOLE"), new Expression("0.12 * KMOLE")));
            expectedVCUnits.put("s2_init_umol_dm_2", new OverrideInfo("s2_init_umol_dm_2", "nmol/l", new Expression("0.05 * KMOLE"), new Expression("0.13 * KMOLE")));
            expectedVCUnits.put("s3_init_umol_l_1", new OverrideInfo("s3_init_umol_l_1", "umol/l", new Expression(1000.0), new Expression(14000.0)));
            expectedVCUnits.put("s4_init_umol_l_1", new OverrideInfo("s4_init_umol_l_1", "umol/l", new Expression(2000.0), new Expression(15000.0)));

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
            Assert.assertTrue("expected math overrides to match", equiv);
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
