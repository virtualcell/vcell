/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;

/**
 * This type was created in VisualAge.
 */
import cbit.image.*;

public class ImageContainerPanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		ImageContainerPanel aImageContainerPanel;
		aImageContainerPanel = new ImageContainerPanel();
		frame.add("Center", aImageContainerPanel);
		frame.setSize(aImageContainerPanel.getSize());
		frame.setVisible(true);
		DisplayImage displayImage = DisplayImage.getExample();
		cbit.image.gui.DisplayImageContainer displayImageContainer = new cbit.image.gui.DisplayImageContainer(displayImage);
		aImageContainerPanel.setImageContainer(displayImageContainer);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}
