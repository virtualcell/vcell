package cbit.vcell.graph;

import cbit.vcell.model.*;
import java.awt.*;
import java.awt.geom.*;

public class SimpleReactionShape extends ReactionStepShape {
	/**
	 * SpeciesShape constructor comment.
	 * @param label java.lang.String
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public SimpleReactionShape(SimpleReaction simpleReaction, ModelCartoon modelCartoon) {
		super(simpleReaction, modelCartoon);
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Point
	 */
	@Override
	public Point getAttachmentLocation(int attachmentType) {
		switch (attachmentType){
		case ATTACH_CENTER:
			return new Point(shapeSize.width/2, shapeSize.height/2);
		case ATTACH_LEFT:
			return new Point(shapeSize.height/2, shapeSize.height/2);
		case ATTACH_RIGHT:
			return new Point(shapeSize.width - shapeSize.height/2, shapeSize.height/2);
		}
		return null;	
	}
	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.model.Species
	 */
	public SimpleReaction getSimpleReaction() {
		return (SimpleReaction) reactionStep;
	}

	@Override
	public void paintSelf(java.awt.Graphics2D g, int absPosX, int absPosY ) {

		//
		// draw elipse and two circles
		//
		int diameter = shapeSize.height;
		int hOval = diameter/2;
		Graphics2D g2D = g;
		g2D.setColor(forgroundColor);
		if (icon == null){
			icon = new Area();
			icon.add(new Area(new Ellipse2D.Double(0,0+hOval/2,preferredSize.width,hOval)));
			icon.add(new Area(new Ellipse2D.Double(0,0,diameter,diameter)));
			icon.add(new Area(new Ellipse2D.Double(0+preferredSize.width-diameter,0,diameter,diameter)));
		}
		Area movedIcon = icon.createTransformedArea(AffineTransform.getTranslateInstance(absPosX,absPosY));

		g2D.draw(movedIcon);
		g2D.setColor(backgroundColor);
		g2D.fill(movedIcon);
		//
		// draw label
		//
		if (getDisplayLabels() || isSelected()){
			g.setColor(forgroundColor);
			//java.awt.FontMetrics fm = g.getFontMetrics();
			int textX = absPosX  + shapeSize.width/2 - labelSize.width/2;
			int textY = absPosY + labelSize.height - diameter;
			if (getLabel()!=null && getLabel().length()>0){
				if(isSelected()){
					drawRaisedOutline(textX-5,textY-labelSize.height+3,labelSize.width+10,labelSize.height,
							g,Color.white,Color.black,Color.black);
				}
				g.drawString(getLabel(),textX,textY);
			}
		}
		return;
	}

	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void refreshLabel() {
		//	setLabel("R           P");
		setLabel(getSimpleReaction().getName());
	}
}
