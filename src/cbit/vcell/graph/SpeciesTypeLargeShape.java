package cbit.vcell.graph;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import org.vcell.model.rbm.MolecularType;

public class SpeciesTypeLargeShape {
	
	private static final int componentSeparation = 3;		// distance between components
	private static final int componentDiameter = 14;		// diameter of the component
	private static final int baseWidth = 35;
	private static final int baseHeight = 28;
	private static final int cornerArc = 25;

	private int xPos = 0;
	private int yPos = 0;
		private int width = baseWidth;
	private int height = baseHeight;

	MolecularType mt = null;

	public SpeciesTypeLargeShape(int xPos, int yPos, MolecularType mt) {
		this.mt = mt;
		this.xPos = xPos;
		this.yPos = yPos;
		width = baseWidth + mt.getComponentList().size() * (componentDiameter + componentSeparation);	// adjusted for # of components
		width += 5 * mt.getName().length();
		height = baseHeight + componentDiameter / 2;
	}
	
	public void setX(int xPos){ 
		this.xPos = xPos;
	}
	public int getX(){
		return xPos;
	}
	public void setY(int yPos){
		this.yPos = yPos;
	}
	public int getY(){
		return yPos;
	}
	public int getWidth(){
		return width;
	} 
	public int getHeight(){
		return height;
	}
	public void paintSelf(Graphics g) {
		paintSpecies(g);
	}
	// --------------------------------------------------------------------------------------
	private void paintSpecies(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GradientPaint p = new GradientPaint(xPos, yPos, Color.GRAY, xPos, yPos + baseHeight/2, Color.WHITE, true);
		g2.setPaint(p);

		RoundRectangle2D rect = new RoundRectangle2D.Float(xPos, yPos, width, baseHeight, cornerArc, cornerArc);
		g2.fill(rect);

		RoundRectangle2D inner = new RoundRectangle2D.Float(xPos+1, yPos+1, width-2, baseHeight-2, cornerArc-3, cornerArc-3);
		g2.setPaint(Color.GRAY);
		g2.draw(inner);
		g2.setPaint(Color.DARK_GRAY);
		g2.draw(rect);
		
		g2.drawString(mt.getName(), xPos+8, yPos+baseHeight-8);
		
		for(int i=0; i<mt.getComponentList().size(); i++) {
			paintComponent(g, mt.getComponentList().size()-i);
		}
	}
	private void paintComponent(Graphics g, int index) {
		int fromRight = index*(componentDiameter + componentSeparation);
		int x = xPos + width - fromRight - 5;		// we compute distance from right end
		int y = yPos + baseHeight - componentDiameter/2;
		
		g.setColor(Color.YELLOW);
		g.fillOval(x, y, componentDiameter, componentDiameter);			// g.fillRect(xPos, yPos, width, height);
		g.setColor(Color.BLACK);
		g.drawOval(x, y, componentDiameter, componentDiameter);
	}
}
