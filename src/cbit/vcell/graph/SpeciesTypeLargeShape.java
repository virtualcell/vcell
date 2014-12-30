package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.util.Displayable;

import cbit.vcell.model.SpeciesContext;

public class SpeciesTypeLargeShape implements LargeShape {
	
	private static final int baseWidth = 25;
	private static final int baseHeight = 30;
	private static final int cornerArc = 30;

	private boolean pattern;					// we draw component or we draw component pattern (and perhaps a bond)
	private int xPos = 0;
	private int yPos = 0;
	private int width = baseWidth;
	private int height = baseHeight;

	final Graphics graphicsContext;
	
	private final String name;
	private final MolecularType mt;
	private final MolecularTypePattern mtp;
	private Displayable owner;

	List <MolecularComponentLargeShape> componentShapes = new ArrayList<MolecularComponentLargeShape>();
	
	public SpeciesTypeLargeShape(Displayable owner, int xPos, int yPos, Graphics graphicsContext) {
		this.owner = owner;
		this.pattern = false;
		this.mt = null;
		this.mtp = null;
		this.xPos = xPos;
		this.yPos = yPos;
		this.graphicsContext = graphicsContext;
		// adjustment for name width and for the width of the components
		// TODO: properly calculate the width based on the font and size of each letter
		int offsetFromRight = 0;		// total width of all components, based on the length of their names

		name = adjustForSize();
		width = baseWidth + offsetFromRight;	// adjusted for # of components
//		width += 6 * name.length();				// adjust for the length of the name of the species type
		width += getStringWidth(name);				// adjust for the length of the name of the species type
		height = baseHeight + MolecularComponentLargeShape.componentDiameter / 2;
	}
	public SpeciesTypeLargeShape(int xPos, int yPos, MolecularType mt, Graphics graphicsContext) {
		this.owner = null;
		this.pattern = false;
		this.mt = mt;
		this.mtp = null;
		this.xPos = xPos;
		this.yPos = yPos;
		this.graphicsContext = graphicsContext;
		// adjustment for name width and for the width of the components
		// TODO: properly calculate the width based on the font and size of each letter
		int numComponents = mt.getComponentList().size();
		int offsetFromRight = 0;		// total width of all components, based on the length of their names
		for(int i=numComponents-1; i >=0; i--) {
			MolecularComponent mc = getSpeciesType().getComponentList().get(i);
			// WARNING! we create temporary component shapes whose coordinates are invented, we use them only to compute 
			// the width of the species type shape; only after that is known we can finally compute the exact coordinates
			// of the components
			MolecularComponentLargeShape mlcls = new MolecularComponentLargeShape(100, 50, mc, graphicsContext);
			offsetFromRight += mlcls.getWidth() + MolecularComponentLargeShape.componentSeparation;
		}
		name = adjustForSize();
		width = baseWidth + offsetFromRight;	// adjusted for # of components
//		width += 6 * name.length();				// adjust for the length of the name of the species type
		width += getStringWidth(name);				// adjust for the length of the name of the species type
		height = baseHeight + MolecularComponentLargeShape.componentDiameter / 2;

		int fixedPart = xPos + width;
		offsetFromRight = 10;
		for(int i=numComponents-1; i >=0; i--) {
			int rightPos = fixedPart - offsetFromRight;		// we compute distance from right end
			int y = yPos + height - MolecularComponentLargeShape.componentDiameter;
			// now that we know the dimensions of the species type shape we create the component shapes
			MolecularComponent mc = mt.getComponentList().get(i);
			MolecularComponentLargeShape mlcls = new MolecularComponentLargeShape(rightPos, y, mc, graphicsContext);
			offsetFromRight += mlcls.getWidth() + MolecularComponentLargeShape.componentSeparation;
			componentShapes.add(0, mlcls);
		}
	}
	public SpeciesTypeLargeShape(int xPos, int yPos, MolecularTypePattern mtp, Graphics graphicsContext) {
		this.owner = null;
		this.pattern = true;
		this.mt = mtp.getMolecularType();
		this.mtp = mtp;
		this.xPos = xPos;
		this.yPos = yPos;
		this.graphicsContext = graphicsContext;
		
		int numComponents = mt.getComponentList().size();	// components
		int offsetFromRight = 0;		// total width of all components, based on the length of their names
		for(int i=numComponents-1; i >=0; i--) {
			MolecularComponentPattern mcp = mtp.getComponentPatternList().get(i);
			MolecularComponentLargeShape mlcls = new MolecularComponentLargeShape(100, 50, mcp, graphicsContext);
			offsetFromRight += mlcls.getWidth() + MolecularComponentLargeShape.componentSeparation;
		}
		name = adjustForSize();
		width = baseWidth + offsetFromRight;	// adjusted for # of components
		width += getStringWidth(name);				// adjust for the length of the name of the species type
		height = baseHeight + MolecularComponentLargeShape.componentDiameter / 2;
		
		int fixedPart = xPos + width;
		offsetFromRight = 10;
		for(int i=numComponents-1; i >=0; i--) {
			int rightPos = fixedPart - offsetFromRight;		// we compute distance from right end
			int y = yPos + height - MolecularComponentLargeShape.componentDiameter;

			MolecularComponentPattern mcp = mtp.getComponentPatternList().get(i);
			MolecularComponentLargeShape mlcls = new MolecularComponentLargeShape(rightPos, y, mcp, graphicsContext);
			offsetFromRight += mlcls.getWidth() + MolecularComponentLargeShape.componentSeparation;
			componentShapes.add(0, mlcls);
		}
	}
	
