package cbit.vcell.graph;

import java.awt.Graphics;

import org.vcell.model.rbm.MolecularType;

public interface LargeShape {

	public abstract void setX(int xPos);

	public abstract int getX();

	public abstract void setY(int yPos);

	public abstract int getY();

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract void paintSelf(Graphics g);
	
	public abstract boolean contains(PointLocationInShapeContext locationContext);

}