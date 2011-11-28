package cbit.vcell.geometry;

import java.beans.PropertyChangeSupport;

import cbit.vcell.render.Vect3d;

public abstract class CSGNode {
	private String name;	
	protected CSGNode(String name) {
		this.name = name;
	}
	
	public abstract boolean isInside(Vect3d point);
	public abstract CSGNode cloneTree();

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}
}
