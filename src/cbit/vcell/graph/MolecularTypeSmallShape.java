package cbit.vcell.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.util.Displayable;

import cbit.vcell.graph.SpeciesPatternSmallShape.DisplayRequirements;
import cbit.vcell.mapping.gui.SmallShapeManager;
import cbit.vcell.model.Model.RbmModelContainer;

public class MolecularTypeSmallShape implements AbstractShape, Icon {
	
	private static final int baseWidth = 11;
	private static final int baseHeight = 9;
//	private static final int baseWidth = 15;
//	private static final int baseHeight = 10;
	private static final int cornerArc = 10;

	private int xPos = 0;
	private int yPos = 0;
	private int width = baseWidth;
	private int height = baseHeight;
	
	final Graphics graphicsContext;
	
	private final SmallShapeManager shapeManager;
	private final MolecularType mt;
	private final MolecularTypePattern mtp;
	private final Displayable owner;
	private final AbstractShape parentShape;
	
	List <MolecularComponentSmallShape> componentShapes = new ArrayList<MolecularComponentSmallShape>();

	public MolecularTypeSmallShape(int xPos, int yPos, SmallShapeManager shapeManager, Graphics graphicsContext, Displayable owner, AbstractShape parentShape) {
		this.owner = owner;
		this.parentShape = parentShape;
		this.mt = null;
		this.mtp = null;
		this.xPos = xPos;
		this.yPos = yPos;
		this.shapeManager = shapeManager;
		this.graphicsContext = graphicsContext;
		
		width = baseWidth;		// plain species, we want it look closest to a circle (so width smaller than baseWidth)
		height = baseHeight + MolecularComponentSmallShape.componentDiameter / 2;
		// no species pattern - this is a plain species context
	}
	public MolecularTypeSmallShape(int xPos, int yPos, MolecularTypePattern mtp, SmallShapeManager shapeManager, Graphics graphicsContext, Displayable owner, AbstractShape parentShape) {
		this.owner = owner;
		this.parentShape = parentShape;
		this.mt = mtp.getMolecularType();
		this.mtp = mtp;
		this.xPos = xPos;
		this.yPos = yPos;
		this.shapeManager = shapeManager;
		this.graphicsContext = graphicsContext;
		int numComponents = mt.getComponentList().size();
		int offsetFromRight = 0;		// total width of all components, based on the length of their names
		for(int i=numComponents-1; i >=0; i--) {
//			MolecularComponentPattern mcp = mtp.getComponentPatternList().get(i);
			MolecularComponent mc = mt.getComponentList().get(i);
			MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
			MolecularComponentSmallShape mlcls = new MolecularComponentSmallShape(100, 50, mcp, shapeManager, graphicsContext, owner, this);
			offsetFromRight += mlcls.getWidth() + MolecularComponentSmallShape.componentSeparation;
		}
		
		width = baseWidth + offsetFromRight;	// adjusted for # of components
		height = baseHeight + MolecularComponentSmallShape.componentDiameter / 2;
		int fixedPart = xPos + width;
		offsetFromRight = 4;
		for(int i=numComponents-1; i >=0; i--) {
			int rightPos = fixedPart - offsetFromRight;		// we compute distance from right end
			int y = yPos + height - MolecularComponentSmallShape.componentDiameter;
			// now that we know the dimensions of the molecular type shape we create the component shapes
//			MolecularComponentPattern mcp = mtp.getComponentPatternList().get(i);
			MolecularComponent mc = mt.getComponentList().get(i);
			MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
			MolecularComponentSmallShape mcss = new MolecularComponentSmallShape(rightPos, y-2, mcp, shapeManager, graphicsContext, owner, this);
			offsetFromRight += mcss.getWidth() + MolecularComponentSmallShape.componentSeparation;
			componentShapes.add(0, mcss);
		}
	}
	public MolecularTypeSmallShape(int xPos, int yPos, MolecularType mt, SmallShapeManager shapeManager, Graphics graphicsContext, Displayable owner, AbstractShape parentShape) {
		this.owner = owner;
		this.parentShape = parentShape;
		this.mt = mt;
		this.mtp = null;
		this.xPos = xPos;
		this.yPos = yPos;
		this.shapeManager = shapeManager;
		this.graphicsContext = graphicsContext;
		int numComponents = mt.getComponentList().size();
		int offsetFromRight = 0;		// total width of all components, based on the length of their names
		for(int i=numComponents-1; i >=0; i--) {
			MolecularComponent mc = getMolecularType().getComponentList().get(i);
			MolecularComponentSmallShape mlcls = new MolecularComponentSmallShape(100, 50, mc, shapeManager, graphicsContext, owner, this);
			offsetFromRight += mlcls.getWidth() + MolecularComponentSmallShape.componentSeparation;
		}
		
		width = baseWidth + offsetFromRight;	// adjusted for # of components
		height = baseHeight + MolecularComponentSmallShape.componentDiameter / 2;
		int fixedPart = xPos + width;
		offsetFromRight = 4;
		for(int i=numComponents-1; i >=0; i--) {
			int rightPos = fixedPart - offsetFromRight;		// we compute distance from right end
			int y = yPos + height - MolecularComponentSmallShape.componentDiameter;
			// now that we know the dimensions of the molecular type shape we create the component shapes
			MolecularComponent mc = mt.getComponentList().get(i);
			MolecularComponentSmallShape mcss = new MolecularComponentSmallShape(rightPos, y-2, mc, shapeManager, graphicsContext, owner, this);
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
	public MolecularType getMolecularType() {
		return mt;
	}
	public MolecularTypePattern getMolecularTypePattern() {
		return mtp;
	}
	public MolecularComponentSmallShape getComponentShape(int index) {
		return componentShapes.get(index);
	}
	public MolecularComponentSmallShape getShape(MolecularComponentPattern mcpTo) {
		for(MolecularComponentSmallShape mcss : componentShapes) {
			MolecularComponentPattern mcpThis = mcss.getMolecularComponentPattern();
			if(mcpThis == mcpTo) {
				return mcss;
			}
		}
		return null;
	}
	
	@Override
	public AbstractShape getParentShape() {
		return parentShape ;
	}
	public DisplayRequirements getDisplayRequirements() {
		if(parentShape == null) {
			return DisplayRequirements.normal;
		}
		return parentShape.getDisplayRequirements();
	}
	private Color getDefaultColor(Color defaultCandidate) {
		if(shapeManager == null) {
			return defaultCandidate;
		}
		return shapeManager.isEditable() ? defaultCandidate : LargeShapePanel.uneditableShape;
	}

	public void paintSelf(Graphics g) {
		paintSpecies(g);
	}
	// --------------------------------------------------------------------------------------
	// paintComponent is being overridden in the renderer
	//
	private void paintSpecies(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Color primaryColor = null;
		Color border = Color.black;
		int finalHeight = baseHeight;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(mt == null && mtp == null) {		// plain species context
			
			if(shapeManager == null) {
				 primaryColor = Color.green.darker().darker();
			} else {
				primaryColor = shapeManager.isEditable() ? Color.green.darker().darker() : Color.gray;
			}
			
			 finalHeight = baseHeight+3;
			Point2D center = new Point2D.Float(xPos+finalHeight/3, yPos+finalHeight/3);
			float radius = finalHeight*0.5f;
			Point2D focus = new Point2D.Float(xPos+finalHeight/3-1, yPos+finalHeight/3-1);
			float[] dist = {0.1f, 1.0f};
			Color[] colors = {Color.white, primaryColor};
			RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
			g2.setPaint(p);
			Ellipse2D circle = new Ellipse2D.Double(xPos, yPos, finalHeight, finalHeight);
			g2.fill(circle);
			Ellipse2D circle2 = new Ellipse2D.Double(xPos-1, yPos-1, finalHeight, finalHeight);
			g2.setPaint(getDefaultColor(Color.darkGray));
			g2.draw(circle2);
				
			g.setColor(colorOld);
			return;
		} else {							// molecular type, species pattern, observable
			if(mt == null || mt.getModel() == null) {
				primaryColor = Color.blue.darker().darker();
			} else {
				if(shapeManager != null) {
					if(shapeManager.isShowMoleculeColor()) {
						RbmModelContainer rbmmc = mt.getModel().getRbmModelContainer();
						List<MolecularType> mtList = rbmmc.getMolecularTypeList();
						int index = mtList.indexOf(mt);
						index = index%7;
						primaryColor = MolecularTypeLargeShape.colorTable[index].darker().darker();
					} else {
						primaryColor = Color.gray;
					}
					border = shapeManager.isEditable() ? Color.black : LargeShapePanel.uneditableShape;
				} else {
					RbmModelContainer rbmmc = mt.getModel().getRbmModelContainer();
					List<MolecularType> mtList = rbmmc.getMolecularTypeList();
					int index = mtList.indexOf(mt);
					index = index%7;
					primaryColor = MolecularTypeLargeShape.colorTable[index].darker().darker();
				}
			}
			if(owner instanceof MolecularType && AbstractComponentShape.hasErrorIssues(owner, mt)) {
				primaryColor = Color.red;
			}
		}
		
		GradientPaint p = new GradientPaint(xPos, yPos, primaryColor, xPos, yPos + finalHeight/2, Color.WHITE, true);
		g2.setPaint(p);

		RoundRectangle2D rect = new RoundRectangle2D.Float(xPos, yPos, width, finalHeight, cornerArc, cornerArc);
		g2.fill(rect);
		RoundRectangle2D inner = new RoundRectangle2D.Float(xPos+1, yPos+1, width-2, finalHeight-2, cornerArc-3, cornerArc-3);
		g2.setPaint(border);
		g2.draw(rect);
		
		g.setColor(colorOld);
		for(MolecularComponentSmallShape mcss : componentShapes) {
			mcss.paintSelf(g);
		}
		g.setColor(colorOld);
	}
	
	public static void paintDummy(Graphics g, int xPos, int yPos) {
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Stroke strokeOld = g2.getStroke();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		final float dash1[] = { 2.0f };
		final BasicStroke dashed = new BasicStroke(2.0f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
		g2.setStroke(dashed);
		int w = getDummyWidth();
		int h = baseHeight;
		int c = cornerArc;
		RoundRectangle2D rect = new RoundRectangle2D.Float(xPos+1, yPos+1, w-1, h-1, c-3, c);
//		RoundRectangle2D inner = new RoundRectangle2D.Float(xPos+1, yPos+1, w-2, h-2, c-3, c-3);
//		g2.setPaint(Color.LIGHT_GRAY);
//		g2.draw(inner);
		g2.setPaint(Color.LIGHT_GRAY);
		g2.draw(rect);
		g2.setColor(colorOld);
		g2.setStroke(strokeOld);
	}
	public static int getDummyWidth() {
		return baseWidth+7;
	}
	
	
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		paintSelf(g);
	}
	@Override
	public int getIconWidth() {
		return width;
	}
	@Override
	public int getIconHeight() {
		return height;
	}
}
