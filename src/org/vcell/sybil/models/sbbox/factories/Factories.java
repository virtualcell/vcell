/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.sbbox.factories;

/*   Factories  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   Organizes the RDF data and structures to edit it
 */

public interface Factories {
	public LocationFactory locationFactory();
	public SubstanceFactory substanceFactory();
	public SpeciesFactory speciesFactory();
	public ProcessModelFactory processModelFactory();
	public SystemModelFactory systemModelFactory();
}
