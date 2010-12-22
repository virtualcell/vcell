package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.vcell.sybil.gui.bpimport.EntitySelectionTableRow;
import org.vcell.sybil.models.io.selection.ModelSelector;
import org.vcell.sybil.models.io.selection.ModelSelectorSimple;
import org.vcell.sybil.rdf.smelt.BioPAX2Smelter;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class BioModelEditorPathwayPanel extends BioModelEditorSubPanel {
	
	private PathwayData pathwayData = null; 
	private JSortTable table;
	private BioModelEditorPathwayTableModel tableModel = null;
	private JButton importButton = null;
	private EventHandler eventHandler = new EventHandler();
	private BioModel bioModel = null;
	
	private class EventHandler implements ActionListener, ListSelectionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == importButton) {
				importPathway();
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
		Set<Resource> selectedResources = new HashSet<Resource>();
		for (int i = 0; i < table.getRowCount(); i ++) {
			EntitySelectionTableRow entitySelectionTableRow = tableModel.getValueAt(i);
			if (entitySelectionTableRow.selected()) { 
				selectedResources.add(entitySelectionTableRow.thing().resource()); 
			}
		}
		Model model = pathwayData.getModel();
		model = new BioPAX2Smelter().smelt(model);
		ModelSelector selector = new ModelSelectorSimple();
		Model modelSelection = selector.createSelection(model, selectedResources);
		bioModel.getVCMetaData().addPathwayModel(bioModel, modelSelection);
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
		gbc.fill = GridBagConstraints.BOTH;
		add(table.getEnclosingScrollPane(), gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,0,4,0);
		add(importButton, gbc);
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
		tableModel.setSBBox(pathwayData.getSBBox());
		
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
