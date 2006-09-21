package cbit.vcell.geometry.gui;

import cbit.render.*;
import cbit.render.objects.FilterSpecification;
import cbit.render.objects.SurfaceCollection;
import cbit.render.objects.TaubinSmoothingSpecification;
import cbit.util.StdoutSessionLog;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.surface.SurfaceGenerator;
/**
 * Insert the type's description here.
 * Creation date: (11/26/2003 1:13:27 PM)
 * @author: Jim Schaff
 */
public class SurfaceCanvasTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SurfaceCanvas aSurfaceCanvas;
		aSurfaceCanvas = new SurfaceCanvas();
		frame.setContentPane(aSurfaceCanvas);
		frame.setSize(aSurfaceCanvas.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);

		SurfaceGenerator surfaceGenerator = new SurfaceGenerator(new StdoutSessionLog("surfGen"));
		Geometry geometry = cbit.vcell.geometry.GeometryTest.getExample_er_cytsol3D();
		//for (int i = 0; i < 100; i++){
			//double d = i/100.0;
			SurfaceCollection surfaceCollection = surfaceGenerator.generateSurface(geometry);
			System.out.println("smoothing");
			cbit.render.objects.TaubinSmoothing taubin = new cbit.render.objects.TaubinSmoothing();
			FilterSpecification filterSpec = new FilterSpecification(0.3,0.7,0.2,0.2);
			TaubinSmoothingSpecification taubinSpec = TaubinSmoothingSpecification.fromFilterSpecification(filterSpec);
			taubin.smooth(surfaceCollection, taubinSpec);
			System.out.println("painting");
			aSurfaceCanvas.setSurfaceCollection(surfaceCollection);
		//}
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
