/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt;
import cbit.vcell.math.MathException;
import cbit.vcell.mapping.MappingException;
/**
 * Insert the type's description here.
 * Creation date: (8/22/2005 9:51:02 AM)
 * @author: Jim Schaff
 */
public class ModelOptimizationMappingTest {
/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 9:51:09 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		test();
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 9:52:30 AM)
 */
public static void test() throws MappingException, MathException {
	ModelOptimizationSpec modelOptSpec = ModelOptimizationSpecTest.getExample();
	ModelOptimizationMapping modelOptMapping = new ModelOptimizationMapping(modelOptSpec);
	cbit.vcell.opt.OptimizationSpec optSpec = modelOptMapping.getOptimizationSpec();
	System.out.println(optSpec.getVCML());
}
}
