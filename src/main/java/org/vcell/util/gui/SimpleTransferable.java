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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.vcell.util.BeanUtils;

public class SimpleTransferable extends java.awt.datatransfer.StringSelection {
	private Object xferObject = null;

	public static final DataFlavor OBJECT_FLAVOR =
		new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+"; class="+java.lang.Object.class.getName(),"Java Object");

/**
 * Creates a transferable object capable of transferring the
 * specified string in plain text format.
 */
protected SimpleTransferable(Object obj) {
	super(obj.toString());
	xferObject = obj;
}


/**
 * Insert the method's description here.
 * Creation date: (9/9/2004 1:23:34 PM)
 * @return java.lang.Class
 */
public Class<?> getDataObjectClass() {
	return xferObject.getClass();
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2003 8:43:19 AM)
 * @param obj java.lang.Object
 * @param flavor java.awt.datatransfer.DataFlavor
 */
public static Object getFromClipboard(DataFlavor flavor) {
	//Convenience method to get flavor off of clipboard
	//
	Object results = null;
	Clipboard clipb = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
	java.awt.datatransfer.Transferable clipbContents = clipb.getContents(null);
	if((clipbContents != null) && clipbContents.isDataFlavorSupported(flavor)){
		try{
			results = clipbContents.getTransferData(flavor);
		}catch(java.io.IOException ioe){
			//VCellTransferables don't do this because they are references to objects
			throw new RuntimeException("VCellTransferable.getFromClipboard: "+ioe.getMessage());
		}catch(UnsupportedFlavorException ufe){
			//This won't happen because we checked
			throw new RuntimeException("VCellTransferable.getFromClipboard: "+ufe.getMessage());
		}
	}
	return results;
}


/**
 * Returns an object which represents the data to be transferred.  The class 
 * of the object returned is defined by the representation class of the flavor.
 * 
 * @param flavor the requested flavor for the data
 * @see DataFlavor#getRepresentationClass
 * @exception IOException                if the data is no longer available
 *              in the requested flavor.
 * @exception UnsupportedFlavorException if the requested data flavor is
 *              not supported.
 */
public Object getTransferData(DataFlavor flavor) throws java.io.IOException, UnsupportedFlavorException {
	
	if(isDataFlavorSupported(flavor)){
		//
		if (super.isDataFlavorSupported(flavor)){
			return super.getTransferData(flavor);
		}

		if (isSupportedObjectFlavor(flavor)){
			return xferObject;
		}

		//
		//
		throw new RuntimeException("Mismatch between available flavors and flavors claimed to be supported");
		//
	}
	//
	throw new UnsupportedFlavorException(flavor);
}


	/**
	 * Returns an array of DataFlavor objects indicating the flavors the data 
	 * can be provided in.  The array should be ordered according to preference
	 * for providing the data (from most richly descriptive to least descriptive).
	 * @return an array of data flavors in which this data can be transferred
	 */
public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
	DataFlavor flavors[] = super.getTransferDataFlavors();
	
	// add object flavor to those inherited from StringSelection (ok for all
	flavors = (DataFlavor[]) BeanUtils.addElement(flavors,OBJECT_FLAVOR);

	return flavors;
}


/**
 * Returns whether or not the specified data flavor is supported for
 * this object.
 * @param flavor the requested flavor for the data
 * @return boolean indicating wjether or not the data flavor is supported
 */
public boolean isDataFlavorSupported(DataFlavor flavor) {
	DataFlavor flavors[] = getTransferDataFlavors();
	
	for (int i = 0; i < flavors.length; i++){
		if (flavors[i].equals(flavor)){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (9/9/2004 1:17:52 PM)
 * @return boolean
 * @param dataFlavor java.awt.datatransfer.DataFlavor
 */
protected boolean isSupportedObjectFlavor(DataFlavor dataFlavor) {
	
	if(dataFlavor.equals(OBJECT_FLAVOR)){
		return true;		
	}

	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2003 8:43:19 AM)
 * @param obj java.lang.Object
 * @param flavor java.awt.datatransfer.DataFlavor
 */
public static void sendToClipboard(Object obj) {
	if(obj == null){
		return;
	}
	SimpleTransferable simpleTransferable = new SimpleTransferable(obj);
	Clipboard clipb = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
	clipb.setContents(simpleTransferable,simpleTransferable);
}
}
