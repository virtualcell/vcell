/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gui;
/**
 * Insert the type's description here.
 * Creation date: (8/6/2001 11:14:50 PM)
 * @author: Jim Schaff
 */
import javax.swing.JFrame;

import org.vcell.util.document.KeyValue;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class ExportMonitorPanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		ExportMonitorPanel aExportMonitorPanel;
		aExportMonitorPanel = new ExportMonitorPanel();
		frame.setContentPane(aExportMonitorPanel);
		frame.setSize(aExportMonitorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		VCSimulationDataIdentifier vcSimDataId = new VCSimulationDataIdentifier(new VCSimulationIdentifier(new KeyValue("234"), null),1);
		aExportMonitorPanel.addExportEvent(new ExportEvent(
			aExportMonitorPanel, 123456789L, null,
			vcSimDataId.getID(), vcSimDataId.getSimulationKey(), ExportEvent.EXPORT_PROGRESS,
			"CSV", "", new Double(0.47),
			null, null),
		"bogus [application: model]");
		aExportMonitorPanel.addExportEvent(new ExportEvent(
			aExportMonitorPanel, 987654321L, null,
			vcSimDataId.getID(), vcSimDataId.getSimulationKey(), ExportEvent.EXPORT_COMPLETE,
			"GIF", "http://nrcam.uchc.edu/export/987654321.zip", new Double(1),
			null, null),
		"simulation [application: model]");
		frame.pack();
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
