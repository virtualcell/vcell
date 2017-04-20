/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;

import java.io.Serializable;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;

@SuppressWarnings("serial")
public class SundialsPdeSolverOptions implements Matchable, Serializable {
	private final static int DEFAULT_MAX_ORDER_ADVECTION = 2;
	
	private int maxOrderAdvection = DEFAULT_MAX_ORDER_ADVECTION;
	
	public SundialsPdeSolverOptions() {
		
	}
	public SundialsPdeSolverOptions(int order) {
		maxOrderAdvection = order;
	}
	public SundialsPdeSolverOptions(SundialsPdeSolverOptions sundialsPdeSolverOptions) {
		maxOrderAdvection = sundialsPdeSolverOptions.maxOrderAdvection;
	}
	public SundialsPdeSolverOptions(CommentStringTokenizer tokens) throws DataAccessException {
		this();
		readVCML(tokens);
	}
	
	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof SundialsPdeSolverOptions)) {
			return false;
		}
		SundialsPdeSolverOptions sundialsPdeSolverOptions = (SundialsPdeSolverOptions)obj;
		return maxOrderAdvection == sundialsPdeSolverOptions.maxOrderAdvection;
	}
	
	public String getVCML() {		
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t" + VCML.SundialsSolverOptions + " " + VCML.BeginBlock + "\n");
		buffer.append("\t\t" + VCML.SundialsSolverOptions_maxOrderAdvection + " " + maxOrderAdvection + "\n");
		buffer.append("\t" + VCML.EndBlock + "\n");
		
		return buffer.toString();
	}
	
	private void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.SundialsSolverOptions)) {
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
				throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
			}
		}
		
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.SundialsSolverOptions_maxOrderAdvection)) {
				token = tokens.nextToken();
				maxOrderAdvection = Integer.parseInt(token);
			} else if (token.equalsIgnoreCase("maxOrder")) { // old way
				token = tokens.nextToken();
			} else {
				throw new DataAccessException("unexpected identifier " + token);
			}
		}
	}
	public final int getMaxOrderAdvection() {
		return maxOrderAdvection;
	}
}
