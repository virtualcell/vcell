package org.vcell;

import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.imagej.Dataset;

public class DatasetSelectionPanel extends JPanel {
	
	private HashMap<String, JComboBox<Dataset>> datasetMap;
	
	public DatasetSelectionPanel() {
		setLayout(new GridLayout(0, 2, 10, 10));
		datasetMap = new HashMap<String, JComboBox<Dataset>>();
	}
	
	public void addComboBox(Dataset[] datasets, String description) {
		JComboBox<Dataset> comboBox = new JComboBox<>(datasets);
		add(new JLabel(description));
		add(comboBox);
		datasetMap.put(description, comboBox);
	}
	
	public Dataset getSelectedDatasetForDescription(String description) {
		JComboBox<Dataset> comboBox = datasetMap.get(description);
		return (Dataset) comboBox.getSelectedItem();
	}
}
