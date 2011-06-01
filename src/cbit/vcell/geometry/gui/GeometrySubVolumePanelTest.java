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

import cbit.vcell.parser.*;
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
		geom.getGeometrySpec().addSubVolume(new AnalyticSubVolume("sub1",new Expression(1.0)));
		aGeometrySubVolumePanel.setGeometry(geom);
		
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}
