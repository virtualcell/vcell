/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.db;
import java.io.Serializable;

import org.vcell.util.document.User;

import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (9/3/2003 10:39:26 AM)
 * @author: Fei Gao
 */
public class SimulationMetadata implements Serializable {
	public final VCSimulationIdentifier vcSimID;
	public final String simname;
	public final User owner;
	public final SolverTaskDescription solverTaskDesc;
	public final Integer meshSpecX;
	public final Integer meshSpecY;
	public final Integer meshSpecZ;
	public final Integer scanCount;
	
	/**
 * SimpleJobStatus constructor comment.
 */
public SimulationMetadata(VCSimulationIdentifier vcSimID, String simname, User owner, SolverTaskDescription arg_solverTaskDesc, Integer meshSpecX, Integer meshSpecY, Integer meshSpecZ, Integer scanCount) {	
	super();
	this.vcSimID = vcSimID;
	this.simname = simname;
	this.owner = owner;
	this.solverTaskDesc = arg_solverTaskDesc;
	this.meshSpecX = meshSpecX;
	this.meshSpecY = meshSpecY;
	this.meshSpecZ = meshSpecZ;
	this.scanCount = scanCount;
}

public String getSolverDescriptionVCML() {
	if (solverTaskDesc == null) {
		return "Error: Null Solver Description";
	}
	return solverTaskDesc.getVCML();
}

public String getMeshSampling(){
	if (this.meshSpecX==null){
		return "no mesh";
	}else if (this.meshSpecY!=null){
		if (this.meshSpecZ!=null){
			return "mesh ("+meshSpecX.intValue()+","+meshSpecY.intValue()+","+meshSpecZ.intValue()+") = "+getMeshSize()+" volume elements";
		}else{
			return "mesh ("+meshSpecX.intValue()+","+meshSpecY.intValue()+") = "+getMeshSize()+" volume elements";
		}
	}else{
		return "mesh ("+meshSpecX.intValue()+") = "+getMeshSize()+" volume elements";
	}
}

public long getMeshSize(){
	if (meshSpecX!=null){
		long size = meshSpecX.intValue();
		if (meshSpecY!=null){
			size *= meshSpecY.intValue();
		}
		if (meshSpecZ!=null){
			size *= meshSpecZ.intValue();
		}
		return size;
	}else{
		return 0;
	}
}
}
