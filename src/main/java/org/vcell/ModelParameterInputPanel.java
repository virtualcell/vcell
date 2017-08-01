package org.vcell;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ExplicitRule;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;

/**
 * Created by kevingaffney on 7/10/17.
 */
public class ModelParameterInputPanel extends ModelParameterPanel {
	
	private static final long serialVersionUID = -8994187569679105048L;
	
	private VCellModel vCellModel;
	
	private HashMap<Parameter, JTextField> parameterTable;

    public ModelParameterInputPanel() {
    	parameterTable = new HashMap<>();
    }
    
    public HashMap<Parameter, ASTNode> getParameterMathMap() {
    	HashMap<Parameter, ASTNode> parameterMathMap = new HashMap<>();
    	
    	for (Parameter parameter : parameterTable.keySet()) {
    		String text = parameterTable.get(parameter).getText();
    		ASTNode math = null;
    		try {
    			double value = Double.parseDouble(text);
    			parameter.setValue(value);
    		} catch (NullPointerException | NumberFormatException e) {
    			math = vCellModel.parseValueStringForParameter(parameter, text);
    		}
    		parameterMathMap.put(parameter, math);
    	}
    	
    	return parameterMathMap;
    }
    
    public void setModel(VCellModel vCellModel) {
    	this.vCellModel = vCellModel;
    	parameterTable.clear();
    	removeAll();
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        
    	for (Parameter parameter : vCellModel.getParameters()) {
    		addParameterInput(parameter, vCellModel, c);
    	}
        
        // Add empty panel to occupy any extra space at bottom (pushes components to the top of the panel)
        c.gridy++;
        c.weighty = 1.0;
        add(new JPanel(), c);
        
        revalidate();
    }
    
    private void addParameterInput(Parameter parameter, VCellModel model, GridBagConstraints c) {
    	
        JLabel idLabel = new JLabel(parameter.getId());
        
        JTextField textField = new JTextField();
        
        textField.setText(model.getValueStringForParameter(parameter));
        parameterTable.put(parameter, textField);
        
        JLabel unitLabel = new JLabel(generateHtml(parameter.getUnits()));
        
//        JCheckBox checkBox = new JCheckBox("Scan?");
        
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
        
        // TODO: After parameter scanning is implemented, we can start using this check box
//        add(checkBox, c);
    }

}
