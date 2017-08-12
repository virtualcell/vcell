/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.Serializable;

import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;

public interface GeometryClass extends Matchable, Serializable {
	
	public String getName();
	public void setName(String name) throws PropertyVetoException;
	
	public void addPropertyChangeListener(PropertyChangeListener listener);
	public void removePropertyChangeListener(PropertyChangeListener listener);
	public KeyValue getKey();

}
