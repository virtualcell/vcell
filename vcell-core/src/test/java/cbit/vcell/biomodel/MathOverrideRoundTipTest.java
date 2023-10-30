package cbit.vcell.biomodel;

import cbit.util.xml.XmlUtil;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sedml.ModelFormat;
import org.vcell.sedml.PublicationMetadata;
import org.vcell.sedml.SEDMLExporter;
import org.vcell.test.Fast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Category(Fast.class)
@RunWith(Parameterized.class)
public class MathOverrideRoundTipTest {

    private final String filename;
    private final boolean bDebug = true;

    //
    // save state for zero side-effect
    //
    private String previousInstalldirPropertyValue;
    private String previousWorkingdirPropertyValue;
    private boolean previousWriteDebugFiles;

    public MathOverrideRoundTipTest(String filename) {
        this.filename = filename;
    }

    @Parameterized.Parameters
    public static List<String> filenames() {
        return Arrays.asList(
                "Biomodel_issue_554_r0.vcml",
                "Biomodel_issue_554_r1.vcml",
                "Biomodel_issue_554_r0_r1.vcml",
                "Biomodel_issue_554_r2.vcml",
                "Biomodel_issue_554_r3.vcml",
                "Biomodel_issue_554_r2_r3.vcml",
                "Biomodel_issue_554.vcml"
        );
    }

    @Before
    public void setup() {
        previousWorkingdirPropertyValue = PropertyLoader.getProperty(PropertyLoader.cliWorkingDir, null);
        PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, "../vcell-cli-utils");
        previousInstalldirPropertyValue = PropertyLoader.getProperty(PropertyLoader.installationRoot, null);
        PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
        NativeLib.combinej.load();
        this.previousWriteDebugFiles = SBMLExporter.bWriteDebugFiles;
        SBMLExporter.bWriteDebugFiles = bDebug;
    }

    @After
    public void teardown() {
        if (previousWorkingdirPropertyValue!=null) {
            PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, previousWorkingdirPropertyValue);
        }
        if (previousInstalldirPropertyValue!=null) {
            PropertyLoader.setProperty(PropertyLoader.installationRoot, previousInstalldirPropertyValue);
        }
        SBMLExporter.bWriteDebugFiles = previousWriteDebugFiles;
    }

    @Test
    public void test_General_Kinetics_Override_Roundtrip() throws Exception {
        System.err.println("file "+filename);
        BioModel bioModel = getBioModelFromResource(filename);
        bioModel.updateAll(false);

        File outputDir = Files.createTempDir();
        File omexFile = new File(outputDir, filename + ".omex");

        boolean bHasPython = true;
        boolean bRoundTripSBMLValidation = true;
        boolean bWriteOmexArchive = true;
        Optional<PublicationMetadata> publicationMetadata = Optional.empty();
        Predicate<SimulationContext> simContextFilter = (sc) -> true;
        SEDMLExporter.writeBioModel(bioModel, publicationMetadata, omexFile, ModelFormat.SBML, simContextFilter, bHasPython, bRoundTripSBMLValidation, bWriteOmexArchive);

        SBMLExporter.MemoryVCLogger memoryVCLogger = new SBMLExporter.MemoryVCLogger();
        List<BioModel> bioModels = XmlHelper.readOmex(omexFile, memoryVCLogger);

        Assert.assertTrue(memoryVCLogger.highPriority.size() == 0);

        File tempDir = Files.createTempDir();
        String origVcmlPath = new File(tempDir, "orig.vcml").getAbsolutePath();
        XmlUtil.writeXMLStringToFile(XmlHelper.bioModelToXML(bioModel), origVcmlPath, true);

        if (bDebug) {
            for (int i = 0; i < bioModels.size(); i++) {
                String rereadVcmlPath = new File(tempDir, "reread_" + i + ".vcml").getAbsolutePath();
                XmlUtil.writeXMLStringToFile(XmlHelper.bioModelToXML(bioModels.get(i)), rereadVcmlPath, true);
            }
            System.err.println("wrote original and final BioModel VCML files to " + tempDir.getAbsolutePath());
        }

        Assert.assertEquals("expecting 1 biomodel in round trip", 1, bioModels.size());

        // now compare the MathOverrides
    }
