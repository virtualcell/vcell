/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

/**
 * Insert the type's description here.
 * Creation date: (8/6/2001 11:20:02 PM)
 * @author: Jim Schaff
 */
public class GeometryFilamentCurvePanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GeometryFilamentCurvePanel geometryFilamentDialog = new GeometryFilamentCurvePanel();
		//
		cbit.vcell.geometry.Geometry geom = cbit.vcell.geometry.GeometryTest.getExample(2);
		//
		cbit.vcell.geometry.Line line = new cbit.vcell.geometry.Line(new org.vcell.util.Coordinate(0,0,0),new org.vcell.util.Coordinate(1,1,1));
		line.setClosed(true);
		geom.getGeometrySpec().getFilamentGroup().addCurve("filament1",line);
		line = new cbit.vcell.geometry.Line(new org.vcell.util.Coordinate(0,0,0),new org.vcell.util.Coordinate(1,1,1));
		geom.getGeometrySpec().getFilamentGroup().addCurve("filament2",line);
		//
		geometryFilamentDialog.setGeometry(geom);
		//
		geometryFilamentDialog.setCurve(line);
		//
		frame.setContentPane(geometryFilamentDialog);
		frame.setSize(geometryFilamentDialog.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
