/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import cbit.vcell.opt.ReferenceData;

public class OdeObjectiveFunction implements ObjectiveFunction {
	private String sundialsInputText = null;
	private ReferenceData referenceData = null;

	public OdeObjectiveFunction(String arg_sundialsInputText, ReferenceData arg_referenceData) {
		this.sundialsInputText = arg_sundialsInputText;
		this.referenceData = arg_referenceData;
	}

	public String getSundialsInputText() {
		return sundialsInputText;
	}
	
	public ReferenceData getReferenceData() {
		return referenceData;
	}
	
}
