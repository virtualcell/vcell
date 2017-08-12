package cbit.vcell.geometry.surface;

import java.util.Iterator;

/**
 * adapter {@link Surface} to {@link Iterator<Polygon>}
 */
public class SurfacePolygonIteratorAdapter implements Iterator<Polygon>{
	private final Surface surf;
	int index;
	SurfacePolygonIteratorAdapter(Surface surf) {
		super();
		this.surf = surf;
		this.index = 0;
	}
	@Override
	public boolean hasNext() {
		return index < surf.getPolygonCount();
	}
	@Override
	public Polygon next() {
		return surf.getPolygons(index++);
	}

}
