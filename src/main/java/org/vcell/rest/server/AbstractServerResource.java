package org.vcell.rest.server;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.Variant;

public class AbstractServerResource extends WadlServerResource {

	public AbstractServerResource() {
		super();
	}

	protected boolean getBooleanQueryValue(String paramName, boolean bDefault) {
		String stringValue = getQueryValue(paramName);
		if (stringValue!=null && stringValue.length()>0){
			return (stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("on") || stringValue.equalsIgnoreCase("yes"));
		}else{
			return bDefault;
		}
	}

	protected Long getLongQueryValue(String paramName) {
		String stringValue = getQueryValue(paramName);
		Long longValue = null;
		if (stringValue!=null && stringValue.length()>0){
			try {
				longValue = Long.parseLong(stringValue);
			}catch (NumberFormatException e){
			}
		}
		return longValue;
	}


	@Override
	protected List<Variant> getWadlVariants() {
		ArrayList<Variant> variants = new ArrayList<Variant>();
		variants.add(new Variant(MediaType.APPLICATION_WADL));
		return variants;
	}
}