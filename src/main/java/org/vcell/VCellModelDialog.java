package org.vcell;

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class VCellModelDialog extends JDialog {
	
	private JOptionPane optionPane;
	private VCellModelPanel vCellModelPanel;

	public VCellModelDialog(Frame owner, VCellModelService vCellModelService) {
		
		super(owner, "Select a model", true);
		
		vCellModelPanel = new VCellModelPanel(vCellModelService);
		optionPane = new JOptionPane(vCellModelPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

		setContentPane(optionPane);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
		vCellModelPanel.getModels();
		setVisible(true);
		Object value = optionPane.getValue();
		if (Integer.class.isInstance(value)) {
			return (int) optionPane.getValue();
		}
		return JOptionPane.CANCEL_OPTION; // If something goes wrong, dispose and do nothing
	}
}
