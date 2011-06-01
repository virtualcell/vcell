/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;
/**
 * Insert the type's description here.
 * Creation date: (3/19/2004 4:43:18 PM)
 * @author: Jim Schaff
 */
public class JTreeFancy extends javax.swing.JTree {
/**
 * Insert the method's description here.
 * Creation date: (3/19/2004 4:44:34 PM)
 * @return javax.swing.JToolTip
 */
public javax.swing.JToolTip createToolTip() {
	return new MultiLineToolTip();
}
}
