package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
