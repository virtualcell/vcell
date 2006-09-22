package cbit.vcell.mapping.gui;

/**
 * Insert the type's description here.
 * Creation date: (10/26/2004 10:59:13 PM)
 * @author: Jim Schaff
 */
public class ElectricalStimulusPanelTest {
/**
 * Insert the method's description here.
 * Creation date: (10/26/2004 10:59:37 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ElectricalStimulusPanel aElectricalStimulusPanel;
		aElectricalStimulusPanel = new ElectricalStimulusPanel();
		frame.setContentPane(aElectricalStimulusPanel);
		frame.setSize(aElectricalStimulusPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		
		aElectricalStimulusPanel.setSimulationContext(cbit.vcell.modelapp.SimulationContextTest.getExampleElectrical(1));	
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
