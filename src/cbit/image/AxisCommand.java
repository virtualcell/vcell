/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

/**
 * This type was created in VisualAge.
 */
public class AxisCommand extends Command {
	private int axis = -1;
/**
 * AxisCommand constructor comment.
 */
public AxisCommand(int axis) {
	super();
	this.axis = axis;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getAxis() {
	return axis;
}
/**
 * This method was created in VisualAge.
 * @param oldCommand cbit.image.Command
 */
public void integrateOldCommand(Command oldCommand) {
}
}
