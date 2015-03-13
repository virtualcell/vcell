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
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import cbit.vcell.client.ClientRequestManager.BngUnitSystem;

public class BNGLUnitsPanel extends JPanel {
	
	private static final String concentrationsWarning = "Great choice!";
	private static final String moleculesWarning = "VCell will convert all rates to concentrations using the volume you provided.";
	private static final String concentrations = "Concentrations";
	private static final String molecules = "Molecules";
	
	private static final String[] volumeUnits = new String[] {"nm3 (nanometer)", "um3 (micrometer)", "mm3 (millimeter)"};
	private static final String[] concentrationUnits = new String[] {"M (molar)", "mM (millimolar)", "uM (micromolar)", "nM (nanomolar)"};
	private static final String[] timeUnits = new String[] {"sec (seconds)", "min (minutes)", "h (hours)"};

	private BngUnitSystem unitSystem;
	private JPanel lowerConPanel;
	private JPanel lowerMolPanel;

	ButtonGroup buttonGroup = new ButtonGroup();
	JTextField volumeSize = new JTextField("1");
	
	JComboBox<String> concentrationUnitsCombo = new JComboBox<>(concentrationUnits);
	JComboBox<String> mVolumeUnitsCombo = new JComboBox<>(volumeUnits);
	JComboBox<String> cTimeUnitsCombo = new JComboBox<>(timeUnits);
	JComboBox<String> mTimeUnitsCombo = new JComboBox<>(timeUnits);
	
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
				int x = Integer.parseInt(jTextField.getText());
				} catch (NumberFormatException nfe) {
				jTextField.setText("1");
				}
			}
		}
	}  
	
	
	public BNGLUnitsPanel(BngUnitSystem us) {
		super();
		this.unitSystem = new BngUnitSystem(us);
		this.unitSystem.setOrigin(BngUnitSystem.origin.USER);
		initialize();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {		
				doSomething();
			}
		});
	}
	
	private void initialize(){
		
		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		TitledBorder titleTop = BorderFactory.createTitledBorder(loweredEtchedBorder, " Choose category of units ");
		titleTop.setTitleJustification(TitledBorder.LEFT);
		titleTop.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleConcentrations = BorderFactory.createTitledBorder(loweredEtchedBorder, " Choose units for concentrations ");
		titleConcentrations.setTitleJustification(TitledBorder.LEFT);
		titleConcentrations.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleMolecules = BorderFactory.createTitledBorder(loweredEtchedBorder, " Choose units for molecules ");
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
		con.setSelected(true);
// --------------------------------------------------------------------- lower panels ------------------
		lowerConPanel.setLayout(new GridBagLayout());
		lowerConPanel.setPreferredSize(new Dimension(180,100));
		lowerConPanel.setVisible(true);
		
		gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 6, 0, 0);
		lowerConPanel.add(new JLabel("Scale:   "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 8, 0, 10);
		lowerConPanel.add(concentrationUnitsCombo, gbc);

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

		lowerMolPanel.setLayout(new GridBagLayout());
		lowerMolPanel.setPreferredSize(new Dimension(180,100));
		lowerMolPanel.setVisible(false);
		
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
		lowerMolPanel.add(volumeSize, gbc);
		
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
		
		Dimension size = new Dimension(190,150);
		setPreferredSize(size);
		setMaximumSize(size);
		
		con.addActionListener(eventHandler);
		mol.addActionListener(eventHandler);
		volumeSize.addKeyListener(eventHandler); 
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
			return new BngUnitSystem();	// better to return a default than null 
		}
		BngUnitSystem.origin o = BngUnitSystem.origin.USER;
		boolean isConcentration;
		int volume;
		String substanceUnit;
		String timeUnit;
		
		if(getSelectedButtonText(buttonGroup).equals(concentrations)) {
			isConcentration = true;
			volume = 1;
			substanceUnit = concentrationUnitsCombo.getSelectedItem().toString().substring(0,concentrationUnitsCombo.getSelectedItem().toString().indexOf(" "));
			timeUnit = cTimeUnitsCombo.getSelectedItem().toString().substring(0, cTimeUnitsCombo.getSelectedItem().toString().indexOf(" "));
		} else {		// molecules
			isConcentration = false;
			volume = Integer.parseInt(volumeSize.getText());	// we know for sure it's valid int
			substanceUnit = mVolumeUnitsCombo.getSelectedItem().toString().substring(0,mVolumeUnitsCombo.getSelectedItem().toString().indexOf(" "));
			timeUnit = mTimeUnitsCombo.getSelectedItem().toString().substring(0, mTimeUnitsCombo.getSelectedItem().toString().indexOf(" "));
		}
		return new BngUnitSystem(o, isConcentration, volume, substanceUnit, timeUnit);
	}
	
}
