package org.vcell.model.bngl;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import cbit.vcell.client.ClientRequestManager.BngUnitSystem;
import cbit.vcell.client.ClientRequestManager.BngUnitSystem.BngUnitOrigin;
import cbit.vcell.client.ClientRequestManager.BngUnitSystem.ConcUnitSystem;
import cbit.vcell.client.ClientRequestManager.BngUnitSystem.TimeUnitSystem;
import cbit.vcell.client.ClientRequestManager.BngUnitSystem.VolumeUnitSystem;

public class BNGLUnitsPanel extends JPanel {
	
	private static final String concentrationsWarning = "Great choice!";
	private static final String moleculesWarning = "VCell will convert all rates to concentrations using the volume you provided.";
	private static final String concentrations = "Concentrations";
	private static final String molecules = "Molecules";
	
	private BngUnitSystem unitSystem;
	private JPanel lowerConPanel;
	private JPanel lowerMolPanel;

	ButtonGroup buttonGroup = new ButtonGroup();
	JTextField cVolumeSize = new JTextField();
	JTextField mVolumeSize = new JTextField();
	JComboBox<String> concentrationUnitsCombo = new JComboBox<String>();
	JComboBox<String> cVolumeUnitsCombo = new JComboBox<String>();		// most of the code is present but we don't use it
	JComboBox<String> mVolumeUnitsCombo = new JComboBox<String>();
	JComboBox<String> cTimeUnitsCombo = new JComboBox<String>();
	JComboBox<String> mTimeUnitsCombo = new JComboBox<String>();
	
	private EventHandler eventHandler = new EventHandler();
	private class EventHandler implements ActionListener, FocusListener, PropertyChangeListener, KeyListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source instanceof JRadioButton) {
				JRadioButton button = (JRadioButton) e.getSource();
				if (button.getText().equals(concentrations)){
					System.out.println(concentrations);
					lowerConPanel.setVisible(true);
					lowerMolPanel.setVisible(false);
				}else{
					System.out.println(molecules);
					lowerConPanel.setVisible(false);
					lowerMolPanel.setVisible(true);
				}
			} else {
				
			}
		}
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
		}
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
		}
		@Override
		public void keyTyped(KeyEvent e) {
		}
		@Override
		public void keyPressed(KeyEvent e) {
		}
		@Override
		public void keyReleased(KeyEvent e) {
			Object source = e.getSource();
			if (source instanceof JTextField) {
				JTextField jTextField = (JTextField)e.getSource();
				try {
				double x = Double.parseDouble(jTextField.getText());
				} catch (NumberFormatException nfe) {
				jTextField.setText("1");
				}
			}
		}
	}  
	
	public BNGLUnitsPanel(BngUnitSystem us) {
		super();
		this.unitSystem = new BngUnitSystem(us);
		cVolumeSize.setText(unitSystem.getVolume()+"");
		mVolumeSize.setText(unitSystem.getVolume()+"");

		for(ConcUnitSystem s : ConcUnitSystem.values()) {
			concentrationUnitsCombo.addItem(s.description);
		}
		concentrationUnitsCombo.setSelectedIndex(1);	// we select by default element 1 (micromolar) which is the most sensible choice
		
		cVolumeUnitsCombo.addItem(VolumeUnitSystem.VolumeUnit_um3.description);		// TODO: hardcoded for now
		for(VolumeUnitSystem s : VolumeUnitSystem.values()) {
			mVolumeUnitsCombo.addItem(s.description);
		}
		for(TimeUnitSystem s : TimeUnitSystem.values()) {
			cTimeUnitsCombo.addItem(s.description);
			mTimeUnitsCombo.addItem(s.description);
		}
		initialize();

		SwingUtilities.invokeLater(new Runnable() {		// unused for now
			@Override
			public void run() {		
				doSomething();
			}
		});
	}
	
	private void initialize(){
		
		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleTop = BorderFactory.createTitledBorder(loweredEtchedBorder, " Choose substance units ");
		titleTop.setTitleJustification(TitledBorder.LEFT);
		titleTop.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleConcentrations = BorderFactory.createTitledBorder(loweredEtchedBorder, " Specify concentration and time units ");
		titleConcentrations.setTitleJustification(TitledBorder.LEFT);
		titleConcentrations.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleMolecules = BorderFactory.createTitledBorder(loweredEtchedBorder, " Specify volume and time units ");
		titleMolecules.setTitleJustification(TitledBorder.LEFT);
		titleMolecules.setTitlePosition(TitledBorder.TOP);

		JPanel top = new JPanel();
		lowerConPanel = new JPanel();
		lowerMolPanel = new JPanel();

		top.setBorder(titleTop);
		lowerConPanel.setBorder(titleConcentrations);
		lowerMolPanel.setBorder(titleMolecules);
// ----------------------------------------------------------------------- upper panel -------------------
		JRadioButton con = new JRadioButton(concentrations);
		JRadioButton mol = new JRadioButton(molecules);
		buttonGroup.add(con);
		buttonGroup.add(mol);
		
		top.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		int gridy = 0;
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 10, 0, 0);
		top.add(con, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 0, 0, 10);
		top.add(mol, gbc);
		
		top.setSize(200,150);
		con.setSelected(true);		// select the "Concentration" button
