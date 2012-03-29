/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.image.ThumbnailImage;
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
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		if(brightImage != null){
			return new Dimension(brightImage.getWidth(),brightImage.getHeight());
		}
		return new Dimension(10,10);
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY) {
		// draw background image (of handles)
		if(brightImage != null){
			g.drawImage(brightImage,absPosX, absPosY, null);
		}
	}

	public void setBrightImage(ThumbnailImage thumbnailImage){
		if (thumbnailImage!=null){
			if (thumbnailImage.getNativeImage() instanceof BufferedImage){
				brightImage = (BufferedImage)thumbnailImage.getNativeImage();
			}else{
				brightImage = new BufferedImage(thumbnailImage.getSizeX(),thumbnailImage.getSizeY(),BufferedImage.TYPE_INT_ARGB);
				brightImage.setRGB(0, 0, thumbnailImage.getSizeX(), thumbnailImage.getSizeY(), thumbnailImage.getRGB(), 0, thumbnailImage.getSizeX());
			}
		}else{
			brightImage = null;
		}
	}

	@Override
	public void refreshLabel() {}

}
