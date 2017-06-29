package cbit.vcell.graph;

import java.awt.Graphics;

import org.vcell.util.Displayable;

/*
 * Shapes which can be highlighted
 */
public interface HighlightableShapeInterface extends Displayable {

	public abstract boolean isHighlighted();
	public abstract void setHighlight(boolean highlight, boolean param);
	public abstract void turnHighlightOffRecursive(Graphics g);
}