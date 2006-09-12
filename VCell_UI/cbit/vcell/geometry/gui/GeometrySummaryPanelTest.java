package cbit.vcell.geometry.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
/**
 * Insert the type's description here.
 * Creation date: (4/9/01 9:31:03 AM)
 * @author: Jim Schaff
 */
public class GeometrySummaryPanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GeometrySummaryPanel aGeometrySummaryPanel;
		aGeometrySummaryPanel = new GeometrySummaryPanel();
		frame.setContentPane(aGeometrySummaryPanel);
		frame.setSize(aGeometrySummaryPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);

		//Geometry geometry = cbit.vcell.geometry.GeometryTest.getImageExample2D();
		Geometry geometry = cbit.vcell.geometry.GeometryTest.getExample_er_cytsol2D();
		aGeometrySummaryPanel.setGeometry(geometry);
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
