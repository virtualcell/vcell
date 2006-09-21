package cbit.render.objects;
import cbit.util.Extent;
import cbit.util.ISize;
import cbit.util.Origin;
/**
 * Insert the type's description here.
 * Creation date: (3/11/2002 12:45:46 PM)
 * @author: John Wagner
 */
public class SurfaceGeneratorTest {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 *  Test Args: V:\Josh\525am.tif V:\Josh\membraneImage.tif 
 */
public static void main(String args[]) {
	try {
		//
		ByteImage byteImage = ByteImageTest.getSphereInBox(new ISize(30,30,30),new Extent(1,1,1), 0.4);
		SurfaceGenerator surfaceGenerator = new SurfaceGenerator();
		System.out.println("generating surface");
		java.util.Date timeOne = new java.util.Date();
		RegionImage regionImage = new RegionImage(byteImage);
		SurfaceCollection surfaceCollection = surfaceGenerator.generateSurface(regionImage,3,byteImage.getExtent(),new Origin(0,0,0));
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