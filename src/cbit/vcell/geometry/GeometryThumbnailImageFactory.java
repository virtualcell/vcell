package cbit.vcell.geometry;

import cbit.image.ImageException;
import cbit.image.ThumbnailImage;

public interface GeometryThumbnailImageFactory {
	public ThumbnailImage getThumbnailImage(GeometrySpec geometrySpec) throws ImageException;
}
