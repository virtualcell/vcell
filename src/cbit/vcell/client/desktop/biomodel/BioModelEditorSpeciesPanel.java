package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;

@SuppressWarnings("serial")
public class BioModelEditorSpeciesPanel extends BioModelEditorRightSidePanel<SpeciesContext> {	
	private SpeciesEditorPanel speciesEditorPanel = null;
	public BioModelEditorSpeciesPanel() {
		super();
		initialize();
	}

	private void initialize() {	
		speciesEditorPanel = new SpeciesEditorPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		topPanel.add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		topPanel.add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,100,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		newButton.setPreferredSize(deleteButton.getPreferredSize());
		topPanel.add(newButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.insets = new Insets(4,4,4,20);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		topPanel.add(deleteButton, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.BOTH;
		topPanel.add(table.getEnclosingScrollPane(), gbc);
		
		splitPane.setDividerLocation(450);
		splitPane.setResizeWeight(1.0);
		splitPane.setTopComponent(topPanel);
		splitPane.setBottomComponent(speciesEditorPanel);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
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
			speciesContext.setName(speciesContext.getSpecies().getCommonName());
			bioModel.getModel().addSpeciesContext(speciesContext);
			select(speciesContext);
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(BioModelEditorSpeciesPanel.this, ex.getMessage());
		}
	}
	
	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		if (rows == null || rows.length == 0) {
			return;
		}
		String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Are you sure you want to delete selected species?");
		if (confirm.equals(UserMessage.OPTION_CANCEL)) {
			return;
		}
		ArrayList<SpeciesContext> deleteList = new ArrayList<SpeciesContext>();
		for (int r : rows) {
			if (r < tableModel.getDataSize()) {
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

	@Override
	protected BioModelEditorRightSideTableModel<SpeciesContext> createTableModel() {
		return new BioModelEditorSpeciesTableModel(table);
	}
	
	@Override
	protected void bioModelChange() {
		super.bioModelChange();
		speciesEditorPanel.setModel(bioModel.getModel());
	}
	
	@Override
	protected void tableSelectionChanged() {
		super.tableSelectionChanged();
		int[] rows = table.getSelectedRows();
		if (rows != null && rows.length == 1 && rows[0] < tableModel.getDataSize()) {					
			speciesEditorPanel.setSpeciesContext(tableModel.getValueAt(rows[0]));
		} else {
			speciesEditorPanel.setSpeciesContext(null);
		}
	}
}
