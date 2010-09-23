package cbit.vcell.graph;

import cbit.vcell.model.*;
import java.awt.*;

public class FluxReactionShape extends ReactionStepShape {

	public FluxReactionShape(FluxReaction fluxReaction, ModelCartoon modelCartoon) {
		super(fluxReaction, modelCartoon);
		defaultFGselect = java.awt.Color.red;
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
	public Dimension getPreferedSize(Graphics2D g) {
		getSpaceManager().setSizePreferred(65, 25);
		if(getLabel() != null && getLabel().length() > 0){
			FontMetrics fontMetrics = g.getFontMetrics();
			setLabelSize(fontMetrics.stringWidth(getLabel()), 
					fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent());
		}
		return getSpaceManager().getSizePreferred();
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {
		// draw and fill rounded rectangle
		int hChannel = getSpaceManager().getSize().height/2;
		g.setColor(backgroundColor);
		g.fillRoundRect(absPosX+1, absPosY+1, getSpaceManager().getSize().width-1, getSpaceManager().getSize().height-1, 15, 15);
		//	g.fillRoundRect(absPosX+1,absPosY+1,screenSize.width-1,screenSize.height-1,15,15);
		g.setColor(forgroundColor);
		g.drawRoundRect(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height, 15, 15);
		g.drawLine(absPosX,absPosY+hChannel/2, absPosX,absPosY+hChannel*3/2);
		g.drawLine(absPosX + getSpaceManager().getSize().width, absPosY + hChannel/2, absPosX + getSpaceManager().getSize().width,
				absPosY + hChannel*3/2);
		// draw and white out center channel
		g.setColor(Color.white);
		g.fillRect(absPosX, absPosY + hChannel/2 - 1, getSpaceManager().getSize().width+1, hChannel+2);
		g.setColor(forgroundColor);
		g.drawLine(absPosX, absPosY + hChannel/2-1, absPosX + getSpaceManager().getSize().width, absPosY+hChannel/2 - 1);
		g.drawLine(absPosX, absPosY + hChannel*3/2 + 1, absPosX + getSpaceManager().getSize().width,
				absPosY + hChannel*3/2 + 1);
		// draw label
		int textX = absPosX  + getSpaceManager().getSize().width/2 - getLabelSize().width/2;
		int textY = absPosY + getLabelSize().height;
		if (getLabel()!=null && getLabel().length()>0){
			if(isSelected()){
				drawRaisedOutline(textX - 5, textY - getLabelSize().height + 3,
						getLabelSize().width + 10, getLabelSize().height,
						g, Color.white, Color.black, Color.black);
			}
			g.setColor(Color.black);
			g.drawString(getLabel(),textX,textY);
			g.setColor(forgroundColor);
		}
		return;
	}

	@Override
	public void refreshLabel() {
		setLabel(getFluxReaction().getName());
	}
}
