/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.util;

/*   Sizer  --- by Oliver Ruebenacker, UCHC --- September 2008
 *   Simple class for determining the size of frames and dialogs
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Sizer {

	public static Dimension desiredSize() { return new Dimension(1000, 700); }
	public static Dimension initialSize() { return desiredSize(); }
	// public static Dimension initialSize() { return adjustToFitScreen(desiredSize()); }
	public static Dimension screenSize() { return Toolkit.getDefaultToolkit().getScreenSize(); }
		
	public static Dimension adjustToFitScreen(Dimension size) {
		Dimension screenSize = screenSize();
		Dimension sizeNew = new Dimension(size.width < screenSize.width ? size.width : screenSize.width,
				size.height < screenSize.height ? size.height : screenSize.height);
		return sizeNew;
	}

	public static void shrinkToFitScreen(Component comp) {
		Dimension size = comp.getSize();
		Dimension screenSize = screenSize();
		Dimension sizeNew = new Dimension(size.width < screenSize.width ? size.width : screenSize.width,
				size.height < screenSize.height ? size.height : screenSize.height);
		if(!sizeNew.equals(size)) { comp.setSize(sizeNew); }
	}

	
}
