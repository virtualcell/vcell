package org.vcell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Species;

public class SimulateModelPanel extends JPanel {
	
	private static final long serialVersionUID = -1587450143007943559L;
	
	private ModelParameterInputPanel inputPanel;
	private SimulationSpecificationPanel specificationPanel;
	private JTextField timeStepTextField;
	private JTextField totalTimeTextField;
	
	public SimulateModelPanel(VCellModel model) {
		
		setLayout(new GridLayout(1, 2, 50, 0));
		
		inputPanel = new ModelParameterInputPanel();
		inputPanel.setModel(model);
		
		timeStepTextField = new JTextField();
		totalTimeTextField = new JTextField();
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(headerLabel("Parameters"), BorderLayout.PAGE_START);
		leftPanel.add(new JScrollPane(inputPanel), BorderLayout.CENTER);
		leftPanel.add(timePanel(timeStepTextField, totalTimeTextField), BorderLayout.PAGE_END);
		
		specificationPanel = new SimulationSpecificationPanel(model);
		
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(specificationPanel, BorderLayout.CENTER);
		rightPanel.add(headerLabel("Outputs"), BorderLayout.PAGE_START);
		
		add(leftPanel);
		add(rightPanel);
		
		Dimension d = getPreferredSize();
		d.height = 300;
		setPreferredSize(d);
	}
	
	public HashMap<Parameter, ASTNode> getParameterMathMap() {
		return inputPanel.getParameterMathMap();
	}
	
	public double getTimeStep() {
		return Double.parseDouble(timeStepTextField.getText());
	}
	
	public double getTotalTime() {
		return Double.parseDouble(totalTimeTextField.getText());
	}
	
	public List<Species> getSelectedSpecies() {
		return specificationPanel.getSelectedSpecies();
	}
	
	public boolean getShouldCreateIndividualDatasets() {
		return specificationPanel.getShouldCreateIndividualDatasets();
	}
	
	private JLabel headerLabel(String text) {
		JLabel headerLabel = new JLabel(text);
		Font font = headerLabel.getFont();
		headerLabel.setFont(new Font(font.getFontName(), Font.BOLD, 14));
		headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		return headerLabel;
	}
	
	private JPanel timePanel(JTextField timeStepTextField, JTextField totalTimeTextField) {
		JPanel timePanel = new JPanel(new GridBagLayout());
		timePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 0;
        
        timePanel.add(new JLabel("Time step"), c);
        
        c.weightx = 1.0;
        timePanel.add(timeStepTextField, c);
        c.weightx = 0.0;
        
        timePanel.add(new JLabel("s"), c);
        
        c.gridy++;
        
        timePanel.add(new JLabel("Total time"), c);
        
        c.weightx = 1.0;
        timePanel.add(totalTimeTextField, c);
        c.weightx = 0.0;
        
        timePanel.add(new JLabel("s"), c);
        
        return timePanel;
	}
}
