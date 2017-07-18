package org.vcell;

import javax.swing.*;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kevingaffney on 7/10/17.
 */
public class ModelParameterInputPanel extends JPanel {

	private HashMap<VCellModelParameter, JTextField> parameterTable;

    public ModelParameterInputPanel(ArrayList<VCellModelParameter> parameters) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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

        if (!concentrationParameters.isEmpty()) {

            add(newHeaderLabel("Initial concentrations"));

            for (VCellModelParameter parameter : concentrationParameters) {
                addParameterInput(parameter);
            }
        }

        if (!diffusionParameters.isEmpty()) {

            add(newHeaderLabel("Diffusion constants"));

            for (VCellModelParameter parameter : diffusionParameters) {
                addParameterInput(parameter);
            }
        }
        revalidate();
    }

    private JLabel newHeaderLabel(String text) {
        JLabel headerLabel = new JLabel(text);
        headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.BOLD, 20));
        return headerLabel;
    }

    private void addParameterInput(VCellModelParameter parameter) {
    	
        JLabel idLabel = new JLabel(parameter.getId());
        idLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        JLabel unitLabel = new JLabel(generateHtml(parameter.getUnit()));
        
        JTextField textField = new JTextField(10);
        parameterTable.put(parameter, textField);
        
        JPanel parameterPanel = new JPanel(new BorderLayout());
        parameterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        parameterPanel.add(idLabel, BorderLayout.LINE_START);
        parameterPanel.add(textField, BorderLayout.CENTER);
        parameterPanel.add(unitLabel, BorderLayout.LINE_END);
        add(parameterPanel);
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
