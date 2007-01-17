package cbit.vcell.geometry.gui;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.geometry.surface.SurfaceGenerator;
import cbit.vcell.geometry.surface.SurfaceCollection;
/**
 * Insert the type's description here.
 * Creation date: (11/26/2003 1:13:27 PM)
 * @author: Jim Schaff
 */
public class SurfaceViewerTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SurfaceViewer aSurfaceViewer;
		aSurfaceViewer = new SurfaceViewer();
		frame.setContentPane(aSurfaceViewer);
		frame.setSize(aSurfaceViewer.getSize());
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

		aSurfaceViewer.setGeometry(geometry);

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
