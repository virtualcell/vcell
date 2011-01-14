package cbit.vcell.client.desktop.biomodel.pathway;

import java.awt.Graphics2D;

import org.vcell.pathway.PathwayModel;

import cbit.gui.graph.ContainerShape;

public class PathwayContainerShape extends ContainerShape {
	private PathwayModel pathwayModel = null;

	public PathwayContainerShape(PathwayGraphModel graphModel, PathwayModel pathwayModel) {
		super(graphModel);
		this.pathwayModel = pathwayModel;
		setLabel(" ");
	}

	@Override
	public Object getModelObject() {
		return pathwayModel;
	}

	@Override
	public void paintSelf (Graphics2D g, int absPosX, int absPosY ) {
		g.setColor(backgroundColor);
		g.fillRect(absPosX, absPosY, spaceManager.getSize().width, spaceManager.getSize().height);
		g.setColor(forgroundColor);
		g.drawRect(absPosX,absPosY, spaceManager.getSize().width, spaceManager.getSize().height);
	}


	@Override
	public void notifySelected() {
		notifyUnselected();
	}
}
