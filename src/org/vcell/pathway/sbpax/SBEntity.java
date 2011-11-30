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

import org.vcell.pathway.BioPaxObject;

public interface SBEntity extends BioPaxObject {

	public ArrayList<SBEntity> getSBSubEntity();
	public void setSBSubEntity(ArrayList<SBEntity> sbSubEntity);
	public ArrayList<SBVocabulary> getSBTerm();
	public void setSBTerm(ArrayList<SBVocabulary> sbTerm);
	
}