// --------------------------------------------------------------------- lower panels ------------------
		lowerConPanel.setLayout(new GridBagLayout());

		gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4, 6, 0, 0);
		lowerConPanel.add(new JLabel("Volume:"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 8, 0, 0);
		lowerConPanel.add(cVolumeSize, gbc);
		
		JLabel unitLabel = new JLabel(VolumeUnitSystem.VolumeUnit_um3.description);
		unitLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4, 8, 0, 10);
		lowerConPanel.add(unitLabel, gbc);
//		lowerConPanel.add(cVolumeUnitsCombo, gbc);		// TODO: get rid of hardcoding

		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 6, 4, 0);
		lowerConPanel.add(new JLabel("Time:"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 8, 4, 10);
		lowerConPanel.add(cTimeUnitsCombo, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 6, 0, 0);
		lowerConPanel.add(new JLabel("Scale: "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4, 8, 0, 10);
		lowerConPanel.add(concentrationUnitsCombo, gbc);

		// --------------------------------------------------------------------------------
		lowerMolPanel.setLayout(new GridBagLayout());
		
		gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4, 6, 0, 0);
		lowerMolPanel.add(new JLabel("Volume:"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 8, 0, 0);
		lowerMolPanel.add(mVolumeSize, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4, 8, 0, 10);
		lowerMolPanel.add(mVolumeUnitsCombo, gbc);
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 6, 4, 0);
		lowerMolPanel.add(new JLabel("Time:"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER; // this and next extends this cell all the way to the right!
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 8, 4, 10);
		lowerMolPanel.add(mTimeUnitsCombo, gbc);
// --------------------------------------------------------------------	all together ------------------------	
		setLayout(new GridBagLayout());		
		gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		gbc.fill = GridBagConstraints.BOTH;
		add(top, gbc);							// top panel

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(10, 0, 0, 4);
		gbc.fill = GridBagConstraints.BOTH;
		add(lowerConPanel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(10, 0, 0, 4);
		gbc.fill = GridBagConstraints.BOTH;
		add(lowerMolPanel, gbc);
		
		Dimension size = new Dimension(350,150);
		setPreferredSize(size);
		setMaximumSize(size);
		
		lowerConPanel.setVisible(false);	// hide the "Concentration" panel
		lowerMolPanel.setVisible(true);		// show the "Molecules" panel
		mol.setSelected(true);				// select the "Molecules" button
		
		con.addActionListener(eventHandler);
		mol.addActionListener(eventHandler);
		cVolumeSize.addKeyListener(eventHandler); 
		mVolumeSize.addKeyListener(eventHandler); 
	}


	public void doSomething(){
		updateSomething();
	}
	private void updateSomething(){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

			}
		});
	}

public String getSelectedButtonText(ButtonGroup bg) {
	for (Enumeration<AbstractButton> buttons = bg.getElements(); buttons.hasMoreElements(); ) {
		AbstractButton button = buttons.nextElement();
		if (button.isSelected()) {
			return button.getText();
		}
	}
	return null;
}

	public BngUnitSystem getUnits() {
		if(getSelectedButtonText(buttonGroup) == null) {
			return new BngUnitSystem(BngUnitOrigin.DEFAULT);	// better to return a default than null 
		}
		
		if(getSelectedButtonText(buttonGroup).equals(concentrations)) {
			double volume = Double.parseDouble(cVolumeSize.getText());
			ConcUnitSystem cus = ConcUnitSystem.values()[0];
			for(ConcUnitSystem s : ConcUnitSystem.values()) {
				if(s.description.equals(concentrationUnitsCombo.getSelectedItem().toString())) {
					cus = s;
					break;
				}
			}
			TimeUnitSystem tus = TimeUnitSystem.values()[0];
			for(TimeUnitSystem s : TimeUnitSystem.values()) {
				if(s.description.equals(cTimeUnitsCombo.getSelectedItem().toString())) {
					tus = s;
					break;
				}
			}
			return BngUnitSystem.createAsConcentration(BngUnitOrigin.USER, volume, cus, tus);
			
		} else {		// molecules
			double volume = Double.parseDouble(mVolumeSize.getText());	// we know for sure it's valid double
			VolumeUnitSystem vus = VolumeUnitSystem.values()[0];
			for(VolumeUnitSystem s : VolumeUnitSystem.values()) {
				if(s.description.equals(mVolumeUnitsCombo.getSelectedItem().toString())) {
					vus = s;
					break;
				}
			}
			TimeUnitSystem tus = TimeUnitSystem.values()[0];
			for(TimeUnitSystem s : TimeUnitSystem.values()) {
				if(s.description.equals(mTimeUnitsCombo.getSelectedItem().toString())) {
					tus = s;
					break;
				}
			}
			return BngUnitSystem.createAsMolecules(BngUnitOrigin.USER, volume, vus, tus);
		}
	}
	
}
