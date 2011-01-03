package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ApplicationTasksPanel extends DocumentEditorSubPanel {
	public ApplicationTasksPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
		textArea.setText("Tasks is where simulations are run, output functions are defined and parameter estimation are run.");
		
		setBackground(Color.white);
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(20, 20, 20, 20);
		add(textArea, gbc);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		
	}

}