	public MolecularType getSpeciesType() {
		return mt;
	}

	private String adjustForSize() {
		// we truncate to 12 characters any name longer than 12 characters
		// we keep the first 7 letters, then 2 points, then the last 3 letters
		String s = null;
		if(mt == null) {
			if(owner instanceof SpeciesContext) {
				s = ((SpeciesContext)owner).getDisplayName();
			} else {
				s = "?";
			}
		} else {
			s = mt.getDisplayName();
		}
		int len = s.length();
		if(len > 12) {
			return(s.substring(0,7) + ".." + s.substring(len-3, len));
		} else {
			return(s);
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

	@Override
	public void setX(int xPos){ 
		this.xPos = xPos;
	}
	@Override
	public int getX(){
		return xPos;
	}
	@Override
	public void setY(int yPos){
		this.yPos = yPos;
	}
	@Override
	public int getY(){
		return yPos;
	}
	@Override
	public int getWidth(){
		return width;
	} 
	@Override
	public int getHeight(){
		return height;
	}
	public final boolean getPattern() {
		return pattern;
	}
	public final MolecularTypePattern getMolecularTypePattern() {
		return mtp;
	}
	public MolecularComponentLargeShape getComponentShape(int index) {
		return componentShapes.get(index);
	}
	public MolecularComponentLargeShape getShape(MolecularComponentPattern mcpTo) {
		for(MolecularComponentLargeShape mcls : componentShapes) {
			MolecularComponentPattern mcpThis = mcls.getMolecularComponentPattern();
			if(mcpThis == mcpTo) {
				return mcls;
			}
		}
		return null;
	}

	
	@Override
	public void paintSelf(Graphics g) {
		paintSpecies(g);
	}
	// --------------------------------------------------------------------------------------
	private void paintSpecies(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color primaryColor = null;
		if(mt == null && mtp == null) {	// plain species context
			 primaryColor = Color.green.darker().darker();
		} else {						// molecular type, species pattern
			primaryColor = Color.gray;
		}
		GradientPaint p = new GradientPaint(xPos, yPos, primaryColor, xPos, yPos + baseHeight/2, Color.WHITE, true);
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
		g2.drawString(name, xPos+11, yPos+baseHeight-9);
		g.setFont(fontOld);
		g.setColor(colorOld);
		
		for(MolecularComponentLargeShape mcls : componentShapes) {
			mcls.paintSelf(g);
		}
	}
}
