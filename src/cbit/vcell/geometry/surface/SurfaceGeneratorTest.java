package cbit.vcell.geometry.surface;
import cbit.util.ISize;
import cbit.vcell.server.*;
import cbit.vcell.geometry.*;
import cbit.image.*;
/**
 * Insert the type's description here.
 * Creation date: (3/11/2002 12:45:46 PM)
 * @author: John Wagner
 */
public class SurfaceGeneratorTest {
/**
 * SurfaceGeneratorTest constructor comment.
 */
public SurfaceGeneratorTest() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (3/12/2002 11:05:56 AM)
 * @return cbit.image.VCImage
 */
public static VCImage createImage(int sX, int sY, int sZ) {
	try {
		long t1 = System.currentTimeMillis();
		ImageFile inImageFile = new ImageFile("C:\\temp\\mito_3D_no_hole.tif");
		VCImage inImage = inImageFile.getVCImage();
		long t2 = System.currentTimeMillis();
		System.out.println("reading Image File, time = "+(t2-t1)/1000.0+" sec");
		if (sX==1 && sY==1 && sZ==1){
			return inImage;
		}
		System.out.println("resampling image "+inImageFile.getFilename());
		//
		// Create an image...
		int nX = inImage.getNumX()/sX, nY = inImage.getNumY()/sY, nZ = inImage.getNumZ()/sZ;
		cbit.util.Extent extent = new cbit.util.Extent(1.0, 1.0, 1.0);
		byte[] pixels = new byte[nX*nY*nZ];
		//
		int l = 0;
		for (int k = 0; k < nZ; k++) {
			for (int j = 0; j < nY; j++) {
				for (int i = 0; i < nX; i++) {
					int p = 0xFF & ((int) inImage.getPixel(sX*i, sY*j, sZ*k));
					pixels[l++] = (byte) (p != 0 ? 1 : 0);
				}
			}
		}
		VCImage vcImage = new VCImageUncompressed(null, pixels, extent, nX, nY, nZ);
		return(vcImage);
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
	return (null);
}


/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 *  Test Args: V:\Josh\525am.tif V:\Josh\membraneImage.tif 
 */
public static void main(String args[]) {
	try {
		new cbit.vcell.server.PropertyLoader();
		//
		VCImage vcImage = createImage(1, 1, 1);
		cbit.vcell.geometry.Geometry geometry = new cbit.vcell.geometry.Geometry ("GriddingTestGeometry",vcImage);
		geometry.getGeometrySpec().setOrigin(new cbit.util.Origin(0, 0, 0));
		SurfaceGenerator surfaceGenerator = new SurfaceGenerator(new cbit.vcell.server.StdoutSessionLog ("TriangleTest"));
		System.out.println("generating surface");
		java.util.Date timeOne = new java.util.Date();
		SurfaceCollection surfaceCollection = surfaceGenerator.generateSurface(geometry);
		java.util.Date timeTwo = new java.util.Date();
		long timeInMilliseconds = timeTwo.getTime() - timeOne.getTime();
		System.out.println("Surface Generation time: " + timeInMilliseconds/1000.0 + " s.");
		
		TaubinSmoothing taubinSmoothing = new TaubinSmoothing();
		FilterSpecification filterSpec = new FilterSpecification(0.5, 1.5, 0.2, 0.2);
		TaubinSmoothingSpecification taubinSpec = TaubinSmoothingSpecification.fromFilterSpecification(filterSpec);
		taubinSmoothing.smooth(surfaceCollection, taubinSpec);
		//
		System.out.flush();
		//try {
			//java.io.PrintStream stream = new java.io.PrintStream(new java.io.FileOutputStream("C:\\temp\\SurfaceTecPlot.dat"));
			//stream.println("TITLE=\"SurfaceGeneratorTest\"");
			//stream.println("VARIABLES = \"X\", \"Y\", \"Z\"");
			////
			//for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
				//Surface surface = surfaceCollection.getSurfaces(i);
				//stream.println("ZONE T=\"NAME" + i + "\", N=" + surfaceCollection.getNodeCount() + ", E=" + surface.getPolygonCount() + ", F=FEPOINT, ET=QUADRILATERAL");
				//for (int j = 0; j < surfaceCollection.getNodeCount(); j++) {
					//Node node = surfaceCollection.getNodes(j);
					//stream.println(node.getX() + " " + node.getY() + " " + node.getZ());
				//}
				//for (int j = 0; j < surface.getPolygonCount(); j++) {
					//Polygon polygon = surface.getPolygons(j);
					//int n0 = 1 + polygon.getNodes(0 % polygon.getNodeCount()).getGlobalIndex();
					//int n1 = 1 + polygon.getNodes(1 % polygon.getNodeCount()).getGlobalIndex();
					//int n2 = 1 + polygon.getNodes(2 % polygon.getNodeCount()).getGlobalIndex();
					//int n3 = 1 + polygon.getNodes(3 % polygon.getNodeCount()).getGlobalIndex();
					//stream.println(n0 + " " + n1 + " " + n2 + " " + n3);
				//}
			//}
		//} catch (Throwable throwable) {
			//throwable.printStackTrace();
		//}
		
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	} finally {
		System.exit(0);
	}
}
}