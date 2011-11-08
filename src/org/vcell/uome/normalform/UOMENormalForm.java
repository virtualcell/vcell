/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome.normalform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sbpax.util.StringUtil;
import org.vcell.sybil.util.lists.ListUtil;
import org.vcell.uome.core.UOMEExpression;
import org.vcell.uome.core.UOMEUnit;

public class UOMENormalForm implements UOMEExpression {

	protected final double factor, offset;
	protected final List<UOMEUnit> units = new ArrayList<UOMEUnit>();
	protected final Map<UOMEUnit, Double> exponentMap = new HashMap<UOMEUnit, Double>();
	
	public UOMENormalForm(List<UOMEUnit> units, Map<UOMEUnit, Double> exponentMap, double factor, double offset) {
		this.units.addAll(units);
		this.exponentMap.putAll(exponentMap);
		this.factor = factor;
		this.offset = offset;
	}
	
	public UOMENormalForm(List<UOMEUnit> units, Map<UOMEUnit, Double> exponentMap, double factor) { 
		this(units, exponentMap, factor, 0.0);
	}
	
	public UOMENormalForm(List<UOMEUnit> units, Map<UOMEUnit, Double> exponentMap) { this(units, exponentMap, 1.0); }
	
	public List<UOMEUnit> getUnits() { return units; }
	public Map<UOMEUnit, Double> getExponentMap() { return exponentMap; }
	public double getFactor() { return factor; }
	public double getOffset() { return offset; }

	public String buildString(List<String> strings) {
		String string = UOMENumberUtil.toString(factor) + " ";
		List<String> unitPowers = new ArrayList<String>();
		List<String> stringsFilled = ListUtil.fillGaps(strings, "?", units.size());
		for(int i = 0; i < units.size(); ++ i) {			
			Double exponent = exponentMap.get(units.get(i));
			if(exponent != null && !UOMENumberUtil.almostEqual(exponent.doubleValue(), 0)) {
				if(exponent > 0.0) {
					if(UOMENumberUtil.almostEqual(exponent.doubleValue(), 1.0)) {
						unitPowers.add(stringsFilled.get(i));						
					} else {
						unitPowers.add(stringsFilled.get(i) + "^" + UOMENumberUtil.toString(exponent.doubleValue()));						
					}
				} else {
					unitPowers.add(stringsFilled.get(i) + "^(" + UOMENumberUtil.toString(exponent.doubleValue()) + ")");
				}
			} 
		}
		string = string + StringUtil.concat(unitPowers, "*");
		if(!UOMENumberUtil.almostEqual(offset, 0.0)) { 
			if(offset > 0.0) {
				string = string + " + " + UOMENumberUtil.toString(offset); 				
			} else {
				string = string + " - " + UOMENumberUtil.toString(-offset);
			}
		}
		return string;
	}
	
	public int hashCode() { return exponentMap.hashCode(); }

}
