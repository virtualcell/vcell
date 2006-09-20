package cbit.vcell.constraints.gui;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics2D;
import cbit.gui.graph.GraphModel;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 11:58:47 AM)
 * @author: Jim Schaff
 */
public class BoundsNode extends ConstraintGraphNode {
	private cbit.vcell.constraints.SimpleBounds simpleBounds = null;

/**
 * ConstraintVarNode constructor comment.
 * @param node cbit.vcell.mapping.potential.Node
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public BoundsNode(cbit.vcell.constraints.SimpleBounds argSimpleBounds, ConstraintsGraphModel graphModel) {
	super(graphModel,1);
	this.simpleBounds = argSimpleBounds;
	defaultBG = java.awt.Color.white;
	defaultFGselect = java.awt.Color.black;
	backgroundColor = defaultBG;
	refreshLabel();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public java.lang.Object getModelObject() {
	return simpleBounds;
}


/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics2D g, int parentOriginX, int parentOriginY) {

   int absPosX = screenPos.x + parentOriginX;
   int absPosY = screenPos.y + parentOriginY;
	//
	boolean isBound = false;
	//
	// draw elipse
	//
	if (getConstraintContainerImpl().getActive(simpleBounds)){
		g.setColor(backgroundColor);
		g.fillRect(absPosX+1,absPosY+1+labelPos.y,2*radius-1,2*radius-1);
		g.setColor(forgroundColor);
		g.drawRect(absPosX,absPosY+labelPos.y,2*radius,2*radius);
	}else{
		if (isSelected()){
			g.setColor(java.awt.Color.pink);
			g.fillRect(absPosX+1,absPosY+1+labelPos.y,2*radius-1,2*radius-1);
			g.setColor(java.awt.Color.lightGray);
			g.drawRect(absPosX,absPosY+labelPos.y,2*radius,2*radius);
		}else{
			g.setColor(java.awt.Color.white);
			g.fillRect(absPosX+1,absPosY+1+labelPos.y,2*radius-1,2*radius-1);
			g.setColor(java.awt.Color.lightGray);
			g.drawRect(absPosX,absPosY+labelPos.y,2*radius,2*radius);
		}
	}
	//
	// draw label
	//
	if (isSelected()){
		java.awt.FontMetrics fm = g.getFontMetrics();
		int textX = labelPos.x + absPosX;
		int textY = labelPos.y + absPosY;
		g.setColor(forgroundColor);
		if (getLabel()!=null && getLabel().length()>0){
			g.drawString(getLabel(),textX,textY);
		}
	}
	return;
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel(simpleBounds.getIdentifier()+"=["+simpleBounds.getBounds().lo()+","+simpleBounds.getBounds().hi()+"]");
}
}