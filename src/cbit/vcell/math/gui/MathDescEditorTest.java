/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math.gui;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.parser.*;
import cbit.vcell.math.*;
import cbit.vcell.server.*;
/**
 * This type was created in VisualAge.
 */
public class MathDescEditorTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		MathDescEditor aMathDescEditor;
		aMathDescEditor = new MathDescEditor();
		frame.setContentPane(aMathDescEditor);
		frame.setSize(aMathDescEditor.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
//		aMathDescEditor.setMakeCanonicalVisibility(true);
//		aMathDescEditor.setFlattenVisibility(true);
//		aMathDescEditor.setApproxSensSolnButtonVisibility(true);
//		aMathDescEditor.setConstructedSolnButtonVisibility(true);
		
		frame.setVisible(true);
		//cbit.vcell.mapping.SimulationContext simContext = cbit.vcell.mapping.SimulationContextTest.getExample(0);
		//cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContext);
		aMathDescEditor.setMathDescription(MathDescriptionTest.getMathDescExample_Stoch());

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
