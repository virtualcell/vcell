package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.Issue.Severity;

import cbit.vcell.graph.SpeciesPatternSmallShape.DisplayRequirements;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;

public class MolecularComponentSmallShape extends AbstractComponentShape implements AbstractShape {
	
	static final int componentSeparation = 1;		// distance between components
	static final int componentDiameter = 8;			// diameter of the component (circle)
	
	final Graphics graphicsContext;
	
	private int xPos = 0;
	private int yPos = 0;
	private int width = componentDiameter;
	private int height = componentDiameter;
	
	private final LargeShapePanel shapeManager;
	private final MolecularComponent mc;
	private final MolecularComponentPattern mcp;
	private final Displayable owner;
	private final AbstractShape parentShape;

	// rightPos is rightmost corner of the ellipse, we compute the xPos based on the text width
	public MolecularComponentSmallShape(int rightPos, int y, MolecularComponent mc, LargeShapePanel shapeManager, Graphics graphicsContext, Displayable owner, AbstractShape parentShape) {
		this.owner = owner;
		this.parentShape = parentShape;
		this.mcp = null;
		this.mc = mc;
		this.shapeManager = shapeManager;
		this.graphicsContext = graphicsContext;
		xPos = rightPos-width;
		yPos = y;
	}
	public MolecularComponentSmallShape(int rightPos, int y, MolecularComponentPattern mcp, LargeShapePanel shapeManager, Graphics graphicsContext, Displayable owner, AbstractShape parentShape) {
		this.owner = owner;
		this.parentShape = parentShape;
		this.mcp = mcp;
		this.mc = mcp.getMolecularComponent();
		this.shapeManager = shapeManager;
		this.graphicsContext = graphicsContext;
		xPos = rightPos-width;
		yPos = y;
	}

	public MolecularComponentPattern getMolecularComponentPattern() {
		return mcp;
	}
	
	public int getX(){
		return xPos;
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
	
	private Color setComponentColor() {
		if(getDisplayRequirements() == DisplayRequirements.highlightBonds) {
			return setComponentColorHighlightBonds();
		} else {
			return setComponentColorNormal();
		}
	}
	private Color setComponentColorHighlightBonds() {
		if(owner == null) {
			return componentBad;
		}
		Color componentColor = componentBad;
		if(owner instanceof MolecularType) {
			componentColor = componentHidden;
		} else if(owner instanceof SpeciesContext) {
			if(mcp.getBondType() == BondType.Specified) {
				componentColor = Color.green;
			} else {
				componentColor = componentHidden;
			}
		} else if(mcp != null && owner instanceof RbmObservable) {
			if(mcp.getBondType() == BondType.Specified) {
				componentColor = Color.green;
			} else {
				componentColor = componentHidden;
			}
		} else if(owner instanceof ReactionRule || owner instanceof SimpleReaction || owner instanceof FluxReaction) {
			if(mcp.getBondType() == BondType.Specified) {
				componentColor = Color.green;
			} else {
				componentColor = componentHidden;
			}
		}
		return componentColor;
	}
	private Color setComponentColorNormal() {
		if(owner == null) {
//			return componentBad;
			return Color.red;
		}
//		Color componentColor = componentBad;
		Color componentColor = Color.red;

		if(owner instanceof MolecularType) {
			componentColor = componentGreen;
		} else if(owner instanceof SpeciesContext) {
			componentColor = componentGreen;
		} else if(mcp != null && owner instanceof RbmObservable) {
			componentColor = componentHidden;
			if(mcp.isbVisible()) {
				componentColor = componentGreen;
			}
			ComponentStatePattern csp = mcp.getComponentStatePattern();
			if(csp != null && !csp.isAny()) {
				componentColor = componentGreen;
			}
		} else if(owner instanceof SimpleReaction || owner instanceof FluxReaction) {
			componentColor = componentHidden;
			if(mcp.isbVisible()) {
				componentColor = componentGreen;
			}
			ComponentStatePattern csp = mcp.getComponentStatePattern();
			if(csp != null && !csp.isAny()) {
				componentColor = componentGreen;
			}
		} else if(owner instanceof ReactionRule) {
			componentColor = componentHidden;
			if(shapeManager != null && shapeManager.isShowDifferencesOnly()) {
				switch (((RulesShapePanel)shapeManager).hasBondChanged(mcp)){
				case CHANGED:
					componentColor = Color.orange;
					break;
				}
			} else {
				if(mcp.isbVisible()) {
					componentColor = componentGreen;
				}
				ComponentStatePattern csp = mcp.getComponentStatePattern();
				if(csp != null && !csp.isAny()) {
					componentColor = componentGreen;
				}
			}
		}
//		if(!mc.getComponentStateDefinitions().isEmpty()) {
//			// comment this out if don't want to show the states at all
//			componentColor = componentYellow;
//		}
		if(AbstractComponentShape.hasErrorIssues(owner, mcp, mc)) {
//			componentColor = componentBad;
			componentColor = Color.red;
		}
		return componentColor;

//		Old way of doing it below
//		Color componentColor = componentBad;
//		if(mc.getComponentStateDefinitions().isEmpty()) {
//			componentColor = componentGreen;
//		} else {
//			componentColor = componentYellow;
//		}
//		if(isHidden(owner, mcp)) {
//			componentColor = componentHidden;
//		}
//		if(hasIssues(owner, mcp, mc)) {
//			componentColor = componentBad;
//		}
//		return componentColor;
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

	public void paintSelf(Graphics g) {
		paintComponent(g);
	}
	
	// ----------------------------------------------------------------------------------------------
	private void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g.getColor();

		Color componentColor = setComponentColor();
		
		g2.setColor(componentColor);
		g2.fillOval(xPos, yPos, componentDiameter, componentDiameter);			// g.fillRect(xPos, yPos, width, height);
		g2.setColor(Color.BLACK);
		g2.drawOval(xPos, yPos, componentDiameter, componentDiameter);
		
		if(mc.getComponentStateDefinitions().size()>0) {
			if(getDisplayRequirements() == DisplayRequirements.highlightBonds) {
				g2.setColor(componentHidden);
			} else {
				if(shapeManager != null && owner instanceof ReactionRule && shapeManager.isShowDifferencesOnly()) {
					switch (((RulesShapePanel)shapeManager).hasStateChanged(mcp)){
					case CHANGED:
						g2.setColor(Color.orange);
						break;
					case UNCHANGED:
						g2.setColor(componentHidden);
						break;
					}
				} else {
					g2.setColor(componentYellow);
				}
			}
			g2.fillOval(xPos + width - 5, yPos-2, 5, 5);
			g.setColor(Color.darkGray);
			g2.drawOval(xPos + width - 5, yPos-2, 5, 5);
		}
		g.setColor(colorOld);
	}
}
