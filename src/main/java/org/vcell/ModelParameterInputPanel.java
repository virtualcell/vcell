package org.vcell;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Created by kevingaffney on 7/10/17.
 */
public class ModelParameterInputPanel extends ModelParameterPanel {
	
	private static final long serialVersionUID = -8994187569679105048L;
	
	private HashMap<VCellModelParameter, JTextField> parameterTable;

    public ModelParameterInputPanel() {
    	super();
    	parameterTable = new HashMap<>();
    }
    
    @Override
    public void setParameters(List<VCellModelParameter> parameters) {
    	super.setParameters(parameters);
    	parameterTable.clear();
    	removeAll();
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        
        for (String description : parameterDescriptionMap.keySet()) {
        	List<VCellModelParameter> filteredParameters = parameterDescriptionMap.get(description);
        	addHeaderLabel(description, c);
        	for (VCellModelParameter filteredParameter : filteredParameters) {
        		addParameterInput(filteredParameter, c);
        	}
        }
        
        // Add empty panel to occupy any extra space at bottom (pushes components to the top of the panel)
        c.gridy++;
        c.weighty = 1.0;
        add(new JPanel(), c);
        
        revalidate();
    }
    
    private void addParameterInput(VCellModelParameter parameter, GridBagConstraints c) {
    	
        JLabel idLabel = new JLabel(parameter.getId());
        
        JTextField textField = new JTextField();
        parameterTable.put(parameter, textField);
        
        JLabel unitLabel = new JLabel(generateHtml(parameter.getUnit()));
        
        JCheckBox checkBox = new JCheckBox("Scan?");
        
        c.gridx = 0;
        c.gridy++;
        add(idLabel, c);
        c.gridx = GridBagConstraints.RELATIVE;
        
        c.weightx = 1.0;
        add(textField, c);
        c.weightx = 0.0;
        
        c.ipadx = 20;
        add(unitLabel, c);
        c.ipadx = 0;
        
        add(checkBox, c);
    }

}
