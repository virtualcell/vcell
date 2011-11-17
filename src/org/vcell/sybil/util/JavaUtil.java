/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util;

import javax.swing.JFileChooser;
import javax.swing.plaf.basic.BasicFileChooserUI;

public class JavaUtil {
	
	public static boolean equals(Object o1, Object o2) { return o1 == null ? o2 == null : o1.equals(o2); }
	public static int hashCode(Object o) { return o == null ? 0 : o.hashCode(); }
	
	// The work-around recommended by Sun for the problem that getSelectedFile()
	// would otherwise only be updated from the tex field if OK button or return is pressed.
	public static void updateJFileChooser(JFileChooser fileChooser) {
		if (fileChooser.getUI() instanceof BasicFileChooserUI) {
	        BasicFileChooserUI ui = (BasicFileChooserUI) fileChooser.getUI();
	        ui.getApproveSelectionAction().actionPerformed(null);
	    }
	}

}
