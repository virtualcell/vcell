package org.vcell.service;

import org.scijava.Context;
import org.scijava.service.ServiceHelper;

public class VCellServiceHelper {
	private static final Context context = new Context();
	
	public static ServiceHelper getInstance(){
		return new ServiceHelper(context);
	}
	
}
