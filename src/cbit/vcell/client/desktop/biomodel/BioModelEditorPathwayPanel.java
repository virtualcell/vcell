package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdom.Document;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.persistence.PathwayReader;
import cbit.vcell.client.desktop.biomodel.EntitySelectionTableRow;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.xml.rdf.XMLRDFWriter;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

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
		HashSet<BioPaxObject> selectedSet = new HashSet<BioPaxObject> ();
		for (BioPaxObject bpObject : selectedBioPaxObjects){
			ArrayList<BioPaxObject> parents = pathwayData.getPathwayModel().getParents(bpObject);
			if(parents != null){
				for(BioPaxObject bp : parents){
					selectedSet.add(bp);
				}
			}
		}
		// add the selectedSet to selectedBioPaxObjects
		for(BioPaxObject bp : selectedSet){
			selectedBioPaxObjects.add((BioPaxObject)bp);
		}
		PathwayModel selectedPathwayModel = new PathwayModel();
		for (BioPaxObject bpObject : selectedBioPaxObjects){
			selectedPathwayModel.add(bpObject);
		}
		bioModel.getPathwayModel().merge(selectedPathwayModel);
	}

	private void initialize() {
		table = new JSortTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
		
		int rowCount = table.getRowCount();
		int colCount = table.getColumnCount();
		final int[] maxColumnWidths = new int[colCount];
		java.util.Arrays.fill(maxColumnWidths,0);
		for(int iCol = 0; iCol < colCount; iCol++) {
			TableColumn column = table.getColumnModel().getColumn(iCol);
			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if (headerRenderer == null) {
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			}
			if (headerRenderer != null) {
				Component comp = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, iCol); 
				maxColumnWidths[iCol] = Math.max(maxColumnWidths[iCol],comp.getPreferredSize().width);
			}
			for(int iRow = 0; iRow < rowCount; iRow++) {
				TableCellRenderer cellRenderer = table.getCellRenderer(iRow,iCol);
				if (cellRenderer == null) {
					continue;
				}
				Component comp = cellRenderer.getTableCellRendererComponent(table, table.getValueAt(iRow, iCol), false, false, iRow, iCol);
				maxColumnWidths[iCol] = Math.max(maxColumnWidths[iCol], comp.getPreferredSize().width);
			}
		}
		for(int iCol = 0; iCol < colCount; iCol++) {
			table.getColumnModel().getColumn(iCol).setPreferredWidth(maxColumnWidths[iCol]);
		}
	}

	public void setBioModel(BioModel bioModel) {
		this.bioModel = bioModel;
	}
}
