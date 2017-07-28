package org.vcell;

import java.awt.GridBagConstraints;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ModelParameterListPanel extends ModelParameterPanel {
	
	private static final long serialVersionUID = -2876934820098020420L;
	
	public ModelParameterListPanel() {
		super();
	}
	
	@Override
	public void setParameters(List<VCellModelParameter> parameters) {
		super.setParameters(parameters);
		removeAll();
		
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        
        addHeaderLabel("Parameters", c);
        
        for (String description : parameterDescriptionMap.keySet()) {
        	List<VCellModelParameter> filteredParameters = parameterDescriptionMap.get(description);
        	addHeaderLabel(description, c);
        	addParameterList(filteredParameters, c);
        }
        
        // Add empty panel to occupy any extra space at bottom (pushes components to the top of the panel)
        c.gridy++;
        c.weighty = 1.0;
        add(new JPanel(), c);
        
        revalidate();
	}
	
	private void addParameterList(List<VCellModelParameter> parameterList, GridBagConstraints c) {
		
		String formattedList = parameterList.stream()
				.map(p -> generateHtml(p.toString()))
				.collect(Collectors.joining(", "));
		JLabel label = new JLabel(formattedList);
		
        c.gridx = 0;
        c.gridy++;
        c.weightx = 1.0;
        add(label, c);
	}
}
