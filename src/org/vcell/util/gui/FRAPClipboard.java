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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/*
 * Able to copy a string or an image to clipboard
 * It is made simple and used in Virtual Frap initially.
 * author: Tracy Li
 * version: 1.0
 */
public class FRAPClipboard implements Transferable, ClipboardOwner {
	Object clipObj = null;
	
	public FRAPClipboard(Object obj)
	{
		clipObj = obj;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException 
	{
		//String flavor
		if(flavor == DataFlavor.stringFlavor)
		{
			return clipObj;
		}
		else if (flavor == DataFlavor.imageFlavor)
		{
			//return an image if any
		}
		return null;
	}

	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors = {DataFlavor.imageFlavor, DataFlavor.stringFlavor};
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor == DataFlavor.imageFlavor || flavor == DataFlavor.stringFlavor)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
    public void sendToClip() 
    {
	    //Gets tools from system.
	    Toolkit tools = Toolkit.getDefaultToolkit();
	    Clipboard clip = tools.getSystemClipboard();
	    clip.setContents(this, this);
    }

    public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// no code is needed.
		
	}
}
