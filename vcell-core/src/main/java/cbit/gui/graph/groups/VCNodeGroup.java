/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
