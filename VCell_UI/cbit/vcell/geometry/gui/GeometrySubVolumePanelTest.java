package cbit.vcell.geometry.gui;

import org.vcell.expression.ExpressionFactory;

import cbit.vcell.parser.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
import cbit.vcell.server.*;
/**
 * This type was created in VisualAge.
 */
public class GeometrySubVolumePanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GeometrySubVolumePanel aGeometrySubVolumePanel;
		aGeometrySubVolumePanel = new GeometrySubVolumePanel();
		frame.setContentPane(aGeometrySubVolumePanel);
		frame.setSize(aGeometrySubVolumePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		
		Geometry geom = new Geometry("analytic1",2);// GeometryTest.getImageExample2D();
		geom.getGeometrySpec().addSubVolume(new AnalyticSubVolume("sub1",ExpressionFactory.createExpression(1.0)));
		aGeometrySubVolumePanel.setGeometry(geom);
		
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}
