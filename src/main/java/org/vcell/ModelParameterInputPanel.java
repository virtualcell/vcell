package org.vcell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Created by kevingaffney on 7/10/17.
 */
public class ModelParameterInputPanel extends JPanel {

	private HashMap<VCellModelParameter, JTextField> parameterTable;

    public ModelParameterInputPanel(ArrayList<VCellModelParameter> parameters) {
        setLayout(new GridBagLayout());
        setParameters(parameters);
    }
    
    public void setParameters(ArrayList<VCellModelParameter> parameters) {
    	
    	parameterTable = new HashMap<>();
    	removeAll();
    	
        List<VCellModelParameter> concentrationParameters = parameters.stream()
                .filter(p -> p.getParameterType() == VCellModelParameter.CONCENTRATION)
                .collect(Collectors.toList());

        List<VCellModelParameter> diffusionParameters = parameters.stream()
                .filter(p -> p.getParameterType() == VCellModelParameter.DIFFUSION_CONSTANT)
                .collect(Collectors.toList());
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        if (!concentrationParameters.isEmpty()) {
            addHeaderLabel("Initial concentrations", c);
            for (VCellModelParameter parameter : concentrationParameters) {
                addParameterInput(parameter, c);
            }
        }

        if (!diffusionParameters.isEmpty()) {
            addHeaderLabel("Diffusion constants", c);
            for (VCellModelParameter parameter : diffusionParameters) {
                addParameterInput(parameter, c);
            }
        }
        
        // Add empty panel to occupy any extra space at bottom (pushes components to the top of the panel)
        c.gridy++;
        c.weighty = 1.0;
        add(new JPanel(), c);
        
        revalidate();
    }
    
    private void addHeaderLabel(String text, GridBagConstraints c) {
    	
    	JLabel headerLabel = new JLabel(text);
        headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.BOLD, 14));
        
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.ipady = 20;
        add(headerLabel, c);
        c.gridwidth = 1;
        c.ipady = 0;
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

    private String generateHtml(String text) {

        Pattern pattern = Pattern.compile("(\\D-\\d+)|(\\D\\d+)");
        Matcher matcher = pattern.matcher(text);
        StringBuilder stringBuilder = new StringBuilder("<html>");

        int startIndex = 0;
        while (matcher.find()) {
            stringBuilder.append(text.substring(startIndex, matcher.start() + 1));
            stringBuilder.append("<sup>");
            stringBuilder.append(text.substring(matcher.start() + 1, matcher.end()));
            stringBuilder.append("</sup>");
            startIndex = matcher.end();
        }

        stringBuilder.append(text.substring(startIndex));
        stringBuilder.append("</html>");
        return stringBuilder.toString();
    }
}
