package cbit.vcell.biomodel;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ModelCountAndConcentrationTest {

    @Test
    public void test_concentration_to_count() throws IOException, XmlParseException, PropertyVetoException, MappingException, MatrixException, ModelException, MathException, ExpressionException {
        BioModel bioModel_stoch_init_concentration = getBioModelFromResource("ExportScanTest2_stoch_concentration.vcml");

        BioModel expected_bioModel_stoch_init_count = getBioModelFromResource("ExportScanTest2_stoch_count.vcml");
        expected_bioModel_stoch_init_count.refreshDependencies();
        MathDescription expectedMathDescription = expected_bioModel_stoch_init_count.getSimulationContext(0).createNewMathMapping().getMathDescription();

        SimulationContext stoch_app = bioModel_stoch_init_concentration.getSimulationContext("stoch app");
        stoch_app.setUsingConcentration(false, true);

        //
        // test that maths are equivalent (math for the translation from concentration to count)
        //
        MathCompareResults mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), expectedMathDescription, stoch_app.getMathDescription());
        boolean mathMatches = mathCompareResults.isEquivalent();
        Assert.assertTrue("expecting concentration to count translation to match: reason: "+mathCompareResults.toDatabaseStatus(), mathMatches);

        //
        // test that math overrides same for new concentration to count translation saved BioModel
        //
        Simulation expectedSim = expected_bioModel_stoch_init_count.getSimulation(0);
        Simulation sim = bioModel_stoch_init_concentration.getSimulation(0);
        boolean overridesMatch = expectedSim.getMathOverrides().compareEqual(sim.getMathOverrides());
        Assert.assertTrue("expecting math overrides to be equivalent", overridesMatch);
    }

    @Test
    public void test_concentration_to_count_notransform() throws IOException, XmlParseException, PropertyVetoException, MappingException, MatrixException, ModelException, MathException, ExpressionException {
        BioModel bioModel_stoch_init_concentration = getBioModelFromResource("ExportScanTest2_stoch_concentration.vcml");

        BioModel expected_bioModel_stoch_init_count = getBioModelFromResource("ExportScanTest2_stoch_count.vcml");
        expected_bioModel_stoch_init_count.refreshDependencies();
        MathDescription expectedMathDescription = expected_bioModel_stoch_init_count.getSimulationContext(0).createNewMathMapping().getMathDescription();

        SimulationContext stoch_app = bioModel_stoch_init_concentration.getSimulationContext("stoch app");
        stoch_app.setUsingConcentration(false, false);

        //
        // test that math overrides are not the same for new concentration to count translation saved BioModel (because didn't transform)
        //
        Simulation expectedSim = expected_bioModel_stoch_init_count.getSimulation(0);
        Simulation sim = bioModel_stoch_init_concentration.getSimulation(0);
        boolean overridesMatch = expectedSim.getMathOverrides().compareEqual(sim.getMathOverrides());
        Assert.assertFalse("expecting math overrides to not be equivalent", overridesMatch);
    }

    @Test
    public void test_count_to_concentration() throws IOException, XmlParseException, PropertyVetoException, MappingException, MatrixException, ModelException, MathException, ExpressionException {
        BioModel bioModel_stoch_init_count = getBioModelFromResource("ExportScanTest2_stoch_count.vcml");

        BioModel expected_bioModel_stoch_init_concentration = getBioModelFromResource("ExportScanTest2_stoch_concentration.vcml");
        expected_bioModel_stoch_init_concentration.refreshDependencies();
        MathDescription expectedMathDescription = expected_bioModel_stoch_init_concentration.getSimulationContext(0).createNewMathMapping().getMathDescription();

        SimulationContext stoch_app = bioModel_stoch_init_count.getSimulationContext("stoch app");
        stoch_app.setUsingConcentration(true, true);

        //
        // test that maths are equivalent (math for the translation from concentration to count)
        //
        MathCompareResults mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), expectedMathDescription, stoch_app.getMathDescription());
        boolean mathMatches = mathCompareResults.isEquivalent();
        Assert.assertTrue("expecting concentration to count translation to match: reason: "+mathCompareResults.toDatabaseStatus(), mathMatches);

        //
        // test that math overrides same for new concentration to count translation saved BioModel
        //
        Simulation expectedSim = expected_bioModel_stoch_init_concentration.getSimulation(0);
        Simulation sim = bioModel_stoch_init_count.getSimulation(0);
        boolean overridesMatch = expectedSim.getMathOverrides().compareEqual(sim.getMathOverrides());
        Assert.assertTrue("expecting math overrides to be equivalent", overridesMatch);
    }

    @Test
    public void test_count_to_concentration_notransform() throws IOException, XmlParseException, PropertyVetoException, MappingException, MatrixException, ModelException, MathException, ExpressionException {
        BioModel bioModel_stoch_init_count = getBioModelFromResource("ExportScanTest2_stoch_count.vcml");

        BioModel expected_bioModel_stoch_init_concentration = getBioModelFromResource("ExportScanTest2_stoch_concentration.vcml");
        expected_bioModel_stoch_init_concentration.refreshDependencies();
        MathDescription expectedMathDescription = expected_bioModel_stoch_init_concentration.getSimulationContext(0).createNewMathMapping().getMathDescription();

        SimulationContext stoch_app = bioModel_stoch_init_count.getSimulationContext("stoch app");
        stoch_app.setUsingConcentration(true, false);

        //
        // test that math overrides not the same for new concentration to count translation saved BioModel (because didn't transform)
        //
        Simulation expectedSim = expected_bioModel_stoch_init_concentration.getSimulation(0);
        Simulation sim = bioModel_stoch_init_count.getSimulation(0);
        boolean overridesMatch = expectedSim.getMathOverrides().compareEqual(sim.getMathOverrides());
        Assert.assertFalse("expecting math overrides to not be equivalent", overridesMatch);
    }

    private static BioModel getBioModelFromResource(String fileName) throws IOException, XmlParseException {
        InputStream inputStream = ModelCountAndConcentrationTest.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            String vcml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return XmlHelper.XMLToBioModel(new XMLSource(vcml));
        }
    }

}
