package cbit.vcell.client.desktop.biomodel.pathway;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import cbit.gui.graph.visualstate.EdgeVisualState;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.DefaultEdgeVisualState;

public class BioPaxConversionEdgeShape extends Shape implements EdgeVisualState.Owner {

	protected final BioPaxConversionEdge edge;
	protected final BioPaxConversionShape conversionShape;
	protected final BioPaxPhysicalEntityShape physicalEntityShape;
	
	public BioPaxConversionEdgeShape(BioPaxConversionShape conversionShape,
			BioPaxPhysicalEntityShape physicalEntityShape, PathwayGraphModel graphModel) {
		super(graphModel);
		edge = new BioPaxConversionEdge(conversionShape.getConversion(), 
				physicalEntityShape.getPhysicalEntity());
		this.conversionShape = conversionShape;
		this.physicalEntityShape = physicalEntityShape;
	}
	public VisualState.Owner getStartShape() {
		return conversionShape;
	}

	public VisualState.Owner getEndShape() {
		return physicalEntityShape;
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		// TODO Auto-generated method stub
		return new Dimension(0, 0);
	}

	@Override
	protected boolean isInside(Point p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refreshLayout() throws LayoutException {
		// TODO Auto-generated method stub

	}

	@Override
	public void paintSelf(Graphics2D g2d, int xAbs, int yAbs) {
		// TODO Auto-generated method stub
		Point startPos = conversionShape.getSpaceManager().getAbsCenter();
		Point endPos = physicalEntityShape.getSpaceManager().getAbsCenter();
		g2d.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
	}

	@Override
	public VisualState createVisualState() {
		return new DefaultEdgeVisualState(this);
	}

	@Override
	public Object getModelObject() {
		return edge;
	}

	@Override
	public void refreshLabel() {
		// TODO Auto-generated method stub
		
	}

}
