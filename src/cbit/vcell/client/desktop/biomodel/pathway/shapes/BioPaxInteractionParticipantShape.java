package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.InteractionParticipant.Type;
import org.vcell.sybil.util.gui.ArrowPainter;

import cbit.gui.graph.Shape;
import cbit.gui.graph.visualstate.EdgeVisualState;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.DefaultEdgeVisualState;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxInteractionParticipantShape extends Shape implements EdgeVisualState.Owner {

	public static final float ARROW_LENGTH = 12f;
	public static final float ARROW_WIDTH = 7f;
	
	public static final Stroke CATALYST_STROKE = new BasicStroke(1f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 1f, new float[]{2f, 2f}, 0f);
	
	protected final InteractionParticipant participant;
	protected final BioPaxConversionShape conversionShape;
	protected final BioPaxPhysicalEntityShape physicalEntityShape;
	
	public BioPaxInteractionParticipantShape(BioPaxConversionShape conversionShape,
			BioPaxPhysicalEntityShape physicalEntityShape, InteractionParticipant.Type type,
			PathwayGraphModel graphModel) {
		super(graphModel);
		participant = new InteractionParticipant(conversionShape.getConversion(), 
				physicalEntityShape.getPhysicalEntity(), type);
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
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		// TODO Auto-generated method stub
		return new Dimension(0, 0);
	}

	@Override
	protected boolean isInside(Point p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refreshLayoutSelf() {
		// do nothing
	}

	@Override
	public void paintSelf(Graphics2D g2d, int xAbs, int yAbs) {
		// TODO Auto-generated method stub
		Point startPos = conversionShape.getSpaceManager().getAbsCenter();
		Point endPos = physicalEntityShape.getSpaceManager().getAbsCenter();
		if(participant.getType().hasSuperType(Type.PHYSICAL_CONTROLLER)) {
			Stroke previousStroke = g2d.getStroke();
			g2d.setStroke(CATALYST_STROKE);
			g2d.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);			
			g2d.setStroke(previousStroke);
		} else {
			g2d.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
			
		}
		if(participant.getType().equals(Type.LEFT)) {
			ArrowPainter.paintArrow(g2d, endPos, startPos, ARROW_LENGTH, ARROW_WIDTH);
		} else if(participant.getType().equals(Type.RIGHT)) {
			ArrowPainter.paintArrow(g2d, startPos, endPos, ARROW_LENGTH, ARROW_WIDTH);
		}
	}

	@Override
	public VisualState createVisualState() {
		return new DefaultEdgeVisualState(this);
	}

	@Override
	public Object getModelObject() {
		return participant;
	}

	@Override
	public void refreshLabel() {
		// TODO Auto-generated method stub
		
	}

}
