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

import org.vcell.sybil.util.lists.ListOfTwo;

public abstract class UOMEBinaryExpression implements UOMEExpression {

	protected UOMEUnit unit1, unit2;
	
	public void setUnit1(UOMEUnit unit1) { this.unit1 = unit1; }
	public UOMEUnit getUnit1() { return unit1; }
	public void setUnit2(UOMEUnit unit2) { this.unit2 = unit2; }
	public UOMEUnit getUnit2() { return unit2; }
	public List<UOMEUnit> getUnits() { return new ListOfTwo<UOMEUnit>(unit1, unit2); }
	
	
}
