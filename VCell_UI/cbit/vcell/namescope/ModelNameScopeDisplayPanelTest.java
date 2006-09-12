/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.namescope;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Model;
import cbit.vcell.biomodel.BioModelTest;
/**
 * Insert the type's description here.
 * Creation date: (4/12/2004 5:41:08 PM)
 * @author: Anuradha Lakshminarayana
 */
public class ModelNameScopeDisplayPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		
		Model model = cbit.vcell.model.ModelTest.getExample2();
		
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ModelNameScopeDisplayPanel aNameScopeDisplayPanel;
		aNameScopeDisplayPanel = new ModelNameScopeDisplayPanel();
		aNameScopeDisplayPanel.setModel(model);
		
		
		frame.setContentPane(aNameScopeDisplayPanel);
		frame.setSize(aNameScopeDisplayPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		// javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	} 
}
}
