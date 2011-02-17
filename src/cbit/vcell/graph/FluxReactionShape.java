package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import cbit.vcell.model.FluxReaction;

public class FluxReactionShape extends ReactionStepShape {

	public FluxReactionShape(FluxReaction fluxReaction, ModelCartoon modelCartoon) {
		super(fluxReaction, modelCartoon);
	}

	@Override
	public Point getAttachmentLocation(int attachmentType) {
		switch (attachmentType){
		case ATTACH_CENTER:
			return new Point(getSpaceManager().getSize().width/2, getSpaceManager().getSize().height/2);
		case ATTACH_LEFT:
			return new Point(0, getSpaceManager().getSize().height/2);
		case ATTACH_RIGHT:
			return new Point(getSpaceManager().getSize().width, getSpaceManager().getSize().height/2);
		}
		return null;	
	}

	public FluxReaction getFluxReaction() {
		return (FluxReaction) reactionStep;
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		getSpaceManager().setSizePreferred(25, 25);
		if(getLabel() != null && getLabel().length() > 0){
			FontMetrics fontMetrics = g.getFontMetrics();
			setLabelSize(fontMetrics.stringWidth(getLabel()), 
					fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent());
		}
		return getSpaceManager().getSizePreferred();
	}

	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY) {
		// draw and fill rounded rectangle
		int height = getSpaceManager().getSize().height;
		int width = getSpaceManager().getSize().width;
		g2D.setColor(backgroundColor);
		int roundRectWidth = 16;
		int roundRectHeight = 12;
		int offsetX = (width - roundRectWidth)/2;
		int roundRectX = absPosX + offsetX;
		int offsetY = (height - roundRectHeight)/2;
		int roundRectY = absPosY + offsetY;
		int arcWidth = 10;
		g2D.fillRoundRect(roundRectX, roundRectY, roundRectWidth, roundRectHeight, arcWidth, arcWidth);
		//	g.fillRoundRect(absPosX+1,absPosY+1,screenSize.width-1,screenSize.height-1,15,15);
		g2D.setColor(forgroundColor);
		g2D.drawRoundRect(roundRectX, roundRectY, roundRectWidth, roundRectHeight, arcWidth, arcWidth);
		// draw and white out center channel
		g2D.setColor(Color.white);
		g2D.fillRect(roundRectX - 1, roundRectY + roundRectHeight/3, roundRectWidth + 2, roundRectHeight/3);
		g2D.setColor(forgroundColor);
		g2D.drawLine(roundRectX, roundRectY + roundRectHeight/3, roundRectX + roundRectWidth, roundRectY + roundRectHeight/3);
		g2D.drawLine(roundRectX, roundRectY + roundRectHeight*2/3, roundRectX + roundRectWidth, roundRectY + roundRectHeight*2/3);

		// draw label
		if (getDisplayLabels() || isSelected()) {
			g2D.setColor(forgroundColor);
			int textX = absPosX  + width/2 - getLabelSize().width/2;
			int textY = absPosY + offsetY - getLabelSize().height / 2;
			if (getLabel()!=null && getLabel().length()>0){
				if(isSelected()){
					drawRaisedOutline(textX - 5, textY - getLabelSize().height + 3,
							getLabelSize().width + 10, getLabelSize().height,
							g2D, Color.white, Color.black, Color.black);
					g2D.drawString(getLabel(),textX,textY);
				}
			}
		}
		return;
	}

	@Override
	public void refreshLabel() {
		setLabel(getFluxReaction().getName());
	}
}
