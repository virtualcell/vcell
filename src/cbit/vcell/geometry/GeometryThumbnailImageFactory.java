package cbit.vcell.geometry;

import cbit.image.ImageException;
import cbit.image.ThumbnailImage;
import cbit.vcell.geometry.gui.GeometryThumbnailImageFactoryAWT;

public abstract class GeometryThumbnailImageFactory {
	private static GeometryThumbnailImageFactory geometryThumbnailImageFactory = new GeometryThumbnailImageFactoryAWT();
	
	public static void setGeometryThumbnailImageFactory(GeometryThumbnailImageFactory argGeometryThumbnailImageFactory){
		GeometryThumbnailImageFactory.geometryThumbnailImageFactory = argGeometryThumbnailImageFactory;
	}
	
	public static GeometryThumbnailImageFactory getGeometryThumbnailImageFactory(){
		return geometryThumbnailImageFactory;
	}

	public abstract ThumbnailImage getThumbnailImage(GeometrySpec geometrySpec) throws ImageException;
}
