package cbit.vcell.client.desktop.biomodel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PathwaySelectionExpander;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;

@SuppressWarnings("serial")
public class BioModelEditorPathwayPanel extends DocumentEditorSubPanel {
	
	private PathwayData pathwayData = null; 
	private JSortTable table;
	private BioModelEditorPathwayTableModel tableModel = null;
	private JButton importButton = null;
	
	// wei's code filter
	private JButton filterNameButton = null;

	private JButton showAllButton = null;
	private JTextField filterText = null;
	
	
	private void searchTable() {
		String searchText = filterText.getText();
		tableModel.setSearchText(searchText);
	}
	private void showAllButtonPressed() {
		if (filterText.getText() == null || filterText.getText().length() == 0) {
			return;
		}
		filterText.setText(null);
		tableModel.setSearchText(null);
	}
	//done
	
	private EventHandler eventHandler = new EventHandler();
	private BioModel bioModel = null;
	
	private class EventHandler implements ActionListener, ListSelectionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == importButton) {
				importPathway();
			}else if (e.getSource() == showAllButton) {
				showAllButtonPressed();
			} else if (e.getSource() == filterText || e.getSource() == filterNameButton){
					searchTable();// filtering 
			}
		}
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == table.getSelectionModel()) {
				importButton.setEnabled(table.getSelectedRowCount() > 0);
			}
		}
	}
	
	public BioModelEditorPathwayPanel() {
		super();
		initialize();
	}
	
	public void importPathway() {
		ArrayList<BioPaxObject> selectedBioPaxObjects = new ArrayList<BioPaxObject>();
		for (int i = 0; i < table.getRowCount(); i ++) {
			EntitySelectionTableRow entitySelectionTableRow = tableModel.getValueAt(i);
			if (entitySelectionTableRow.selected()) { 
				selectedBioPaxObjects.add(entitySelectionTableRow.getBioPaxObject()); 
			}
		}
		PathwaySelectionExpander selectionExpander = new PathwaySelectionExpander();
		selectionExpander.expandSelection(pathwayData.getPathwayModel(), selectedBioPaxObjects);
		PathwayModel selectedPathwayModel = new PathwayModel();
		for (BioPaxObject bpObject : selectedBioPaxObjects){
			selectedPathwayModel.add(bpObject);
		}
		bioModel.getPathwayModel().merge(selectedPathwayModel);
	}

	private void initialize() {
		table = new JSortTable();
		tableModel = new BioModelEditorPathwayTableModel();
		table.setModel(tableModel);
		table.disableUneditableForeground();
		importButton = new JButton("Add Selected");
		importButton.setEnabled(false);
		importButton.addActionListener(eventHandler);
		table.getSelectionModel().addListSelectionListener(eventHandler);
		
		int gridy = 0;
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 6;
		gbc.fill = GridBagConstraints.BOTH;
		add(table.getEnclosingScrollPane(), gbc);
		

		
		// wei's code
		// table filtering
		
		filterText = new JTextField(15);
		filterNameButton = new JButton("Search");
		showAllButton = new JButton("Show All");
		
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(filterText, gbc);
		
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(filterNameButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(showAllButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,20,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		importButton.setPreferredSize(importButton.getPreferredSize());
		add(importButton, gbc);
		
		filterNameButton.addActionListener(eventHandler);
		showAllButton.addActionListener(eventHandler);
		
		//done
		
		
// wei		gridy ++;
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
//		gbc.insets = new Insets(4,0,4,0);
//done		add(importButton, gbc);
		
		
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		PathwayData pathwayData = null;
		if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof PathwayData) {
			pathwayData = (PathwayData) selectedObjects[0];
			setPathwayData(pathwayData);
		}
	}


	private void setPathwayData(PathwayData pathwayData) {
		if (this.pathwayData == pathwayData) {
			return;
		}
		this.pathwayData = pathwayData;
		refreshInterface();
	}


	private void refreshInterface() {
		if (pathwayData == null) {
			return;
		}
		tableModel.setPathwayModel(pathwayData.getPathwayModel());
		
		GuiUtils.flexResizeTableColumns(table);
	}

	public void setBioModel(BioModel bioModel) {
		this.bioModel = bioModel;
	}
}
