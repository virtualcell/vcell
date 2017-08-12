/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.sbpax.SBEntity;

public interface Entity extends SBEntity {
	public ArrayList<String> getName();
	public void setName(ArrayList<String> name);
	
	public void setFormalNames(ArrayList<String> formalNames);
	public ArrayList<String> getFormalNames();
	
	public ArrayList<String> getAvailability();
	public void setAvailability(ArrayList<String> availability);

	public ArrayList<Xref> getxRef();
	public void setxRef(ArrayList<Xref> xRef);

	public ArrayList<Provenance> getDataSource();
	public void setDataSource(ArrayList<Provenance> dataSource);

	public ArrayList<Evidence> getEvidence();
	public void setEvidence(ArrayList<Evidence> evidence);

}
