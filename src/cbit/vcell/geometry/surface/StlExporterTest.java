/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.surface;
/**
 * Insert the type's description here.
 * Creation date: (7/19/2004 10:52:53 AM)
 * @author: Jim Schaff
 */
public class StlExporterTest {
/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 10:54:55 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		cbit.vcell.geometry.Geometry geometry = cbit.vcell.geometry.GeometryTest.getImageExample3D();
		GeometrySurfaceDescription geometrySurfaceDescription = geometry.getGeometrySurfaceDescription();
		java.io.StringWriter writer = new java.io.StringWriter(2000);
		geometrySurfaceDescription.updateAll();
		StlExporter.writeStl(geometrySurfaceDescription,writer);
		System.out.println(writer.getBuffer().toString());
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
}
