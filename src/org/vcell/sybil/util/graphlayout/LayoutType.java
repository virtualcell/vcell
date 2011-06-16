/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.graphlayout;

/*   LayoutType  --- by Oliver Ruebenacker, UCHC --- January 2008 to February 2009
 *   Types of graph layout methods
 */


public class LayoutType {
	protected String name;
	public LayoutType(String nameNew) { name = nameNew; }
	public String name() { return name; }
	public static final LayoutType Annealer = new LayoutType("Annealer");
	public static final LayoutType Circularizer = new LayoutType("Circularizer");
	public static final LayoutType Leveller = new LayoutType("Leveller");
	public static final LayoutType Randomizer = new LayoutType("Randomizer");
	public static final LayoutType Relaxer = new LayoutType("Relaxer");
}
