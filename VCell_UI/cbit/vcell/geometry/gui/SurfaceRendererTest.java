package cbit.vcell.geometry.gui;
import org.vcell.expression.ExpressionFactory;
import org.vcell.render.*;
import org.vcell.spatial.SurfaceCollection;

/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 5:32:11 PM)
 * @author: Jim Schaff
 */
public class SurfaceRendererTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		cbit.vcell.geometry.Geometry geo = new cbit.vcell.geometry.Geometry("geo1",3);
		geo.getGeometrySpec().addSubVolume(new cbit.vcell.geometry.AnalyticSubVolume("sv1",ExpressionFactory.createExpression("x<z")));
		geo.getGeometrySpec().addSubVolume(new cbit.vcell.geometry.AnalyticSubVolume("sv2",ExpressionFactory.createExpression("1.0")));
		geo.getGeometrySpec().setExtent(new org.vcell.util.Extent(10.0,10.0,10.0));
		geo.getGeometrySpec().setOrigin(new org.vcell.util.Origin(0.0,0.0,0.0));
		org.vcell.util.Origin origin = geo.getOrigin();
		org.vcell.util.Extent extent = geo.getExtent();
		geo.getGeometrySurfaceDescription().setFilterCutoffFrequency(new Double(0.6));
		geo.getGeometrySurfaceDescription().updateAll();
		if (geo.getGeometrySpec().getValid()==false){
			throw new RuntimeException("geometry is not valid: "+geo.getGeometrySpec().getWarningMessage());
		}
		SurfaceCollection surfCollection = geo.getGeometrySurfaceDescription().getSurfaceCollection();
		SurfaceRenderer surfaceRenderer = new SurfaceRenderer(new Trackball(new Camera()),extent,origin);
		int numX = 5;
		int numY = 5;
		int numZ = 5;
		cbit.image.VCImage sampledImage = surfaceRenderer.sampleVolume(surfCollection,numX,numY,numZ);
		for (int i = 0; i < numX*numY*numZ; i++){
			System.out.print(sampledImage.getPixels()[i]+" ");
			if (i%numX==(numX-1)){
				System.out.println("");
			}
			if (i%(numX*numY)==(numX*numY-1)){
				System.out.println("");
			}
		}
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
}