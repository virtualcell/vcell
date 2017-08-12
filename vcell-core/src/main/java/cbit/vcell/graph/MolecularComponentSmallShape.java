package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.Displayable;

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
	
	private final SmallShapeManager shapeManager;
	private final MolecularComponent mc;
	private final MolecularComponentPattern mcp;
	private final Displayable owner;
	private final AbstractShape parentShape;

	// rightPos is rightmost corner of the ellipse, we compute the xPos based on the text width
	public MolecularComponentSmallShape(int rightPos, int y, MolecularComponent mc, SmallShapeManager shapeManager, Graphics graphicsContext, 
			Displayable owner, AbstractShape parentShape, IssueListProvider issueListProvider) {
		super(issueListProvider); 
		
		this.owner = owner;
		this.parentShape = parentShape;
		this.mcp = null;
		this.mc = mc;
		this.shapeManager = shapeManager;
		this.graphicsContext = graphicsContext;
		xPos = rightPos-width;
		yPos = y;
	}
	public MolecularComponentSmallShape(int rightPos, int y, MolecularComponentPattern mcp, SmallShapeManager shapeManager, Graphics graphicsContext, 
			Displayable owner, AbstractShape parentShape, IssueListProvider issueListProvider) {
		super(issueListProvider);
		
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
			return Color.red;
		}
		Color componentColor = Color.red;

		if(shapeManager != null && !shapeManager.isShowMoleculeColor()) {
			componentColor = componentVeryLightGray;
		} else {
			if(owner instanceof MolecularType) {
				componentColor = Color.yellow;
			} else if(owner instanceof SpeciesContext) {
				componentColor = Color.yellow;
			} else if(mcp != null && owner instanceof RbmObservable) {
				componentColor = componentVeryLightGray;
				if(mcp.getBondType() != BondType.Possible) {
					componentColor = Color.yellow;
				}
			} else if(owner instanceof SimpleReaction || owner instanceof FluxReaction) {
				componentColor = componentVeryLightGray;
				if(mcp.getBondType() != BondType.Possible) {
					componentColor = Color.yellow;
				}
			} else if(owner instanceof ReactionRule) {
				componentColor = componentVeryLightGray;
				if(mcp.getBondType() != BondType.Possible) {
					componentColor = Color.yellow;
				}
			}
		}
		
		if(hasErrorIssues(owner, mcp, mc)) {
			componentColor = Color.red;
		}
		return componentColor;
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
		g2.setColor(getDefaultColor(Color.black));
		g2.drawOval(xPos, yPos, componentDiameter, componentDiameter);
		
		if(mc.getComponentStateDefinitions().size() > 0) {
			if(getDisplayRequirements() == DisplayRequirements.highlightBonds) {
				g2.setColor(componentVeryLightGray);
			} else {
				g2.setColor(componentVeryLightGray);
				if(owner instanceof MolecularType) {
					if(hasErrorIssues(owner, mc)) {
						g2.setColor(Color.red);
					} else {
						if(shapeManager != null && !shapeManager.isShowMoleculeColor()) {
							g2.setColor(componentVeryLightGray);
						} else {
							g2.setColor(Color.yellow);
						}
					}
				} else if(mcp != null) {
					ComponentStatePattern csp = mcp.getComponentStatePattern();
					if(csp != null && !csp.isAny()) {
						if(shapeManager != null && !shapeManager.isShowMoleculeColor()) {
							g2.setColor(componentVeryLightGray);
						} else {
							g2.setColor(Color.yellow);
						}
					}
				}
			}
			g2.fillOval(xPos + width - 5, yPos-2, 5, 5);
			g.setColor(getDefaultColor(Color.darkGray));
			g2.drawOval(xPos + width - 5, yPos-2, 5, 5);
		}
		g.setColor(colorOld);
	}
	private Color getDefaultColor(Color defaultCandidate) {
		if(shapeManager == null) {
			return defaultCandidate;
		}
		return shapeManager.isEditable() ? defaultCandidate : LargeShapeCanvas.uneditableShape;
	}

}
