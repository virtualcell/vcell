package org.vcell.imagej.common.vcell;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.sbml.jsbml.Species;

public class SimulationSpecificationPanel extends JPanel {
	
	private static final long serialVersionUID = -7025485553341057449L;
	
	private HashMap<Species, JCheckBox> speciesCheckBoxMap;
	private JRadioButton individualButton;
	
	public SimulationSpecificationPanel(VCellModel model) {
		
		speciesCheckBoxMap = new HashMap<>();
		
		setLayout(new BorderLayout());
		
		JPanel speciesPanel = new JPanel(new GridLayout(0, 1));
		for (Species species : model.getSpecies()) {
			JCheckBox checkBox = new JCheckBox(species.getId());
			speciesPanel.add(checkBox);
			speciesCheckBoxMap.put(species, checkBox);
		}
		
		individualButton = new JRadioButton("Create individual datasets for each selected species");
		individualButton.setSelected(true);
		JRadioButton sumButton = new JRadioButton("Create one results dataset as the sum of selected species");
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(individualButton);
		buttonGroup.add(sumButton);
		
		JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		buttonPanel.add(individualButton);
		buttonPanel.add(sumButton);
		
		add(new JScrollPane(speciesPanel), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.PAGE_END);
	}
	
	public List<Species> getSelectedSpecies() {
		List<Species> speciesList = new ArrayList<>();
		for (Species species : speciesCheckBoxMap.keySet()) {
			if (speciesCheckBoxMap.get(species).isSelected()) {
				speciesList.add(species);
			}
		}
		return speciesList;
	}
	
	public boolean getShouldCreateIndividualDatasets() {
		return individualButton.isSelected();
	}
}
