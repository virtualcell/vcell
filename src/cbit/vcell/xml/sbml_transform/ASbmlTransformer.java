/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * 
 */
package cbit.vcell.xml.sbml_transform;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mlevin
 *
 */
abstract class ASbmlTransformer implements ISbmlTransformer {
	private List<String > commandInfo = new ArrayList<String>();
	
	public int countParameters() {return 0;}
	
	protected void storeTransformationInfo(String[] parameters, String comment) {
		int n = countParameters();
		if( parameters.length > n ) {
			String msg = "" + n + " parameters expected; unknown parameter: \"" + parameters[n] + "\"";
			throw new SbmlTransformException(msg);
		}
		if( parameters.length < countParameters() ) {
			String msg = "" + parameters.length + " parameters found, " + n + " expected;";
			throw new SbmlTransformException(msg);
		}
		
		StringBuffer buff = new StringBuffer();
		buff.append( getName() );
		if( parameters.length > 0 ) {
			buff.append("(");
			buff.append(parameters[0]);
			for( int i = 1; i < parameters.length; ++ i ) {
				buff.append(",");
				buff.append(parameters[i]);
			}
			buff.append(")");
		}
		
		if( null != comment && comment.length() > 0 ) 
			buff.append(" ").append(comment);
		
		commandInfo.add(buff.toString());
	}
	
	/**
	 * @param i
	 * @return transformation info string; can be used for error messages
	 */
	public String getCommandInfo(int i) {
		return commandInfo.get(i);
	}
	
	protected String getErrorMessage(int i) {
		return "error applying transformation \"" + getCommandInfo(i) + "\"";
	}
	
}
