package cbit.vcell.client.desktop.biomodel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JLabel;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.mapping.BioEvent;

@SuppressWarnings("serial")
public class EventsDisplayPanel extends BioModelEditorApplicationRightSidePanel<BioEvent> {	
	public EventsDisplayPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		setName("EventsPanel");
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
	
	@Override
	protected void newButtonPressed() {
		if (simulationContext == null) {
			return;
		}
		String eventName = simulationContext.getFreeEventName();
		try {
			BioEvent bioEvent = new BioEvent(eventName, simulationContext);
			simulationContext.addBioEvent(bioEvent);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, "Error adding Event : " + e.getMessage());
		}		
	}

	@Override
	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		ArrayList<BioEvent> deleteList = new ArrayList<BioEvent>();
		for (int r : rows) {
			if (r < tableModel.getDataSize()) {
				deleteList.add(tableModel.getValueAt(r));
			}
		}
		try {
			for (BioEvent bioEvent : deleteList) {
				simulationContext.removeBioEvent(bioEvent);
			}
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}		
	}

	@Override
	protected BioModelEditorApplicationRightSideTableModel<BioEvent> createTableModel() {
		return new EventsSummaryTableModel(table);
	}
	
}
