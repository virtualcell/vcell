package org.vcell.imagej.common.vcell;

import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.sbml.jsbml.Parameter;

public class ModelParameterListPanel extends ModelParameterPanel {
	
	private static final long serialVersionUID = -2876934820098020420L;
	
	public ModelParameterListPanel() {
		super();
	}
	
	public void setModel(VCellModel model) {
		removeAll();
		
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        
        addHeaderLabel("Parameters", c);
        
        addParameterList(model.getParameters(), c);
        
        // Add empty panel to occupy any extra space at bottom (pushes components to the top of the panel)
        c.gridy++;
        c.weighty = 1.0;
        add(new JPanel(), c);
        
        revalidate();
	}
	
	private void addParameterList(List<Parameter> parameterList, GridBagConstraints c) {
		
		if (parameterList.isEmpty()) return;
		
		StringBuilder builder = new StringBuilder();
		builder.append(generateParameterString(parameterList.get(0)));
		
		for (Parameter parameter : parameterList) {
			builder.append(", ");
			builder.append(generateParameterString(parameter));
		}
		
		JLabel label = new JLabel(generateHtml(builder.toString()));
		
        c.gridx = 0;
        c.gridy++;
        c.weightx = 1.0;
        add(label, c);
	}
	
	private String generateParameterString(Parameter parameter) {
		return parameter.getId() + " (" + parameter.getUnits() + ")";
	}
}
