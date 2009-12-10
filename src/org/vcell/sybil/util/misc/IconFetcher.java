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
