package cbit.vcell.math.gui;

import cbit.vcell.math.MathDescriptionTest;
import cbit.vcell.mathmodel.MathModel;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class MathDebuggerPanelTest {
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
		MathDebuggerPanel aMathDebuggerPanel;
		aMathDebuggerPanel = new MathDebuggerPanel();
		frame.setContentPane(aMathDebuggerPanel);
		frame.setTitle("Math Descriptions Comparator");
		frame.setSize(aMathDebuggerPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		MathModel mathModel1 = new MathModel(null);
		mathModel1.setName("Math 1");
		mathModel1.setMathDescription(MathDescriptionTest.getOdeExampleWagner());
		
		MathModel mathModel2 = new MathModel(null);
		mathModel2.setName("Math 2");
		mathModel2.setMathDescription(MathDescriptionTest.getOdeExampleWagner());
		
		aMathDebuggerPanel.setMathModel1(mathModel1);
		aMathDebuggerPanel.setMathModel2(mathModel2);
		
		System.out.println("BEGINMATH:\n\n");
		System.out.println(MathDescriptionTest.getOdeExactExample().getVCML() + "ENDMATH\n\nBEGINMATH:" + MathDescriptionTest.getOdeExample().getVCML() + "ENDMATH");
		// System.out.println(MathDescriptionTest.getOdeExactExample().getVCML() + MathDescriptionTest.getOdeExample().getVCML());
		
		frame.setVisible(true);
		//cbit.vcell.mapping.SimulationContext simContext = cbit.vcell.mapping.SimulationContextTest.getExample(0);
		//cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContext);

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}