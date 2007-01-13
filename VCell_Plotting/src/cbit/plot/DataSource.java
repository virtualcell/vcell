/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.plot;
/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:23:20 PM)
 * @author: Jim Schaff
 */
public interface DataSource {

	public String[] getColumnNames();

	public double[] getColumnData(int index);
	
	public java.lang.String getName();

	public int getColumnCount();
	
	public int findColumn(String columnName);
	
	public int getRenderHints(int index);
	
}