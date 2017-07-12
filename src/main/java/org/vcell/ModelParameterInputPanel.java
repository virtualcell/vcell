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

    private ArrayList<JTextField> textFields;

//    private JTextField txtCytConc;
//    private JTextField txtCytDiff;
//    private JTextField txtPMConc;
//    private JTextField txtPMDiff;
//    private JTextField txtActivationRate;
//    private JTextField txtBindingRate;

    public ModelParameterInputPanel(ArrayList<VCellModelParameter> parameters) {

        super(new GridLayout(0, 3, 10, 10));
        Hashtable<VCellModelParameter, JTextField> textField = new Hashtable<>();
        textFields = new ArrayList<>(parameters.size());

        List<VCellModelParameter> concentrationParameters = parameters.stream()
                .filter(p -> p.getParameterType() == VCellModelParameter.CONCENTRATION)
                .collect(Collectors.toList());

        List<VCellModelParameter> diffusionParameters = parameters.stream()
                .filter(p -> p.getParameterType() == VCellModelParameter.DIFFUSION_CONSTANT)
                .collect(Collectors.toList());

        if (!concentrationParameters.isEmpty()) {

            add(newHeaderLabel("Concentration"));
            add(new JPanel());
            add(new JPanel());

            for (VCellModelParameter parameter : concentrationParameters) {
                addParameterInput(parameter);
            }
        }

        if (!diffusionParameters.isEmpty()) {

            add(newHeaderLabel("Diffusion"));
            add(new JPanel());
            add(new JPanel());

            for (VCellModelParameter parameter : diffusionParameters) {
                addParameterInput(parameter);
            }
        }
    }

    private JLabel newHeaderLabel(String text) {
        JLabel headerLabel = new JLabel(text);
        headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.BOLD, 20));
        return headerLabel;
    }

    private void addParameterInput(VCellModelParameter parameter) {
        JLabel idLabel = new JLabel(parameter.getId());
        idLabel.setHorizontalAlignment(JLabel.RIGHT);
        add(idLabel);
        JTextField textField = new JTextField();
        textFields.add(textField);
        add(textField);
        add(new JLabel(generateHtml(parameter.getUnit())));
    }

    public String generateHtml(String text) {

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
