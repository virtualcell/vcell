/*
 * Copyright (C) 1999-2026 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package cbit.vcell.render;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A drag has to turn the scene the way the mouse went, and which way that is depends on which face
 * of the virtual ball the drag grabs -- which depends on the caller's depth convention.
 * <p>
 * Worth pinning because getting it wrong is invisible in a still image and obvious the moment
 * anyone drags: the Langevin trajectory viewer shipped inverted on both axes because it draws with
 * +z toward the viewer while the trackball assumed the opposite.
 */
@Tag("Fast")
public class TrackballHandednessTest {

	/** Where a world point lands after the current rotation. */
	private static Vect3d seenBy(Trackball tb, Vect3d world) {
		Affine rot = new Affine();
		tb.getMatrixGL(rot);
		return rot.mult(world);
	}

	/** A trackball at the oblique view the trajectory viewer opens at. */
	private static Trackball atDefaultView(Trackball.Handedness handedness) {
		Trackball tb = new Trackball(new Camera(), handedness);
		tb.getCamera().resetView();
		tb.setRotation(Math.toRadians(35 - 90), Math.toRadians(30), 0);
		return tb;
	}

	/** Normalized coords for a canvas that puts world +y up the screen. */
	private static void dragRight(Trackball tb) {
		tb.rotate_xy(0.0, 0.0, 0.2, 0.0);
	}

	private static void dragDown(Trackball tb) {
		tb.rotate_xy(0.0, 0.0, 0.0, -0.2);
	}

	/**
	 * In a right-handed viewer -- +z toward the camera, as the trajectory viewer draws -- the nearest
	 * point should follow the mouse: drag right and it goes right, drag down and it goes down.
	 */
	@Test
	public void rightHandedFollowsTheMouse() {
		Vect3d near = new Vect3d(0, 0, 1);

		Trackball right = atDefaultView(Trackball.Handedness.RIGHT_HANDED);
		double x0 = seenBy(right, near).getX();
		dragRight(right);
		assertTrue(seenBy(right, near).getX() > x0,
			"dragging right should carry the near point right");

		Trackball down = atDefaultView(Trackball.Handedness.RIGHT_HANDED);
		double y0 = seenBy(down, near).getY();
		dragDown(down);
		assertTrue(seenBy(down, near).getY() < y0,
			"dragging down should carry the near point down (world +y is up the screen)");
	}

	/**
	 * The left-handed convention grabs the other face, so the same drags carry that same point the
	 * other way. That is correct for the geometry and PDE surface viewers, whose +z points away from
	 * the viewer; it is only wrong for a caller that draws the other way round.
	 */
	@Test
	public void leftHandedGrabsTheOtherFace() {
		Vect3d p = new Vect3d(0, 0, 1);

		Trackball right = atDefaultView(Trackball.Handedness.LEFT_HANDED);
		double x0 = seenBy(right, p).getX();
		dragRight(right);
		assertTrue(seenBy(right, p).getX() < x0, "left-handed drag-right carries +z the other way");

		Trackball down = atDefaultView(Trackball.Handedness.LEFT_HANDED);
		double y0 = seenBy(down, p).getY();
		dragDown(down);
		assertTrue(seenBy(down, p).getY() > y0, "left-handed drag-down carries +z the other way");
	}
}
