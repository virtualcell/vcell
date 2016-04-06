/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server.bionetgen;
import java.beans.PropertyVetoException;
import java.util.List;

import org.vcell.model.bngl.ParseException;

import cbit.vcell.mapping.BioNetGenUpdaterCallback;


public interface BNGExecutorService {
	
	public static BNGExecutorService getInstance(BNGInput bngInput, Long timeoutDurationMS){
		//return new BNGExecutorServiceNative(bngInput, timeoutDurationMS);
		return new BNGExecutorServiceMultipass(bngInput, timeoutDurationMS);
	}
	public static BNGExecutorService getInstanceOld(BNGInput bngInput, Long timeoutDurationMS){
		return new BNGExecutorServiceNative(bngInput, timeoutDurationMS);
//		return new BNGExecutorServiceMultipass(bngInput, timeoutDurationMS);
	}
		
	public void registerBngUpdaterCallback(BioNetGenUpdaterCallback callbackOwner);
	
	public List<BioNetGenUpdaterCallback> getCallbacks();
	
	public BNGOutput executeBNG() throws BNGException, ParseException, PropertyVetoException;
	
	public void stopBNG() throws Exception;
	
	public boolean isStopped();
	
	public long getStartTime();
	
}
