package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;

public class FeatureShape extends StructureShape {

	public FeatureShape (Feature feature, Model model, GraphModel graphModel) {
		super(feature, model, graphModel);
	}

	@Override
	public Point getAttachmentLocation(int attachmentType) {
		return new Point(getSpaceManager().getSize().width/2, getLabelSize().height*5/4);
	}

	public Feature getFeature() {
		return (Feature)getStructure();
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		getSpaceManager().setSizePreferred((getLabelSize().width + defaultSpacingX*2), 
				(getLabelSize().height + defaultSpacingY*2));
		return getSpaceManager().getSizePreferred();
	}

	@Override
	public void refreshLayoutSelf() {
		int centerX = getSpaceManager().getSize().width/2;
		int currentY = getLabelSize().height;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = currentY;
	}

}