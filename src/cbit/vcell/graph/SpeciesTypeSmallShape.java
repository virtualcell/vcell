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
import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.Displayable;

public class SpeciesTypeSmallShape {
	
	private static final int baseWidth = 11;
	private static final int baseHeight = 10;
	private static final int cornerArc = 10;

	private int xPos = 0;
	private int yPos = 0;
	private int width = baseWidth;
	private int height = baseHeight;
	
	final Graphics graphicsContext;
	
	private final MolecularType mt;
	private final Displayable owner;
	
	List <MolecularComponentSmallShape> componentShapes = new ArrayList<MolecularComponentSmallShape>();

	public SpeciesTypeSmallShape(int xPos, int yPos, MolecularType mt, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.mt = mt;
		this.xPos = xPos;
		this.yPos = yPos;
		this.graphicsContext = graphicsContext;
		int numComponents = mt.getComponentList().size();
		int offsetFromRight = 0;		// total width of all components, based on the length of their names
		for(int i=numComponents-1; i >=0; i--) {
			MolecularComponent mc = getSpeciesType().getComponentList().get(i);
			MolecularComponentSmallShape mlcls = new MolecularComponentSmallShape(100, 50, mc, graphicsContext, owner);
			offsetFromRight += mlcls.getWidth() + MolecularComponentSmallShape.componentSeparation;
		}
		width = baseWidth + offsetFromRight;	// adjusted for # of components
		height = baseHeight + MolecularComponentSmallShape.componentDiameter / 2;
	
		int fixedPart = xPos + width;
		offsetFromRight = 4;
		for(int i=numComponents-1; i >=0; i--) {
			int rightPos = fixedPart - offsetFromRight;		// we compute distance from right end
			int y = yPos + height - MolecularComponentSmallShape.componentDiameter;
			// now that we know the dimensions of the species type shape we create the component shapes
			MolecularComponent mc = mt.getComponentList().get(i);
			MolecularComponentSmallShape mcss = new MolecularComponentSmallShape(rightPos, y-2, mc, graphicsContext, owner);
			offsetFromRight += mcss.getWidth() + MolecularComponentSmallShape.componentSeparation;
			componentShapes.add(0, mcss);
		}
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
	// paintComponent is being overridden in the renderer
	//
	private void paintSpecies(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GradientPaint p = new GradientPaint(xPos, yPos, Color.blue.darker().darker(), xPos, yPos + baseHeight/2, Color.WHITE, true);
		g2.setPaint(p);

		RoundRectangle2D rect = new RoundRectangle2D.Float(xPos, yPos, width, baseHeight, cornerArc, cornerArc);
		g2.fill(rect);

		RoundRectangle2D inner = new RoundRectangle2D.Float(xPos+1, yPos+1, width-2, baseHeight-2, cornerArc-3, cornerArc-3);
//		g2.setPaint(Color.GRAY);
//		g2.draw(inner);
		g2.setPaint(Color.black);
		g2.draw(rect);
		
		for(MolecularComponentSmallShape mcss : componentShapes) {
			mcss.paintSelf(g);
		}
	}
}
