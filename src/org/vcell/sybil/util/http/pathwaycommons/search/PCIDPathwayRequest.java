/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.pathwaycommons.search;

/*   PCIDRequest  --- by Oliver Ruebenacker, UCHC --- March 2009 to January 2010
 *   Launch a web request using command get_record_by_cpathID from Pathway Commons,
 *   expecting a pathway as a return
 */

public class PCIDPathwayRequest extends PCIDRequest {
	
	
	public PCIDPathwayRequest(String id)  { super(id); }

	@Override
	public String description() { return "CPath id search for pathway " + id; }
	@Override public String shortTitle() { return "Pathway " + id; }
	
}
