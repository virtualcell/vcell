package org.vcell.model.springsalad.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.model.Model;

// we should use WindowBuilder Plugin (add it to Eclipse IDE) to speed up panel design
// can choose absolute layout and place everything exactly as we see fit
@SuppressWarnings("serial")
public class MolecularStructuresPanel extends DocumentEditorSubPanel implements ApplicationSpecificationsPanel.Specifier {

	private EventHandler eventHandler = new EventHandler();
	private SimulationContext fieldSimulationContext;
	private IssueManager fieldIssueManager;
	private SelectionManager fieldSelectionManager;
	
	private class EventHandler implements FocusListener, ActionListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {

		}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
		}
		public void propertyChange(java.beans.PropertyChangeEvent event) {

		}
	}

	public MolecularStructuresPanel() {
		super();
		initialize();
	}
	
	@Override
	public ActiveViewID getActiveView() {
		return ActiveViewID.network_setting;
	}
	/**
	 * no-op 
	 */
	@Override
	public void setSearchText(String s) {
		
	}
	

	private void initialize() {

		JPanel thePanel = new JPanel();

		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		TitledBorder titleLeft = BorderFactory.createTitledBorder(loweredEtchedBorder, " Molecule Types ");
		titleLeft.setTitleJustification(TitledBorder.LEFT);
		titleLeft.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleCenter = BorderFactory.createTitledBorder(loweredEtchedBorder, " Sites ");
		titleCenter.setTitleJustification(TitledBorder.LEFT);
		titleCenter.setTitlePosition(TitledBorder.TOP);
		
		TitledBorder titleRight = BorderFactory.createTitledBorder(loweredEtchedBorder, " Links ");
		titleRight.setTitleJustification(TitledBorder.LEFT);
		titleRight.setTitlePosition(TitledBorder.TOP);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 1, 1, 1);
		add(thePanel, gbc);
		
		// ------------------------------------------- the 3 main panels ---------------
		JPanel left = new JPanel();
		JPanel center = new JPanel();
		JPanel right = new JPanel();
		
		left.setBorder(titleLeft);
		center.setBorder(titleCenter);
		right.setBorder(titleRight);
		
		thePanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
		thePanel.add(left, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		thePanel.add(center, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		thePanel.add(right, gbc);
		
		// --- left -------------------------------------------------
		JPanel leftSubpanel1 = new JPanel();
		JList<String> molecules = new JList<String> ();
		molecules.setBorder(loweredEtchedBorder);
		JPanel leftSubpanel2 = new JPanel();
		JTextField radius = new JTextField();
		JTextField diameter = new JTextField();
		JTextField color = new JTextField();

		leftSubpanel1.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		leftSubpanel1.add(molecules, gbc);

		leftSubpanel2.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(5, 2, 2, 3);
		leftSubpanel2.add(new JLabel("Radius (nm): "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
		leftSubpanel2.add(radius, gbc);
		
		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(5, 2, 2, 3);
		leftSubpanel2.add(new JLabel("D (um^2/s): "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		leftSubpanel2.add(diameter, gbc);

		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(5, 2, 2, 3);
		leftSubpanel2.add(new JLabel("Color: "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		leftSubpanel2.add(color, gbc);
		
		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		leftSubpanel2.add(new JLabel("States"), gbc);

		JList<String> states = new JList<String> ();
		states.setBorder(loweredEtchedBorder);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		leftSubpanel2.add(states, gbc);

		left.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		left.add(leftSubpanel1, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		left.add(leftSubpanel2, gbc);

		// --- center --------------------------------------------------
		JPanel centerSubpanel1 = new JPanel();
		JList<String> sites = new JList<String> ();
		sites.setBorder(loweredEtchedBorder);
		JPanel centerSubpanel2 = new JPanel();
		JTextField radiusCenter = new JTextField();
		JTextField diameterCenter = new JTextField();
		JTextField locationCenter = new JTextField();

		centerSubpanel1.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		centerSubpanel1.add(sites, gbc);

		centerSubpanel2.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(5, 2, 2, 3);
		centerSubpanel2.add(new JLabel("Radius (nm): "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
		centerSubpanel2.add(radiusCenter, gbc);
		
		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(5, 2, 2, 3);
		centerSubpanel2.add(new JLabel("D (um^2/s): "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		centerSubpanel2.add(diameterCenter, gbc);

		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(5, 2, 2, 3);
		centerSubpanel2.add(new JLabel("Location: "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		centerSubpanel2.add(locationCenter, gbc);
		
		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		centerSubpanel2.add(new JLabel("Position (nm) "), gbc);
		
		JPanel centerSubpanel3 = new JPanel();	// ===================
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 3);
		centerSubpanel2.add(centerSubpanel3, gbc);
		
		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		centerSubpanel2.add(new JButton("Set Position"), gbc);

		gbc = new GridBagConstraints();		// ghost
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		centerSubpanel2.add(new JLabel(" "), gbc);


		centerSubpanel3.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 2, 2, 0);		//  top, left, bottom, right 
		centerSubpanel3.add(new JLabel("X: "), gbc);

		JTextField x = new JTextField();
		x.setEditable(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 1, 2, 3);
		centerSubpanel3.add(x, gbc);

		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 7, 2, 0);
		centerSubpanel3.add(new JLabel("Y: "), gbc);

		JTextField y = new JTextField();
		y.setEditable(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 1, 2, 3);
		centerSubpanel3.add(y, gbc);

		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 7, 2, 0);
		centerSubpanel3.add(new JLabel("Z: "), gbc);

		JTextField z = new JTextField();
		z.setEditable(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 1, 2, 0);
		centerSubpanel3.add(z, gbc);

		center.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		center.add(centerSubpanel1, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		center.add(centerSubpanel2, gbc);
		
		// --- right -----------------------------------------------
		JList<String> links = new JList<String> ();
		links.setBorder(loweredEtchedBorder);
		JTextField lengthRight = new JTextField();
		lengthRight.setEditable(false);
		JButton addLink = new JButton("Add Link");
		JButton removeLink = new JButton("Remove Link");
		JButton setLinkLength = new JButton("Set Link Length");
		
		right.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		right.add(links, gbc);

		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(5, 2, 2, 3);
		right.add(new JLabel("Length (nm): "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
		right.add(lengthRight, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		right.add(addLink, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		right.add(removeLink, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		right.add(setLinkLength, gbc);

	}
	
	public void setSimulationContext(SimulationContext simulationContext) {
		if(simulationContext == null) {
			return;
		}
		if(fieldSimulationContext != null) {
			fieldSimulationContext.removePropertyChangeListener(eventHandler);
		}
		fieldSimulationContext = simulationContext;
		fieldSimulationContext.addPropertyChangeListener(eventHandler);
		
		Model m = fieldSimulationContext.getModel();
		if(m != null) {
			m.removePropertyChangeListener(eventHandler);
			m.addPropertyChangeListener(eventHandler);
		}

		refreshInterface();
	}
	public SimulationContext getSimulationContext() {
		return fieldSimulationContext;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		fieldSelectionManager = selectionManager;
	}
	public IssueManager getIssueManager() {
		return fieldIssueManager;
	}
	public void setIssueManager(IssueManager issueManager) {
		fieldIssueManager = issueManager;
	}
	
	
	
	private void appendToConsole(String string) {
		TaskCallbackMessage newCallbackMessage = new TaskCallbackMessage(TaskCallbackStatus.Error, string);
		fieldSimulationContext.appendToConsole(newCallbackMessage);
	}
	private void appendToConsole(TaskCallbackMessage newCallbackMessage) {
		fieldSimulationContext.appendToConsole(newCallbackMessage);
	}
	
	public void refreshInterface() {


	}
	

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {

	}

}

