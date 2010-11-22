package cbit.vcell.client.desktop.biomodel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;

@SuppressWarnings("serial")
public class BioModelEditorSpeciesPanel extends BioModelEditorRightSidePanel<SpeciesContext> {	
	
	public BioModelEditorSpeciesPanel() {
		super();
		initialize();
	}

	private void initialize() {	
		tableModel = new BioModelEditorSpeciesTableModel(table);
		table.setModel(tableModel);			

		setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,100,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		addButton.setPreferredSize(deleteButton.getPreferredSize());
		add(addButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.insets = new Insets(4,4,4,20);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(deleteButton, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.BOTH;
		add(table.getEnclosingScrollPane(), gbc);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			JFrame frame = new javax.swing.JFrame();
			BioModelEditorSpeciesPanel panel = new BioModelEditorSpeciesPanel();
			frame.add(panel);
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.pack();
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}

	protected void newButtonPressed() {
		SpeciesContext speciesContext = new SpeciesContext(new Species(bioModel.getModel().getFreeSpeciesName(), null), bioModel.getModel().getStructures()[0]);
		try {
			bioModel.getModel().addSpecies(speciesContext.getSpecies());
			bioModel.getModel().addSpeciesContext(speciesContext);
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(BioModelEditorSpeciesPanel.this, ex.getMessage());
		}
	}
	
	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		ArrayList<SpeciesContext> deleteList = new ArrayList<SpeciesContext>();
		for (int r : rows) {
			if (r < bioModel.getModel().getNumSpeciesContexts()) {
				deleteList.add(tableModel.getValueAt(r));
			}
		}
		try {
			for (SpeciesContext sc : deleteList) {
				bioModel.getModel().removeSpeciesContext(sc);
			}
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(BioModelEditorSpeciesPanel.this, ex.getMessage());
		}
	}
}
