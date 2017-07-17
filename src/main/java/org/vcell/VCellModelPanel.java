package org.vcell;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


@SuppressWarnings("serial")
public class VCellModelPanel extends JPanel {
	
	private JList<VCellModel> list;
	
	public VCellModelPanel(VCellModel[] models) {
		initializeComponents(models);
		setPreferredSize(new Dimension(500, 300));
	}
	
	private void initializeComponents(VCellModel[] models) {
		setLayout(new BorderLayout());
		
		DefaultListModel<VCellModel> listModel = new DefaultListModel<>();
		for (VCellModel vCellModel : models) {
			listModel.addElement(vCellModel);
		}
		
		list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new DefaultListCellRenderer());
		list.setFixedCellWidth(200);
		
		JScrollPane scrollPane = new JScrollPane(list);
		add(scrollPane, BorderLayout.LINE_START);
		
		SBMLVisualizationPanel sbmlVisualizationPanel = new SBMLVisualizationPanel();
		add(sbmlVisualizationPanel, BorderLayout.CENTER);
		list.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				VCellModel selectedModel = list.getSelectedValue();
				sbmlVisualizationPanel.visualize(selectedModel.getSbmlDocument());
			}
		});
		
	}
	
	public VCellModel getSelectedModel() {
		return list.getSelectedValue();
	}
}
