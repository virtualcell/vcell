package cbit.vcell.client.desktop.biomodel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;

public class SimulationConsolePanel extends JPanel {

	private EventHandler eventHandler = new EventHandler();
	private IssueManager fieldIssueManager;
	private SelectionManager fieldSelectionManager;

	private SimulationContext fieldSimulationContext;

	
	private JTextPane netGenConsoleText;
	
	
	private class EventHandler implements FocusListener, ActionListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {

		}

		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {

		}
		
		public void propertyChange(java.beans.PropertyChangeEvent event) {
			if(event.getSource() instanceof Model && event.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
				refreshInterface();
			}
		}
	}

	
	public SimulationConsolePanel() {
		super();
		initialize();
	}

	private void initialize() {
		
		netGenConsoleText = new JTextPane();
		netGenConsoleText.addFocusListener(eventHandler);

		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		JScrollPane netGenConsoleScrollPane = new JScrollPane(netGenConsoleText);
		netGenConsoleScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		TitledBorder titleConsole = BorderFactory.createTitledBorder(loweredBevelBorder, " BioNetGen Console ");
		titleConsole.setTitleJustification(TitledBorder.LEFT);
		titleConsole.setTitlePosition(TitledBorder.ABOVE_TOP);
		netGenConsoleScrollPane.setBorder(titleConsole);

		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());		// --- bottom
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = gbc.weighty = 1.0;			// get all the available space
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(20, 4, 4, 10);
		add(netGenConsoleScrollPane, gbc);
		
		netGenConsoleText.setFont(new Font("monospaced", Font.PLAIN, 11));
		netGenConsoleText.setEditable(false);

		
		
		
	}
	public SimulationContext getSimulationContext() {
		return fieldSimulationContext;
	}
	public void setSimulationContext(SimulationContext fieldSimulationContext) {
		if(this.fieldSimulationContext == fieldSimulationContext) {
			return;
		}
		
		this.fieldSimulationContext = fieldSimulationContext;
		refreshInterface();
	}

	
	private void refreshInterface() {
		String text1 = null;
		text1 = "Simulation Console for ";
		
		if(fieldSimulationContext == null) {
			netGenConsoleText.setText(text1 + "no simulation");
		} else {
			netGenConsoleText.setText(text1 + fieldSimulationContext.getName());
		}
	}


}
