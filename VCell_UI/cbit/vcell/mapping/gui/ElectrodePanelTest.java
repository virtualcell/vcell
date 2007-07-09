package cbit.vcell.mapping.gui;

import org.vcell.util.Coordinate;

import cbit.vcell.model.*;
import cbit.vcell.geometry.*;
import edu.uchc.vcell.expression.internal.*;
/**
 * Insert the type's description here.
 * Creation date: (4/10/2002 2:33:29 PM)
 * @author: Anuradha Lakshminarayana
 */
public class ElectrodePanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ElectrodePanel aElectrodePanel = new ElectrodePanel();
		frame.setContentPane(aElectrodePanel);
		frame.setSize(aElectrodePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		cbit.vcell.model.Model model = ModelTest.getExample();
		aElectrodePanel.setModel(model);
		cbit.vcell.modelapp.Electrode newelectrode = new cbit.vcell.modelapp.Electrode((Feature)model.getStructure("Cytosol"), new Coordinate(10.0, 20.0,30.0));
		aElectrodePanel.setElectrode(newelectrode);
		cbit.vcell.geometry.Geometry geom = GeometryTest.getExample(2);
		aElectrodePanel.setGeometry(geom);
		
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
