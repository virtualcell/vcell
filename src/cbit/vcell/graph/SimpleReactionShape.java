package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import cbit.vcell.model.SimpleReaction;

public class SimpleReactionShape extends ReactionStepShape {

	public SimpleReactionShape(SimpleReaction simpleReaction,
			ModelCartoon modelCartoon) {
		super(simpleReaction, modelCartoon);
	}

	@Override
	public Point getAttachmentLocation(int attachmentType) {
		switch (attachmentType) {
		case ATTACH_CENTER:
			return new Point(getSpaceManager().getSize().width / 2, getSpaceManager().getSize().height / 2);
		case ATTACH_LEFT:
			return new Point(getSpaceManager().getSize().height / 2, getSpaceManager().getSize().height / 2);
		case ATTACH_RIGHT:
			return new Point(getSpaceManager().getSize().width - getSpaceManager().getSize().height / 2, getSpaceManager().getSize().height / 2);
		}
		return null;
	}

	public SimpleReaction getSimpleReaction() {
		return (SimpleReaction) reactionStep;
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY) {
		// draw elipse and two circles
		int diameter = getSpaceManager().getSize().height;
		int hOval = diameter / 2;
		Graphics2D g2D = g;
		g2D.setColor(forgroundColor);
		if (icon == null) {
			icon = new Area();
			icon.add(new Area(new Ellipse2D.Double(0, 0 + hOval / 2,
					getSpaceManager().getSizePreferred().width, hOval)));
			icon.add(new Area(new Ellipse2D.Double(0, 0, diameter, diameter)));
			icon.add(new Area(new Ellipse2D.Double(0 + getSpaceManager().getSizePreferred().width
					- diameter, 0, diameter, diameter)));
		}
		Area movedIcon = icon.createTransformedArea(
			AffineTransform.getTranslateInstance(absPosX, absPosY));

		g2D.draw(movedIcon);
		g2D.setColor(backgroundColor);
		g2D.fill(movedIcon);
		// draw label
		if (getDisplayLabels() || isSelected()) {
			g.setColor(forgroundColor);
			// java.awt.FontMetrics fm = g.getFontMetrics();
			int textX = absPosX + getSpaceManager().getSize().width / 2 - getLabelSize().width / 2;
			int textY = absPosY + getLabelSize().height - diameter;
			if (getLabel() != null && getLabel().length() > 0) {
				if (isSelected()) {
					drawRaisedOutline(textX - 5, textY - getLabelSize().height + 3,
							getLabelSize().width + 10, getLabelSize().height, g,
							Color.white, Color.black, Color.black);
				}
				g.drawString(getLabel(), textX, textY);
			}
		}
		return;
	}

	@Override
	public void refreshLabel() {
		setLabel(getSimpleReaction().getName());
	}
}
