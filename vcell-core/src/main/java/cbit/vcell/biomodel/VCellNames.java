/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel;
import java.util.Hashtable;
/**
 * Insert the type's description here.
 * Creation date: (1/18/01 1:24:28 PM)
 * @author: Jim Schaff
 */
public class VCellNames {
	private static Hashtable hash = null;

	static {
		hash = new Hashtable();
		hash.put(cbit.vcell.biomodel.BioModel.class, "BioModel");
		hash.put(cbit.vcell.mapping.SimulationContext.class, "Application");
		hash.put(cbit.vcell.model.Model.class, "Physiology");
		hash.put(cbit.vcell.solver.Simulation.class, "Simulation");
	};

/**
 * Insert the method's description here.
 * Creation date: (1/18/01 1:25:42 PM)
 * @return java.lang.String
 * @param object java.lang.Object
 */
private static String createName(Class cls) {
	String clsName = cls.getName();
	int indexOfLastDot = clsName.lastIndexOf('.');
	return clsName.substring(indexOfLastDot+1);
}


/**
 * Insert the method's description here.
 * Creation date: (1/18/01 1:25:42 PM)
 * @return java.lang.String
 * @param object java.lang.Object
 */
public static String getName(Object object) {
	if (object == null){
		return "<<<<NULL OBJECT>>>>";
	}
	Class cls = object.getClass();
	String hashedValue = (String)hash.get(cls);
	if (hashedValue != null){
		return hashedValue;
	}else{
		return createName(cls);
	}
}
}
