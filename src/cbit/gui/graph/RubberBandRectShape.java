package cbit.gui.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.*;
/**
 * This type was created in VisualAge.
 */
public final class RubberBandRectShape extends RectangleShape {
	private Point start = new Point();
	private Point end = new Point();

	/**
	 * RubberBandEdgeShape constructor comment.
	 * @param start java.awt.Point
	 * @param end java.awt.Point
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public RubberBandRectShape(java.awt.Point start, java.awt.Point end, GraphModel graphModel) {
		super(graphModel);
		this.start = start;
		this.end = end;
		defaultFG = java.awt.Color.red;
		forgroundColor = defaultFG;
	}

	@Override
	public Object getModelObject() {
		return null;
	}


	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void layout() {

		shapeSize.width = Math.abs(end.x-start.x);
		shapeSize.height = Math.abs(end.y-start.y);
		relativePos.x = Math.min(end.x,start.x);
		relativePos.y = Math.min(end.y,start.y);
		//
		// this is like a row/column layout  (1 column)
		//
		int centerX = shapeSize.width/2;
		int centerY = shapeSize.height/2;

		//
		// position label
		//
		labelPos.x = centerX - labelSize.width/2;
		labelPos.y = centerY - labelSize.height/2;
	}


	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void refreshLabel() {
	}


	/**
	 * This method was created in VisualAge.
	 * @param start java.awt.Point
	 * @param end java.awt.Point
	 */
	public void setEnd(Point end) throws Exception {

		this.end = end;

		layout();
	}
}