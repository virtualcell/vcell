package cbit.image;

import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelTest;
import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathSymbolTableFactory;
import cbit.vcell.model.ModelTest;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.test.Fast;
import org.vcell.util.Extent;

import javax.xml.stream.XMLStreamException;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.StringBufferInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Category(Fast.class)
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
        Assert.assertArrayEquals("uncompressed and original image not equal", origUncompressedBytes, vcImageUncompressedFromBytes.getPixels());
        Assert.assertArrayEquals("round trip compression not equal", vcImageUncompressedFromBytes.getPixels(), vcImageCompressedFromImage.getPixels());
        Assert.assertArrayEquals("round trip uncompressed not equal", vcImageUncompressedFromBytes.getPixelsCompressed(), vcImageCompressedFromImage.getPixelsCompressed());

        VCImageCompressed vcImageCompressedFromBytes = new VCImageCompressed(null, vcImageUncompressedFromBytes.getPixelsCompressed(), extent, nx, ny, nz);
        VCImageUncompressed vcImageUncompressedFromImage = new VCImageUncompressed(vcImageCompressedFromBytes);
        Assert.assertArrayEquals("round trip compression not equal", vcImageUncompressedFromImage.getPixels(), vcImageCompressedFromBytes.getPixels());
        Assert.assertArrayEquals("round trip uncompressed not equal", vcImageUncompressedFromImage.getPixelsCompressed(), vcImageCompressedFromBytes.getPixelsCompressed());
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
        Assert.assertArrayEquals("uncompressed bytes not same - from compressed image",vcImage.getPixels(), vcImageCompressedFromImage.getPixels());
        Assert.assertArrayEquals("compressed bytes not same - from compressed image",vcImage.getPixelsCompressed(), vcImageCompressedFromImage.getPixelsCompressed());

        String xmlString2 = XmlHelper.imageToXML(vcImageUncompressedFromBytes);
        VCImage vcImage2 = XmlHelper.XMLToImage(xmlString2);
        Assert.assertArrayEquals("uncompressed bytes not same - from uncompressed image",vcImage2.getPixels(), vcImageUncompressedFromBytes.getPixels());
        Assert.assertArrayEquals("compressed bytes not same - from uncompressed image",vcImage2.getPixelsCompressed(), vcImageUncompressedFromBytes.getPixelsCompressed());

        Assert.assertEquals("xml strings different", xmlString, xmlString2);
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
        Assert.assertTrue("math descriptions didn't match: "+mathEquivalent.toDatabaseStatus(),mathEquivalent.isEquivalent());

        VCImage origImage = bioModel.getSimulationContext(0).getGeometry().getGeometrySpec().getImage();
        VCImage roundTripImage = roundTripBioModel.getSimulationContext(0).getGeometry().getGeometrySpec().getImage();
        int dimension = bioModel.getSimulationContext(0).getGeometry().getDimension();
        boolean bImageEquivalent = origImage.compareEqual(roundTripImage, dimension,true);
        Assert.assertTrue("images don't match",bImageEquivalent);
    }
}
