package org.vcell.util.importer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class ResourceImportPanel<T> extends JPanel {
	
	protected final JPanel listPanel = new JPanel();
	protected final JScrollPane listScrollPanel = new JScrollPane(listPanel);
	protected final Map<ButtonModel, T> button2optionMap = new HashMap<ButtonModel, T>();
	protected final ButtonGroup buttonGroup = new ButtonGroup();
	
	public ResourceImportPanel(List<T> options) {
		setLayout(new BorderLayout());
		add(listScrollPanel);
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
		for(T option : options) {
			JPanel optionPanel = new JPanel();
			optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.LINE_AXIS));
			optionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			JRadioButton button = new JRadioButton();
			buttonGroup.add(button);
			if(buttonGroup.getSelection() == null) { button.setSelected(true); }
			button2optionMap.put(button.getModel(), option);
			optionPanel.add(button);
			optionPanel.add(new JLabel(option.toString()));
			listPanel.add(optionPanel);
		}
	}
	
	public T getSelectedOption() {
		T selectedOption = null;
		ButtonModel selectedButtonModel = buttonGroup.getSelection();
		if(selectedButtonModel != null) {
			selectedOption = button2optionMap.get(selectedButtonModel);
		}
		return selectedOption;
	}

}
