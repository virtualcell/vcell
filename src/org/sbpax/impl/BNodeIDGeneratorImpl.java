/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.impl;

public class BNodeIDGeneratorImpl implements BNodeIDGenerator {

	protected final String prefix;
	protected long count = 0;
	
	public BNodeIDGeneratorImpl(String prefix) { this.prefix = prefix; }
	
	public String getNextBNodeID() {
		String nextID = prefix + "_" + count;
		++count;
		return nextID;
	}

}
