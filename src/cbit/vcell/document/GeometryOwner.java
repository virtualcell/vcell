package cbit.vcell.document;
import java.beans.PropertyChangeListener;

import cbit.vcell.geometry.Geometry;

public interface GeometryOwner {
	public static final String PROPERTY_NAME_GEOMETRY = "geometry";
	
	Geometry getGeometry();
	void addPropertyChangeListener(PropertyChangeListener listener);
	void removePropertyChangeListener(PropertyChangeListener listener);
}

