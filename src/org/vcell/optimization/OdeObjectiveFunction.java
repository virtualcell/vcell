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
