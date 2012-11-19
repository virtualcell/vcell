package cbit.vcell.message.jms;

import cbit.vcell.message.VCMessageSelector;

public class VCMessageSelectorJms extends VCMessageSelector {
	
	String selectorString = null;
	
	VCMessageSelectorJms(String selectorString){
		this.selectorString = selectorString;
	}

	@Override
	public String getSelectionString() {
		return selectorString;
	}
	
	

}
