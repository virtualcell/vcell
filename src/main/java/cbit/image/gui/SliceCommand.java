/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;

import cbit.image.Command;

/**
 * This type was created in VisualAge.
 */
public class SliceCommand extends Command {
	private int sliceOffset = -1;
/**
 * SliceCommand constructor comment.
 * @param id int
 */
public SliceCommand(int sliceOffset) {
	super();
	this.sliceOffset = sliceOffset;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getSliceOffset() {
	return sliceOffset;
}
/**
 * This method was created in VisualAge.
 * @param oldCommand cbit.image.Command
 */
public void integrateOldCommand(Command oldCommand) {
	sliceOffset += ((SliceCommand)oldCommand).getSliceOffset();
}
}
