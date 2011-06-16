/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.render;

/**
 * Insert the type's description here.
 * Creation date: (12/1/2003 9:30:18 AM)
 * @author: Jim Schaff
 */
public class TrackballTest {
/**
 * TrackballTest constructor comment.
 */
public TrackballTest() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (12/1/2003 9:30:29 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		Trackball trackball = new Trackball(new Camera());
		Vect3d unitX = new Vect3d(1, 2, 3);
		Vect3d projX = new Vect3d();
		Vect3d unprojX = new Vect3d();

		for (int i=0;i<5;i++){
			trackball.getCamera().projectPoint(unitX,projX);
			System.out.println("   unitX = "+unitX);
			System.out.println("   projX = "+projX);
			trackball.getCamera().unProjectPoint(projX,unprojX);
			System.out.println("   unprojX = "+unprojX);
			System.out.println("");
			trackball.rotate_xy(0.0, 0.0, 0.01, 0.0);
		}
		
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
}
