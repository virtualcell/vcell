package org.vcell.util.gui;

import cbit.vcell.client.laf.VCellFonts;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

/**
 * Panel which displays list of suggestions
 * @author gweatherby
 *
 */
@SuppressWarnings("serial")
public class SuggestionPanel extends JPanel {
	private JTextPane solutionsText;

	public SuggestionPanel() {
		setLayout(new BorderLayout(0, 0));
		solutionsText = new JTextPane();
		VCellFonts.setCategory(solutionsText, VCellFonts.Category.BODY);
		solutionsText.setContentType("text/html");
		add(solutionsText);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Possible Solutions(s):");
		VCellFonts.setCategory(lblNewLabel, VCellFonts.Category.TITLE, Font.BOLD | Font.ITALIC);
		panel.add(lblNewLabel);
	}
	
	/**
	 * sets lists of suggestions. Replacing any prior setting
	 * @param solutions
	 * @throws IllegalArgumentException if null passed
	 */
	public void setSuggestedSolution(Collection<String> solutions) {
		if (solutions == null) {
			throw new IllegalArgumentException("null collection passed");
		}
		String buffer = "<html><ul>";
		for (String sol: solutions) {
			buffer = buffer + "<li>" + sol + "</li>";
		}
		buffer = buffer + "</ul></html>";
		solutionsText.setText(buffer);
	}
	

}
