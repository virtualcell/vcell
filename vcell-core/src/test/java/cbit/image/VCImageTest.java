package cbit.image;

import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.junit.Assert;
import org.junit.Test;
import org.vcell.util.Extent;

import java.util.Random;

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

//    @Test
//    public void testSBMLSampledVolume() throws ImageException, PropertyVetoException, MappingException, XMLStreamException, SbmlException, VCLoggerException {
//        Random rand = new Random(0);
//        int nx = 5;
//        int ny = 5;
//        int nz = 1;
//        byte[] origUncompressedBytes = new byte[] {
//                0,0,0,0,0,
//                0,0,0,0,0,
//                0,0,0,0,0,
//                1,1,1,1,1,
//                1,1,1,1,1};
//        rand.nextBytes(origUncompressedBytes);
//        Extent extent = new Extent(5, 5, 1);
//        VCImageUncompressed vcImageUncompressedFromBytes = new VCImageUncompressed(null, origUncompressedBytes, extent, nx, ny, nz);
//        Geometry geo = new Geometry("my_geo", vcImageUncompressedFromBytes);
//        BioModel bioModel = new BioModel(null);
//        bioModel.getModel();
//        SimulationContext simContext = bioModel.addNewSimulationContext("new_app", SimulationContext.Application.NETWORK_DETERMINISTIC);
//        simContext.setGeometry(geo);
//        bioModel.updateAll(false);
//        SBMLExporter sbmlExporter = new SBMLExporter(simContext, 3, 1, true);
//        String sbmlString = sbmlExporter.getSBMLString();
//        SBMLImporter sbmlImporter = new SBMLImporter(new StringBufferInputStream(sbmlString), new SBMLExporter.MemoryVCLogger(), true);
//        BioModel roundTripBioModel = sbmlImporter.getBioModel();
//        VCImage roundTripImage = roundTripBioModel.getSimulationContext(0).getGeometry().getGeometrySpec().getImage();
//        Assert.assertEquals("images don't match",vcImageUncompressedFromBytes, roundTripImage);
//    }
}
