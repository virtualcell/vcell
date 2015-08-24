package cbit.vcell.graph;

import java.awt.Graphics;

import org.vcell.model.rbm.MolecularType;

/*
 * Shapes which can be highlighted
 */
public interface HighlightableShapeInterface {

	public abstract boolean isHighlighted();
	public abstract void setHighlight(boolean highlight);
	public abstract void turnHighlightOffRecursive(Graphics g);
}