//
//
//        {
//            Simulation sim = bioModel_sbmlUnits.getSimulation(0);
//            MathOverrides mathOverrides = sim.getMathOverrides();
//
//            LinkedHashMap<String, OverrideInfo> expectedVCUnits = new LinkedHashMap<>();
//            expectedVCUnits.put("Kf_r0", new OverrideInfo("Kf_r0", "um2.s-1.molecules-1", new Expression("5.0 / KMOLE"), new Expression("21.0 / KMOLE")));
//            expectedVCUnits.put("Kf_r1", new OverrideInfo("Kf_r1", "s-1", new Expression(3.0), new Expression(22.0)));
//            expectedVCUnits.put("Kr_r0", new OverrideInfo("Kr_r0", "s-1", new Expression(7.0), new Expression(23.0)));
//            expectedVCUnits.put("Kr_r1", new OverrideInfo("Kr_r1", "s-1", new Expression(8.0), new Expression(24.0)));
//            expectedVCUnits.put("s0_init_uM_um", new OverrideInfo("s0_init_uM_um", "uM", new Expression("3.0 * KMOLE"), new Expression("11.0 * KMOLE")));
//            expectedVCUnits.put("s1_init_uM_um", new OverrideInfo("s1_init_uM_um", "uM", new Expression("4.0 * KMOLE"), new Expression("12.0 * KMOLE")));
//            expectedVCUnits.put("s2_init_uM_um", new OverrideInfo("s2_init_uM_um", "uM", new Expression("5.0 * KMOLE"), new Expression("13.0 * KMOLE")));
//            expectedVCUnits.put("s3_init_uM", new OverrideInfo("s3_init_uM", "uM", new Expression(1.0), new Expression(14.0)));
//            expectedVCUnits.put("s4_init_uM", new OverrideInfo("s3_init_uM", "uM", new Expression(2.0), new Expression(15.0)));
//
//            MathOverrides expectedMathOverrides = new MathOverrides(sim);
//            for (Map.Entry<String, OverrideInfo> entry : expectedVCUnits.entrySet()) {
//                expectedMathOverrides.putConstant(new Constant(entry.getKey(), entry.getValue().overrideExp));
//            }
//            boolean equiv = expectedMathOverrides.compareEqual(mathOverrides);
//            if (!equiv) {
//                for (String c : expectedMathOverrides.getOverridenConstantNames()) {
//                    Constant constant = expectedMathOverrides.getConstant(c);
//                    System.out.println("expected: " + constant.getName() + "=" + constant.getExpression().infix());
//                }
//                for (String c : mathOverrides.getOverridenConstantNames()) {
//                    Constant constant = mathOverrides.getConstant(c);
//                    System.out.println("parsed: " + constant.getName() + "=" + constant.getExpression().infix());
//                }
//            }
//            Assert.assertTrue("expected math overrides to match", equiv);
//        }


