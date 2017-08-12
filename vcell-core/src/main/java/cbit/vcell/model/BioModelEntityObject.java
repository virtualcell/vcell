/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import org.vcell.util.Displayable;
import org.vcell.util.document.Identifiable;
/*
 * used in  relationship objects (mapping between BioModel object and BioPax entity)
 * 
 * RelationshipObject implements Identifiable{
 *   private BioPaxObject biopaxObject = null;
 *   private BioModelEntityObject bioModelObject
 *   ...
 */
public interface BioModelEntityObject extends Identifiable, Displayable {
	String getName();
	String getTypeLabel();
	Structure getStructure();
}
