package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.mapping.BioEvent;

@SuppressWarnings("serial")
public class EventsDisplayPanel extends BioModelEditorApplicationRightSidePanel<BioEvent> {
	private JSplitPane outerSplitPane = null;
	private EventPanel eventPanel = null;

	public EventsDisplayPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		setName("EventsPanel");
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
		addButton.setPreferredSize(deleteButton.getPreferredSize());
		topPanel.add(addButton, gbc);
		
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
		
		outerSplitPane = new JSplitPane();
		outerSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		outerSplitPane.setDividerLocation(300);			
		outerSplitPane.setTopComponent(topPanel);
		outerSplitPane.setBottomComponent(getEventPanel());
		setLayout(new BorderLayout());			
		add(outerSplitPane, BorderLayout.CENTER);		
	}
	
	private EventPanel getEventPanel() {
		if (eventPanel == null) {
			eventPanel = new EventPanel();
			eventPanel.setName("EventPanel");
		}
		
		return eventPanel;
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

	@Override
	protected void tableSelectionChanged() {
		super.tableSelectionChanged();
		int[] rows = table.getSelectedRows();
		if (rows != null && rows.length == 1 && rows[0] < tableModel.getDataSize()) {
			getEventPanel().setBioEvent(tableModel.getValueAt(rows[0]));
		} else {
			getEventPanel().setBioEvent(null);
		}
	}
	
}
