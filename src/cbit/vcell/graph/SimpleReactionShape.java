package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import cbit.vcell.model.SimpleReaction;

public class SimpleReactionShape extends ReactionStepShape {

	public SimpleReactionShape(SimpleReaction simpleReaction,
			ModelCartoon modelCartoon) {
		super(simpleReaction, modelCartoon);
	}

	@Override
	public Point getAttachmentLocation(int attachmentType) {
		int centerX = getSpaceManager().getSize().width / 2;
		int centerY = getSpaceManager().getSize().height / 2;
		return new Point(centerX, centerY);
	}

	public SimpleReaction getSimpleReaction() {
		return (SimpleReaction) reactionStep;
	}

	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY) {
		int circleDiameter = 9;
		int shapeHeight = getSpaceManager().getSize().height;
		int shapeWidth = getSpaceManager().getSize().width;
		int offsetX = (shapeWidth-circleDiameter) / 2;
		int offsetY = (shapeHeight-circleDiameter) / 2;
		if (icon == null) {
			icon = new Area();
			//icon.add(new Area(new Ellipse2D.Double(offsetX, offsetY,circleDiameter,circleDiameter)));
			icon.add(new Area(new RoundRectangle2D.Double(offsetX, offsetY,circleDiameter,circleDiameter,circleDiameter/2,circleDiameter/2)));
		}
		Area movedIcon = icon.createTransformedArea(
			AffineTransform.getTranslateInstance(absPosX, absPosY));

		g2D.setColor(backgroundColor);
		g2D.fill(movedIcon);
		g2D.setColor(forgroundColor);
		g2D.draw(movedIcon);
		// draw label
		if (getDisplayLabels() || isSelected()) {
			g2D.setColor(forgroundColor);
			int textX = absPosX + shapeWidth / 2 - getLabelSize().width / 2;
			int textY = absPosY + offsetY - getLabelSize().height / 2;
			if (getLabel() != null && getLabel().length() > 0) {
				if (isSelected()) {
					drawRaisedOutline(textX - 5, textY - getLabelSize().height + 3,
							getLabelSize().width + 10, getLabelSize().height, g2D,
							Color.white, Color.black, Color.black);
				}
				g2D.drawString(getLabel(), textX, textY);
			}
		}
		return;
	}

	@Override
	public void refreshLabel() {
		setLabel(getSimpleReaction().getName());
	}
}
