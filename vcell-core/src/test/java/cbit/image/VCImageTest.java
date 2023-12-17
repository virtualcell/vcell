package cbit.image;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelTest;
import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.Extent;

import java.io.StringBufferInputStream;
import java.util.Random;

@Tag("Fast")
public class VCImageTest {

    @Test
    public void testCompression() throws ImageException {
        Random rand = new Random(0);
        int nx = 10;
        int ny = 5;
        int nz = 1;
        byte[] origUncompressedBytes = new byte[nx * ny * nz];
        rand.nextBytes(origUncompressedBytes);
        Extent extent = new Extent(5, 5, 1);
        VCImageUncompressed vcImageUncompressedFromBytes = new VCImageUncompressed(null, origUncompressedBytes, extent, nx, ny, nz);
        VCImageCompressed vcImageCompressedFromImage = new VCImageCompressed(vcImageUncompressedFromBytes);
        Assertions.assertArrayEquals(origUncompressedBytes, vcImageUncompressedFromBytes.getPixels(), "uncompressed and original image not equal");
        Assertions.assertArrayEquals(vcImageUncompressedFromBytes.getPixels(), vcImageCompressedFromImage.getPixels(), "round trip compression not equal");
        Assertions.assertArrayEquals(vcImageUncompressedFromBytes.getPixelsCompressed(), vcImageCompressedFromImage.getPixelsCompressed(), "round trip uncompressed not equal");

        VCImageCompressed vcImageCompressedFromBytes = new VCImageCompressed(null, vcImageUncompressedFromBytes.getPixelsCompressed(), extent, nx, ny, nz);
        VCImageUncompressed vcImageUncompressedFromImage = new VCImageUncompressed(vcImageCompressedFromBytes);
        Assertions.assertArrayEquals(vcImageUncompressedFromImage.getPixels(), vcImageCompressedFromBytes.getPixels(),"round trip compression not equal");
        Assertions.assertArrayEquals(vcImageUncompressedFromImage.getPixelsCompressed(), vcImageCompressedFromBytes.getPixelsCompressed(),"round trip uncompressed not equal");
    }

    @Test
    public void testImageXML() throws ImageException, XmlParseException {
        Random rand = new Random(0);
        int nx = 10;
        int ny = 5;
        int nz = 1;
        byte[] origUncompressedBytes = new byte[nx * ny * nz];
        rand.nextBytes(origUncompressedBytes);
        Extent extent = new Extent(5, 5, 1);
        VCImageUncompressed vcImageUncompressedFromBytes = new VCImageUncompressed(null, origUncompressedBytes, extent, nx, ny, nz);
        VCImageCompressed vcImageCompressedFromImage = new VCImageCompressed(vcImageUncompressedFromBytes);


        String xmlString = XmlHelper.imageToXML(vcImageCompressedFromImage);
        VCImage vcImage = XmlHelper.XMLToImage(xmlString);
        Assertions.assertArrayEquals(vcImage.getPixels(), vcImageCompressedFromImage.getPixels(),"uncompressed bytes not same - from compressed image");
        Assertions.assertArrayEquals(vcImage.getPixelsCompressed(), vcImageCompressedFromImage.getPixelsCompressed(),"compressed bytes not same - from compressed image");

        String xmlString2 = XmlHelper.imageToXML(vcImageUncompressedFromBytes);
        VCImage vcImage2 = XmlHelper.XMLToImage(xmlString2);
        Assertions.assertArrayEquals(vcImage2.getPixels(), vcImageUncompressedFromBytes.getPixels(),"uncompressed bytes not same - from uncompressed image");
        Assertions.assertArrayEquals(vcImage2.getPixelsCompressed(), vcImageUncompressedFromBytes.getPixelsCompressed(),"compressed bytes not same - from uncompressed image");

        Assertions.assertEquals(xmlString, xmlString2, "xml strings different");
    }

    @Test
    public void testSBMLSampledVolume_no_validation() throws Exception {
        BioModel bioModel = BioModelTest.getSimpleExampleWithImage(false);
        bioModel.updateAll(false);
//        String vcmlString_orig = XmlHelper.bioModelToXML(bioModel);
//        Files.write(vcmlString_orig, new File("__VCImageTest_vcml_orig.xml"), StandardCharsets.UTF_8);

        BioModel bioModelSBML = ModelUnitConverter.createBioModelWithSBMLUnitSystem(bioModel);
        bioModelSBML.updateAll(false);
//        String vcmlString_sbml = XmlHelper.bioModelToXML(bioModelSBML);
//        Files.write(vcmlString_sbml, new File("__VCImageTest_vcml_sbmlunits.xml"), StandardCharsets.UTF_8);

        SimulationContext simContext = bioModelSBML.getSimulationContext(0);
        boolean bValidateSBML = true;
        SBMLExporter sbmlExporter = new SBMLExporter(simContext, 3, 1, bValidateSBML);
        String sbmlString = sbmlExporter.getSBMLString();
//        Files.write(sbmlString, new File("__VCImageTest_sbml.xml"), StandardCharsets.UTF_8);

        SBMLImporter sbmlImporter = new SBMLImporter(new StringBufferInputStream(sbmlString), new SBMLExporter.MemoryVCLogger(), bValidateSBML);
        BioModel roundTripBioModel = sbmlImporter.getBioModel();
        roundTripBioModel.updateAll(false);

        MathCompareResults mathEquivalent = MathDescription.testEquivalency(
                SimulationSymbolTable.createMathSymbolTableFactory(),
                bioModelSBML.getSimulationContext(0).getMathDescription(),
                roundTripBioModel.getSimulationContext(0).getMathDescription());
        Assertions.assertTrue(mathEquivalent.isEquivalent(),"math descriptions didn't match: "+mathEquivalent.toDatabaseStatus());

        VCImage origImage = bioModel.getSimulationContext(0).getGeometry().getGeometrySpec().getImage();
        VCImage roundTripImage = roundTripBioModel.getSimulationContext(0).getGeometry().getGeometrySpec().getImage();
        int dimension = bioModel.getSimulationContext(0).getGeometry().getDimension();
        boolean bImageEquivalent = origImage.compareEqual(roundTripImage, dimension,true);
        Assertions.assertTrue(bImageEquivalent,"images don't match");
    }
}