//    @Test
//    public void test_VCell_to_SBML_conversion_overrides_default_SBML() throws IOException, XmlParseException, ExpressionException, MatrixException, ModelException, MathException, MappingException {
//        BioModel bioModel_vcellUnits = getBioModelFromResource("BioModel_overrides_std_units.vcml");
//        {
//            Simulation sim = bioModel_vcellUnits.getSimulation(0);
//            MathOverrides mathOverrides = sim.getMathOverrides();
//
//            LinkedHashMap<String, OverrideInfo> expectedVCUnits = new LinkedHashMap<>();
//            expectedVCUnits.put("Kf_r0", new OverrideInfo("Kf_r0", "um2.s-1.molecules-1", new Expression(5.0), new Expression(21.0)));
//            expectedVCUnits.put("Kf_r1", new OverrideInfo("Kf_r1", "s-1", new Expression(3.0), new Expression(22.0)));
//            expectedVCUnits.put("Kr_r0", new OverrideInfo("Kr_r0", "s-1", new Expression(7.0), new Expression(23.0)));
//            expectedVCUnits.put("Kr_r1", new OverrideInfo("Kr_r1", "s-1", new Expression(8.0), new Expression(24.0)));
//            expectedVCUnits.put("s0_init_molecules_um_2", new OverrideInfo("s0_init_molecules_um_2", "molecules.um-2", new Expression(3.0), new Expression(11.0)));
//            expectedVCUnits.put("s1_init_molecules_um_2", new OverrideInfo("s1_init_molecules_um_2", "molecules.um-2", new Expression(4.0), new Expression(12.0)));
//            expectedVCUnits.put("s2_init_molecules_um_2", new OverrideInfo("s2_init_molecules_um_2", "molecules.um-2", new Expression(5.0), new Expression(13.0)));
//            expectedVCUnits.put("s3_init_uM", new OverrideInfo("s3_init_uM", "uM", new Expression(1.0), new Expression(14.0)));
//            expectedVCUnits.put("s4_init_uM", new OverrideInfo("s3_init_uM", "uM", new Expression(2.0), new Expression(15.0)));
//
//            MathOverrides expectedMathOverrides = new MathOverrides(sim);
//            for (Map.Entry<String, OverrideInfo> entry : expectedVCUnits.entrySet()) {
//                expectedMathOverrides.putConstant(new Constant(entry.getKey(), entry.getValue().overrideExp));
//            }
//            boolean equiv = expectedMathOverrides.compareEqual(mathOverrides);
//            if (!equiv) {
//                for (String c : expectedMathOverrides.getOverridenConstantNames()) {
//                    Constant constant = expectedMathOverrides.getConstant(c);
//                    System.out.println("expected: " + constant.getName() + "=" + constant.getExpression().infix());
//                }
//                for (String c : mathOverrides.getOverridenConstantNames()) {
//                    Constant constant = mathOverrides.getConstant(c);
//                    System.out.println("parsed: " + constant.getName() + "=" + constant.getExpression().infix());
//                }
//            }
//            Assert.assertTrue("expected math overrides to match", equiv);
//        }
//        BioModel bioModel_sbmlUnits = ModelUnitConverter.createBioModelWithSBMLUnitSystem(bioModel_vcellUnits);
//        {
//            Simulation sim = bioModel_sbmlUnits.getSimulation(0);
//            MathOverrides mathOverrides = sim.getMathOverrides();
//
//            LinkedHashMap<String, OverrideInfo> expectedVCUnits = new LinkedHashMap<>();
//            expectedVCUnits.put("Kf_r0", new OverrideInfo("Kf_r0", "dm2.s-1.umol-1", new Expression("5.0 * 100000.0 / KMOLE"), new Expression("21.0 * 100000.0 / KMOLE")));
//            expectedVCUnits.put("Kf_r1", new OverrideInfo("Kf_r1", "s-1", new Expression(3.0), new Expression(22.0)));
//            expectedVCUnits.put("Kr_r0", new OverrideInfo("Kr_r0", "s-1", new Expression(7.0), new Expression(23.0)));
//            expectedVCUnits.put("Kr_r1", new OverrideInfo("Kr_r1", "s-1", new Expression(8.0), new Expression(24.0)));
//            expectedVCUnits.put("s0_init_umol_dm_2", new OverrideInfo("s0_init_umol_dm_2", "umol.dm-2", new Expression("3.0 * 1.0E-5 * KMOLE"), new Expression("11.0 * 1.0E-5 * KMOLE")));
//            expectedVCUnits.put("s1_init_umol_dm_2", new OverrideInfo("s1_init_umol_dm_2", "umol.dm-2", new Expression("4.0 * 1.0E-5 * KMOLE"), new Expression("12.0 * 1.0E-5 * KMOLE")));
//            expectedVCUnits.put("s2_init_umol_dm_2", new OverrideInfo("s2_init_umol_dm_2", "nmol.dm-2", new Expression("5.0 * 1.0E-5 * KMOLE"), new Expression("13.0 * 1.0E-5 * KMOLE")));
//            expectedVCUnits.put("s3_init_umol_l_1", new OverrideInfo("s3_init_umol_l_1", "umol/l", new Expression(1.0), new Expression(14.0)));
//            expectedVCUnits.put("s4_init_umol_l_1", new OverrideInfo("s4_init_umol_l_1", "umol/l", new Expression(2.0), new Expression(15.0)));
//
//            MathOverrides expectedMathOverrides = new MathOverrides(sim);
//            for (Map.Entry<String, OverrideInfo> entry : expectedVCUnits.entrySet()) {
//                expectedMathOverrides.putConstant(new Constant(entry.getKey(), entry.getValue().overrideExp));
//            }
//            boolean equiv = expectedMathOverrides.compareEqual(mathOverrides);
//            if (!equiv) {
//                for (String c : expectedMathOverrides.getOverridenConstantNames()) {
//                    Constant constant = expectedMathOverrides.getConstant(c);
//                    System.out.println("expected: " + constant.getName() + "=" + constant.getExpression().flattenSafe().infix());
//                }
//                for (String c : mathOverrides.getOverridenConstantNames()) {
//                    Constant constant = mathOverrides.getConstant(c);
//                    System.out.println("parsed: " + constant.getName() + "=" + constant.getExpression().flattenSafe().infix());
//                }
//            }
//            Assert.assertTrue("expected math overrides to match", equiv);
//        }
//
//    }


    private static BioModel getBioModelFromResource(String fileName) throws IOException, XmlParseException {
        InputStream inputStream = MathOverrideRoundTipTest.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            String vcml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return XmlHelper.XMLToBioModel(new XMLSource(vcml));
        }
    }

}
