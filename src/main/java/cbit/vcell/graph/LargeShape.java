package cbit.vcell.graph;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.vcell.model.rbm.MolecularType;

public interface LargeShape {

	public abstract void setX(int xPos);

	public abstract int getX();

	public abstract void setY(int yPos);

	public abstract int getY();

	public abstract int getWidth();

	public abstract int getHeight();
	
	public abstract Rectangle getLabelOutline();
	public abstract Font getLabelFont();
	public abstract String getFullName();

	public abstract void paintSelf(Graphics g);
	
	public abstract boolean contains(PointLocationInShapeContext locationContext);

}