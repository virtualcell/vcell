package cbit.gui.graph.groups;

/* A group of model elements
 * September 2010
 */

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VCNodeGroup {

	protected final String name;
	protected final Set<Object> modelObjects;

	public VCNodeGroup(String name, Collection<?> modelObjects) {
		this.name = name;
		this.modelObjects = Collections.unmodifiableSet(new HashSet<Object>(
				modelObjects));
	}

	public String getName() {
		return name;
	}

	public Set<Object> getModelObjects() {
		return modelObjects;
	}

	public int hashCode() {
		return name.hashCode() + modelObjects.hashCode();
	}

	public boolean equals(Object other) {
		if (other instanceof VCNodeGroup) {
			return name.equals(((VCNodeGroup) other).getName())
					&& modelObjects.equals(((VCNodeGroup) other)
							.getModelObjects());
		}
		return false;
	}
}
