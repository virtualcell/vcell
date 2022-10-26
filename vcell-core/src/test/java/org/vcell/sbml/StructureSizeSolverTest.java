package org.vcell.sbml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.*;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.junit.Assert;
import org.junit.Test;
import org.vcell.sbml.vcell.StructureSizeSolver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class StructureSizeSolverTest {

    @Test
    public void test_mathGenerationEquivalence() throws XmlParseException, MappingException, IOException {
        InputStream legacyBioModelInputStream = VcmlTestSuiteFiles.getVcmlTestCase("biomodel_89712092_simple.vcml");
        String legacyModelVcml = new BufferedReader(new InputStreamReader(legacyBioModelInputStream))
                .lines().collect(Collectors.joining("\n"));

        BioModel legacyBioModel = XmlHelper.XMLToBioModel(new XMLSource(legacyModelVcml));
        legacyBioModel.refreshDependencies();
        MathDescription legacyMathDescription = legacyBioModel.getSimulationContext(0).getMathDescription();

        BioModel legacyBioModelCloned = XmlHelper.cloneBioModel(legacyBioModel);
        legacyBioModelCloned.updateAll(false);
        MathDescription newMathDescription = legacyBioModelCloned.getSimulationContext(0).getMathDescription();
        final File dataDir = new File("/Users/schaff/Documents/workspace/vcell/vcell-core/src/test/resources/org/vcell/sbml/vcml_published");
        final File newVCML = new File(dataDir,"biomodel_89712092_simple_regenerated.vcml");
        Files.write(newVCML.toPath(), XmlHelper.bioModelToXML(legacyBioModelCloned).getBytes(StandardCharsets.UTF_8));

        MathCompareResults results = MathDescription.testEquivalency(
                SimulationSymbolTable.createMathSymbolTableFactory(), legacyMathDescription, newMathDescription);
        Assert.assertTrue("results should be equivalent: "+results.toDatabaseStatus(),results.isEquivalent());
    }

    @Test
    public void test_legacyTransformer() throws XmlParseException, ExpressionException {
        InputStream legacyBioModelInputStream = VcmlTestSuiteFiles.getVcmlTestCase("biomodel_89712092_simple.vcml");
        String legacyModelVcml = new BufferedReader(new InputStreamReader(legacyBioModelInputStream))
                .lines().collect(Collectors.joining("\n"));

        //
        // BEFORE TRANSFORMATION, verify state of model
        //
        {
            //
            // check that original Application has relative sizes set as expected, and that all absolute sizes are null
            //
            BioModel legacyBioModel = XmlHelper.XMLToBioModel(new XMLSource(legacyModelVcml));
            GeometryContext geometryContext = legacyBioModel.getSimulationContext(0).getGeometryContext();
            Model model = legacyBioModel.getModel();

            Structure NM = model.getStructure("NM");
            StructureMapping.StructureMappingParameter SurfToVol_NM = geometryContext.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_SurfaceToVolumeRatio);
            StructureMapping.StructureMappingParameter VolFract_Nucleus = geometryContext.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_VolumeFraction);
            StructureMapping.StructureMappingParameter Size_NM = geometryContext.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_Size);

            Structure PM = model.getStructure("PM");
            StructureMapping.StructureMappingParameter SurfToVol_PM = geometryContext.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_SurfaceToVolumeRatio);
            StructureMapping.StructureMappingParameter VolFract_Cytosol = geometryContext.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_VolumeFraction);
            StructureMapping.StructureMappingParameter Size_PM = geometryContext.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_Size);

            Structure Nucleus = model.getStructure("Nucleus");
            StructureMapping.StructureMappingParameter Size_Nucleus = geometryContext.getStructureMapping(Nucleus).getParameterFromRole(StructureMapping.ROLE_Size);

            Structure Cytosol = model.getStructure("Cytosol");
            StructureMapping.StructureMappingParameter Size_Cytosol = geometryContext.getStructureMapping(Cytosol).getParameterFromRole(StructureMapping.ROLE_Size);

            Structure Extracellular = model.getStructure("Extraclellular");
            StructureMapping.StructureMappingParameter Size_Extracellular = geometryContext.getStructureMapping(Extracellular).getParameterFromRole(StructureMapping.ROLE_Size);

            Assert.assertEquals("unexpected SurfToVol_NM", "1.0", SurfToVol_NM.getExpression().infix());
            Assert.assertEquals("unexpected VolFract_Nucleus", "0.1", VolFract_Nucleus.getExpression().infix());
            Assert.assertEquals("unexpected SurfToVol_PM", "0.263", SurfToVol_PM.getExpression().infix());
            Assert.assertEquals("unexpected VolFract_Cytosol", "0.8", VolFract_Cytosol.getExpression().infix());

            Assert.assertEquals("expected Size_NM exp to be initially null", null, Size_NM.getExpression());
            Assert.assertEquals("expected Size_PM exp to be initially null", null, Size_PM.getExpression());
            Assert.assertEquals("expected Size_Nucleus exp to initially be null", null, Size_Nucleus.getExpression());
            Assert.assertEquals("expected Size_Cytosol exp to initially be null", null, Size_Cytosol.getExpression());
            Assert.assertEquals("expected Size_Extracellular exp to initially be null", null, Size_Extracellular.getExpression());
        }

        //
        // AFTER TRANSFORMATION, verify state of model
        //
        {
            //
            // check that transformed Application has absolute sizes set as expected
            //
            BioModel legacyBioModelCloned = XmlHelper.XMLToBioModel(new XMLSource(legacyModelVcml));
            Model model = legacyBioModelCloned.getModel();

            // transform the model using Legacy Transformer
            SimulationContext.MathMappingCallback mmc = new MathMappingCallbackTaskAdapter(null);
            LegacySimContextTransformer legacySimContextTransformer = new LegacySimContextTransformer();
            SimContextTransformer.SimContextTransformation simContextTransformation = legacySimContextTransformer.transform(legacyBioModelCloned.getSimulationContext(0), mmc, null);
            GeometryContext geometryContextTransformed = simContextTransformation.transformedSimContext.getGeometryContext();


            Structure NM = model.getStructure("NM");
            StructureMapping.StructureMappingParameter SurfToVol_NM = geometryContextTransformed.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_SurfaceToVolumeRatio);
            StructureMapping.StructureMappingParameter VolFract_Nucleus = geometryContextTransformed.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_VolumeFraction);
            StructureMapping.StructureMappingParameter Size_NM_transformed = geometryContextTransformed.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_Size);

            Structure PM = model.getStructure("PM");
            StructureMapping.StructureMappingParameter SurfToVol_PM = geometryContextTransformed.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_SurfaceToVolumeRatio);
            StructureMapping.StructureMappingParameter VolFract_Cytosol = geometryContextTransformed.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_VolumeFraction);
            StructureMapping.StructureMappingParameter Size_PM_transformed = geometryContextTransformed.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_Size);

            Structure Nucleus = model.getStructure("Nucleus");
            StructureMapping.StructureMappingParameter Size_Nucleus_transformed = geometryContextTransformed.getStructureMapping(Nucleus).getParameterFromRole(StructureMapping.ROLE_Size);

            Structure Cytosol = model.getStructure("Cytosol");
            StructureMapping.StructureMappingParameter Size_Cytosol_transformed = geometryContextTransformed.getStructureMapping(Cytosol).getParameterFromRole(StructureMapping.ROLE_Size);

            Structure Extracellular = model.getStructure("Extraclellular");
            StructureMapping.StructureMappingParameter Size_Extracellular_transformed = geometryContextTransformed.getStructureMapping(Extracellular).getParameterFromRole(StructureMapping.ROLE_Size);

            Assert.assertEquals("unexpected SurfToVol_NM", "1.0", SurfToVol_NM.getExpression().infix());
            Assert.assertEquals("unexpected VolFract_Nucleus", "0.1", VolFract_Nucleus.getExpression().infix());
            Assert.assertEquals("unexpected SurfToVol_PM", "0.263", SurfToVol_PM.getExpression().infix());
            Assert.assertEquals("unexpected VolFract_Cytosol", "0.8", VolFract_Cytosol.getExpression().infix());

            String[] symbols = new String[] {
                    "Cytosol_mapping.Size",
                    "NM_mapping.VolFraction",
                    "PM_mapping.VolFraction",
                    "NM_mapping.SurfToVolRatio",
                    "PM_mapping.SurfToVolRatio"
            };
            SimpleSymbolTable symbolTable = new SimpleSymbolTable(symbols);

            // chosen as 1.0 for Cytosol size as it is the first StructureMapping (see LegacySimContextTransformer.transform)
            double specified_cytosol_size = 1.0;
            double original_SurfToVol_NM = 1.0;
            double original_VolFract_Nucleus = 0.1;
            double original_SurfToVol_PM = 0.263;
            double original_VolFract_Cytosol = 0.8;

            double[] values = new double[] {
                    specified_cytosol_size,
                    original_VolFract_Nucleus,
                    original_VolFract_Cytosol,
                    original_SurfToVol_NM,
                    original_SurfToVol_PM
            };

            double val_Size_Cytosol = new Expression(Size_Cytosol_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_Extracellular = new Expression(Size_Extracellular_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_NM = new Expression(Size_NM_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_Nucleus = new Expression(Size_Nucleus_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_PM = new Expression(Size_PM_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);

            double expected_Size_Cytosol = specified_cytosol_size;
            double expected_Size_Extracellular = 0.27777777777658313;
            double expected_Size_NM = 0.11111111111111004;
            double expected_Size_Nucleus = 0.11111111111150196;
            double expected_Size_PM = 0.29222222227462435;

            Assert.assertTrue("unexpected Size_NM", equiv(expected_Size_NM, val_Size_NM));
            Assert.assertTrue("unexpected Size_PM, "+expected_Size_PM+" !~ "+val_Size_PM, equiv(expected_Size_PM, val_Size_PM));
            Assert.assertTrue("unexpected Size_Nucleus, "+expected_Size_Nucleus+" !~ "+val_Size_Nucleus, equiv(expected_Size_Nucleus, val_Size_Nucleus));
            Assert.assertTrue("unexpected Size_Cytosol, "+expected_Size_Cytosol+" !~ "+val_Size_Cytosol, equiv(expected_Size_Cytosol, val_Size_Cytosol));
            Assert.assertTrue("unexpected Size_Extracellular, "+expected_Size_Extracellular+" !~ "+val_Size_Extracellular, equiv(expected_Size_Extracellular, val_Size_Extracellular));

            //
            // verify expected relationships between relative and absolute sizes for this model
            //
            double val_VolFract_Nucleus = VolFract_Nucleus.getExpression().evaluateConstant();
            double val_VolFract_Cytosol = VolFract_Cytosol.getExpression().evaluateConstant();
            double val_SurfToVol_NM = SurfToVol_NM.getExpression().evaluateConstant();
            double val_SurfToVol_PM = SurfToVol_PM.getExpression().evaluateConstant();

            //
            // verify relative sizes in terms of solved absolute sizes
            //
            Assert.assertTrue("VolFract_Nucleus value doesn't match solution", equiv(val_VolFract_Nucleus, val_Size_Nucleus / (val_Size_Cytosol + val_Size_Nucleus)));
            Assert.assertTrue("VolFract_Cytosol value doesn't match solution", equiv(val_VolFract_Cytosol, (val_Size_Cytosol + val_Size_Nucleus) / (val_Size_Extracellular + val_Size_Cytosol + val_Size_Nucleus)));
            Assert.assertTrue("SurfToVol_NM value doesn't match solution", equiv(val_SurfToVol_NM, val_Size_NM / val_Size_Nucleus));
            Assert.assertTrue("SurfToVol_PM value doesn't match solution", equiv(val_SurfToVol_PM, val_Size_PM / (val_Size_Cytosol + val_Size_Nucleus)));
        }
    }

    private static boolean equiv(double v1, double v2){
        double scale = Math.max(Math.abs(v1),Math.abs(v2));
        double absDiff = Math.abs(v1-v2);
        double reltol = 1e-8;
        boolean equiv = (absDiff < scale * reltol);
        return equiv;
    }

    @Test
    public void test_updateAbsoluteStructureSizes() throws Exception {
        InputStream legacyBioModelInputStream = VcmlTestSuiteFiles.getVcmlTestCase("biomodel_89712092_simple.vcml");
        String legacyModelVcml = new BufferedReader(new InputStreamReader(legacyBioModelInputStream))
                .lines().collect(Collectors.joining("\n"));

        {
            //
            // check that transformed Application has absolute sizes set as expected
            //
            BioModel legacyBioModelCloned = XmlHelper.XMLToBioModel(new XMLSource(legacyModelVcml));
            Model model = legacyBioModelCloned.getModel();

            double specified_cytosol_size = 1.0;

            // transform the model using Legacy Transformer
            SimulationContext.MathMappingCallback mmc = new MathMappingCallbackTaskAdapter(null);
            Structure Cytosol = model.getStructure("Cytosol");
            Structure NM = model.getStructure("NM");
            Structure PM = model.getStructure("PM");
            Structure Nucleus = model.getStructure("Nucleus");
            Structure Extracellular = model.getStructure("Extraclellular"); // sic

            SimulationContext simulationContext = legacyBioModelCloned.getSimulationContext(0);
            StructureMapping structureMapping_Cytosol = simulationContext.getGeometryContext().getStructureMapping(Cytosol);
            StructureSizeSolver.updateAbsoluteStructureSizes(simulationContext, Cytosol, specified_cytosol_size, structureMapping_Cytosol.getSizeParameter().getUnitDefinition());
            GeometryContext geometryContextTransformed = simulationContext.getGeometryContext();

            StructureMapping.StructureMappingParameter SurfToVol_NM = geometryContextTransformed.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_SurfaceToVolumeRatio);
            StructureMapping.StructureMappingParameter VolFract_Nucleus = geometryContextTransformed.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_VolumeFraction);
            StructureMapping.StructureMappingParameter Size_NM_transformed = geometryContextTransformed.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_Size);
            StructureMapping.StructureMappingParameter SurfToVol_PM = geometryContextTransformed.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_SurfaceToVolumeRatio);
            StructureMapping.StructureMappingParameter VolFract_Cytosol = geometryContextTransformed.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_VolumeFraction);
            StructureMapping.StructureMappingParameter Size_PM_transformed = geometryContextTransformed.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_Size);
            StructureMapping.StructureMappingParameter Size_Nucleus_transformed = geometryContextTransformed.getStructureMapping(Nucleus).getParameterFromRole(StructureMapping.ROLE_Size);
            StructureMapping.StructureMappingParameter Size_Cytosol_transformed = geometryContextTransformed.getStructureMapping(Cytosol).getParameterFromRole(StructureMapping.ROLE_Size);
            StructureMapping.StructureMappingParameter Size_Extracellular_transformed = geometryContextTransformed.getStructureMapping(Extracellular).getParameterFromRole(StructureMapping.ROLE_Size);

            double original_SurfToVol_NM = 1.0;
            double original_VolFract_Nucleus = 0.1;
            double original_SurfToVol_PM = 0.263;
            double original_VolFract_Cytosol = 0.8;

            Assert.assertEquals("unexpected SurfToVol_NM", Double.toString(original_SurfToVol_NM), SurfToVol_NM.getExpression().infix());
            Assert.assertEquals("unexpected VolFract_Nucleus", Double.toString(original_VolFract_Nucleus), VolFract_Nucleus.getExpression().infix());
            Assert.assertEquals("unexpected SurfToVol_PM", Double.toString(original_SurfToVol_PM), SurfToVol_PM.getExpression().infix());
            Assert.assertEquals("unexpected VolFract_Cytosol", Double.toString(original_VolFract_Cytosol), VolFract_Cytosol.getExpression().infix());

            //
            // verify expected relationships between relative and absolute sizes for this model
            //
            double val_VolFract_Nucleus = VolFract_Nucleus.getExpression().evaluateConstant();
            double val_VolFract_Cytosol = VolFract_Cytosol.getExpression().evaluateConstant();
            double val_SurfToVol_NM = SurfToVol_NM.getExpression().evaluateConstant();
            double val_SurfToVol_PM = SurfToVol_PM.getExpression().evaluateConstant();

            String[] symbols = new String[] {
                    "Cytosol_mapping.Size",
                    "NM_mapping.VolFraction",
                    "PM_mapping.VolFraction",
                    "NM_mapping.SurfToVolRatio",
                    "PM_mapping.SurfToVolRatio"
            };
            SimpleSymbolTable symbolTable = new SimpleSymbolTable(symbols);
            double[] values = new double[] {
                    specified_cytosol_size,
                    original_VolFract_Nucleus,
                    original_VolFract_Cytosol,
                    original_SurfToVol_NM,
                    original_SurfToVol_PM
            };

            //
            // clone expressions, bind to local symbol table, and evaluate using values from array above.
            //
            double val_Size_Cytosol = new Expression(Size_Cytosol_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_Extracellular = new Expression(Size_Extracellular_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_NM = new Expression(Size_NM_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_Nucleus = new Expression(Size_Nucleus_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_PM = new Expression(Size_PM_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);

            //
            // verify relative sizes in terms of solved absolute sizes
            //
            Assert.assertTrue("VolFract_Nucleus value doesn't match solution", equiv(val_VolFract_Nucleus, val_Size_Nucleus / (val_Size_Cytosol + val_Size_Nucleus)));
            Assert.assertTrue("VolFract_Cytosol value doesn't match solution", equiv(val_VolFract_Cytosol, (val_Size_Cytosol + val_Size_Nucleus) / (val_Size_Extracellular + val_Size_Cytosol + val_Size_Nucleus)));
            Assert.assertTrue("SurfToVol_NM value doesn't match solution", equiv(val_SurfToVol_NM, val_Size_NM / val_Size_Nucleus));
            Assert.assertTrue("SurfToVol_PM value doesn't match solution", equiv(val_SurfToVol_PM, val_Size_PM / (val_Size_Cytosol + val_Size_Nucleus)));

            double expected_Size_Cytosol = specified_cytosol_size;
            double expected_Size_Extracellular = 0.27777777777658313;
            double expected_Size_NM = 0.11111111111111004;
            double expected_Size_Nucleus = 0.11111111111150196;
            double expected_Size_PM = 0.29222222227462435;

            Assert.assertTrue("unexpected Size_NM", equiv(expected_Size_NM, val_Size_NM));
            Assert.assertTrue("unexpected Size_PM, "+expected_Size_PM+" !~ "+val_Size_PM, equiv(expected_Size_PM, val_Size_PM));
            Assert.assertTrue("unexpected Size_Nucleus, "+expected_Size_Nucleus+" !~ "+val_Size_Nucleus, equiv(expected_Size_Nucleus, val_Size_Nucleus));
            Assert.assertTrue("unexpected Size_Cytosol, "+expected_Size_Cytosol+" !~ "+val_Size_Cytosol, equiv(expected_Size_Cytosol, val_Size_Cytosol));
            Assert.assertTrue("unexpected Size_Extracellular, "+expected_Size_Extracellular+" !~ "+val_Size_Extracellular, equiv(expected_Size_Extracellular, val_Size_Extracellular));
        }
    }

    @Test
    public void test_updateAbsoluteStructureSizes_symbolic() throws Exception {
        InputStream legacyBioModelInputStream = VcmlTestSuiteFiles.getVcmlTestCase("biomodel_89712092_simple.vcml");
        String legacyModelVcml = new BufferedReader(new InputStreamReader(legacyBioModelInputStream))
                .lines().collect(Collectors.joining("\n"));

        {
            //
            // check that transformed Application has absolute sizes set as expected
            //
            BioModel legacyBioModelCloned = XmlHelper.XMLToBioModel(new XMLSource(legacyModelVcml));
            Model model = legacyBioModelCloned.getModel();

            double specified_cytosol_size = 1.0;

            // transform the model using Legacy Transformer
            SimulationContext.MathMappingCallback mmc = new MathMappingCallbackTaskAdapter(null);
            Structure Cytosol = model.getStructure("Cytosol");
            Structure NM = model.getStructure("NM");
            Structure PM = model.getStructure("PM");
            Structure Nucleus = model.getStructure("Nucleus");
            Structure Extracellular = model.getStructure("Extraclellular"); // sic

            SimulationContext simulationContext = legacyBioModelCloned.getSimulationContext(0);
            StructureMapping structureMapping_Cytosol = simulationContext.getGeometryContext().getStructureMapping(Cytosol);
            StructureSizeSolver.updateAbsoluteStructureSizes_symbolic(simulationContext, Cytosol, specified_cytosol_size, structureMapping_Cytosol.getSizeParameter().getUnitDefinition());
            GeometryContext geometryContextTransformed = simulationContext.getGeometryContext();

            StructureMapping.StructureMappingParameter SurfToVol_NM = geometryContextTransformed.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_SurfaceToVolumeRatio);
            StructureMapping.StructureMappingParameter VolFract_Nucleus = geometryContextTransformed.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_VolumeFraction);
            StructureMapping.StructureMappingParameter Size_NM_transformed = geometryContextTransformed.getStructureMapping(NM).getParameterFromRole(StructureMapping.ROLE_Size);
            StructureMapping.StructureMappingParameter SurfToVol_PM = geometryContextTransformed.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_SurfaceToVolumeRatio);
            StructureMapping.StructureMappingParameter VolFract_Cytosol = geometryContextTransformed.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_VolumeFraction);
            StructureMapping.StructureMappingParameter Size_PM_transformed = geometryContextTransformed.getStructureMapping(PM).getParameterFromRole(StructureMapping.ROLE_Size);
            StructureMapping.StructureMappingParameter Size_Nucleus_transformed = geometryContextTransformed.getStructureMapping(Nucleus).getParameterFromRole(StructureMapping.ROLE_Size);
            StructureMapping.StructureMappingParameter Size_Cytosol_transformed = geometryContextTransformed.getStructureMapping(Cytosol).getParameterFromRole(StructureMapping.ROLE_Size);
            StructureMapping.StructureMappingParameter Size_Extracellular_transformed = geometryContextTransformed.getStructureMapping(Extracellular).getParameterFromRole(StructureMapping.ROLE_Size);

            double original_SurfToVol_NM = 1.0;
            double original_VolFract_Nucleus = 0.1;
            double original_SurfToVol_PM = 0.263;
            double original_VolFract_Cytosol = 0.8;

            Assert.assertEquals("unexpected SurfToVol_NM", Double.toString(original_SurfToVol_NM), SurfToVol_NM.getExpression().infix());
            Assert.assertEquals("unexpected VolFract_Nucleus", Double.toString(original_VolFract_Nucleus), VolFract_Nucleus.getExpression().infix());
            Assert.assertEquals("unexpected SurfToVol_PM", Double.toString(original_SurfToVol_PM), SurfToVol_PM.getExpression().infix());
            Assert.assertEquals("unexpected VolFract_Cytosol", Double.toString(original_VolFract_Cytosol), VolFract_Cytosol.getExpression().infix());

            //
            // verify expected relationships between relative and absolute sizes for this model
            //
            double val_VolFract_Nucleus = VolFract_Nucleus.getExpression().evaluateConstant();
            double val_VolFract_Cytosol = VolFract_Cytosol.getExpression().evaluateConstant();
            double val_SurfToVol_NM = SurfToVol_NM.getExpression().evaluateConstant();
            double val_SurfToVol_PM = SurfToVol_PM.getExpression().evaluateConstant();

            String[] symbols = new String[] {
                    "Cytosol_mapping.Size",
                    "NM_mapping.VolFraction",
                    "PM_mapping.VolFraction",
                    "NM_mapping.SurfToVolRatio",
                    "PM_mapping.SurfToVolRatio"
            };
            SimpleSymbolTable symbolTable = new SimpleSymbolTable(symbols);
            double[] values = new double[] {
                    specified_cytosol_size,
                    original_VolFract_Nucleus,
                    original_VolFract_Cytosol,
                    original_SurfToVol_NM,
                    original_SurfToVol_PM
            };

            //
            // clone expressions, bind to local symbol table, and evaluate using values from array above.
            //
            double val_Size_Cytosol = new Expression(Size_Cytosol_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_Extracellular = new Expression(Size_Extracellular_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_NM = new Expression(Size_NM_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_Nucleus = new Expression(Size_Nucleus_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);
            double val_Size_PM = new Expression(Size_PM_transformed.getExpression()).bindExpressionAndReturn(symbolTable).evaluateVector(values);

            //
            // verify relative sizes in terms of solved absolute sizes
            //
            Assert.assertTrue("VolFract_Nucleus value doesn't match solution", equiv(val_VolFract_Nucleus, val_Size_Nucleus / (val_Size_Cytosol + val_Size_Nucleus)));
            Assert.assertTrue("VolFract_Cytosol value doesn't match solution", equiv(val_VolFract_Cytosol, (val_Size_Cytosol + val_Size_Nucleus) / (val_Size_Extracellular + val_Size_Cytosol + val_Size_Nucleus)));
            Assert.assertTrue("SurfToVol_NM value doesn't match solution", equiv(val_SurfToVol_NM, val_Size_NM / val_Size_Nucleus));
            Assert.assertTrue("SurfToVol_PM value doesn't match solution", equiv(val_SurfToVol_PM, val_Size_PM / (val_Size_Cytosol + val_Size_Nucleus)));

            double expected_Size_Cytosol = specified_cytosol_size;
            double expected_Size_Extracellular = 0.27777777777658313;
            double expected_Size_NM = 0.11111111111111004;
            double expected_Size_Nucleus = 0.11111111111150196;
            double expected_Size_PM = 0.29222222227462435;

            Assert.assertTrue("unexpected Size_NM", equiv(expected_Size_NM, val_Size_NM));
            Assert.assertTrue("unexpected Size_PM, "+expected_Size_PM+" !~ "+val_Size_PM, equiv(expected_Size_PM, val_Size_PM));
            Assert.assertTrue("unexpected Size_Nucleus, "+expected_Size_Nucleus+" !~ "+val_Size_Nucleus, equiv(expected_Size_Nucleus, val_Size_Nucleus));
            Assert.assertTrue("unexpected Size_Cytosol, "+expected_Size_Cytosol+" !~ "+val_Size_Cytosol, equiv(expected_Size_Cytosol, val_Size_Cytosol));
            Assert.assertTrue("unexpected Size_Extracellular, "+expected_Size_Extracellular+" !~ "+val_Size_Extracellular, equiv(expected_Size_Extracellular, val_Size_Extracellular));
        }
    }

}
