package cbit.vcell.geometry.surface;

/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 1:02:07 AM)
 * @author: Jim Schaff
 */
public class TaubinSmoothingTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		testConvergence();
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 10:54:07 PM)
 */
public static void testConvergence() {
	try {
		cbit.vcell.geometry.Geometry geometry = new cbit.vcell.geometry.Geometry("test1",2);
		double r = 0.8;
		geometry.getGeometrySpec().addSubVolume(new cbit.vcell.geometry.AnalyticSubVolume(null,"subVolume1",new cbit.vcell.parser.Expression("x^2+y^2<"+(r*r)),2));
		geometry.getGeometrySpec().addSubVolume(new cbit.vcell.geometry.AnalyticSubVolume(null,"subVolume0",new cbit.vcell.parser.Expression(1.0),1));
		geometry.getGeometrySpec().setExtent(new org.vcell.util.Extent(2,2,2));
		geometry.getGeometrySpec().setOrigin(new org.vcell.util.Origin(-1,-1,-1));
		GeometrySurfaceDescription geometrySurfaceDescription = geometry.getGeometrySurfaceDescription();
		org.vcell.util.ISize sampleSize = new org.vcell.util.ISize(10,10,3);  // geometry.getGeometrySpec().getDefaultSampledImageSize();
		for (int i = 0; i < 8; i++){
			//
			// set the sample size
			//
			geometrySurfaceDescription.setVolumeSampleSize(sampleSize);

			//
			// set the smoothing parameter
			//
			double charSize = 1.0;
			double resolution = geometry.getExtent().getX()/sampleSize.getX();
			double normalizedFrequency = resolution/charSize;
			geometrySurfaceDescription.setFilterCutoffFrequency(new Double(normalizedFrequency));

			//
			// perform all required operations
			//
			geometrySurfaceDescription.updateAll();
			SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
			//
			// compute area of surfaces
			//
			double area = 0.0;
			for (int j = 0; j < surfaceCollection.getSurfaceCount(); j++){
				cbit.vcell.geometry.surface.Surface surface = surfaceCollection.getSurfaces(j);
				area += surface.getArea();
			}
			double exactArea = 2*Math.PI*r*geometry.getExtent().getZ();
			System.out.println("area = "+area+", exact = "+exactArea+", error = "+(area - exactArea));
			sampleSize = new org.vcell.util.ISize(sampleSize.getX()*2,sampleSize.getY()*2,sampleSize.getZ());
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
