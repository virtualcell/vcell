package org.vcell;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class ModelParameterPanel extends JPanel {
	
	private static final long serialVersionUID = -33245111637587112L;
	
	public ModelParameterPanel() {
		setLayout(new GridBagLayout());
	}
	
    protected void addHeaderLabel(String text, GridBagConstraints c) {
    	
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
    
    protected String generateHtml(String text) {

        Pattern pattern = Pattern.compile("(\\D-\\d+)|(\\D\\d+)");
        Matcher matcher = pattern.matcher(text);
        StringBuilder stringBuilder = new StringBuilder("<html>");

        int startIndex = 0;
        while (matcher.find()) {
        	int index = matcher.start();
        	if (text.charAt(index) != '_') {
        		index++;
        	}
            stringBuilder.append(text.substring(startIndex, index));
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
