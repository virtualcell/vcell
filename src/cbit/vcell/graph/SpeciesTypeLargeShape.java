package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;

public class SpeciesTypeLargeShape {
	
	private static final int baseWidth = 25;
	private static final int baseHeight = 30;
	private static final int cornerArc = 25;

	private int xPos = 0;
	private int yPos = 0;
	private int width = baseWidth;
	private int height = baseHeight;
	
	final Graphics graphicsContext;
	
	private final String name;
	private final MolecularType mt;

	public SpeciesTypeLargeShape(int xPos, int yPos, MolecularType mt, Graphics graphicsContext) {
		this.mt = mt;
		this.xPos = xPos;
		this.yPos = yPos;
		this.graphicsContext = graphicsContext;
		// adjustment for name width and for the width of the components
		// TODO: properly calculate the width based on the font and size of each letter
		int numComponents = mt.getComponentList().size();
		int offsetFromRight = 0;		// total width of all components, based on the length of their names
		for(int i=numComponents-1; i >=0; i--) {
			MolecularComponent mc = getSpeciesType().getComponentList().get(i);
			MolecularComponentLargeShape mlcls = new MolecularComponentLargeShape(100, 50, mc, graphicsContext);
			offsetFromRight += mlcls.getWidth() + MolecularComponentLargeShape.componentSeparation;
		}
		name = adjustForSize(mt.getName());
		width = baseWidth + offsetFromRight;	// adjusted for # of components
//		width += 6 * name.length();				// adjust for the length of the name of the species type
		width += getStringWidth(name);				// adjust for the length of the name of the species type
		height = baseHeight + MolecularComponentLargeShape.componentDiameter / 2;
	}
	
	private String adjustForSize(String name) {
		int len = mt.getName().length();
		if(len > 12) {
			return(mt.getName().substring(0,7) + ".." + mt.getName().substring(len-3, len));
		} else {
			return(mt.getName());
		}
	}
	
	private int getStringWidth(String s) {
		Font font = graphicsContext.getFont().deriveFont(Font.BOLD);
		FontMetrics fm = graphicsContext.getFontMetrics(font);
		int stringWidth = fm.stringWidth(s);
//		AffineTransform at = font.getTransform();
//		FontRenderContext frc = new FontRenderContext(at,true,true);
//		int textwidth = (int)(font.getStringBounds(s, frc).getWidth());
		return stringWidth;
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
	public MolecularType getSpeciesType() {
		return mt;
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
		
		Font fontOld = g.getFont();
		Color colorOld = g.getColor();
		Font font = fontOld.deriveFont(Font.BOLD);
		g.setFont(font);
		g.setColor(Color.black);
		g2.drawString(name, xPos+10, yPos+baseHeight-9);
		g.setFont(fontOld);
		g.setColor(colorOld);
		
		MolecularComponentLargeShape.paintComponents(g, this, graphicsContext);
	}
}
