package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.vcell.geometry.Geometry;

public class SubVolumeContainerShape extends ContainerShape{
	Geometry geom = null;
	private BufferedImage brightImage = null;

	public SubVolumeContainerShape(Geometry argGeom, GraphModel graphModel) {
		super(graphModel);
		geom = argGeom;
	}

	@Override
	public Object getModelObject() {
		return geom;
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		if(brightImage != null){
			return new Dimension(brightImage.getWidth(),brightImage.getHeight());
		}
		return new Dimension(10,10);
	}

	@Override
	public Point getSeparatorDeepCount() {	
		return new Point(0,0);
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY) {
		// draw background image (of handles)
		if(brightImage != null){
			g.drawImage(brightImage,absPosX, absPosY, null);
		}
	}

	public void setBrightImage(BufferedImage newBrightImage){
		brightImage = newBrightImage;
	}

	@Override
	public void refreshLabel() {}

	@Override
	public void resize(Graphics2D g, Dimension newSize) {
		return;
	}
}