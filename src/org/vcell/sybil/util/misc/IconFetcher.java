/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.misc;

import javax.swing.ImageIcon;

import org.vcell.sybil.util.exception.CatchUtil;

public class IconFetcher {

	public static final String imageBasePath = "sybil/images/";
	
	public static ImageIcon fetch(String name) {
		try { return new ImageIcon(IconFetcher.class.getClassLoader()
				.getResource(imageBasePath + name)); }
		catch (Exception e) { CatchUtil.handle(e, CatchUtil.JustPrint); return null; }
	}	
}
