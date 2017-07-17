package org.vcell;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class VCellModelDialog extends JDialog {
	
	private JOptionPane optionPane;

	public VCellModelDialog(Frame owner, VCellModel[] models) {
		
		super(owner, "Select a model", true);
		
		VCellModelPanel vCellModelPanel = new VCellModelPanel(models);
		optionPane = new JOptionPane(vCellModelPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

		setContentPane(optionPane);
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		optionPane.addPropertyChangeListener(e -> {
			if (isVisible() 
					&& e.getSource() == optionPane 
					&& e.getPropertyName() == JOptionPane.VALUE_PROPERTY) {
				setVisible(false);
			}
		});
	}
	
	public int display() {
		pack();
		setVisible(true);
		return (int) optionPane.getValue();
	}
}
