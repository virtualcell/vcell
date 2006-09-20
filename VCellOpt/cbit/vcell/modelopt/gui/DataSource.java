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
package cbit.vcell.modelopt.gui;
/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:23:20 PM)
 * @author: Jim Schaff
 */
public class DataSource {
	private String name = null;
	private Object source = null;

/**
 * DataSource constructor comment.
 */
public DataSource(cbit.vcell.opt.ReferenceData argSource, String argName) {
	super();
	this.source = argSource;
	this.name = argName;
}


/**
 * DataSource constructor comment.
 */
public DataSource(cbit.vcell.simdata.ODESolverResultSet argSource, String argName) {
	super();
	this.source = argSource;
	this.name = argName;
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:25:28 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:54:22 PM)
 * @return int
 * @param columnName java.lang.String
 */
public int getRenderHints(String columnName) {
	if (getSource() instanceof cbit.vcell.simdata.ODESolverResultSet){
		return (cbit.plot.Plot2D.RENDERHINT_DRAWLINE);
	}else if (getSource() instanceof cbit.vcell.opt.ReferenceData){
		return (cbit.plot.Plot2D.RENDERHINT_DRAWLINE);
	}else{
		return (cbit.plot.Plot2D.RENDERHINT_DRAWLINE | cbit.plot.Plot2D.RENDERHINT_DRAWPOINT);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:25:28 PM)
 * @return java.lang.Object
 */
public java.lang.Object getSource() {
	return source;
}
}