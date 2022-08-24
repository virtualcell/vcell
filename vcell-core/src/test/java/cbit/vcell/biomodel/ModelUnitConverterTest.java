package cbit.vcell.biomodel;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
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

public class ModelUnitConverterTest {

    @Test
    public void test_VCell_to_SBML_conversion() throws IOException, XmlParseException, ExpressionException, MatrixException, ModelException, MathException, MappingException {
        BioModel bioModel_vcellUnits = getBioModelFromResource("MathOverrides_UnitSystem_vcell_units.vcml");

        BioModel expected_bioModel_sbmlUnts = getBioModelFromResource("MathOverrides_UnitSystem_sbml_units.vcml");
        expected_bioModel_sbmlUnts.refreshDependencies();
        MathDescription expectedMathDescription = expected_bioModel_sbmlUnts.getSimulationContext(0).createNewMathMapping().getMathDescription();

        BioModel bioModel_sbmlUnits = ModelUnitConverter.createBioModelWithSBMLUnitSystem(bioModel_vcellUnits);
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
//        Simulation expectedSim = expected_bioModel_vcellUnits.getSimulation(0);
//        Simulation sim = bioModel_vcellUnits.getSimulation(0);
//        boolean overridesMatch = expectedSim.getMathOverrides().compareEqual(sim.getMathOverrides());
//        Assert.assertTrue("expecting math overrides to be equivalent", overridesMatch);
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
