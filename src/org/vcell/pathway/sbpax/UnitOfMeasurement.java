/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.sbpax;

import java.util.ArrayList;
import java.util.List;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.BioPaxObjectImpl;
import org.vcell.pathway.UtilityClass;
import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class UnitOfMeasurement extends BioPaxObjectImpl implements UtilityClass {

	protected List<String> names = new ArrayList<String>();
	protected List<String> symbols = new ArrayList<String>();
	
	public List<String> getNames() { return names; }
	public void setNames(List<String> names) { this.names = names; }
	public List<String> getSymbols() { return symbols; }
	public void setSymbols(List<String> symbols) { this.symbols = symbols; }
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}
}
