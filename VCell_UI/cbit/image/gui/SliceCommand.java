package cbit.image.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
