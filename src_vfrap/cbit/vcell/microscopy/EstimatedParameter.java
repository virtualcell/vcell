/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.units.VCUnitDefinition;

public class EstimatedParameter {
	
	private String paramName = null;
	private String paramDescription = null;
	private Double paramValue = null;
	private Expression paramExpression = null;
	private VCUnitDefinition unitDefinition = null;

	public EstimatedParameter(String argName, String description, Expression expression, Double value, VCUnitDefinition uDefinition) {
		if (argName == null){
			throw new IllegalArgumentException("parameter name is null");
		}
		if (argName.length()<1){
			throw new IllegalArgumentException("parameter name is zero length");
		}
		this.paramName = argName;
		this.paramDescription = description;
		this.paramExpression = expression;
		this.paramValue = value;
		this.unitDefinition = uDefinition;
	}

	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof EstimatedParameter)){
			return false;
		}
		EstimatedParameter ep = (EstimatedParameter)obj;
		if (!Compare.isEqual(ep.paramName, this.paramName)){
			return false;
		}
		if(!Compare.isEqualOrNull(ep.paramDescription, this.paramDescription))
		{
			return false;
		}
		if (!Compare.isEqualOrNull(ep.paramExpression, this.paramExpression)){
			return false;
		}
		if (!Compare.isEqualOrNull(this.paramValue, this.paramValue))
		{
			return false;
		}
		if(!Compare.isEqualOrNull(ep.unitDefinition, this.unitDefinition))
		{
			return false;
		}
		return true;
	}
	
	public Double getValue() {
		return this.paramValue;
	}      

	public Expression getExpression() {
		return this.paramExpression;
	}

	public String getName(){ 
		return this.paramName; 
	}   

	public String getDescrition(){ 
		return this.paramDescription; 
	}
	
	public VCUnitDefinition getUnitDefinition() {
		return unitDefinition;
	}
	
	public void setName(String paramName) {
		this.paramName = paramName;
	}

	public void setDescription(String paramDescription) {
		this.paramDescription = paramDescription;
	}

	public void setValue(Double paramValue) {
		this.paramValue = paramValue;
	}

	public void setExpression(Expression paramExpression) {
		this.paramExpression = paramExpression;
	}

	public void setUnitDefinition(VCUnitDefinition unitDefinition) {
		this.unitDefinition = unitDefinition;
	}

}
