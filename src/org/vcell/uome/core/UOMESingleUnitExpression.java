/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome.core;

import java.util.List;

import org.vcell.sybil.util.lists.ListOfOne;

public abstract class UOMESingleUnitExpression implements UOMEExpression {

	protected UOMEUnit unit;
	
	public void setUnit(UOMEUnit unit) { this.unit = unit; }
	public UOMEUnit getUnit() { return unit; }
	public List<UOMEUnit> getUnits() { return new ListOfOne<UOMEUnit>(unit); }
	
}
