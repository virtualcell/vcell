/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde;

import org.vcell.util.Compare;
import org.vcell.util.Coordinate;
import org.vcell.util.Matchable;

public abstract class SpatialBasis implements Matchable {

	private String name;
	private int basisIndex;
	private int controlPointMeshIndex;
	private Coordinate controlPointCoord;
	private boolean bWithinBleach;

	public SpatialBasis() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBasisIndex() {
		return basisIndex;
	}

	public void setBasisIndex(int basisIndex) {
		this.basisIndex = basisIndex;
	}

	public int getControlPointMeshIndex() {
		return controlPointMeshIndex;
	}

	public void setControlPointIndex(int controlPointMeshIndex) {
		this.controlPointMeshIndex = controlPointMeshIndex;
	}

	public Coordinate getControlPointCoord() {
		return controlPointCoord;
	}

	public void setControlPointCoord(Coordinate controlPointCoord) {
		this.controlPointCoord = controlPointCoord;
	}

	public boolean isWithinBleach() {
		return bWithinBleach;
	}

	public void setWithinBleach(boolean withinBleach) {
		bWithinBleach = withinBleach;
	}

	protected boolean compareEqual0(Matchable obj) {
		if (obj instanceof SpatialBasis){
			SpatialBasis sb = (SpatialBasis)obj;
			if (!Compare.isEqualOrNull(name, sb.name)){
				return false;
			}
			if (basisIndex!=sb.basisIndex){
				return false;
			}
			if (controlPointMeshIndex!=sb.controlPointMeshIndex){
				return false;
			}
			if (!Compare.isEqualOrNull(controlPointCoord,sb.controlPointCoord)){
				return false;
			}
			if (bWithinBleach!=sb.bWithinBleach){
				return false;
			}
			return true;
		}
		return false;
	}
	
	public String getReport(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("name="+name+", bi="+basisIndex+", cp="+controlPointMeshIndex+", coord="+controlPointCoord+", withinBleach="+bWithinBleach);
		return buffer.toString();
	}

}