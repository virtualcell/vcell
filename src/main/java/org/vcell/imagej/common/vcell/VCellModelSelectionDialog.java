package org.vcell.imagej.common.vcell;

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class VCellModelSelectionDialog extends JDialog {
	
	private JOptionPane optionPane;
	private VCellModelSelectionPanel vCellModelPanel;

	public VCellModelSelectionDialog(Frame owner, VCellModelService vCellModelService) {
		
		super(owner, "Select a model", true);
		
		vCellModelPanel = new VCellModelSelectionPanel(vCellModelService);
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
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				vCellModelPanel.displayImageOfSelectedModel();
			}
		});
		
	}
	
	public void setModels(List<VCellModel> models) {
		vCellModelPanel.setModels(models);
	}
	
	public int display() {
		pack();
		setVisible(true);
		Object value = optionPane.getValue();
		if (Integer.class.isInstance(value)) {
			return (int) optionPane.getValue();
		}
		return JOptionPane.CANCEL_OPTION; // If something goes wrong, dispose and do nothing
	}
	
	public VCellModel getSelectedModel() {
		return vCellModelPanel.getSelectedModel();
	}
}
