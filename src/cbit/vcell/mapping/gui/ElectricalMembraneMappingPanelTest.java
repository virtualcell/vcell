/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import cbit.vcell.model.*;
import cbit.vcell.mapping.*;
/**
 * This type was created in VisualAge.
 */
public class ElectricalMembraneMappingPanelTest {
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
		ElectricalMembraneMappingPanel aElectricalMembraneMappingPanel;
		aElectricalMembraneMappingPanel = new ElectricalMembraneMappingPanel();
		frame.add("Center", aElectricalMembraneMappingPanel);
		frame.setSize(aElectricalMembraneMappingPanel.getSize());
		frame.setVisible(true);
		aElectricalMembraneMappingPanel.setSimulationContext(SimulationContextTest.getExampleElectrical(0));
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}
