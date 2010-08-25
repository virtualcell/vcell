package cbit.vcell.constraints.gui;
import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.visualstate.ImmutableVisualState;
import cbit.gui.graph.visualstate.VisualState;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics2D;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 11:58:47 AM)
 * @author: Jim Schaff
 */
public abstract class ConstraintGraphNode extends ElipseShape {
	protected int radius = 8;
	private int degree = 0;

	/**
	 * ConstraintVarNode constructor comment.
	 * @param node cbit.vcell.mapping.potential.Node
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public ConstraintGraphNode(ConstraintsGraphModel graphModel, int argDegree) {
		super(graphModel);
		defaultBG = java.awt.Color.white;
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
		this.degree = argDegree;
	}

	@Override
	public VisualState createVisualState() {
		return new ImmutableVisualState(this, VisualState.PaintLayer.NODE);
	}

	protected ConstraintContainerImpl getConstraintContainerImpl() {
		return ((ConstraintsGraphModel)graphModel).getConstraintContainerImpl();
	}

	public int getDegree() {
		return degree;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public Dimension getPreferedSize(java.awt.Graphics2D g) {
		java.awt.FontMetrics fm = g.getFontMetrics();
		labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
		labelSize.width = fm.stringWidth(getLabel());
		//	preferedSize.height = radius*2 + labelSize.height;
		//	preferedSize.width = Math.max(radius*2,labelSize.width);
		preferredSize.height = radius*2;
		preferredSize.width = radius*2;
		return preferredSize;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 */
	@Override
	public Point getSeparatorDeepCount() {	
		return new Point(0,0);
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public void layout() {

		//	if (screenSize.width<labelSize.width ||
		//		 screenSize.height<labelSize.height){
		//		 throw new Exception("screen size smaller than label");
		//	} 
		//
		// this is like a row/column layout  (1 column)
		//
		int centerX = shapeSize.width/2;

		//
		// position label
		//
		labelPos.x = centerX - labelSize.width/2;
		labelPos.y = 0;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @param newSize java.awt.Dimension
	 */
	@Override
	public void resize(Graphics2D g, Dimension newSize) {
		return;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (7/10/2003 12:35:00 PM)
	 * @param newDegree int
	 */
	public void setDegree(int newDegree) {
		if (newDegree < 0){
			throw new IllegalArgumentException("degree must be non-negative");
		}
		degree = newDegree;
	}
}