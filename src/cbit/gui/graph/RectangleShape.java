package cbit.gui.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.*;

import cbit.gui.graph.visualstate.ImmutableVisualState;
import cbit.gui.graph.visualstate.VisualState;

public abstract class RectangleShape extends Shape {

	public RectangleShape (GraphModel graphModel) {
		super(graphModel);
	}

	@Override
	public VisualState createVisualState() { 
		return new ImmutableVisualState(this, VisualState.PaintLayer.COMPARTMENT);
	}

	protected void drawLabel(Graphics2D g2D,int absPosX,int absPosY) {

		if (getLabel() != null && getLabel().length()>0){
			int textX = absPosX  + Math.max(0,shapeSize.width/2 - labelSize.width/2);
			int textY = absPosY + Math.max(0,5 + labelSize.height);
			if(isSelected()){
				drawRaisedOutline(textX-5,textY-labelSize.height+3,labelSize.width+10,labelSize.height,g2D,Color.white,Color.black,Color.gray);
			}
			Color origColor = g2D.getColor();
			g2D.setColor(Color.black);
			Font origFont = g2D.getFont();
			g2D.setFont(getLabelFont(g2D));
			g2D.drawString(getLabel(),textX,textY);
			g2D.setColor(origColor);
			g2D.setFont(origFont);
		}


	}


	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2005 2:35:33 PM)
	 * @return java.awt.Font
	 * @param g java.awt.Graphics
	 */
	public Font getLabelFont(Graphics g) {
		return getBoldFont(g);
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public Dimension getPreferedSize(java.awt.Graphics2D g) {
		if (g == null){
			labelSize.height = 20;
			labelSize.width = 10;
		}else{
			try {
				java.awt.FontMetrics fm = g.getFontMetrics();
				labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
				if (getLabel()!=null){
					labelSize.width = fm.stringWidth(getLabel());
				}else{
					labelSize.width = 1;
				}
			}catch (NullPointerException e){
				labelSize.height = 20;
				labelSize.width = 10;
			}
		}
		preferredSize.height = labelSize.height + 10;
		preferredSize.width = labelSize.width + 10;
		return preferredSize;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return boolean
	 * @param p java.awt.Point
	 */
	@Override
	protected final boolean isInside (Point p ) {
		//
		// bring into local coordinates
		//
		int x = p.x - relativePos.x;
		int y = p.y - relativePos.y;

		//
		// check to see if inside rectangle
		//
		if (x>0 && x<shapeSize.width && 
				y>0 && y<shapeSize.height){
			return true;
		}else{
			return false;
		}
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return boolean
	 * @param p java.awt.Point
	 */
	@Override
	public final boolean isOnBorder(Point p) {

		int tolerance = 3;
		//
		// bring into local coordinates
		//
		int x = p.x - relativePos.x;
		int y = p.y - relativePos.y;

		//
		// check to see if inside rectangle
		//
		if (x>-tolerance && x<shapeSize.width+tolerance){
			if (Math.abs(y)                   < tolerance || 
					Math.abs(y-shapeSize.height) < tolerance){
				return true;
			}
		}
		if (y>-tolerance && y<shapeSize.height+tolerance){
			if (Math.abs(x)                  < tolerance || 
					Math.abs(x-shapeSize.width) < tolerance){
				return true;
			}
		}
		return false;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public void layout() throws LayoutException {

		if (shapeSize.width<=labelSize.width ||
				shapeSize.height<=labelSize.height){
			//throw new LayoutException("screen size smaller than label");
			//System.out.println("RectangleShape.layout: screen size smaller than label");
		} 
		//
		// this is like a row/column layout  (1 row)
		//
		int centerX = shapeSize.width/2;
		int centerY = shapeSize.height/2;

		//
		// position label
		//
		labelPos.x = centerX - labelSize.width/2;
		labelPos.y = centerY - labelSize.height/2;
	}

	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY ) {

		//
		// draw rectangle
		//
		g2D.setColor(backgroundColor);
		if (!bNoFill){
			g2D.fillRect(absPosX,absPosY,shapeSize.width,shapeSize.height);
		}
		g2D.setColor(forgroundColor);
		g2D.drawRect(absPosX,absPosY,shapeSize.width,shapeSize.height);

		drawLabel(g2D,absPosX,absPosY);
	}

}