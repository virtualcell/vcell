package org.vcell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SpeciesReference;


@SuppressWarnings("serial")
public class SBMLVisualizationPanel extends JPanel {
	
	private JPanel panel;
	private JScrollPane scrollPane;
	
	public SBMLVisualizationPanel() {
		setLayout(new BorderLayout());
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(panel);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public void visualize(SBMLDocument sbmlDocument) {
		for (Reaction reaction : sbmlDocument.getModel().getListOfReactions()) {
			addReaction(reaction);
		}
		revalidate();
	}
	
	private void addReaction(Reaction reaction) {
		JPanel reactionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		reactionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		reactionPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		addListOfSpecies(reaction.getListOfReactants(), reactionPanel);
		reactionPanel.add(new JLabel(" -> "));
		addListOfSpecies(reaction.getListOfProducts(), reactionPanel);
		panel.add(reactionPanel);
	}
	
	private void addListOfSpecies(ListOf<SpeciesReference> listOfSpecies, JPanel reactionPanel) {
		
		ArrayList<JPanel> compartmentPanels = new ArrayList<>();
		
		for (SpeciesReference species : listOfSpecies) {
			
			String compartment = species.getSpeciesInstance().getCompartment();
			
			JPanel compartmentPanel = compartmentPanels.stream()
					.filter(p -> p.getName().equals(compartment))
					.findFirst()
					.orElse(null);
			
			if (compartmentPanel == null) {
				compartmentPanel = new JPanel();
				compartmentPanel.setName(compartment);
				compartmentPanels.add(compartmentPanel);
			} else {
				compartmentPanel.add(new JLabel(" + "));
			}
			
			compartmentPanel.add(new JLabel(species.getSpecies()));
		}
		
		if (!compartmentPanels.isEmpty()) {
			addCompartmentPanelToReactionPanel(compartmentPanels.get(0), reactionPanel);
			for (int i = 1; i < compartmentPanels.size(); i++) {
				reactionPanel.add(new JLabel(" + "));
				addCompartmentPanelToReactionPanel(compartmentPanels.get(i), reactionPanel);
			}
		}
	}
	
	private void addCompartmentPanelToReactionPanel(JPanel compartmentPanel, JPanel reactionPanel) {
		compartmentPanel.setBorder(BorderFactory.createTitledBorder(compartmentPanel.getName()));
		compartmentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reactionPanel.add(compartmentPanel);
	}
}
