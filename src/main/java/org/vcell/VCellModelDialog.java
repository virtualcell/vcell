package org.vcell;

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class VCellModelDialog extends JDialog {
	
	private JOptionPane optionPane;

	public VCellModelDialog(Frame owner, VCellModelService vCellModelService) {
		
		super(owner, "Select a model", true);
		
		VCellModelPanel vCellModelPanel = new VCellModelPanel(vCellModelService);
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
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				vCellModelPanel.displayImageOfSelectedModel();
			}
		});
	}
	
	public int display() {
		pack();
		setVisible(true);
		return (int) optionPane.getValue();
	}
}
